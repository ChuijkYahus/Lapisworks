package com.luxof.lapisworks.interop.hextended;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexItems;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.items.shit.DurabilityPartAmel;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import org.jetbrains.annotations.Nullable;

/** Not a literal <code>interface</code>, but an interface to do stuff that'd be added to by Lapixtended. */
public class LapixtendedInterface {
    /** Returns <code>PARTAMEL_STAFF</code> if <code>fromitem</code> is an <code>ItemStaff</code>, otherwise <code>PARTAMEL_WAND</code> if it's an <code>ItemExtendedStaff</code>.
     * <p>returns <code>null</code> if it's neither.
    */
    @Nullable
    public static DurabilityPartAmel getAppropriatePartAmelGeneric(Item forItem) {
        if (Lapisworks.HEXTENDED_INTEROP) {
            if (forItem instanceof abilliontrillionstars.hextended.items.ItemExtendedStaff) {
                return (DurabilityPartAmel)(com.luxof.lapisworks.interop.hextended.Lapixtended.PARTAMEL_WAND);
            }
        }
        return forItem instanceof ItemStaff ? ModItems.PARTAMEL_STAFF : null;
    }

    /** if it's an extended staff, gives you an amel wand. else, amel staff. */
    @Nullable
    public static Item getAppropriateFullAmel(Item ofItem) {
        if (Lapisworks.HEXTENDED_INTEROP) {
            if (ofItem instanceof abilliontrillionstars.hextended.items.ItemExtendedStaff) {
                return com.luxof.lapisworks.interop.hextended.Lapixtended.AMEL_WAND;
            }
        }
        return ofItem instanceof ItemStaff ? ModItems.AMEL_STAFF : null;
    }

    /** gives a staff that is known to have a recipe. (oak, to be precise.)
     * <p>if you pass in an extended staff, gives you an extended oak. else, regular oak.
     * <p>Yes, this means the mod completely and utterly fucking dies if the recipe is disabled.
     * <p>Do I care? */
    public static Item getGenericStaffKnownToHaveRecipe(ItemStaff item) {
        if (Lapisworks.HEXTENDED_INTEROP) {
            if (item instanceof abilliontrillionstars.hextended.items.ItemExtendedStaff) {
                return Registries.ITEM.get(
                    abilliontrillionstars.hextended.LanisHextendedStaves.id("staff/long/oak")
                );
            }
        }
        return HexItems.STAFF_OAK;
    }
}
