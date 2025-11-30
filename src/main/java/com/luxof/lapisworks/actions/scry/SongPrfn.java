package com.luxof.lapisworks.actions.scry;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;

import com.luxof.lapisworks.blocks.entities.LiveJukeboxEntity;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.LapisworksIDs.LIVE_JUKEBOX_BLOCK;
import static com.luxof.lapisworks.MishapThrowerJava.assertInRange;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class SongPrfn implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        BlockPos pos = OperatorUtils.getBlockPos(args, 0, getArgc());
        assertInRange(ctx, pos);
        LiveJukeboxEntity liveJukeBox = throwIfEmpty(
            ctx.getWorld().getBlockEntity(pos, ModBlocks.LIVE_JUKEBOX_ENTITY_TYPE),
            new MishapBadBlock(pos, LIVE_JUKEBOX_BLOCK)
        );

        return List.of(
            //                                                   vvvvvv fuck you too Java
            new ListIota(liveJukeBox.notes.stream().map(integ -> (Iota)new DoubleIota(integ)).toList()),
            new DoubleIota(liveJukeBox.frequency)
        );
    }

    @Override
    public int getArgc() { return 1; }

    @Override
    public long getMediaCost() { return 0L; }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
    
}
