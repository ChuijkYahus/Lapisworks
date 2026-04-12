package com.luxof.lapisworks.init;

import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.fabric.cc.adimpl.CCEntityIotaHolder;

import static at.petrak.hexcasting.fabric.cc.HexCardinalComponents.IOTA_HOLDER;

import com.luxof.lapisworks.client.collar.additions.FocusCollarAddition;
import com.luxof.lapisworks.mixinsupport.CollarControllable;

import static com.luxof.lapisworks.init.ModItems.COLLAR;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class LapisCardinalComponents implements EntityComponentInitializer {

    @SuppressWarnings("null")
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(
            CatEntity.class,
            IOTA_HOLDER,
            CollarableEntityIotaHolder::new
        );
    }

    public static class CollarableEntityIotaHolder extends CCEntityIotaHolder {
        public final CollarControllable collarable;

        public CollarableEntityIotaHolder(Entity entity) {
            collarable = (CollarControllable)entity;
        }

        @Override
        public @Nullable NbtCompound readIotaTag() {
            ItemStack collar = collarable.getCollar();
            if (!(
                collar.isOf(COLLAR) &&
                COLLAR.getAdditions(collar).contains(FocusCollarAddition.ID)
            )) return null;
            return NBTHelper.getCompound(collar, "stored_iota");
        }

        @Override
        public boolean writeIota(@Nullable Iota iota, boolean simulate) {
            ItemStack collar = collarable.getCollar();
            if (!(
                collar.isOf(COLLAR) &&
                COLLAR.getAdditions(collar).contains(FocusCollarAddition.ID)
            )) return false;
            NBTHelper.putCompound(
                collar, "stored_iota", IotaType.serialize(iota == null ? new NullIota() : iota)
            );
            return true;
        }

        @Override
        public boolean writeable() {
            return COLLAR.getAdditions(collarable.getCollar()).contains(FocusCollarAddition.ID);
        }
        
    }

    public static class ToCollarableEntity extends ItemDelegatingEntityIotaHolder {
        public <T extends Entity> ToCollarableEntity(T entity) {
            super(
                () -> ((CollarControllable)entity).getCollar(),
                (ItemStack stack) -> { ((CollarControllable)entity).setCollar(stack); }
            );
        }
    }
}
