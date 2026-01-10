package com.luxof.lapisworks.init.Mutables;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.items.ItemStaff;

import com.luxof.lapisworks.BeegInfusions.EnhanceEnchantedBook;
import com.luxof.lapisworks.BeegInfusions.MakeGenericPartAmel;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.interop.hextended.LapixtendedInterface;
import com.luxof.lapisworks.inv.HandsInv;
import com.luxof.lapisworks.inv.SMindInfusionSetupInv;
import com.luxof.lapisworks.mindinfusions.MakeSimpleImpetus;
import com.luxof.lapisworks.mindinfusions.UnflayVillager;
import com.luxof.lapisworks.recipes.ImbuementRec;
import com.luxof.lapisworks.recipes.SMindInfusionRec;

import static com.luxof.lapisworks.LapisworksIDs.AMEL_TAG;
import static com.luxof.lapisworks.LapisworksIDs.EMPTY_IMP_INTO_SIMP;
import static com.luxof.lapisworks.LapisworksIDs.ENCHSENT_ADVANCEMENT;
import static com.luxof.lapisworks.LapisworksIDs.ENHANCE_ENCHANTED_BOOK;
import static com.luxof.lapisworks.LapisworksIDs.FLAY_ARTMIND_ADVANCEMENT;
import static com.luxof.lapisworks.LapisworksIDs.HASTENATURE_ADVANCEMENT;
import static com.luxof.lapisworks.LapisworksIDs.MAKE_GENERIC_PARTAMEL;
import static com.luxof.lapisworks.LapisworksIDs.POTION_TAG;
import static com.luxof.lapisworks.LapisworksIDs.UNFLAY_FLAYED_VILLAGER;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

    public static boolean isAmel(ItemStack stack) { return stack.isEmpty() ? false : stack.isIn(AMEL_TAG); }
    public static boolean isAmel(Item item) { return isAmel(new ItemStack(item)); }
    public static boolean isInPotionTag(ItemStack stack) { return stack.isEmpty() ? false : stack.isIn(POTION_TAG); }
    public static boolean isInPotionTag(Item item) { return isInPotionTag(new ItemStack(item)); }

    public static int getCostForFullInfusionOfStaff(ItemStaff item, World world) {
        return world.getRecipeManager().getFirstMatch(
            ImbuementRec.Type.INSTANCE,
            new HandsInv(List.of(new ItemStack(
                LapixtendedInterface.getGenericStaffKnownToHaveRecipe(item)
            ))),
            world
        ).get().getFullAmelsCost();
    }

    public static class BeegInfusions {
        private static Map<Identifier, BeegInfusion> recipes = new HashMap<>();

        @Nullable public static BeegInfusion get(Identifier id) { return recipes.get(id); }
        public static void put(
            Identifier id,
            BeegInfusion recipe
        ) {
            recipes.put(id, recipe);
        }
        public static Map<Identifier, BeegInfusion> filter(
            List<HeldItemInfo> items, CastingEnvironment ctx, List<? extends Iota> stack, VAULT vault
        ) {
            Map<Identifier, BeegInfusion> ret = new HashMap<>();

            for (var entry : recipes.entrySet()) {
                var key = entry.getKey();
                var recipe = entry.getValue().setUp(items, ctx, stack, vault);

                if (recipe.test()) {
                    ret.put(key, recipe);
                }
            }

            return ret;
        }
    }

    public static class SMindInfusions {
        private static Map<Identifier, SMindInfusion> recipes = new HashMap<>();

        @Nullable public static SMindInfusion get(Identifier id) { return recipes.get(id); }
        public static void put(
            Identifier id,
            SMindInfusion recipe
        ) {
            recipes.put(id, recipe);
        }

        public static Map<Identifier, SMindInfusion> filter(
            BlockPos bp, CastingEnvironment ctx, List<? extends Iota> iotaStack, VAULT vault
        ) {
            Map<Identifier, SMindInfusion> ret = new HashMap<>();

            // i'm using the devil (var) because types are obvious and ts needs cleaning
            for (var entry : recipes.entrySet()) {
                var key = entry.getKey();
                var recipe = entry.getValue().setUp(bp, ctx, iotaStack, vault);

                if (recipe.testBlock()) {
                    ret.put(key, recipes.get(key));
                }
            }
            return ret;
        }
        /** takes datapacked recipes into account. */
        public static Map<Identifier, SMindInfusion> filterAll(
            BlockPos bp, CastingEnvironment ctx, List<? extends Iota> iotaStack, VAULT vault
        ) {
            Map<Identifier, SMindInfusion> ret = new HashMap<>();
            ctx.getWorld().getRecipeManager().getAllMatches(
                SMindInfusionRec.Type.INSTANCE,
                new SMindInfusionSetupInv(bp, ctx, iotaStack, vault),
                ctx.getWorld()
            ).forEach(recipe -> ret.put(recipe.getId(), recipe.innerInfusion));

            // yes, this allows you to override datapacked recipes with code.
            // this is ideal.
            filter(bp, ctx, iotaStack, vault).forEach(ret::put);

            return ret;
        }

        public static Map<Identifier, SMindInfusion> filter(
            Entity ent, CastingEnvironment ctx, List<? extends Iota> iotaStack, VAULT vault
        ) {
            Map<Identifier, SMindInfusion> ret = new HashMap<>();

            for (var entry : recipes.entrySet()) {
                var key = entry.getKey();
                var recipe = entry.getValue().setUp(ent, ctx, iotaStack, vault);

                if (recipe.testEntity()) {
                    ret.put(key, recipes.get(key));
                }
            }
            return ret;
        }
        /** takes datapacked recipes into account. */
        public static Map<Identifier, SMindInfusion> filterAll(
            Entity ent, CastingEnvironment ctx, List<? extends Iota> iotaStack, VAULT vault
        ) {
            var ret = filter(ent, ctx, iotaStack, vault);

            /*ctx.getWorld().getRecipeManager().getAllMatches(
                SMindInfusionRec.Type.INSTANCE,
                new SMindInfusionSetupInv(ent, ctx, iotaStack, vault),
                ctx.getWorld()
            ).forEach(recipe -> ret.put(recipe.getId(), recipe.innerInfusion));*/

            return ret;
        }
    }

    public static void innitBruv() {
        // i wonder if i could move this and make it be almost completely data-driven?
        wizardDiariesGainableAdvancements.add(ENCHSENT_ADVANCEMENT);
        wizardDiariesGainableAdvancements.add(FLAY_ARTMIND_ADVANCEMENT);
        wizardDiariesGainableAdvancements.add(HASTENATURE_ADVANCEMENT);

        SMindInfusions.put(EMPTY_IMP_INTO_SIMP, new MakeSimpleImpetus());
        SMindInfusions.put(UNFLAY_FLAYED_VILLAGER, new UnflayVillager());

        BeegInfusions.put(ENHANCE_ENCHANTED_BOOK, new EnhanceEnchantedBook());
        BeegInfusions.put(MAKE_GENERIC_PARTAMEL, new MakeGenericPartAmel());
    }
}
