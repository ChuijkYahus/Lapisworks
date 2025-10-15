package com.luxof.lapisworks.init;

import static com.luxof.lapisworks.LapisworksIDs.IMBUEMENT_RECIPE_ID;
import static com.luxof.lapisworks.LapisworksIDs.MOLD_AMEL_RECIPE_ID;

import com.luxof.lapisworks.recipes.ImbuementRec;
import com.luxof.lapisworks.recipes.ImbuementRecSerializer;
import com.luxof.lapisworks.recipes.MoldRec;
import com.luxof.lapisworks.recipes.MoldRecSerializer;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static void apologizeForWarcrimes() {
        registerSerializer(IMBUEMENT_RECIPE_ID, ImbuementRecSerializer.INSTANCE);
        registerType(IMBUEMENT_RECIPE_ID, ImbuementRec.Type.INSTANCE);
        registerSerializer(MOLD_AMEL_RECIPE_ID, MoldRecSerializer.INSTANCE);
        registerType(MOLD_AMEL_RECIPE_ID, MoldRec.Type.INSTANCE);
    }

    public static void registerSerializer(
        Identifier ID,
        RecipeSerializer<?> serializerInstance
    ) { Registry.register(Registries.RECIPE_SERIALIZER, ID, serializerInstance); }
    public static void registerType(
        Identifier ID,
        RecipeType<?> typeInstance
    ) { Registry.register(Registries.RECIPE_TYPE, ID, typeInstance); }
}
