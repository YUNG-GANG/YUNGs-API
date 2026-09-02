package com.yungnickyoung.minecraft.yungsapi.services;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.yungsapi.api.world.structure.processor.StructureEntityProcessorBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

import java.util.Optional;
import java.util.function.Function;

public class NeoForgeStructureProcessorHelper implements IStructureProcessorHelper {
    @Override
    public StructureProcessorType<StructureProcessor> createStructureProcessorType(final Function<ValueInput, Optional<StructureEntityProcessorBuilder.Processor>> function) {
        var processor = new StructureProcessor() {
            private final MapCodec<StructureProcessor> codec = MapCodec.unit(this);
            private final StructureProcessorType<StructureProcessor> type = () -> this.codec;

            @Override public StructureTemplate.StructureEntityInfo processEntity(
                    final LevelReader world,
                    final BlockPos seedPos,
                    final StructureTemplate.StructureEntityInfo rawEntityInfo,
                    final StructureTemplate.StructureEntityInfo entityInfo,
                    final StructurePlaceSettings placementSettings,
                    final StructureTemplate template) {
                var valueInput = TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), entityInfo.nbt);
                return function.apply(valueInput).map(p -> {
                    var valueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, world.registryAccess());
                    p.process(world, seedPos, rawEntityInfo, entityInfo, placementSettings)
                            .accept(valueOutput);
                    var processedTag = entityInfo.nbt.copy();
                    var newTag = valueOutput.buildResult();
                    IStructureProcessorHelper.merge(processedTag, newTag);
                    return new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, processedTag);
                }).orElse(entityInfo);
            }

            @Override protected StructureProcessorType<?> getType() {
                return this.type;
            }
        };
        return processor.type;
    }
}
