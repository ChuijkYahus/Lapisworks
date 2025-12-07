package com.luxof.lapisworks.interop.hexical.blocks;

import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.magic.ItemMediaBattery;

import com.luxof.lapisworks.blocks.stuff.LinkableMediaBlock;
import com.luxof.lapisworks.interop.hexical.Lapixical;
import com.luxof.lapisworks.mixinsupport.GetServerStatus;
import com.luxof.lapisworks.mixinsupport.ItemEntityMinterface;

import static com.luxof.lapisworks.LapisworksIDs.IS_IN_CRADLE;

import java.util.Set;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CradleEntity extends BlockEntity implements Inventory, LinkableMediaBlock {
    private ItemStack heldStack = ItemStack.EMPTY.copy();
    public ItemEntity heldEntity = null;
    private UUID persistentUUID = UUID.randomUUID();

    public CradleEntity(BlockPos pos, BlockState state) {
        super(Lapixical.CRADLE_ENTITY_TYPE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity blockE) {
        CradleEntity bE = (CradleEntity)blockE;
		bE.updateItemEntity();
		bE.configureItemEntity();
    }

    public void updateItemEntity() {
        if (world.isClient) return;
        if (heldStack.isEmpty()) {
            if (heldEntity == null) return;
            persistentUUID = UUID.randomUUID();
            heldEntity.discard();
            heldEntity = null;
            markDirty();
            return;
        }

        ServerWorld sWorld = (ServerWorld)world;

        // just be over with please
        if (heldEntity == null || heldEntity.isRemoved()) {
            Vec3d pos = Vec3d.ofCenter(this.pos);
            heldEntity = new ItemEntity(sWorld, pos.x, pos.y, pos.z, heldStack);
            heldEntity.setUuid(persistentUUID);
            configureItemEntity();
            ((ItemEntityMinterface)heldEntity).setBlockPosOfCradle(this.pos);
            sWorld.spawnEntity(heldEntity);
        } else if (heldEntity.getStack() != heldStack) {
            persistentUUID = UUID.randomUUID();
            heldEntity.discard();
            heldEntity = null;
            Vec3d pos = Vec3d.ofCenter(this.pos);
            heldEntity = new ItemEntity(sWorld, pos.x, pos.y, pos.z, heldStack);
            heldEntity.setUuid(persistentUUID);
            configureItemEntity();
            ((ItemEntityMinterface)heldEntity).setBlockPosOfCradle(this.pos);
            sWorld.spawnEntity(heldEntity);
        }
        markDirty();
    }

    public void configureItemEntity() {
        if (heldEntity == null) return;
        Vec3d pos = this.pos.toCenterPos();
        heldEntity.setPosition(pos);
        heldEntity.noClip = true;
        heldEntity.setNeverDespawn();
        heldEntity.setNoGravity(true);
        heldEntity.setInvisible(false);
        heldEntity.setInvulnerable(true);
        heldEntity.setVelocity(Vec3d.ZERO);
        heldEntity.setPickupDelayInfinite();
        NBTHelper.putBoolean(heldEntity.getStack(), IS_IN_CRADLE, true);
        heldEntity.calculateDimensions();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        heldStack = ItemStack.fromNbt(nbt.getCompound("item"));
        this.persistentUUID = nbt.getUuid("persistent_uuid");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("item", heldStack.writeNbt(new NbtCompound()));
        nbt.putUuid("persistent_uuid", persistentUUID);

        if (world.isClient) return;
        if (heldEntity != null && ((GetServerStatus)world.getServer()).isShuttingDown()) {
            // the item entities always seem to become glitchy on world load
            // i reckon it's because i can't grab them on world load
            heldEntity.discard();
            heldEntity = null;
        }
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }

    @Override
	public void markDirty() {
		world.updateListeners(pos, this.getCachedState(), this.getCachedState(), 3);
		super.markDirty();
	}

    @Override
    public void clear() {
        heldStack = ItemStack.EMPTY.copy();
        updateItemEntity();
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) { return false; }

    @Override
    public ItemStack getStack(int slot) { return slot == 0 ? heldStack : ItemStack.EMPTY.copy(); }

    @Override
    public boolean isEmpty() { return heldStack.isEmpty(); }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot != 0) return ItemStack.EMPTY.copy();
        heldStack = ItemStack.EMPTY.copy();
        updateItemEntity();
        markDirty();
        return heldStack;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot != 0) return ItemStack.EMPTY.copy();
        ItemStack removed = heldStack.split(amount);
        updateItemEntity();
        markDirty();
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot != 0) return;
        heldStack = stack;
        updateItemEntity();
        markDirty();
    }

    @Override
    public int size() { return 1; }

    @Override public void addLink(BlockPos pos) {}
    @Override public void removeLink(BlockPos pos) {}
    @Override public boolean isLinkedTo(BlockPos pos) { return false; }
    @Override public Set<BlockPos> getLinks() { return Set.of(); }
    @Override public int getNumberOfLinks() { return 0; }
    @Override public int getMaxNumberOfLinks() { return 0; }
    @Override public BlockPos getThisPos() { return this.pos; }

    @Override
    public long depositMedia(long amount, boolean simulate) {
        if (!(heldStack.getItem() instanceof ItemMediaBattery phial)) return 0;
        phial.insertMedia(heldEntity.getStack(), amount, simulate);
        return phial.insertMedia(heldStack, amount, simulate);
    }

    @Override
    public long withdrawMedia(long amount, boolean simulate) {
        if (!(heldStack.getItem() instanceof ItemMediaBattery phial)) return 0;
        phial.withdrawMedia(heldEntity.getStack(), amount, simulate);
        return phial.withdrawMedia(heldStack, amount, simulate);
    }

    @Override
    public long getMediaHere() {
        if (!(heldStack.getItem() instanceof ItemMediaBattery phial)) return 0;
        return phial.getMedia(heldStack);
    }
}
