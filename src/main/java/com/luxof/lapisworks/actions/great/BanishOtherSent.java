package com.luxof.lapisworks.actions.great;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapUnenlightened;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;

import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.joml.Vector3f;

public class BanishOtherSent implements ConstMediaAction {
    public int getArgc() {
        return 1;
    }
    // looks
    public int intFloor(double num) { return (int)(Math.floor(num)); }

    @Override
    public long getMediaCost() {
        return MediaConstants.DUST_UNIT * 5;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        if (!ctx.isEnlightened()) {
            MishapThrowerJava.throwMishap(new MishapUnenlightened());
        }
        BlockPos chosen = OperatorUtils.getBlockPos(args, 0, getArgc());

        ctx.getWorld().getServer().getPlayerManager().getPlayerList().forEach((ServerPlayerEntity ent) -> {
            Vec3d currSentPos = ((EnchSentInterface)ent).getEnchantedSentinel();
            if (currSentPos == null) { return; }
            BlockPos currSentBlock = new BlockPos(
                intFloor(currSentPos.x), intFloor(currSentPos.y), intFloor(currSentPos.z)
            );
            if (!currSentBlock.equals(chosen)) { return; }
            ((EnchSentInterface)ent).setEnchantedSentinel(null, null);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(true);
            buf.writeVector3f(new Vector3f());
            buf.writeDouble(0.0);
            ServerPlayNetworking.send(ent, SEND_SENT, buf);
        });

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
