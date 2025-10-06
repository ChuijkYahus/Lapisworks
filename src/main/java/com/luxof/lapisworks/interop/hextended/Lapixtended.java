package com.luxof.lapisworks.interop.hextended;

import abilliontrillionstars.hextended.LanisHextendedStaves;

import com.luxof.lapisworks.init.Mutables;
import com.luxof.lapisworks.interop.hextended.items.AmelOrb;
import com.luxof.lapisworks.interop.hextended.items.AmelWand;
import com.luxof.lapisworks.interop.hextended.items.PartAmelWand;
import com.luxof.lapisworks.items.shit.FullyAmelInterface;
import com.luxof.lapisworks.items.shit.PartiallyAmelInterface;
import com.luxof.lapisworks.items.PartiallyAmelStaff;

import static com.luxof.lapisworks.init.ModItems.registerItem;

import com.mojang.datafixers.util.Pair;

import java.util.Map;

import static java.util.Map.entry;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

/* Lapixtended? Lapistended? Hextended Lapis?
 * maybe Lapix/Lapistended for Hextended Gear stuff added by Lapisworks
 * and Hextended Lapis for Lapisworks stuff added by Hextended Gear
 * that makes sense i think (at least in my head) */
public class Lapixtended {
    public static final Item PARTAMEL_ACACIA_WAND = registerItem("staves/incomplete/acacia_wand", new PartAmelWand());
    public static final Item PARTAMEL_BAMBOO_WAND = registerItem("staves/incomplete/bamboo_wand", new PartAmelWand());
    public static final Item PARTAMEL_BIRCH_WAND = registerItem("staves/incomplete/birch_wand", new PartAmelWand());
    public static final Item PARTAMEL_CHERRY_WAND = registerItem("staves/incomplete/cherry_wand", new PartAmelWand());
    public static final Item PARTAMEL_CRIMSON_WAND = registerItem("staves/incomplete/crimson_wand", new PartAmelWand());
    public static final Item PARTAMEL_DARK_OAK_WAND = registerItem("staves/incomplete/dark_oak_wand", new PartAmelWand());
    public static final Item PARTAMEL_EDIFIED_WAND = registerItem("staves/incomplete/edified_wand", new PartAmelWand());
    public static final Item PARTAMEL_JUNGLE_WAND = registerItem("staves/incomplete/jungle_wand", new PartAmelWand());
    public static final Item PARTAMEL_MANGROVE_WAND = registerItem("staves/incomplete/mangrove_wand", new PartAmelWand());
    public static final Item PARTAMEL_MINDSPLICE_WAND = registerItem("staves/incomplete/mindsplice_wand", new PartAmelWand());
    public static final Item PARTAMEL_OAK_WAND = registerItem("staves/incomplete/oak_wand", new PartAmelWand());
    public static final Item PARTAMEL_SPRUCE_WAND = registerItem("staves/incomplete/spruce_wand", new PartAmelWand());
    public static final Item PARTAMEL_WARPED_WAND = registerItem("staves/incomplete/warped_wand", new PartAmelWand());
    public static final Item PARTAMEL_MOSS = registerItem("staves/incomplete/moss", new PartiallyAmelStaff());
    public static final Item PARTAMEL_MOSS_WAND = registerItem("staves/incomplete/moss_wand", new PartAmelWand());
    public static final Item PARTAMEL_FLOWERED_MOSS = registerItem("staves/incomplete/flowered_moss", new PartiallyAmelStaff());
    public static final Item PARTAMEL_FLOWERED_MOSS_WAND = registerItem("staves/incomplete/flowered_moss_wand", new PartAmelWand());
    public static final Item PARTAMEL_PRISMARINE = registerItem("staves/incomplete/prismarine", new PartiallyAmelStaff());
    public static final Item PARTAMEL_PRISMARINE_WAND = registerItem("staves/incomplete/prismarine_wand", new PartAmelWand());
    public static final Item PARTAMEL_DARK_PRISMARINE = registerItem("staves/incomplete/dark_prismarine", new PartiallyAmelStaff());
    public static final Item PARTAMEL_DARK_PRISMARINE_WAND = registerItem("staves/incomplete/dark_prismarine_wand", new PartAmelWand());
    public static final Item PARTAMEL_PURPUR = registerItem("staves/incomplete/purpur", new PartiallyAmelStaff());
    public static final Item PARTAMEL_PURPUR_WAND = registerItem("staves/incomplete/purpur_wand", new PartAmelWand());
    public static final Item PARTAMEL_WAND = registerItem("staves/incomplete/generic_wand", new PartAmelWand());
    public static final Item AMEL_WAND = registerItem("staves/amel_wand", new AmelWand());
    public static final Item AMEL_ORB = registerItem("amel_constructs/amel_orb", new AmelOrb());
    private static boolean recipesInitialized = false;

