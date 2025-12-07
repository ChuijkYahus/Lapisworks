package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.GetServerStatus;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.thread.ReentrantThreadExecutor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements CommandOutput, GetServerStatus {
    public MinecraftServerMixin(String string) {
        super(string);
    }

    @Unique private boolean shuttingDown = false;

    // i don't wanna rely on isStopped(), it may be false when the server IS shutting down and
    // then the cradle will look like shit next time you load your world
    @Inject(at = @At("HEAD"), method = "shutdown")
    private void shutdown(CallbackInfo ci) { shuttingDown = true; }

    @Override @Unique public boolean isShuttingDown() { return shuttingDown; }
}
