package com.luxof.lapisworks.blocks.bigchalk;

import com.luxof.lapisworks.init.ModBlocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
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

        Direction forward = attached == Direction.NORTH || attached == Direction.SOUTH ?
            Direction.UP : Direction.NORTH;
        Direction backward = forward.getOpposite();

        Vec3i _leftVec = forward.getVector().crossProduct(attached.getVector());
        Direction left = Direction.getFacing(_leftVec.getX(), _leftVec.getY(), _leftVec.getZ());
        Direction right = left.getOpposite();

        for (
            BlockPos otherPos : List.of(
                pos.offset(forward),
                pos.offset(forward).offset(left),
                pos.offset(forward).offset(right),
                pos.offset(left),
                pos.offset(right),
                pos.offset(backward),
                pos.offset(backward).offset(left),
                pos.offset(backward).offset(right)
            )
        ) {
            if (
                isCenter() && world.getBlockState(otherPos).getBlock() instanceof BigChalkPart ||
                !isCenter() && world.getBlockState(otherPos).getBlock() instanceof BigChalkCenter
            )
                world.breakBlock(otherPos, false);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
