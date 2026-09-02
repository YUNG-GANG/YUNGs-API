package com.yungnickyoung.minecraft.yungsapi.services;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.api.world.structure.processor.StructureEntityProcessorBuilder;
import com.yungnickyoung.minecraft.yungsapi.world.processor.StructureEntityProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

import java.util.Optional;
import java.util.function.Function;

public class FabricStructureProcessorHelper implements IStructureProcessorHelper {
    @Override
    public StructureProcessorType<StructureProcessor> createStructureProcessorType(final Function<ValueInput, Optional<StructureEntityProcessorBuilder.Processor>> function) {
        var processor = new StructureEntityProcessor() {
            private final MapCodec<StructureProcessor> codec = MapCodec.unit(this);
            private final StructureProcessorType<StructureProcessor> type = () -> this.codec;

            @Override
            public StructureTemplate.StructureEntityInfo processEntity(
                    final ServerLevelAccessor serverLevelAccessor,
                    final BlockPos structurePiecePos,
                    final BlockPos structurePieceBottomCenterPos,
                    final StructureTemplate.StructureEntityInfo localEntityInfo,
                    final StructureTemplate.StructureEntityInfo globalEntityInfo,
                    final StructurePlaceSettings structurePlaceSettings) {
                var valueInput = TagValueInput.create(ProblemReporter.DISCARDING, serverLevelAccessor.registryAccess(), globalEntityInfo.nbt);
                return function.apply(valueInput).map(p -> {
                    var valueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, serverLevelAccessor.registryAccess());
                    p.process(serverLevelAccessor, structurePiecePos,
                                    localEntityInfo, globalEntityInfo, structurePlaceSettings)
                            .accept(valueOutput);
                    var processedTag = globalEntityInfo.nbt.copy();
                    var newTag = valueOutput.buildResult();
                    IStructureProcessorHelper.merge(processedTag, newTag);
                    return new StructureTemplate.StructureEntityInfo(globalEntityInfo.pos, globalEntityInfo.blockPos, processedTag);
                }).orElse(globalEntityInfo);
            }

            @Override protected StructureProcessorType<?> getType() {
                return this.type;
            }
        };
        return processor.type;
    }
}
