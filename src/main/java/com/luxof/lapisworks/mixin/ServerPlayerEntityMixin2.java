package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;
import com.luxof.lapisworks.mixinsupport.ChariotServerPlayer;

import com.mojang.authlib.GameProfile;

import java.util.ArrayList;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin2 extends PlayerEntity implements ChariotServerPlayer {
    public ServerPlayerEntityMixin2(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    ArrayList<Amalgamation> amalgamations = new ArrayList<>();
    
    @Override @Unique
    public ArrayList<Amalgamation> getFusedAmalgamations() {
        return amalgamations;
    }
}
