package com.yungnickyoung.minecraft.yungsapi.world.jigsaw;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.yungsapi.YungsApi;
import com.yungnickyoung.minecraft.yungsapi.api.YungJigsawConfig;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.BoundingBoxAccessor;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.StructureTemplatePoolAccessor;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.IMaxCountJigsawPiece;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.*;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;
import java.util.function.Predicate;

public class JigsawManager {
    public static Optional<PieceGenerator<YungJigsawConfig>> assembleJigsawStructure(
        PieceGeneratorSupplier.Context<YungJigsawConfig> jigsawContext,
        JigsawPlacement.PieceFactory pieceFactory,
        BlockPos startPos,
        boolean doBoundaryAdjustments,
        boolean useHeightmap,
        int structureBoundingBoxRadius
    ) {
        // Extract data from context
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenRandom.setLargeFeatureSeed(jigsawContext.seed(), jigsawContext.chunkPos().x, jigsawContext.chunkPos().z);
        RegistryAccess registryAccess = jigsawContext.registryAccess();
        YungJigsawConfig config = jigsawContext.config();
        ChunkGenerator chunkGenerator = jigsawContext.chunkGenerator();
        StructureManager structureManager = jigsawContext.structureManager();
        LevelHeightAccessor levelHeightAccessor = jigsawContext.heightAccessor();
        Predicate<Biome> predicate = jigsawContext.validBiome();

        StructureFeature.bootstrap(); // Ensures static members are all loaded

        // Get jigsaw pool registry
        Registry<StructureTemplatePool> registry = registryAccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);

        // Get a random orientation for starting piece
        Rotation rotation = Rotation.getRandom(worldgenRandom);

        // Get starting pool
        StructureTemplatePool structureTemplatePool = registry.get(config.getStartPool());

        // Grab a random starting piece from the start pool. This is just the piece design itself, without rotation or position information.
        // Think of it as a blueprint.
        StructurePoolElement startPieceBlueprint = structureTemplatePool.getRandomTemplate(worldgenRandom);
        if (startPieceBlueprint == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        }

        // Instantiate a piece using the "blueprint" we just got.
        PoolElementStructurePiece startPiece = pieceFactory.create(
            structureManager,
            startPieceBlueprint,
            startPos,
            startPieceBlueprint.getGroundLevelDelta(),
            rotation,
            startPieceBlueprint.getBoundingBox(structureManager, startPos, rotation)
        );

        // Store center position of starting piece's bounding box
        BoundingBox pieceBoundingBox = startPiece.getBoundingBox();
        int pieceCenterX = (pieceBoundingBox.maxX() + pieceBoundingBox.minX()) / 2;
        int pieceCenterZ = (pieceBoundingBox.maxZ() + pieceBoundingBox.minZ()) / 2;
        int pieceCenterY = useHeightmap
            ? startPos.getY() + chunkGenerator.getFirstFreeHeight(pieceCenterX, pieceCenterZ, Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor)
            : startPos.getY();
        if (!predicate.test(chunkGenerator.getNoiseBiome(QuartPos.fromBlock(pieceCenterX), QuartPos.fromBlock(pieceCenterY), QuartPos.fromBlock(pieceCenterZ)))) {
            return Optional.empty();
        }
        int yAdjustment = pieceBoundingBox.minY() + startPiece.getGroundLevelDelta(); // groundLevelDelta seems to always be 1. Not sure what the point of this is.
        startPiece.move(0, pieceCenterY - yAdjustment, 0); // Ends up always offseting the piece by y = -1?

