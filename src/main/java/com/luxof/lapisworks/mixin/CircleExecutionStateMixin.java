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
@Mixin(value = CircleExecutionState.class) //There is a net/minecraft/util/math/BlockPos class in the full annotation, so it had to be remap=true so mixin could find it.
public abstract class CircleExecutionStateMixin {
    /** mixinextras is neat */
    @Inject(
        method = "createNew",
        at = @At(
            value = "INVOKE",
            //at.petrak.hexcasting.api.casting.circles.ICircleComponent.possibleExitDirections(net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState bs, net.minecraft.world.World world)
            //target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z",
            target = "Lat/petrak/hexcasting/api/casting/circles/ICircleComponent;possibleExitDirections(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;)Ljava/util/EnumSet;",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void beforePossibleExitDirections(
        BlockEntityAbstractImpetus _impetus,
        ServerPlayerEntity _player,
        CallbackInfoReturnable<Result<CircleExecutionState, BlockPos>> callback,
        @Local ServerWorld level,
        @Local Stack<Pair<Direction, BlockPos>> todo,
        @Local Direction enterDir,
        @Local BlockPos herePos,
        @Local ICircleComponent cmp
    ) {
        // jump slate needs to hijack here and add it's own exit destination, lest the spell circle fail as the exit is not directly adjacent.
        if (!(cmp instanceof JumpSlate jmpSlate)) return;
        Pair<Direction, BlockPos> exit = jmpSlate.getProbableExitPlace(enterDir, herePos, level);
        if (exit == null) return;
        todo.add(exit);
    }
}
