package com.luxof.lapisworks.actions.great;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapUnenlightened;
import at.petrak.hexcasting.api.misc.MediaConstants;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.luxof.lapisworks.MishapThrowerJava;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

// name from chatgpt, sorry but i'm bad at naming things
// and Hastenature is a name that kicks ass you have to admit
public class Hastenature implements SpellAction {
    public int getArgc() {
        return 2;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        if (!ctx.isEnlightened()) { MishapThrowerJava.throwMishap(new MishapUnenlightened()); }
        BlockPos pos = OperatorUtils.getBlockPos(args, 0, getArgc());
        int times = OperatorUtils.getPositiveInt(args, 1, getArgc());
        BlockState state = ctx.getWorld().getBlockState(pos);

        return new SpellAction.Result(
            new Spell(state, pos, times),
            MediaConstants.DUST_UNIT * 5 * times,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            times
        );
    }

    public class Spell implements RenderedSpell {
        public final BlockState state;
        public final BlockPos pos;
        public final int times;

        public Spell(BlockState state, BlockPos pos, int times) {
            this.state = state;
            this.pos = pos;
            this.times = times;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            for (int i = 0; i < times; i++) {
                this.state.randomTick(ctx.getWorld(), this.pos, ctx.getWorld().random);
            }
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

    @Nullable
    public static ServerPlayerEntity getPlayerOrNull(CastingEnvironment ctx) {
        return ctx.getCastingEntity() != null ? (ServerPlayerEntity)ctx.getCastingEntity() : null;
    }
}
