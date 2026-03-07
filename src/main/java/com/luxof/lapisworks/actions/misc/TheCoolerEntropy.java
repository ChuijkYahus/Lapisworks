package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.ArrayList;
import java.util.List;

// was cool for other reasons before, but those reasons were invalid and this got repurposed
// it's now cool because it's named "Dealer's Purification" (cool name)
public class TheCoolerEntropy extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        ArrayList<Iota> list = stack.getJUSTAList(0);

        return List.of(
            list.get(world.getRandom().nextBetween(0, list.size()))
        );
    }
}
