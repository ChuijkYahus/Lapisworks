package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.utils.NBTHelper;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.luxof.lapisworks.mixinsupport.LapisworksInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface.AllEnchantments;
import com.luxof.lapisworks.interop.hexical.EntityDimensionsButTheHitboxIsDown;

import static com.luxof.lapisworks.LapisworksIDs.IS_IN_CRADLE;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.Box;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    // why can't i just inject in ItemEntityMixin bruh
    @ModifyReturnValue(method = "getDimensions", at = @At("RETURN"))
    private EntityDimensions lapisworks$getDimensions(EntityDimensions og) {
        if (!((Object)this instanceof ItemEntity itemEntity)) return og;

        if (!NBTHelper.contains(itemEntity.getStack(), IS_IN_CRADLE)) return og;

        return new EntityDimensionsButTheHitboxIsDown(1.0F, 1.0F);
    }

    @ModifyReturnValue(method = "getBoundingBox", at = @At("RETURN"))
    private Box lapisworks$getBoundingBox(Box og) {
        if (!((Object)this instanceof ItemEntity itemEntity)) return og;

        if (!NBTHelper.contains(itemEntity.getStack(), IS_IN_CRADLE)) return og;

        return itemEntity.getDimensions(itemEntity.getPose()).getBoxAt(itemEntity.getPos());
    }
}
