package com.yungnickyoung.minecraft.yungsapi.world.jigsaw;


import com.google.common.collect.Queues;
import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.yungsapi.YungsApi;
import com.yungnickyoung.minecraft.yungsapi.api.YungJigsawConfig;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.IMaxCountJigsawPiece;
import net.minecraft.block.JigsawBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.jigsaw.*;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;

/**
 * Reimplementation of {@link net.minecraft.world.gen.feature.jigsaw.JigsawManager} with additional options.
 *
 * Do not use this class directly. Make calls to {@link com.yungnickyoung.minecraft.yungsapi.api.YungJigsawManager} instead.
 */
public class JigsawManager {
    public static void assembleJigsawStructure(
        DynamicRegistries dynamicRegistryManager,
        YungJigsawConfig jigsawConfig,
        ChunkGenerator chunkGenerator,
        TemplateManager templateManager,
        BlockPos startPos,
        List<? super AbstractVillagePiece> components,
        Random random,
        boolean doBoundaryAdjustments,
        boolean useHeightmap
    ) {
        // Get jigsaw pool registry
        MutableRegistry<JigsawPattern> jigsawPoolRegistry = dynamicRegistryManager.getRegistry(Registry.JIGSAW_POOL_KEY);

        // Get a random orientation for the starting piece
        Rotation rotation = Rotation.randomRotation(random);

        // Get starting pool
        JigsawPattern startPool = jigsawConfig.getStartPoolSupplier().get();

        // Grab a random starting piece from the start pool. This is just the piece design itself, without rotation or position information.
        // Think of it as a blueprint.
        JigsawPiece startPieceBlueprint = startPool.getRandomPiece(random);

        // Instantiate a piece using the "blueprint" we just got.
        AbstractVillagePiece startPiece = new AbstractVillagePiece(
            templateManager,
            startPieceBlueprint,
            startPos,
            startPieceBlueprint.getGroundLevelDelta(),
            rotation,
            startPieceBlueprint.getBoundingBox(templateManager, startPos, rotation)
        );

        // Store center position of starting piece's bounding box
        MutableBoundingBox pieceBoundingBox = startPiece.getBoundingBox();
        int pieceCenterX = (pieceBoundingBox.maxX + pieceBoundingBox.minX) / 2;
        int pieceCenterZ = (pieceBoundingBox.maxZ + pieceBoundingBox.minZ) / 2;
        int pieceCenterY = useHeightmap
            ? startPos.getY() + chunkGenerator.getNoiseHeight(pieceCenterX, pieceCenterZ, Heightmap.Type.WORLD_SURFACE_WG)
            : startPos.getY();

        int yAdjustment = pieceBoundingBox.minY + startPiece.getGroundLevelDelta(); // groundLevelDelta seems to always be 1. Not sure what the point of this is.
        startPiece.offset(0, pieceCenterY - yAdjustment, 0); // Ends up always offseting the piece by y = -1?

        components.add(startPiece); // Add start piece to list of pieces

        if (jigsawConfig.getMaxChainPieceLength() > 0) { // Realistically this should always be true. Why make a jigsaw config with a non-positive size?
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pieceCenterX - 80, pieceCenterY - 80, pieceCenterZ - 80, pieceCenterX + 80 + 1, pieceCenterY + 80 + 1, pieceCenterZ + 80 + 1);
            Assembler assembler = new Assembler(jigsawPoolRegistry, jigsawConfig.getMaxChainPieceLength(), chunkGenerator, templateManager, components, random);
            Entry startPieceEntry = new Entry(
                startPiece,
                new MutableObject<>(
                    VoxelShapes.combineAndSimplify(
                        VoxelShapes.create(axisAlignedBB),
                        VoxelShapes.create(AxisAlignedBB.toImmutable(pieceBoundingBox)),
                        IBooleanFunction.ONLY_FIRST
                    )
                ),
                pieceCenterY + 80,
                0
            );
            assembler.availablePieces.addLast(startPieceEntry);

            while (!assembler.availablePieces.isEmpty()) {
                Entry entry = assembler.availablePieces.removeFirst();
                assembler.processPiece(entry.villagePiece, entry.voxelShape, entry.boundsTop, entry.depth, doBoundaryAdjustments);
            }
        }
    }

    public static final class Assembler {
        private final Registry<JigsawPattern> patternRegistry;
        private final int maxDepth;
        private final ChunkGenerator chunkGenerator;
        private final TemplateManager templateManager;
        private final List<? super AbstractVillagePiece> structurePieces;
        private final Random rand;
        public final Deque<Entry> availablePieces = Queues.newArrayDeque();
        private final Map<String, Integer> pieceCounts;
        private final Map<String, Integer> maxPieceCounts;
        private final int maxY;

        public Assembler(Registry<JigsawPattern> patternRegistry, int maxDepth, ChunkGenerator chunkGenerator, TemplateManager templateManager, List<? super AbstractVillagePiece> structurePieces, Random rand) {
            this.patternRegistry = patternRegistry;
            this.maxDepth = maxDepth;
            this.chunkGenerator = chunkGenerator;
            this.templateManager = templateManager;
            this.structurePieces = structurePieces;
            this.rand = rand;
            this.pieceCounts = new HashMap<>();
            this.maxPieceCounts = new HashMap<>();
            this.maxY = 255;
        }

        public void processPiece(AbstractVillagePiece piece, MutableObject<VoxelShape> voxelShape, int boundsTop, int depth, boolean doBoundaryAdjustments) {
            // Collect data from params regarding piece to process
            JigsawPiece pieceBlueprint = piece.getJigsawPiece();
            BlockPos piecePos = piece.getPos();
            Rotation pieceRotation = piece.getRotation();
            MutableBoundingBox pieceBoundingBox = piece.getBoundingBox();
            int pieceMinY = pieceBoundingBox.minY;

            // I think this is a holder variable for reuse
            MutableObject<VoxelShape> tempNewPieceVoxelShape = new MutableObject<>();

            // Get list of all jigsaw blocks in this piece
            List<Template.BlockInfo> pieceJigsawBlocks = pieceBlueprint.getJigsawBlocks(this.templateManager, piecePos, pieceRotation, this.rand);

            for (Template.BlockInfo jigsawBlock : pieceJigsawBlocks) {
                // Gather jigsaw block information
                Direction direction = JigsawBlock.getConnectingDirection(jigsawBlock.state);
                BlockPos jigsawBlockPos = jigsawBlock.pos;
                BlockPos jigsawBlockTargetPos = jigsawBlockPos.offset(direction);

                // Get the jigsaw block's piece pool
                ResourceLocation jigsawBlockPool = new ResourceLocation(jigsawBlock.nbt.getString("pool"));
                Optional<JigsawPattern> poolOptional = this.patternRegistry.getOptional(jigsawBlockPool);

                // Only continue if we are using the jigsaw pattern registry and if it is not empty
                if (!(poolOptional.isPresent() && (poolOptional.get().getNumberOfPieces() != 0 || Objects.equals(jigsawBlockPool, JigsawPatternRegistry.field_244091_a.getLocation())))) {
                    YungsApi.LOGGER.warn("Empty or nonexistent pool: {}", jigsawBlockPool);
                    continue;
                }

                // Get the jigsaw block's fallback pool (which is a part of the pool's JSON)
                ResourceLocation jigsawBlockFallback = poolOptional.get().getFallback();
                Optional<JigsawPattern> fallbackOptional = this.patternRegistry.getOptional(jigsawBlockFallback);

                // Only continue if the fallback pool is present and valid
                if (!(fallbackOptional.isPresent() && (fallbackOptional.get().getNumberOfPieces() != 0 || Objects.equals(jigsawBlockFallback, JigsawPatternRegistry.field_244091_a.getLocation())))) {
                    YungsApi.LOGGER.warn("Empty or nonexistent fallback pool: {}", jigsawBlockFallback);
                    continue;
                }

                // Adjustments for if the target block position is inside the current piece
                boolean isTargetInsideCurrentPiece = pieceBoundingBox.isVecInside(jigsawBlockTargetPos);
                MutableObject<VoxelShape> pieceVoxelShape;
                int targetPieceBoundsTop;
                if (isTargetInsideCurrentPiece) {
                    pieceVoxelShape = tempNewPieceVoxelShape;
                    targetPieceBoundsTop = pieceMinY;
                    if (tempNewPieceVoxelShape.getValue() == null) {
                        tempNewPieceVoxelShape.setValue(VoxelShapes.create(AxisAlignedBB.toImmutable(pieceBoundingBox)));
                    }
                } else {
                    pieceVoxelShape = voxelShape;
                    targetPieceBoundsTop = boundsTop;
                }

                // Process the pool pieces, randomly choosing different pieces from the pool to spawn
                if (depth != this.maxDepth) {
                    JigsawPiece generatedPiece = this.processList(new ArrayList<>(poolOptional.get().rawTemplates), doBoundaryAdjustments, jigsawBlock, jigsawBlockTargetPos, pieceMinY, jigsawBlockPos, pieceVoxelShape, piece, depth, targetPieceBoundsTop);
                    if (generatedPiece != null) continue; // Stop here since we've already generated the piece
                }

                // Process the fallback pieces in the event none of the pool pieces work
                this.processList(new ArrayList<>(fallbackOptional.get().rawTemplates), doBoundaryAdjustments, jigsawBlock, jigsawBlockTargetPos, pieceMinY, jigsawBlockPos, pieceVoxelShape, piece, depth, targetPieceBoundsTop);
            }
        }

        /**
         * Helper function. Searches candidatePieces for a suitable piece to spawn.
         * All other params are intended to be passed directly from {@link Assembler#processPiece}
         * @return The piece generated, or null if no suitable pieces were found.
         */
        private JigsawPiece processList(
            List<Pair<JigsawPiece, Integer>> candidatePieces,
            boolean doBoundaryAdjustments,
            Template.BlockInfo jigsawBlock,
            BlockPos jigsawBlockTargetPos,
            int pieceMinY,
            BlockPos jigsawBlockPos,
            MutableObject<VoxelShape> pieceVoxelShape,
            AbstractVillagePiece piece,
            int depth,
            int targetPieceBoundsTop
        ) {
            JigsawPattern.PlacementBehaviour piecePlacementBehavior = piece.getJigsawPiece().getPlacementBehaviour();
            boolean isPieceRigid = piecePlacementBehavior == JigsawPattern.PlacementBehaviour.RIGID;
            int jigsawBlockRelativeY = jigsawBlockPos.getY() - pieceMinY;
            int surfaceHeight = -1; // The y-coordinate of the surface. Only used if isPieceRigid is false.

            // Sum of weights of all pieces in the pool
            int totalWeightSum = candidatePieces.stream().mapToInt(Pair::getSecond).reduce(0, Integer::sum);

            while (candidatePieces.size() > 0) {
                Pair<JigsawPiece, Integer> chosenPiecePair = null;

                // Choose piece if portal room wasn't selected
                int chosenWeight = rand.nextInt(totalWeightSum) + 1;

                for (Pair<JigsawPiece, Integer> candidate : candidatePieces) {
                    chosenWeight -= candidate.getSecond();
                    if (chosenWeight <= 0) {
                        chosenPiecePair = candidate;
                        break;
                    }
                }

                JigsawPiece candidatePiece = chosenPiecePair.getFirst();

                // Abort if we reach an empty piece.
                // Not sure if aborting is necessary here, but this is vanilla behavior.
                if (candidatePiece == EmptyJigsawPiece.INSTANCE) {
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
                for (Rotation rotation : Rotation.shuffledRotations(this.rand)) {
                    List<Template.BlockInfo> candidateJigsawBlocks = candidatePiece.getJigsawBlocks(this.templateManager, BlockPos.ZERO, rotation, this.rand);
                    MutableBoundingBox tempCandidateBoundingBox = candidatePiece.getBoundingBox(this.templateManager, BlockPos.ZERO, rotation);

                    // Some sort of logic for setting the candidateHeightAdjustments var if doBoundaryAdjustments.
                    // Not sure on this - personally, I never enable doBoundaryAdjustments.
                    int candidateHeightAdjustments;
                    if (doBoundaryAdjustments && tempCandidateBoundingBox.getYSize() <= 16) {
                        candidateHeightAdjustments = candidateJigsawBlocks.stream().mapToInt((pieceCandidateJigsawBlock) -> {
                            if (!tempCandidateBoundingBox.isVecInside(pieceCandidateJigsawBlock.pos.offset(JigsawBlock.getConnectingDirection(pieceCandidateJigsawBlock.state)))) {
                                return 0;
                            } else {
                                ResourceLocation candidateTargetPool = new ResourceLocation(pieceCandidateJigsawBlock.nbt.getString("pool"));
                                Optional<JigsawPattern> candidateTargetPoolOptional = this.patternRegistry.getOptional(candidateTargetPool);
                                Optional<JigsawPattern> candidateTargetFallbackOptional = candidateTargetPoolOptional.flatMap((p_242843_1_) -> this.patternRegistry.getOptional(p_242843_1_.getFallback()));
                                int tallestCandidateTargetPoolPieceHeight = candidateTargetPoolOptional.map((p_242842_1_) -> p_242842_1_.getMaxSize(this.templateManager)).orElse(0);
                                int tallestCandidateTargetFallbackPieceHeight = candidateTargetFallbackOptional.map((p_242840_1_) -> p_242840_1_.getMaxSize(this.templateManager)).orElse(0);
                                return Math.max(tallestCandidateTargetPoolPieceHeight, tallestCandidateTargetFallbackPieceHeight);
                            }
                        }).max().orElse(0);
                    } else {
                        candidateHeightAdjustments = 0;
                    }

                    // Check for each of the candidate's jigsaw blocks for a match
                    for (Template.BlockInfo candidateJigsawBlock : candidateJigsawBlocks) {
                        if (JigsawBlock.hasJigsawMatch(jigsawBlock, candidateJigsawBlock)) {
                            BlockPos candidateJigsawBlockPos = candidateJigsawBlock.pos;
                            BlockPos candidateJigsawBlockRelativePos = new BlockPos(jigsawBlockTargetPos.getX() - candidateJigsawBlockPos.getX(), jigsawBlockTargetPos.getY() - candidateJigsawBlockPos.getY(), jigsawBlockTargetPos.getZ() - candidateJigsawBlockPos.getZ());

                            // Get the bounding box for the piece, offset by the relative position difference
                            MutableBoundingBox candidateBoundingBox = candidatePiece.getBoundingBox(this.templateManager, candidateJigsawBlockRelativePos, rotation);

                            // Determine if candidate is rigid
                            JigsawPattern.PlacementBehaviour candidatePlacementBehavior = candidatePiece.getPlacementBehaviour();
                            boolean isCandidateRigid = candidatePlacementBehavior == JigsawPattern.PlacementBehaviour.RIGID;

                            // Determine how much the candidate jigsaw block is off in the y direction.
                            // This will be needed to offset the candidate piece so that the jigsaw blocks line up properly.
                            int candidateJigsawBlockRelativeY = candidateJigsawBlockPos.getY();
                            int candidateJigsawYOffsetNeeded = jigsawBlockRelativeY - candidateJigsawBlockRelativeY + JigsawBlock.getConnectingDirection(jigsawBlock.state).getYOffset();

                            // Determine how much we need to offset the candidate piece itself in order to have the jigsaw blocks aligned.
                            // Depends on if the placement of both pieces is rigid or not
                            int adjustedCandidatePieceMinY;
                            if (isPieceRigid && isCandidateRigid) {
                                adjustedCandidatePieceMinY = pieceMinY + candidateJigsawYOffsetNeeded;
                            } else {
                                if (surfaceHeight == -1) {
                                    surfaceHeight = this.chunkGenerator.getNoiseHeight(jigsawBlockPos.getX(), jigsawBlockPos.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                                }

                                adjustedCandidatePieceMinY = surfaceHeight - candidateJigsawBlockRelativeY;
                            }
                            int candidatePieceYOffsetNeeded = adjustedCandidatePieceMinY - candidateBoundingBox.minY;

                            // Offset the candidate's bounding box by the necessary amount
                            MutableBoundingBox adjustedCandidateBoundingBox = candidateBoundingBox.func_215127_b(0, candidatePieceYOffsetNeeded, 0);

                            // Add this offset to the relative jigsaw block position as well
                            BlockPos adjustedCandidateJigsawBlockRelativePos = candidateJigsawBlockRelativePos.add(0, candidatePieceYOffsetNeeded, 0);

                            // Final adjustments to the bounding box.
                            if (candidateHeightAdjustments > 0) {
                                int k2 = Math.max(candidateHeightAdjustments + 1, adjustedCandidateBoundingBox.maxY - adjustedCandidateBoundingBox.minY);
                                adjustedCandidateBoundingBox.maxY = adjustedCandidateBoundingBox.minY + k2;
                            }

                            // Prevent pieces from spawning above max Y
                            if (adjustedCandidateBoundingBox.maxY > this.maxY) {
                                continue;
                            }

                            // Some sort of final boundary check before adding the new piece.
                            // Not sure why the candidate box is shrunk by 0.25.
                            if (!VoxelShapes.compare
                                (
                                    pieceVoxelShape.getValue(),
                                    VoxelShapes.create(AxisAlignedBB.toImmutable(adjustedCandidateBoundingBox).shrink(0.25D)),
                                    IBooleanFunction.ONLY_SECOND
                                )
                            ) {
                                pieceVoxelShape.setValue(
                                    VoxelShapes.combine(
                                        pieceVoxelShape.getValue(),
                                        VoxelShapes.create(AxisAlignedBB.toImmutable(adjustedCandidateBoundingBox)),
                                        IBooleanFunction.ONLY_FIRST
                                    )
                                );

                                // Determine ground level delta for this new piece
                                int newPieceGroundLevelDelta = piece.getGroundLevelDelta();
                                int groundLevelDelta;
                                if (isCandidateRigid) {
                                    groundLevelDelta = newPieceGroundLevelDelta - candidateJigsawYOffsetNeeded;
                                } else {
                                    groundLevelDelta = candidatePiece.getGroundLevelDelta();
                                }

                                // Create new piece
                                AbstractVillagePiece newPiece = new AbstractVillagePiece(
                                    this.templateManager,
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
                                        surfaceHeight = this.chunkGenerator.getNoiseHeight(jigsawBlockPos.getX(), jigsawBlockPos.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
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
                                this.structurePieces.add(newPiece);
                                if (depth + 1 <= this.maxDepth) {
                                    this.availablePieces.addLast(new Entry(newPiece, pieceVoxelShape, targetPieceBoundsTop, depth + 1));
                                }

                                // Update piece count, if piece is of max count type
                                if (candidatePiece instanceof IMaxCountJigsawPiece) {
                                    String pieceName = ((IMaxCountJigsawPiece) candidatePiece).getName();
                                    this.pieceCounts.put(pieceName, this.pieceCounts.getOrDefault(pieceName, 0) + 1);
                                }
                                return candidatePiece;
                            }
                        }
                    }
                }
                totalWeightSum -= chosenPiecePair.getSecond();
                candidatePieces.remove(chosenPiecePair);
            }
            return null;
        }
    }

    public static final class Entry {
        public final AbstractVillagePiece villagePiece;
        public final MutableObject<VoxelShape> voxelShape;
        public final int boundsTop;
        public final int depth;

        public Entry(AbstractVillagePiece villagePiece, MutableObject<VoxelShape> voxelShape, int boundsTop, int depth) {
            this.villagePiece = villagePiece;
            this.voxelShape = voxelShape;
            this.boundsTop = boundsTop;
            this.depth = depth;
        }
    }
}

