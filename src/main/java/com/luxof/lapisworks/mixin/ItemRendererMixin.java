package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.utils.NBTHelper;

import static com.luxof.lapisworks.LapisworksIDs.IS_IN_CRADLE;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

// who up rendering they items rn
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(
        method = "renderItem",
        at = @At("HEAD")
    )
    public void render(
        ItemStack stack,
        ModelTransformationMode renderMode,
        boolean leftHanded,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        int overlay,
        BakedModel model,
        CallbackInfo ci
    ) {
        if (NBTHelper.contains(stack, IS_IN_CRADLE)) matrices.translate(0.0, -0.25, 0.0);
    }
}
