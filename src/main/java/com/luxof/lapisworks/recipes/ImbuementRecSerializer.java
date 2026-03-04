package com.luxof.lapisworks.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import com.luxof.lapisworks.recipes.ImbuementRec.ImbuementRecJsonFormat;
import com.luxof.lapisworks.recipes.ImbuementRec.ImbuementRecModJsonFormat;

import static com.luxof.lapisworks.Lapisworks.verDifference;

import java.util.List;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ImbuementRecSerializer implements RecipeSerializer<ImbuementRec> {
    private ImbuementRecSerializer() {}
    public static final ImbuementRecSerializer INSTANCE = new ImbuementRecSerializer();

    private static boolean modsLoaded(Identifier recipeId, List<JsonObject> mods) {
        for (JsonObject obj : mods) {
            ImbuementRecModJsonFormat mod = new Gson().fromJson(obj, ImbuementRecModJsonFormat.class);
            if (!FabricLoader.getInstance().isModLoaded(mod.id)) return false;
            if (mod.min == null) continue;
            Integer diff = verDifference(mod.id, mod.min);
            if (diff == null) throw new JsonSyntaxException("Invalid version (" + mod.min + ") for mod \"" + mod.id + "\" in recipe " + recipeId.toString());
            else if (diff < 0) return false;
        }
        return true;
    }

    @Override
    public ImbuementRec read(Identifier id, JsonObject json) {
        ImbuementRecJsonFormat recipeJson = new Gson().fromJson(json, ImbuementRecJsonFormat.class);
        if (recipeJson.normal == null) {
            throw new JsonSyntaxException("Specify a motherfuckin' start, cuh. (" + id + ")");
        } else if (recipeJson.fullamel == null) {
            throw new JsonSyntaxException("The journey may not always be about the destination, but this ain't no journey. Specify a fullamel. (" + id + ")");
        } else if (recipeJson.cost == null) {
            throw new JsonSyntaxException("You didn't specify a cost. (" + id + ")");
        }
        if (recipeJson.requiredmod != null &&
            !modsLoaded(id, recipeJson.requiredmod)) {
            return new ImbuementRec(
                id,
                false,
                Ingredient.ofItems(Items.AIR),
                Items.AIR,
                Items.AIR,
                0
            );
        }
        Ingredient normal = Ingredient.fromJson(recipeJson.normal);
        Item partAmel;
        try {
            partAmel = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.partamel)).orElseGet(() -> null);
        } catch (NullPointerException e) { partAmel = null; }
        Item fullAmel = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.fullamel))
            .orElseThrow(() -> new JsonSyntaxException("No such item (fullamel): " + recipeJson.fullamel));

        return new ImbuementRec(id, true, normal, partAmel, fullAmel, recipeJson.cost);
    }

    @Override
    public ImbuementRec read(Identifier id, PacketByteBuf buf) {
        return new ImbuementRec(
            id,
            buf.readBoolean(),
            Ingredient.fromPacket(buf),
            buf.readBoolean() ? Registries.ITEM.get(buf.readIdentifier()) : null,
            Registries.ITEM.get(buf.readIdentifier()),
            buf.readInt()
        );
    }

    @Override
    public void write(PacketByteBuf buf, ImbuementRec recipe) {
        buf.writeBoolean(recipe.getRequiredModIsLoaded());
        recipe.getNormal().write(buf);
        buf.writeBoolean(recipe.getPartAmel() != null);
        if (recipe.getPartAmel() != null) {
            buf.writeIdentifier(Registries.ITEM.getId(recipe.getPartAmel()));
        }
        buf.writeIdentifier(Registries.ITEM.getId(recipe.getFullAmel()));
        buf.writeInt(recipe.getFullAmelsCost());
    }
}
