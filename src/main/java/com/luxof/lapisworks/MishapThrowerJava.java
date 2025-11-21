package com.luxof.lapisworks;

import java.util.Optional;

import com.luxof.lapisworks.MishapThrower;

import at.petrak.hexcasting.api.casting.mishaps.Mishap;

// VSCode has heinous Kotlin support for Minecraft modding,
// and I REFUSE to use IntelliJ.
// So I made this just to stop it from being mad at me EVERYWHERE.
public class MishapThrowerJava {
    public static void throwMishap(Mishap mishap) {
        MishapThrower.throwMishap(mishap);
    }

    public static <T extends Object> T throwIfEmpty(Optional<T> opt, Mishap mishap) {
        if (opt.isEmpty()) { MishapThrower.throwMishap(mishap); }
        return opt.get();
    }
}
