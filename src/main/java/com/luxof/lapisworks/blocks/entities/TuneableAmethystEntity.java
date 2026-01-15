package com.luxof.lapisworks.blocks.entities;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.blocks.TuneableAmethyst;
import com.luxof.lapisworks.blocks.stuff.UnlinkableMediaBlock;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.mixinsupport.RitualsUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class TuneableAmethystEntity extends BlockEntity implements UnlinkableMediaBlock {
    public TuneableAmethystEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TUNEABLE_AMETHYST_ENTITY_TYPE, pos, state);
    }

    public long media = 0L;
    @Nullable private Iota tunedFrequency = null;
    @Nullable private NbtCompound tunedNbt = null;

    public final double minAmbit = 2.0;
    public final double ambitCap = 16;
    private final double ambitCapSqr = ambitCap*ambitCap;
    private final double minAmbitSqr = minAmbit*minAmbit;

    public final long mediaCap = (long)(MediaConstants.DUST_UNIT * ambitCapSqr);

    public double getMediaInDust() { return (double)media / (double)MediaConstants.DUST_UNIT; }
    public double getAmbit() { return Math.max(minAmbit, Math.sqrt(getMediaInDust())); }
    public double getAmbitSqr() { return Math.max(minAmbitSqr, getMediaInDust()); }

    /** to clear, you can also pass in a NullIota.
     * <p>Server-only method. Throws if on client. */
    public void tune(@Nullable Iota frequency) {
        // i'd do @Environment(EnvType.CLIENT) but that shit makes it disappear on the server(?!)
        if (world.isClient) throw new IllegalStateException("Server-only method.");
        RitualsUtil ritualsUtil = (RitualsUtil)world;

        if (tunedFrequency != null) {
            if (frequency != null && Iota.tolerates(tunedFrequency, frequency)) return;

            ritualsUtil.getTuneables(tunedFrequency).remove(pos);
        }

        tunedFrequency = frequency instanceof NullIota ? null : frequency;
        tunedNbt = frequency instanceof NullIota ? null : IotaType.serialize(frequency);
        if (tunedFrequency != null)
            ritualsUtil.addTuneable(tunedFrequency, pos);
        save();
    }
    public Iota getTunedFrequency() { return tunedFrequency; }

    /** Used usually on client where there is no way to deserialize an iota. */
    @Nullable
    public Text getTunedFrequencyDisplay() {
        return tunedNbt != null ? IotaType.getDisplay(tunedNbt) : null;
    }

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
        if (tunedFrequency != null) {
            tunedNbt = IotaType.serialize(tunedFrequency);
            nbt.put("frequency", tunedNbt);
        } else if (tunedNbt != null)
            nbt.put("frequency", tunedNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        media = nbt.getLong("media");
        if (nbt.contains("frequency")) {
            tunedNbt = nbt.getCompound("frequency");
            if (world instanceof ServerWorld sw)
                tunedFrequency = IotaType.deserialize(nbt, sw);
        } else {
            tunedFrequency = null;
            tunedNbt = null;
        }
    }

    @Override @Nullable public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt() { return createNbt(); }

    @Override public BlockPos getThisPos() { return this.getPos(); }
    @Override public long getMediaHere() { return media; }
    @Override public void setMedia(long media) { this.media = media; updateState(); save(); }
    @Override public long getMaxMedia() { return mediaCap; }
}