    public static void initHextendedInterop() {
        Mutables.scheduleRegisterAtServerStart(() -> {
            if (recipesInitialized) return;
            // a saying i know goes:
            // "the law's hands are long."
            recipesInitialized = true;
            Map<String, Item> STAFF_TO_PARTAMEL = Map.ofEntries(
                // source for ids: hextended
                entry("long/acacia", PARTAMEL_ACACIA_WAND),
                entry("long/bamboo", PARTAMEL_BAMBOO_WAND),
                entry("long/birch", PARTAMEL_BIRCH_WAND),
                entry("long/cherry", PARTAMEL_CHERRY_WAND),
                entry("long/crimson", PARTAMEL_CRIMSON_WAND),
                entry("long/dark_oak", PARTAMEL_DARK_OAK_WAND),
                entry("long/edified", PARTAMEL_EDIFIED_WAND),
                entry("long/jungle", PARTAMEL_JUNGLE_WAND),
                entry("long/mangrove", PARTAMEL_MANGROVE_WAND),
                entry("long/mindsplice", PARTAMEL_MINDSPLICE_WAND),
                entry("long/oak", PARTAMEL_OAK_WAND),
                entry("long/spruce", PARTAMEL_SPRUCE_WAND),
                entry("moss", PARTAMEL_MOSS),
                entry("long/moss", PARTAMEL_MOSS_WAND),
                entry("flowered_moss", PARTAMEL_FLOWERED_MOSS),
                entry("long/flowered_moss", PARTAMEL_FLOWERED_MOSS_WAND),
                entry("prismarine", PARTAMEL_PRISMARINE),
                entry("long/prismarine", PARTAMEL_PRISMARINE_WAND),
                entry("dark_prismarine", PARTAMEL_DARK_PRISMARINE),
                entry("long/dark_prismarine", PARTAMEL_DARK_PRISMARINE_WAND),
                entry("purpur", PARTAMEL_PURPUR),
                entry("long/purpur", PARTAMEL_PURPUR_WAND)
            );
            Map<String, Pair<Item, Item>> IMBUEMENT_RECIPES = Map.ofEntries(
                entry("staff/drawing_orb", new Pair<>(null, AMEL_ORB))
            );
            for (String id : STAFF_TO_PARTAMEL.keySet()) {
                Mutables.registerInfusionRecipe(
                    Registries.ITEM.get(LanisHextendedStaves.id("staff/" + id)),
                    (PartiallyAmelInterface)STAFF_TO_PARTAMEL.get(id),
                    (FullyAmelInterface)AMEL_WAND
                );
            }
            for (String id : IMBUEMENT_RECIPES.keySet()) {
                Mutables.registerInfusionRecipe(
                    Registries.ITEM.get(LanisHextendedStaves.id(id)),
                    (PartiallyAmelInterface)IMBUEMENT_RECIPES.get(id).getFirst(),
                    (FullyAmelInterface)IMBUEMENT_RECIPES.get(id).getSecond()
                );
            }
        });
    }
}
