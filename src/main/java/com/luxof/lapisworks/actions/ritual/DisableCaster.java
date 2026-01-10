package com.luxof.lapisworks.actions.ritual;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.MishapThrowerJava.assertInOneTimeRitual;

import java.util.List;

public class DisableCaster extends ConstMediaActionNCT {
    public int argc = 0;
    public long mediaCost = 0;

    // i feel like an ADHDer on Adderall
    // "is this how normal mfs feel?"
    // except instead of focus it's not having sore hands every time i make a pattern
    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment env) {
        assertInOneTimeRitual(env).disableCaster();
        return List.of();
    }
}
