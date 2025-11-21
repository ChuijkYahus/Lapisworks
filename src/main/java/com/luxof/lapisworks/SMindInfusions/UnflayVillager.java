package com.luxof.lapisworks.SMindInfusions;

import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.init.Mutables.SMindInfusion;

import net.minecraft.entity.passive.VillagerEntity;

public class UnflayVillager extends SMindInfusion {
    @Override
    public boolean testEntity() {
        return entity instanceof VillagerEntity villager
            && IXplatAbstractions.INSTANCE.isBrainswept(villager);
    }

    @SuppressWarnings("null")
    @Override
    public void accept() {
        VillagerEntity villager = (VillagerEntity)entity;
        HexCardinalComponents.BRAINSWEPT.get(villager).setBrainswept(false);
    }
}
