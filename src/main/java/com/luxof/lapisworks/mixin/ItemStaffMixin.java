package com.luxof.lapisworks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.luxof.lapisworks.items.shit.PartiallyAmelInterface;

import at.petrak.hexcasting.common.items.ItemStaff;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Mixin(ItemStaff.class)
public class ItemStaffMixin {
    @Inject(at = @At("HEAD"), method = "use")
    public void use(
        World world,
        PlayerEntity user,
        Hand hand,
        CallbackInfoReturnable<TypedActionResult<ItemStack>> cir
    ) {
        if ((Object)this instanceof PartiallyAmelInterface) {
            ((PartiallyAmelInterface)(Object)this).specialUseBehaviour(user, world, hand);
        }
    }
}
