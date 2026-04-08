package com.luxof.lapisworks.client.collar;

import com.luxof.lapisworks.init.ModItems;

import static com.luxof.lapisworks.Lapisworks.id;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class CollarItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final Identifier collarModelId = id("item/collar_with_model");
    public static final ItemStack collarWithModelStack = new ItemStack(ModItems.COLLAR_WITH_MODEL);

    @Override
    public void render(
        ItemStack stack,
        ModelTransformationMode mode,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        int overlay
    ) { render(stack, null, mode, matrices, vertexConsumers, light, overlay); }
    
    public void render(
        ItemStack stack,
        @Nullable LivingEntity entity,
        ModelTransformationMode mode,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        int overlay
    ) {
        MinecraftClient client = MinecraftClient.getInstance();

        BakedModelManager modelManager = client.getBakedModelManager();
        BakedModel collarModel = modelManager.getModel(collarModelId);

        matrices.push();
        matrices.translate(.5, .5, .5);
        collarModel.getTransformation().getTransformation(mode).apply(false, matrices);
        LapisCollarAdditions.renderAll(stack, entity, mode, matrices, vertexConsumers, light, overlay);
        matrices.pop();
    }
}
