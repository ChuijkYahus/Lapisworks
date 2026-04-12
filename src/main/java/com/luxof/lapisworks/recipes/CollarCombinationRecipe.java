package com.luxof.lapisworks.recipes;

import com.luxof.lapisworks.client.collar.LapisCollarAddition;
import com.luxof.lapisworks.client.collar.LapisCollarAdditions;

import static com.luxof.lapisworks.init.ModItems.COLLAR;

import java.util.List;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CollarCombinationRecipe extends SpecialCraftingRecipe {

    public CollarCombinationRecipe(Identifier id) {
        super(id, CraftingRecipeCategory.MISC);
    }

    public static class Type implements RecipeType<CollarCombinationRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack base = null;

        for (ItemStack stack : inventory.getInputStacks()) {
            if (!stack.isOf(COLLAR)) continue;
            base = stack;
            break;
        }

        if (base == null)
            return ItemStack.EMPTY;

        List<Identifier> existingAdditions = COLLAR.getAdditions(base);

        for (ItemStack stack : inventory.getInputStacks()) {
            Identifier additionId = null;
            LapisCollarAddition addition = null;

            for (var entry : LapisCollarAdditions.getAll().entrySet()) {
                Identifier id = entry.getKey();
                LapisCollarAddition testAddy = entry.getValue();
                if (
                    !testAddy.testItem(stack.getItem()) || !testAddy.canAdd(base, existingAdditions, id)
                ) return ItemStack.EMPTY;

                additionId = entry.getKey();
                addition = entry.getValue();
                break;
            }

            if (additionId == null || addition == null || existingAdditions.contains(additionId))
                continue;

            base = addition.craft(base, existingAdditions, stack, additionId);
            existingAdditions = COLLAR.getAdditions(base);
        }

        return base;
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        ItemStack base = null;
        List<Identifier> existingAdditions = null;
        boolean combiningWith = false;

        for (ItemStack stack : inventory.getInputStacks()) {
            if (!stack.isOf(COLLAR)) continue;
            base = stack;
            existingAdditions = COLLAR.getAdditions(stack);
            break;
        }
        if (base == null) return false;

        for (ItemStack stack : inventory.getInputStacks()) {
            for (var entry : LapisCollarAdditions.getAll().entrySet()) {
                Identifier id = entry.getKey();
                LapisCollarAddition addition = entry.getValue();

                combiningWith = addition.testItem(stack.getItem())
                    && addition.canAdd(base, existingAdditions, id);

                if (combiningWith) break;
            }
            if (combiningWith) break;
        }

        return combiningWith;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CollarCombinationRecipeSerializer.INSTANCE;
    }
    @Override
    public boolean fits(int width, int height) {
        return true;
    }
}
