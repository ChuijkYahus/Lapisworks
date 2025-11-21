package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import static com.luxof.lapisworks.Lapisworks.getFirstTrinketIfEquipped;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE;

import dev.emi.trinkets.api.SlotReference;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

public class WriteableNecklace implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        List<Iota> FALSE = List.of(new BooleanIota(false));
        LivingEntity ent = ctx.getCastingEntity();
        if (ent == null) return FALSE;

        Pair<SlotReference, ItemStack> necklace = getFirstTrinketIfEquipped(ent, FOCUS_NECKLACE);

        if (necklace == null) return FALSE;

        ItemStack trinket = necklace.getRight();
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(trinket);
        return List.of(new BooleanIota(
            iotaHolder != null && (
                iotaHolder.writeable()
            )
        ));
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
