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
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
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
            PieceEntry startPieceEntry = new PieceEntry(startPiece, new MutableObject<>(maxStructureBounds), 0);

            // Add the start piece to the placer
            assembler.pieceQueue.addLast(startPieceEntry);

            // Assemble the structure
            while (!assembler.pieceQueue.isEmpty()) {
                PieceEntry entry = assembler.pieceQueue.removeFirst();
                assembler.processPiece(entry.piece, entry.boxOctreeMutableObject, entry.depth, useExpansionHack, levelHeightAccessor, generationContext.randomState());
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
        public final Deque<PieceEntry> pieceQueue = Queues.newArrayDeque();

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

        public void processPiece(
                PoolElementStructurePiece piece,
                MutableObject<BoxOctree> boxOctree,
                int depth,
                boolean useExpansionHack,
                LevelHeightAccessor levelHeightAccessor,
                RandomState randomState
        ) {
            // Collect data from params regarding piece to process
            StructurePoolElement pieceBlueprint = piece.getElement();
            BlockPos piecePos = piece.getPosition();
            Rotation pieceRotation = piece.getRotation();
            BoundingBox pieceBoundingBox = piece.getBoundingBox();
            int pieceMinY = pieceBoundingBox.minY();
            MutableObject<BoxOctree> parentOctree = new MutableObject<>();

            // Get list of all jigsaw blocks in this piece
            List<StructureTemplate.StructureBlockInfo> pieceJigsawBlocks = pieceBlueprint.getShuffledJigsawBlocks(this.structureTemplateManager, piecePos, pieceRotation, this.rand);

            for (StructureTemplate.StructureBlockInfo jigsawBlockInfo : pieceJigsawBlocks) {
                // Gather jigsaw block information
                Direction direction = JigsawBlock.getFrontFacing(jigsawBlockInfo.state);
                BlockPos jigsawBlockPos = jigsawBlockInfo.pos;
                BlockPos jigsawBlockTargetPos = jigsawBlockPos.relative(direction);

                // Get the jigsaw block's piece pool
                ResourceLocation jigsawBlockPoolId = new ResourceLocation(jigsawBlockInfo.nbt.getString("pool"));
                Optional<StructureTemplatePool> poolOptional = this.poolRegistry.getOptional(jigsawBlockPoolId);

                // Only continue if our pool exists and is not empty.
                // The only allowed empty pool is minecraft:empty.
                if (!(poolOptional.isPresent() && (poolOptional.get().size() != 0 || Objects.equals(jigsawBlockPoolId, Pools.EMPTY.location())))) {
                    YungsApiCommon.LOGGER.warn("Empty or nonexistent pool: {}", jigsawBlockPoolId);
                    continue;
                }

                // Get the jigsaw block's fallback pool (which is a part of the pool's JSON)
                ResourceLocation jigsawBlockFallback = poolOptional.get().getFallback();
                Optional<StructureTemplatePool> fallbackOptional = this.poolRegistry.getOptional(jigsawBlockFallback);

                // Only continue if the fallback pool exists and is not empty.
                // The only allowed empty pool is minecraft:empty.
                if (!(fallbackOptional.isPresent() && (fallbackOptional.get().size() != 0 || Objects.equals(jigsawBlockFallback, Pools.EMPTY.location())))) {
                    YungsApiCommon.LOGGER.warn("Empty or nonexistent fallback pool: {}", jigsawBlockFallback);
                    continue;
                }

                // Adjustments for if the target block position is inside the current piece
                boolean isTargetInsideCurrentPiece = pieceBoundingBox.isInside(jigsawBlockTargetPos);
                MutableObject<BoxOctree> octreeToUse;
                if (isTargetInsideCurrentPiece) {
                    octreeToUse = parentOctree;
                    if (parentOctree.getValue() == null) {
                        parentOctree.setValue(new BoxOctree(AABB.of(pieceBoundingBox)));
                    }
                } else {
                    octreeToUse = boxOctree;
                }

                // Process the pool pieces, randomly choosing different pieces from the pool to spawn
                if (depth != this.maxDepth) {
                    StructurePoolElement generatedPiece = this.processList(
                            new ArrayList<>(((StructureTemplatePoolAccessor) poolOptional.get()).getRawTemplates()), useExpansionHack, jigsawBlockInfo, jigsawBlockTargetPos, pieceMinY, jigsawBlockPos, octreeToUse, piece, depth, levelHeightAccessor, randomState);
                    if (generatedPiece != null) continue; // Stop here since we've already generated the piece
                }

                // Process the fallback pieces in the event none of the pool pieces work
                this.processList(new ArrayList<>(((StructureTemplatePoolAccessor) fallbackOptional.get()).getRawTemplates()), useExpansionHack, jigsawBlockInfo, jigsawBlockTargetPos, pieceMinY, jigsawBlockPos, octreeToUse, piece, depth, levelHeightAccessor, randomState);
            }
        }

        /**
         * Helper function. Searches candidatePieces for a suitable piece to spawn.
         * All other params are intended to be passed directly from {@link Assembler#processPiece}
         *
         * @return The piece generated, or null if no suitable piece was found.
         */
        private StructurePoolElement processList(
                List<Pair<StructurePoolElement, Integer>> candidatePieces,
                boolean useExpansionHack,
                StructureTemplate.StructureBlockInfo jigsawBlock,
                BlockPos jigsawBlockTargetPos,
                int pieceMinY,
                BlockPos jigsawBlockPos,
                MutableObject<BoxOctree> boxOctree,
                PoolElementStructurePiece piece,
                int depth,
                LevelHeightAccessor levelHeightAccessor,
                RandomState randomState
        ) {
            boolean isPieceRigid = piece.getElement().getProjection() == StructureTemplatePool.Projection.RIGID;
            int jigsawBlockRelativeY = jigsawBlockPos.getY() - pieceMinY;
            int surfaceHeight = -1; // The y-coordinate of the surface. Only used if isPieceRigid is false.

            // Sum of weights in all pieces in the pool.
            // When choosing a piece, we will remove its weight from this sum.
            int totalWeightSum = candidatePieces.stream().mapToInt(Pair::getSecond).reduce(0, Integer::sum);

            while (candidatePieces.size() > 0 && totalWeightSum > 0) {
                Pair<StructurePoolElement, Integer> chosenPiecePair = null;

                // First, check for any priority pieces
                for (Pair<StructurePoolElement, Integer> candidatePiecePair : candidatePieces) {
                    StructurePoolElement candidatePiece = candidatePiecePair.getFirst();
                    if (candidatePiece instanceof YungJigsawSinglePoolElement yungJigsawPiece && yungJigsawPiece.isPriorityPiece()) {
                        chosenPiecePair = candidatePiecePair;
                        break;
                    }
                }

                // Randomly choose piece if priority piece wasn't selected
                if (chosenPiecePair == null) {
                    // Random weight used to choose random piece from the pool of candidates
                    int chosenWeight = rand.nextInt(totalWeightSum) + 1;

                    // Randomly choose a candidate piece
                    for (Pair<StructurePoolElement, Integer> candidate : candidatePieces) {
                        chosenWeight -= candidate.getSecond();
                        if (chosenWeight <= 0) {
                            chosenPiecePair = candidate;
                            break;
                        }
                    }
                }

                // Extract data from the chosen piece pair.
                StructurePoolElement chosenPiece = chosenPiecePair.getFirst();
                int chosenPieceWeight = chosenPiecePair.getSecond();

                // Abort if we reach an empty piece.
                // Not sure if aborting is necessary here, but this is vanilla behavior.
                if (chosenPiece == EmptyPoolElement.INSTANCE) {
                    return null;
                }

                // Validate to make sure we haven't reached the max number of instances of this piece, if applicable
                if (chosenPiece instanceof YungJigsawSinglePoolElement yungJigsawPiece && yungJigsawPiece.maxCount.isPresent()) {
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
                            candidatePieces.remove(chosenPiecePair);
                            continue;
                        }
                    }
                }

                // LEGACY - support for IMaxCountJigsawPiece
                if (chosenPiece instanceof IMaxCountJigsawPiece) {
                    String pieceName = ((IMaxCountJigsawPiece) chosenPiece).getName();
                    int maxCount = ((IMaxCountJigsawPiece) chosenPiece).getMaxCount();

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
                        totalWeightSum -= chosenPiecePair.getSecond();
                        candidatePieces.remove(chosenPiecePair);
                        continue;
                    }
                }

                // Validate piece depth, if applicable
                if (chosenPiece instanceof YungJigsawSinglePoolElement yungJigsawPiece && !yungJigsawPiece.isAtValidDepth(depth)) {
                    totalWeightSum -= chosenPieceWeight;
                    candidatePieces.remove(chosenPiecePair);
                    continue;
                }

                // Try different rotations to see which sides of the piece are fit to be the receiving end
                for (Rotation rotation : Rotation.getShuffled(this.rand)) {
                    List<StructureTemplate.StructureBlockInfo> candidateJigsawBlocks = chosenPiece.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, rotation, this.rand);

                    // Some sort of logic for setting the candidateHeightAdjustments var if useExpansionHack.
                    // Not sure on this - personally, I never enable useExpansionHack.
                    BoundingBox tempCandidateBoundingBox = chosenPiece.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, rotation);
                    int candidateHeightAdjustments = 0;
                    if (useExpansionHack && tempCandidateBoundingBox.getYSpan() <= 16) {
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
                        if (!JigsawBlock.canAttach(jigsawBlock, candidateJigsawBlock)) continue;

                        BlockPos candidateJigsawBlockPos = candidateJigsawBlock.pos;
                        BlockPos candidateJigsawBlockRelativePos = jigsawBlockTargetPos.subtract(candidateJigsawBlockPos);

                        // Get the rotated bounding box for the piece, offset by the relative position difference
                        BoundingBox rotatedCandidateBoundingBox = chosenPiece.getBoundingBox(this.structureTemplateManager, candidateJigsawBlockRelativePos, rotation);

                        // Determine if candidate is rigid
                        StructureTemplatePool.Projection candidatePlacementBehavior = chosenPiece.getProjection();
                        boolean isCandidateRigid = candidatePlacementBehavior == StructureTemplatePool.Projection.RIGID;

                        // Determine how much the candidate jigsaw block is off in the y direction.
                        // This will be needed to offset the candidate piece so that the jigsaw blocks line up properly.
                        int candidateJigsawBlockRelativeY = candidateJigsawBlockPos.getY();
                        int candidateJigsawYOffsetNeeded = jigsawBlockRelativeY - candidateJigsawBlockRelativeY + JigsawBlock.getFrontFacing(jigsawBlock.state).getStepY();

                        // Determine how much we need to offset the candidate piece itself in order to have the jigsaw blocks aligned.
                        // Depends on if the placement of both pieces is rigid or not
                        int adjustedCandidatePieceMinY;
                        if (isPieceRigid && isCandidateRigid) {
                            adjustedCandidatePieceMinY = pieceMinY + candidateJigsawYOffsetNeeded;
                        } else {
                            if (surfaceHeight == -1) {
                                surfaceHeight = this.chunkGenerator.getFirstFreeHeight(jigsawBlockPos.getX(), jigsawBlockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor, randomState);
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

                        if (chosenPiece instanceof YungJigsawSinglePoolElement yungJigsawPiece) {
                            pieceIgnoresBounds = yungJigsawPiece.ignoresBounds();
                        }

                        // Validate piece boundaries
                        if (!pieceIgnoresBounds) {
                            boolean pieceIntersectsExistingPieces = boxOctree.getValue().intersectsAnyBox(aabbDeflated);
                            boolean pieceIsContainedWithinStructureBoundaries = boxOctree.getValue().boundaryContains(aabbDeflated);
                            if (pieceIntersectsExistingPieces || !pieceIsContainedWithinStructureBoundaries) {
                                continue;
                            }
                        }

                        // Validate conditions for this piece, if applicable
                        if (chosenPiece instanceof YungJigsawSinglePoolElement yungJigsawPiece) {
                            ConditionContext ctx = new ConditionContext(adjustedCandidateBoundingBox.minY(), adjustedCandidateBoundingBox.maxY(), depth);
                            if (!yungJigsawPiece.passesConditions(ctx)) {
                                continue; // Abort this piece & rotation if it doesn't pass conditions check
                            }
                        }

                        // At this point we are locked in for adding this piece, so add its box to the structure's
                        boxOctree.getValue().addBox(aabb);

                        // Determine ground level delta for this new piece
                        int newPieceGroundLevelDelta = piece.getGroundLevelDelta();
                        int groundLevelDelta;
                        if (isCandidateRigid) {
                            groundLevelDelta = newPieceGroundLevelDelta - candidateJigsawYOffsetNeeded;
                        } else {
                            groundLevelDelta = chosenPiece.getGroundLevelDelta();
                        }

                        // Create new piece
                        PoolElementStructurePiece newPiece = new PoolElementStructurePiece(
                                this.structureTemplateManager,
                                chosenPiece,
                                adjustedCandidateJigsawBlockRelativePos,
                                groundLevelDelta,
                                rotation,
                                adjustedCandidateBoundingBox
                        );

                        // Determine actual y-value for the new jigsaw block
                        int candidateJigsawBlockY;
                        if (isPieceRigid) {
                            candidateJigsawBlockY = pieceMinY + jigsawBlockRelativeY;
                        } else if (isCandidateRigid) {
                            candidateJigsawBlockY = adjustedCandidatePieceMinY + candidateJigsawBlockRelativeY;
                        } else {
                            if (surfaceHeight == -1) {
                                surfaceHeight = this.chunkGenerator.getFirstFreeHeight(jigsawBlockPos.getX(), jigsawBlockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor, randomState);
                            }

                            candidateJigsawBlockY = surfaceHeight + candidateJigsawYOffsetNeeded / 2;
                        }

                        // Add the junction to the existing piece
                        piece.addJunction(
                                new JigsawJunction(
                                        jigsawBlockTargetPos.getX(),
                                        candidateJigsawBlockY - jigsawBlockRelativeY + newPieceGroundLevelDelta,
                                        jigsawBlockTargetPos.getZ(),
                                        candidateJigsawYOffsetNeeded,
                                        candidatePlacementBehavior)
                        );

                        // Add the junction to the new piece
                        newPiece.addJunction(
                                new JigsawJunction(
                                        jigsawBlockPos.getX(),
                                        candidateJigsawBlockY - candidateJigsawBlockRelativeY + groundLevelDelta,
                                        jigsawBlockPos.getZ(),
                                        -candidateJigsawYOffsetNeeded,
                                        piece.getElement().getProjection())
                        );

                        // Add the piece
                        this.pieces.add(newPiece);
                        if (depth + 1 <= this.maxDepth) {
                            this.pieceQueue.addLast(new PieceEntry(newPiece, boxOctree, depth + 1));
                        }

                        // Update piece count, if applicable
                        if (chosenPiece instanceof YungJigsawSinglePoolElement yungJigsawPiece && yungJigsawPiece.maxCount.isPresent()) {
                            // Max count pieces must also be named.
                            // The following condition will never be met if users correctly configure their template pools.
                            if (yungJigsawPiece.name.isEmpty()) {
                                // If name is missing, ignore max count for this piece. We've already logged an error for it earlier.
                                return chosenPiece;
                            }

                            String pieceName = yungJigsawPiece.name.get();
                            this.pieceCounts.put(pieceName, this.pieceCounts.getOrDefault(pieceName, 0) + 1);
                        }

                        // LEGACY - support for IMaxCountJigsawPiece
                        if (chosenPiece instanceof IMaxCountJigsawPiece) {
                            String pieceName = ((IMaxCountJigsawPiece) chosenPiece).getName();
                            this.pieceCounts.put(pieceName, this.pieceCounts.getOrDefault(pieceName, 0) + 1);
                        }

                        return chosenPiece;
                    }
                }
                totalWeightSum -= chosenPieceWeight;
                candidatePieces.remove(chosenPiecePair);
            }
            return null;
        }
    }

    public record PieceEntry(PoolElementStructurePiece piece, MutableObject<BoxOctree> boxOctreeMutableObject, int depth) {
    }
}
