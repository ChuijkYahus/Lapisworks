package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.HexAPI;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.luxof.lapisworks.LapisworksServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = HexAPI.class, remap = false)
public interface HexAPIMixin {
    @ModifyReturnValue(method = "getActionI18nKey", at = @At("RETURN"))
    private String lapisworks$getRobbieKey(String original) {
        return original +
            (original.endsWith("lapisworks:robbie_exalt")
                ? String.valueOf(LapisworksServer.ROBBIES_EXALT_VARIANT) : "");
    }
}
