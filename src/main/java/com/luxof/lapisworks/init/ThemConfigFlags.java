package com.luxof.lapisworks.init;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;

import vazkii.patchouli.api.PatchouliAPI;

// really evil fucking bullshit to circumvent some stuff that was probably there for a reason
// why?
// i like pretty shit
// i guess you could also make the excuse for originality too, afaik no one has done
// per-world shape patterns
public class ThemConfigFlags {
    /** A generic ID */
    public static HashMap<String, Integer> chosenFlags = new HashMap<String, Integer>();
    public static HashMap<String, List<String>> allPerWorldShapePatterns = new HashMap<String, List<String>>();

    public static NbtCompound turnChosenIntoNbt() {
        NbtCompound nbt = new NbtCompound();
        chosenFlags.forEach((key, val) -> { nbt.putInt(key, val); });
        return nbt;
    }

    /** Call after you register the variant patterns.
     * <p>Grabs all patterns with the id <code>{genericId}{X}</code> where X is an int.
     * <p>There needn't be a range of patterns with X between 0 and (amount prefixed with genericId).
     * <p>Yes, you may use this during registration to add variants to existing PW Shape Patterns. */
    public static void registerPWShapePattern(String genericId) {
        chosenFlags.put(genericId, null);

        List<String> sigs = new ArrayList<>();
        Map<String, String> unsorted = new HashMap<>();

        Registry<ActionRegistryEntry> registry = IXplatAbstractions.INSTANCE.getActionRegistry();
        for (var key : registry.getKeys()) {

            String id = key.getValue().toString();
            if (id.startsWith(genericId))
                unsorted.put(id, registry.get(key).prototype().anglesSignature());

        }

        for (Integer i = 0; i < unsorted.keySet().size(); i++) {
            String id = genericId + i.toString();
            if (unsorted.containsKey(id))
                sigs.add(unsorted.get(id));
        }

        allPerWorldShapePatterns.put(genericId, sigs);
    }

    /** Call after initializing your PW Shape Patterns.
     * <p>Safe to call more than once. */
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
