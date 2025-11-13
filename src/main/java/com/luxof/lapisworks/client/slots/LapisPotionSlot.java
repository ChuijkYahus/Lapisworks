package com.luxof.lapisworks.client.slots;

import com.luxof.lapisworks.recipes.BrewingRec;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class LapisPotionSlot extends Slot {
    public World world;


    public LapisPotionSlot(World world, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.world = world;
    }

    // BrewingStandScreenHandler$PotionSlot isn't visible :broken_heart:
    @Override
    public boolean canInsert(ItemStack stack) {
        return matches(stack);
    }

    public boolean matches(ItemStack stack) {
        for (BrewingRec rec : world.getRecipeManager().listAllOfType(BrewingRec.Type.INSTANCE)) {
            if (rec.isItemBrew()) {
                return rec.getFrom().left().get().ingredient.test(stack);
            } else {
                return PotionUtil.getPotion(stack) == Potion.byId(rec.getFrom().right().get());
            }
        }
        return false;
    }
}
