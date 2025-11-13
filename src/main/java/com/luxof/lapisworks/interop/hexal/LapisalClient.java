package com.luxof.lapisworks.interop.hexal;

import com.luxof.lapisworks.interop.hexal.blocks.EnchSlipwayRenderer;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

public class LapisalClient {
    public static void beCoolOnTheClient() {
        BlockEntityRendererRegistry.register(
            Lapisal.ENCH_SLIPWAY_ENTITY_TYPE,
            EnchSlipwayRenderer::new
        );
    }
}
