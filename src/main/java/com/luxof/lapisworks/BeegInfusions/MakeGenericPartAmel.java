package com.luxof.lapisworks.BeegInfusions;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.ItemStaff;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.init.Mutables.BeegInfusion;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.interop.hextended.LapixtendedInterface;
import com.luxof.lapisworks.items.shit.PartiallyAmelInterface;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;
import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.luxof.lapisworks.recipes.HandsInv;
import com.luxof.lapisworks.recipes.ImbuementRec;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class MakeGenericPartAmel extends BeegInfusion {
    private ItemStack stack = null;
    private Item item = null;
    private Hand hand = null;
    private int fullInfusionCost = 0;
    private int infusing = 0;

    @Override
    protected void postSetUp() {
        try {
            fullInfusionCost = item instanceof PartiallyAmelInterface ?
                (int)Math.ceil(
                    stack.getDamage() /
                    (double)((PartiallyAmelInterface)item).getAmelWorthInDurability()
                ) : Mutables.getBaseCostForInfusionOf(LapixtendedInterface.getAppropriateFullAmel(item));
        } catch (NullPointerException e) {}
        infusing = Math.min(
            OperatorUtils.getPositiveInt(hexStack, 0, hexStack.size()),
            fullInfusionCost
        );
    }

    @Override
    public boolean test() {
        boolean ret = false;
        for (HeldItemInfo heldInfo : this.heldInfos) {
            ret = true;
            stack = heldInfo.stack();
            item = stack.getItem();
            hand = heldInfo.hand();
            if (!(item instanceof ItemStaff)) ret = false;
            else if (ctx.getWorld().getRecipeManager().getFirstMatch(
                    ImbuementRec.Type.INSTANCE,
                    new HandsInv(((GetStacks)ctx).getHeldItemStacks()),
                    ctx.getWorld()
                ).isPresent()) ret = false;
        }
        return ret;
    }

    @Override
    public void mishapIfNeeded() {
        int availableAmel = vault.fetch(Mutables::isAmel, Flags.PRESET_Stacks_InvItem_UpToHotbar);
        if (availableAmel < infusing) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughItems(
                AMEL,
                availableAmel,
                infusing
            ));
        }
    }

    @Override
    public Long getCost() { return MediaConstants.SHARD_UNIT * 2 * infusing; }

    @Override
    public void accept() {
        vault.drain(Mutables::isAmel, infusing, Flags.PRESET_Stacks_InvItem_UpToHotbar);

        PartiallyAmelInterface partAmel = LapixtendedInterface.getAppropriatePartAmelGeneric(item);
        Item fullAmel = LapixtendedInterface.getAppropriateFullAmel(item);
        ItemStack newStaff;
        if (infusing == fullInfusionCost) { newStaff = new ItemStack(fullAmel); }
        else if (!(item instanceof PartiallyAmelInterface)) {
            newStaff = new ItemStack((Item)partAmel);
            newStaff.setDamage(
            newStaff.getMaxDamage() - infusing * partAmel.getAmelWorthInDurability()
        );
        } else {
            newStaff = stack.copy();
            newStaff.setDamage(
                stack.getDamage() - infusing * partAmel.getAmelWorthInDurability()
            );
        }
        ctx.replaceItem(
            stack -> stack.getItem() instanceof ItemStaff, 
            newStaff,
            hand
        );
    }
}
