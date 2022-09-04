package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.module.*;

public class FabricModulesLoader implements IModulesLoader {
    @Override
    public void loadModLoaderDependentModules() {
        StructurePoolElementTypeModuleFabric.init();
        CriteriaModuleFabric.init();
        SoundEventModuleFabric.init();
        StructureFeatureModuleFabric.init();
        FeatureModuleFabric.init();
        ConfiguredFeatureModuleFabric.init();
        PlacedFeatureModuleFabric.init();
        CreativeModeTabModuleFabric.init();
        ItemModuleFabric.init();
        BlockModuleFabric.init();
        StructureProcessorTypeModuleFabric.init();
        BlockEntityTypeModuleFabric.init();
        BiomeModuleFabric.init();
        EntityTypeModuleFabric.init();
        MobEffectModuleFabric.init();
        PotionModuleFabric.init();
        ParticleTypeModuleFabric.init();
    }
}
