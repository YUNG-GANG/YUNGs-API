package com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw;

import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.StructureTemplatePoolAccessor;
import com.yungnickyoung.minecraft.yungsapi.util.BoxOctree;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.assembler.JigsawStructureAssembler;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element.YungJigsawPoolElement;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

public class JigsawManager {
    public static Optional<Structure.GenerationStub> assembleJigsawStructure(
            Structure.GenerationContext generationContext,
            Holder<StructureTemplatePool> startPool,
            Optional<ResourceLocation> startJigsawNameOptional,
            int maxDepth,
            BlockPos locatePos, // The original starting position of the structure, also where /locate points to
            boolean useExpansionHack, // Used to be doBoundaryAdjustments
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxDistanceFromCenter, // Used to be structureBoundingBoxRadius
            Optional<Integer> maxY,
            Optional<Integer> minY,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings

    ) {
        // Extract data from context
        RegistryAccess registryAccess = generationContext.registryAccess();
        ChunkGenerator chunkGenerator = generationContext.chunkGenerator();
        StructureTemplateManager structureManager = generationContext.structureTemplateManager();
        LevelHeightAccessor levelHeightAccessor = generationContext.heightAccessor();
        WorldgenRandom worldgenRandom = generationContext.random();
        Registry<StructureTemplatePool> registry = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL);

        // Grab a random starting piece from the start pool
        Optional<PoolElementStructurePiece> startPieceOptional = getStartPiece(startPool, startJigsawNameOptional, locatePos, structureManager, worldgenRandom, liquidSettings);
        if (startPieceOptional.isEmpty()) {
            return Optional.empty();
        }
        PoolElementStructurePiece startPiece = startPieceOptional.get();

        // Offset vector from the /locate position to the piece's starting position.
        // This will be a zero vector if no start jigsaw name was specified.
        Vec3i startingPosOffset = locatePos.subtract(startPiece.getPosition());

        // Grab some data regarding starting piece's bounding box & position
        BoundingBox pieceBoundingBox = startPiece.getBoundingBox();
        int bbCenterX = (pieceBoundingBox.maxX() + pieceBoundingBox.minX()) / 2;
        int bbCenterZ = (pieceBoundingBox.maxZ() + pieceBoundingBox.minZ()) / 2;
        // Note that the bbCenterY does not actually refer to the center of the piece, unlike the bbCenterX/Z variables.
        // If a heightmap is used, the bbCenterY will be the y-value of the /locate position (anchor pos) after adjusting for the heightmap.
        // Otherwise, the bbCenterY is simply the starting position's y-value. I'm not sure why it uses that position and not the /locate position,
        // but that's vanilla behavior. It almost certainly won't make a difference anyway, as structures are basically never that tall.
        int bbCenterY = projectStartToHeightmap
                .map(types -> locatePos.getY() + chunkGenerator.getFirstFreeHeight(bbCenterX, bbCenterZ, types, levelHeightAccessor, generationContext.randomState()))
                .orElseGet(() -> startPiece.getPosition().getY());
        int adjustedPieceCenterY = bbCenterY + startingPosOffset.getY();

        // Move the starting piece to account for any y-level change due to heightmap and/or groundLevelDelta
        int yAdjustment = pieceBoundingBox.minY() + startPiece.getGroundLevelDelta();
        startPiece.move(0, bbCenterY - yAdjustment, 0);

        // Establish max bounds of entire structure.
        // Make sure the supplied distance is large enough to cover the size of your entire structure.
        AABB aABB = new AABB(
                bbCenterX - maxDistanceFromCenter, adjustedPieceCenterY - maxDistanceFromCenter, bbCenterZ - maxDistanceFromCenter,
                bbCenterX + maxDistanceFromCenter + 1, adjustedPieceCenterY + maxDistanceFromCenter + 1, bbCenterZ + maxDistanceFromCenter + 1);
        BoxOctree maxStructureBounds = new BoxOctree(aABB); // The maximum boundary of the entire structure
        maxStructureBounds.addBox(AABB.of(pieceBoundingBox)); // Add start piece to our structure's bounds

