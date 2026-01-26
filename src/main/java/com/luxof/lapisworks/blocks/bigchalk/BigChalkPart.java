package com.luxof.lapisworks.blocks.bigchalk;

import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.Lapisworks.get3x3;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

// big chalk is after me!
public class BigChalkPart extends Block {
    public BigChalkPart() {
        super(
            Settings.copy(ModBlocks.CHALK)
        );
        setDefaultState(
            getDefaultState()
                .with(ATTACHED, Direction.DOWN)
        );
    }

    public static final EnumProperty<Direction> ATTACHED = EnumProperty.of(
        "attached",
        Direction.class
    );
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ATTACHED);
    }

    private static VoxelShape DOWN_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    private static VoxelShape UP_SHAPE = Block.createCuboidShape(0, 15, 0, 16, 16, 16);
    private static VoxelShape NORTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 1);
    private static VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0, 0, 15, 16, 16, 16);
    private static VoxelShape WEST_SHAPE = Block.createCuboidShape(0, 0, 0, 1, 16, 16);
    private static VoxelShape EAST_SHAPE = Block.createCuboidShape(15, 0, 0, 16, 16, 16);
    @Override
    public VoxelShape getOutlineShape(
        BlockState pState,
        BlockView pLevel,
        BlockPos pPos,
        ShapeContext pContext
    ) {
        Direction attachedTo = pState.get(ATTACHED);

        return switch (attachedTo) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit
    ) {
        // always delegate onUse to the center of the multiblock
        Direction attached = state.get(ATTACHED);

        for (BlockPos otherPos : get3x3(pos, attached, false)) {
            BlockState otherState = world.getBlockState(otherPos);
            if (!otherState.isOf(ModBlocks.BIG_CHALK_CENTER)) continue;
            return otherState.getBlock().onUse(otherState, world, otherPos, player, hand, hit);
        }

        return ActionResult.PASS;
    }

    @Override
    public void neighborUpdate(
        BlockState state,
        World world,
        BlockPos pos,
        Block fromBlock,
        BlockPos fromPos,
        boolean notify
    ) {
        Direction comingFrom = Direction.fromVector(
            fromPos.getX() - pos.getX(),
            fromPos.getY() - pos.getY(),
            fromPos.getZ() - pos.getZ()
        );
        if (comingFrom == state.get(ATTACHED))
            world.breakBlock(pos, false);
    }

    protected boolean isCenter() { return false; }
    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(
        BlockState state,
        World world,
        BlockPos pos,
        BlockState newState,
        boolean moved
    ) {
        if (state.getBlock() == newState.getBlock()) return;
        Direction attached = state.get(ATTACHED);

        for (BlockPos otherPos : get3x3(pos, attached, false)) {
            if (
                isCenter() && world.getBlockState(otherPos).getBlock() instanceof BigChalkPart ||
                !isCenter() && world.getBlockState(otherPos).getBlock() instanceof BigChalkCenter
            )
                world.breakBlock(otherPos, false);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
