package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;

import static com.luxof.lapisworks.LapisworksIDs.SIMP_IMP_BLOCK;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class AskSImp implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        BlockPos pos = OperatorUtils.getBlockPos(args, 0, getArgc());
        if (ctx.getWorld().getBlockEntity(pos) instanceof SimpleImpetusEntity simpleImpetus) {
            return List.of(
                simpleImpetus.getIsTuned() ?
                    new PatternIota(HexPattern.fromAngles(simpleImpetus.getTuned(), HexDir.EAST))
                    : new NullIota()
            );
        } else {
            MishapThrowerJava.throwMishap(new MishapBadBlock(pos, SIMP_IMP_BLOCK));
            // won't stop yelling if i don't do this, even though it's unreachable.
            // god bless java-kotlin interop
            return List.of();
        }
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
    }

    @Override
    public int getArgc() { return 1; }

    @Override
    public long getMediaCost() { return 0L; }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
