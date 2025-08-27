package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.MishapThrowerJava;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.LivingEntity;

public class ReadableMainHand implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        Optional<LivingEntity> casterOp = Optional.of(ctx.getCastingEntity());
        if (casterOp.isEmpty()) { MishapThrowerJava.throwMishap(new MishapBadCaster()); }
        LivingEntity caster = casterOp.get();
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(caster.getMainHandStack());
        return List.of(new BooleanIota(
            iotaHolder != null && (
                iotaHolder.readIota(ctx.getWorld()) != null || iotaHolder.emptyIota() != null
            )
        ));
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
    }

    @Override
    public int getArgc() {
        return 0;
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
