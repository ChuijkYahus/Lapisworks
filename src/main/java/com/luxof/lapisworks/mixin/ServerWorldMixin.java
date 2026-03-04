package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.PersistentStateRituals;
import com.luxof.lapisworks.init.PersistentStateRituals.IotaKey;
import com.luxof.lapisworks.mixinsupport.RitualsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess, AttachmentTarget, RitualsUtil {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef,
            DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry,
            Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess,
            int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess,
                maxChainedNeighborUpdates);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

        ServerWorld thisWorld = getWorld();
        PersistentStateRituals state = getState();
        ArrayList<OneTimeRitualExecutionState> rituals = state.getRituals(thisWorld);

        for (int i = rituals.size() - 1; i >= 0; i--) {
            if (!rituals.get(i).tick(thisWorld)) rituals.remove(i);
            state.markDirty();
        }

        for (var entry : getTuneables().entrySet()) {
            var poses = entry.getValue();

            for (int i = poses.size() - 1; i >= 0; i--) {

                // doesn't this chunkload?
                if (!thisWorld.getBlockState(poses.get(i)).isOf(ModBlocks.TUNEABLE_AMETHYST)) {
                    poses.remove(i);
                    state.markDirty();
                }

            }
        }

    }

    @Unique private ServerWorld getWorld() { return (ServerWorld)(Object)this; }
    @Unique private PersistentStateRituals getState() {
        return PersistentStateRituals.getState(getWorld());
    }

    @Unique @Override
    public ArrayList<OneTimeRitualExecutionState> getRituals() {
        ServerWorld thisWorld = getWorld();
        return getState().getRituals(thisWorld);
    }

    @Unique @Override
    public void addRitual(OneTimeRitualExecutionState ritual) {
        ServerWorld thisWorld = getWorld();

        PersistentStateRituals state = getState();
        state.getRituals(thisWorld).add(ritual);
        state.markDirty();
    }

    @Unique @Override
    public HashMap<IotaKey, ArrayList<BlockPos>> getTuneables() {
        ServerWorld thisWorld = getWorld();
        return getState().getTuneables(thisWorld);
    }

    @Unique @Override
    public ArrayList<BlockPos> getTuneables(Iota key) {
        ServerWorld thisWorld = getWorld();
        return getState().getTuneables(thisWorld, key);
    }

    @Unique @Override
    public void addTuneable(Iota key, BlockPos positionOfTuneable) {
        ServerWorld thisWorld = getWorld();
        PersistentStateRituals state = getState();

        var tuneables = state.getTuneables(thisWorld, key);
        tuneables.add(positionOfTuneable);
        state.markDirty();
    }

    @Unique @Override
    public void addTuneables(Iota key, ArrayList<BlockPos> positionsOfTuneables) {
        positionsOfTuneables.forEach(pos -> addTuneable(key, pos));
    }

    @Unique @Override
    public void removeTuneable(Iota previousKey, BlockPos positionOfTuneable) {
        getTuneables(previousKey).remove(positionOfTuneable);
        getState().markDirty();
    }
}
