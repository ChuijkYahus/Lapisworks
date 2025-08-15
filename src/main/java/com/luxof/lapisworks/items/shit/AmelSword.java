package com.luxof.lapisworks.items.shit;

import static com.luxof.lapisworks.Lapisworks.isAmel;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class AmelSword extends SwordItem implements FullyAmelInterface {
    public Item material;

    public AmelSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings, Item material) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.material = material;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return stack.getDamage() < this.getMaxDamage() * 0.9 ?
                    isAmel(ingredient.getItem()) :
                    ingredient.getItem() == this.material;
    }

    public EquipmentSlot getSlotType() {
        return EquipmentSlot.MAINHAND;
    }

    // shit that you *should* fuck with
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }
    public UseAction getUseAction(ItemStack stack) {
      return UseAction.BLOCK;
    }
    // also postHit and getAttributeModifiers ig

    @Override public int getRequiredAmelToMakeFromBase() { return 32; }
    @Override public boolean noPartAmelPhase() { return true; }
}
