package com.luxof.lapisworks.client;

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.interop.hextended.items.AmelOrb;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.Lapisworks.clamp;
import static com.luxof.lapisworks.Lapisworks.nullConfigFlags;
import static com.luxof.lapisworks.Lapisworks.prettifyFloat;
import static com.luxof.lapisworks.LapisworksIDs.AMEL_ORB_IS_FILLED;
import static com.luxof.lapisworks.LapisworksIDs.BLOCKING_MPP;
import static com.luxof.lapisworks.LapisworksIDs.SCRYING_MIND_END;
import static com.luxof.lapisworks.LapisworksIDs.SCRYING_MIND_START;
import static com.luxof.lapisworks.LapisworksIDs.SEND_PWSHAPE_PATS;
import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;
import static com.luxof.lapisworks.init.ModItems.IRON_SWORD;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;

import com.mojang.datafixers.util.Pair;

import java.util.Optional;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import vazkii.patchouli.api.PatchouliAPI;

public class LapisworksClient implements ClientModInitializer {
    public Vec3d bufferSentinelPos = null;
    public Double bufferSentinelAmbit = null;
    public boolean playerHasJoined = false;

    public static void registerMPPs() {
        ModelPredicateProviderRegistry.register(
            IRON_SWORD,
            BLOCKING_MPP, // first person doesn't work but WHATEVER
            (stack, world, entity, seed) -> {
                return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
            }    
        );
        if (Lapisworks.HEXTENDED_INTEROP) {
            ModelPredicateProviderRegistry.register(
                com.luxof.lapisworks.interop.hextended.Lapixtended.AMEL_ORB,
                AMEL_ORB_IS_FILLED,
                (stack, world, entity, seed) -> {
                    AmelOrb orb = (AmelOrb)stack.getItem();
                    return orb.getPlaceInAmbit(stack) == null ? 0.0F : 1.0F;
                }
            );
        }
    }

    public static void overlayWorld(MatrixStack ms, float tickDelta) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            Vec3d sentinel = ((EnchSentInterface)player).getEnchantedSentinel();
            if (sentinel != null) { THEGRANDROTATER.renderEnchantedSentinel(sentinel, ms, tickDelta); }
        }
    }

    @Override
    public void onInitializeClient() {
        // the eternal fucking grammar battle with this simple Markiplier ass log will drive me insane
        // thankful i won't have to edit this file anymore
        // ^^^^ what was that, chief?
        LOGGER.info("Hello everybody my name is LapisworksClient and today what we are going to do is: scrying lens tooltips, make blocks transparent, keybinds, networking, Model Predicate Providers, spin 4D hypercubes for the FUNNY, and client-side rendering!");
        LOGGER.info("Does NONE of that sound fun? Well, that's because it isn't. So let's get started, shall we?");

        TrinketRendererRegistry.registerRenderer(ModItems.AMEL_JAR, new JarTrinketRenderer());

        // we all thank hexxy for adding simple addDisplayer() instead of requiring mixin in unison
        ScryingLensOverlayRegistry.addDisplayer(
            ModBlocks.MIND_BLOCK,
            (lines, state, pos, observer, world, direction) -> {
                Optional<BlockEntity> blockEntityOpt = world.getBlockEntity(pos, ModBlocks.MIND_ENTITY_TYPE);
                if (blockEntityOpt.isEmpty()) { return; }
                MindEntity blockEntity = (MindEntity)blockEntityOpt.get();
                lines.add(
                    new Pair<ItemStack, Text>(
                        new ItemStack(ModItems.MIND),
                        SCRYING_MIND_START.copy().append(
                            Text.literal(
                                prettifyFloat(clamp(blockEntity.mindCompletion, 0f, 100f))
                            )
                        ).append(
                            SCRYING_MIND_END
                        ).formatted(
                            Formatting.LIGHT_PURPLE
                        )
                    )
                );
            }
        );
        WorldRenderEvents.AFTER_TRANSLUCENT.register((ctx) -> {
            overlayWorld(ctx.matrixStack(), ctx.tickDelta());
        });
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MIND_BLOCK, RenderLayer.getTranslucent());

        KeyEvents.staticInit();
        ClientTickEvents.END_CLIENT_TICK.register(KeyEvents::endClientTick);

        ClientPlayNetworking.registerGlobalReceiver(
            SEND_SENT,
            (
                client,
                handler,
                buf,
                responseSender
            ) -> {
                LOGGER.info("Got a SEND_SENT packet!");
                boolean banishSentinel = buf.readBoolean();
                if (banishSentinel) {
                    if (!this.playerHasJoined) {
                        this.bufferSentinelPos = null;
                        this.bufferSentinelAmbit = null;
                    } else {
                        ((EnchSentInterface)client.player).setEnchantedSentinel(null, null);
                    }
                    return;
                }
                Vec3d newPos = new Vec3d(buf.readVector3f());
                Double newAmbit = buf.readDouble();
                if (!this.playerHasJoined) {
                    this.bufferSentinelPos = newPos;
                    this.bufferSentinelAmbit = newAmbit;
                } else {
                    ((EnchSentInterface)client.player).setEnchantedSentinel(newPos, newAmbit);
                }
            }
        );
        ClientPlayNetworking.registerGlobalReceiver(
            SEND_PWSHAPE_PATS,
            (client, handler, buf, responseSender) -> {
                LOGGER.info("So we got sent the shit.");
                NbtCompound nbt = buf.readNbt();
                for (String flag : chosenFlags.keySet()) {
                    chosenFlags.put(flag, nbt.getInt(flag));
                    LOGGER.info("For " + flag + " we have " + nbt.getInt(flag));
                    // vv unused but may allow for neat stuff in the future
                    PatchouliAPI.get().setConfigFlag(
                        flag + String.valueOf(nbt.getInt(flag)),
                        true
                    );
                }
            }
        );

        ClientPlayConnectionEvents.JOIN.register((
            handler,
            sender,
            client
        ) -> {
            if (client.player == null) {
                LOGGER.error("Genuinely how the fuck is client.player null on JOIN??");
                return;
            }
            this.playerHasJoined = true;
            ((EnchSentInterface)client.player).setEnchantedSentinel(
                this.bufferSentinelPos,
                this.bufferSentinelAmbit
            );
        });

        // i could just use the server_stopping event and send a packet then but i already wrote this so
        // whatever
        ClientPlayConnectionEvents.DISCONNECT.register((
            handler,
            client
        ) -> {
            this.playerHasJoined = false;
            this.bufferSentinelPos = null;
            this.bufferSentinelAmbit = null;
            nullConfigFlags();
            if (client.player != null) {
                // i don't know, okay? just in case or something
                ((EnchSentInterface)client.player).setEnchantedSentinel(null, null);
            }
        });
    }
}
