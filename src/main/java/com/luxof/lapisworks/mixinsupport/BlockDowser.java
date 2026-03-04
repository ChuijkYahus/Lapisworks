package com.luxof.lapisworks.mixinsupport;

import com.mojang.datafixers.util.Pair;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public interface BlockDowser {
    /** returns whether or not it succeeded. */
    public boolean addTarget(Block block);
    /** returns whether or not it succeeded. */
    public boolean removeTarget(Block block);
    public List<Block> getTargets();
    /** the position and it's distance from the player (squared). can return null. */
    @Nullable public Pair<BlockPos, Double> dowse(Block block);
}
