package com.luxof.lapisworks.actions.ritual;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.MishapThrowerJava.assertInOneTimeRitual;

import java.util.List;

public class EnableCaster extends ConstMediaActionNCT {
    public int argc = 0;
    public long mediaCost = 0;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment env) {
        assertInOneTimeRitual(env).enableCaster();
        return List.of();
    }
}
