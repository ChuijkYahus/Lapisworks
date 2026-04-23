package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.SpiralPatternsClearable;

import com.mojang.authlib.GameProfile;

import at.petrak.hexcasting.xplat.IClientXplatAbstractions;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity implements SpiralPatternsClearable {
    public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Override
    public void setSpiralPatternsClearing(boolean yesOrNo) {
        ((SpiralPatternsClearable)(Object)IClientXplatAbstractions.INSTANCE
            .getClientCastingStack(this))
            .setSpiralPatternsClearing(yesOrNo);
    }

    @Override
    public boolean getSpiralPatternsClearing() {
        return ((SpiralPatternsClearable)(Object)IClientXplatAbstractions.INSTANCE
            .getClientCastingStack(this))
            .getSpiralPatternsClearing();
    }
}
