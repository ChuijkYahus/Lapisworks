package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.casting.circles.CircleExecutionState;
import at.petrak.hexcasting.api.casting.circles.ICircleComponent;
import at.petrak.hexcasting.api.misc.Result;

import com.llamalad7.mixinextras.sugar.Local;

import com.luxof.lapisworks.blocks.JumpSlate;

import com.mojang.datafixers.util.Pair;

import java.util.Stack;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/** I am simple man. I see original interesting thing, I write cursed ass shit to get it. */
@Mixin(value = CircleExecutionState.class, remap = false)
public abstract class CircleExecutionStateMixin {
    /** mixinextras is neat */
    @Inject(
        method = "createNew",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void beforePossibleExitDirections(
        BlockEntityAbstractImpetus _1,
        ServerPlayerEntity _2,
        CallbackInfoReturnable<Result<CircleExecutionState, BlockPos>> cir,
        @Local ServerWorld level,
        @Local Stack<Pair<Direction, BlockPos>> todo,
        @Local Direction enterDir,
        @Local BlockPos herePos,
        @Local ICircleComponent cmp
    ) {
        if (!(cmp instanceof JumpSlate jmpSlate)) return;
        Pair<Direction, BlockPos> exit = jmpSlate.getProbableExitPlace(enterDir, herePos, level);
        if (exit == null) return;
        todo.add(exit);
    }
}
