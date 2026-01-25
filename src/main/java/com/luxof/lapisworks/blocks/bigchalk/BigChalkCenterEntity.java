package com.luxof.lapisworks.blocks.bigchalk;

import at.petrak.hexcasting.api.casting.math.HexPattern;

import com.luxof.lapisworks.blocks.stuff.StampableBE;
import com.luxof.lapisworks.init.ModBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.jetbrains.annotations.Nullable;

public class BigChalkCenterEntity extends BlockEntity implements StampableBE {
    public final Direction facing;
    public final Direction attachedTo;
    public BigChalkCenterEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.BIG_CHALK_CENTER_ENTITY_TYPE, pos, state);
        this.attachedTo = state.get(BigChalkCenter.ATTACHED);
        this.facing = state.get(BigChalkCenter.FACING);

        /*long seed = pos.getX() * 73428767L
              ^ pos.getY() * 912931L
              ^ pos.getZ() * 42317861L;

        seed = (seed ^ (seed >>> 13)) * 1274126177L;
        seed ^= (seed >>> 16);

        textVariant = (int)Math.round((seed & 0xFFFFFFFFL) / (double) 0x100000000L);*/
        textVariant = Math.min((int)Math.floor(3 * Math.random()), 2);
    }

    /** decides what text is displayed on the chalk. */
    public int textVariant;
    public boolean altTexture = false;

    @Nullable
    private HexPattern pattern = null;
    public boolean powered = false;
    @Nullable
    public HexPattern getPattern() { return pattern; }
    @Override
    public void stamp(HexPattern pattern, Direction horizontalPlayerFacing) {
        this.pattern = pattern;
        save();
    }

    public void save() {
        markDirty();
        BlockState state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, 0);
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        altTexture = nbt.getBoolean("altTexture");
        textVariant = nbt.getInt("textVariant");
        if (nbt.contains("pattern")) pattern = HexPattern.fromNBT(nbt.getCompound("pattern"));
        powered = nbt.getBoolean("powered");
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("altTexture", altTexture);
        nbt.putInt("textVariant", textVariant);
        if (pattern != null) nbt.put("pattern", pattern.serializeToNBT());
        nbt.putBoolean("powered", powered);
    }

    @Override @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
