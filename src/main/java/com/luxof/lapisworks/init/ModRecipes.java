package com.luxof.lapisworks.init;

import com.luxof.lapisworks.recipes.BrewItemRecSerializer;
import com.luxof.lapisworks.recipes.BreweryRecipe;
import com.luxof.lapisworks.recipes.BrewingRecSerializer;
import com.luxof.lapisworks.recipes.ImbuementRec;
import com.luxof.lapisworks.recipes.ImbuementRecSerializer;
import com.luxof.lapisworks.recipes.MoldRec;
import com.luxof.lapisworks.recipes.MoldRecSerializer;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final Identifier IMBUEMENT_RECIPE_ID = id("amel_imbuement");
    public static final Identifier MOLD_AMEL_RECIPE_ID = id("mold_amel");
    public static final Identifier BREWING_RECIPE_ID = id("brewery");
    public static final Identifier BREWING_ITEM_RECIPE_ID = id("brewery_item");

    /** warcrimes will not be told */
    public static void apologizeForWarcrimes() {
        registerSerializer(IMBUEMENT_RECIPE_ID, ImbuementRecSerializer.INSTANCE);
        registerType(IMBUEMENT_RECIPE_ID, ImbuementRec.Type.INSTANCE);

        registerSerializer(MOLD_AMEL_RECIPE_ID, MoldRecSerializer.INSTANCE);
        registerType(MOLD_AMEL_RECIPE_ID, MoldRec.Type.INSTANCE);

        registerSerializer(BREWING_RECIPE_ID, BrewingRecSerializer.INSTANCE);
        registerType(BREWING_RECIPE_ID, BreweryRecipe.Type.INSTANCE);

        registerSerializer(BREWING_ITEM_RECIPE_ID, BrewItemRecSerializer.INSTANCE);
        //registerType(BREWING_ITEM_RECIPE_ID, BreweryRecipe.Type.INSTANCE);
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
