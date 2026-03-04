package com.luxof.lapisworks.actions.scry;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.blocks.stuff.LinkableMediaBlock;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.MishapThrowerJava.assertLinkableThere;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class GetCondenserMedia extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPosInRange(0);
        LinkableMediaBlock linkable = assertLinkableThere(pos, ctx);

        return List.of(
            new DoubleIota(linkable.getMediaHere() / (double)MediaConstants.DUST_UNIT)
        );
    }
}
