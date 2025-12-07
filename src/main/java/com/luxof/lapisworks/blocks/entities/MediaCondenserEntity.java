package com.luxof.lapisworks.blocks.entities;

import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.blocks.MediaCondenser;
import com.luxof.lapisworks.blocks.stuff.LinkableMediaBlock;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class MediaCondenserEntity extends BlockEntity implements LinkableMediaBlock {
    public MediaCondenserEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.MEDIA_CONDENSER_ENTITY_TYPE, pos, state);
    }

    public long media = 0;
    public long mediaCap = MediaConstants.DUST_UNIT * 64;
    public HashSet<BlockPos> linkedCondensers = new HashSet<>();

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        ServerWorld sw = (ServerWorld)world;
        int filledState;
        try {
            filledState = Math.min(14, (int)Math.floor(media / (mediaCap / 15.0)));
        } catch (ArithmeticException e) { return; } // btw: does not return..????
        if (filledState == state.get(MediaCondenser.FILLED)) return;
        sw.setBlockState(
            pos,
            state.with(
                MediaCondenser.FILLED,
                filledState
            )
        );
    }

    private List<Integer> posToInts(HashSet<BlockPos> posList) {
        return posList.stream().flatMap(
            pos -> Stream.of(pos.getX(), pos.getY(), pos.getZ())
        ).toList();
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("media", media);
        nbt.putLong("max", mediaCap);
        nbt.putIntArray(
            "links",
            posToInts(linkedCondensers)
        );
    }

    private HashSet<BlockPos> intsToPos(int[] intArray) {
        HashSet<BlockPos> posList = new HashSet<>();
        int x = 0;
        int y = 0;
        int part = 0;
        for (int integer : intArray) {
            switch (part) {
                case 0 -> x = integer;
                case 1 -> y = integer;
                case 2 -> posList.add(new BlockPos(x, y, integer));
                default -> {}
            };
            part = (part + 1) % 3;
        }
        return posList;
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        media = nbt.getLong("media");
        mediaCap = nbt.getLong("max");
        linkedCondensers = intsToPos(nbt.getIntArray("links"));
    }

    @Override @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }



    @Override public void addLink(BlockPos pos) { linkedCondensers.add(pos); }
    @Override public void removeLink(BlockPos pos) { linkedCondensers.remove(pos); }
    @Override public boolean isLinkedTo(BlockPos pos) { return linkedCondensers.contains(pos); }
    @Override public Set<BlockPos> getLinks() { return linkedCondensers; }
    @Override public int getNumberOfLinks() { return linkedCondensers.size(); }
    @Override public BlockPos getThisPos() { return this.getPos(); }
    @Override public long getMediaHere() { return media; }
    @Override
    public long depositMedia(long amount, boolean simulate) {
        long prevMedia = media;

        long nowMedia = Math.min(media + amount, mediaCap);
        if (!simulate) {
            media = nowMedia;
            this.markDirty();
            BlockState state = world.getBlockState(pos);
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }

        return nowMedia - prevMedia;
    }
    @Override
    public long withdrawMedia(long amount, boolean simulate) {
        long prevMedia = media;

        long nowMedia = Math.max(media - amount, 0);
        LOGGER.info("withdrawal request of " + amount + "! " + (prevMedia - nowMedia) + " left!");
        if (!simulate) {
            media = nowMedia;
            this.markDirty();
            BlockState state = world.getBlockState(pos);
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }

        return prevMedia - nowMedia;
    }
}
