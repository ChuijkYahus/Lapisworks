package com.luxof.lapisworks.mindinfusions;

import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.init.Mutables.SMindInfusion;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;

public class UnflayVillager extends SMindInfusion {
    @Override
    public boolean testEntity() {
        return entity instanceof VillagerEntity villager
            && IXplatAbstractions.INSTANCE.isBrainswept(villager);
    }

    @Override
    public void accept() {
        VillagerEntity villager = (VillagerEntity)entity;
        HexCardinalComponents.BRAINSWEPT.get(villager).setBrainswept(false);
        villager.setExperience(0);
        villager.setVillagerData(
            villager.getVillagerData()
                .withLevel(1)
                .withProfession(VillagerProfession.NONE)
        );
        villager.reinitializeBrain(ctx.getWorld());
    }
}
