package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect.ConsumeMedia;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;
import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.init.ModPOIs;
import com.luxof.lapisworks.init.Patterns;
import com.luxof.lapisworks.mixinsupport.Markable;

import static com.luxof.lapisworks.Lapisworks.exemptFromMediaConsumptionDecrease;
import static com.luxof.lapisworks.Lapisworks.getIdOf;

import java.util.List;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage.OccupationStatus;

import org.jetbrains.annotations.NotNull;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = PatternIota.class, remap = false)
public abstract class PatternIotaMixin implements Markable {
    @Unique private static RegistryKey<ActionRegistryEntry> doNothing = null;
    @Unique private boolean marked = false;
    @Unique @Override public PatternIota mark() {
        this.marked = true;
        return (PatternIota)(Object)this;
    }

    @Unique
    private static boolean triggerSimpleImpeti(
        HexPattern pat,
        boolean isValid,
        CastingEnvironment ctx
    ) {
        if (doNothing == null) {
            doNothing = Patterns.ARCHON_OF_MEANINGLESSNESS;
        }
        ServerWorld sw = ctx.getWorld();
        // maffs
        try {
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
        } catch (Exception e) { // HexDebug
            return false;
        }
    }

    @Shadow
    public abstract HexPattern getPattern();

    @Inject(
        method = "execute",
        at = @At(
            value = "INVOKE_ASSIGN",
            // woah, being able to browse bytecode to just copy-paste is so fucking neat
            target = "at/petrak/hexcasting/common/casting/PatternRegistryManifest.matchPattern(Lat/petrak/hexcasting/api/casting/math/HexPattern;Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;Z)Lat/petrak/hexcasting/api/casting/PatternShapeMatch;"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    public @NotNull void execute(
        CastingVM vm,
        ServerWorld world,
        SpellContinuation continuation,
        CallbackInfoReturnable<CastResult> cir,
        @Local LocalRef<PatternShapeMatch> lookupRef
    ) {
        if (triggerSimpleImpeti(
                getPattern(),
                !(lookupRef.get() instanceof PatternShapeMatch.Nothing),
                vm.getEnv()
            )) {
            lookupRef.set(new PatternShapeMatch.Normal(doNothing));
        }
        if (marked && exemptFromMediaConsumptionDecrease(getIdOf(lookupRef.get())))
            marked = false;
    }

    @Unique
    public double getCostMultiplier() {
        return LapisConfig.getCurrentConfig().getGrandRitualSettings().cost_multiplier();
    }
    @Inject(
        method = "execute",
        at = @At(
            value = "NEW",
            target = "at/petrak/hexcasting/api/casting/eval/CastResult"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    public @NotNull void execute(
        CastingVM vm,
        ServerWorld world,
        SpellContinuation continuation,
        CallbackInfoReturnable<CastResult> cir,
        @Local List<OperatorSideEffect> sideEffects
    ) {
        if (!marked) return;
        List<OperatorSideEffect> copy = List.copyOf(sideEffects);

        for (int i = 0; i < copy.size(); i++) {
            OperatorSideEffect sideEffect = copy.get(i);

            if (sideEffect instanceof ConsumeMedia fx) {
                sideEffects.set(i, new ConsumeMedia((long)(fx.getAmount() * getCostMultiplier())));
            }
        }
    }
}
