package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.blocks.stuff.LinkableMediaBlock;

import java.util.Set;

import miyucomics.hexical.features.media_jar.MediaJarBlock;
import miyucomics.hexical.features.media_jar.MediaJarBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = MediaJarBlockEntity.class, remap = false)
public abstract class MediaJarBlockEntityMixin extends BlockEntity implements LinkableMediaBlock {
    public MediaJarBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow abstract long getMedia();
    //@Shadow abstract long getMaxMedia(); // ??? why not it's right there
    @Unique private long getMaxMedia() { return MediaJarBlock.MAX_CAPACITY; }
    @Shadow abstract void setMedia(long media);

    @Override @Unique public void addLink(BlockPos pos) {}
    @Override @Unique public void removeLink(BlockPos pos) {}
    @Override @Unique public boolean isLinkedTo(BlockPos pos) { return false; }
    @Override @Unique public Set<BlockPos> getLinks() { return Set.of(); }
    @Override @Unique public int getNumberOfLinks() { return 0; }
    @Override @Unique public int getMaxNumberOfLinks() { return 0; }
    @Override @Unique public BlockPos getThisPos() { return this.pos; }
    @Override @Unique public long depositMedia(long amount, boolean simulate) {
        long prevMedia = getMedia();
        long idealMedia = prevMedia + amount;
        long nowMedia = Math.min(idealMedia, getMaxMedia());

        if (!simulate) setMedia(idealMedia);

        return nowMedia - prevMedia;
    }
    @Override @Unique public long withdrawMedia(long amount, boolean simulate) {
        long prevMedia = getMedia();
        long idealMedia = prevMedia - amount;
        long nowMedia = Math.max(idealMedia, 0);

        if (!simulate) setMedia(idealMedia);

        return prevMedia - nowMedia;
    }
    @Override @Unique public long getMediaHere() { return getMedia(); }
}
