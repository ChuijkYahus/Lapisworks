package com.luxof.lapisworks.client.collar.additions;

import com.luxof.lapisworks.client.collar.LapisCollarAddition;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.init.ModItems.COLLAR;
import static com.luxof.lapisworks.init.ModItems.COLLAR_WITH_MODEL;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

// i don't think this class even gets used by virtue of implements DyeableItem
// not sure though
public class DyeCollarAddition implements LapisCollarAddition {
    public static final Identifier ID = id("collar/base");

    @Override
    public Text getName() { return Text.literal(""); }

    @Override
    public Text getName(ItemStack collarStack) { return Text.literal(""); }

    @Override
    public boolean testItem(Item item) { return item instanceof DyeItem; }

    @Override
    public boolean canAdd(ItemStack collarStack, List<Identifier> existingAdditions, Identifier yourId) {
        return true;
    }

    private static final int BITS_24 = (int)Math.pow(2, 24) - 1;
    private static final int BITS_16 = (int)Math.pow(2, 16) - 1;
    private static final int BITS_8 = (int)Math.pow(2, 8) - 1;
    private int colorBlend(int color1, int color2) {
        int r1 = color1 & (BITS_24 - BITS_16);
        int g1 = color1 & (BITS_16 - BITS_8);
        int b1 = color1 & (BITS_8);

        int r2 = color2 & (BITS_24 - BITS_16);
        int g2 = color2 & (BITS_16 - BITS_8);
        int b2 = color2 & (BITS_8);

        return (r1 + r2) / 2 + (g1 + g2) / 2 + (b1 + b2) / 2;
    }

    @Override
    public ItemStack craft(ItemStack collarStack, List<Identifier> existingAdditions, ItemStack yourStack,
            Identifier yourId) {
        ItemStack stack = collarStack.copy();
        int dyeColor = COLLAR.getColorFrom(((DyeItem)yourStack.getItem()).getColor());

        COLLAR.setColor(
            stack,
            COLLAR.hasColor(stack) ? colorBlend(COLLAR.getColor(stack), dyeColor) : dyeColor
        );

        return stack;
    }

    @Override
    public void render(ItemStack collarStack, Identifier yourId, @Nullable LivingEntity entity,
            ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            int overlay) {
        DyeCollarAddition.renderStatic(
            collarStack, yourId, entity, mode, matrices, vertexConsumers, light, overlay
        );
    }

    public static void renderStatic(ItemStack collarStack, Identifier yourId, @Nullable LivingEntity entity,
            ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            int overlay) {
        // good lord i hope this isn't too taxing to do every frame
        ItemStack colored = new ItemStack(COLLAR_WITH_MODEL);
        COLLAR.setColor(colored, COLLAR.getColor(collarStack));
        MinecraftClient.getInstance().getItemRenderer().renderItem(
            entity,
            colored,
            mode,
            false,
            matrices,
            vertexConsumers,
            entity != null ? entity.getWorld() : null,
            light,
            overlay,
            0
        );
    }
}
