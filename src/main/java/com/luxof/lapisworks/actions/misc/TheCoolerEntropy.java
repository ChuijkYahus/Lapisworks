package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

public class TheCoolerEntropy extends ConstMediaActionNCT {
    //private static final double BIGGEST_LESS_THAN_ONE = 0.999999999999999888977697537484;
    // iirc hex compares down to the 4th digit after the dot
    private static final double BIGGEST_LESS_THAN_ONE = 0.9999;

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        return List.of(new DoubleIota(Math.random()*BIGGEST_LESS_THAN_ONE));
    }
}
