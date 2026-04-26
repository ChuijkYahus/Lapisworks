package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.fabric.xplat.FabricXplatImpl;

import com.luxof.lapisworks.mixinsupport.forge.BrainsweepSetterMinterface;

import net.minecraft.entity.mob.MobEntity;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = FabricXplatImpl.class, remap = false)
public class FabricXplatImplMixin implements BrainsweepSetterMinterface {

    @Override
    public void setBrainsweep(MobEntity mob, boolean to) {
        HexCardinalComponents.BRAINSWEPT.get(mob).setBrainswept(to);
    }
    
}
