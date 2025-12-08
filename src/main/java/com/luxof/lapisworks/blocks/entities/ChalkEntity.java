package com.luxof.lapisworks.blocks.entities;

import com.luxof.lapisworks.init.ModBlocks;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChalkEntity extends BlockEntity {
    public ChalkEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CHALK_ENTITY_TYPE, pos, state);
    }

    HexPattern[] patterns = new HexPattern[5];

    public void tick(World world, BlockPos pos, BlockState bs) {}
}
