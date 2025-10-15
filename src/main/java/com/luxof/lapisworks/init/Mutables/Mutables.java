package com.luxof.lapisworks.init.Mutables;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.TriConsumer;
import com.luxof.lapisworks.BeegInfusions.EnhanceEnchantedBook;
import com.luxof.lapisworks.BeegInfusions.MakeGenericPartAmel;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.init.ModItems.AMEL_RING;
import static com.luxof.lapisworks.init.ModItems.AMEL_STAFF;
import static com.luxof.lapisworks.init.ModItems.JUMP_SLATE_AM1;
import static com.luxof.lapisworks.LapisworksIDs.AMEL_TAG;
import static com.luxof.lapisworks.LapisworksIDs.ENCHSENT_ADVANCEMENT;
import static com.luxof.lapisworks.LapisworksIDs.ENHANCE_ENCHANTED_BOOK;
import static com.luxof.lapisworks.LapisworksIDs.FLAY_ARTMIND_ADVANCEMENT;
import static com.luxof.lapisworks.LapisworksIDs.HASTENATURE_ADVANCEMENT;
import static com.luxof.lapisworks.LapisworksIDs.JUKEBOX_INTO_LIVE_JUKEBOX;
import static com.luxof.lapisworks.LapisworksIDs.MAKE_GENERIC_PARTAMEL;
import static com.luxof.lapisworks.LapisworksIDs.SIMPLE_MIND_INTO_AMETHYST;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.ArrayList;
import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** all of the stuff here is stuff that's looked at when the player is in the world
 * so no need to rush registering all this before Lapisworks or something */
public class Mutables {
    /** hello, modifying this value without registering the arms in CastingEnvironment#getPrimaryStacks
     * may cause crashes */
    public static int maxHands = 2;
    public static List<Identifier> wizardDiariesGainableAdvancements = new ArrayList<>();
    private static Map<Item, Integer> infusionBaseCostMap = new HashMap<>();
    // like what the fuck are these types below me scoob
    private static Map<Identifier, BeegInfusion> beegInfusionRecipes = new HashMap<>();
    private static Map<Identifier, BiPredicate<BlockPos, World>> imbueMindRecipeFilters = new HashMap<>();
    private static Map<Identifier, TriConsumer<BlockPos, World, ServerPlayerEntity>> imbueMindRecipeDoers = new HashMap<>();


    public static boolean isAmel(ItemStack stack) { return stack.isEmpty() ? false : stack.isIn(TagKey.of(RegistryKeys.ITEM, AMEL_TAG)); }
    public static boolean isAmel(Item item) { return isAmel(new ItemStack(item)); }


    public static void registerBaseCostFor(Item any, int cost) { infusionBaseCostMap.put(any, cost); }
    public static int getBaseCostForInfusionOf(Item any) { return infusionBaseCostMap.get(any); }


    public static void registerBeegInfusionRecipe(
        @NotNull Identifier id,
        @NotNull BeegInfusion recipe
    ) {
        beegInfusionRecipes.put(id, recipe);
    }
    public static BeegInfusion getBeegInfusionRecipe(Identifier id) { return beegInfusionRecipes.get(id); }
    /** you probably shouldn't re-<code>setup</code> any you get from here, they've already been set up. */
    public static Map<Identifier, BeegInfusion> testBeegInfusionFilters(
        List<HeldItemInfo> itemInfos, CastingEnvironment ctx, List<? extends Iota> stack, VAULT vault
    ) {
        Map<Identifier, BeegInfusion> ret = new HashMap<>();
        for (Identifier id : beegInfusionRecipes.keySet()) {
            BeegInfusion recipe = beegInfusionRecipes.get(id).setUp(itemInfos, ctx, stack, vault);
            if (recipe.test()) { ret.put(id, recipe); }
        }
        return ret;
    }
    /** Note: any of the params passed to your doer can be null. */
    public static void registerImbueMindRecipe(
        @NotNull BiPredicate<BlockPos, World> filter,
        @NotNull Identifier id,
        // i'm sure log4j wouldn't mind lol
        @NotNull TriConsumer<BlockPos, World, ServerPlayerEntity> doer
    ) {
        imbueMindRecipeFilters.put(id, filter);
        imbueMindRecipeDoers.put(id, doer);
    }
    public static Map<Identifier, TriConsumer<BlockPos, World, ServerPlayerEntity>> checkImbueMindRecipes(
        BlockPos bp, World world
    ) {
        // sure i could make the List<Pair<>> a Map<>, but this has silly uses, a'ight?
        Map<Identifier, TriConsumer<BlockPos, World, ServerPlayerEntity>> ret = new HashMap<>();
        for (Identifier key : imbueMindRecipeFilters.keySet()) {
            if (imbueMindRecipeFilters.get(key).test(bp, world)) {
                ret.put(key, imbueMindRecipeDoers.get(key));
            }
        }
        return ret;
    }

    /** only returns null when there is no non-null value for the given key. */
    @Nullable
    private static <T extends Object, T2 extends Object> T2 getFirstValue(T key, List<T> keys, List<T2> values) {
        for (int i = 0; i < values.size(); i++) {
            T gotKey = keys.get(i);
            if (gotKey != key) { continue; }
            else if (gotKey == null) { continue; }
            return values.get(i);
        }
        return null;
    }

    public static void innitBruv() {
        // i wonder if i could move this and make it be almost completely data-driven?
        wizardDiariesGainableAdvancements.add(ENCHSENT_ADVANCEMENT);
        wizardDiariesGainableAdvancements.add(FLAY_ARTMIND_ADVANCEMENT);
        wizardDiariesGainableAdvancements.add(HASTENATURE_ADVANCEMENT);

        registerBaseCostFor(AMEL_STAFF, 10);
        registerBaseCostFor(AMEL_RING, 1);
        registerBaseCostFor(JUMP_SLATE_AM1, 20);

        registerImbueMindRecipe(
            (bp, world) -> world.getBlockState(bp).getBlock() == Blocks.AMETHYST_BLOCK,
            SIMPLE_MIND_INTO_AMETHYST,
            (bp, world, caster) -> world.setBlockState(bp, Blocks.BUDDING_AMETHYST.getDefaultState())
        );
        registerImbueMindRecipe(
            (bp, world) -> world.getBlockState(bp).getBlock() == Blocks.JUKEBOX,
            JUKEBOX_INTO_LIVE_JUKEBOX,
            (bp, world, caster) -> world.setBlockState(bp, ModBlocks.LIVE_JUKEBOX_BLOCK.getDefaultState())
        );

        registerBeegInfusionRecipe(ENHANCE_ENCHANTED_BOOK, new EnhanceEnchantedBook());
        registerBeegInfusionRecipe(MAKE_GENERIC_PARTAMEL, new MakeGenericPartAmel());
    }
}
