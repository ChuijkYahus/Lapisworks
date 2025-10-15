package com.luxof.lapisworks.init;

import com.luxof.lapisworks.interop.hexical.LargeItemEntity;

import static com.luxof.lapisworks.Lapisworks.id;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities {
    public static final EntityType<LargeItemEntity> LARGE_ITEM_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        id("large_item"),
        FabricEntityTypeBuilder.<LargeItemEntity>create(SpawnGroup.MISC, LargeItemEntity::new)
            .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
            .trackRangeBlocks(10)
            .trackedUpdateRate(10)
            .build()
    );
    public static void questionWhyIMustDoThis() {

    }
}
