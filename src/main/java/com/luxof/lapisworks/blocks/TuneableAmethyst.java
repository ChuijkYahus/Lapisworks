package com.luxof.lapisworks.blocks;

import com.luxof.lapisworks.blocks.entities.TuneableAmethystEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class TuneableAmethyst extends BlockWithEntity {
    public TuneableAmethyst() {
        super(
            Settings.create()
                .solid()
                .nonOpaque()
                .strength(1.5f)
                .mapColor(DyeColor.PINK)
                .pistonBehavior(PistonBehavior.DESTROY)
                .sounds(BlockSoundGroup.AMETHYST_CLUSTER)
        );
        setDefaultState(
            this.stateManager.getDefaultState()
                .with(STAGE, 0)
        );
    }

    public static final IntProperty STAGE = IntProperty.of("stage", 0, 2);
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new TuneableAmethystEntity(arg0, arg1);
    }
}
