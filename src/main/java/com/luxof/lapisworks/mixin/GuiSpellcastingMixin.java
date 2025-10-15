package com.luxof.lapisworks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.init.ModItems;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(GuiSpellcasting.class)
public class GuiSpellcastingMixin {
    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tick(CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            ci.cancel();
        }
        if (
            Lapisworks.trinketEquipped(player, ModItems.CASTING_RING) ||
            Lapisworks.trinketEquipped(player, ModItems.AMEL_RING) ||
            Lapisworks.trinketEquipped(player, ModItems.AMEL_RING2)
        ) {
            // PROBABLY should add check for if player opened casting menu
            // but i'll let that be until it bites me in the ass
            ci.cancel();
        }
    }
}
