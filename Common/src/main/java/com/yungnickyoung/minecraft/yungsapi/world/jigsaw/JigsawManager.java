package com.yungnickyoung.minecraft.yungsapi.world.jigsaw;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.BoundingBoxAccessor;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.StructureTemplatePoolAccessor;
import com.yungnickyoung.minecraft.yungsapi.util.BoxOctree;
import com.yungnickyoung.minecraft.yungsapi.world.condition.ConditionContext;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.IMaxCountJigsawPiece;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.YungJigsawSinglePoolElement;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;

public class JigsawManager {
    public static Optional<Structure.GenerationStub> assembleJigsawStructure(
            Structure.GenerationContext generationContext,
            Holder<StructureTemplatePool> startPool,
            Optional<ResourceLocation> startJigsawNameOptional,
            int maxDepth,
            BlockPos startPos,
            boolean useExpansionHack, // Used to be doBoundaryAdjustments
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxDistanceFromCenter, // Used to be structureBoundingBoxRadius
            Optional<Integer> maxY,
            Optional<Integer> minY
    ) {
        // Extract data from context
        RegistryAccess registryAccess = generationContext.registryAccess();
        ChunkGenerator chunkGenerator = generationContext.chunkGenerator();
        StructureTemplateManager structureManager = generationContext.structureTemplateManager();
        LevelHeightAccessor levelHeightAccessor = generationContext.heightAccessor();
        WorldgenRandom worldgenRandom = generationContext.random();

        // Get jigsaw pool registry
        Registry<StructureTemplatePool> registry = registryAccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);

        // Get a random orientation for starting piece
        Rotation rotation = Rotation.getRandom(worldgenRandom);

        // Get starting pool
        StructureTemplatePool structureTemplatePool = startPool.value();

        // Grab a random starting piece from the start pool. This is just the piece design itself, without rotation or position information.
        // Think of it as a blueprint.
        StructurePoolElement startPieceBlueprint = structureTemplatePool.getRandomTemplate(worldgenRandom);
        if (startPieceBlueprint == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        }

        BlockPos blockPos;
        if (startJigsawNameOptional.isPresent()) {
            ResourceLocation name = startJigsawNameOptional.get();
            Optional<BlockPos> optional = getPosOfJigsawBlockWithName(startPieceBlueprint, name, startPos, rotation, structureManager, worldgenRandom);
            if (optional.isEmpty()) {
                YungsApiCommon.LOGGER.error("No starting jigsaw with Name {} found in start pool {}", name, startPool.unwrapKey().get().location());
                return Optional.empty();
            }

            blockPos = optional.get();
        } else {
            blockPos = startPos;
        }

        Vec3i startingPosOffset = blockPos.subtract(startPos);
        BlockPos adjustedStartPos = startPos.subtract(startingPosOffset);

        // Instantiate a piece using the "blueprint" we just got.
        PoolElementStructurePiece startPiece = new PoolElementStructurePiece(
                structureManager,
                startPieceBlueprint,
                adjustedStartPos,
                startPieceBlueprint.getGroundLevelDelta(),
                rotation,
                startPieceBlueprint.getBoundingBox(structureManager, adjustedStartPos, rotation)
        );

        // Store center position of starting piece's bounding box
        BoundingBox pieceBoundingBox = startPiece.getBoundingBox();
        int pieceCenterX = (pieceBoundingBox.maxX() + pieceBoundingBox.minX()) / 2;
        int pieceCenterZ = (pieceBoundingBox.maxZ() + pieceBoundingBox.minZ()) / 2;
        int pieceCenterY = projectStartToHeightmap
                .map(types -> startPos.getY() + chunkGenerator.getFirstFreeHeight(pieceCenterX, pieceCenterZ, types, levelHeightAccessor, generationContext.randomState()))
                .orElseGet(adjustedStartPos::getY);

        int yAdjustment = pieceBoundingBox.minY() + startPiece.getGroundLevelDelta();
        startPiece.move(0, pieceCenterY - yAdjustment, 0);
        int adjustedPieceCenterY = pieceCenterY + startingPosOffset.getY();

