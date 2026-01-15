package com.luxof.lapisworks.blocks.stuff;

import java.util.Set;

import net.minecraft.util.math.BlockPos;

/** A <code>LinkableMediaBlock</code> that cannot be linked to other blocks. */
public interface UnlinkableMediaBlock extends LinkableMediaBlock {
    @Override default public void addLink(BlockPos pos) {};
    @Override default public void removeLink(BlockPos pos) {};
    @Override default boolean isLinkedTo(BlockPos pos) { return false; }
    @Override default public Set<BlockPos> getLinks() { return Set.of(); }
    @Override default public int getNumberOfLinks() { return 0; }
    @Override default public int getMaxNumberOfLinks() { return 0; }
    @Override default public long getMaxMedia() { return 9_000_000_000_000_000_000L; }
}
