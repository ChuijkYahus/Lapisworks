package com.luxof.lapisworks.media;

import static com.luxof.lapisworks.Lapisworks.interactWithLinkableMediaBlocks;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

/** implement in a block entity. */
public interface LinkableMediaBlock extends MediaTransferInterface {
    public World getWorld();

    public void addLink(BlockPos pos);
    public void removeLink(BlockPos pos);
    public boolean isLinkedTo(BlockPos pos);
    public Set<BlockPos> getLinks();
    /** may return links to blocks which are no longer <code>LinkableMediaBlock</code>s.
     * used internally. */
    public Set<BlockPos> getLinksNoRefresh();
    public int getNumberOfLinks();

    /** removes links to blocks which are no longer <code>LinkableMediaBlock</code>s.
     * you should use this in every links-related method except <code>getLinksNoRefresh</code> and
     * <code>removeLink</code> (to prevent infinite recursion). */
    default void removeDeadLinks() {
        World world = getWorld();
        for (BlockPos block : getLinksNoRefresh()) {
            if (!(world.getBlockEntity(block) instanceof LinkableMediaBlock))
                removeLink(block);
        }
    }
    default public int getMaxNumberOfLinks() { return 5; }
    @Nullable default Vec3d getPosIfPossible() { return getThisPos().toCenterPos(); }
    public BlockPos getThisPos();

    public long getMediaHereSingular();
    default long getMediaHere() {
        long total = 0L;
        Stack<BlockPos> todo = new Stack<>();
        HashSet<BlockPos> seen = new HashSet<>();

        todo.add(getThisPos());
        seen.add(getThisPos());

        while (!todo.isEmpty()) {
            BlockPos currPos = todo.pop();
            LinkableMediaBlock curr = (LinkableMediaBlock)getWorld().getBlockEntity(currPos);
            total += curr.getMediaHereSingular();
            curr.getLinks().forEach(pos -> { if (seen.add(pos)) todo.add(pos); });
        }

        return total;
    }

    public long getMaxMediaSingular();
    default long getMaxMedia() {
        long total = 0L;
        Stack<BlockPos> todo = new Stack<>();
        HashSet<BlockPos> seen = new HashSet<>();

        todo.add(getThisPos());
        seen.add(getThisPos());

        while (!todo.isEmpty()) {
            BlockPos currPos = todo.pop();
            LinkableMediaBlock curr = (LinkableMediaBlock)getWorld().getBlockEntity(currPos);
            total += curr.getMaxMediaSingular();
            curr.getLinks().forEach(pos -> { if (seen.add(pos)) todo.add(pos); });
        }

        return total;
    }

    default public long depositMediaSingular(long amount, boolean simulate) {
        return MediaTransferInterface.super.depositMedia(amount, simulate);
    }
    default long depositMedia(long amount, boolean simulate) {
        return interactWithLinkableMediaBlocks(
            getWorld(),
            Set.of(getThisPos()),
            amount,
            true,
            simulate
        ).getLeft();
    }

    default public long withdrawMediaSingular(long amount, boolean simulate) {
        return MediaTransferInterface.super.withdrawMedia(amount, simulate);
    }
    default long withdrawMedia(long amount, boolean simulate) {
        return interactWithLinkableMediaBlocks(
            getWorld(),
            Set.of(getThisPos()),
            amount,
            false,
            simulate
        ).getLeft();
    }
}