        return Optional.of(new Structure.GenerationStub(new BlockPos(pieceCenterX, adjustedPieceCenterY, pieceCenterZ), (structurePiecesBuilder) -> {
            ArrayList<PoolElementStructurePiece> pieces = Lists.newArrayList();
            pieces.add(startPiece);
            if (maxDepth <= 0) { // Realistically this should never be true. Why make a jigsaw config with a non-positive size?
                return;
            }

            // Establish max bounds of entire structure.
            // Make sure the supplied distance is large enough to cover the size of your entire structure.
            AABB aABB = new AABB(
                    pieceCenterX - maxDistanceFromCenter, adjustedPieceCenterY - maxDistanceFromCenter, pieceCenterZ - maxDistanceFromCenter,
                    pieceCenterX + maxDistanceFromCenter + 1, adjustedPieceCenterY + maxDistanceFromCenter + 1, pieceCenterZ + maxDistanceFromCenter + 1);
            BoxOctree maxStructureBounds = new BoxOctree(aABB); // The maximum boundary of the entire structure
            maxStructureBounds.addBox(AABB.of(pieceBoundingBox)); // Add start piece to our structure's bounds

            // Create placer + initial entry
            Assembler assembler = new Assembler(registry, maxDepth, chunkGenerator, structureManager, pieces, worldgenRandom, maxY, minY);
            PieceEntry startPieceEntry = new PieceEntry(startPiece, new MutableObject<>(maxStructureBounds), null, 0, null, null, null);

            // Add the start piece to the placer
            assembler.unprocessedPieceEntries.addLast(startPieceEntry);

            // Assemble the structure
            while (!assembler.unprocessedPieceEntries.isEmpty()) {
                PieceEntry entry = assembler.unprocessedPieceEntries.removeFirst();
                assembler.processPieceEntry(entry, useExpansionHack, levelHeightAccessor, generationContext.randomState());
            }

            // Add all assembled pieces to the structure builder. These will be placed at a later stage of worldgen.
            pieces.forEach(structurePiecesBuilder::addPiece);
        }));
    }

    private static Optional<BlockPos> getPosOfJigsawBlockWithName(StructurePoolElement startPieceBlueprint, ResourceLocation name, BlockPos startPos, Rotation rotation, StructureTemplateManager structureTemplateManager, WorldgenRandom worldgenRandom) {
        List<StructureTemplate.StructureBlockInfo> shuffledJigsawBlocks = startPieceBlueprint.getShuffledJigsawBlocks(structureTemplateManager, startPos, rotation, worldgenRandom);
        for (StructureTemplate.StructureBlockInfo jigsawBlockInfo : shuffledJigsawBlocks) {
            ResourceLocation jigsawBlockName = ResourceLocation.tryParse(jigsawBlockInfo.nbt.getString("name"));
            if (name.equals(jigsawBlockName)) {
                return Optional.of(jigsawBlockInfo.pos);
            }
        }

        return Optional.empty();
    }

    public static final class Assembler {
        // Vanilla
        private final Registry<StructureTemplatePool> poolRegistry;
        private final int maxDepth;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super PoolElementStructurePiece> pieces;
        private final RandomSource rand;
        public final Deque<PieceEntry> unprocessedPieceEntries = Queues.newArrayDeque();

        // Additional, non-vanilla behavior
        private final Map<String, Integer> pieceCounts;
        private final Map<String, Integer> maxPieceCounts;
        private final Optional<Integer> maxY;
        private final Optional<Integer> minY;

        public Assembler(
                Registry<StructureTemplatePool> poolRegistry,
                int maxDepth,
                ChunkGenerator chunkGenerator,
                StructureTemplateManager structureTemplateManager,
                List<? super PoolElementStructurePiece> pieces,
                RandomSource rand,
                Optional<Integer> maxY,
                Optional<Integer> minY
        ) {
            this.poolRegistry = poolRegistry;
            this.maxDepth = maxDepth;
            this.chunkGenerator = chunkGenerator;
            this.structureTemplateManager = structureTemplateManager;
            this.pieces = pieces;
            this.rand = rand;

            // Initialize piece counts
            this.pieceCounts = new HashMap<>();
            this.maxPieceCounts = new HashMap<>();
            this.maxY = maxY;
            this.minY = minY;
        }

        public void processPieceEntry(
                PieceEntry pieceEntry,
                boolean useExpansionHack,
                LevelHeightAccessor levelHeightAccessor,
                RandomState randomState
        ) {
            processPieceEntry(pieceEntry, useExpansionHack, levelHeightAccessor, randomState, false, true);
        }

        public void processPieceEntry(
                PieceEntry pieceEntry,
                boolean useExpansionHack,
                LevelHeightAccessor levelHeightAccessor,
                RandomState randomState,
                boolean forceUseFallback,
                boolean doDeadendCheck
        ) {
            // Collect data from params regarding piece to process
            PoolElementStructurePiece piece = pieceEntry.getPiece();
            int depth = pieceEntry.getDepth();
            BoundingBox pieceBoundingBox = piece.getBoundingBox();
            int pieceMinY = pieceBoundingBox.minY();
            MutableObject<BoxOctree> parentOctree = new MutableObject<>();

            // Get list of all jigsaw blocks in this piece
            List<StructureTemplate.StructureBlockInfo> pieceJigsawBlocks = piece.getElement().getShuffledJigsawBlocks(
                    this.structureTemplateManager,
                    piece.getPosition(),
                    piece.getRotation(),
                    this.rand);

            boolean generatedAtLeastOneChildPiece = false;

            for (StructureTemplate.StructureBlockInfo jigsawBlockInfo : pieceJigsawBlocks) {
                // Gather jigsaw block information
                Direction direction = JigsawBlock.getFrontFacing(jigsawBlockInfo.state);
                BlockPos jigsawBlockPos = jigsawBlockInfo.pos;
                BlockPos jigsawBlockTargetPos = jigsawBlockPos.relative(direction);

                // Get the jigsaw block's piece pool
                ResourceLocation targetPoolId = new ResourceLocation(jigsawBlockInfo.nbt.getString("pool"));
                Optional<StructureTemplatePool> poolOptional = this.poolRegistry.getOptional(targetPoolId);

                // Only continue if our pool exists and is not empty.
                // The only allowed empty pool is minecraft:empty.
                if (poolOptional.isEmpty() || (poolOptional.get().size() == 0 && !Objects.equals(targetPoolId, Pools.EMPTY.location()))) {
                    YungsApiCommon.LOGGER.warn("Empty or nonexistent pool: {}", targetPoolId);
                    continue;
                }

                StructureTemplatePool targetPool = poolOptional.get();

                // Get the jigsaw block's fallback pool (defined in the pool's JSON)
                ResourceLocation targetPoolFallbackId = targetPool.getFallback();
                Optional<StructureTemplatePool> fallbackPoolOptional = this.poolRegistry.getOptional(targetPoolFallbackId);

                // Only continue if the fallback pool exists and is not empty.
                // The only allowed empty pool is minecraft:empty.
                if (fallbackPoolOptional.isEmpty() || (fallbackPoolOptional.get().size() == 0 && !Objects.equals(targetPoolFallbackId, Pools.EMPTY.location()))) {
                    YungsApiCommon.LOGGER.warn("Empty or nonexistent fallback pool: {}", targetPoolFallbackId);
                    continue;
                }

                StructureTemplatePool fallbackPool = fallbackPoolOptional.get();

                // Adjustments for if the target block position is inside the current piece
                boolean isTargetInsideCurrentPiece = pieceBoundingBox.isInside(jigsawBlockTargetPos);
                MutableObject<BoxOctree> octreeToUse;
                if (isTargetInsideCurrentPiece) {
                    octreeToUse = parentOctree;
                    if (parentOctree.getValue() == null) {
                        parentOctree.setValue(new BoxOctree(AABB.of(pieceBoundingBox)));
                    }
                } else {
                    octreeToUse = pieceEntry.getBoxOctree();
                }

                StructurePoolElement newlyGeneratedPiece = null;
                PieceContext pieceContext = new PieceContext(
                        new ObjectArrayList<>(((StructureTemplatePoolAccessor) targetPool).getRawTemplates()),
                        useExpansionHack,
                        jigsawBlockInfo,
                        jigsawBlockTargetPos,
                        pieceMinY,
                        jigsawBlockPos,
                        octreeToUse,
                        pieceEntry,
                        depth,
                        levelHeightAccessor,
                        randomState);

                // Attempt to place a piece from the target pool
                if (depth != this.maxDepth && !forceUseFallback) {
                    newlyGeneratedPiece = this.processList(pieceContext);
                }

                // If no pieces in the target pool could be placed, try the fallback pool
                if (newlyGeneratedPiece == null) {
                    pieceContext.candidatePoolElements = new ObjectArrayList<>(((StructureTemplatePoolAccessor) fallbackPool).getRawTemplates());
                    newlyGeneratedPiece = this.processList(pieceContext);
                }

                // If any piece was placed, we record that fact for later
                if (newlyGeneratedPiece != null) {
                    generatedAtLeastOneChildPiece = true;
                }
            }

            /* If this piece has jigsaw blocks leading out of it yet no child pieces were able to be placed,
               convert this piece to a dead end.
               This is accomplished by re-processing its parent piece, but force using the fallback pool.
             */
            if (pieceEntry.hasDeadendAdjustment() && !generatedAtLeastOneChildPiece && doDeadendCheck && pieceJigsawBlocks.size() > 1) {
                // Replace this piece with one from its fallback pool
                PieceEntry parentEntry = pieceEntry.getParentEntry();
                PieceContext sourceContext = pieceEntry.getSourcePieceContext();
                AABB pieceAabb = pieceEntry.getPieceAabb();
                if (parentEntry != null && pieceAabb != null) {
                    parentEntry.getPiece().getJunctions().remove(pieceEntry.getParentJunction());
                    pieceEntry.getBoxOctree().getValue().removeBox(pieceAabb);
                    this.pieces.remove(piece);
                    this.processPieceEntry(parentEntry, useExpansionHack, levelHeightAccessor, randomState, true, false);
                }
            }
        }

        /**
         * Helper function. Searches candidatePoolElements for a suitable piece to spawn.
         *
         * @return The StructurePoolElement for the piece generated, or null if no suitable piece was found.
         */
        private StructurePoolElement processList(PieceContext context) {
            // Extract args from context
            ObjectArrayList<Pair<StructurePoolElement, Integer>> candidatePoolElements = context.candidatePoolElements;
            PoolElementStructurePiece piece = context.pieceEntry.getPiece();
            boolean isPieceRigid = piece.getElement().getProjection() == StructureTemplatePool.Projection.RIGID;
            int jigsawBlockRelativeY = context.jigsawBlockPos.getY() - context.pieceMinY;
            int surfaceHeight = -1; // The y-coordinate of the surface. Only used if isPieceRigid is false.

            // Shuffle our candidate pool elements
            Util.shuffle(candidatePoolElements, this.rand);

            // Sum of weights in all pieces in the pool.
            // When choosing a piece, we will remove its weight from this sum.
            int totalWeightSum = candidatePoolElements.stream().mapToInt(Pair::getSecond).reduce(0, Integer::sum);

            while (candidatePoolElements.size() > 0 && totalWeightSum > 0) {
                Pair<StructurePoolElement, Integer> chosenPoolElementPair = null;

                // First, check for any priority pieces
                for (Pair<StructurePoolElement, Integer> candidatePiecePair : candidatePoolElements) {
                    StructurePoolElement candidatePiece = candidatePiecePair.getFirst();
                    if (candidatePiece instanceof YungJigsawSinglePoolElement yungJigsawPiece && yungJigsawPiece.isPriorityPiece()) {
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

                // Abort if we reach an empty piece.
                // Not sure if aborting is necessary here, but this is vanilla behavior.
                if (chosenPoolElement == EmptyPoolElement.INSTANCE) {
                    return null;
                }

                // Validate to make sure we haven't reached the max number of instances of this piece, if applicable
                if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungJigsawPiece && yungJigsawPiece.maxCount.isPresent()) {
                    int pieceMaxCount = yungJigsawPiece.maxCount.get();

                    // Max count pieces must also be named
                    if (yungJigsawPiece.name.isEmpty()) {
                        YungsApiCommon.LOGGER.error("Found YUNG Jigsaw piece with max_count={} missing \"name\" property.", pieceMaxCount);
                        YungsApiCommon.LOGGER.error("Max count pieces must be named in order to work properly!");
                        YungsApiCommon.LOGGER.error("Ignoring max_count for this piece...");
                    } else {
                        String pieceName = yungJigsawPiece.name.get();
                        // Check if max count of this piece does not match stored max count for this name.
                        // This can happen when the same name is reused across pools, but the max count values are different.
                        if (this.maxPieceCounts.containsKey(pieceName) && this.maxPieceCounts.get(pieceName) != pieceMaxCount) {
                            YungsApiCommon.LOGGER.error("YUNG Jigsaw Piece with name {} and max_count {} does not match stored max_count of {}!", pieceName, pieceMaxCount, this.maxPieceCounts.get(pieceName));
                            YungsApiCommon.LOGGER.error("This can happen when multiple pieces across pools use the same name, but have different max_count values.");
                            YungsApiCommon.LOGGER.error("Please change these max_count values to match. Using max_count={} for now...", pieceMaxCount);
                        }

                        // Update stored maxCount entry
                        this.maxPieceCounts.put(pieceName, pieceMaxCount);

                        // If we reached the max count already, remove this piece from the list of candidates and retry
                        if (this.pieceCounts.getOrDefault(pieceName, 0) >= pieceMaxCount) {
                            totalWeightSum -= chosenPieceWeight;
                            candidatePoolElements.remove(chosenPoolElementPair);
                            continue;
                        }
                    }
                }

                // LEGACY - support for IMaxCountJigsawPiece
                if (chosenPoolElement instanceof IMaxCountJigsawPiece) {
                    String pieceName = ((IMaxCountJigsawPiece) chosenPoolElement).getName();
                    int maxCount = ((IMaxCountJigsawPiece) chosenPoolElement).getMaxCount();

                    // Check if max count of this piece does not match stored max count for this name.
                    // This can happen when the same name is reused pool entries, but the max count values are different.
                    if (this.maxPieceCounts.containsKey(pieceName) && this.maxPieceCounts.get(pieceName) != maxCount) {
                        YungsApiCommon.LOGGER.error("Max Count Jigsaw Piece with name {} and max_count {} does not match stored max_count of {}!", pieceName, maxCount, this.maxPieceCounts.get(pieceName));
                        YungsApiCommon.LOGGER.error("This can happen when multiple pieces across pools use the same name, but have different max_count values.");
                        YungsApiCommon.LOGGER.error("Please change these max_count values to match. Using max_count={} for now...", maxCount);
                    }

                    // Update stored maxCount entry
                    this.maxPieceCounts.put(pieceName, maxCount);

                    // Remove this piece from the list of candidates and retry if we reached the max count
                    if (this.pieceCounts.getOrDefault(pieceName, 0) >= maxCount) {
                        totalWeightSum -= chosenPoolElementPair.getSecond();
                        candidatePoolElements.remove(chosenPoolElementPair);
                        continue;
                    }
                }

                // Validate piece depth, if applicable
                if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungJigsawPiece && !yungJigsawPiece.isAtValidDepth(context.depth)) {
                    totalWeightSum -= chosenPieceWeight;
                    candidatePoolElements.remove(chosenPoolElementPair);
                    continue;
                }

                // Try different rotations to see which sides of the piece are fit to be the receiving end
                for (Rotation rotation : Rotation.getShuffled(this.rand)) {
                    List<StructureTemplate.StructureBlockInfo> candidateJigsawBlocks = chosenPoolElement.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, rotation, this.rand);

                    // Some sort of logic for setting the candidateHeightAdjustments var if useExpansionHack.
                    // Not sure on this - personally, I never enable useExpansionHack.
                    BoundingBox tempCandidateBoundingBox = chosenPoolElement.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, rotation);
                    int candidateHeightAdjustments = 0;
                    if (context.useExpansionHack && tempCandidateBoundingBox.getYSpan() <= 16) {
                        candidateHeightAdjustments = candidateJigsawBlocks.stream().mapToInt((pieceCandidateJigsawBlock) -> {
                            if (!tempCandidateBoundingBox.isInside(pieceCandidateJigsawBlock.pos.relative(JigsawBlock.getFrontFacing(pieceCandidateJigsawBlock.state)))) {
                                return 0;
                            }
                            ResourceLocation candidateTargetPool = new ResourceLocation(pieceCandidateJigsawBlock.nbt.getString("pool"));
                            Optional<StructureTemplatePool> candidateTargetPoolOptional = this.poolRegistry.getOptional(candidateTargetPool);
                            Optional<StructureTemplatePool> candidateTargetFallbackOptional = candidateTargetPoolOptional.flatMap((StructureTemplatePool) -> this.poolRegistry.getOptional(StructureTemplatePool.getFallback()));
                            int tallestCandidateTargetPoolPieceHeight = candidateTargetPoolOptional.map((structureTemplatePool) -> structureTemplatePool.getMaxSize(this.structureTemplateManager)).orElse(0);
                            int tallestCandidateTargetFallbackPieceHeight = candidateTargetFallbackOptional.map((structureTemplatePool) -> structureTemplatePool.getMaxSize(this.structureTemplateManager)).orElse(0);
                            return Math.max(tallestCandidateTargetPoolPieceHeight, tallestCandidateTargetFallbackPieceHeight);
                        }).max().orElse(0);
                    }

                    // Check each of the candidate's jigsaw blocks for a match
                    for (StructureTemplate.StructureBlockInfo candidateJigsawBlock : candidateJigsawBlocks) {
                        if (!JigsawBlock.canAttach(context.jigsawBlock, candidateJigsawBlock)) continue;

                        BlockPos candidateJigsawBlockPos = candidateJigsawBlock.pos;
                        BlockPos candidateJigsawBlockRelativePos = context.jigsawBlockTargetPos.subtract(candidateJigsawBlockPos);

                        // Get the rotated bounding box for the piece, offset by the relative position difference
                        BoundingBox rotatedCandidateBoundingBox = chosenPoolElement.getBoundingBox(this.structureTemplateManager, candidateJigsawBlockRelativePos, rotation);

                        // Determine if candidate is rigid
                        StructureTemplatePool.Projection candidatePlacementBehavior = chosenPoolElement.getProjection();
                        boolean isCandidateRigid = candidatePlacementBehavior == StructureTemplatePool.Projection.RIGID;

                        // Determine how much the candidate jigsaw block is off in the y direction.
                        // This will be needed to offset the candidate piece so that the jigsaw blocks line up properly.
                        int candidateJigsawBlockRelativeY = candidateJigsawBlockPos.getY();
                        int candidateJigsawYOffsetNeeded = jigsawBlockRelativeY - candidateJigsawBlockRelativeY + JigsawBlock.getFrontFacing(context.jigsawBlock.state).getStepY();

                        // Determine how much we need to offset the candidate piece itself in order to have the jigsaw blocks aligned.
                        // Depends on if the placement of both pieces is rigid or not
                        int adjustedCandidatePieceMinY;
                        if (isPieceRigid && isCandidateRigid) {
                            adjustedCandidatePieceMinY = context.pieceMinY + candidateJigsawYOffsetNeeded;
                        } else {
                            if (surfaceHeight == -1) {
                                surfaceHeight = this.chunkGenerator.getFirstFreeHeight(context.jigsawBlockPos.getX(), context.jigsawBlockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.levelHeightAccessor, context.randomState);
                            }

                            adjustedCandidatePieceMinY = surfaceHeight - candidateJigsawBlockRelativeY;
                        }
                        int candidatePieceYOffsetNeeded = adjustedCandidatePieceMinY - rotatedCandidateBoundingBox.minY();

                        // Offset the candidate's bounding box by the necessary amount
                        BoundingBox adjustedCandidateBoundingBox = rotatedCandidateBoundingBox.moved(0, candidatePieceYOffsetNeeded, 0);

                        // Add this offset to the relative jigsaw block position as well
                        BlockPos adjustedCandidateJigsawBlockRelativePos = candidateJigsawBlockRelativePos.offset(0, candidatePieceYOffsetNeeded, 0);

                        // Final adjustments to the bounding box. Can only happen if useExpansionHack is true.
                        if (candidateHeightAdjustments > 0) {
                            int k2 = Math.max(candidateHeightAdjustments + 1, adjustedCandidateBoundingBox.maxY() - adjustedCandidateBoundingBox.minY());
                            ((BoundingBoxAccessor) adjustedCandidateBoundingBox).setMaxY(adjustedCandidateBoundingBox.minY() + k2);
                        }

                        // Prevent pieces from spawning above max Y and below min Y
                        if (this.maxY.isPresent() && adjustedCandidateBoundingBox.maxY() > this.maxY.get()) continue;
                        if (this.minY.isPresent() && adjustedCandidateBoundingBox.minY() < this.minY.get()) continue;

                        // Final boundary check before adding the new piece.
                        // Not sure why the candidate box is shrunk by 0.25. Maybe just ensures no overlap for adjacent block positions?
                        AABB aabb = AABB.of(adjustedCandidateBoundingBox);
                        AABB aabbDeflated = aabb.deflate(0.25);
                        boolean pieceIgnoresBounds = false;

                        if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungJigsawPiece) {
                            pieceIgnoresBounds = yungJigsawPiece.ignoresBounds();
                        }

                        // Validate piece boundaries
                        if (!pieceIgnoresBounds) {
                            boolean pieceIntersectsExistingPieces = context.boxOctree.getValue().intersectsAnyBox(aabbDeflated);
                            boolean pieceIsContainedWithinStructureBoundaries = context.boxOctree.getValue().boundaryContains(aabbDeflated);
                            if (pieceIntersectsExistingPieces || !pieceIsContainedWithinStructureBoundaries) {
                                continue;
                            }
                        }

                        // Validate conditions for this piece, if applicable
                        if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungJigsawPiece) {
                            ConditionContext ctx = new ConditionContext.Builder()
                                    .pieceMinY(adjustedCandidateBoundingBox.minY())
                                    .pieceMaxY(adjustedCandidateBoundingBox.maxY())
                                    .depth(context.depth)
                                    .build();
                            if (!yungJigsawPiece.passesConditions(ctx)) {
                                continue; // Abort this piece & rotation if it doesn't pass conditions check
                            }
                        }

                        // At this point we are locked in for adding this piece, so add its box to the structure's
                        context.boxOctree.getValue().addBox(aabb);

                        // Determine ground level delta for this new piece
                        int newPieceGroundLevelDelta = piece.getGroundLevelDelta();
                        int groundLevelDelta;
                        if (isCandidateRigid) {
                            groundLevelDelta = newPieceGroundLevelDelta - candidateJigsawYOffsetNeeded;
                        } else {
                            groundLevelDelta = chosenPoolElement.getGroundLevelDelta();
                        }

                        // Create new piece
                        PoolElementStructurePiece newPiece = new PoolElementStructurePiece(
                                this.structureTemplateManager,
                                chosenPoolElement,
                                adjustedCandidateJigsawBlockRelativePos,
                                groundLevelDelta,
                                rotation,
                                adjustedCandidateBoundingBox
                        );

                        // Determine actual y-value for the new jigsaw block
                        int candidateJigsawBlockY;
                        if (isPieceRigid) {
                            candidateJigsawBlockY = context.pieceMinY + jigsawBlockRelativeY;
                        } else if (isCandidateRigid) {
                            candidateJigsawBlockY = adjustedCandidatePieceMinY + candidateJigsawBlockRelativeY;
                        } else {
                            if (surfaceHeight == -1) {
                                surfaceHeight = this.chunkGenerator.getFirstFreeHeight(context.jigsawBlockPos.getX(), context.jigsawBlockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.levelHeightAccessor, context.randomState);
                            }

                            candidateJigsawBlockY = surfaceHeight + candidateJigsawYOffsetNeeded / 2;
                        }

                        // Add the junction to the existing piece
                        JigsawJunction newJunctionOnParent = new JigsawJunction(
                                context.jigsawBlockTargetPos.getX(),
                                candidateJigsawBlockY - jigsawBlockRelativeY + newPieceGroundLevelDelta,
                                context.jigsawBlockTargetPos.getZ(),
                                candidateJigsawYOffsetNeeded,
                                candidatePlacementBehavior);
                        piece.addJunction(newJunctionOnParent);

                        // Add the junction to the new piece
                        newPiece.addJunction(
                                new JigsawJunction(
                                        context.jigsawBlockPos.getX(),
                                        candidateJigsawBlockY - candidateJigsawBlockRelativeY + groundLevelDelta,
                                        context.jigsawBlockPos.getZ(),
                                        -candidateJigsawYOffsetNeeded,
                                        piece.getElement().getProjection())
                        );

                        // Add the piece
                        PieceEntry newPieceEntry = new PieceEntry(newPiece, context.boxOctree, aabb,
                                context.depth + 1, context.pieceEntry, context.copy(), newJunctionOnParent);
                        this.pieces.add(newPiece);
                        context.pieceEntry.addChildEntry(newPieceEntry);

                        // If there's still room, add an entry for the new piece to be processed
                        if (context.depth + 1 <= this.maxDepth) {
                            this.unprocessedPieceEntries.addLast(newPieceEntry);
                        }

                        // Update piece count, if applicable
                        if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungJigsawPiece && yungJigsawPiece.maxCount.isPresent()) {
                            // Max count pieces must also be named.
                            // The following condition will never be met if users correctly configure their template pools.
                            if (yungJigsawPiece.name.isEmpty()) {
                                // If name is missing, ignore max count for this piece. We've already logged an error for it earlier.
                                return chosenPoolElement;
                            }

                            String pieceName = yungJigsawPiece.name.get();
                            this.pieceCounts.put(pieceName, this.pieceCounts.getOrDefault(pieceName, 0) + 1);
                        }

                        // LEGACY - support for IMaxCountJigsawPiece
                        if (chosenPoolElement instanceof IMaxCountJigsawPiece) {
                            String pieceName = ((IMaxCountJigsawPiece) chosenPoolElement).getName();
                            this.pieceCounts.put(pieceName, this.pieceCounts.getOrDefault(pieceName, 0) + 1);
                        }

                        return chosenPoolElement;
                    }
                }
                totalWeightSum -= chosenPieceWeight;
                candidatePoolElements.remove(chosenPoolElementPair);
            }
            return null;
        }
    }

    public static class PieceContext {
        public ObjectArrayList<Pair<StructurePoolElement, Integer>> candidatePoolElements;
        public boolean useExpansionHack;
        public StructureTemplate.StructureBlockInfo jigsawBlock;
        public BlockPos jigsawBlockTargetPos;
        public int pieceMinY;
        public BlockPos jigsawBlockPos;
        public MutableObject<BoxOctree> boxOctree;
        public PieceEntry pieceEntry;
        public int depth;
        public LevelHeightAccessor levelHeightAccessor;
        public RandomState randomState;

        public PieceContext(ObjectArrayList<Pair<StructurePoolElement, Integer>> candidatePoolElements,
                            boolean useExpansionHack,
                            StructureTemplate.StructureBlockInfo jigsawBlock,
                            BlockPos jigsawBlockTargetPos,
                            int pieceMinY,
                            BlockPos jigsawBlockPos,
                            MutableObject<BoxOctree> boxOctree,
                            PieceEntry pieceEntry,
                            int depth,
                            LevelHeightAccessor levelHeightAccessor,
                            RandomState randomState) {
            this.candidatePoolElements = candidatePoolElements;
            this.useExpansionHack = useExpansionHack;
            this.jigsawBlock = jigsawBlock;
            this.jigsawBlockTargetPos = jigsawBlockTargetPos;
            this.pieceMinY = pieceMinY;
            this.jigsawBlockPos = jigsawBlockPos;
            this.boxOctree = boxOctree;
            this.pieceEntry = pieceEntry;
            this.depth = depth;
            this.levelHeightAccessor = levelHeightAccessor;
            this.randomState = randomState;
        }

        public PieceContext copy() {
            return new PieceContext(candidatePoolElements, useExpansionHack, jigsawBlock, jigsawBlockTargetPos,
                    pieceMinY, jigsawBlockPos, boxOctree, pieceEntry, depth, levelHeightAccessor, randomState);
        }
    }
}
