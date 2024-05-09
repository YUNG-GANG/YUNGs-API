package com.yungnickyoung.minecraft.yungsapi.world.processor;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;

public record StructureProcessingContext(ServerLevelAccessor serverLevelAccessor,
                                         StructurePlaceSettings structurePlaceSettings, BlockPos structurePiecePos,
                                         BlockPos structurePiecePivotPos,
                                         List<StructureTemplate.StructureEntityInfo> rawEntityInfos) {
    public StructureProcessingContext(ServerLevelAccessor serverLevelAccessor,
                                      StructurePlaceSettings structurePlaceSettings,
                                      BlockPos structurePiecePos,
                                      BlockPos structurePiecePivotPos,
                                      List<StructureTemplate.StructureEntityInfo> rawEntityInfos) {
        this.serverLevelAccessor = serverLevelAccessor;
        this.structurePlaceSettings = structurePlaceSettings;
        this.structurePiecePos = structurePiecePos;
        this.structurePiecePivotPos = structurePiecePivotPos;
        this.rawEntityInfos = Util.make(() -> {
            List<StructureTemplate.StructureEntityInfo> list = new ArrayList<>(rawEntityInfos.size());
            rawEntityInfos.forEach((entityInfo) ->
                    list.add(new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, entityInfo.nbt)));
            return list;
        });
    }
}