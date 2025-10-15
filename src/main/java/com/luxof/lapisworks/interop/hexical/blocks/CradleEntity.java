package com.luxof.lapisworks.interop.hexical.blocks;

import com.luxof.lapisworks.interop.hexical.LargeItemEntity;
import com.luxof.lapisworks.interop.hexical.Lapixical;

import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CradleEntity extends BlockEntity implements Inventory {
    private ItemStack heldStack = ItemStack.EMPTY.copy();
    public LargeItemEntity heldEntity = null;
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
            heldEntity = new LargeItemEntity(sWorld, pos.x, pos.y, pos.z, heldStack);
            heldEntity.setUuid(persistentUUID);
            configureItemEntity();
            sWorld.spawnEntity(heldEntity);
        } else if (heldEntity.getStack() != heldStack) {
            persistentUUID = UUID.randomUUID();
            heldEntity.discard();
            heldEntity = null;
            Vec3d pos = Vec3d.ofCenter(this.pos);
            heldEntity = new LargeItemEntity(sWorld, pos.x, pos.y, pos.z, heldStack);
            heldEntity.setUuid(persistentUUID);
            configureItemEntity();
            sWorld.spawnEntity(heldEntity);
        }
        markDirty();
    }

    public void configureItemEntity() {
        if (heldEntity == null) return;
        Vec3d pos = this.pos.toCenterPos();
        heldEntity.setPosition(pos);
        heldEntity.setBoundingBox(new Box( // why the fuck doesn't this work?
            pos.x - 1, pos.y - 1, pos.z - 1,
            pos.x + 1, pos.y + 1, pos.z + 1
        ));
        heldEntity.noClip = true;
        heldEntity.setNeverDespawn();
        heldEntity.setNoGravity(true);
        heldEntity.setInvisible(false);
        heldEntity.setInvulnerable(true);
        heldEntity.setVelocity(Vec3d.ZERO);
        heldEntity.setPickupDelayInfinite();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        heldStack = ItemStack.fromNbt(nbt.getCompound("item"));
        if (nbt.containsUuid("persistent_uuid")) persistentUUID = nbt.getUuid("persistent_uuid");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("item", heldStack.writeNbt(new NbtCompound()));
        if (persistentUUID != null) {
            nbt.putUuid("persistent_uuid", persistentUUID);
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
}
