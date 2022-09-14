package com.yungnickyoung.minecraft.yungsapi.services;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class FabricParticleTypeHelper implements IParticleTypeHelper {
    @Override
    public SimpleParticleType simple(boolean alwaysSpawn) {
        return FabricParticleTypes.simple(alwaysSpawn);
    }
}
