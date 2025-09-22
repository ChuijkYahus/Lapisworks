package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.blocks.Mind;
import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.Lapisworks.prettifyDouble;
import static com.luxof.lapisworks.LapisworksIDs.MIND_BLOCK;

import java.util.List;
import java.util.Optional;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class CognitionPrfn implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        BlockPos mindPos = OperatorUtils.getBlockPos(args, 0, getArgc());
        try { ctx.assertPosInRange(mindPos); }
        catch (Mishap mishap) { MishapThrowerJava.throwMishap(mishap); }
        MishapBadBlock needMind = new MishapBadBlock(mindPos, MIND_BLOCK);
        if (!(ctx.getWorld().getBlockState(mindPos).getBlock() instanceof Mind)) {
            MishapThrowerJava.throwMishap(needMind);
        }


        Optional<BlockEntity> blockEntityOpt = ctx
            .getWorld()
            .getBlockEntity(mindPos, ModBlocks.MIND_ENTITY_TYPE);
        if (blockEntityOpt.isEmpty()) { MishapThrowerJava.throwMishap(needMind); }
        MindEntity blockEntity = (MindEntity)blockEntityOpt.get();

        return List.of(new DoubleIota(prettifyDouble((double)blockEntity.mindCompletion)));
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
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
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
