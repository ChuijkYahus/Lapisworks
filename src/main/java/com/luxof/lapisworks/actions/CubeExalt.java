package com.luxof.lapisworks.actions;

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

import com.luxof.lapisworks.MishapThrowerJava;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.Vec3d;

public class CubeExalt implements Action {
    public int getArgc() {
        return 4;
    }

    @Override
    public OperationResult operate(CastingEnvironment ctx, CastingImage img, SpellContinuation cont) {
        List<Iota> stack = new ArrayList<Iota>(img.getStack());
        if (stack.size() < getArgc()) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughArgs(3, stack.size()));
        }
        int lastIdx = stack.size() - 1;
        SpellList intrs = OperatorUtils.getList(stack, lastIdx - 3, getArgc());
        Vec3d pointA = OperatorUtils.getVec3(stack, lastIdx - 2, getArgc());
        Vec3d pointB = OperatorUtils.getVec3(stack, lastIdx - 1, getArgc());

        CastingImage img2 = img.withUsedOp().copy(
            stack,
            img.getParenCount(),
            img.getParenthesized(),
            img.getEscapeNext(),
            img.getOpsConsumed(),
            img.getUserData()
        );
        SpellList datum = OperatorUtils.getBool(stack, lastIdx, getArgc()) ?
            generatePointsInHollowCube(pointA, pointB) :
            generatePointsInFilledCube(pointA, pointB);
        FrameForEach frame = new FrameForEach(datum, intrs, null, new ArrayList<Iota>());

        return new OperationResult(img2, List.of(), cont.pushFrame(frame), HexEvalSounds.THOTH);
    }
    
    public static SpellList generatePointsInFilledCube(Vec3d pointA, Vec3d pointB) {
        List<Iota> points = new ArrayList<Iota>();
        // naming things is hard
        boolean a = pointA.x < pointB.x;
        boolean b = pointA.y < pointB.y;
        boolean c = pointA.z < pointB.z;
        Vec3d A = new Vec3d(
            a ? pointA.x : pointB.x,
            b ? pointA.y : pointB.y,
            c ? pointA.z : pointB.z
        );
        Vec3d B = new Vec3d(
            a ? pointB.x : pointA.x,
            b ? pointB.y : pointA.y,
            c ? pointB.z : pointA.z
        );
        
        for (double z = A.z; z < Math.ceil(B.z); z++) {
            for (double y = A.y; y < Math.ceil(B.y); y++) {
                for (double x = A.x; x < Math.ceil(B.x); x++) {
                    points.add(new Vec3Iota(new Vec3d(Math.min(x, B.x), Math.min(y, B.y), Math.min(z, B.z))));
                }
            }
        }

        return new SpellList.LList(points);
    }

    public static SpellList generatePointsInHollowCube(Vec3d pointA, Vec3d pointB) {
        List<Iota> points = new ArrayList<Iota>();

        // my brain is too small for the other approach (tried and skill issued)
        generatePointsInFilledCube(pointA, pointB).forEach((Iota anyIota) -> {
            Vec3d any = ((Vec3Iota)anyIota).getVec3();
            if (any.x == pointA.x || any.y == pointA.y || any.z == pointA.z ||
                any.x == pointB.x || any.y == pointB.y || any.z == pointB.z) {
                points.add(new Vec3Iota(any));
            }
        });

        return new SpellList.LList(points);
    }
}
