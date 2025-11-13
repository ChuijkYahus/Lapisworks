package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.mishaps.MishapBadTrinket;
import com.luxof.lapisworks.mishaps.MishapNotWearingTrinket;

import static com.luxof.lapisworks.Lapisworks.getFirstTrinketIfEquipped;
import static com.luxof.lapisworks.LapisworksIDs.READABLE;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE;

import dev.emi.trinkets.api.SlotReference;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

public class ReadNecklace implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        LivingEntity ent = ctx.getCastingEntity();
        if (ent == null) MishapThrowerJava.throwMishap(new MishapBadCaster());

        Pair<SlotReference, ItemStack> necklace = getFirstTrinketIfEquipped(ent, FOCUS_NECKLACE);

        if (necklace == null) {
            MishapThrowerJava.throwMishap(
                new MishapNotWearingTrinket(FOCUS_NECKLACE)
            );
            return null; // VSCode likes complaining about null
        }

        ItemStack trinket = necklace.getRight();
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(trinket);
        if (iotaHolder == null ||
            (iotaHolder.readIota(ctx.getWorld()) == null &
             iotaHolder.emptyIota() == null)) {
            MishapThrowerJava.throwMishap(new MishapBadTrinket(
                FOCUS_NECKLACE,
                READABLE
            ));
            return null; // VSCode likes complaining about null
        }
        return List.of(iotaHolder.readIota(ctx.getWorld()));
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
    }

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public long getMediaCost() {
        return 0;
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
