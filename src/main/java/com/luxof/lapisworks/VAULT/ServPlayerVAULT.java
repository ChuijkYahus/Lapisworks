package com.luxof.lapisworks.VAULT;


import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.Lapisworks.getAllHands;
import static com.luxof.lapisworks.LapisworksIDs.PLAYER_VAULT;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;

import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

/** if you're actually looking at VAULT implementations, you're either cooked or cooking */
public class ServPlayerVAULT extends VAULT {
    private final ServerPlayerEntity player;
    protected ServPlayerVAULT(ServerPlayerEntity player) { this.player = player; }
    @Override public Identifier getKindOfVault() { return PLAYER_VAULT; }


    @Override
    public int fetch(Predicate<Item> item, Flags flags) {
        Flags currFlags = null;
        int runningSum = 0;
        currFlags = flags.getKindsOfFlag(Flags.EQ_TRINKETS);
        if (currFlags.canSearch(Flags.EQ_TRINKETS)) {
            Optional<TrinketComponent> trinkCompOpt = TrinketsApi.getTrinketComponent(this.player);
            if (trinkCompOpt.isPresent()) {
                TrinketComponent trinkComp = trinkCompOpt.get();
                for (Pair<SlotReference, ItemStack> pair : trinkComp.getAllEquipped()) {
                    ItemStack stack = pair.getRight();
                    if (currFlags.canWorkIn(Flags.INVITEM, Flags.EQ_TRINKETS)) {
                        runningSum += this.handleFetchInvItem(stack, item, currFlags);
                    }
                    if (currFlags.canWorkIn(Flags.STACKS, Flags.EQ_TRINKETS)) {
                        runningSum += this.handleFetchStack(stack, item, currFlags);
                    }
                }
            }
        }

        currFlags = flags.getKindsOfFlag(Flags.HANDS);
        if (currFlags.canSearch(Flags.HANDS)) {
            for (Hand hand : getAllHands()) {
                ItemStack stack = player.getStackInHand(hand);
                if (currFlags.canWorkIn(Flags.INVITEM, Flags.HANDS)) {
                    runningSum += this.handleFetchInvItem(stack, item, currFlags);
                }
                if (currFlags.canWorkIn(Flags.STACKS, Flags.HANDS)) {
                    runningSum += this.handleFetchStack(stack, item, currFlags);
                }
            }
        }

        currFlags = flags.getKindsOfFlag(Flags.HOTBAR);
        if (currFlags.canSearch(Flags.HOTBAR)) {
            PlayerInventory inv = this.player.getInventory();
            for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
                ItemStack stack = inv.getStack(i);
                if (currFlags.canWorkIn(Flags.INVITEM, Flags.HOTBAR)) {
                    runningSum += this.handleFetchInvItem(stack, item, currFlags);
                }
                if (currFlags.canWorkIn(Flags.STACKS, Flags.HOTBAR)) {
                    runningSum += this.handleFetchStack(stack, item, currFlags);
                }
            }
        }

        currFlags = flags.getKindsOfFlag(Flags.INVENTORY);
        if (currFlags.canSearch(Flags.INVENTORY)) {
            PlayerInventory inv = player.getInventory();
            for (int i = PlayerInventory.getHotbarSize(); i < inv.size(); i++) {
                ItemStack stack = inv.getStack(i);
                if (currFlags.canWorkIn(Flags.INVITEM, Flags.INVENTORY)) {
                    runningSum = this.handleFetchInvItem(stack, item, currFlags);
                }
                if (currFlags.canWorkIn(Flags.STACKS, Flags.INVENTORY)) {
                    runningSum = this.handleFetchStack(stack, item, currFlags);
                }
            }
        }

