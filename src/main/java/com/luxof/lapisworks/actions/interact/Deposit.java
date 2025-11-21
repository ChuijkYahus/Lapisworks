package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.google.common.collect.ImmutableSet;

import static com.luxof.lapisworks.Lapisworks.interactWithLinkableMediaBlocks;
import static com.luxof.lapisworks.MishapThrowerJava.assertInRange;
import static com.luxof.lapisworks.MishapThrowerJava.assertLinkableThere;

import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class Deposit implements SpellAction {
    public int getArgc() {
        return 2;
    }

    @Override
    public Result execute(List<? extends Iota> stack, CastingEnvironment ctx) {
        BlockPos pos = OperatorUtils.getBlockPos(stack, 0, getArgc());
        long amount = OperatorUtils.getPositiveInt(stack, 1, getArgc()) * MediaConstants.DUST_UNIT;

        assertInRange(ctx, pos);
        assertLinkableThere(pos, ctx);

        return new SpellAction.Result(
            new Spell(pos, amount),
            amount + (long)(amount * 0.1),
            List.of(
                ParticleSpray.cloud(ctx.mishapSprayPos(), 3, 20),
                ParticleSpray.cloud(pos.toCenterPos(), 3, 20)
            ),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final BlockPos pos;
        public final long amount;

        public Spell(BlockPos pos, long amount) {
            this.pos = pos;
            this.amount = amount;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            interactWithLinkableMediaBlocks(
                ctx.getWorld(),
                ImmutableSet.of(pos),
                amount,
                true
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
