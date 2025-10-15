package com.luxof.lapisworks.client;

import com.luxof.lapisworks.init.ModItems;

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

/**
 * @author WireSegal
 * Created at 9:50 AM on 7/25/22.
 * <p>(added because LensTrinketRenderer also has it)
 */
public class JarTrinketRenderer implements TrinketRenderer {
    // if i unravelled that many arguments it'd be even worse to read bruh wtf
    @Override
    @SuppressWarnings("unchecked")
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity,
            float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
            float headPitch) {
        if (!(stack.isOf(ModItems.AMEL_JAR) &&
                contextModel instanceof PlayerEntityModel playerModel &&
                entity instanceof AbstractClientPlayerEntity player)) return;
        matrices.push();
        TrinketRenderer.followBodyRotations(entity, playerModel);
        // PRAYING this translates to the belt
        // this does NOT translate to the belt :sob: :pray:
        //TrinketRenderer.translateToRightLeg(matrices, playerModel, player);
        TrinketRenderer.translateToChest(matrices, playerModel, player);

        matrices.translate(-0.2, 0.1, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrices.scale(0.35f, 0.35f, 0.35f);

        MinecraftClient instance = MinecraftClient.getInstance();
        // this ItemRenderer shit seems EXTREMELY! useful for my 4-armed project
        instance.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, light,
            OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, instance.world, 0);
        matrices.pop();
    }
}
