package com.yungnickyoung.minecraft.yungsapi.world.processor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link StructureProcessor} that processes entities.
 * May or may not also process blocks normally.
 */
public abstract class StructureEntityProcessor extends StructureProcessor {

    /**
     * Applies a processor to an entity in a structure component or Jigsaw piece.
     * @param serverLevelAccessor The WorldView
     * @param structurePiecePos The global block position of the current structure component or Jigsaw piece.
     *                          Usually this is a corner of the piece, often lowest in x-z value.
     * @param structurePieceBottomCenterPos The global block position of the bottom-center of the
     *                                      current structure component or Jigsaw piece.
     * @param localEntityInfo The raw entity info. Its {@code pos} and {@code blockpos} fields will refer to its local
     *                        position within the current structure component or jigsaw piece.
     *                        DO NOT modify its {@code pos} or {@code blockpos} fields!
     * @param globalEntityInfo The global entity info. This object's {@code pos} and {@code blockpos} fields are the
     *                         adjusted, real-world coordinates. You can use these for a unique random seed, for example.
     *                         This object reflects updates made from previously run processors for this entity.
     *                         This object is set to the return value of this function, so be careful when overwriting
     *                         existing NBT.
     *                         DO NOT modify its {@code pos} or {@code blockpos} fields!
     * @param structurePlaceSettings The structure's placement data
     * @return The processed StructureEntityInfo. Note that the globalEntityInfo parameter will be set to this object,
     *         so if you want to combine the effects of multiple processors on a single entity, do NOT overwrite
     *         or discard existing NBT from the globalEntityInfo.
     *         If a null value is returned, the entity being processed will be discarded.
     */
    @Nullable
    public abstract StructureTemplate.StructureEntityInfo processEntity(ServerLevelAccessor serverLevelAccessor,
                                                                        BlockPos structurePiecePos,
                                                                        BlockPos structurePieceBottomCenterPos,
                                                                        StructureTemplate.StructureEntityInfo localEntityInfo,
                                                                        StructureTemplate.StructureEntityInfo globalEntityInfo,
                                                                        StructurePlaceSettings structurePlaceSettings);
}
