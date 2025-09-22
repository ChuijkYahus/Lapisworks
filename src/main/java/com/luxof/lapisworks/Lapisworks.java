package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.math.HexCoord;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexItems;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.init.ModPOIs;
import com.luxof.lapisworks.init.Patterns;
import com.luxof.lapisworks.init.LapisworksLoot;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.ThemConfigFlags;
import com.luxof.lapisworks.init.Mutables;
import com.luxof.lapisworks.items.shit.FullyAmelInterface;
import com.luxof.lapisworks.mixinsupport.GetStacks;

import static com.luxof.lapisworks.LapisworksIDs.MAINHAND;
import static com.luxof.lapisworks.LapisworksIDs.OFFHAND;
import static com.luxof.lapisworks.init.ThemConfigFlags.allPerWorldShapePatterns;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import org.jetbrains.annotations.Nullable;

import org.joml.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vazkii.patchouli.api.PatchouliAPI;

// why is this project actually big?
public class Lapisworks implements ModInitializer {
	public static Map<Item, FullyAmelInterface> swordToAmelMap = Map.of(
		Items.DIAMOND_SWORD, (FullyAmelInterface)ModItems.DIAMOND_SWORD,
		Items.IRON_SWORD, (FullyAmelInterface)ModItems.IRON_SWORD,
		Items.GOLDEN_SWORD, (FullyAmelInterface)ModItems.GOLD_SWORD
	);

