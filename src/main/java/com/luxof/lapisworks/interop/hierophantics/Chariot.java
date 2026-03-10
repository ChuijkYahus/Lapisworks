package com.luxof.lapisworks.interop.hierophantics;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;

import com.google.common.collect.ImmutableSet;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.interop.hierophantics.blocks.ChariotMind;
import com.luxof.lapisworks.interop.hierophantics.blocks.ChariotMindEntity;
import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation.AmalgamationIota;

import static com.luxof.lapisworks.Lapisworks.id;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.BlockEntityFactory;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Chariot {
    public static IotaType<AmalgamationIota> AmalgamIotaType = iotaType(
        "amalgamation", AmalgamationIota.TYPE
    );

    public static ChariotMind CHARIOT_MIND = new ChariotMind();
    public static BlockEntityType<ChariotMindEntity> CHARIOT_MIND_ENTITY_TYPE =
        beType(ChariotMindEntity::new, CHARIOT_MIND);
    public static BlockItem CHARIOT_MIND_ITEM =
        new BlockItem(CHARIOT_MIND, new FabricItemSettings().maxCount(64));



    public static void readTarotCards() {
        block("chariotmind", CHARIOT_MIND);
        registerBeType("chariotmind", CHARIOT_MIND_ENTITY_TYPE);
        ModItems.registerItem("chariotmind", CHARIOT_MIND_ITEM);
    }



    private static <IOTA extends Iota> IotaType<IOTA> iotaType(
        String name,
        IotaType<IOTA> type
    ) {
        return Registry.register(HexIotaTypes.REGISTRY, id(name), type);
    }

    private static <BLOCK extends Block> BLOCK block(
        String name,
        BLOCK block
    ) {
        return Registry.register(Registries.BLOCK, id(name), block);
    }
    // mark, this is *good news*. we can finally be bees.
    private static <BE extends BlockEntity> BlockEntityType<BE> beType(
        BlockEntityFactory<BE> factory,
        Block block
    ) {
        return new BlockEntityType<>(factory, ImmutableSet.of(block), null);
    }

    private static void registerBeType(
        String name,
        BlockEntityType<? extends BlockEntity> beType
    ) {
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            id(name),
            beType
        );
    }
}
