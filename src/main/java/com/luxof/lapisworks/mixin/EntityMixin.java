package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.ArtMindInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface.AllEnchantments;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;

@Mixin(Entity.class)
public class EntityMixin {
	@Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
	public void isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof LivingEntity) {
            if (damageSource.isIn(DamageTypeTags.IS_FIRE) &&
                ((LapisworksInterface)this).getEnchant(AllEnchantments.fireResist) > 0) {
                cir.setReturnValue(true);
            }
        }
	}

    @Inject(at = @At("HEAD"), method = "readNbt")
	public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if ((Object)this instanceof LivingEntity) {
            // oh yeah, it's backwards compat time
            try {
                ((LapisworksInterface)this).setLapisworksAttributes(new AttributeContainer(
                    DefaultAttributeContainer.builder()
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, nbt.getDouble("LAPISWORKS_JUICED_FISTS"))
                    .add(EntityAttributes.GENERIC_ATTACK_SPEED, nbt.getDouble("LAPISWORKS_JUICED_DEX"))
                    .add(EntityAttributes.GENERIC_MAX_HEALTH, nbt.getDouble("LAPISWORKS_JUICED_SKIN"))
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, nbt.getDouble("LAPISWORKS_JUICED_FEET"))
                    .build()
                ));
            } catch (Exception e) {
                LOGGER.warn("JUST FOUND AN ERROR, COULDN'T LOAD JUICED ATTRS!");
                e.printStackTrace();
            }
            try {
                ((LapisworksInterface)this).setEnchantments(
                    nbt.getIntArray("LAPISWORKS_ENCHANTMENTS")
                );
            } catch (Exception e) {
                LOGGER.warn("couldn't load enchantments!!");
                e.printStackTrace();
            }

            if ((Object)this instanceof VillagerEntity) {
                try {
                    ((ArtMindInterface)this).setUsedMindPercentage(nbt.getFloat("LAPISWORKS_MIND_USED"));
                    ((ArtMindInterface)this).setMindBeingUsedTicks(nbt.getInt("LAPISWORKS_MIND_HEAL_COOLDOWN"));
                } catch (Exception e) {
                    LOGGER.warn("Couldn't load VillagerEntity shit!");
                    e.printStackTrace();
                }
            }
        }
	}

	@Inject(at = @At("HEAD"), method = "writeNbt")
	public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if ((Object)this instanceof LivingEntity) {
            AttributeContainer attrs = ((LapisworksInterface)this).getLapisworksAttributes();
            nbt.putDouble("LAPISWORKS_JUICED_FISTS", attrs.getBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
            nbt.putDouble("LAPISWORKS_JUICED_DEX", attrs.getBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED));
            nbt.putDouble("LAPISWORKS_JUICED_SKIN", attrs.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH));
            nbt.putDouble("LAPISWORKS_JUICED_FEET", attrs.getBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            nbt.putIntArray("LAPISWORKS_ENCHANTMENTS", ((LapisworksInterface)this).getEnchantments());
            if ((Object)this instanceof VillagerEntity) {
                nbt.putFloat("LAPISWORKS_MIND_USED", ((ArtMindInterface)this).getUsedMindPercentage());
                nbt.putInt("LAPISWORKS_MIND_HEAL_COOLDOWN", ((ArtMindInterface)this).getMindBeingUsedTicks());
            }
        }
	}
}
