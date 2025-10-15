package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import net.minecraft.item.ItemStack;

import com.luxof.lapisworks.VAULT.CastEnvVAULT;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.luxof.lapisworks.mixinsupport.GetVAULT;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CastingEnvironment.class, remap = false)
public abstract class CastingEnvironmentMixin implements GetStacks, GetVAULT {
    private final VAULT vault = VAULT.of((CastingEnvironment)(Object)this);
    @Override public VAULT grabVAULT() {
        ((CastEnvVAULT)vault).initInnerServPlayerVAULT(); // <-- silly shenanigans that are required
        return this.vault;
    }

    @Shadow
    protected abstract List<HeldItemInfo> getPrimaryStacks();
    @Override
    public List<HeldItemInfo> getHeldStacks() {
        List<HeldItemInfo> stacks = new ArrayList<>(this.getPrimaryStacks());
        if (stacks.size() == 2) {
            // hexcasting does it the wrong way (they do offhand first then mainhand)
            HeldItemInfo buffer = stacks.get(0);
            stacks.set(0, stacks.get(1));
            stacks.set(1, buffer);
        }
        return List.copyOf(stacks);
    }
    @Override
    public List<HeldItemInfo> getHeldStacksOtherFirst() { return this.getPrimaryStacks(); }
    @Override
    public List<ItemStack> getHeldItemStacks() {
        return this.getHeldStacks().stream().map((held) -> held.stack()).toList();
    }
    @Override
    public List<ItemStack> getHeldItemStacksOtherFirst() {
        return this.getHeldStacksOtherFirst().stream().map((held) -> held.stack()).toList();
    }
}
