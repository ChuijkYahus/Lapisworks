package com.luxof.lapisworks.interop.hierophantics.blocks;

import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.interop.hierophantics.Chariot;
import com.luxof.lapisworks.nocarpaltunnel.LapisBlockWithEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChariotMind extends LapisBlockWithEntity {
    public ChariotMind() {
        super(
            Settings.copy(ModBlocks.MIND_BLOCK).mapColor(DyeColor.LIGHT_GRAY)
        );
        setDefaultState(
            this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FOR_NOOBS, false)
        );
    }
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    // tempting, but dont' hurt feelings
    public static final BooleanProperty FOR_NOOBS = BooleanProperty.of("nonmaxlevel");

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(FOR_NOOBS);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction horizontal = ctx.getHorizontalPlayerFacing();
        return getDefaultState()
            .with(
                FACING,
                ctx.getPlayer().isSneaking()
                    ? horizontal
                    : horizontal.getOpposite()
            );
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new ChariotMindEntity(arg0, arg1);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        return type == Chariot.CHARIOT_MIND_ENTITY_TYPE
            ? (_1, _2, inState, ent) -> { ((ChariotMindEntity)ent).tick(state); }
            : null;
    }
}
