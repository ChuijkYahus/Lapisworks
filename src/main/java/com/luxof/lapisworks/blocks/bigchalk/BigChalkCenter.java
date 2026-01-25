package com.luxof.lapisworks.blocks.bigchalk;

import com.luxof.lapisworks.init.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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

    @Override
    public ActionResult onUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit
    ) {
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isOf(ModItems.CHALK)) return ActionResult.PASS;

        BigChalkCenterEntity bE = (BigChalkCenterEntity)world.getBlockEntity(pos);
        bE.altTexture = !bE.altTexture;
        bE.save();
        
        return ActionResult.SUCCESS;
    }
}
