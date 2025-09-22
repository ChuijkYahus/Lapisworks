package com.luxof.lapisworks.init;

import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.NbtCompound;

import vazkii.patchouli.api.PatchouliAPI;

// really evil fucking dark magic bullshit to circumvent some stuff that was probably there for a reason
// (still probably better then Hexical's panic())
// why?
// i like pretty shit
// i guess you could also make the excuse for originality too, afaik no one has done
// per-world shape patterns
public class ThemConfigFlags {
    public static HashMap<String, Integer> chosenFlags = new HashMap<String, Integer>();
    public static HashMap<String, List<String>> allPerWorldShapePatterns = new HashMap<String, List<String>>();

    public static NbtCompound turnChosenIntoNbt() {
        NbtCompound nbt = new NbtCompound();
        chosenFlags.forEach((key, val) -> { nbt.putInt(key, val); });
        return nbt;
    }

    /** make sure you call this before you call declareEm() */
    public static void registerPWShapePattern(String genericId, List<String> sigs) {
        chosenFlags.put(genericId, null);
        allPerWorldShapePatterns.put(genericId, sigs);
    }

    public static void declareEm() {
        allPerWorldShapePatterns.keySet().forEach(
            (String id) -> {
                List<String> sigs = allPerWorldShapePatterns.get(id);
                for (int i = 0; i < sigs.size(); i++) {
                    PatchouliAPI.get().setConfigFlag(id + String.valueOf(i), false);
                }
            }
        );
    }
}
