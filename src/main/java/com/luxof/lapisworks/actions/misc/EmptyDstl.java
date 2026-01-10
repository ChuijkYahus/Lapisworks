package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.Lapisworks.castRay;

import java.util.List;

import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EmptyDstl extends ConstMediaActionNCT {
    public int argc = 2;
    public long mediaCost = (long)(MediaConstants.DUST_UNIT * 0.01);

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos start = stack.getBlockPosInRange(0);
        BlockPos end = stack.getBlockPosInRange(1);

        Vec3d dir = end.toCenterPos().subtract(start.toCenterPos()).normalize();

        return List.of(new BooleanIota(
            !castRay(
                BlockPos.ofFloored(start.toCenterPos().add(dir)),
                BlockPos.ofFloored(end.toCenterPos().subtract(dir)),
                pos -> 
                    new Pair<>(pos, !ctx.getWorld().getBlockState(pos).isOpaque())
            ).getRight()
        ));
    }
}
