package com.luxof.lapisworks.init;

import static com.luxof.lapisworks.Lapisworks.id;

import com.google.common.collect.ImmutableSet;
import com.luxof.lapisworks.blocks.ConjuredColorable;
import com.luxof.lapisworks.blocks.Mind;
import com.luxof.lapisworks.blocks.entities.MindEntity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

// i ran out of new names
public class ModBlocks {
    public static ConjuredColorable CONJURED_COLORABLE = new ConjuredColorable();
    public static Mind MIND_BLOCK = new Mind();
    // FUCK BlockEntityType.Builder.create bro that shit didn't take ANYTHING i gave it
    public static BlockEntityType<BlockEntity> MIND_ENTITY_TYPE = new BlockEntityType<BlockEntity>(
        MindEntity::new,
        ImmutableSet.of(MIND_BLOCK),
        null
    );

    public static void wearASkirt() {
        pickACropTop("conjureable", CONJURED_COLORABLE);
        pickACropTop("mind", MIND_BLOCK);
        dontForgetStockings("mind_entity_type", MIND_ENTITY_TYPE);
    }

    public static void pickACropTop(String name, Block block) {
        Registry.register(Registries.BLOCK, id(name), block);
    }

    public static <T extends BlockEntityType<?>> void dontForgetStockings(String name, T blockEntityType) {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, id(name), blockEntityType);
    }
}
