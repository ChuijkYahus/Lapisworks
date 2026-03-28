package com.luxof.lapisworks.blocks.stuff;

import java.util.Set;

import com.luxof.lapisworks.media.MediaTransferInterface;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface LinkableMediaBlock extends MediaTransferInterface {
    public void addLink(BlockPos pos);
    public void removeLink(BlockPos pos);
    public boolean isLinkedTo(BlockPos pos);
    public Set<BlockPos> getLinks();
    public int getNumberOfLinks();
    default public int getMaxNumberOfLinks() { return 5; }
    public BlockPos getThisPos();
    default Vec3d getPosIfPossible() { return getThisPos().toCenterPos(); };
}