        return runningSum;
    }


    @Override
    public int drain(Predicate<Item> item, int amount, Flags flags) {
        Flags currFlags = null;
        int left = amount;
        currFlags = flags.getKindsOfFlag(Flags.EQ_TRINKETS);
        if (currFlags.canSearch(Flags.EQ_TRINKETS)) {
            Optional<TrinketComponent> trinkCompOpt = TrinketsApi.getTrinketComponent(this.player);
            if (trinkCompOpt.isPresent()) {
                TrinketComponent trinkComp = trinkCompOpt.get();
                for (Pair<SlotReference, ItemStack> pair : trinkComp.getAllEquipped()) {
                    ItemStack stack = pair.getRight();
                    if (currFlags.canWorkIn(Flags.INVITEM, Flags.EQ_TRINKETS)) {
                        left = this.handleDrainInvItem(stack, item, left, currFlags);
                    }
                }
                if (left == 0) return 0;
                else if (left < 0) {
                    LOGGER.error("CRITICAL ERROR: VAULT DRAINING MADE \"left\" < 0!!");
                    return 0;
                }
            }
        }

        currFlags = flags.getKindsOfFlag(Flags.HANDS);
        if (currFlags.canSearch(Flags.HANDS)) {
            for (Hand currHand : getAllHands()) {
                ItemStack stack = player.getStackInHand(currHand);
                if (currFlags.canWorkIn(Flags.INVITEM, Flags.HANDS)) {
                    left = this.handleDrainInvItem(stack, item, left, currFlags);
                }
                if (currFlags.canWorkIn(Flags.STACKS, Flags.HANDS)) {
                    left = this.handleDrainStack(stack, item, left, currHand, player::setStackInHand);
                }
                if (left == 0) return 0;
                else if (left < 0) {
                    LOGGER.error("CRITICAL ERROR: VAULT DRAINING MADE \"left\" < 0!!");
                    return 0;
                }
            }
        }

        currFlags = flags.getKindsOfFlag(Flags.HOTBAR);
        if (currFlags.canSearch(Flags.HOTBAR)) {
            PlayerInventory inv = player.getInventory();
            for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
                ItemStack stack = inv.getStack(i);
                if (currFlags.canWorkIn(Flags.INVITEM, Flags.HOTBAR)) {
                    left = this.handleDrainInvItem(stack, item, left, currFlags);
                }
                if (currFlags.canWorkIn(Flags.STACKS, Flags.HOTBAR)) {
                    left = this.handleDrainStack(stack, item, left, i, inv::setStack);
                }
                if (left == 0) return 0;
                else if (left < 0) {
                    LOGGER.error("CRITICAL ERROR: VAULT DRAINING MADE \"left\" < 0!!");
                    return 0;
                }
            }
        }

        currFlags = flags.getKindsOfFlag(Flags.INVENTORY);
        if (currFlags.canSearch(Flags.INVENTORY)) {
            PlayerInventory inv = player.getInventory();
            for (int i = PlayerInventory.getHotbarSize(); i < inv.size(); i++) {
                ItemStack stack = inv.getStack(i);
                if (currFlags.canWorkIn(Flags.INVITEM, Flags.INVENTORY)) {
                    left = this.handleDrainInvItem(stack, item, amount, currFlags);
                }
                if (currFlags.canWorkIn(Flags.STACKS, Flags.INVENTORY)) {
                    left = this.handleDrainStack(stack, item, amount, i, inv::setStack);
                }
                if (left == 0) return 0;
                else if (left < 0) {
                    LOGGER.error("CRITICAL ERROR: VAULT DRAINING MADE \"left\" < 0!!");
                    return 0;
                }
            }
        }

        return left;
    }


    @Override
    public int give(Predicate<Item> item, int amount, Flags flags) {
        Flags currFlags = null;
        int left = amount;
        currFlags = flags.getKindsOfFlag(Flags.EQ_TRINKETS);
        if (currFlags.canSearch(Flags.EQ_TRINKETS)) {
            Optional<TrinketComponent> trinkCompOpt = TrinketsApi.getTrinketComponent(this.player);
            if (trinkCompOpt.isPresent()) {
                TrinketComponent trinkComp = trinkCompOpt.get();
                for (Pair<SlotReference, ItemStack> pair : trinkComp.getAllEquipped()) {
                    ItemStack stack = pair.getRight();
                    if (currFlags.canWorkIn(Flags.INVITEM, Flags.EQ_TRINKETS)) {
                        left = this.handleGiveInvItem(stack, item, left, currFlags);
                    }
                }
                if (left == 0) return 0;
                else if (left < 0) {
                    LOGGER.error("CRITICAL ERROR: VAULT GIVING MADE \"left\" < 0!!");
                    return 0;
                }
            }
        }

        currFlags = flags.getKindsOfFlag(Flags.HANDS);
        if (currFlags.canSearch(Flags.HANDS)) {
            for (Hand hand : getAllHands()) {
                ItemStack stack = player.getStackInHand(hand);
                if (currFlags.canWorkIn(Flags.INVITEM, Flags.HANDS)) {
                    left = this.handleGiveInvItem(stack, item, left, currFlags);
                }
                if (currFlags.canWorkIn(Flags.STACKS, Flags.HANDS)) {
                    left = this.handleGiveStack(stack, item, left, hand, player::setStackInHand);
                }
                if (left == 0) return 0;
                else if (left < 0) {
                    LOGGER.error("CRITICAL ERROR: VAULT GIVING MADE \"left\" < 0!!");
                    return 0;
                }
            }
        }

        currFlags = flags.getKindsOfFlag(Flags.HOTBAR);
        if (currFlags.canSearch(Flags.HOTBAR)) {
            PlayerInventory inv = player.getInventory();
            for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
                ItemStack stack = inv.getStack(i);
                if (currFlags.canWorkIn(Flags.INVITEM, Flags.HOTBAR)) {
                    left = this.handleGiveInvItem(stack, item, left, currFlags);
                }
                if (currFlags.canWorkIn(Flags.STACKS, Flags.HOTBAR)) {
                    left = this.handleGiveStack(stack, item, left, i, inv::setStack);
                }
                if (left == 0) return 0;
                else if (left < 0) {
                    LOGGER.error("CRITICAL ERROR: VAULT GIVING MADE \"left\" < 0!!");
                    return 0;
                }
            }
        }

        currFlags = flags.getKindsOfFlag(Flags.INVENTORY);
        if (currFlags.canSearch(Flags.INVENTORY)) {
            PlayerInventory inv = player.getInventory();
            for (int i = PlayerInventory.getHotbarSize(); i < inv.size(); i++) {
                ItemStack stack = inv.getStack(i);
                if (currFlags.canWorkIn(Flags.INVITEM, Flags.INVENTORY)) {
                    left = this.handleGiveInvItem(stack, item, amount, currFlags);
                }
                if (currFlags.canWorkIn(Flags.STACKS, Flags.INVENTORY)) {
                    left = this.handleGiveStack(stack, item, amount, i, inv::setStack);
                }
                if (left == 0) return 0;
                else if (left < 0) {
                    LOGGER.error("CRITICAL ERROR: VAULT GIVING MADE \"left\" < 0!!");
                    return 0;
                }
            }
        }

        return left;
    }
}
