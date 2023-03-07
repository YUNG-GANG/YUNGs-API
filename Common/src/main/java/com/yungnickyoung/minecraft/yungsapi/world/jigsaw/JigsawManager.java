package com.yungnickyoung.minecraft.yungsapi.world.jigsaw;

import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.yungsapi.api.YungJigsawConfig;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.StructureTemplatePoolAccessor;
import com.yungnickyoung.minecraft.yungsapi.util.BoxOctree;
import com.yungnickyoung.minecraft.yungsapi.util.Util;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.assembler.JigsawStructureAssembler;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.YungJigsawSinglePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.phys.AABB;

import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class JigsawManager {
    public static Optional<PieceGenerator<YungJigsawConfig>> assembleJigsawStructure(
        PieceGeneratorSupplier.Context<YungJigsawConfig> ctx,
        JigsawPlacement.PieceFactory pieceFactory,
        BlockPos startPos,
        boolean doBoundaryAdjustments,
        boolean useHeightmap,
        int maxDistanceFromCenter // Used to be structureBoundingBoxRadius
    ) {
        // Extract data from context
        WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
        random.setLargeFeatureSeed(ctx.seed(), ctx.chunkPos().x, ctx.chunkPos().z);
        ChunkGenerator chunkGenerator = ctx.chunkGenerator();
        StructureManager structureManager = ctx.structureManager();
        LevelHeightAccessor levelHeightAccessor = ctx.heightAccessor();
        Predicate<Holder<Biome>> validBiomePredicate = ctx.validBiome();
        Registry<StructureTemplatePool> registry = ctx.registryAccess().registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
        YungJigsawConfig config = ctx.config();

        StructureFeature.bootstrap(); // Ensures static members are all loaded

        // Get starting pool
        StructureTemplatePool startPool = registry.get(config.getStartPool());

        // Grab a random starting piece from the start pool
        Optional<PoolElementStructurePiece> startPieceOptional = getStartPiece(startPool, startPos, structureManager, pieceFactory, random);
        if (startPieceOptional.isEmpty()) {
            return Optional.empty();
        }
        PoolElementStructurePiece startPiece = startPieceOptional.get();

        // Grab some data regarding starting piece's bounding box & position
        BoundingBox pieceBoundingBox = startPiece.getBoundingBox();
        int bbCenterX = (pieceBoundingBox.maxX() + pieceBoundingBox.minX()) / 2;
        int bbCenterZ = (pieceBoundingBox.maxZ() + pieceBoundingBox.minZ()) / 2;
        int bbCenterY = useHeightmap
            ? startPos.getY() + chunkGenerator.getFirstFreeHeight(bbCenterX, bbCenterZ, Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor)
            : startPos.getY();

        // Validate biome at position
        if (!validBiomePredicate.test(chunkGenerator.getNoiseBiome(QuartPos.fromBlock(bbCenterX), QuartPos.fromBlock(bbCenterY), QuartPos.fromBlock(bbCenterZ)))) {
            return Optional.empty();
        }

        // Move the starting piece to account for any y-level change due to heightmap and/or groundLevelDelta
        int yAdjustment = pieceBoundingBox.minY() + startPiece.getGroundLevelDelta();
        startPiece.move(0, bbCenterY - yAdjustment, 0);

        // Establish max bounds of entire structure.
        // Make sure the supplied radius is large enough to cover the size of your entire structure.
        AABB aABB = new AABB(
                bbCenterX - maxDistanceFromCenter, bbCenterY - maxDistanceFromCenter, bbCenterZ - maxDistanceFromCenter,
                bbCenterX + maxDistanceFromCenter + 1, bbCenterY + maxDistanceFromCenter + 1, bbCenterZ + maxDistanceFromCenter + 1);
        BoxOctree maxStructureBounds = new BoxOctree(aABB); // The maximum boundary of the entire structure
        maxStructureBounds.addBox(AABB.of(pieceBoundingBox)); // Add start piece to our structure's bounds

        return Optional.of((structurePiecesBuilder, context) -> {
            if (config.getMaxDepth() <= 0) { // Realistically this should never be true. Why make a jigsaw config with a non-positive size?
                return;
            }

            // Create assembler + initial entry
            JigsawStructureAssembler assembler = new JigsawStructureAssembler(new JigsawStructureAssembler.Settings()
                    .poolRegistry(registry)
                    .maxDepth(config.getMaxDepth())
                    .chunkGenerator(chunkGenerator)
                    .structureManager(structureManager)
                    .rand(random)
                    .maxY(config.getMaxY())
                    .minY(config.getMinY())
                    .useExpansionHack(doBoundaryAdjustments)
                    .levelHeightAccessor(levelHeightAccessor)
                    .pieceFactory(pieceFactory));

            // Add the start piece to the assembler & assemble the structure
            assembler.assembleStructure(startPiece, maxStructureBounds);
            assembler.addAllPiecesToStructureBuilder(structurePiecesBuilder);
        });
    }

    /**
     * Returns a piece from the provided pool to be used as the starting piece for a structure.
     * Pieces are chosen randomly, but some conditions as well as the isPriority flag are respected.
     * <p>
     * Note that only some conditions are supported. Conditions checking for things like piece position or orientation
     * should not be used, as instead those checks can be performed on the structure's placement itself.
     */
    private static Optional<PoolElementStructurePiece> getStartPiece(
            StructureTemplatePool startPool,
            BlockPos locatePos,
            StructureManager structureManager,
            JigsawPlacement.PieceFactory pieceFactory,
            Random rand
    ) {
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
                if (candidatePiece instanceof YungJigsawSinglePoolElement yungSingleElement && yungSingleElement.isPriorityPiece()) {
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

            // Validate conditions for this piece, if applicable
            if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungSingleElement) {
                StructureContext ctx = new StructureContext.Builder()
                        .structureManager(structureManager)
                        .pos(locatePos)
                        .rotation(rotation)
                        .depth(0)
                        .random(rand)
                        .build();
                if (!yungSingleElement.passesConditions(ctx)) {
                    totalWeightSum -= chosenPieceWeight;
                    candidatePoolElements.remove(chosenPoolElementPair);
                    continue; // Abort this piece if it doesn't pass conditions check
                }
            }

            // Instantiate piece
            return Optional.of(pieceFactory.create(
                    structureManager,
                    chosenPoolElement,
                    locatePos,
                    chosenPoolElement.getGroundLevelDelta(),
                    rotation,
                    chosenPoolElement.getBoundingBox(structureManager, locatePos, rotation)
            ));
        }
        return Optional.empty();
    }
}
