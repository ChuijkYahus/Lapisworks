package com.luxof.lapisworks.recipes;

import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ImbuementRec implements Recipe<HandsInv> {
    private final Identifier id;
    private final boolean requiredModIsLoaded;
    private final Ingredient normal;
    private final Item partAmel;
    private final Item fullAmel;
    private final int cost;

    public static class Type implements RecipeType<ImbuementRec> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }
    
    protected class ImbuementRecJsonFormat {
        JsonObject normal;
        List<JsonObject> requiredmod;
        String partamel;
        String fullamel;
        Integer cost;
    }
    protected class ImbuementRecModJsonFormat {
        String id;
        String min;
    }

    public ImbuementRec(
        Identifier id,
        boolean requiredModIsLoaded,
        Ingredient norm,
        Item partAmel,
        Item fullAmel,
        int cost
    ) {
        this.id = id;
        this.requiredModIsLoaded = requiredModIsLoaded;
        this.normal = norm;
        this.partAmel = partAmel;
        this.fullAmel = fullAmel;
        this.cost = cost;
    }

    @Override
    public Identifier getId() { return this.id; }
    public boolean getRequiredModIsLoaded() { return this.requiredModIsLoaded; }
    public Ingredient getNormal() { return this.normal; }
    public Item getPartAmel() { return this.partAmel; }
    public Item getFullAmel() { return this.fullAmel; }
    /** the cost (in amel) to make the full amel product from <code>normal</code>.
     * you could also call it the "base cost".
     * <p>for oak staff, it'd return 10 since you need 10 amel to make an amel staff from an oak staff. */
    public int getFullAmelsCost() { return this.cost; }
    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return new ItemStack(this.getFullAmel());
    }

    @Override
    public ItemStack craft(HandsInv inventory, DynamicRegistryManager registryManager) {
        return this.getOutput(registryManager).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() { return ImbuementRecSerializer.INSTANCE; }

    @Override
    public RecipeType<?> getType() { return Type.INSTANCE; }

    @Override
    public boolean matches(HandsInv inventory, World world) {
        if (!this.requiredModIsLoaded) return false;
        boolean ret = false;
        for (ItemStack stack : inventory.getHands()) {
            ret = ret || normal.test(stack) || partAmel == stack.getItem();
        }
        return ret;
    }
}
