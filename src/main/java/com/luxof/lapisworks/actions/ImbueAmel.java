package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.TriConsumer;
import com.luxof.lapisworks.items.shit.FullyAmelInterface;
import com.luxof.lapisworks.items.shit.PartiallyAmelInterface;
import com.luxof.lapisworks.mishaps.MishapBadHandItem;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.Lapisworks.getStackFromHand;
import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.LapisworksIDs.IMBUEABLE;
import static com.luxof.lapisworks.init.Mutables.getBeegInfusionRecipeDoer;
import static com.luxof.lapisworks.init.Mutables.getBeegInfusionRecipeMediaCostDecider;
import static com.luxof.lapisworks.init.Mutables.getFullyAmelProduct;
import static com.luxof.lapisworks.init.Mutables.getPartAmelProduct;
import static com.luxof.lapisworks.init.Mutables.infusionRecipeExistsFor;
import static com.luxof.lapisworks.init.Mutables.isAmel;
import static com.luxof.lapisworks.init.Mutables.testBeegInfusionFilters;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class ImbueAmel implements SpellAction {
    public int getArgc() {
        return 1;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        int wantToInfuseAmount = OperatorUtils.getPositiveInt(args, 0, getArgc());
        if (wantToInfuseAmount <= 0) {
            // go fuck yourself
            return new SpellAction.Result(
                new DoNothingSpell(),
                0L,
                List.of(),
                1
            );
        }

        ItemStack offHandItems = getStackFromHand(ctx, 1);
        ItemStack mainHandItems = getStackFromHand(ctx, 0);

        MishapBadHandItem needAmel = new MishapBadHandItem(
            offHandItems,
            AMEL,
            Hand.OFF_HAND
        );
        MishapBadHandItem needImbueable = new MishapBadHandItem(
            mainHandItems,
            IMBUEABLE,
            Hand.MAIN_HAND
        );

        if (offHandItems.isEmpty()) {
            MishapThrowerJava.throwMishap(needAmel);
        } else if (!isAmel(offHandItems)) {
            MishapThrowerJava.throwMishap(needAmel);
        } else if (mainHandItems.isEmpty()) {
            MishapThrowerJava.throwMishap(needImbueable);
        } else if (mainHandItems.getItem() instanceof FullyAmelInterface) {
            MishapThrowerJava.throwMishap(needImbueable);
        }

        Item mainHandItem = mainHandItems.getItem();
        if (!infusionRecipeExistsFor(mainHandItem)) {
            // if the filter vv throws? throws out of here and gets caught by Hex Casting
            List<Identifier> beegInfusionRecipes = testBeegInfusionFilters(
                new HeldItemInfo(mainHandItems, Hand.MAIN_HAND),
                ctx,
                args
            );
            if (!beegInfusionRecipes.isEmpty()) {
                HeldItemInfo heldInfo = new HeldItemInfo(mainHandItems, Hand.MAIN_HAND);
                return new SpellAction.Result(
                    new BeegInfusion(
                        heldInfo,
                        getBeegInfusionRecipeDoer(beegInfusionRecipes.get(0)),
                        args
                    ),
                    getBeegInfusionRecipeMediaCostDecider(beegInfusionRecipes.get(0)).apply(
                        heldInfo,
                        ctx,
                        args
                    ),
                    List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10)),
                    1
                );
            }
            MishapThrowerJava.throwMishap(needImbueable);
        }
        PartiallyAmelInterface partAmel = getPartAmelProduct(mainHandItem);
        FullyAmelInterface fullAmel = getFullyAmelProduct(mainHandItem);

        // yes i will explain my math (past programmer is the worst)
        int requiredAmelForFullInfusion = mainHandItem instanceof PartiallyAmelInterface ?
            // amel needed to make full amel = damage suffered (or healing needed) / 1 amel's worth for healing
            (int)Math.ceil((double)mainHandItems.getDamage() / (double)partAmel.getAmelWorthInDurability()) :
            // or just the amount it takes
            fullAmel.getRequiredAmelToMakeFromBase();
        // use Math.min() so i don't overspend
        int infuseAmount = Math.min(wantToInfuseAmount, requiredAmelForFullInfusion);

        if (offHandItems.getCount() < infuseAmount) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughItems(offHandItems, infuseAmount));
        }

        LOGGER.info("required amel: " + requiredAmelForFullInfusion);
        LOGGER.info("infusing: " + infuseAmount);
        ItemStack changeToItemStack;
        if (infuseAmount == requiredAmelForFullInfusion) { changeToItemStack = new ItemStack((Item)fullAmel); }
        else if (!(mainHandItem instanceof PartiallyAmelInterface)) {
            changeToItemStack = new ItemStack((Item)partAmel);
            changeToItemStack.setDamage(
                changeToItemStack.getMaxDamage() - infuseAmount * partAmel.getAmelWorthInDurability()
            );
        } else {
            changeToItemStack = mainHandItems.copy();
            changeToItemStack.setDamage(mainHandItems.getDamage() - infuseAmount * partAmel.getAmelWorthInDurability());
        }

        return new SpellAction.Result(
            new Spell(changeToItemStack, infuseAmount),
            MediaConstants.SHARD_UNIT * 2 * infuseAmount,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10 + infuseAmount)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final ItemStack changeToItem;
        public final int count;

        public Spell(ItemStack changeToItem, int count) {
            this.changeToItem = changeToItem;
            this.count = count;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ItemStack offHandItems = getStackFromHand(ctx, 1);
            ctx.replaceItem(any -> true, new ItemStack(
                offHandItems.getItem(),
                offHandItems.getCount() - this.count
            ), Hand.OFF_HAND);
            ctx.replaceItem(any -> true, this.changeToItem, Hand.MAIN_HAND);
		}

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }

    /** should really just call it a sophisticated infusion */
    public class BeegInfusion implements RenderedSpell {
        public final HeldItemInfo heldInfo;
        public final TriConsumer<HeldItemInfo, CastingEnvironment, List<? extends Iota>> doer;
        public final List<? extends Iota> stack;

        public BeegInfusion(
            HeldItemInfo heldInfo,
            TriConsumer<HeldItemInfo, CastingEnvironment, List<? extends Iota>> doer,
            List<? extends Iota> stack
        ) {
            this.heldInfo = heldInfo;
            this.doer = doer;
            this.stack = stack;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            doer.accept(heldInfo, ctx, stack);
        }

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }

    /** paranoia, ig */
    public class DoNothingSpell implements RenderedSpell {
        @Override
        public void cast(CastingEnvironment arg0) {
            return;
        }

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }

    @Override
    public boolean awardsCastingStat(CastingEnvironment ctx) {
        return SpellAction.DefaultImpls.awardsCastingStat(this, ctx);
    }

    @Override
    public Result executeWithUserdata(List<? extends Iota> args, CastingEnvironment env, NbtCompound userData) {
        return SpellAction.DefaultImpls.executeWithUserdata(this, args, env, userData);
    }

    @Override
    public boolean hasCastingSound(CastingEnvironment ctx) {
        return SpellAction.DefaultImpls.hasCastingSound(this, ctx);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return SpellAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
