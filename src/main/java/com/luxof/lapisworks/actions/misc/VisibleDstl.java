package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import static com.luxof.lapisworks.Lapisworks.castRay;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VisibleDstl implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        Entity entity = OperatorUtils.getEntity(args, 0, getArgc());
        BlockPos start = BlockPos.ofFloored(entity.getEyePos());
        BlockPos end = OperatorUtils.getBlockPos(args, 1, getArgc());

        ctx.assertPosInRange(start);
        ctx.assertPosInRange(end);

        Vec3d dir = end.toCenterPos().subtract(start.toCenterPos()).normalize();

        return List.of(new BooleanIota(
            entity.getRotationVector().dotProduct(dir) >= 0.1
            && !castRay(
                start,
                BlockPos.ofFloored(end.toCenterPos().subtract(dir)),
                pos -> 
                    new Pair<>(pos, !ctx.getWorld().getBlockState(pos).isOpaque())
            ).getRight()
        ));
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
    }

    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    public long getMediaCost() {
        return (long)(MediaConstants.DUST_UNIT * 0.01);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
