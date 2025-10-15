package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C;
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import static com.luxof.lapisworks.Lapisworks.pickUsingSeed;
import static com.luxof.lapisworks.Lapisworks.pickConfigFlags;
import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.Lapisworks.nullConfigFlags;
import static com.luxof.lapisworks.LapisworksIDs.SEND_PWSHAPE_PATS;
import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;
import static com.luxof.lapisworks.init.ThemConfigFlags.turnChosenIntoNbt;

import java.util.List;

import kotlin.Pair;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class LapisworksServer {
    public static void onJoinEnchSentStuff(
        ServerPlayNetworkHandler handler,
        PacketSender sender,
        MinecraftServer server
    ) {
        ServerPlayerEntity player = handler.getPlayer();
        Vec3d sentPos = ((EnchSentInterface)player).getEnchantedSentinel();
        Double sentAmbit = ((EnchSentInterface)player).getEnchantedSentinelAmbit();
        if (sentPos == null) { return; }
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);
        buf.writeVector3f(sentPos.toVector3f());
        buf.writeDouble(sentAmbit);
        ServerPlayNetworking.send(player, SEND_SENT, buf);
    }

    public static void onJoinPWShapeStuff(
        ServerPlayNetworkHandler handler,
        PacketSender sender,
        MinecraftServer server
    ) {
        ServerPlayerEntity player = handler.getPlayer();
        PacketByteBuf patsBuf = PacketByteBufs.create();
        // hell naw i'm not dealing with the two extra args to writeMap() (i dunno wtf those are)
        patsBuf.writeNbt(turnChosenIntoNbt());
        ServerPlayNetworking.send(player, SEND_PWSHAPE_PATS, patsBuf);
    }

    public static void juiceUpAttr(ServerPlayerEntity plr, EntityAttribute attr) {
        try {
            plr.getAttributes().getCustomInstance(attr).setBaseValue(
                plr.getAttributeBaseValue(attr) + ((LapisworksInterface)plr).getAmountOfAttrJuicedUpByAmel(attr)
            );
        } catch (Exception e) {
            LOGGER.error("We had an error in the juiceUpAttr part of the code. USUALLY this shouldn't happen, but it probably isn't a problem if it does.");
            e.printStackTrace();
        }
    }

    public static void onJoinLoadJuicedAttrs(
        ServerPlayNetworkHandler handler,
        PacketSender sender,
        MinecraftServer server
    ) {
        // somehow this works to fix the bug.
        // i'm guessing somewhere between PlayerEntity#readNbt() and the player loading into the world,
        // PlayerEntity.attributes is reset to the defaults (but then why doesn't health reset to default??)
        ServerPlayerEntity player = handler.getPlayer();
        juiceUpAttr(player, EntityAttributes.GENERIC_ATTACK_DAMAGE);
        juiceUpAttr(player, EntityAttributes.GENERIC_ATTACK_SPEED);
        //juiceUpAttr(player, EntityAttributes.GENERIC_MAX_HEALTH);
        juiceUpAttr(player, EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }

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
            LapisworksIDs.OPEN_CASTING_GRID,
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
            onJoinEnchSentStuff(handler, sender, server);
            onJoinPWShapeStuff(handler, sender, server);
            onJoinLoadJuicedAttrs(handler, sender, server);
        });
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            pickConfigFlags(pickUsingSeed(server.getOverworld().getSeed()));
        });
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> { nullConfigFlags(); });
    }
}
