package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;

import com.luxof.lapisworks.blocks.stuff.LinkableMediaBlock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockEntityAbstractImpetus.class, remap = false)
public abstract class BlockEntityAbstractImpetusMixin extends HexBlockEntity implements SidedInventory, LinkableMediaBlock {
    public BlockEntityAbstractImpetusMixin(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) { super(pType, pWorldPosition, pBlockState); }

    @Unique
    private static final long MAX_CAPACITY = 9_000_000_000_000_000_000L;
    @Unique
    private HashSet<BlockPos> linked = new HashSet<>();
    @Shadow
    protected long media;

    @Override
    public void addLink(BlockPos pos) {
        linked.add(pos);
    }

    @Override
    public void removeLink(BlockPos pos) {
        linked.remove(pos);
    }

    @Override
    public boolean isLinkedTo(BlockPos pos) {
        return linked.contains(pos);
    }

    @Override
    public Set<BlockPos> getLinks() {
        return linked;
    }

    @Override
    public int getNumberOfLinks() {
        return linked.size();
    }

    @Override
    public int getMaxNumberOfLinks() {
        return 1;
    }

    @Override
    public BlockPos getThisPos() {
        return getPos();
    }

    // I do this to make sure the impetus is un-interactable with by the network
    @Override
    public long depositMedia(long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long withdrawMedia(long amount, boolean simulate) {
        return 0;
    }
    
    @Override public long getMediaHere() {
        return media;
    }


    private List<Integer> posToInts(HashSet<BlockPos> posList) {
        return posList.stream().flatMap(
            pos -> Stream.of(pos.getX(), pos.getY(), pos.getZ())
        ).toList();
    }
    @Inject(
        method = "saveModData",
        at = @At("HEAD")
    )
    protected void saveModData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putIntArray(
            "links",
            posToInts(linked)
        );
    }

    private HashSet<BlockPos> intsToPos(int[] intArray) {
        HashSet<BlockPos> posList = new HashSet<>();
        int x = 0;
        int y = 0;
        int part = 0;
        for (int integer : intArray) {
            switch (part) {
                case 0 -> x = integer;
                case 1 -> y = integer;
                case 2 -> posList.add(new BlockPos(x, y, integer));
                default -> {}
            };
            part = (part + 1) % 3;
        }
        return posList;
    }
    @Inject(
        method = "loadModData",
        at = @At("HEAD")
    )
    protected void loadModData(NbtCompound nbt, CallbackInfo ci) {
        linked = intsToPos(nbt.getIntArray("links"));
    }
}
