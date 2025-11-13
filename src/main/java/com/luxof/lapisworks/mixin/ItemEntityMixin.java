package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.ItemEntityMinterface;

import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin implements ItemEntityMinterface {
    public BlockPos cradlePos = null;

    @Override public void setBlockPosOfCradle(BlockPos blockPos) { cradlePos = blockPos; }
    @Override public BlockPos getBlockPosOfCradle() { return cradlePos; }
    
    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbt(NbtCompound compound, CallbackInfo ci) {
        if (!compound.contains("cradlePosX")) return;
        cradlePos = new BlockPos(
            compound.getInt("cradlePosX"),
            compound.getInt("cradlePosY"),
            compound.getInt("cradlePosZ")
        );
    }
    
    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbt(NbtCompound compound, CallbackInfo ci) {
        if (cradlePos == null) return;
        compound.putInt("cradlePosX", cradlePos.getX());
        compound.putInt("cradlePosY", cradlePos.getY());
        compound.putInt("cradlePosZ", cradlePos.getZ());
    }
}
