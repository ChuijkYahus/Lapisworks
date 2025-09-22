package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;

import com.luxof.lapisworks.mixinsupport.GetStacks;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CastingEnvironment.class, remap = false)
public abstract class CastingEnvironmentMixin implements GetStacks {
    @Shadow
    protected abstract List<HeldItemInfo> getPrimaryStacks();
    @Override
    public List<HeldItemInfo> getHeldStacks() {
        // bitchass hexcasting does it the wrong way (they do offhand first then mainhand)
        List<HeldItemInfo> stacks = new ArrayList<>(this.getPrimaryStacks());
        HeldItemInfo buffer = stacks.get(0);
        stacks.set(0, stacks.get(1));
        stacks.set(1, buffer);
        return stacks;
    }
}
