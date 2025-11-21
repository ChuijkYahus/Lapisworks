package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;

import java.util.List;

import net.minecraft.nbt.NbtCompound;

public class DoNothing implements SpellAction {
    @Override
    public boolean awardsCastingStat(CastingEnvironment arg0) { return false; }

    @Override
    public Result execute(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return new SpellAction.Result(
            new DoNothingSpell(),
            0L,
            List.of(),
            0
        );
    }

    public static class DoNothingSpell implements RenderedSpell {
        @Override
        public void cast(CastingEnvironment arg0) {
            return;
        }

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }

    @Override
    public Result executeWithUserdata(List<? extends Iota> arg0, CastingEnvironment arg1, NbtCompound arg2) {
        return SpellAction.DefaultImpls.executeWithUserdata(this, arg0, arg1, arg2);
    }

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public boolean hasCastingSound(CastingEnvironment arg0) {
        return false;
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return SpellAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
