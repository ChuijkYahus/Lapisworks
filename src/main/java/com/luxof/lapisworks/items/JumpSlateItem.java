package com.luxof.lapisworks.items;

import com.luxof.lapisworks.items.shit.FullyAmelInterface;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class JumpSlateItem extends BlockItem implements FullyAmelInterface {
    public JumpSlateItem(Block block, Settings settings) { super(block, settings); }
    @Override public int getRequiredAmelToMakeFromBase() { return 20; }
}
