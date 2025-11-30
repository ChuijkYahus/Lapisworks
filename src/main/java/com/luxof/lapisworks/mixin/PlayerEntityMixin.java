package com.luxof.lapisworks.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// "you know you can just use Cardinal Components for this-"
// Metal Gear Rising: Revengeance OST It Has To Be This Way Extended
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements EnchSentInterface {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

    @Unique public Vec3d enchSentPos = null;
    @Unique public Double sentRange = null;

    @Unique @Override @Nullable public Vec3d getEnchantedSentinel() { return this.enchSentPos; }
    @Unique @Override @Nullable public Double getEnchantedSentinelAmbit() { return this.sentRange; }
    @Unique @Override
    public void setEnchantedSentinel(Vec3d pos, Double ambit) {
        this.enchSentPos = pos;
        this.sentRange = ambit;
    }
    @Unique @Override
    public boolean shouldBreakSent() {
        return this.getEnchantedSentinel() == null ?
            false :
            this.getPos().distanceTo(this.getEnchantedSentinel()) > 32.0;
    }
    @Unique @Override
    public void breakSent() {
        this.setEnchantedSentinel(null, null);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (this.shouldBreakSent()) { this.breakSent(); }
    }
}
