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
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.LapisworksIDs.SIMP_IMP_BLOCK;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class TeachSImp implements SpellAction {
    public int getArgc() {
        return 2;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        BlockPos pos = OperatorUtils.getBlockPos(args, 0, getArgc());
        SimpleImpetusEntity blockEntity = throwIfEmpty(
            ctx.getWorld().getBlockEntity(pos, ModBlocks.SIMPLE_IMPETUS_ENTITY_TYPE),
            new MishapBadBlock(pos, SIMP_IMP_BLOCK)
        );

        return new SpellAction.Result(
            new Spell(blockEntity, OperatorUtils.getPattern(args, 1, getArgc())),
            MediaConstants.DUST_UNIT * 2,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final SimpleImpetusEntity blockEntity;
        public final HexPattern pattern;

        public Spell(SimpleImpetusEntity blockEntity, HexPattern pattern) {
            this.blockEntity = blockEntity;
            this.pattern = pattern;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            this.blockEntity.tune(pattern, true);
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
