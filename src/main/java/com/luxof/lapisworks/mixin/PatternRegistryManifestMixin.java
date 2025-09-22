package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;
import static com.luxof.lapisworks.init.ThemConfigFlags.allPerWorldShapePatterns;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PatternRegistryManifest.class, remap = false)
public abstract class PatternRegistryManifestMixin {

    @Inject(at = @At("HEAD"), method = "matchPattern", cancellable = true)
    private static void matchPattern(
        HexPattern pat,
        CastingEnvironment environment,
        boolean checkForAlternateStrokeOrders,
        CallbackInfoReturnable<PatternShapeMatch> cir
    ) {
        // i only have to invalidate the ones that aren't chosen
        // because hex will automatically validate the one that is chosen if this is it.
        if (!chosenFlags.values().contains(null)) {
            String sig = pat.anglesSignature();
            for (String patId : allPerWorldShapePatterns.keySet()) {
                List<String> pats = allPerWorldShapePatterns.get(patId);
                int idx = pats.indexOf(sig);
                if (idx == -1) { continue; }
                if (idx != chosenFlags.get(patId)) {
                    cir.setReturnValue(new PatternShapeMatch.Nothing());
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
    }
}
