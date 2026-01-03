package com.luxof.lapisworks.items;

import at.petrak.hexcasting.api.utils.NBTHelper;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.items.shit.InventoryItem;

import static com.luxof.lapisworks.Lapisworks.getAllHands;
import static com.luxof.lapisworks.init.Mutables.Mutables.isAmel;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class AmelJar extends Item implements InventoryItem {
    private static final Settings defaultSettings = new Item.Settings().maxCount(1);
    public AmelJar(int maxAmel, boolean worksInHotbar) { super(defaultSettings); this.maxAmel = maxAmel; this.worksInHotbar = worksInHotbar; }
    public AmelJar(Settings settings, int maxAmel, boolean worksInHotbar) {super(settings); this.maxAmel = maxAmel; this.worksInHotbar = worksInHotbar; }

    private static final String STORED_AMEL = "amel_amount";
    public final int maxAmel;
    public final boolean worksInHotbar;

    public TypedActionResult<ItemStack> deposit(
        PlayerEntity user,
        Hand hand,
        ItemStack stack,
        Hand otherHand
    ) {
        ItemStack amelStack = user.getStackInHand(otherHand);

        if (!isAmel(amelStack) || amelStack.isEmpty())
            return TypedActionResult.fail(stack);

        int wouldBeStoredNow = getStored(stack) + amelStack.getCount();
        int shouldBeStoredNow = Math.min(maxAmel, wouldBeStoredNow);

        int difference = wouldBeStoredNow - shouldBeStoredNow;
        if (difference == 0) amelStack.copyAndEmpty();
        else amelStack.setCount(difference);

        setStored(
            stack,
            shouldBeStoredNow
        );

        return TypedActionResult.success(stack, true);
    }

    public TypedActionResult<ItemStack> withdraw(
        PlayerEntity user,
        Hand hand,
        ItemStack stack,
        Hand otherHand
    ) {
        ItemStack amelStack = user.getStackInHand(otherHand);

        if (!isAmel(amelStack) && !amelStack.isEmpty())
            return TypedActionResult.fail(stack);

        int wouldBeWithdrawn = amelStack.getMaxCount() - amelStack.getCount();
        int shouldBeWithdrawn = Math.min(getStored(amelStack), wouldBeWithdrawn);

        user.setStackInHand(otherHand, new ItemStack(
            amelStack.isEmpty() ? ModItems.AMEL_ITEM : amelStack.getItem(),
            amelStack.isEmpty() ? 0 : amelStack.getCount() + shouldBeWithdrawn
        ));

        setStored(
            stack,
            getStored(stack) - shouldBeWithdrawn
        );

        return TypedActionResult.success(stack, true);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        List<Hand> hands = getAllHands();
        hands.remove(hand);
        Hand otherHand = hands.get(0);
        ItemStack stack = user.getStackInHand(hand);
        return user.isSneaking() ? withdraw(user, hand, stack, otherHand) : deposit(user, hand, stack, otherHand);
    }

    public int getStored(ItemStack stack) { return NBTHelper.getInt(stack, STORED_AMEL); }
    public void setStored(ItemStack stack, int count) { NBTHelper.putInt(stack, STORED_AMEL, count); }

    @Override
    public void appendTooltip(
        ItemStack stack,
        @Nullable World world,
        List<Text> components,
        TooltipContext flag
    ) {
        components.add(
            Text.translatable("tooltips.lapisworks.amel_jar.pre")
                .formatted(Formatting.DARK_PURPLE)
                .append(Text.literal(String.valueOf(this.getStored(stack))).formatted(Formatting.BLUE))
        );
    }

    @Override
    public boolean canAccess(Flags flags) {
        return this.worksInHotbar ? true : !flags.canWorkIn(Flags.INVITEM, Flags.HOTBAR) &&
                !flags.canWorkIn(Flags.INVITEM, Flags.INVENTORY);
    }
    @Override
    public int fetch(ItemStack stack, Predicate<Item> item) {
        if (!item.test(ModItems.AMEL_ITEM)) return 0;
        return this.getStored(stack);
    }
    @Override
    public int drain(ItemStack stack, Predicate<Item> item, int count) {
        if (!item.test(ModItems.AMEL_ITEM)) return count;
        int takeAway = Math.min(this.getStored(stack), count);
        int remainingInStore = this.getStored(stack) - takeAway;
        int remainingToTake = count - takeAway;
        this.setStored(stack, remainingInStore);
        return remainingToTake;
    }
    @Override
    public int give(ItemStack stack, Predicate<Item> item, int count) {
        if (!item.test(ModItems.AMEL_ITEM)) return count;
        // by the power of the lord above my math will work
        int give = Math.min(this.maxAmel - this.getStored(stack), count);
        int remaining = Math.max(this.getStored(stack) + count - this.maxAmel, 0);
        this.setStored(stack, this.getStored(stack) + give);
        return remaining;
    }
}
