package com.luxof.lapisworks.mixin;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.luxof.lapisworks.mixinsupport.ArtMindInterface;

import net.minecraft.entity.passive.VillagerEntity;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin implements ArtMindInterface {
    public float usedMindPercentage = 0.0f;
    public int mindBeingUsedTicks = 0;
    public int dontUseAgainTicks = 0;
    @Override public float getUsedMindPercentage() { return this.usedMindPercentage; }
    @Override public void setUsedMindPercentage(float val) { this.usedMindPercentage = val; }
    @Override public void incUsedMindPercentage(float amount) { this.usedMindPercentage += amount; }
    @Override public int getMindBeingUsedTicks() { return this.mindBeingUsedTicks; }
    @Override public void setMindBeingUsedTicks(int val) { this.mindBeingUsedTicks = val; }
    @Override public void incMindBeingUsedTicks(int amount) { this.mindBeingUsedTicks += amount; }
    @Override public void setDontUseAgainTicks(int ticks) { this.dontUseAgainTicks = ticks; }
    @Override public void incDontUseAgainTicks(int ticks) { this.dontUseAgainTicks += ticks; }
    @Override public int getDontUseAgainTicks() { return this.dontUseAgainTicks; }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        // must have cooldown or else heals at same rate as damaged
        if (this.getMindBeingUsedTicks() == 0) {
            if (this.getUsedMindPercentage() > 0.0f) {
                // magic numbers: goes down by 100% over a 2 minute period
                this.incUsedMindPercentage(-(100f / (120f * 20f)));
            }
        } else if (this.getMindBeingUsedTicks() < 0) {
            // if you have a use case for this, hmu and tell me to make it fuck off
            LOGGER.warn("why was mindBeingUsedTicks below 0?");
            this.setMindBeingUsedTicks(0);
        } else {
            this.incMindBeingUsedTicks(-1);
        }
        if (this.getDontUseAgainTicks() > 0) {
            this.incDontUseAgainTicks(-1);
        }
    }
}
