package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.Vec3d;

public class SphereDst implements Action {
    public int getArgc() {
        return 3;
    }

    @Override
    public OperationResult operate(CastingEnvironment ctx, CastingImage img, SpellContinuation cont) {
        List<Iota> stack = new ArrayList<Iota>(img.getStack());
        if (stack.size() < getArgc())
            throw new MishapNotEnoughArgs(3, stack.size());

        int lastIdx = stack.size() - 1;
        SpellList intrs = OperatorUtils.getList(stack, lastIdx - 2, getArgc());
        Vec3d pos = OperatorUtils.getVec3(stack, lastIdx - 1, getArgc());
        int radius = OperatorUtils.getIntBetween(stack, lastIdx, 1, 64, getArgc());
        stack.remove(lastIdx);
        stack.remove(lastIdx - 1);
        stack.remove(lastIdx - 2);

        CastingImage img2 = img.withUsedOp().copy(
            stack,
            img.getParenCount(),
            img.getParenthesized(),
            img.getEscapeNext(),
            img.getOpsConsumed(),
            img.getUserData()
        );
        SpellList datum = generatePointsOnHollowSphere(pos, radius);
        FrameForEach frame = new FrameForEach(datum, intrs, null, new ArrayList<Iota>());

        return new OperationResult(img2, List.of(), cont.pushFrame(frame), HexEvalSounds.THOTH);
    }

    public static SpellList generatePointsOnHollowSphere(Vec3d offset, int radius) {
        // technique: go through a cube and add all blocks which distance to center ~= radius
        // (the vultures pick another piece off of HexKinetics)
        List<Iota> sphere = new ArrayList<Iota>();
        double innerRad = (radius - 1);

        double checkRad = radius * radius;
        double checkInnerRad = innerRad * innerRad;

        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    double sum = x*x + y*y + z*z;
                    if (sum <= checkRad && sum >= checkInnerRad) {
                        sphere.add(new Vec3Iota(new Vec3d(x, y, z).add(offset)));
                    }
                }
            }
        }

        return new SpellList.LList(sphere);
    }
}
