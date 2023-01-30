package com.yungnickyoung.minecraft.yungsapi.world.jigsaw.assembler;

import com.google.common.collect.Queues;
import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.BoundingBoxAccessor;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.StructureTemplatePoolAccessor;
import com.yungnickyoung.minecraft.yungsapi.util.BoxOctree;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.JigsawManager;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.PieceEntry;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.IMaxCountJigsawPoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.jigsaw.piece.YungJigsawSinglePoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Class responsible for assembling a YUNG Jigsaw structure.
 * This class is not intended for use on its own, but rather as a utility within {@link JigsawManager}.
 * It is originally based on vanilla's Placer class within {@link JigsawPlacement}.
 */
public class JigsawStructureAssembler {
    private final Settings settings;

    /**
     * Pieces of the structure.
     * A piece only gets added to this list when its spawn conditions have been validated to ensure
     * it should be added to the structure.
     */
    private final List<PieceEntry> pieces = new ArrayList<>();

    /**
     * Queue of unprocessed pieces to be processed.
     * These are pieces that have already been added to the {@code pieces} list, and will therefore spawn in the structure,
     * but need to be scanned for Jigsaw blocks themselves to determine if they have any child pieces to be placed.
     */
    public Deque<PieceEntry> unprocessedPieceEntries = Queues.newArrayDeque();

    /**
     * Map of each piece to the current count of that piece in the entire structure.
     * This data is only stored for the pieces that need it, i.e. named {@link IMaxCountJigsawPoolElement} pieces and
     * named {@link YungJigsawSinglePoolElement} pieces with the {@code maxCount} specified.
     */
    private final Map<String, Integer> pieceCounts = new HashMap<>();

    /**
     * Map of each piece count to the maximum number of that piece allowed in the structure.
     * Only applies to named {@link IMaxCountJigsawPoolElement} pieces, or named {@link YungJigsawSinglePoolElement} pieces with the {@code maxCount} specified.
     */
    private final Map<String, Integer> maxPieceCounts = new HashMap<>();

    public JigsawStructureAssembler(Settings settings) {
        this.settings = settings;
    }

    /**
     * Assembles a YUNG Jigsaw structure, populating an internal list of all the pieces that comprise the structure.
     *
     * @param startPiece The starting piece of the structure
     * @param structureBounds The maximum allowed bounds of the structure
     */
    public void assembleStructure(PoolElementStructurePiece startPiece, BoxOctree structureBounds) {
        // Create starting piece entry
        PieceEntry startPieceEntry = new PieceEntry(
                startPiece,
                new MutableObject<>(structureBounds),
                null,
                0,
                null,
                null,
                null);

        // Add the start piece to the assembler
        this.pieces.add(startPieceEntry);
        this.unprocessedPieceEntries.addLast(startPieceEntry);

        // Assemble the structure
        while (!this.unprocessedPieceEntries.isEmpty()) {
            PieceEntry entry = this.unprocessedPieceEntries.removeFirst();
            this.addChildrenForPiece(entry);
        }

        // Final post-assemble modifications
        this.applyModifications();
    }

    /**
     * Adds all assembled pieces to the provided StructurePiecesBuilder.
     * These will be actually placed in the world during a later stage of worldgen.
     * This should only be called after {@link JigsawStructureAssembler#assembleStructure(PoolElementStructurePiece, BoxOctree)}
     * has been called.
     */
    public void addAllPiecesToStructureBuilder(StructurePiecesBuilder structurePiecesBuilder) {
        this.pieces.forEach(pieceEntry -> structurePiecesBuilder.addPiece(pieceEntry.getPiece()));
    }

