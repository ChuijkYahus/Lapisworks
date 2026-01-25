package com.luxof.lapisworks.blocks.bigchalk;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BigChalkCenterRenderer implements BlockEntityRenderer<BigChalkCenterEntity> {
    private static final float y = 0.001f;

    private static final RenderLayer TEXTURE_main = RenderLayer.getEntityCutoutNoCull(
        id("textures/block/chalk_big.png")
    );
    private static final float[] TEXTURE_main_size = {960f, 384f};
    private static final float[] TEXTURE_main_sprite_size = {192f, 192f};

    /** provides both start and end in 4 items. */
    private static float[] getUVOfSprite(int spriteIdx) {
        int spritesPerRow = (int)(TEXTURE_main_size[0] / TEXTURE_main_sprite_size[0]);
        
        int x = (int)TEXTURE_main_sprite_size[0] * (spriteIdx % spritesPerRow);
        int y = (int)TEXTURE_main_sprite_size[1] * (spriteIdx / spritesPerRow);

        float u1 = (float)x / TEXTURE_main_size[0];
        float v1 = (float)y / TEXTURE_main_size[1];

        float u2 = u1 + TEXTURE_main_sprite_size[0] / TEXTURE_main_size[0];
        float v2 = v1 + TEXTURE_main_sprite_size[1] / TEXTURE_main_size[1];

        return new float[] { u1, v1, u2, v2 };
    }

    public BigChalkCenterRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(BigChalkCenterEntity chalk, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {

        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);

        Direction attachedTo = chalk.attachedTo;
        if (attachedTo == Direction.UP)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        else if (attachedTo == Direction.NORTH)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

        else if (attachedTo == Direction.SOUTH)
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));

        else if (attachedTo == Direction.EAST)
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));

        else if (attachedTo == Direction.WEST)
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(90));


        matrices.multiply(
            switch (chalk.facing) {
                case WEST -> RotationAxis.POSITIVE_Y.rotationDegrees(90 + (chalk.attachedTo == Direction.UP ? 180 : 0));
                case SOUTH -> RotationAxis.POSITIVE_Y.rotationDegrees(180);
                case EAST -> RotationAxis.POSITIVE_Y.rotationDegrees(270 - (chalk.attachedTo == Direction.UP ? 180 : 0));
                default -> RotationAxis.POSITIVE_Z.rotationDegrees(0);
            }
        );

        matrices.translate(-0.5f, -0.5f, -0.5f);

        Matrix4f posMat = matrices.peek().getPositionMatrix();
        Matrix3f normMat = matrices.peek().getNormalMatrix();

        VertexConsumer vc = vertexConsumers.getBuffer(TEXTURE_main);
        for (int i = 0; i < 6; i++) {
            if (i == 5) i += chalk.textVariant;
            float[] uv = getUVOfSprite(i);
            vc.vertex(posMat, -1f, y, -1f).color(255, 255, 255, 255).texture(uv[0], uv[1]).overlay(overlay).light(light).normal(normMat, 0f, 1f, 0f).next();
            vc.vertex(posMat, -1f, y, 2f).color(255, 255, 255, 255).texture(uv[0], uv[3]).overlay(overlay).light(light).normal(normMat, 0f, 1f, 0f).next();
            vc.vertex(posMat, 2f, y, 2f).color(255, 255, 255, 255).texture(uv[2], uv[3]).overlay(overlay).light(light).normal(normMat, 0f, 1f, 0f).next();
            vc.vertex(posMat, 2f, y, -1f).color(255, 255, 255, 255).texture(uv[2], uv[1]).overlay(overlay).light(light).normal(normMat, 0f, 1f, 0f).next();
        }

        matrices.pop();
    }
}
