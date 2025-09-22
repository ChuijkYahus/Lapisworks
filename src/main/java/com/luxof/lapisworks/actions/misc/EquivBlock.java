package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;

import java.util.List;

import com.luxof.lapisworks.MishapThrowerJava;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class EquivBlock implements ConstMediaAction {
    public BlockPos confirmInAmbit(BlockPos pos, CastingEnvironment ctx) {
        try { ctx.assertPosInRange(pos); }
        catch (Mishap e) { MishapThrowerJava.throwMishap(e); }
        return pos;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        Block a = ctx.getWorld().getBlockState(confirmInAmbit(OperatorUtils.getBlockPos(args, 0, getArgc()), ctx)).getBlock();
        Block b = ctx.getWorld().getBlockState(confirmInAmbit(OperatorUtils.getBlockPos(args, 1, getArgc()), ctx)).getBlock();
        return List.of(new BooleanIota(a == b));
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
        return 0;
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