    /**
     * Scans the provided PieceEntry's piece for Jigsaw blocks.
     * For each Jigsaw block, if a valid target child piece is found, then the child will be added to this.pieces,
     * meaning it will generate in the final structure (barring any post-assemble modifications transforming it).
     * The child will also be added to this.unprocessedPieceEntries, meaning it will also eventually be scanned for
     * potential child pieces of its own.
     */
    private void addChildrenForPiece(PieceEntry pieceEntry) {
        // Collect data from params regarding piece to process
        PoolElementStructurePiece piece = pieceEntry.getPiece();
        MutableObject<BoxOctree> parentOctree = new MutableObject<>();

        // Get list of all jigsaw blocks in this piece
        List<StructureTemplate.StructureBlockInfo> pieceJigsawBlocks = piece.getElement().getShuffledJigsawBlocks(
                this.settings.structureTemplateManager,
                piece.getPosition(),
                piece.getRotation(),
                this.settings.rand);

        boolean generatedAtLeastOneChildPiece = false;

        for (StructureTemplate.StructureBlockInfo jigsawBlockInfo : pieceJigsawBlocks) {
            // Get the jigsaw block's target pool
            ResourceLocation targetPoolId = new ResourceLocation(jigsawBlockInfo.nbt.getString("pool"));
            Optional<StructureTemplatePool> targetPool = getPoolFromId(targetPoolId);
            if (targetPool.isEmpty()) continue;

            // Get the jigsaw block's fallback pool (defined in the pool's JSON)
            ResourceLocation fallbackPoolId = targetPool.get().getFallback();
            Optional<StructureTemplatePool> fallbackPool = getPoolFromId(fallbackPoolId);
            if (fallbackPool.isEmpty()) continue;

            PieceContext pieceContext = createPieceContextForJigsawBlock(jigsawBlockInfo, pieceEntry, parentOctree);
            Optional<StructurePoolElement> newlyGeneratedPiece = Optional.empty();

            // Attempt to place a piece from the target pool
            if (pieceEntry.getDepth() != this.settings.maxDepth) {
                pieceContext.candidatePoolElements = new ObjectArrayList<>(((StructureTemplatePoolAccessor) targetPool.get()).getRawTemplates());
                newlyGeneratedPiece = this.chooseCandidateFromPool(pieceContext);
            }

            // If no pieces in the target pool could be placed, try the fallback pool
            if (newlyGeneratedPiece.isEmpty()) {
                pieceContext.candidatePoolElements = new ObjectArrayList<>(((StructureTemplatePoolAccessor) fallbackPool.get()).getRawTemplates());
                newlyGeneratedPiece = this.chooseCandidateFromPool(pieceContext);
            }

            // If any piece was placed, we record that fact for later
            if (newlyGeneratedPiece.isPresent()) {
                generatedAtLeastOneChildPiece = true;
            }
        }

        /* If this piece has a deadend pool specified and has jigsaw blocks leading out of it yet no child pieces
         * were able to be placed, convert this piece to a dead end.
         * This is accomplished by re-processing its parent piece, but force using the deadend pool.
         */
        if (pieceEntry.hasDeadendPool() && !generatedAtLeastOneChildPiece && pieceJigsawBlocks.size() > 1) {
            // Get deadend pool from id
            ResourceLocation deadendPoolId = ((YungJigsawSinglePoolElement) piece.getElement()).getDeadendPool();
            Optional<StructureTemplatePool> deadendPool = this.settings.poolRegistry.getOptional(deadendPoolId);
            if (deadendPool.isEmpty()) {
                YungsApiCommon.LOGGER.error("Unable to find deadend pool {} for element {}", deadendPoolId, piece.getElement());
                return;
            }

            PieceEntry parentEntry = pieceEntry.getParentEntry();
            PieceContext newContext = pieceEntry.getSourcePieceContext().copy();
            newContext.candidatePoolElements = new ObjectArrayList<>(((StructureTemplatePoolAccessor) deadendPool.get()).getRawTemplates());
            AABB pieceAabb = pieceEntry.getPieceAabb();
            if (parentEntry != null && pieceAabb != null) {
                parentEntry.getPiece().getJunctions().remove(pieceEntry.getParentJunction());
                pieceEntry.getBoxOctree().getValue().removeBox(pieceAabb);
                this.pieces.remove(pieceEntry);
                this.chooseCandidateFromPool(newContext);
            }
        }
    }

    private Optional<StructureTemplatePool> getPoolFromId(ResourceLocation id) {
        Optional<StructureTemplatePool> pool = this.settings.poolRegistry.getOptional(id);

        // Check if pool is empty. The only allowed empty pool is minecraft:empty.
        if (pool.isEmpty() || (pool.get().size() == 0 && !Objects.equals(id, Pools.EMPTY.location()))) {
            YungsApiCommon.LOGGER.warn("Empty or nonexistent pool: {}", id);
            return Optional.empty();
        }

        return pool;
    }

