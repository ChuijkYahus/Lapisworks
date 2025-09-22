package com.luxof.lapisworks.actions;

import java.util.List;

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

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.init.ModItems;

public class ImbueLap implements SpellAction {
    public int getArgc() {
        return 0;
    }

    private static boolean isLapis(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == Items.LAPIS_LAZULI;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        HeldItemInfo heldStackInfo = ctx.getHeldItemToOperateOn(ImbueLap::isLapis);
        if (heldStackInfo == null) {
            //                              *shrug* i don't know why it's different either.
            // i now know: i was stupid
            MishapThrowerJava.throwMishap(MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), "lapis_lazuli"));
        }
        int count = heldStackInfo.stack().getCount();

        return new SpellAction.Result(
            new Spell(count, heldStackInfo.hand()),
            MediaConstants.SHARD_UNIT * 2 * count,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10 + count)),
            1 // I REALLY don't know what to make this but I think it's for stuff like Hermes' Gambit
        );
    }

    public class Spell implements RenderedSpell {
        public final int count;
        public final Hand hand;

        public Spell(int count, Hand hand) { this.count = count; this.hand = hand; }

		@Override
		public void cast(CastingEnvironment ctx) {
            ctx.replaceItem(ImbueLap::isLapis, new ItemStack(ModItems.AMEL_ITEM, this.count), hand);
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
