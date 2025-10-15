package com.luxof.lapisworks.items.shit;

import java.util.function.Predicate;

import com.luxof.lapisworks.VAULT.Flags;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** Makes this item accessible by the VAULT. */
public interface InventoryItem {
    // implementing this? you should deal only with SEARCHING and WORK. Probably.
    public boolean canAccess(Flags flags);
    /** fetches the amount of <code>item</code> in there. */
    public int fetch(ItemStack stack, Predicate<Item> item);
    /** returns the amount that couldn't be drained. */
    public int drain(ItemStack stack, Predicate<Item> item, int count);
    /** returns the amount that couldn't be given. */
    public int give(ItemStack stack, Predicate<Item> item, int count);
}
