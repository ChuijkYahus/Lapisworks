package com.luxof.lapisworks.interop.hextended;

import at.petrak.hexcasting.common.items.ItemStaff;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.items.shit.DurabilityPartAmel;

import net.minecraft.item.Item;

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

    /** <code>ofItem instanceof ItemExtendStaff ? AMEL_WAND : ofItem instanceof ItemStaff ? AMEL_STAFF : null</code> */
    @Nullable
    public static Item getAppropriateFullAmel(Item ofItem) {
        if (Lapisworks.HEXTENDED_INTEROP) {
            if (ofItem instanceof abilliontrillionstars.hextended.items.ItemExtendedStaff) {
                return com.luxof.lapisworks.interop.hextended.Lapixtended.AMEL_WAND;
            }
        }
        return ofItem instanceof ItemStaff ? ModItems.AMEL_STAFF : null;
    }
}
