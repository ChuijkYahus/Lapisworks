package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.interop.hextended.items.AmelOrb;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import java.util.List;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        try {
            if (nbt.getBoolean("LAPISWORKS_EnchSent_exists")) {
                Vec3d sentPos = new Vec3d(
                    nbt.getDouble("LAPISWORKS_EnchSent_posX"),
                    nbt.getDouble("LAPISWORKS_EnchSent_posY"),
                    nbt.getDouble("LAPISWORKS_EnchSent_posZ")
                );
                double sentAmbit = nbt.getDouble("LAPISWORKS_EnchSent_Ambit");
                ((EnchSentInterface)this).setEnchantedSentinel(sentPos, sentAmbit);
            }
        } catch (Exception e) {
            LOGGER.warn("Couldn't load enchanted sentinel!");
            e.printStackTrace();
        }
    }

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        Vec3d sentPos = ((EnchSentInterface)this).getEnchantedSentinel();
        Double ambit = ((EnchSentInterface)this).getEnchantedSentinelAmbit();
        nbt.putBoolean("LAPISWORKS_EnchSent_exists", sentPos != null);
        Vec3d usePos = sentPos != null ? sentPos : new Vec3d(0.0, 0.0, 0.0);
        double useAmbit = ambit != null ? ambit : 0.0;
        nbt.putDouble("LAPISWORKS_EnchSent_posX", usePos.x);
        nbt.putDouble("LAPISWORKS_EnchSent_posY", usePos.y);
        nbt.putDouble("LAPISWORKS_EnchSent_posZ", usePos.z);
        nbt.putDouble("LAPISWORKS_EnchSent_Ambit", useAmbit);
    }

    public Vec3d spawnAt = ((EnchSentInterface)this).getEnchantedSentinel();
    public ParticleSpray particles = null;
    public void spawnEnchSentParticles() {
        Vec3d sentinelPosition = ((EnchSentInterface)this).getEnchantedSentinel();
        if ((spawnAt == null && sentinelPosition != null) || spawnAt != sentinelPosition) {
            spawnAt = sentinelPosition;
            if (spawnAt != null) { particles = ParticleSpray.burst(spawnAt, 2, 1); }
        }
        else if (spawnAt == null) { return; }
        particles.sprayParticles(
            (ServerWorld)this.getWorld(), // is it as simple as this? do I not need the whole chain?
            // damn, it IS as simple as that.
            IXplatAbstractions.INSTANCE.getPigment((PlayerEntity)(Object)this)
        );
    }
    public void spawnOrbParticles() {
        List<Hand> hands = List.of(Hand.MAIN_HAND, Hand.OFF_HAND);
        for (Hand hand : hands) {
            ItemStack stack = this.getStackInHand(hand);
            if (!(stack.getItem() instanceof AmelOrb orb)) continue;
            Vec3d placeInAmbit = orb.getPlaceInAmbit(stack);
            ParticleSpray particles = ParticleSpray.burst(placeInAmbit, 3, 2);
            particles.sprayParticles(
                (ServerWorld)this.getWorld(),
                IXplatAbstractions.INSTANCE.getPigment((PlayerEntity)(Object)this)
            );
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        this.spawnEnchSentParticles();
        this.spawnOrbParticles();
    }
}
