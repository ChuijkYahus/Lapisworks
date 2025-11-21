package com.luxof.lapisworks.blocks.stuff;

import java.util.Set;

import net.minecraft.util.math.BlockPos;

public interface LinkableMediaBlock {
    public void addLink(BlockPos pos);
    public void removeLink(BlockPos pos);
    public boolean isLinkedTo(BlockPos pos);
    public Set<BlockPos> getLinks();
    public int getNumberOfLinks();
    default public int getMaxNumberOfLinks() { return 5; }
    public BlockPos getThisPos();
    /** returns the amount that was deposited. */
    public long depositMedia(long amount, boolean simulate);
    /** returns the amount that was withdrawn. */
    public long withdrawMedia(long amount, boolean simulate);

    // don't override these :pray:
    /** does not simulate. */
    default public long depositMedia(long amount) { return depositMedia(amount, false); }
    /** does not simulate. */
    default public long withdrawMedia(long amount) { return withdrawMedia(amount, false); }
}