        return Optional.of(new Structure.GenerationStub(
                new BlockPos(bbCenterX, adjustedPieceCenterY, bbCenterZ),
                (structurePiecesBuilder) -> {
                    if (maxDepth <= 0) { // Realistically this should never be true. Why make a jigsaw config with a non-positive size?
                        return;
                    }

                    // Create assembler + initial entry
                    JigsawStructureAssembler assembler = new JigsawStructureAssembler(new JigsawStructureAssembler.Settings()
                            .poolRegistry(registry)
                            .maxDepth(maxDepth)
                            .chunkGenerator(chunkGenerator)
                            .structureTemplateManager(structureManager)
                            .randomState(generationContext.randomState())
                            .rand(worldgenRandom)
                            .maxY(maxY)
                            .minY(minY)
                            .useExpansionHack(useExpansionHack)
                            .levelHeightAccessor(levelHeightAccessor)
                            .dimensionPadding(dimensionPadding)
                            .liquidSettings(liquidSettings));

                    // Add the start piece to the assembler & assemble the structure
                    assembler.assembleStructure(startPiece, maxStructureBounds);
                    assembler.addAllPiecesToStructureBuilder(structurePiecesBuilder);
                }));
    }

    /**
     * Returns a piece from the provided pool to be used as the starting piece for a structure.
     * Pieces are chosen randomly, but some conditions as well as the isPriority flag are respected.
     * <p>
     * Note that only some conditions are supported. Conditions checking for things like piece position or orientation
     * should not be used, as instead those checks can be performed on the structure's placement itself.
     */
    private static Optional<PoolElementStructurePiece> getStartPiece(
            Holder<StructureTemplatePool> startPoolHolder,
            Optional<ResourceLocation> startJigsawNameOptional,
            BlockPos locatePos,
            StructureTemplateManager structureTemplateManager,
            RandomSource rand,
            LiquidSettings liquidSettings
    ) {
        StructureTemplatePool startPool = startPoolHolder.value();
        ObjectArrayList<Pair<StructurePoolElement, Integer>> candidatePoolElements = new ObjectArrayList<>(((StructureTemplatePoolAccessor) startPool).getRawTemplates());

        // Shuffle our candidate pool elements
        Util.shuffle(candidatePoolElements, rand);

        // Get a random orientation for starting piece
        Rotation rotation = Rotation.getRandom(rand);

        // Sum of weights in all pieces in the pool.
        // When choosing a piece, we will remove its weight from this sum.
        int totalWeightSum = candidatePoolElements.stream().mapToInt(Pair::getSecond).reduce(0, Integer::sum);

        while (candidatePoolElements.size() > 0 && totalWeightSum > 0) {
            Pair<StructurePoolElement, Integer> chosenPoolElementPair = null;

            // First, check for any priority pieces
            for (Pair<StructurePoolElement, Integer> candidatePiecePair : candidatePoolElements) {
                StructurePoolElement candidatePiece = candidatePiecePair.getFirst();
                if (candidatePiece instanceof YungJigsawPoolElement yungElement && yungElement.isPriorityPiece()) {
                    chosenPoolElementPair = candidatePiecePair;
                    break;
                }
            }

            // Randomly choose piece if priority piece wasn't selected
            if (chosenPoolElementPair == null) {
                // Random weight used to choose random piece from the pool of candidates
                int chosenWeight = rand.nextInt(totalWeightSum) + 1;

                // Randomly choose a candidate piece
                for (Pair<StructurePoolElement, Integer> candidate : candidatePoolElements) {
                    chosenWeight -= candidate.getSecond();
                    if (chosenWeight <= 0) {
                        chosenPoolElementPair = candidate;
                        break;
                    }
                }
            }

            // Extract data from the chosen piece pair.
            StructurePoolElement chosenPoolElement = chosenPoolElementPair.getFirst();
            int chosenPieceWeight = chosenPoolElementPair.getSecond();

            if (chosenPoolElement == EmptyPoolElement.INSTANCE) {
                return Optional.empty();
            }

            BlockPos anchorPos;
            if (startJigsawNameOptional.isPresent()) {
                ResourceLocation name = startJigsawNameOptional.get();
                Optional<BlockPos> optional = getPosOfJigsawBlockWithName(chosenPoolElement, name, locatePos, rotation, structureTemplateManager, rand);
                if (optional.isEmpty()) {
                    YungsApiCommon.LOGGER.error("No starting jigsaw with Name {} found in start pool {}", name, startPoolHolder.unwrapKey()
                            .map(pool -> pool.location().toString())
                            .orElse("<unregistered>"));
                    return Optional.empty();
                }

                anchorPos = optional.get();
            } else {
                anchorPos = locatePos;
            }

            // We adjust the starting position such that, if a named start jigsaw is being used (i.e. an anchor),
            // then the anchor's position will be located at the original starting position.
            Vec3i startingPosOffset = anchorPos.subtract(locatePos);
            BlockPos adjustedStartPos = locatePos.subtract(startingPosOffset);

            // Validate conditions for this piece, if applicable
            if (chosenPoolElement instanceof YungJigsawPoolElement yungElement) {
                StructureContext ctx = new StructureContext.Builder()
                        .structureTemplateManager(structureTemplateManager)
                        .pos(adjustedStartPos)
                        .rotation(rotation)
                        .depth(0)
                        .random(rand)
                        .build();
                if (!yungElement.passesConditions(ctx)) {
                    totalWeightSum -= chosenPieceWeight;
                    candidatePoolElements.remove(chosenPoolElementPair);
                    continue; // Abort this piece if it doesn't pass conditions check
                }
            }

            // Instantiate piece
            return Optional.of(new PoolElementStructurePiece(
                    structureTemplateManager,
                    chosenPoolElement,
                    adjustedStartPos,
                    chosenPoolElement.getGroundLevelDelta(),
                    rotation,
                    chosenPoolElement.getBoundingBox(structureTemplateManager, adjustedStartPos, rotation),
                    liquidSettings
            ));
        }
        return Optional.empty();
    }

    /**
     * Returns a jigsaw block with the specified name in the StructurePoolElement.
     * If no such jigsaw block is found, returns an empty Optional.
     * <p>
     * This is used for starting pieces, when you want /locate to point to a position other than the
     * corner of the start piece, such as the center of ancient cities.
     */
    private static Optional<BlockPos> getPosOfJigsawBlockWithName(
            StructurePoolElement structurePoolElement,
            ResourceLocation name,
            BlockPos startPos,
            Rotation rotation,
            StructureTemplateManager structureTemplateManager,
            RandomSource rand
    ) {
        // Wrap in try-catch because for some reason, getShuffledJigsawBlocks rarely throws a ConcurrentModificationException.
        // We'd rather just ignore the anchor jigsaw block than crash the game.
        try {
            List<StructureTemplate.StructureBlockInfo> shuffledJigsawBlocks = structurePoolElement.getShuffledJigsawBlocks(structureTemplateManager, startPos, rotation, rand);
            for (StructureTemplate.StructureBlockInfo jigsawBlockInfo : shuffledJigsawBlocks) {
                ResourceLocation jigsawBlockName = ResourceLocation.tryParse(jigsawBlockInfo.nbt().getString("name"));
                if (name.equals(jigsawBlockName)) {
                    return Optional.of(jigsawBlockInfo.pos());
                }
            }
        } catch (ConcurrentModificationException e) {
            YungsApiCommon.LOGGER.error("Encountered unexpected ConcurrentModException while trying to get jigsaw block with name {} from structure pool element {}", name, structurePoolElement);
            YungsApiCommon.LOGGER.error("Ignoring - the structure will still generate, but /locate will not point to the structure's anchor block.");
            return Optional.empty();
        }

        return Optional.empty();
    }
}
