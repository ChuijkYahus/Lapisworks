package com.luxof.lapisworks.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;

public class IngredientWithCount {
    private final Ingredient ingredient;
    private final int amount;
    public IngredientWithCount(Ingredient ing, int am) {
        this.ingredient = ing;
        this.amount = am;
    }
    public static IngredientWithCount fromJson(JsonObject json) {
        return new IngredientWithCount(
            Ingredient.fromJson(json),
            JsonHelper.getInt(json, "count")
        );
    }

    public static IngredientWithCount fromJson(JsonElement json) {
        return fromJson(json.getAsJsonObject());
    }

    public static IngredientWithCount read(PacketByteBuf buf) {
        return new IngredientWithCount(
            Ingredient.fromPacket(buf),
            buf.readInt()
        );
    }

    public void write(PacketByteBuf buf) {
        ingredient.write(buf);
        buf.writeInt(amount);
    }

    public static void write(PacketByteBuf buf, IngredientWithCount ing) {
        ing.ingredient.write(buf);
        buf.writeInt(ing.amount);
    }

    public boolean test(ItemStack stack) { return this.ingredient.test(stack); }
    public int getCount() { return this.amount; }

    public boolean testWithCount(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= getCount();
    }
    public boolean equals(IngredientWithCount other) {
        return ingredient.equals(other.ingredient) && amount == other.amount;
    }
}
