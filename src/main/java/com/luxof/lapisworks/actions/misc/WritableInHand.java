package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import static com.luxof.lapisworks.Lapisworks.getStackFromHand;
import static com.luxof.lapisworks.init.Mutables.maxHands;

import java.util.List;

public class WritableInHand implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        int hand = OperatorUtils.getIntBetween(args, 0, 0, maxHands - 1, getArgc());
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(getStackFromHand(ctx, hand));
        // apparently no, OperatorUtils.asActionResult can't be used here because it returns
        // a List<BooleanIota> instead of a List<Iota> (I'm doing the same thing??)
        return List.of(new BooleanIota(
            iotaHolder != null && (
                iotaHolder.writeable()
            )
        ));
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
