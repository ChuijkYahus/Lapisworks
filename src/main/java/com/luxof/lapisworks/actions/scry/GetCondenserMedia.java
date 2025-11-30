package com.luxof.lapisworks.actions.scry;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.blocks.entities.MediaCondenserEntity;

import static com.luxof.lapisworks.LapisworksIDs.MEDIA_CONDENSER;
import static com.luxof.lapisworks.MishapThrowerJava.assertInRange;
import static com.luxof.lapisworks.MishapThrowerJava.assertIsThisBlock;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class GetCondenserMedia implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        BlockPos mindPos = OperatorUtils.getBlockPos(args, 0, getArgc());
        assertInRange(ctx, mindPos);

        MediaCondenserEntity condenserEntity = assertIsThisBlock(
            ctx, mindPos, MediaCondenserEntity.class, MEDIA_CONDENSER
        );

        return List.of(
            new DoubleIota(condenserEntity.getMediaHere() / (double)MediaConstants.DUST_UNIT)
        );
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
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
