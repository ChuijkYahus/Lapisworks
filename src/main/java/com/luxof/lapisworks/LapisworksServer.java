package com.luxof.lapisworks;

import java.util.List;

import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.LapisworksNetworking.SEND_SENT;

import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C;
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import kotlin.Pair;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class LapisworksServer {
    public static void handleCastingGridPacket(
        MinecraftServer server,
        ServerPlayerEntity player,
        ServerPlayNetworkHandler handler,
        PacketByteBuf buf,
        PacketSender responseSender
    ) {
        boolean clearGrid = buf.readBoolean();
        if (clearGrid) {
            IXplatAbstractions.INSTANCE.clearCastingData(player);
            // i don't know why, it's just in the itemstaff code
            MsgClearSpiralPatternsS2C packet = new MsgClearSpiralPatternsS2C(player.getUuid());
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, packet);
            IXplatAbstractions.INSTANCE.sendPacketTracking(player, packet);
        }

        CastingVM vm = IXplatAbstractions.INSTANCE.getStaffcastVM(player, Hand.MAIN_HAND);
        List<ResolvedPattern> patterns = IXplatAbstractions.INSTANCE.getPatternsSavedInUi(player);
        Pair<List<NbtCompound>, NbtCompound> descs = vm.generateDescs();

        IXplatAbstractions.INSTANCE.sendPacketToPlayer(
            player,
            new MsgOpenSpellGuiS2C(
                Hand.MAIN_HAND,
                patterns,
                descs.getFirst(),
                descs.getSecond(),
                0 // it says "todo fix" in the hex casting github 'round here, wonder why
            )
        );
    }

    public static void lockIn() {
        ServerPlayNetworking.registerGlobalReceiver(
            LapisworksNetworking.OPEN_CASTING_GRID,
            (
                MinecraftServer server,
                ServerPlayerEntity player,
                ServerPlayNetworkHandler handler,
                PacketByteBuf buf,
                PacketSender responseSender
            ) -> {
                handleCastingGridPacket(server, player, handler, buf, responseSender);
            }
        );

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            Vec3d sentPos = ((EnchSentInterface)player).getEnchantedSentinel();
            Double sentAmbit = ((EnchSentInterface)player).getEnchantedSentinelAmbit();
            if (sentPos == null) { return; }
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(false);
            buf.writeVector3f(sentPos.toVector3f());
            buf.writeDouble(sentAmbit);
            ServerPlayNetworking.send(player, SEND_SENT, buf);
        });
    }
}
