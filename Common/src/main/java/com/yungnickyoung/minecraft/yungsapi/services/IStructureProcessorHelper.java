package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.api.world.processor.StructureEntityProcessorBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.storage.ValueInput;

import java.util.Optional;
import java.util.function.Function;

public interface IStructureProcessorHelper {
    StructureProcessorType<StructureProcessor> createStructureProcessorType(Function<ValueInput, Optional<StructureEntityProcessorBuilder.Processor>> function);

    static CompoundTag merge(CompoundTag into, CompoundTag from) {
        for (var entry : from.entrySet()) {
            var key = entry.getKey();
            var tag = entry.getValue();
            if (tag instanceof CompoundTag childFrom && into.get(key) instanceof CompoundTag childInto) {
                tag = merge(childInto, childFrom);
            }
            into.put(key, tag);
        }
        return into;
    }
}
