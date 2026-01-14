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

import net.minecraft.registry.RegistryKey;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = PatternRegistryManifest.class, remap = false)
public abstract class PatternRegistryManifestMixin {

    @Unique private static String getIdOf(RegistryKey<?> key) {
        return key.getValue().toString();
    }
    @Nullable
    @Unique private static String getIdOf(PatternShapeMatch psm) {
        if (psm instanceof PatternShapeMatch.Normal nsm)
            return getIdOf(nsm.key);
        else if (psm instanceof PatternShapeMatch.PerWorld pwsm && pwsm.certain)
            return getIdOf(pwsm.key);
        else if (psm instanceof PatternShapeMatch.Special ssm)
            return getIdOf(ssm.key);
        else
            return null;
    }

    @WrapMethod(method = {"matchPattern"})
    private static PatternShapeMatch matchPattern(
        HexPattern pat,
        CastingEnvironment environment,
        boolean checkForAlternateStrokeOrders,
        Operation<PatternShapeMatch> og
    ) {
        PatternShapeMatch shapeMatch = og.call(pat, environment, checkForAlternateStrokeOrders);

        String id = getIdOf(shapeMatch);
        String sig = pat.anglesSignature();
        if (id == null) return shapeMatch;

        if (chosenFlags.values().contains(null)) {
            LOGGER.error("Why the fuck have the flags not been chosen yet?!");
            return shapeMatch;
        }

        for (String genericId : chosenFlags.keySet()) {
            if (!id.startsWith(genericId)) continue;
            int chosenIdx = chosenFlags.get(genericId);
            if (
                // rust method, anyone?
                !allPerWorldShapePatterns
                    .get(genericId)
                    .get(chosenIdx)
                    .equals(sig)
            )
                return new PatternShapeMatch.Nothing();
            break;
        }

        return shapeMatch;
    }
}
