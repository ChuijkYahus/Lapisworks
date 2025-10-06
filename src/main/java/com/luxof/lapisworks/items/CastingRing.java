package com.luxof.lapisworks.items;

import at.petrak.hexcasting.common.items.HexBaubleItem;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexAttributes;

import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

// don't be stupid, just implement HexBaubleItem because hexcasting
// registers those as Trinkets type shit while doing Trinkets API Interop.
// that took me a LOOONG time to figure out.
public class CastingRing extends ItemStaff implements HexBaubleItem {
    public EntityAttributeModifier GRID_ZOOM = new EntityAttributeModifier(
        // different UUID from others to stack it on shit
        UUID.fromString("a897e19e-b03f-43ee-970f-d0f657b88a49"),
        "Casting Ring Defocus And Weakness",
        // any X.YZ you give to Operation.MULTIPLY_WHATEVER *increases*
        // the base/total by XYZ%, so clearly it's going:
        // value * (1.00 + modifier)
        // thus i can flip the sign to achieve the opposite effect.
        // or at least, should be able to.
        -0.10,
        EntityAttributeModifier.Operation.MULTIPLY_BASE
    );

    public CastingRing(FabricItemSettings props) { super(props); }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(
        EquipmentSlot slot
    ) {
        HashMultimap<EntityAttribute, EntityAttributeModifier> out = HashMultimap.create(super.getAttributeModifiers(slot));
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            out.put(HexAttributes.GRID_ZOOM, GRID_ZOOM);
        }
        return out;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getHexBaubleAttrs(ItemStack stack) {
        HashMultimap<EntityAttribute, EntityAttributeModifier> out = HashMultimap.create();
        out.put(HexAttributes.GRID_ZOOM, GRID_ZOOM);
        return out;
    }
}
