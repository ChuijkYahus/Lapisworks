package com.luxof.lapisworks.client;

import at.petrak.hexcasting.common.lib.HexSounds;

import org.lwjgl.glfw.GLFW;

import com.luxof.lapisworks.init.ModItems;

import static com.luxof.lapisworks.Lapisworks.trinketEquipped;
import static com.luxof.lapisworks.LapisworksNetworking.OPEN_CASTING_GRID;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;

public class KeyEvents {
    public static KeyBinding useCastingRing = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "keys.lapisworks.use_casting_ring",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_G, // sorry hexical that keybind is mine now
        "key_category.lapisworks.lapisworks"
    ));

    public static void endClientTick(MinecraftClient client) {
        if (useCastingRing.wasPressed()) { onPressUseCastingRing(client); }
    }
    
    public static void onPressUseCastingRing(MinecraftClient client) {
        if (client.player == null) { return; }
        else if (
            !(trinketEquipped(client.player, ModItems.CASTING_RING) ||
            trinketEquipped(client.player, (Item)ModItems.AMEL_RING) ||
            trinketEquipped(client.player, (Item)ModItems.AMEL_RING2))
        ) { return; }

        PacketByteBuf buf = PacketByteBufs.create();
        if (client.player.isSneaking()) {
            // clear cast grid
            client.player.playSound(HexSounds.STAFF_RESET, 1f, 1f);
            buf.writeBoolean(true);
        } else {
            buf.writeBoolean(false);
        }
        ClientPlayNetworking.send(OPEN_CASTING_GRID, buf);
    }

    public static void staticInit() {}
}
