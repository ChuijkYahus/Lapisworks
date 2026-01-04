package com.luxof.lapisworks.mindinfusions;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.common.blocks.circles.BlockEmptyImpetus;
import at.petrak.hexcasting.common.lib.HexBlocks;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.blocks.SimpleImpetus;
import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.init.Mutables.SMindInfusion;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MakeSimpleImpetus extends SMindInfusion {
    private BlockState state;
    private ServerWorld world;

    @Override
    public SMindInfusion setUp(
        BlockPos blockPos,
        CastingEnvironment ctx,
        List<? extends Iota> hexStack,
        VAULT vault
    ) {
        this.world = ctx.getWorld();
        this.state = world.getBlockState(blockPos);
        return super.setUp(blockPos, ctx, hexStack, vault);
    }

    @Override public boolean testBlock() {
        return ctx.isEnlightened()
            && ctx.getWorld().getBlockState(blockPos).isOf(HexBlocks.IMPETUS_EMPTY);
    }

    @Override
    public void mishapIfNeeded() {
        int fetched = vault.fetch(Mutables::isAmel, Flags.PRESET_Stacks_InvItem_UpToHotbar);
        if (fetched < 5) {
            throw new MishapNotEnoughItems(AMEL, fetched, 5);
        }
        if (
            ctx.getCastingEntity() == null ||
            !(ctx.getCastingEntity() instanceof ServerPlayerEntity)
        )
            throw new MishapBadCaster();
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
