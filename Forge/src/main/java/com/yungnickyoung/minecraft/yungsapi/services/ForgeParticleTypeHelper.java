package com.yungnickyoung.minecraft.yungsapi.services;

import net.minecraft.core.particles.SimpleParticleType;

public class ForgeParticleTypeHelper implements IParticleTypeHelper {
    @Override
    public SimpleParticleType simple(boolean alwaysSpawn) {
        return new SimpleParticleType(alwaysSpawn);
    }
}
