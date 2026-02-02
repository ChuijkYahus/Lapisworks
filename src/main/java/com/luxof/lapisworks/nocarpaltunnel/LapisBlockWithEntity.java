package com.luxof.lapisworks.nocarpaltunnel;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class LapisBlockWithEntity extends BlockWithEntity {
    protected <T extends Comparable<?>> LapisBlockWithEntity(
        Settings settings
    ) {
        super(settings);
    }

    @Override
    public abstract BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1);

    @Override
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    );

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
