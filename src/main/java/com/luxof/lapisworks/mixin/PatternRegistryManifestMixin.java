package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;
import static com.luxof.lapisworks.init.ThemConfigFlags.allPerWorldShapePatterns;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = PatternRegistryManifest.class, remap = false)
public abstract class PatternRegistryManifestMixin {

    @WrapMethod(method = {"matchPattern"})
    private static PatternShapeMatch matchPattern(
        HexPattern pat,
        CastingEnvironment environment,
        boolean checkForAlternateStrokeOrders,
        Operation<PatternShapeMatch> og
    ) {
        PatternShapeMatch shapeMatch = og.call(pat, environment, checkForAlternateStrokeOrders);

        // i only have to invalidate the ones that aren't chosen
        // because hex will automatically validate the one that is chosen if this is it.
        if (!chosenFlags.values().contains(null)) {
            String sig = pat.anglesSignature();
            for (String patId : allPerWorldShapePatterns.keySet()) {
                List<String> pats = allPerWorldShapePatterns.get(patId);
                int idx = pats.indexOf(sig);
                if (idx == -1) { continue; }
                if (idx != chosenFlags.get(patId)) {
                    shapeMatch = new PatternShapeMatch.Nothing();
                } else {
                    break; // approved!
                }
            }
            for (List<String> pats : allPerWorldShapePatterns.values()) {
                if (!pats.contains(sig)) { continue; }
            }
        } else {
            LOGGER.error("Why the fuck have the flags not been chosen yet?!");
        }

        return shapeMatch;
    }
}
