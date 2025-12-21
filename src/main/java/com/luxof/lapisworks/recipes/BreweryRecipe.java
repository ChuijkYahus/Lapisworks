package com.luxof.lapisworks.recipes;

import com.luxof.lapisworks.inv.BrewerInv;

import java.util.List;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

import org.jetbrains.annotations.Nullable;

// convenience and shit
public interface BreweryRecipe extends Recipe<BrewerInv> {
    public static class Type implements RecipeType<BreweryRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }

    public List<IngredientWithCount> getPossibleCatalysts();
    public boolean inputMatches(ItemStack stack);
    public ItemStack craft(ItemStack stack);

    /** attempts to return the amount of this specific catalyst this recipe takes for one batch. */
    @Nullable
    default public Integer getNeededAmountOfCatalyst(ItemStack catalystInQuestion) {
        Optional<IngredientWithCount> maybeIng = getPossibleCatalysts().stream()
            .filter(catalyst -> catalyst.testWithCount(catalystInQuestion))
            .findFirst();
        return maybeIng.isPresent() ? maybeIng.get().getCount() : null;
    }
    /** returns whether or not this catalyst is useable in this recipe. */
    default public boolean catalystMatches(ItemStack catalystInQuestion) {
        return getNeededAmountOfCatalyst(catalystInQuestion) != null;
    }
    /** Basically a macro.
     * <p><code>return inputMatches(stack) ? craft(stack) : stack</code> */
    default public ItemStack craftIfMatchesInput(ItemStack stack) {
        return inputMatches(stack) ? craft(stack) : stack;
    }
    default public RecipeType<?> getType() { return BreweryRecipe.Type.INSTANCE; }
}
