package com.luxof.lapisworks.BeegInfusions;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.init.Mutables.BeegInfusion;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mishaps.MishapBadHandItem;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.LapisworksIDs.ENCHBOOK_WITH_NOTONE_ENCH;
import static com.luxof.lapisworks.LapisworksIDs.ENCHBOOK_WITH_ONE_ENCH;

import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class EnhanceEnchantedBook extends BeegInfusion {
    private int requiredAmel = 0;
    private int availableAmel = 0;
    private ItemStack stack = null;
    private Hand hand = null;
    private int infusing = 0;

    @Override
    public boolean test() {
        boolean ret = false;
        for (HeldItemInfo heldInfo : this.heldInfos) {
            stack = heldInfo.stack();
            hand = heldInfo.hand();
            if (stack.isOf(Items.ENCHANTED_BOOK)) {
                ret = true;
                break;
            }
        }
        if (!ret) return false;
        // ^^vv don't wanna uselessly go through a lot of items just to return false
        availableAmel = vault.fetch(Mutables::isAmel, Flags.PRESET_Stacks_InvItem_UpToHotbar);
        requiredAmel = 20 * EnchantmentHelper.get(stack).values().iterator().next();
        infusing = Math.min(
            OperatorUtils.getPositiveInt(this.hexStack, 0, this.hexStack.size()),
            requiredAmel
        );
        return ret;
    }

    @Override
    public void mishapIfNeeded() {
        // this seems a bit problematic for any other enchanted book handlers..
        // open an issue or something if you don't want this first mishap here vvv
        if (EnchantmentHelper.get(stack).values().size() != 1) {
            LOGGER.info("enchantment size btw: " + EnchantmentHelper.get(stack).values().size());
            LOGGER.info("enchants btw: " + EnchantmentHelper.get(stack).toString());
            MishapThrowerJava.throwMishap(new MishapBadHandItem(
                stack,
                ENCHBOOK_WITH_ONE_ENCH,
                ENCHBOOK_WITH_NOTONE_ENCH,
                hand
            ));
        } else if (infusing < requiredAmel) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughItems(AMEL, infusing, requiredAmel));
        } else if (availableAmel < requiredAmel) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughItems(AMEL, availableAmel, requiredAmel));
        }
    }

    @Override
    public Long getCost() {
        return MediaConstants.CRYSTAL_UNIT * 5;
    }

    @Override
    public void accept() {
        // see? at least the VAULT isn't useless.
        vault.drain(Mutables::isAmel, requiredAmel, Flags.PRESET_Stacks_InvItem_UpToHotbar);

        Map<Enchantment, Integer> enchants = EnchantmentHelper.get(stack);
        Enchantment enchant = enchants.keySet().iterator().next();
        enchants.put(enchant, enchants.get(enchant) + 1);
        EnchantmentHelper.set(enchants, stack);
        ctx.replaceItem(
            stack -> true,
            stack,
            hand
        );
    }
}
