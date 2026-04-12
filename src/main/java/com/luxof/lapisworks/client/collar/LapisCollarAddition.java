package com.luxof.lapisworks.client.collar;

import java.util.List;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public interface LapisCollarAddition {
    public Text getName();
    public Text getName(ItemStack collarStack);
    /** No <code>ItemStack</code> usage so I can *eventually* put this stuff in EMI.  */
    public boolean testItem(Item item);
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected);

    public boolean canAdd(
        ItemStack collarStack,
        List<Identifier> existingAdditions,
        Identifier yourId
    );
    /** Add your addition to the stack via <code>COLLAR.addAddition</code>. */
    public ItemStack craft(
        ItemStack collarStack,
        List<Identifier> existingAdditions,
        ItemStack yourStack,
        Identifier yourId
    );
    public void render(
        ItemStack collarStack,
        Identifier yourId,
        @Nullable LivingEntity entity,
        ModelTransformationMode mode,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        int overlay
    );

    default boolean renderingAsTrinket(ModelTransformationMode mode) {
        return mode.equals(ModelTransformationMode.GUI);
    }
}
