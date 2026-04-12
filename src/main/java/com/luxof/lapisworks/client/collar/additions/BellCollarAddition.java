package com.luxof.lapisworks.client.collar.additions;

import com.luxof.lapisworks.client.collar.LapisCollarAddition;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.init.ModItems.COLLAR;
import static com.luxof.lapisworks.init.ModItems.COLLAR_BELL;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class BellCollarAddition implements LapisCollarAddition {
    public static final Identifier ID = id("collar/bell");

    @Override public Text getName() {
        return Text.translatable("tooltips.lapisworks.collar.added_items.bell")
            .formatted(Formatting.YELLOW);
    }
    @Override public Text getName(ItemStack collarStack) { return getName(); }
    @Override public boolean testItem(Item item) { return item == Items.GOLD_INGOT; }

    @Override
    public boolean canAdd(ItemStack collarStack, List<Identifier> existingAdditions, Identifier yourId) {
        return true;
    }

    @Override
    public ItemStack craft(ItemStack collarStack, List<Identifier> existingAdditions, ItemStack yourStack,
            Identifier yourId) {
        ItemStack newCollar = collarStack.copy();
        COLLAR.addAddition(newCollar, yourId);
        return newCollar;
    }

    private final ItemStack BELL_STACK = new ItemStack(COLLAR_BELL);
    @Override
    public void render(ItemStack collarStack, Identifier yourId, @Nullable LivingEntity entity,
            ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            int overlay) {
        MinecraftClient.getInstance().getItemRenderer().renderItem(
            entity,
            BELL_STACK,
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
