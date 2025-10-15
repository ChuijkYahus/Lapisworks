package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.mishaps.MishapBadHandItem;

import static com.luxof.lapisworks.Lapisworks.getStackFromHand;
import static com.luxof.lapisworks.Lapisworks.intToHand;
import static com.luxof.lapisworks.LapisworksIDs.NON_IOTAHOLDER;
import static com.luxof.lapisworks.LapisworksIDs.READONLY_HOLDER;
import static com.luxof.lapisworks.LapisworksIDs.WRITEABLE;
import static com.luxof.lapisworks.init.Mutables.Mutables.maxHands;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

public class WriteToHand implements SpellAction {
    @Override
    public Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        Iota iota = args.get(0);
        int hand = OperatorUtils.getIntBetween(args, 1, 0, maxHands - 1, getArgc());
        final Hand HAND = intToHand(hand);
        ItemStack stack = getStackFromHand(ctx, hand);
        if (stack == null) {
            MishapThrowerJava.throwMishap(new MishapBadHandItem(
                stack,
                WRITEABLE,
                HAND
            ));
        }
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(stack);
        // "let's make the error message more helpful!"
        // :thumbsup:
        if (iotaHolder == null) {
            MishapThrowerJava.throwMishap(new MishapBadHandItem(
                stack,
                WRITEABLE,
                NON_IOTAHOLDER,
                HAND
            ));
        } else if (!iotaHolder.writeIota(iota, true)) {
            MishapThrowerJava.throwMishap(new MishapBadHandItem(
                stack,
                WRITEABLE,
                READONLY_HOLDER,
                HAND
            ));
        }
        PlayerEntity truename = MishapOthersName
            .getTrueNameFromDatum(iota, (PlayerEntity)ctx.getCastingEntity());
        if (truename != null) { MishapThrowerJava.throwMishap(new MishapOthersName(truename)); }

        return new SpellAction.Result(
            new Spell(iota, iotaHolder),
            0,
            List.of(),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final Iota iota;
        public final ADIotaHolder iotaHolder;
        public Spell(Iota iota, ADIotaHolder iotaHolder) {
            this.iota = iota;
            this.iotaHolder = iotaHolder;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            iotaHolder.writeIota(iota, false);
        }

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }

    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return SpellAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }

    @Override
    public boolean awardsCastingStat(CastingEnvironment arg0) {
        return SpellAction.DefaultImpls.awardsCastingStat(this, arg0);
    }

    @Override
    public Result executeWithUserdata(List<? extends Iota> arg0, CastingEnvironment arg1, NbtCompound arg2) {
        return SpellAction.DefaultImpls.executeWithUserdata(this, arg0, arg1, arg2);
    }

    @Override
    public boolean hasCastingSound(CastingEnvironment arg0) { return true; }
}
