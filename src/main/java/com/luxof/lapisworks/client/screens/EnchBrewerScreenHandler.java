package com.luxof.lapisworks.client.screens;

import com.luxof.lapisworks.client.slots.LapisPotionIngredientSlot;
import com.luxof.lapisworks.client.slots.LapisPotionSlot;
import com.luxof.lapisworks.client.slots.SpecificItemSlot;
import com.luxof.lapisworks.init.ModScreens;
import com.luxof.lapisworks.inv.EnchBrewerInv;
import com.luxof.lapisworks.recipes.BrewingRec;

import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_BLAZE;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_BREWINGINTO_1;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_BREWINGINTO_2;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_BREWINGINTO_3;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_INPUT;
import static com.luxof.lapisworks.inv.EnchBrewerInv.IDX_DUST;
import static net.minecraft.item.ItemStack.EMPTY;

import java.util.List;
import java.util.function.Supplier;

public class EnchBrewerScreenHandler extends ScreenHandler {
    public final EnchBrewerInv inventory;
    private final Supplier<Pair<Integer, Integer>> fuelAndBrewTimeSupplier;
    private final Supplier<List<BrewingRec>> recipesSupplier;

    public EnchBrewerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new EnchBrewerInv(), () -> new Pair<>(0, 0), () -> List.of());
    }

    public EnchBrewerScreenHandler(
        int syncId,
        PlayerInventory plrInv,
        EnchBrewerInv blockInv,
        Supplier<Pair<Integer, Integer>> fuelAndBrewTimeSupplier,
        Supplier<List<BrewingRec>> recipesSupplier
    ) {
        super(ModScreens.ENCH_BREWER_SCREEN_HANDLER, syncId);
        checkSize(blockInv, 5);
        this.inventory = blockInv;
        this.fuelAndBrewTimeSupplier = fuelAndBrewTimeSupplier;
        this.recipesSupplier = recipesSupplier;

        World world = plrInv.player.getWorld();
        this.addSlot(new SpecificItemSlot(Items.BLAZE_POWDER, inventory, IDX_BLAZE, 79, 17));
        this.addSlot(new LapisPotionIngredientSlot(world, inventory, IDX_INPUT, 79, 45));
        this.addSlot(new LapisPotionSlot(world, inventory, IDX_BREWINGINTO_1, 56, 57));
        this.addSlot(new LapisPotionSlot(world, inventory, IDX_BREWINGINTO_2, 79, 64));
        this.addSlot(new LapisPotionSlot(world, inventory, IDX_BREWINGINTO_3, 102, 57));
        this.addSlot(new SpecificItemSlot(HexItems.AMETHYST_DUST, inventory, IDX_DUST, 56, 31));

        // hell no i'm not even gonna ATTEMPT to understand the fabric wiki's shit
        // The player inventory
        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(plrInv, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        // The player Hotbar
        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(plrInv, m, 8 + m * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIdx) {
        // i have no clue why this is the way it is.
        ItemStack newStack = EMPTY.copy();

        Slot slot = this.getSlot(slotIdx);
        if (slot == null || !slot.hasStack()) return newStack;
        ItemStack ogStack = slot.getStack();
        newStack = ogStack.copy();
        if (ogStack.isEmpty()) return newStack;

        if (slotIdx < this.inventory.size()) {
            if (!this.insertItem(ogStack, this.inventory.size(), this.slots.size(), true))
                return EMPTY.copy();

        } else if (ogStack.isOf(Items.BLAZE_POWDER)) {
            if (!this.insertItem(ogStack, IDX_BLAZE, IDX_BLAZE + 1, false)) return EMPTY.copy();

        } else if (ogStack.isOf(HexItems.AMETHYST_DUST)) {
            if (!this.insertItem(ogStack, IDX_DUST, IDX_DUST + 1, false)) return EMPTY.copy();

        // i can use these 2 vars because inputs and brewinginto are placed in a continuous line of idxes
        } else if (!this.insertItem(ogStack, IDX_INPUT, IDX_BREWINGINTO_3 + 1, false)) return EMPTY.copy();


        if (ogStack.isEmpty()) slot.setStack(EMPTY.copy());
        else slot.markDirty();

        slot.onTakeItem(player, newStack);
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) { return this.inventory.canPlayerUse(player); }

    public Pair<Integer, Integer> getFuelAndBrewTime() { return this.fuelAndBrewTimeSupplier.get(); }
    public int getFuel() { return this.fuelAndBrewTimeSupplier.get().getLeft(); }
    public int getBrewTime() { return this.fuelAndBrewTimeSupplier.get().getRight(); }
    public boolean getIsBrewing() {
        Pair<Integer, Integer> fuelAndBrewTime = this.fuelAndBrewTimeSupplier.get();
        return fuelAndBrewTime.getLeft() > 0 || fuelAndBrewTime.getRight() > 0;
    }
    public List<BrewingRec> getRecipes() { return this.recipesSupplier.get(); }
}