    private PieceContext createPieceContextForJigsawBlock(
            StructureTemplate.StructureBlockInfo jigsawBlockInfo,
            PieceEntry pieceEntry,
            MutableObject<BoxOctree> parentOctree
    ) {
        BoundingBox pieceBoundingBox = pieceEntry.getPiece().getBoundingBox();
        MutableObject<BoxOctree> pieceOctree = pieceEntry.getBoxOctree();

        // Gather jigsaw block information
        Direction direction = JigsawBlock.getFrontFacing(jigsawBlockInfo.state);
        BlockPos jigsawBlockTargetPos = jigsawBlockInfo.pos.relative(direction);

        // Adjustments for if the target block position is inside the current piece
        boolean isTargetInsideCurrentPiece = pieceBoundingBox.isInside(jigsawBlockTargetPos);
        if (isTargetInsideCurrentPiece) {
            pieceOctree = parentOctree;
            if (parentOctree.getValue() == null) {
                parentOctree.setValue(new BoxOctree(AABB.of(pieceBoundingBox)));
            }
        }

        return new PieceContext(
                null,
                jigsawBlockInfo,
                jigsawBlockTargetPos,
                pieceBoundingBox.minY(),
                jigsawBlockInfo.pos,
                pieceOctree,
                pieceEntry,
                pieceEntry.getDepth());
    }

    /**
     * Searches the context's candidatePoolElements for a suitable piece to spawn.
     * If a suitable piece is found, then it will be added to this assembler's list of pieces ({@link JigsawStructureAssembler#pieces}.
     * It will be also be added to this assembler's unprocessed piece queue.
     *
     * @return The StructurePoolElement for the piece generated, or empty Optional if no suitable piece was found.
     */
    private Optional<StructurePoolElement> chooseCandidateFromPool(PieceContext context) {
        // Extract args from context
        ObjectArrayList<Pair<StructurePoolElement, Integer>> candidatePoolElements = context.candidatePoolElements;
        PoolElementStructurePiece piece = context.pieceEntry.getPiece();
        boolean isPieceRigid = piece.getElement().getProjection() == StructureTemplatePool.Projection.RIGID;
        int jigsawBlockRelativeY = context.jigsawBlockPos.getY() - context.pieceMinY;
        int surfaceHeight = -1; // The y-coordinate of the surface. Only used if isPieceRigid is false.

        // Shuffle our candidate pool elements
        Util.shuffle(candidatePoolElements, this.settings.rand);

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
                int chosenWeight = this.settings.rand.nextInt(totalWeightSum) + 1;

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
                return Optional.empty();
            }

