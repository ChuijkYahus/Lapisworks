package com.luxof.lapisworks.items;

import at.petrak.hexcasting.api.utils.NBTHelper;

import com.luxof.lapisworks.client.collar.LapisCollarAddition;
import com.luxof.lapisworks.client.collar.LapisCollarAdditions;

import static com.luxof.lapisworks.Lapisworks.log;

import java.util.List;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class Collar extends Item implements DyeableItem {

    public Collar() {
        super(
            new FabricItemSettings()
                .maxCount(1)
        );
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        if (!state.isOf(Blocks.WATER_CAULDRON)) return ActionResult.PASS;

        removeColor(stack);
        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack stack = user.getStackInHand(hand);
        ItemStack otherStack = user.getStackInHand(otherHand);

        if (otherStack.getCount() >= otherStack.getMaxCount())
            return TypedActionResult.pass(stack);

        for (var id : getAdditions(stack)) {
            LapisCollarAddition addition = LapisCollarAdditions.get(id);
            if (addition.testItem(otherStack.getItem())) {
                user.setCurrentHand(hand);
                return TypedActionResult.consume(stack);
            }
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        Hand hand = user.getActiveHand();
        Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack otherStack = user.getStackInHand(otherHand);

        for (var id : getAdditions(stack)) {
            LapisCollarAddition addition = LapisCollarAdditions.get(id);
            if (addition.testItem(otherStack.getItem())) {
                user.setCurrentHand(hand);
                otherStack.increment(1);
                removeAddition(stack, id);
                return stack;
            }
        }

        return stack;
    }

    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.BOW; }
    @Override public int getMaxUseTime(ItemStack stack) { return 20; }

    /*@Override
    public int getColor(ItemStack stack) {
        NbtCompound display = stack.getSubNbt("display");
        return display != null && display.contains("color", NbtElement.NUMBER_TYPE)
            ? display.getInt("color") : getColorFrom(DyeColor.BLACK);
    }*/

    private static double linearize(int cnotnormal) {
        double c = cnotnormal / 255.0;
        return c <= 0.04045 ? c / 12.92 : (c+0.055)/1.055;
    }
    private static double[] toLMS(double r, double g, double b) {
        return new double[] {
            r*0.4122214708 + g*0.5363325363 + b*0.0514459929,
            r*0.2119034982 + g*0.6806995451 + b*0.1073969566,
            r*0.0883024619 + g*0.2817188376 + b*0.6299787005
        };
    }
    private static double[] toOkLab(double[] lmscomp) {
        return new double[] {
            lmscomp[0]*0.2104542553 + lmscomp[1]*0.7936177850 + lmscomp[2]*-0.0040720468,
            lmscomp[0]*1.9779984951 + lmscomp[1]*-2.4285922050 + lmscomp[2]*0.4505937099,
            lmscomp[0]*0.0259040371 + lmscomp[1]*0.7827717662 + lmscomp[2]*-0.8086757660
        };
    }
    private static double distanceBetween(int color1, int color2) {
        double[] lms1 = toLMS(
            linearize(color1 >> 16),
            linearize(color1 & 0xff00 >> 8),
            linearize(color1 & 0xff)
        );
        double[] lms2 = toLMS(
            linearize(color2 >> 16),
            linearize(color2 & 0xff00 >> 8),
            linearize(color2 & 0xff)
        );
        double[] lms1comp = {
            Math.pow(lms1[0], 1.0/3.0),
            Math.pow(lms1[1], 1.0/3.0),
            Math.pow(lms1[2], 1.0/3.0)
        };
        double[] lms2comp = {
            Math.pow(lms2[0], 1.0/3.0),
            Math.pow(lms2[1], 1.0/3.0),
            Math.pow(lms2[2], 1.0/3.0)
        };
        double[] oklab1 = toOkLab(lms1comp);
        double[] oklab2 = toOkLab(lms2comp);
        return Math.pow(oklab2[0]-oklab1[0], 2)
            + Math.pow(oklab2[1]-oklab1[1], 2)
            + Math.pow(oklab2[2]-oklab1[2], 2);
    }

    public int getColorFrom(DyeColor dyeColor) {
        return dyeColor.getFireworkColor();
    }

    public final List<Pair<Integer, Text>> colors = List.of(
        new Pair<>(getColorFrom(DyeColor.WHITE), Text.translatable("tooltips.lapisworks.collar.color.white")),
        new Pair<>(getColorFrom(DyeColor.ORANGE), Text.translatable("tooltips.lapisworks.collar.color.orange")),
        new Pair<>(getColorFrom(DyeColor.MAGENTA), Text.translatable("tooltips.lapisworks.collar.color.magenta")),
        new Pair<>(getColorFrom(DyeColor.LIGHT_BLUE), Text.translatable("tooltips.lapisworks.collar.color.light_blue")),
        new Pair<>(getColorFrom(DyeColor.YELLOW), Text.translatable("tooltips.lapisworks.collar.color.yellow")),
        new Pair<>(getColorFrom(DyeColor.LIME), Text.translatable("tooltips.lapisworks.collar.color.lime")),
        new Pair<>(getColorFrom(DyeColor.PINK), Text.translatable("tooltips.lapisworks.collar.color.pink")),
        new Pair<>(getColorFrom(DyeColor.GRAY), Text.translatable("tooltips.lapisworks.collar.color.gray")),
        new Pair<>(getColorFrom(DyeColor.LIGHT_GRAY), Text.translatable("tooltips.lapisworks.collar.color.light_gray")),
        new Pair<>(getColorFrom(DyeColor.CYAN), Text.translatable("tooltips.lapisworks.collar.color.cyan")),
        new Pair<>(getColorFrom(DyeColor.PURPLE), Text.translatable("tooltips.lapisworks.collar.color.yo_bro_y_u_urple")),
        new Pair<>(getColorFrom(DyeColor.BLUE), Text.translatable("tooltips.lapisworks.collar.color.blue")),
        new Pair<>(getColorFrom(DyeColor.BROWN), Text.translatable("tooltips.lapisworks.collar.color.brown")),
        new Pair<>(getColorFrom(DyeColor.GREEN), Text.translatable("tooltips.lapisworks.collar.color.green")),
        new Pair<>(getColorFrom(DyeColor.RED), Text.translatable("tooltips.lapisworks.collar.color.red")),
        new Pair<>(getColorFrom(DyeColor.BLACK), Text.translatable("tooltips.lapisworks.collar.color.black"))
    );
    public Text colorClosestTo(int color) {
        Text closest = null;
        double closestScore = Integer.MAX_VALUE;
        for (var pair : colors) {
            double score = distanceBetween(color, pair.getLeft());
            log("distance between %s and %s (%s): %d", Integer.toHexString(color), Integer.toHexString(pair.getLeft()), DyeColor.byFireworkColor(pair.getLeft()).asString(), score);

            if (score < closestScore) {
                closest = pair.getRight();
                closestScore = score;
            }
        }
        return closest;
    }

    @Override
    public void appendTooltip(
        ItemStack stack, World world, List<Text> tooltip, TooltipContext context
    ) {
        int color = getColor(stack);
        tooltip.add(Text.translatable(
            "tooltips.lapisworks.collar.color",
            colorClosestTo(color)
                .copy()
                .styled(s -> s.withColor(color))
        ));

        List<Identifier> added = getAdditions(stack);
        tooltip.add(
            added.size() > 0
                ? Text.translatable("tooltips.lapisworks.collar.added_items_list_pre")
                : Text.translatable("tooltips.lapisworks.collar.no_added_items")
        );
        added.forEach(
            id -> tooltip.add(LapisCollarAdditions.get(id).getName(stack))
        );
    }

    public List<Identifier> getAdditions(ItemStack stack) {
        NbtList added = NBTHelper.getList(stack, "additions", NbtElement.STRING_TYPE);
        return added == null
            ? List.of()
            : added
                .stream()
                .map(e -> new Identifier(e.asString()))
                .toList();
    }

    public void addAddition(ItemStack stack, Identifier id) {
        NbtList added = NBTHelper.getList(stack, "additions", NbtElement.STRING_TYPE);
        if (added == null) added = new NbtList();

        added.add(NbtString.of(id.toString()));
        NBTHelper.putList(stack, "additions", added);
    }

    @SuppressWarnings("unlikely-arg-type")
    public void removeAddition(ItemStack stack, Identifier id) {
        NbtList added = NBTHelper.getList(stack, "additions", NbtElement.STRING_TYPE);
        added.remove(id.toString());
        NBTHelper.putList(stack, "additions", added);
    }
}
