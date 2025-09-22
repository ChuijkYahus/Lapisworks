package com.luxof.lapisworks.actions;

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

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.init.Mutables;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

public class SwapAmel implements SpellAction {
    public int getArgc() {
        return 0;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        HeldItemInfo amelInfo = ctx.getHeldItemToOperateOn(Mutables::itemHasMoldAmelProduct);
        if (amelInfo == null) {
            MishapThrowerJava.throwMishap(MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), "amel"));
        }
        ItemStack amelStack = amelInfo.stack();
        Hand hand = amelInfo.hand();

        Item swapWith = Mutables.getMoldAmelProduct(amelStack.getItem());
        int count = amelStack.getCount();

        return new SpellAction.Result(
            new Spell(swapWith, count, hand),
            0,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10 + count)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final Item item;
        public final int count;
        public final Hand hand;

        public Spell(Item item, int count, Hand hand) {
            this.item = item;
            this.count = count;
            this.hand = hand;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ctx.replaceItem(
                Mutables::itemHasMoldAmelProduct,
                new ItemStack(this.item, this.count),
                this.hand
            );
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
