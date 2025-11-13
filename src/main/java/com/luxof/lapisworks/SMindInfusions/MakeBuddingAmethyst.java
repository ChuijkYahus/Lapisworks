package com.luxof.lapisworks.SMindInfusions;

import com.luxof.lapisworks.init.Mutables.SMindInfusion;

import net.minecraft.block.Blocks;

public class MakeBuddingAmethyst extends SMindInfusion {
    @Override
    public boolean testBlock() {
        return ctx.getWorld().getBlockState(blockPos).isOf(Blocks.AMETHYST_BLOCK);
    }

    @Override
    public void accept() {
        ctx.getWorld().setBlockState(blockPos, Blocks.BUDDING_AMETHYST.getDefaultState());
    }
}
