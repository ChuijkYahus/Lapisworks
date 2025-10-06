package com.luxof.lapisworks.interop.hextended.items;

import com.luxof.lapisworks.items.PartiallyAmelStaff;

import java.util.UUID;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.entity.attribute.EntityAttributeModifier;

public class PartAmelWand extends PartiallyAmelStaff {
    private static FabricItemSettings static_settings = new FabricItemSettings().maxCount(1).maxDamage(400);
    public PartAmelWand() { super(static_settings); }

    public EntityAttributeModifier GRID_ZOOM = new EntityAttributeModifier(
        // same UUID as hextended to not stack on them
        UUID.fromString("a370ec84-ea18-4de6-8730-4271516dcf9c"),
        "Extended Partially Amel Staff Zoom",
        0.45,
        EntityAttributeModifier.Operation.MULTIPLY_BASE
    );
    @Override public EntityAttributeModifier _getGridZoom() { return this.GRID_ZOOM; }
    @Override public int getMaxDurability() { return 400; }
}
