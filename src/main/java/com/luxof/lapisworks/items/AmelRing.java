package com.luxof.lapisworks.items;

import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.luxof.lapisworks.items.shit.FullyAmelInterface;

import at.petrak.hexcasting.common.items.HexBaubleItem;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexAttributes;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

public class AmelRing extends ItemStaff implements HexBaubleItem, FullyAmelInterface {
    public EntityAttributeModifier GRID_ZOOM = new EntityAttributeModifier(
        UUID.fromString("a897e19e-b03f-43ee-970f-d0f657b88a49"),
        "Amel Ring Focus",
        0.10,
        EntityAttributeModifier.Operation.MULTIPLY_TOTAL
    );

    public AmelRing(FabricItemSettings props) { super(props); }

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

    @Override
    public int getRequiredAmelToMakeFromBase() { return 1; }
}
