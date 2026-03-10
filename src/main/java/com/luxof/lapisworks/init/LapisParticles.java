package com.luxof.lapisworks.init;

import com.luxof.lapisworks.client.particles.*;

import static com.luxof.lapisworks.Lapisworks.id;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.PendingParticleFactory;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LapisParticles {
    public static final DefaultParticleType FLOATING_ENCHANT = FabricParticleTypes.simple();
    public static final DefaultParticleType AMETHYST_DUST = FabricParticleTypes.simple();

    public static void pawtickle() {
        register(FLOATING_ENCHANT, "floating_enchant");
        register(AMETHYST_DUST, "amethyst_dust");
    }
    public static void clientTicklesPaw() {
        register(FLOATING_ENCHANT, FloatingEnchant.FloatingEnchantFactory::new);
        register(AMETHYST_DUST, AmethystDust.AmethystDustFactory::new);
    }

    private static void register(
        ParticleType<?> particleType,
        String name
    ) {
        Registry.register(Registries.PARTICLE_TYPE, id(name), particleType);
    }
    private static <T extends ParticleEffect> void register(
        ParticleType<T> type,
        PendingParticleFactory<T> factory
    ) {
        ParticleFactoryRegistry.getInstance().register(
            type,
            factory
        );
    }
}
