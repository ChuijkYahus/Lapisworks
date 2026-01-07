package com.luxof.lapisworks.blocks.entities;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.blocks.TuneableAmethyst;
import com.luxof.lapisworks.blocks.stuff.LinkableMediaBlock;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.mixinsupport.RitualsUtil;

import java.util.Set;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class TuneableAmethystEntity extends BlockEntity implements LinkableMediaBlock {
    public TuneableAmethystEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TUNEABLE_AMETHYST_ENTITY_TYPE, pos, state);
    }

    public long media = 0L;
    @Nullable private Iota tunedFrequency = null;

    public final double ambitCap = 8;
    public final long mediaCap = (long)(MediaConstants.DUST_UNIT * ambitCap*ambitCap);

    public double getAmbit() { return Math.sqrt(media); }
    public double getAmbitSqr() { return media; }

    /** to clear the tuned frequency, pass in a NullIota. */
    @Environment(EnvType.SERVER)
    public void tune(Iota frequency) {
        RitualsUtil ritualsUtil = (RitualsUtil)world;

        if (tunedFrequency != null) {
            if (Iota.tolerates(tunedFrequency, frequency)) return;

            ritualsUtil.getTuneables(tunedFrequency).remove(pos);
        }

        if (frequency instanceof NullIota)
            tunedFrequency = null;
        else {
            tunedFrequency = frequency;
            ritualsUtil.addTuneable(tunedFrequency, pos);
        }
        save();
    }
    public Iota getTunedFrequency() { return tunedFrequency; }

    @SuppressWarnings("deprecation")
    public void updateState() {
        BlockState state = world.getBlockState(pos);
        int filled = Math.min(3, (int)Math.floor(media / (mediaCap / 3)));

        if (filled == state.get(TuneableAmethyst.STAGE)) return;
        BlockState newState = state.with(TuneableAmethyst.STAGE, filled);
        world.setBlockState(pos, newState);
        setCachedState(newState);
    }

    public void save() {
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("media", media);
        if (tunedFrequency != null)
            nbt.put("frequency", IotaType.serialize(tunedFrequency));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        media = nbt.getLong("media");
        if (nbt.contains("frequency") && world instanceof ServerWorld sw)
            tunedFrequency = IotaType.deserialize(nbt, sw);
    }

    @Override @Nullable public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt() { return createNbt(); }

    @Override public int getMaxNumberOfLinks() { return 0; }
    @Override public void addLink(BlockPos pos) {}
    @Override public void removeLink(BlockPos pos) {}
    @Override public boolean isLinkedTo(BlockPos pos) { return false; }
    @Override public Set<BlockPos> getLinks() { return Set.of(); }
    @Override public int getNumberOfLinks() { return 0; }
    @Override public BlockPos getThisPos() { return this.getPos(); }
    @Override public long getMediaHere() { return media; }
    @Override
    public long depositMedia(long amount, boolean simulate) {
        long prevMedia = media;

        long nowMedia = Math.min(media + amount, mediaCap);
        if (!simulate) {
            media = nowMedia;
            updateState();
            save();
        }

        return nowMedia - prevMedia;
    }
    @Override
    public long withdrawMedia(long amount, boolean simulate) {
        long prevMedia = media;

        long nowMedia = Math.max(media - amount, 0);
        if (!simulate) {
            media = nowMedia;
            updateState();
            save();
        }

        return prevMedia - nowMedia;
    }
}
