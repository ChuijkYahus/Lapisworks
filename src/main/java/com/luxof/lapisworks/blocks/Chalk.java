package com.luxof.lapisworks.blocks;

import com.luxof.lapisworks.blocks.entities.ChalkEntity;
import com.luxof.lapisworks.init.ModBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Chalk extends BlockWithEntity {
    public Chalk() {
        super(
            Settings.copy(Blocks.REDSTONE_WIRE)
                .mapColor(DyeColor.PINK)
                .dropsNothing()
        );
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChalkEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (type == ModBlocks.CHALK_ENTITY_TYPE) {
            return (a, b, c, bE) -> ((ChalkEntity)bE).tick(a, b, c);
        } else return null;
    }
}
