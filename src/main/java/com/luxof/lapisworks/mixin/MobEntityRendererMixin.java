package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.CollarControllable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntityRenderer.class)
public class MobEntityRendererMixin {
    @Inject(
        method = "render",
        at = @At("TAIL")
    )
    public void lapisworks$renderCollar(
        MobEntity mobEntity,
        float f,
        float g,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumerProvider,
        int i,
        CallbackInfo ci
    ) {
        if (!(mobEntity instanceof CollarControllable collarable)) return;

        matrices.push();
        //matrices.translate(0.0, 0.0, 0.35);
        //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.scale(0.7f, 0.7f, 0.7f);
        matrices.translate(0.0, 0.5, 0.0);

        ItemStack collar = collarable.getCollar();
        MinecraftClient.getInstance().getItemRenderer().renderItem(
            collar,
            ModelTransformationMode.HEAD,
            i,
            OverlayTexture.DEFAULT_UV,
            matrices,
            vertexConsumerProvider,
            mobEntity.getWorld(),
            0
        );

        matrices.pop();
    }
}
