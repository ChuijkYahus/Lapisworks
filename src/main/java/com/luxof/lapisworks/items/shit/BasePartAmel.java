package com.luxof.lapisworks.items.shit;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface BasePartAmel {
    /** ran every time the Imbue Amel spell imbues amel into this item.
     * <br>the NBT thing is set already when this is called. */
    public void onImbue(ItemStack stack, int addedAmel);

    /** at the moment? i think this is broken. haven't checked. */
    default void onAttack(LivingEntity user, World world, Hand hand, Entity attacked) {};
}
