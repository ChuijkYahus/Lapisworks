package com.luxof.lapisworks.chalk;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.common.lib.HexAttributes;

import java.util.UUID;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

public abstract class RitualExecutionState {
    public BlockPos currentPos;
    /** for intersections. In case of 3-ways where forward exists not, simply pick a random side. */
    @Nullable public Direction forward;
    public CastingImage currentImage;
    @Nullable public UUID caster;
    @Nullable public FrozenPigment pigment;

    protected RitualExecutionState(
        BlockPos currentPos,
        Direction forward,
        CastingImage currentImage,
        @Nullable UUID caster,
        @Nullable FrozenPigment pigment
    ) {
        this.currentPos = currentPos;
        this.forward = forward;
        this.currentImage = currentImage;
        this.caster = caster;
        this.pigment = pigment;
    }
    protected RitualExecutionState(
        NbtCompound nbt,
        ServerWorld world
    ) {
        loadBase(nbt, world);
    }

    @Nullable
    public ServerPlayerEntity getCaster(ServerWorld world) {
        if (caster == null) return null;
        if (world.getEntity(caster) instanceof ServerPlayerEntity sp) return sp;
        return null;
    }
    public FrozenPigment getPigment() {
        return pigment;
    }
    public FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
        this.pigment = pigment;
        return pigment;
    }

    protected void saveBase(NbtCompound nbt) {
        NbtCompound posNbt = new NbtCompound();
        posNbt.putInt("x", currentPos.getX());
        posNbt.putInt("y", currentPos.getY());
        posNbt.putInt("z", currentPos.getZ());
        nbt.put("currentPos", posNbt);
        
        nbt.putString("forward", forward.toString());

        nbt.put("image", currentImage.serializeToNbt());

        if (caster != null)
            nbt.putUuid("caster", caster);

        if (pigment != null)
            nbt.put("pigment", pigment.serializeToNBT());
    }


    public abstract void save(NbtCompound nbt);
    public NbtCompound save() {
        NbtCompound nbt = new NbtCompound();
        save(nbt);
        return nbt;
    }
    public abstract long extractMedia(long cost, boolean simulate);
    public abstract void printMessage(Text message, ServerWorld world);
    public boolean isVecInHalfAmbitOfCaster(Vec3d vec, ServerWorld world) {
        ServerPlayerEntity caster = getCaster(world);
        if (caster == null) return false;


        double playerAmbit = caster.getAttributeValue(HexAttributes.AMBIT_RADIUS) / 2;
        if (caster.getPos().squaredDistanceTo(vec) <= playerAmbit*playerAmbit) return true;


        Sentinel sentinel = HexAPI.instance().getSentinel(caster);
        double sentinelAmbit = caster.getAttributeValue(HexAttributes.SENTINEL_RADIUS);
        if (
            sentinel != null &&
            sentinel.extendsRange() &&
            sentinel.position().squaredDistanceTo(vec) <=
                (sentinelAmbit*sentinelAmbit + 0.00000000001) / 2
        )
            return true;
        

        EnchSentInterface enchantedSentinel = (EnchSentInterface)caster;
        Vec3d enchSentPos = enchantedSentinel.getEnchantedSentinel();
        double enchSentAmbit = enchantedSentinel.getEnchantedSentinelAmbit();
        if (
            enchSentPos != null &&
            enchSentPos.squaredDistanceTo(vec) <= enchSentAmbit*enchSentAmbit / 2
        )
            return true;


        return false;
    }
    public boolean isVecInAmbit(Vec3d vec, ServerWorld world) {
        return isVecInHalfAmbitOfCaster(vec, world);
    }
    /** Returns whether to continue. */
    public abstract boolean tick(ServerWorld world);


    protected static final record BaseConstructorArguments (
        BlockPos currentPos,
        Direction forward,
        CastingImage currentImage,
        @Nullable UUID caster,
        @Nullable FrozenPigment pigment
    ) {}
    protected static BaseConstructorArguments loadBase(NbtCompound nbt, ServerWorld world) {
        NbtCompound posNbt = nbt.getCompound("currentPos");
        BlockPos currentPos = new BlockPos(
            posNbt.getInt("x"),
            posNbt.getInt("y"),
            posNbt.getInt("z")
        );

        Direction forward = Direction.byName(nbt.getString("forward"));

        CastingImage currentImage = CastingImage.loadFromNbt(nbt.getCompound("image"), world);

        UUID caster = null;
        if (nbt.contains("caster")) caster = nbt.getUuid("caster");

        FrozenPigment pigment = null;
        if (nbt.contains("pigment")) pigment = FrozenPigment.fromNBT(nbt.getCompound("pigment"));

        return new BaseConstructorArguments(currentPos, forward, currentImage, caster, pigment);
    }
}