	private static FrozenPigment BLACK_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.BLACK)), Util.NIL_UUID);
	private static FrozenPigment BROWN_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.BROWN)), Util.NIL_UUID);
	private static FrozenPigment BLUE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.BLUE)), Util.NIL_UUID);
	private static FrozenPigment CYAN_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.CYAN)), Util.NIL_UUID);
	private static FrozenPigment GRAY_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.GRAY)), Util.NIL_UUID);
	private static FrozenPigment GREEN_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.GREEN)), Util.NIL_UUID);
	private static FrozenPigment LIGHT_BLUE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.LIGHT_BLUE)), Util.NIL_UUID);
	private static FrozenPigment LIGHT_GRAY_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.LIGHT_GRAY)), Util.NIL_UUID);
	private static FrozenPigment LIME_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.LIME)), Util.NIL_UUID);
	private static FrozenPigment MAGENTA_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.MAGENTA)), Util.NIL_UUID);
	private static FrozenPigment ORANGE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.ORANGE)), Util.NIL_UUID);
	private static FrozenPigment PINK_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.PINK)), Util.NIL_UUID);
	private static FrozenPigment PURPLE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.PURPLE)), Util.NIL_UUID);
	private static FrozenPigment RED_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.RED)), Util.NIL_UUID);
	private static FrozenPigment WHITE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.WHITE)), Util.NIL_UUID);
	private static FrozenPigment YELLOW_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.YELLOW)), Util.NIL_UUID);

	public static final String MOD_ID = "lapisworks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ThemConfigFlags.declareEm();
		Patterns.init();
		ModItems.init_shit();
		LapisworksServer.lockIn();
		ModBlocks.wearASkirt();
		LapisworksLoot.gibLootexclamationmark();
		Mutables.innitBruv();
		ModPOIs.crawlOutOfHell();

        LOGGER.info("Luxof's pet Lapisworks is getting a bit hyperactive.");
		LOGGER.info("\"Lapisworks! Lapis Lapis!\"");
		LOGGER.info("Feed it redstone.");
	}

	public static Identifier id(String string) {
		return new Identifier(MOD_ID, string);
	}

	public static boolean trinketEquipped(LivingEntity entity, Item item) {
		Optional<TrinketComponent> trinkCompOp = TrinketsApi.getTrinketComponent(entity);
		return trinkCompOp.isEmpty() ? false : trinkCompOp.get().isEquipped(item);
	}

	@Nullable
	public static FrozenPigment getPigmentFromDye(DyeColor dye) {
		// if I can't have Map to do it I'll get a function to do it
		switch (dye) {
			case BLACK: return BLACK_FP;
			case BROWN: return BROWN_FP;
			case BLUE: return BLUE_FP;
			case CYAN: return CYAN_FP;
			case GRAY: return GRAY_FP;
			case GREEN: return GREEN_FP;
			case LIGHT_BLUE: return LIGHT_BLUE_FP;
			case LIGHT_GRAY: return LIGHT_GRAY_FP;
			case LIME: return LIME_FP;
			case MAGENTA: return MAGENTA_FP;
			case ORANGE: return ORANGE_FP;
			case PINK: return PINK_FP;
			case PURPLE: return PURPLE_FP;
			case RED: return RED_FP;
			case WHITE: return WHITE_FP;
			case YELLOW: return YELLOW_FP;
			default: return null;
		}
	}

	@Nullable
	public static DyeColor getDyeFromPigment(FrozenPigment pigment) {
		// uncommon, that's my excuse
		if (pigment == BLACK_FP) { return DyeColor.BLACK; }
		else if (pigment == BROWN_FP) { return DyeColor.BROWN; }
		else if (pigment == BLUE_FP) { return DyeColor.BLUE; }
		else if (pigment == CYAN_FP) { return DyeColor.CYAN; }
		else if (pigment == GRAY_FP) { return DyeColor.GRAY; }
		else if (pigment == GREEN_FP) { return DyeColor.GREEN; }
		else if (pigment == LIGHT_BLUE_FP) { return DyeColor.LIGHT_BLUE; }
		else if (pigment == LIGHT_GRAY_FP) { return DyeColor.LIGHT_GRAY; }
		else if (pigment == LIME_FP) { return DyeColor.LIME; }
		else if (pigment == MAGENTA_FP) { return DyeColor.MAGENTA; }
		else if (pigment == ORANGE_FP) { return DyeColor.ORANGE; }
		else if (pigment == PINK_FP) { return DyeColor.PINK; }
		else if (pigment == PURPLE_FP) { return DyeColor.PURPLE; }
		else if (pigment == RED_FP) { return DyeColor.RED; }
		else if (pigment == WHITE_FP) { return DyeColor.WHITE; }
		else if (pigment == YELLOW_FP) { return DyeColor.YELLOW; }
		else { return null; }
	}

	public static double clamp(double num, double min, double max) { return Math.min(Math.max(num, min), max); }
	public static float clamp(float num, float min, float max) { return Math.min(Math.max(num, min), max); }

	/** Computes the seed that will be used to compute per-world pattern shapes from a world seed. */
	public static int pickUsingSeed(long seed) {
		// i'm trusting that org.joml.Random won't change and that java.util.Random will across Java versions
		// (should probably homebrew my own atp)
		Random rng = new Random(seed);
		int sendThisSeed = 0;
		for (int i = -1; i < seed % 13; i++) { // so they can't easily predict world seed
			sendThisSeed = rng.nextInt(32767); // wonder if i should use newSeed() instead
		}
		return sendThisSeed;
	}

	/** Computes the config flags and selects them for you. ASSUMES THIS IS A FRESHLY-MADE RNG!! */
	public static void pickConfigFlags(Random rng) {
		for (String patId : allPerWorldShapePatterns.keySet()) {
			int chosen = rng.nextInt(allPerWorldShapePatterns.get(patId).size());
			PatchouliAPI.get().setConfigFlag(
				patId + String.valueOf(chosen),
				false
			);
			chosenFlags.put(patId, chosen);
		}
	}

	/** Nulls the config flags for you. */
	public static void nullConfigFlags() {
		LOGGER.info("Nulling config flags.");
		for (String patId : allPerWorldShapePatterns.keySet()) {
			for (int i = 0; i < allPerWorldShapePatterns.get(patId).size(); i++) {
				PatchouliAPI.get().setConfigFlag(
					patId + String.valueOf(i),
					false
				);
			}
			chosenFlags.put(patId, null);
		}
	}

	/** removes everything after the first two digits after the dot. */
	public static String prettifyFloat(float value) {
		// val % 0.01 flickers sometimes
		return String.valueOf(Math.floor((double)value * 100.0) / 100.0);
	}
	public static double prettifyDouble(double value) {
		return Math.floor(value * 100.0) / 100.0;
	}

	public static boolean matchShape(HexPattern pat1, HexPattern p2) {
		// i think i read somewhere by some guy that if you record how many times
		// a position is drawn over then it's fine
		// he wasn't too sure though, but i pray he's right
		// because nothing else i've done has worked
		return equalsButUnordered(setTopLeftOrigin(pat1.positions()), setTopLeftOrigin(p2.positions()));
	}
	public static List<HexCoord> setTopLeftOrigin(List<HexCoord> pat) {
		HexCoord runningTopLeft = new HexCoord(0, 0);
		for (HexCoord coord : pat) {
			if (coord.getQ() < runningTopLeft.getQ() && coord.getR() <= runningTopLeft.getR()) {
				runningTopLeft = new HexCoord(coord.getQ(), coord.getR());
			} else if (coord.getR() < runningTopLeft.getR()) {
				runningTopLeft = new HexCoord(coord.getQ(), coord.getR());
			}
		}
        LOGGER.info("top left!: " + runningTopLeft.toString());
		// "must be final" my ass
		HexCoord topLeft = new HexCoord(runningTopLeft.getQ(), runningTopLeft.getR());
        return pat.stream().map((coord) -> {
            return new HexCoord(coord.getQ() - topLeft.getQ(), coord.getR() - topLeft.getR());
        }).collect(Collectors.toList());
	}
    /** Checks if two lists are equal, but does not check if their elements are ordered the same way. */
    public static <T extends Object> boolean equalsButUnordered(List<T> list1, List<T> list2) {
        if (list1.size() != list2.size()) { return false; }
        else if (list1.size() == 0) { return true; }
        List<T> l2 = new ArrayList<T>(list2);
        for (T thing : list1) {
            int idx = l2.indexOf(thing);
            if (idx == -1) { return false; }
            l2.remove(idx);
        }
        return true;
    }

	public static <T extends Object> List<T> toList(Collection<T> collection) {
		return collection.stream().collect(Collectors.toList());
	}

	public static boolean closeEnough(float a, float b, float epsilon) {
		return Math.abs(b - a) < epsilon;
	}
	public static boolean closeEnough(double a, double b, double epsilon) {
		return Math.abs(b - a) < epsilon;
	}

    /** returns null if hand isn't MAIN_HAND or OFF_HAND (i'll add more eventually..!!) */
    @Nullable
    public static ItemStack getStackFromHand(CastingEnvironment ctx, int hand) {
        List<HeldItemInfo> stacks = ((GetStacks)ctx).getHeldStacks();
        try { return stacks.get(hand).stack(); }
		catch (IndexOutOfBoundsException e) {
			LOGGER.info("Someone tried to access idx " + hand + " of " + stacks.toString() + ".");
			return null;
		}
    }

	/** returns null if resulting Hand can't be MAIN_HAND or OFF_HAND (MORE WILL COME, THEE SHALL KNOW) */
	@Nullable
	public static Hand intToHand(int hand) {
		switch (hand) {
			case 0: return Hand.MAIN_HAND;
			case 1: return Hand.OFF_HAND;
			default: return null;
		}
	}

	/** Returns stuff like "43rd" (A LITERAL, NOT TRANSLATEABLE!!!) if it doesn't know wtf that Hand is */
	public static Text handToString(Hand hand) {
		if (hand == Hand.MAIN_HAND) { return MAINHAND; }
		else if (hand == Hand.OFF_HAND) { return OFFHAND; }
		return Text.translatable("hands.lapisworks." + hand.ordinal());
	}
}
