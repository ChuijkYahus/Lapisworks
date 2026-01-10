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
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.Mutables.Mutables.SMindInfusions;
import com.luxof.lapisworks.init.Mutables.SMindInfusion;
import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.LapisworksIDs.ENTITY_INFUSEABLE_WITH_SMIND;
import static com.luxof.lapisworks.LapisworksIDs.FULL_SIMPLE_MIND;
import static com.luxof.lapisworks.LapisworksIDs.INFUSEABLE_WITH_SMIND;
import static com.luxof.lapisworks.LapisworksIDs.MIND_BLOCK;
import static com.luxof.lapisworks.MishapThrowerJava.getBlockPosOrEntity;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import com.mojang.datafixers.util.Either;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class FlayArtMind implements SpellAction {
    public int getArgc() {
        return 2;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        Either<BlockPos, Entity> flayInto = getBlockPosOrEntity(args, 0, getArgc());
        BlockPos flayIntoPos = flayInto.left().orElse(null);
        Entity flayIntoEntity = flayInto.right().orElse(null);

        VAULT vault = ((GetVAULT)ctx).grabVAULT();
        // stop complaing about maybe not init fuckahh :pray:
        SMindInfusion infusionRecipe = new SMindInfusion();

        if (flayIntoPos != null) {
            infusionRecipe = SMindInfusions
                .filterAll(flayIntoPos, ctx, args, vault)
                .values().stream().findFirst()
                .orElseThrow(() -> new MishapBadBlock(flayIntoPos, INFUSEABLE_WITH_SMIND));
        }
        else if (flayIntoEntity != null) {
            infusionRecipe = SMindInfusions.filterAll(flayIntoEntity, ctx, args, vault )
                .values().stream().findFirst()
                .orElseThrow(() -> new MishapBadEntity(flayIntoEntity, ENTITY_INFUSEABLE_WITH_SMIND));
        }

        infusionRecipe.mishapIfNeeded();

        // be funny. come on. try it.
        BlockPos mindPos = OperatorUtils.getBlockPos(args, 1, getArgc());
        MindEntity blockEntity = throwIfEmpty(
            ctx.getWorld().getBlockEntity(mindPos, ModBlocks.MIND_ENTITY_TYPE),
            new MishapBadBlock(mindPos, MIND_BLOCK)
        );
        if (blockEntity.mindCompletion < 100f) {
            throw new MishapBadBlock(mindPos, FULL_SIMPLE_MIND);
        }
        blockEntity.mindCompletion = 0F;

        return new SpellAction.Result(
            new Spell(flayIntoPos, infusionRecipe),
            MediaConstants.CRYSTAL_UNIT,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final BlockPos flayIntoPos;
        public final SMindInfusion flayer;

        public Spell(BlockPos flayIntoPos, SMindInfusion flayer) {
            this.flayIntoPos = flayIntoPos;
            this.flayer = flayer;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            this.flayer.accept();
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
