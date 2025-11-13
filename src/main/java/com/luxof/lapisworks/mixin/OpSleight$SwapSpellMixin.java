package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.utils.NBTHelper;

import com.luxof.lapisworks.interop.hexical.blocks.CradleEntity;
import com.luxof.lapisworks.mixinsupport.ItemEntityMinterface;

import static com.luxof.lapisworks.LapisworksIDs.IS_IN_CRADLE;

import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.NotNull;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
    "miyucomics.hexical.features.wristpocket.OpSleight$SwapSpell",
    "miyucomics.hexical.casting.patterns.wristpocket.OpSleight$SwapSpell"
}, remap = false)
public abstract class OpSleight$SwapSpellMixin {
    @Shadow
    public abstract ItemEntity getItem();

    @Inject(at = @At("HEAD"), method = "cast")
    public void cast(@NotNull CastingEnvironment env, CallbackInfo ci) {
        ItemEntity item = getItem();
        if (NBTHelper.contains(item.getStack(), IS_IN_CRADLE))
            NBTHelper.remove(item.getStack(), IS_IN_CRADLE);
        BlockPos cradlePos = ((ItemEntityMinterface)item).getBlockPosOfCradle();
        if (cradlePos == null) return;
        CradleEntity cradle = (CradleEntity)env.getWorld().getBlockEntity(cradlePos);
        if (cradle == null) return;
        cradle.clear();
    }
}
