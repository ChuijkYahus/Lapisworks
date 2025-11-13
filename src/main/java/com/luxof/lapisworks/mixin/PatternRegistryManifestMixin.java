package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.math.HexAngle;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;
import com.luxof.lapisworks.init.ModPOIs;
import com.luxof.lapisworks.init.Patterns;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;
import static com.luxof.lapisworks.init.ThemConfigFlags.allPerWorldShapePatterns;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage.OccupationStatus;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = PatternRegistryManifest.class, remap = false)
public abstract class PatternRegistryManifestMixin {
    @Unique
    private static RegistryKey<ActionRegistryEntry> doNothing = null;

    @Unique
    private static boolean triggerSimpleImpeti(
        HexPattern pat,
        boolean isValid,
        CastingEnvironment ctx
    ) {
        ServerWorld sw = ctx.getWorld();
        // maffs
        return sw.getPointOfInterestStorage().getInCircle(
            any -> any.matchesKey(ModPOIs.SIMP_IMPETUS_KEY),
            BlockPos.ofFloored(ctx.mishapSprayPos()),
            32,
            OccupationStatus.ANY
        ).filter(poi -> {
            BlockPos pos = poi.getPos();
            if (!(sw.getBlockEntity(pos) instanceof SimpleImpetusEntity simpleImpetus)) return false;
            return simpleImpetus.tryTrigger(
                pat.anglesSignature(),
                isValid,
                // so it doesn't explode in my face one day
                ctx.getCastingEntity() instanceof ServerPlayerEntity sp ? sp : null
            );
        }).count() > 0;
    }

    @Shadow
    @Final
    private static ConcurrentMap<List<HexAngle>, RegistryKey<ActionRegistryEntry>> NORMAL_ACTION_LOOKUP;

    @WrapMethod(method = {"matchPattern"})
    private static PatternShapeMatch matchPattern(
        HexPattern pat,
        CastingEnvironment environment,
        boolean checkForAlternateStrokeOrders,
        Operation<PatternShapeMatch> og
    ) {
        if (doNothing == null) {
            doNothing = NORMAL_ACTION_LOOKUP.get(Patterns.ARCHON_OF_MEANINGLESSNESS.getAngles());
        }

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

        boolean triggeredATunedSImp = false;
        triggeredATunedSImp = triggerSimpleImpeti(
            pat,
            !(shapeMatch instanceof PatternShapeMatch.Nothing),
            environment
        );
        return shapeMatch instanceof PatternShapeMatch.Nothing && triggeredATunedSImp ?
            new PatternShapeMatch.Normal(doNothing) : shapeMatch;
    }
}
