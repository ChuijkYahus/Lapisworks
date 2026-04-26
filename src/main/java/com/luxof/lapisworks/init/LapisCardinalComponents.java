package com.luxof.lapisworks.init;

import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder;
import at.petrak.hexcasting.fabric.cc.adimpl.CCEntityIotaHolder;

import static at.petrak.hexcasting.fabric.cc.HexCardinalComponents.IOTA_HOLDER;

import com.luxof.lapisworks.mixinsupport.CollarControllable;

import java.util.function.Function;

import dev.onyxstudios.cca.api.v3.component.ComponentFactory;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;

public class LapisCardinalComponents implements EntityComponentInitializer {

    @SuppressWarnings("null")
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(
            CatEntity.class,
            IOTA_HOLDER,
            wrapItemEntityDelegate(ToCollarable::new)
        );
    }

    // yoink!
    private <E extends Entity> ComponentFactory<E, CCEntityIotaHolder.Wrapper> wrapItemEntityDelegate(
        Function<E, ItemDelegatingEntityIotaHolder> make
    ) {
        return e -> new CCEntityIotaHolder.Wrapper(make.apply(e));
    }

    public static class ToCollarable extends ItemDelegatingEntityIotaHolder {
        public ToCollarable(Entity entity) {
            super(
                ((CollarControllable)entity)::getCollar,
                stack -> { ((CollarControllable)entity).setCollar(stack); }
            );
        }
    }
}
