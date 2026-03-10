package com.luxof.lapisworks.mixin;


import com.luxof.lapisworks.interop.valkyrienskies.ValkyrienUtils;

import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// "you know you can just use Cardinal Components for this-"
// Metal Gear Rising: Revengeance OST It Has To Be This Way Extended
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements EnchSentInterface {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

    @Unique private Vec3d enchSentPos = null;
    @Unique private Double sentRange = null;

    @Unique @Override @Nullable public Vec3d getEnchantedSentinel() { return this.enchSentPos; }
    @Unique @Override @Nullable public Double getEnchantedSentinelAmbit() { return this.sentRange; }
    @Unique @Override
    public void setEnchantedSentinel(Vec3d pos, Double ambit) {
        this.enchSentPos = pos;
        this.sentRange = ambit;
    }
    @Unique @Override
    public boolean shouldBreakSent() {
        double casterAmbit = ((LivingEntity)this).getAttributeValue(HexAttributes.AMBIT_RADIUS);
        return this.getEnchantedSentinel() == null ?
            false :
                (FabricLoader.getInstance().isModLoaded("valkyrienskies") ?
                        ValkyrienUtils.distance(getWorld(), this.getPos(), this.getEnchantedSentinel()) :
                        this.getPos().distanceTo(this.getEnchantedSentinel())) > casterAmbit;
    }
    @Unique @Override
    public void breakSent() {
        this.setEnchantedSentinel(null, null);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (this.shouldBreakSent()) { this.breakSent(); }
    }

    // mojang............
    @Inject(
        at = @At("TAIL"),
        method = "readCustomDataFromNbt"
    )
    public void lapisworks$JUSTFUCKINGCARRYTHESPEEDYOUTWAT(NbtCompound nbt, CallbackInfo ci) {
        var speed = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        speed.setBaseValue(
            speed.getBaseValue() +
            ((LapisworksInterface)this)
                .getAmountOfAttrJuicedUpByAmel(EntityAttributes.GENERIC_MOVEMENT_SPEED)
        );
    }
}
