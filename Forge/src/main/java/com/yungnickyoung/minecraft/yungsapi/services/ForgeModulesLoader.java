package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.*;

public class ForgeModulesLoader implements IModulesLoader {
    @Override
    public void loadModLoaderDependentModules() {
        StructurePoolElementTypeModuleForge.init();
        CriteriaModuleForge.init();
        SoundEventModuleForge.init();
        BlockModuleForge.init();
        FeatureModuleForge.init();
        ConfiguredFeatureModuleForge.init();
        PlacedFeatureModuleForge.init();
        StructureFeatureModuleForge.init();
        CreativeModeTabModuleForge.init();
        ItemModuleForge.init();
        BlockEntityTypeModuleForge.init();
        StructureProcessorTypeModuleForge.init();
        BiomeModuleForge.init();
        EntityTypeModuleForge.init();
        MobEffectModuleForge.init();
        PotionModuleForge.init();
        ParticleTypeModuleForge.init();
    }
}
