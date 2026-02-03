package com.luxof.lapisworks.interop.hierophantics.blocks;

import com.luxof.lapisworks.interop.hierophantics.Chariot;
import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;
import com.luxof.lapisworks.nocarpaltunnel.LapisBlockEntity;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ChariotMindEntity extends LapisBlockEntity {
    public NbtCompound storedAmalgamationNbt;
    public ChariotMindEntity(BlockPos pos, BlockState state) {
        super(Chariot.CHARIOT_MIND_ENTITY_TYPE, pos, state);
        storedAmalgamationNbt = new Amalgamation(
            pos,
            state.get(ChariotMind.FOR_NOOBS),
            0,
            0.0,
            List.of()
        ).serialize();
    }

    public Amalgamation getAmalgamation(ServerWorld world) {
        return new Amalgamation(storedAmalgamationNbt, world);
    }

    @Override
    public void save(NbtCompound nbt) {
        nbt.put("amalgam", storedAmalgamationNbt);
    }

    @Override
    public void load(NbtCompound nbt) {
        storedAmalgamationNbt = nbt.getCompound("amalgam");
    }

    @Override
    public void tick(BlockState state) {}
}
