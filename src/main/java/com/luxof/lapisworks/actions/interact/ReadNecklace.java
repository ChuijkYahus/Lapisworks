package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.mishaps.MishapBadTrinket;
import com.luxof.lapisworks.mishaps.MishapNotWearingTrinket;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.Lapisworks.getFirstTrinketIfEquipped;
import static com.luxof.lapisworks.LapisworksIDs.READABLE;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfNull;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE;

import java.util.List;

import net.minecraft.entity.LivingEntity;

public class ReadNecklace extends ConstMediaActionNCT {
    public int argc = 0;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack args, CastingEnvironment ctx) {
        LivingEntity ent = throwIfNull(ctx.getCastingEntity(), new MishapBadCaster());

        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(throwIfNull(
            getFirstTrinketIfEquipped(ent, FOCUS_NECKLACE),
            new MishapNotWearingTrinket(FOCUS_NECKLACE)
        ).getRight());

        if (
            iotaHolder == null ||
            (iotaHolder.readIota(ctx.getWorld()) == null & iotaHolder.emptyIota() == null)
        )
            throw new MishapBadTrinket(
                FOCUS_NECKLACE,
                READABLE
            );

        return List.of(iotaHolder.readIota(ctx.getWorld()));
    }
}
