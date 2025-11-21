package com.luxof.lapisworks.SMindInfusions;

import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.common.blocks.circles.BlockEmptyImpetus;
import at.petrak.hexcasting.common.lib.HexBlocks;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.blocks.SimpleImpetus;
import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.init.Mutables.SMindInfusion;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class MakeSimpleImpetus extends SMindInfusion {
    private BlockState state;
    private ServerWorld world;

    @Override
    public boolean testBlock() {
        world = ctx.getWorld();
        state = world.getBlockState(blockPos);
        return ctx.isEnlightened()
            && state.isOf(HexBlocks.IMPETUS_EMPTY);
    }

    @Override
    public void mishapIfNeeded() {
        int got = vault.fetch(Mutables::isAmel, Flags.PRESET_Stacks_InvItem_UpToHotbar);
        if (got < 5) MishapThrowerJava.throwMishap(new MishapNotEnoughItems(AMEL, got, 5));
        else if (ctx.getCastingEntity() == null
            || !(ctx.getCastingEntity() instanceof ServerPlayerEntity)) {
            MishapThrowerJava.throwMishap(new MishapBadCaster());
        } else if (state.get(BlockEmptyImpetus.ENERGIZED)) {

        }
    }

    @Override
    public void accept() {
        vault.drain(Mutables::isAmel, 5, Flags.PRESET_Stacks_InvItem_UpToHotbar);
        world.setBlockState(
            blockPos,
            ModBlocks.SIMPLE_IMPETUS.getDefaultState()
                .with(
                    SimpleImpetus.FACING,
                    state.get(BlockEmptyImpetus.FACING)
                ).with(
                    SimpleImpetus.ENERGIZED,
                    state.get(BlockEmptyImpetus.ENERGIZED)
                )
        );
        // PLEASE be there PLEASE be there PLEASE be there..
        // IT'S THERE
        SimpleImpetusEntity bE = ((SimpleImpetusEntity)world.getBlockEntity(blockPos));
        bE.setPlayer((ServerPlayerEntity)ctx.getCastingEntity());
        bE.markDirty();
    }
}
