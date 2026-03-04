package com.luxof.lapisworks.media;

import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public interface LinkableMediaBlock extends MediaTransferInterface {
    public void addLink(BlockPos pos);
    public void removeLink(BlockPos pos);
    public boolean isLinkedTo(BlockPos pos);
    public Set<BlockPos> getLinks();
    /** may return links to blocks which are no longer <code>LinkableMediaBlock</code>s.
     * used internally. */
    public Set<BlockPos> getLinksNoRefresh();
    public int getNumberOfLinks();

    /** removes links to blocks which are no longer <code>LinkableMediaBlock</code>s.
     * used after every links-related method except <code>getLinksNoRefresh</code> and
     * <code>removeLink</code> (to prevent infinite recursion). */
    default void removeDeadLinks(World world) {
        for (BlockPos block : getLinksNoRefresh()) {
            if (!(world.getBlockEntity(block) instanceof LinkableMediaBlock))
                removeLink(block);
        }
    }
    default public int getMaxNumberOfLinks() { return 5; }
    @Nullable default Vec3d getPosIfPossible() { return getThisPos().toCenterPos(); }
    public BlockPos getThisPos();
}
