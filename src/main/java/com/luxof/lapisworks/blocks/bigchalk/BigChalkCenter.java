package com.luxof.lapisworks.blocks.bigchalk;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BigChalkCenter extends BigChalkPart implements BlockEntityProvider {
    public BigChalkCenter() {
        super();
        setDefaultState(
            getDefaultState()
                .with(ATTACHED, Direction.DOWN)
                .with(FACING, Direction.NORTH)
        );
    }

    public static final EnumProperty<Direction> FACING = EnumProperty.of(
        "facing",
        Direction.class
    );
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ATTACHED);
        builder.add(FACING);
    }

    @Override
    protected boolean isCenter() { return true; }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new BigChalkCenterEntity(arg0, arg1);
    }
}
