package com.luxof.lapisworks.blocks.entities;

import com.luxof.lapisworks.blocks.stuff.AbstractBrewerEntity;
import com.luxof.lapisworks.client.screens.EnchBrewerScreenHandler;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.inv.EnchBrewerInv;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class EnchBrewerEntity extends AbstractBrewerEntity {
    public EnchBrewerEntity(BlockPos pos, BlockState state) {
        // 10 maxfuel because normal brewing stand has it as 20
        // 200 brewtime to brew twice as fast (normal is 400)
        super(
            ModBlocks.ENCH_BREWER_ENTITY_TYPE,
            pos,
            state,
            10,
            200,
            new EnchBrewerInv()
        );
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("gui.lapisworks.enchbrewer.name");
    }

    @Override
    public ScreenHandler createMenu(int syncID, PlayerInventory plrInv, PlayerEntity plr) {
        return new EnchBrewerScreenHandler(
            syncID,
            plrInv,
            (EnchBrewerInv)inv,
            () -> new Pair<>(this.fuel, this.brewTime),
            () -> this.currentRecipes
        );
    }
}
