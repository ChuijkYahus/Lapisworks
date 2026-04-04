package com.luxof.lapisworks.items;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.items.shit.ITotem;

import dev.emi.trinkets.api.SlotReference;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TotemNecklace extends Item implements ITotem {
    public TotemNecklace() {
        super(
            new FabricItemSettings()
                .maxCount(1)
                .maxDamage(3)
        );
    }

    @Override
    public ItemStack getFloatingItemToShowClientPlayerOnRevive(
        ItemStack stack,
        SlotReference slot
    ) {
        return new ItemStack(ModItems.TOTEM_NECKLACE_FLOATY_DISPLAY);
    }
}
