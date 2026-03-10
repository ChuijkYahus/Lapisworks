package com.luxof.lapisworks.init;

import com.luxof.lapisworks.client.screens.ChalkWithPatternScreen;
import com.luxof.lapisworks.client.screens.ChalkWithPatternScreenHandler;
import com.luxof.lapisworks.client.screens.EnchBrewerScreen;
import com.luxof.lapisworks.client.screens.EnchBrewerScreenHandler;

import static com.luxof.lapisworks.Lapisworks.id;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

import static net.minecraft.registry.Registry.register;

public class ModScreens {
    public static final ScreenHandlerType<EnchBrewerScreenHandler> ENCH_BREWER_SCREEN_HANDLER = register(
        Registries.SCREEN_HANDLER,
        id("ench_brewer"),
        new ScreenHandlerType<>(EnchBrewerScreenHandler::new, FeatureSet.empty())
    );
    public static final ScreenHandlerType<ChalkWithPatternScreenHandler> CHALK_WITH_PATTERN_SCREEN_HANDLER = register(
        Registries.SCREEN_HANDLER,
        id("chalk_with_pattern"),
        new ExtendedScreenHandlerType<>(ChalkWithPatternScreenHandler::new)
    );

    public static void whatWasThatTF2CommentAboutMakingBadGUICodeSoYouDontHaveToTouchItAgain() {}
    
    public static void registerOnClient() {
        HandledScreens.register(ModScreens.ENCH_BREWER_SCREEN_HANDLER, EnchBrewerScreen::new);
        HandledScreens.register(ModScreens.CHALK_WITH_PATTERN_SCREEN_HANDLER, ChalkWithPatternScreen::new);
    }
}
