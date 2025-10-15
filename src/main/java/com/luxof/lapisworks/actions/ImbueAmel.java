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
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.init.Mutables.BeegInfusion;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.items.shit.PartiallyAmelInterface;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;
import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.luxof.lapisworks.mixinsupport.GetVAULT;
import com.luxof.lapisworks.recipes.HandsInv;
import com.luxof.lapisworks.recipes.ImbuementRec;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.LapisworksIDs.IMBUEABLE;
import static com.luxof.lapisworks.init.Mutables.Mutables.testBeegInfusionFilters;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        VAULT vault = ((GetVAULT)ctx).grabVAULT();

        int availableAmel = vault.fetch(Mutables::isAmel, Flags.PRESET_Stacks_InvItem_UpToHotbar);
        List<HeldItemInfo> heldInfos = ((GetStacks)ctx).getHeldStacksOtherFirst();
        List<ItemStack> heldStacks = ((GetStacks)ctx).getHeldItemStacksOtherFirst();

        MishapBadOffhandItem needImbueable = new MishapBadOffhandItem(
            ItemStack.EMPTY.copy(),
            IMBUEABLE
        );

        Optional<ImbuementRec> recipeOpt = ctx.getWorld().getRecipeManager().getFirstMatch(
            ImbuementRec.Type.INSTANCE,
            new HandsInv(heldStacks),
            ctx.getWorld()
        );
        if (recipeOpt.isEmpty()) {
            Map<Identifier, BeegInfusion> beegInfusionRecipes = testBeegInfusionFilters(
                heldInfos,
                ctx,
                args,
                vault
            );
            if (beegInfusionRecipes.isEmpty()) MishapThrowerJava.throwMishap(needImbueable);

            BeegInfusion selected = beegInfusionRecipes.values().iterator().next();
            selected.mishapIfNeeded();

            return new SpellAction.Result(
                new SpellBeegInfusion(selected),
                selected.getCost(),
                List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10)),
                1
            );
        }
        ImbuementRec recipe = recipeOpt.get();
        ItemStack items = null;
        Hand hand = null;
        for (HeldItemInfo held : heldInfos) {
            if (recipe.getNormal().test(held.stack()) || held.stack().isOf(recipe.getPartAmel())) {
                items = held.stack();
                hand = held.hand();
            }
        }

        Item item = items.getItem();
        PartiallyAmelInterface partAmel = (PartiallyAmelInterface)recipe.getPartAmel();
        Item fullAmel = recipe.getFullAmel();

        // yes i will explain my math (past programmer is the worst)
        int requiredAmelForFullInfusion = item instanceof PartiallyAmelInterface ?
            // amel needed to make full amel = damage suffered (or healing needed) / 1 amel's worth for healing
            (int)Math.ceil(
                (double)items.getDamage() /
                (double)partAmel.getAmelWorthInDurability()
            ) :
            // or the base cost
            recipe.getFullAmelsCost();
        // use Math.min() so i don't overspend
        int infuseAmount = Math.min(wantToInfuseAmount, requiredAmelForFullInfusion);

        if (availableAmel < infuseAmount) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughItems(AMEL, availableAmel, infuseAmount));
        }

        LOGGER.info("required amel: " + requiredAmelForFullInfusion);
        LOGGER.info("infusing: " + infuseAmount);
        ItemStack newStack = null;
        if (infuseAmount == requiredAmelForFullInfusion) { newStack = new ItemStack((Item)fullAmel); }
        else if (partAmel == null) {
            MishapThrowerJava.throwMishap(
                new MishapNotEnoughItems(AMEL, infuseAmount, requiredAmelForFullInfusion)
            );
        } else if (!(item instanceof PartiallyAmelInterface)) {
            newStack = new ItemStack((Item)partAmel);
            newStack.setDamage(
                newStack.getMaxDamage() - infuseAmount * partAmel.getAmelWorthInDurability()
            );
        } else {
            newStack = items.copy();
            newStack.setDamage(
                items.getDamage() - infuseAmount * partAmel.getAmelWorthInDurability()
            );
        }

        return new SpellAction.Result(
            new Spell(newStack, hand, infuseAmount, vault),
            MediaConstants.DUST_UNIT * 2 * infuseAmount,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10 + infuseAmount)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final ItemStack changeToItem;
        public final Hand hand;
        public final int count;
        public final VAULT vault;

        public Spell(ItemStack changeToItem, Hand hand, int count, VAULT vault) {
            this.changeToItem = changeToItem;
            this.hand = hand;
            this.count = count;
            this.vault = vault;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            vault.drain(Mutables::isAmel, count, Flags.PRESET_Stacks_InvItem_UpToHotbar);
            ctx.replaceItem(any -> true, changeToItem, hand);
		}

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }

    /** really should just call it a sophisticated infusion */
    public class SpellBeegInfusion implements RenderedSpell {
        public final BeegInfusion recipe;

        public SpellBeegInfusion( BeegInfusion recipe ) { this.recipe = recipe; }

        @Override
        public void cast(CastingEnvironment ctx) { this.recipe.accept(); }

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
