//package com.yungnickyoung.minecraft.yungsapi.mixin;
//
//import com.mojang.serialization.Codec;
//import net.minecraft.world.level.chunk.ChunkGenerator;
//import net.minecraft.world.level.levelgen.StructureSettings;
//
//@Mixin(ChunkGenerator.class)
//public interface ChunkGeneratorAccessor {
//    @Accessor("settings")
//    void yungsapi_setSettings(StructureSettings structureSettings);
//
//    @Invoker("codec")
//    Codec<ChunkGenerator> yungsapi_getCodec();
//}
