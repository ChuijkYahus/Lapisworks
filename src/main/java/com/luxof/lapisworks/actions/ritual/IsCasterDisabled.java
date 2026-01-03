package com.luxof.lapisworks.actions.ritual;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.MishapThrowerJava.assertInOneTimeRitual;

import java.util.List;

public class IsCasterDisabled extends ConstMediaActionNCT {
    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment env) {
        return List.of(
            new BooleanIota(
                !assertInOneTimeRitual(env).isCasterDisabled
            )
        );
    }
}
