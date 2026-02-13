package com.luxof.lapisworks.interop.hierophantics;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.mixinsupport.ChariotServerPlayer;

import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class ChariotInterface {
    private static int argc = 2;

    /** null = couldn't work. */
    @Nullable
    public static SpellAction.Result tryImbueChariotMind(
        List<? extends Iota> args,
        CastingEnvironment ctx
    ) {
        ServerWorld world = ctx.getWorld();
        BlockPos flayInto = OperatorUtils.getBlockPos(args, 0, argc);
        //LOGGER.info("flay into pos: " + flayInto.toString());
        //LOGGER.info("instance of flay bed? " + String.valueOf(world.getBlockEntity(flayInto) instanceof robotgiggle.hierophantics.blocks.FlayBedBlockEntity flayBed));
        if (
            !(world.getBlockEntity(flayInto) instanceof
            robotgiggle.hierophantics.blocks.FlayBedBlockEntity flayBed)
        )
            return null;
        var player = flayBed.getSleeper(world) instanceof ServerPlayerEntity plr
            ? plr
            : null;

        BlockPos flayFrom = OperatorUtils.getBlockPos(args, 1, argc);
        throwIfEmpty(
            world.getBlockEntity(flayFrom, Chariot.CHARIOT_MIND_ENTITY_TYPE),
            new MishapBadBlock(flayFrom, Chariot.CHARIOT_MIND.getName())
        );

        return new SpellAction.Result(
            new Spell(flayFrom, player),
            (long)(MediaConstants.CRYSTAL_UNIT * 5),
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 10, 50)),
            1L
        );
    }

    public static class Spell implements RenderedSpell {
        public final BlockPos flayFrom;
        public final ServerPlayerEntity flayInto;

        public Spell(BlockPos flayFrom, ServerPlayerEntity flayInto) {
            this.flayFrom = flayFrom;
            this.flayInto = flayInto;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ServerWorld world = ctx.getWorld();

            var chariotMind = world.getBlockEntity(flayFrom, Chariot.CHARIOT_MIND_ENTITY_TYPE);
            if (flayInto != null)
                ((ChariotServerPlayer)flayInto).getFusedAmalgamations().add(
                    chariotMind.get().getAmalgamation(world)
                );
            world.setBlockState(flayFrom, ModBlocks.MIND_BLOCK.getDefaultState());
        }

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }
}
