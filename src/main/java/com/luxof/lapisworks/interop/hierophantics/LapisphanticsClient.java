package com.luxof.lapisworks.interop.hierophantics;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.minecraft.client.render.RenderLayer;

// is Lapisphantics a cooler name than Chariot?
// hard to decide
public class LapisphanticsClient {
    public static void doMyShitTwin() {
        BlockRenderLayerMap.INSTANCE.putBlock(Chariot.CHARIOT_MIND, RenderLayer.getTranslucent());
    }
}
