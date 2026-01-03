package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.LapisPersistentState;
import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;
import com.luxof.lapisworks.mixinsupport.OneTimeRitualsControl;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess, AttachmentTarget, OneTimeRitualsControl {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef,
            DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry,
            Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess,
            int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess,
                maxChainedNeighborUpdates);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

        ServerWorld thisWorld = (ServerWorld)(Object)this;
        LapisPersistentState state = LapisPersistentState.getState(thisWorld);
        ArrayList<OneTimeRitualExecutionState> rituals = state.getRituals(thisWorld);

        for (int i = rituals.size() - 1; i >= 0; i--) {
            if (!rituals.get(i).tick(thisWorld)) rituals.remove(i);
            state.markDirty();
        }

    }

    @Override
    public void addRitual(OneTimeRitualExecutionState ritual) {
        ServerWorld thisWorld = (ServerWorld)(Object)this;

        LapisPersistentState state = LapisPersistentState.getState(thisWorld);
        state.getRituals(thisWorld).add(ritual);
        state.markDirty();
    }
}
