package com.luxof.lapisworks.client;

import com.luxof.lapisworks.items.FocusNecklace;

import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE2;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE2_WORN;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE_WORN;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;

import java.util.Map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

/**
 * @author WireSegal
 * Created at 9:50 AM on 7/25/22.
 * <p>(added because LensTrinketRenderer also has it even though the code is different)
 */
public class NecklaceTrinketRenderer implements TrinketRenderer {
    // MPPs gave up on me, so this is what I'ma do now.
    private final ItemStack WORN_1 = new ItemStack(FOCUS_NECKLACE_WORN);
    private final ItemStack WORN_2 = new ItemStack(FOCUS_NECKLACE2_WORN);
    private final Map<Item, ItemStack> mapNecklaceToRenderStack = Map.of(
        FOCUS_NECKLACE, WORN_1,
        FOCUS_NECKLACE2, WORN_2
    );

    @Override
    @SuppressWarnings("unchecked")
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity,
            float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
            float headPitch) {
        if (!(stack.getItem() instanceof FocusNecklace &&
                contextModel instanceof PlayerEntityModel playerModel &&
                entity instanceof AbstractClientPlayerEntity player)) return;
        matrices.push();
        TrinketRenderer.followBodyRotations(entity, playerModel);
        TrinketRenderer.translateToFace(matrices, playerModel, player, 0f, 0f);

        matrices.translate(0.0, 9.0/16.0, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrices.scale(0.7f, 0.7f, 0.7f);

        MinecraftClient instance = MinecraftClient.getInstance();
        instance.getItemRenderer().renderItem(mapNecklaceToRenderStack.get(stack.getItem()),
            ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers,
            instance.world, 0);
        matrices.pop();
    }
}
