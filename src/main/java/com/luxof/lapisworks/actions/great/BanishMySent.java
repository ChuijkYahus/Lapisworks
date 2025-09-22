package com.luxof.lapisworks.actions.great;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapUnenlightened;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;

import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import org.joml.Vector3f;

public class BanishMySent implements ConstMediaAction {
    public int getArgc() {
        return 0;
    }

    @Override
    public long getMediaCost() {
        return (long)(MediaConstants.DUST_UNIT * 0.01);
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        if (!ctx.isEnlightened()) {
            MishapThrowerJava.throwMishap(new MishapUnenlightened());
        }
        Optional<LivingEntity> casterOp = Optional.of(ctx.getCastingEntity());
        if (casterOp.isEmpty()) { MishapThrowerJava.throwMishap(new MishapBadCaster()); }
        else if (!(casterOp.get() instanceof PlayerEntity)) {
            MishapThrowerJava.throwMishap(new MishapBadCaster());
        }
        ServerPlayerEntity caster = (ServerPlayerEntity)casterOp.get();

        ((EnchSentInterface)caster).setEnchantedSentinel(null, null);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(true);
        buf.writeVector3f(new Vector3f());
        buf.writeDouble(0.0);
        ServerPlayNetworking.send(caster, SEND_SENT, buf);

        return List.of();
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
