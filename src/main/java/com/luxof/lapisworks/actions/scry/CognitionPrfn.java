package com.luxof.lapisworks.actions.scry;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;

import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;

import static com.luxof.lapisworks.Lapisworks.prettifyDouble;
import static com.luxof.lapisworks.LapisworksIDs.MIND_BLOCK;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class CognitionPrfn extends ConstMediaActionNCT {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        BlockPos mindPos = OperatorUtils.getBlockPos(args, 0, getArgc());
        ctx.assertPosInRange(mindPos);

        MindEntity blockEntity = throwIfEmpty(
            ctx.getWorld().getBlockEntity(mindPos, ModBlocks.MIND_ENTITY_TYPE),
            new MishapBadBlock(mindPos, MIND_BLOCK)
        );

        return List.of(new DoubleIota(prettifyDouble((double)blockEntity.mindCompletion)));
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
