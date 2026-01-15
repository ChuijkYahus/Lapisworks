package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.blocks.stuff.UnlinkableMediaBlock;

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
public abstract class MediaJarBlockEntityMixin extends BlockEntity implements UnlinkableMediaBlock {
    public MediaJarBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow abstract long getMedia();

    @Override @Unique public BlockPos getThisPos() { return this.pos; }
    @Shadow abstract public void setMedia(long media);
    //@Shadow abstract long getMaxMedia(); // oh it's static
    @Override @Unique public long getMaxMedia() { return MediaJarBlock.MAX_CAPACITY; }
    @Override @Unique public long getMediaHere() { return getMedia(); }
}
