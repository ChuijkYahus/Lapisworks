package com.luxof.lapisworks.SMindInfusions;

import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.Mutables.SMindInfusion;

import net.minecraft.block.Blocks;

public class MakeJukeboxLive extends SMindInfusion {
    @Override
    public boolean testBlock() {
        return ctx.getWorld().getBlockState(blockPos).isOf(Blocks.JUKEBOX);
    }

    @Override
    public void accept() {
        ctx.getWorld().setBlockState(blockPos, ModBlocks.LIVE_JUKEBOX_BLOCK.getDefaultState());
    }
}
