package com.luxof.lapisworks.media;

import java.util.Set;

import net.minecraft.util.math.BlockPos;

/** A <code>LinkableMediaBlock</code> that cannot be linked to other blocks. */
public interface UnlinkableMediaBlock extends LinkableMediaBlock {
    default void addLink(BlockPos pos) {};
    default void removeLink(BlockPos pos) {};
    default boolean isLinkedTo(BlockPos pos) { return false; }
    default Set<BlockPos> getLinks() { return Set.of(); }
    default int getNumberOfLinks() { return 0; }
    default int getMaxNumberOfLinks() { return 0; }
    default long getMaxMedia() { return 9_000_000_000_000_000_000L; }
    default Set<BlockPos> getLinksNoRefresh() { return Set.of(); }
}
