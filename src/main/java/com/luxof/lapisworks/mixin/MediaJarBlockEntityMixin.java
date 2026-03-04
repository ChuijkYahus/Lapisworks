package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.media.MediaTransferInterface;

import kotlin.jvm.internal.Intrinsics;

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
public abstract class MediaJarBlockEntityMixin extends BlockEntity implements MediaTransferInterface {
    public MediaJarBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow private long media;
    @Shadow public abstract long getMedia();

    @Override @Unique public void setMedia(long media) {
        this.media = Math.max(Math.min(media, 6400000L), 0L);
        this.markDirty();
        if (!world.isClient) {
            world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }
    @Override @Unique public long getMaxMedia() { return MediaJarBlock.MAX_CAPACITY; }
    @Override @Unique public long getMediaHere() { return getMedia(); }
}
