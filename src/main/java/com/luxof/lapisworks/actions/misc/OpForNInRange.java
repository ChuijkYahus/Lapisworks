package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;

import com.luxof.lapisworks.frames.FrameExecuteManyTimes;

import static com.luxof.lapisworks.Lapisworks.CastingImgWithStack;

import java.util.ArrayList;
import java.util.List;

public class OpForNInRange implements Action {
    private final boolean isThisKitkat;
    public OpForNInRange(boolean isThisKitkat) {
        this.isThisKitkat = isThisKitkat;
    }
    @Override
    public OperationResult operate(CastingEnvironment ctx, CastingImage img, SpellContinuation cont) {
        List<Iota> stack = img.getStack();
        SpellList instrs = OperatorUtils.getList(stack, stack.size() - 1, stack.size());
        stack.remove(stack.size() - 1);

        ContinuationFrame newFrame;
        if (isThisKitkat) {
            int thisManyTimes = OperatorUtils.getInt(stack, stack.size() - 1, stack.size());
            stack.remove(stack.size() - 1);
            newFrame = new FrameExecuteManyTimes(instrs, stack, thisManyTimes);
        } else {
            int from = OperatorUtils.getInt(stack, stack.size() - 2, stack.size());
            int to = OperatorUtils.getInt(stack, stack.size() - 1, stack.size());
            stack.remove(stack.size() - 1);
            stack.remove(stack.size() - 1);

            if (from == to) {
                return new OperationResult(
                    CastingImgWithStack(img, stack),
                    List.of(),
                    cont,
                    HexEvalSounds.THOTH
                );
            }

            List<Iota> data = new ArrayList<>();
            boolean traditional = from < to;
            int inc = traditional ? 1 : -1;

            // is this understandable? lmao
            for (int i = from; from < to ? i < to : i > to; i += inc) {
                data.add(new DoubleIota(i));
            }
            SpellList datum = new ListIota(data).getList();

            newFrame = new FrameForEach(datum, instrs, stack, new ArrayList<>());
        }

        return new OperationResult(
            CastingImgWithStack(img.withUsedOp(), stack),
            List.of(),
            cont.pushFrame(newFrame),
            HexEvalSounds.THOTH
        );
    }
}
