package com.luxof.lapisworks.mixin.forge;

import com.luxof.lapisworks.mixinsupport.forge.BrainsweepSetterMinterface;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = {"at.petrak.hexcasting.forge.xplat.ForgeXplatImpl"}, remap = false)
public class ForgeXplatImplMixin implements BrainsweepSetterMinterface {

    @Override
    public void setBrainsweep(MobEntity mob, boolean to) {
        // HexConnect y u no open-source
        try {
            NbtCompound persistentData = (NbtCompound)mob.getClass()
                .getMethod("getPersistentData").invoke(mob);
            persistentData.putBoolean("hexcasting:brainswept", to);

            if (!mob.getWorld().isClient)
                return;

            Object network = Class.forName(
                "at.petrak.hexcasting.forge.network.ForgePacketHandler"
                ).getMethod(
                    "getNetwork",
                    Class.forName("net.minecraftforge.network.simple.SimpleChannel")
                ).invoke(null);

            Class<?> packetDistributor = Class.forName(
                "net.minecraftforge.network.PacketDistributor"
            );
            Object TRACKING_ENTITY = packetDistributor.getField("TRACKING_ENTITY");

            network.getClass()
                .getMethod(
                    "send",
                    packetDistributor,
                    Class.forName("at.petrak.hexcasting.common.msgs.IMessage")
                ).invoke(
                    network,
                    TRACKING_ENTITY.getClass()
                        .getMethod("with", Supplier.class)
                        .invoke(TRACKING_ENTITY, (Supplier<MobEntity>)(() -> mob)),
                    Class.forName("at.petrak.hexcasting.forge.network.MsgBrainsweepAck")
                        .getMethod("of", Entity.class).invoke(null)
                );
        } catch (Exception e) {}
    }
}