        return Optional.of((structurePiecesBuilder, context) -> {
            ArrayList<PoolElementStructurePiece> pieces = Lists.newArrayList();
            pieces.add(startPiece);
            if (config.getMaxDepth() <= 0) { // Realistically this should never be true. Why make a jigsaw config with a non-positive size?
                return;
            }

            // We expand the bounding box of the start piece each direction.
            // Make sure the supplied radius is large enough to cover the size of your entire piece.
            AABB aABB = new AABB(
                    pieceCenterX - structureBoundingBoxRadius, pieceCenterY - structureBoundingBoxRadius, pieceCenterZ - structureBoundingBoxRadius,
                    pieceCenterX + structureBoundingBoxRadius + 1, pieceCenterY + structureBoundingBoxRadius + 1, pieceCenterZ + structureBoundingBoxRadius + 1);
            Placer placer = new Placer(registry, config.getMaxDepth(), pieceFactory, chunkGenerator, structureManager, pieces, worldgenRandom);
            PieceState startPieceEntry = new PieceState(
                    startPiece,
                    new MutableObject<>(
                            Shapes.join(
                                    Shapes.create(aABB),
                                    Shapes.create(AABB.of(pieceBoundingBox)),
                                    BooleanOp.ONLY_FIRST
                            )
                    ),
                    0
            );

            // Add the start piece to the placer
            placer.placing.addLast(startPieceEntry);

            // Place the structure
            while (!placer.placing.isEmpty()) {
                PieceState entry = placer.placing.removeFirst();
                placer.processPiece(entry.piece, entry.free, entry.depth, doBoundaryAdjustments, levelHeightAccessor);
            }
            pieces.forEach(structurePiecesBuilder::addPiece);
        });
    }

    public static Optional<PieceGenerator<YungJigsawConfig>> assembleJigsawStructure(
            PieceGeneratorSupplier.Context<YungJigsawConfig> jigsawContext,
            JigsawPlacement.PieceFactory pieceFactory,
            BlockPos startPos,
            boolean doBoundaryAdjustments,
            boolean useHeightmap)
    {
        return assembleJigsawStructure(jigsawContext, pieceFactory, startPos, doBoundaryAdjustments, useHeightmap, 80);
    }

    public static final class Placer {
        // Vanilla
        private final Registry<StructureTemplatePool> patternRegistry;
        private final int maxDepth;
        private final JigsawPlacement.PieceFactory pieceFactory;
        private final ChunkGenerator chunkGenerator;
        private final StructureManager structureManager;
        private final List<? super PoolElementStructurePiece> pieces;
        private final Random rand;
        public final Deque<PieceState> placing = Queues.newArrayDeque();

        // Additional behavior
        private final Map<String, Integer> pieceCounts;
        private final Map<String, Integer> maxPieceCounts;
        private final int maxY;

        public Placer(
            Registry<StructureTemplatePool> patternRegistry,
            int maxDepth,
            JigsawPlacement.PieceFactory pieceFactory,
            ChunkGenerator chunkGenerator,
            StructureManager structureManager,
            List<? super PoolElementStructurePiece> pieces,
            Random rand
        ) {
            this.patternRegistry = patternRegistry;
            this.maxDepth = maxDepth;
            this.pieceFactory = pieceFactory;
            this.chunkGenerator = chunkGenerator;
            this.structureManager = structureManager;
            this.pieces = pieces;
            this.rand = rand;
            this.pieceCounts = new HashMap<>();
            this.maxPieceCounts = new HashMap<>();
            this.maxY = 255;
        }

        public void processPiece(
                PoolElementStructurePiece piece,
                MutableObject<VoxelShape> voxelShape,
                int depth,
                boolean doBoundaryAdjustments,
                LevelHeightAccessor levelHeightAccessor
        ) {
            // Collect data from params regarding piece to process
            StructurePoolElement pieceBlueprint = piece.getElement();
            BlockPos piecePos = piece.getPosition();
            Rotation pieceRotation = piece.getRotation();
            BoundingBox pieceBoundingBox = piece.getBoundingBox();
            int pieceMinY = pieceBoundingBox.minY();

            // I think this is a holder variable for reuse
            MutableObject<VoxelShape> tempNewPieceVoxelShape = new MutableObject<>();

            // Get list of all jigsaw blocks in this piece
            List<StructureTemplate.StructureBlockInfo> pieceJigsawBlocks = pieceBlueprint.getShuffledJigsawBlocks(this.structureManager, piecePos, pieceRotation, this.rand);

            for (StructureTemplate.StructureBlockInfo jigsawBlock : pieceJigsawBlocks) {
                // Gather jigsaw block information
                Direction direction = JigsawBlock.getFrontFacing(jigsawBlock.state);
                BlockPos jigsawBlockPos = jigsawBlock.pos;
                BlockPos jigsawBlockTargetPos = jigsawBlockPos.relative(direction);

                // Get the jigsaw block's piece pool
                ResourceLocation jigsawBlockPoolId = new ResourceLocation(jigsawBlock.nbt.getString("pool"));
                Optional<StructureTemplatePool> poolOptional = this.patternRegistry.getOptional(jigsawBlockPoolId);

                // Only continue if we are using the jigsaw pattern registry and if it is not empty
                if (!(poolOptional.isPresent() && (poolOptional.get().size() != 0 || Objects.equals(jigsawBlockPoolId, Pools.EMPTY.location())))) {
                    YungsApi.LOGGER.warn("Empty or nonexistent pool: {}", jigsawBlockPoolId);
                    continue;
                }

                // Get the jigsaw block's fallback pool (which is a part of the pool's JSON)
                ResourceLocation jigsawBlockFallback = poolOptional.get().getFallback();
                Optional<StructureTemplatePool> fallbackOptional = this.patternRegistry.getOptional(jigsawBlockFallback);

                // Only continue if the fallback pool is present and valid
                if (!(fallbackOptional.isPresent() && (fallbackOptional.get().size() != 0 || Objects.equals(jigsawBlockFallback, Pools.EMPTY.location())))) {
                    YungsApi.LOGGER.warn("Empty or nonexistent fallback pool: {}", jigsawBlockFallback);
                    continue;
                }

                // Adjustments for if the target block position is inside the current piece
                boolean isTargetInsideCurrentPiece = pieceBoundingBox.isInside(jigsawBlockTargetPos);
                MutableObject<VoxelShape> pieceVoxelShape;
                if (isTargetInsideCurrentPiece) {
                    pieceVoxelShape = tempNewPieceVoxelShape;
                    if (tempNewPieceVoxelShape.getValue() == null) {
                        tempNewPieceVoxelShape.setValue(Shapes.create(AABB.of(pieceBoundingBox)));
                    }
                } else {
                    pieceVoxelShape = voxelShape;
                }

                // Process the pool pieces, randomly choosing different pieces from the pool to spawn
                if (depth != this.maxDepth) {
                    StructurePoolElement generatedPiece = this.processList(new ArrayList<>(((StructureTemplatePoolAccessor)poolOptional.get()).getRawTemplates()), doBoundaryAdjustments, jigsawBlock, jigsawBlockTargetPos, pieceMinY, jigsawBlockPos, pieceVoxelShape, piece, depth, levelHeightAccessor);
                    if (generatedPiece != null) continue; // Stop here since we've already generated the piece
                }

                // Process the fallback pieces in the event none of the pool pieces work
                this.processList(new ArrayList<>(((StructureTemplatePoolAccessor)fallbackOptional.get()).getRawTemplates()), doBoundaryAdjustments, jigsawBlock, jigsawBlockTargetPos, pieceMinY, jigsawBlockPos, pieceVoxelShape, piece, depth, levelHeightAccessor);
            }
        }

        /**
         * Helper function. Searches candidatePieces for a suitable piece to spawn.
         * All other params are intended to be passed directly from {@link Placer#processPiece}
         * @return The piece generated, or null if no suitable piece was found.
         */
        private StructurePoolElement processList(
            List<Pair<StructurePoolElement, Integer>> candidatePieces,
            boolean doBoundaryAdjustments,
            StructureTemplate.StructureBlockInfo jigsawBlock,
            BlockPos jigsawBlockTargetPos,
            int pieceMinY,
            BlockPos jigsawBlockPos,
            MutableObject<VoxelShape> pieceVoxelShape,
            PoolElementStructurePiece piece,
            int depth,
            LevelHeightAccessor levelHeightAccessor
        ) {
            StructureTemplatePool.Projection piecePlacementBehavior = piece.getElement().getProjection();
            boolean isPieceRigid = piecePlacementBehavior == StructureTemplatePool.Projection.RIGID;
            int jigsawBlockRelativeY = jigsawBlockPos.getY() - pieceMinY;
            int surfaceHeight = -1; // The y-coordinate of the surface. Only used if isPieceRigid is false.

            // Sum of weights of all pieces in the pool
            int totalWeightSum = candidatePieces.stream().mapToInt(Pair::getSecond).reduce(0, Integer::sum);

            while (candidatePieces.size() > 0 && totalWeightSum > 0) {
                Pair<StructurePoolElement, Integer> chosenPiecePair = null;

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

                StructurePoolElement candidatePiece = chosenPiecePair.getFirst();

                // Abort if we reach an empty piece.
                // Not sure if aborting is necessary here, but this is vanilla behavior.
                if (candidatePiece == EmptyPoolElement.INSTANCE) {
                    return null;
                }

                // Before performing any logic, check to ensure we haven't reached the max number of instances of this piece.
                // This is my own additional feature - vanilla does not offer this behavior.
                if (candidatePiece instanceof IMaxCountJigsawPiece) {
                    String pieceName = ((IMaxCountJigsawPiece) candidatePiece).getName();
                    int maxCount = ((IMaxCountJigsawPiece) candidatePiece).getMaxCount();

                    // Check if max count of this piece does not match stored max count for this name.
                    // This can happen when the same name is reused across pools, but the max count values are different.
                    if (this.maxPieceCounts.containsKey(pieceName) && this.maxPieceCounts.get(pieceName) != maxCount) {
                        YungsApi.LOGGER.error("YUNG Jigsaw piece with name {} and max_count {} does not match stored max_count of {}!", pieceName, maxCount, this.maxPieceCounts.get(pieceName));
                        YungsApi.LOGGER.error("This can happen when multiple pieces across pools use the same name, but have different max_count values.");
                        YungsApi.LOGGER.error("Please change these max_count values to match. Using max_count={} for now...", maxCount);
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

                // Try different rotations to see which sides of the piece are fit to be the receiving end
                for (Rotation rotation : Rotation.getShuffled(this.rand)) {
                    List<StructureTemplate.StructureBlockInfo> candidateJigsawBlocks = candidatePiece.getShuffledJigsawBlocks(this.structureManager, BlockPos.ZERO, rotation, this.rand);
                    BoundingBox tempCandidateBoundingBox = candidatePiece.getBoundingBox(this.structureManager, BlockPos.ZERO, rotation);

                    // Some sort of logic for setting the candidateHeightAdjustments var if doBoundaryAdjustments.
                    // Not sure on this - personally, I never enable doBoundaryAdjustments.
                    int candidateHeightAdjustments;
                    if (doBoundaryAdjustments && tempCandidateBoundingBox.getYSpan() <= 16) {
                        candidateHeightAdjustments = candidateJigsawBlocks.stream().mapToInt((pieceCandidateJigsawBlock) -> {
                            if (!tempCandidateBoundingBox.isInside(pieceCandidateJigsawBlock.pos.relative(JigsawBlock.getFrontFacing(pieceCandidateJigsawBlock.state)))) {
                                return 0;
                            }
                            ResourceLocation candidateTargetPool = new ResourceLocation(pieceCandidateJigsawBlock.nbt.getString("pool"));
                            Optional<StructureTemplatePool> candidateTargetPoolOptional = this.patternRegistry.getOptional(candidateTargetPool);
                            Optional<StructureTemplatePool> candidateTargetFallbackOptional = candidateTargetPoolOptional.flatMap((StructureTemplatePool) -> this.patternRegistry.getOptional(StructureTemplatePool.getFallback()));
                            int tallestCandidateTargetPoolPieceHeight = candidateTargetPoolOptional.map((structureTemplatePool) -> structureTemplatePool.getMaxSize(this.structureManager)).orElse(0);
                            int tallestCandidateTargetFallbackPieceHeight = candidateTargetFallbackOptional.map((structureTemplatePool) -> structureTemplatePool.getMaxSize(this.structureManager)).orElse(0);
                            return Math.max(tallestCandidateTargetPoolPieceHeight, tallestCandidateTargetFallbackPieceHeight);
                        }).max().orElse(0);
                    } else {
                        candidateHeightAdjustments = 0;
                    }

                    // Check for each of the candidate's jigsaw blocks for a match
                    for (StructureTemplate.StructureBlockInfo candidateJigsawBlock : candidateJigsawBlocks) {
                        if (!JigsawBlock.canAttach(jigsawBlock, candidateJigsawBlock)) continue;

                        BlockPos candidateJigsawBlockPos = candidateJigsawBlock.pos;
                        BlockPos candidateJigsawBlockRelativePos = jigsawBlockTargetPos.subtract(candidateJigsawBlockPos);

                        // Get the bounding box for the piece, offset by the relative position difference
                        BoundingBox candidateBoundingBox = candidatePiece.getBoundingBox(this.structureManager, candidateJigsawBlockRelativePos, rotation);

                        // Determine if candidate is rigid
                        StructureTemplatePool.Projection candidatePlacementBehavior = candidatePiece.getProjection();
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
                                surfaceHeight = this.chunkGenerator.getFirstFreeHeight(jigsawBlockPos.getX(), jigsawBlockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor);
                            }

                            adjustedCandidatePieceMinY = surfaceHeight - candidateJigsawBlockRelativeY;
                        }
                        int candidatePieceYOffsetNeeded = adjustedCandidatePieceMinY - candidateBoundingBox.minY();

                        // Offset the candidate's bounding box by the necessary amount
                        BoundingBox adjustedCandidateBoundingBox = candidateBoundingBox.moved(0, candidatePieceYOffsetNeeded, 0);

                        // Add this offset to the relative jigsaw block position as well
                        BlockPos adjustedCandidateJigsawBlockRelativePos = candidateJigsawBlockRelativePos.offset(0, candidatePieceYOffsetNeeded, 0);

                        // Final adjustments to the bounding box.
                        if (candidateHeightAdjustments > 0) {
                            int k2 = Math.max(candidateHeightAdjustments + 1, adjustedCandidateBoundingBox.maxY() - adjustedCandidateBoundingBox.minY());
                            ((BoundingBoxAccessor)adjustedCandidateBoundingBox).setMaxY(adjustedCandidateBoundingBox.minY() + k2);
                        }

                        // Prevent pieces from spawning above max Y
                        if (adjustedCandidateBoundingBox.maxY() > this.maxY) {
                            continue;
                        }

                        // Some sort of final boundary check before adding the new piece.
                        // Not sure why the candidate box is shrunk by 0.25.
                        if (Shapes.joinIsNotEmpty(pieceVoxelShape.getValue(), Shapes.create(AABB.of(adjustedCandidateBoundingBox).deflate(0.25D)), BooleanOp.ONLY_SECOND)) {
                            continue;
                        }
                        pieceVoxelShape.setValue(Shapes.joinUnoptimized(pieceVoxelShape.getValue(), Shapes.create(AABB.of(adjustedCandidateBoundingBox)), BooleanOp.ONLY_FIRST));

                        // Determine ground level delta for this new piece
                        int newPieceGroundLevelDelta = piece.getGroundLevelDelta();
                        int groundLevelDelta;
                        if (isCandidateRigid) {
                            groundLevelDelta = newPieceGroundLevelDelta - candidateJigsawYOffsetNeeded;
                        } else {
                            groundLevelDelta = candidatePiece.getGroundLevelDelta();
                        }

                        // Create new piece
                        PoolElementStructurePiece newPiece = pieceFactory.create(
                            this.structureManager,
                            candidatePiece,
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
                                surfaceHeight = this.chunkGenerator.getFirstFreeHeight(jigsawBlockPos.getX(), jigsawBlockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor);
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
                                piecePlacementBehavior)
                        );

                        // Add the piece
                        this.pieces.add(newPiece);
                        if (depth + 1 <= this.maxDepth) {
                            this.placing.addLast(new PieceState(newPiece, pieceVoxelShape, depth + 1));
                        }

                        // Update piece count, if piece is of max count type
                        if (candidatePiece instanceof IMaxCountJigsawPiece) {
                            String pieceName = ((IMaxCountJigsawPiece) candidatePiece).getName();
                            this.pieceCounts.put(pieceName, this.pieceCounts.getOrDefault(pieceName, 0) + 1);
                        }
                        return candidatePiece;
                    }
                }
                totalWeightSum -= chosenPiecePair.getSecond();
                candidatePieces.remove(chosenPiecePair);
            }
            return null;
        }
    }

    public static final class PieceState {
        public final PoolElementStructurePiece piece;
        public final MutableObject<VoxelShape> free;
        public final int depth;

        public PieceState(PoolElementStructurePiece piece, MutableObject<VoxelShape> voxelShape, int depth) {
            this.piece = piece;
            this.free = voxelShape;
            this.depth = depth;
        }
    }
}
