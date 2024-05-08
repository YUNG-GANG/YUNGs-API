package com.yungnickyoung.minecraft.yungsapi.world.processor;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class  StructureProcessingContext {
    private final ServerLevelAccessor serverLevelAccessor;
    private final StructurePlaceSettings structurePlaceSettings;
    private final BlockPos structurePiecePos;
    private final BlockPos structurePiecePivotPos;
    private final Iterator<StructureTemplate.StructureEntityInfo> iterator;
    private final List<StructureTemplate.StructureEntityInfo> rawEntityInfos;

    public StructureProcessingContext(ServerLevelAccessor serverLevelAccessor,
                                      StructurePlaceSettings structurePlaceSettings,
                                      BlockPos structurePiecePos,
                                      BlockPos structurePiecePivotPos,
                                      List<StructureTemplate.StructureEntityInfo> rawEntityInfos) {
        this.serverLevelAccessor = serverLevelAccessor;
        this.structurePlaceSettings = structurePlaceSettings;
        this.structurePiecePos = structurePiecePos;
        this.structurePiecePivotPos = structurePiecePivotPos;
        YungsApiCommon.LOGGER.info("COPYING LIST OF SIZE {}", rawEntityInfos.size());
        List<StructureTemplate.StructureEntityInfo> copy = Util.make(() -> {
            List<StructureTemplate.StructureEntityInfo> list = new ArrayList<>(rawEntityInfos.size());
            rawEntityInfos.forEach((entityInfo) ->
                    list.add(new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, entityInfo.nbt)));
            return list;
        });
        if (!rawEntityInfos.isEmpty()) {
            YungsApiCommon.LOGGER.info("MORE THAN 0");
        }
        this.rawEntityInfos = copy;
        this.iterator = copy.listIterator();

    }

    public ServerLevelAccessor getServerLevelAccessor() {
        return serverLevelAccessor;
    }

    public StructurePlaceSettings getStructurePlaceSettings() {
        return structurePlaceSettings;
    }

    public BlockPos getStructurePiecePos() {
        return structurePiecePos;
    }

    public BlockPos getStructurePiecePivotPos() {
        return structurePiecePivotPos;
    }

    public Iterator<StructureTemplate.StructureEntityInfo> getIterator() {
        return iterator;
    }

    public List<StructureTemplate.StructureEntityInfo> getRawEntityInfos() {
        return rawEntityInfos;
    }
}
