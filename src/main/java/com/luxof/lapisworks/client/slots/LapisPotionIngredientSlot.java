package com.luxof.lapisworks.client.slots;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import com.luxof.lapisworks.recipes.BrewingRec;
import com.luxof.lapisworks.recipes.BrewingRec.BrewerIngredientWithCount;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class LapisPotionIngredientSlot extends Slot {
    public World world;

    public LapisPotionIngredientSlot(World world, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.world = world;
    }
    
    @Override
    public boolean canInsert(ItemStack stack) {
        return matches(stack);
    }

    public boolean matches(ItemStack stack) {
        for (BrewingRec rec : world.getRecipeManager().listAllOfType(BrewingRec.Type.INSTANCE)) {
            LOGGER.info("testing recipe " + rec.getId());
            for (BrewerIngredientWithCount ing : rec.getPossibleInputs()) {
                LOGGER.info("testing ing " + ing.ingredient.toString());
                if (ing.ingredient.test(stack)) return true;
            }
            LOGGER.info("nope");
        }
        return false;
    }
}
