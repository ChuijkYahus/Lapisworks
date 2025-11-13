package com.luxof.lapisworks.init;

import static com.luxof.lapisworks.Lapisworks.id;
import static net.minecraft.registry.Registry.register;

import com.luxof.lapisworks.client.screens.EnchBrewerScreenHandler;

import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreens {
    public static final ScreenHandlerType<EnchBrewerScreenHandler> ENCH_BREWER_SCREEN_HANDLER = register(
        Registries.SCREEN_HANDLER,
        id("ench_brewer"),
        new ScreenHandlerType<>(EnchBrewerScreenHandler::new, FeatureSet.empty())
    );

    public static void whatWasThatTF2CommentAboutMakingBadGUICodeSoYouDontHaveToTouchItAgain() {}
}
