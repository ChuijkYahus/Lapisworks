package com.luxof.lapisworks.items;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexAttributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.luxof.lapisworks.items.shit.PartiallyAmelInterface;

import java.util.UUID;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class PartiallyAmelStaff extends ItemStaff implements PartiallyAmelInterface {
    private static FabricItemSettings static_settings = new FabricItemSettings().maxCount(1).maxDamage(200);

    public EntityAttributeModifier GRID_ZOOM = new EntityAttributeModifier(
        // same UUID as hextended to not stack on them
        UUID.fromString("a370ec84-ea18-4de6-8730-4271516dcf9c"),
        "Partially Amel Staff Zoom",
        0.35,
        EntityAttributeModifier.Operation.MULTIPLY_BASE
    );
    public EntityAttributeModifier _getGridZoom() { return this.GRID_ZOOM; }

    public PartiallyAmelStaff() { super(static_settings); }
    public PartiallyAmelStaff(FabricItemSettings props) { super(props); }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        HashMultimap<EntityAttribute, EntityAttributeModifier> out = HashMultimap.create(super.getAttributeModifiers(slot));
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            out.put(HexAttributes.GRID_ZOOM, this._getGridZoom());
        }
        return out;
    }

    @Override
    public void specialUseBehaviour(PlayerEntity player, World world, Hand hand) {
        ItemStack staff = player.getStackInHand(hand);
        staff.damage(1, player, (LivingEntity entity) -> {});
    }

    @Override
    public boolean isDamageable() { return true; }
    @Override
    public int getMaxDurability() { return 200; }
    @Override
    public int getAmelWorthInDurability() { return 20; }
}
