package com.luxof.lapisworks.chalk;

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import java.util.UUID;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

public class OneTimeRitualExecutionState extends RitualExecutionState {
    public long media;
    public UUID casterWhileCasterIsDisabled = null;
    public boolean isCasterDisabled = false;

    public OneTimeRitualExecutionState(
        BlockPos currentPos,
        Direction forward,
        CastingImage currentImage,
        @Nullable UUID caster,
        @Nullable FrozenPigment pigment,
        long startingMedia
    ) {
        super(currentPos, forward, currentImage, caster, pigment);
        this.media = startingMedia;
    }

    public void disableCaster() {
        if (isCasterDisabled) return;
        casterWhileCasterIsDisabled = caster;
        caster = null;
        isCasterDisabled = true;
    }

    public void enableCaster() {
        if (!isCasterDisabled) return;
        caster = casterWhileCasterIsDisabled;
        casterWhileCasterIsDisabled = caster;
        isCasterDisabled = false;
    }

    /** bypasses disabled casters and shiet. */
    @Nullable
    public ServerPlayerEntity getCasterAbsolute(ServerWorld world) {
        if (!isCasterDisabled)
            return getCaster(world);
        else if (world.getEntity(casterWhileCasterIsDisabled) instanceof ServerPlayerEntity sp)
            return sp;
        else
            return null;
    }

    @Override
    public boolean isVecInAmbit(Vec3d vec, ServerWorld world) {
        return isVecInAmbitOfPlayer(vec, world, 0.5)
            || isVecInAmbitOfTuneableAmethyst(vec, world, 1);
    }

    @Override
    public void save(NbtCompound nbt) {
        super.saveBase(nbt);
        nbt.putLong("media", media);
    }

    public static OneTimeRitualExecutionState load(NbtCompound nbt, ServerWorld world) {
        BaseConstructorArguments base = loadBase(nbt, world);
        return new OneTimeRitualExecutionState(
            base.currentPos(),
            base.forward(),
            base.currentImage(),
            base.caster(),
            base.pigment(),
            nbt.getLong("media")
        );
    }

    @Override
    public long extractMedia(long cost, boolean simulate) {
        long take = Math.min(cost, media);
        cost -= take;
        if (!simulate) media -= take;
        return cost;
    }

    @Override
    public void printMessage(Text message, ServerWorld world) {
        ServerPlayerEntity caster = getCasterAbsolute(world);
        if (caster == null) return;
        caster.sendMessage(message);
    }

    @Override
    public boolean tick(ServerWorld world) {
        RitualCastEnv env = new RitualCastEnv(world, this);

        if (!(world.getBlockEntity(currentPos) instanceof RitualComponent)) {
            // explosions, Break Block, damn near anything could cause this.
            return false;
        }
        
        RitualComponent ritualComponent = (RitualComponent)world.getBlockEntity(currentPos);
        Pair<BlockPos, CastingImage> result = ritualComponent.execute(env);
        world.setBlockState(currentPos.offset(forward.getOpposite()), Blocks.AIR.getDefaultState());

        if (result == null || result.getLeft() == null) {
            world.setBlockState(currentPos, Blocks.AIR.getDefaultState());
            return false;
        }

        forward = Direction.fromVector(
            result.getLeft().getX() - currentPos.getX(),
            result.getLeft().getY() - currentPos.getY(),
            result.getLeft().getZ() - currentPos.getZ()
        );
        currentPos = result.getLeft();
        currentImage = result.getRight();

        return true;
    }
}
