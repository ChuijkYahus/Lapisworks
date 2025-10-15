package com.luxof.lapisworks.VAULT;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import com.luxof.lapisworks.items.shit.InventoryItem;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/** Very Ass and Unnecessary but not use-Less Terminal (for items)
 * <p>Fetches, drains, gives items.
 * <p>Dynamic. Does not need to save into NBT data or anything.
 * <p>Adding your own type support if you want (why would you?) is easy.
 * <p>Right now, only god and I know why these math formulas make sense.
 * Soon, only god will know.
 */
public abstract class VAULT {
    protected VAULT() {}
    // this is the part you can mixin to add support
    @Nullable
    public static <ANY extends Object> VAULT of(ANY literallyFuckingAnything) {
        if (literallyFuckingAnything instanceof ServerPlayerEntity player) {
            return new ServPlayerVAULT(player);
        } else if (literallyFuckingAnything instanceof CastingEnvironment castEnv) {
            return new CastEnvVAULT(castEnv);
        } else {
            return null;
        }
    }


    public abstract Identifier getKindOfVault();


    protected int handleFetchInvItem(ItemStack stack, Predicate<Item> searchingFor, Flags flags) {
        return stack.getItem() instanceof InventoryItem invItem && invItem.canAccess(flags) ?
                invItem.fetch(stack, searchingFor) : 0;
    }
    protected int handleFetchStack(ItemStack stack, Predicate<Item> searchingFor, Flags flags) {
        return searchingFor.test(stack.getItem()) ? stack.getCount() : 0;
    }
    /** Returns the amount of that item present. */
    public abstract int fetch(Predicate<Item> itemPred, Flags flags);
    /** Returns the amount of that item present. */
    public int fetch(Item item, Flags flags) { return this.fetch(it -> it == item, flags); };


    protected int handleDrainInvItem(ItemStack stack, Predicate<Item> take, int amount, Flags flags) {
        if (!(stack.getItem() instanceof InventoryItem invItem && invItem.canAccess(flags))) return amount;
        return invItem.drain(stack, take, amount);
    }
    protected <ANY extends Object> int handleDrainStack(
        ItemStack stack,
        Predicate<Item> take,
        int amount,
        ANY firstArg,
        BiConsumer<ANY, ItemStack> setStackFunc
    ) {
        if (!take.test(stack.getItem())) return amount;
        int leftInStack = Math.max(stack.getCount() - amount, 0);
        int leftToTake = amount - (stack.getCount() - leftInStack);
        setStackFunc.accept(
            firstArg,
            leftInStack == 0 ? ItemStack.EMPTY.copy() : new ItemStack(stack.getItem(), leftInStack)
        );
        return leftToTake;
    }
    /** Returns the amount that couldn't be drained. */
    public abstract int drain(Predicate<Item> itemPred, int amount, Flags flags);
    /** Returns the amount that couldn't be drained. */
    public int drain(Item item, int amount, Flags flags) { return this.drain(it -> it == item, amount, flags); };


    protected int handleGiveInvItem(ItemStack stack, Predicate<Item> give, int amount, Flags flags) {
        if (!(stack.getItem() instanceof InventoryItem invItem && invItem.canAccess(flags))) return amount;
        return invItem.give(stack, give, amount);
    }
    protected <ANY extends Object> int handleGiveStack(
        ItemStack stack,
        Predicate<Item> give,
        int amount,
        ANY firstArg,
        BiConsumer<ANY, ItemStack> setStackFunc
    ) {
        if (!give.test(stack.getItem())) return amount;
        int limit = stack.getMaxCount() - stack.getCount();
        int left = Math.max(amount - limit, 0);
        setStackFunc.accept(
            firstArg,
            new ItemStack(stack.getItem(), Math.min(stack.getCount() + amount, stack.getMaxCount()))
        );
        return left;
    }
    /** Returns the amount that couldn't be given. */
    public abstract int give(Predicate<Item> itemPred, int amount, Flags flags);
    /** Returns the amount that couldn't be given. */
    public int give(Item item, int amount, Flags flags) { return this.give(it -> it == item, amount, flags); };
}
