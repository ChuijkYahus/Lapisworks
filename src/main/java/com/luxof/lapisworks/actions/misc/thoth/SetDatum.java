package com.luxof.lapisworks.actions.misc.thoth;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;

import com.luxof.lapisworks.mishaps.MishapInvalidContinuation;

import static com.luxof.lapisworks.Lapisworks.CastingImgWithStack;
import static com.luxof.lapisworks.Lapisworks.pullFrameOfType;
import static com.luxof.lapisworks.Lapisworks.setHighestFrameOfTypeTo;

import java.util.ArrayList;
import java.util.List;

public class SetDatum implements Action {
    public int getArgc() { return 1; }

    @Override
    public OperationResult operate(CastingEnvironment ctx, CastingImage img, SpellContinuation cont) {
        List<Iota> stack = new ArrayList<>(img.getStack());
        if (stack.size() < getArgc()) throw new MishapNotEnoughArgs(getArgc(), stack.size());

        
        int lastIdx = stack.size() - 1;
        SpellList newDatum = OperatorUtils.getList(stack, lastIdx, 1);
        stack.remove(lastIdx);

        FrameForEach thothFrame = pullFrameOfType(cont, FrameForEach.class);
        if (thothFrame == null)
            throw new MishapInvalidContinuation("mishaps.lapisworks.descs.thothframe");

        SpellContinuation newCont = setHighestFrameOfTypeTo(
            cont,
            FrameForEach.class,
            thothFrame.copy(
                newDatum, thothFrame.getCode(), thothFrame.getBaseStack(), thothFrame.getAcc()
            )
        );


        return new OperationResult(
            CastingImgWithStack(img.withUsedOp(), stack),
            List.of(),
            newCont,
            HexEvalSounds.NORMAL_EXECUTE
        );
    }
}
