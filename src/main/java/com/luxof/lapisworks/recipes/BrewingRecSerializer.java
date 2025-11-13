package com.luxof.lapisworks.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import com.luxof.lapisworks.recipes.BrewingRec.BrewerIngredientWithCount;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class BrewingRecSerializer implements RecipeSerializer<BrewingRec> {
    private BrewingRecSerializer() {}
    protected static Identifier currentId = null;

    public static final BrewingRecSerializer INSTANCE = new BrewingRecSerializer();

    // sorry.
    @Override
    public BrewingRec read(Identifier id, JsonObject json) {
        currentId = id;
        String brew = JsonHelper.getString(json, "brew");


        List<BrewerIngredientWithCount> input = JsonHelper.getArray(json, "input").asList().stream().map(
            BrewerIngredientWithCount::fromJson
        ).toList();


        if (brew.equals("item")) {
            BrewerIngredientWithCount from = BrewerIngredientWithCount.fromJson(
                JsonHelper.getObject(json, "from")
            );
            JsonObject toObject = JsonHelper.getObject(json, "to");
            ItemStack to = new ItemStack(
                Registries.ITEM.get(new Identifier(JsonHelper.getString(toObject, "item"))),
                JsonHelper.getInt(toObject, "count")
            );
            return new BrewingRec(id, from, input, to);


        } else if (brew.equals("potion")) {
            Identifier from = new Identifier(JsonHelper.getString(json, "from"));
            Identifier to = new Identifier(JsonHelper.getString(json, "to"));
            return new BrewingRec(id, from, input, to);


        } else {
            throw new JsonSyntaxException("Unknown brew type!: " + brew);
        }
    }

    @Override
    public BrewingRec read(Identifier id, PacketByteBuf buf) {
        boolean isItemBrew = buf.readBoolean();
        // :rolling_eyes:
        /*return new BrewingRec(
            id,
            isItemBrew ? BrewerIngredientWithCount.read(buf) : buf.readIdentifier(),
            buf.readList(BrewerIngredientWithCount::read),
            isItemBrew ? buf.readItemStack() : buf.readIdentifier()
        )*/
        return isItemBrew ? new BrewingRec(
            id,
            BrewerIngredientWithCount.read(buf),
            buf.readList(BrewerIngredientWithCount::read),
            buf.readItemStack()
        ) : new BrewingRec(
            id,
            buf.readIdentifier(),
            buf.readList(BrewerIngredientWithCount::read),
            buf.readIdentifier()
        );
    }

    @Override
    public void write(PacketByteBuf buf, BrewingRec recipe) {
        List<BrewerIngredientWithCount> inputs = recipe.getPossibleInputs();

        buf.writeBoolean(recipe.isItemBrew());
        if (recipe.isItemBrew()) {
            recipe.getFrom().left().get().write(buf);
            buf.writeCollection(inputs, BrewerIngredientWithCount::write);
            buf.writeItemStack(recipe.getOutput().left().get());
        } else {
            buf.writeIdentifier(new Identifier(recipe.getFrom().right().get()));
            buf.writeCollection(inputs, BrewerIngredientWithCount::write);
            buf.writeIdentifier(new Identifier(recipe.getOutput().right().get()));
        }
    }
}
