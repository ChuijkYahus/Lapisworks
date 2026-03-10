package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.mishaps.MishapBadTrinket;
import com.luxof.lapisworks.mishaps.MishapNotWearingTrinket;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.Lapisworks.getFirstTrinketIfEquipped;
import static com.luxof.lapisworks.LapisworksIDs.WRITEABLE;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfNull;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class WriteNecklace extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    @Override
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        Iota iota = stack.get(0);
        LivingEntity ent = throwIfNull(ctx.getCastingEntity(), new MishapBadCaster());

        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(throwIfNull(
            getFirstTrinketIfEquipped(ent, FOCUS_NECKLACE),
            new MishapNotWearingTrinket(FOCUS_NECKLACE)
        ).getRight());
        // "let's make the error message more helpful!"
        // :thumbsup:
        if (!iotaHolder.writeIota(iota, true))
            throw new MishapBadTrinket(FOCUS_NECKLACE, WRITEABLE);

        PlayerEntity truename = MishapOthersName
            .getTrueNameFromDatum(iota, (PlayerEntity)ctx.getCastingEntity());
        if (truename != null) throw new MishapOthersName(truename);

        iotaHolder.writeIota(iota, false);

        return List.of();
    }
}
