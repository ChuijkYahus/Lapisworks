package com.luxof.lapisworks.init;

import at.petrak.hexcasting.common.lib.HexBlocks;

import static com.luxof.lapisworks.Lapisworks.id;

import java.util.Set;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.poi.PointOfInterestType;

public class ModPOIs {
    public static final RegistryKey<PointOfInterestType> SLATES_KEY = RegistryKey.of(
        RegistryKeys.POINT_OF_INTEREST_TYPE,
        id("slates_poi")
    );
    public static final PointOfInterestType SLATES_POI_TYPE = PointOfInterestHelper.register(
        id("slates_poi"),
        1,
        1,
        Set.copyOf(HexBlocks.SLATE.getStateManager().getStates())
    );
    public static void crawlOutOfHell() {
    }
}
