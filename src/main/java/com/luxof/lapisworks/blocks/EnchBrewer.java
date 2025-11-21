package com.luxof.lapisworks.blocks;

import com.luxof.lapisworks.blocks.entities.EnchBrewerEntity;
import com.luxof.lapisworks.blocks.stuff.AbstractBrewer;
import com.luxof.lapisworks.init.ModBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnchBrewer extends AbstractBrewer {
    public EnchBrewer() {
        super(
            Settings.create()
                .mapColor(MapColor.RAW_IRON_PINK)
                .requiresTool()
                .strength(0.5f)
                .luminance(any -> 1)
                .nonOpaque()
        );
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new EnchBrewerEntity(arg0, arg1);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (type == ModBlocks.ENCH_BREWER_ENTITY_TYPE) {
            return (a, b, c, bE) -> ((EnchBrewerEntity)bE).tick(a, b, c);
        } else return null;
    }
}
