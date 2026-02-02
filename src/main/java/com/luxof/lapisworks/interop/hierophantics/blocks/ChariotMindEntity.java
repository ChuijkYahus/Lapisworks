package com.luxof.lapisworks.interop.hierophantics.blocks;

import com.luxof.lapisworks.interop.hierophantics.Chariot;
import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;
import com.luxof.lapisworks.nocarpaltunnel.LapisBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class ChariotMindEntity extends LapisBlockEntity {
    public Amalgamation storedAmalgamation;
    public ChariotMindEntity(BlockPos pos, BlockState state) {
        super(Chariot.CHARIOT_MIND_ENTITY_TYPE, pos, state);
        storedAmalgamation = new Amalgamation(
            0,
            0.0,
            state.get(ChariotMind.FOR_NOOBS)
        );
    }

    @Override
    public void save(NbtCompound nbt) {
        nbt.put("amalgam", storedAmalgamation.serialize());
    }

    @Override
    public void load(NbtCompound nbt) {
        storedAmalgamation = new Amalgamation(nbt.getCompound("amalgam"));
    }

    @Override
    public void tick(BlockState state) {}
}
