package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.blocks.Mind;
import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.Mutables;

import static com.luxof.lapisworks.Lapisworks.toList;
import static com.luxof.lapisworks.LapisworksIDs.FULL_SIMPLE_MIND;
import static com.luxof.lapisworks.LapisworksIDs.IMBUEABLE;
import static com.luxof.lapisworks.LapisworksIDs.MIND_BLOCK;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.TriConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FlayArtMind implements SpellAction {
    public int getArgc() {
        return 2;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        BlockPos flayIntoPos = OperatorUtils.getBlockPos(args, 0, getArgc());
        Map<Identifier, TriConsumer<BlockPos, World, ServerPlayerEntity>> flayers = Mutables.checkImbueMindRecipes(flayIntoPos, ctx.getWorld());
        if (flayers.isEmpty()) {
            MishapThrowerJava.throwMishap(new MishapBadBlock(
                flayIntoPos,
                IMBUEABLE
            ));
        }

        BlockPos mindPos = OperatorUtils.getBlockPos(args, 1, getArgc());
        Mishap needMind = new MishapBadBlock(mindPos, MIND_BLOCK);
        if (!(ctx.getWorld().getBlockState(mindPos).getBlock() instanceof Mind) ||
            !(ctx.getWorld().getBlockEntity(mindPos) instanceof MindEntity)) {
            MishapThrowerJava.throwMishap(needMind);
        }
        MindEntity blockEntity = (MindEntity)ctx.getWorld().getBlockEntity(mindPos);
        if (blockEntity.mindCompletion < 100f) {
            MishapThrowerJava.throwMishap(new MishapBadBlock(
                mindPos,
                FULL_SIMPLE_MIND
            ));
        }
        blockEntity.mindCompletion = 0F;

        return new SpellAction.Result(
            new Spell(flayIntoPos, toList(flayers.values()).get(ctx.getWorld().random.nextInt(flayers.size()))),
            MediaConstants.CRYSTAL_UNIT,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final BlockPos flayIntoPos;
        public final TriConsumer<BlockPos, World, ServerPlayerEntity> flayer;

        public Spell(BlockPos flayIntoPos, TriConsumer<BlockPos, World, ServerPlayerEntity> flayer) {
            this.flayIntoPos = flayIntoPos;
            this.flayer = flayer;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            this.flayer.accept(flayIntoPos, ctx.getWorld(), getPlayerOrNull(ctx));
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