            // Validate to make sure we haven't reached the max number of instances of this piece, if applicable
            if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungSingleElement && yungSingleElement.maxCount.isPresent()) {
                int pieceMaxCount = yungSingleElement.maxCount.get();

                // Max count pieces must also be named
                if (yungSingleElement.name.isEmpty()) {
                    YungsApiCommon.LOGGER.error("Found YUNG Jigsaw piece with max_count={} missing \"name\" property.", pieceMaxCount);
                    YungsApiCommon.LOGGER.error("Max count pieces must be named in order to work properly!");
                    YungsApiCommon.LOGGER.error("Ignoring max_count for this piece...");
                } else {
                    String pieceName = yungSingleElement.name.get();
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
            if (chosenPoolElement instanceof IMaxCountJigsawPoolElement) {
                String pieceName = ((IMaxCountJigsawPoolElement) chosenPoolElement).getName();
                int maxCount = ((IMaxCountJigsawPoolElement) chosenPoolElement).getMaxCount();

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
            if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungSingleElement && !yungSingleElement.isAtValidDepth(context.depth)) {
                totalWeightSum -= chosenPieceWeight;
                candidatePoolElements.remove(chosenPoolElementPair);
                continue;
            }

            // Try different rotations to see which sides of the piece are fit to be the receiving end
            for (Rotation rotation : Rotation.getShuffled(this.settings.rand)) {
                List<StructureTemplate.StructureBlockInfo> candidateJigsawBlocks = chosenPoolElement.getShuffledJigsawBlocks(this.settings.structureTemplateManager, BlockPos.ZERO, rotation, this.settings.rand);

                // Some sort of logic for setting the candidateHeightAdjustments var if useExpansionHack.
                // Not sure on this - personally, I never enable useExpansionHack.
                BoundingBox tempCandidateBoundingBox = chosenPoolElement.getBoundingBox(this.settings.structureTemplateManager, BlockPos.ZERO, rotation);
                int candidateHeightAdjustments = 0;
                if (this.settings.useExpansionHack && tempCandidateBoundingBox.getYSpan() <= 16) {
                    candidateHeightAdjustments = candidateJigsawBlocks.stream().mapToInt((pieceCandidateJigsawBlock) -> {
                        if (!tempCandidateBoundingBox.isInside(pieceCandidateJigsawBlock.pos.relative(JigsawBlock.getFrontFacing(pieceCandidateJigsawBlock.state)))) {
                            return 0;
                        }
                        ResourceLocation candidateTargetPool = new ResourceLocation(pieceCandidateJigsawBlock.nbt.getString("pool"));
                        Optional<StructureTemplatePool> candidateTargetPoolOptional = this.settings.poolRegistry.getOptional(candidateTargetPool);
                        Optional<StructureTemplatePool> candidateTargetFallbackOptional = candidateTargetPoolOptional.flatMap((StructureTemplatePool) -> this.settings.poolRegistry.getOptional(StructureTemplatePool.getFallback()));
                        int tallestCandidateTargetPoolPieceHeight = candidateTargetPoolOptional.map((structureTemplatePool) -> structureTemplatePool.getMaxSize(this.settings.structureTemplateManager)).orElse(0);
                        int tallestCandidateTargetFallbackPieceHeight = candidateTargetFallbackOptional.map((structureTemplatePool) -> structureTemplatePool.getMaxSize(this.settings.structureTemplateManager)).orElse(0);
                        return Math.max(tallestCandidateTargetPoolPieceHeight, tallestCandidateTargetFallbackPieceHeight);
                    }).max().orElse(0);
                }

                // Check each of the candidate's jigsaw blocks for a match
                for (StructureTemplate.StructureBlockInfo candidateJigsawBlock : candidateJigsawBlocks) {
                    if (!JigsawBlock.canAttach(context.jigsawBlock, candidateJigsawBlock)) continue;

                    BlockPos candidateJigsawBlockPos = candidateJigsawBlock.pos;
                    BlockPos candidateJigsawBlockRelativePos = context.jigsawBlockTargetPos.subtract(candidateJigsawBlockPos);

                    // Get the rotated bounding box for the piece, offset by the relative position difference
                    BoundingBox rotatedCandidateBoundingBox = chosenPoolElement.getBoundingBox(this.settings.structureTemplateManager, candidateJigsawBlockRelativePos, rotation);

                    // Determine if candidate is rigid
                    StructureTemplatePool.Projection candidateProjection = chosenPoolElement.getProjection();
                    boolean isCandidateRigid = candidateProjection == StructureTemplatePool.Projection.RIGID;

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
                            surfaceHeight = this.settings.chunkGenerator.getFirstFreeHeight(context.jigsawBlockPos.getX(), context.jigsawBlockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, this.settings.levelHeightAccessor, this.settings.randomState);
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
                    if (this.settings.maxY.isPresent() && adjustedCandidateBoundingBox.maxY() > this.settings.maxY.get()) continue;
                    if (this.settings.minY.isPresent() && adjustedCandidateBoundingBox.minY() < this.settings.minY.get()) continue;

                    // Final boundary check before adding the new piece.
                    // Not sure why the candidate box is shrunk by 0.25. Maybe just ensures no overlap for adjacent block positions?
                    AABB aabb = AABB.of(adjustedCandidateBoundingBox);
                    AABB aabbDeflated = aabb.deflate(0.25);
                    boolean pieceIgnoresBounds = false;

                    if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungSingleElement) {
                        pieceIgnoresBounds = yungSingleElement.ignoresBounds();
                    }

                    // Validate piece boundaries
                    if (!pieceIgnoresBounds) {
                        boolean pieceIntersectsExistingPieces = context.boxOctree.getValue().intersectsAnyBox(aabbDeflated);
                        boolean pieceIsContainedWithinStructureBoundaries = context.boxOctree.getValue().boundaryContains(aabbDeflated);
                        if (pieceIntersectsExistingPieces || !pieceIsContainedWithinStructureBoundaries) {
                            continue;
                        }
                    }

                    // Determine ground level delta for this new piece
                    int newPieceGroundLevelDelta = piece.getGroundLevelDelta();
                    int groundLevelDelta;
                    if (isCandidateRigid) {
                        groundLevelDelta = newPieceGroundLevelDelta - candidateJigsawYOffsetNeeded;
                    } else {
                        groundLevelDelta = chosenPoolElement.getGroundLevelDelta();
                    }

                    // Determine actual y-value for the new jigsaw block
                    int candidateJigsawBlockY;
                    if (isPieceRigid) {
                        candidateJigsawBlockY = context.pieceMinY + jigsawBlockRelativeY;
                    } else if (isCandidateRigid) {
                        candidateJigsawBlockY = adjustedCandidatePieceMinY + candidateJigsawBlockRelativeY;
                    } else {
                        if (surfaceHeight == -1) {
                            surfaceHeight = this.settings.chunkGenerator.getFirstFreeHeight(context.jigsawBlockPos.getX(), context.jigsawBlockPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, this.settings.levelHeightAccessor, this.settings.randomState);
                        }

                        candidateJigsawBlockY = surfaceHeight + candidateJigsawYOffsetNeeded / 2;
                    }

                    // Create new piece info
                    PoolElementStructurePiece newPiece = new PoolElementStructurePiece(
                            this.settings.structureTemplateManager,
                            chosenPoolElement,
                            adjustedCandidateJigsawBlockRelativePos,
                            groundLevelDelta,
                            rotation,
                            adjustedCandidateBoundingBox);

                    JigsawJunction newJunctionOnParent = new JigsawJunction(
                            context.jigsawBlockTargetPos.getX(),
                            candidateJigsawBlockY - jigsawBlockRelativeY + newPieceGroundLevelDelta,
                            context.jigsawBlockTargetPos.getZ(),
                            candidateJigsawYOffsetNeeded,
                            candidateProjection);

                    PieceEntry newPieceEntry = new PieceEntry(newPiece, context.boxOctree, aabb,
                            context.depth + 1, context.pieceEntry, context.copy(), newJunctionOnParent);

                    // Validate conditions for this piece, if applicable
                    if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungSingleElement) {
                        StructureContext ctx = new StructureContext.Builder()
                                .structureTemplateManager(this.settings.structureTemplateManager)
                                .pieces(this.pieces)
                                .pieceEntry(newPieceEntry)
                                .pos(adjustedCandidateJigsawBlockRelativePos)
                                .rotation(rotation)
                                .pieceMinY(adjustedCandidateBoundingBox.minY())
                                .pieceMaxY(adjustedCandidateBoundingBox.maxY())
                                .depth(context.depth + 1)
                                .build();
                        if (!yungSingleElement.passesConditions(ctx)) {
                            continue; // Abort this piece & rotation if it doesn't pass conditions check
                        }
                    }

                    // Add the junction to the existing piece
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

                    // Add the new piece's box to the structure's
                    context.boxOctree.getValue().addBox(aabb);

                    // Add the piece
                    this.pieces.add(newPieceEntry);
                    context.pieceEntry.addChildEntry(newPieceEntry);

                    // If there's still room, add an entry for the new piece to be processed
                    if (context.depth + 1 <= this.settings.maxDepth) {
                        this.unprocessedPieceEntries.addLast(newPieceEntry);
                    }

                    // Update piece count, if applicable
                    if (chosenPoolElement instanceof YungJigsawSinglePoolElement yungSingleElement && yungSingleElement.maxCount.isPresent()) {
                        // Max count pieces must also be named.
                        // The following condition will never be met if users correctly configure their template pools.
                        if (yungSingleElement.name.isEmpty()) {
                            // If name is missing, ignore max count for this piece. We've already logged an error for it earlier.
                            return Optional.of(chosenPoolElement);
                        }

                        String pieceName = yungSingleElement.name.get();
                        this.pieceCounts.put(pieceName, this.pieceCounts.getOrDefault(pieceName, 0) + 1);
                    }

                    // LEGACY - support for IMaxCountJigsawPiece
                    if (chosenPoolElement instanceof IMaxCountJigsawPoolElement) {
                        String pieceName = ((IMaxCountJigsawPoolElement) chosenPoolElement).getName();
                        this.pieceCounts.put(pieceName, this.pieceCounts.getOrDefault(pieceName, 0) + 1);
                    }

                    return Optional.of(chosenPoolElement);
                }
            }
            totalWeightSum -= chosenPieceWeight;
            candidatePoolElements.remove(chosenPoolElementPair);
        }
        return Optional.empty();
    }

    /**
     * Applies post-assembly modifications to any Yung elements with modifiers attached.
     * Modifiers can be optionally specified in JSON as part of a Yung pool element.
     */
    private void applyModifications() {
        for (PieceEntry pieceEntry : this.pieces) {
            if (pieceEntry.getPiece().getElement() instanceof YungJigsawSinglePoolElement yungElement) {
                if (yungElement.hasModifiers()) {
                    PoolElementStructurePiece piece = pieceEntry.getPiece();
                    StructureContext structureContext = new StructureContext.Builder()
                            .pos(piece.getPosition())
                            .rotation(piece.getRotation())
                            .depth(pieceEntry.getDepth())
                            .structureTemplateManager(this.settings.structureTemplateManager)
                            .pieceEntry(pieceEntry)
                            .pieces(this.pieces)
                            .pieceMaxY(piece.getBoundingBox().maxY())
                            .pieceMinY(piece.getBoundingBox().minY())
                            .build();
                    yungElement.modifiers.forEach(modifier -> modifier.apply(structureContext));
                }
            }
        }
    }

    public static class Settings {
        /**
         * StructureTemplatePool registry
         **/
        private Registry<StructureTemplatePool> poolRegistry;

        /**
         * The maximum piece depth allowed for this structure
         **/
        private int maxDepth;

        /**
         * The level's ChunkGenerator to be used.
         **/
        private ChunkGenerator chunkGenerator;

        /**
         * The level's StructureTemplateManager to be used.
         **/
        private StructureTemplateManager structureTemplateManager;

        /**
         * The level's LevelHeightAccessor to be used.
         **/
        private LevelHeightAccessor levelHeightAccessor;

        /**
         * The level's RandomSource to be used, usually a WorldGenRandom.
         **/
        private RandomSource rand;

        /**
         * Whether this structure should use the "expansion hack."
         * This is generally a legacy feature that should be avoided.
         **/
        private boolean useExpansionHack;

        /**
         * The RandomState from the GenerationContext.
         */
        public RandomState randomState;

        /**
         * The maximum possible y-value of the structure.
         * If any portion of a piece extends above this value, it will not be placed.
         */
        private Optional<Integer> maxY;

        /**
         * The minimum possible y-value of the structure.
         * If any portion of a piece extends below this value, it will not be placed.
         */
        private Optional<Integer> minY;

        public Settings() {
        }

        public Settings poolRegistry(Registry<StructureTemplatePool> poolRegistry) {
            this.poolRegistry = poolRegistry;
            return this;
        }

        public Settings maxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public Settings chunkGenerator(ChunkGenerator chunkGenerator) {
            this.chunkGenerator = chunkGenerator;
            return this;
        }

        public Settings structureTemplateManager(StructureTemplateManager structureTemplateManager) {
            this.structureTemplateManager = structureTemplateManager;
            return this;
        }

        public Settings randomState(RandomState randomState) {
            this.randomState = randomState;
            return this;
        }

        public Settings rand(RandomSource rand) {
            this.rand = rand;
            return this;
        }

        public Settings useExpansionHack(boolean useExpansionHack) {
            this.useExpansionHack = useExpansionHack;
            return this;
        }

        public Settings levelHeightAccessor(LevelHeightAccessor levelHeightAccessor) {
            this.levelHeightAccessor = levelHeightAccessor;
            return this;
        }

        public Settings maxY(Optional<Integer> maxY) {
            this.maxY = maxY;
            return this;
        }

        public Settings minY(Optional<Integer> minY) {
            this.minY = minY;
            return this;
        }
    }
}
