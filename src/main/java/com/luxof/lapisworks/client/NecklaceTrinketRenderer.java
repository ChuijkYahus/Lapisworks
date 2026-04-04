package com.luxof.lapisworks.client;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class NecklaceTrinketRenderer implements TrinketRenderer {
    private final ItemStack DISPLAY;

    public NecklaceTrinketRenderer(ItemStack displayNecklace) {
        this.DISPLAY = displayNecklace;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity,
            float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
            float headPitch) {

        if (!(contextModel instanceof PlayerEntityModel playerModel &&
                entity instanceof AbstractClientPlayerEntity player)) return;

        matrices.push();
        TrinketRenderer.followBodyRotations(entity, playerModel);
        TrinketRenderer.translateToFace(matrices, playerModel, player, 0f, 0f);

        matrices.translate(0.0, 10.0/24.0, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrices.scale(0.7f, 0.7f, 0.7f);

        MinecraftClient instance = MinecraftClient.getInstance();
        instance.getItemRenderer().renderItem(
            DISPLAY,
            ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers,
            instance.world, 0);
        matrices.pop();
    }
}
