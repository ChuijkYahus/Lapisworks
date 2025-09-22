package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.mishaps.MishapBadHandItem;

import static com.luxof.lapisworks.Lapisworks.getStackFromHand;
import static com.luxof.lapisworks.Lapisworks.intToHand;
import static com.luxof.lapisworks.LapisworksIDs.READABLE;
import static com.luxof.lapisworks.init.Mutables.maxHands;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ReadFromHand implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        int hand = OperatorUtils.getIntBetween(args, 0, 0, maxHands - 1, getArgc());
        final Hand HAND = intToHand(hand);
        ItemStack heldStack = getStackFromHand(ctx, hand);
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(heldStack);
        // no clue what these checks are for other than the first one
        if (iotaHolder == null ||
            (iotaHolder.readIota(ctx.getWorld()) == null &&
             iotaHolder.emptyIota() == null)) {
            MishapThrowerJava.throwMishap(new MishapBadHandItem(
                heldStack,
                READABLE,
                HAND
            ));
        }
        return List.of(iotaHolder.readIota(ctx.getWorld()));
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
    }

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public long getMediaCost() {
        return 0;
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
