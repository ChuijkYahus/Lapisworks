package com.luxof.lapisworks.blocks.stuff;

import com.luxof.lapisworks.inv.BrewerInv;
import com.luxof.lapisworks.recipes.BrewingRec;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static net.minecraft.item.ItemStack.EMPTY;

public abstract class AbstractBrewerEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    // logic similar to og brewing stand. no guarantee tho
    public int fuel = 0;
    public int brewTime = 0;
    // go ahead, fuck with these variables if you really want (command blocks work too)
    public int maxFuel;
    public int maxBrewTime;
    protected List<BrewingRec> currentRecipes = new ArrayList<>();
    public BrewerInv inv = new BrewerInv(
        EMPTY.copy(), EMPTY.copy(), List.of(EMPTY.copy(), EMPTY.copy(), EMPTY.copy())
    );

    public AbstractBrewerEntity(
        BlockEntityType<?> type,
        BlockPos pos,
        BlockState state,
        int maxFuel,
        int maxBrewTime
    ) {
        super(type, pos, state);
        this.maxFuel = maxFuel;
        this.maxBrewTime = maxBrewTime;
    }
    public AbstractBrewerEntity(
        BlockEntityType<?> type,
        BlockPos pos,
        BlockState state,
        int maxFuel,
        int maxBrewTime,
        BrewerInv inv
    ) {
        this(type, pos, state, maxFuel, maxBrewTime);
        this.inv = inv;
    }

    protected void attemptRefuel() {
        if (currentRecipes.size() <= 0) return;
        if (fuel <= 0) {
            LOGGER.info("taking blaze!");
            if (inv.blaze.isEmpty() || inv.blaze.getCount() <= 0) return;
            inv.blaze.decrement(0);
            LOGGER.info("took blaze!");
            fuel = maxFuel;
            LOGGER.info("fuel now: " + fuel);
            markDirty();
        }
        if (brewTime <= 0 && fuel > 0) {
            LOGGER.info("refilling brew!");
            fuel -= 1;
            brewTime = maxBrewTime;
            markDirty();
        }
    }

    protected List<BrewingRec> updateRecipes(BrewerInv inv) {
        if (brewTime == 0 && fuel == 0) return List.of();

        return new ArrayList<>(
            world.getRecipeManager().getAllMatches(BrewingRec.Type.INSTANCE, inv, world)
        );
    }

    protected void craft(BrewingRec recipe, BrewerInv inv) {
        List<ItemStack> crafted = recipe.craft(inv);
        inv.brewingInto = new ArrayList<>(crafted.subList(0, 3));
        inv.input = crafted.get(6);

        for (ItemStack stack : crafted.subList(4, 7)) {
            if (stack.isEmpty()) continue;
            ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            world.spawnEntity(item);
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        attemptRefuel();
        currentRecipes = updateRecipes(inv);
        if (currentRecipes.size() == 0) return;
        LOGGER.info("got recipes! " + currentRecipes.toString());

        if (brewTime > 0) brewTime -= 1;
        else {
            if (fuel > 0) {
                fuel -= 1;
                brewTime = maxBrewTime;
            }
            for (BrewingRec recipe : currentRecipes) {
                craft(recipe, inv);
            }
            currentRecipes.clear();
        }
        markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        inv.writeNbt(nbt);
        nbt.putInt("fuel", fuel);
        nbt.putInt("brewTime", brewTime);
        nbt.putInt("maxFuel", maxFuel);
        nbt.putInt("maxBrewTime", maxBrewTime);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inv.readNbt(nbt);
        fuel = nbt.getInt("fuel");
        brewTime = nbt.getInt("brewTime");
        maxFuel = nbt.getInt("maxFuel");
        nbt.getInt("maxBrewTime");
    }

    @Override @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override public void clear() { inv.clear(); }
    @Override public boolean canPlayerUse(PlayerEntity player) { return inv.canPlayerUse(player); }
    @Override public ItemStack getStack(int slot) { return inv.getStack(slot); }
    @Override public boolean isEmpty() { return inv.isEmpty(); }
    @Override public ItemStack removeStack(int slot) { return inv.removeStack(slot); }
    @Override public ItemStack removeStack(int slot, int amount) { return inv.removeStack(slot, amount); }
    @Override public void setStack(int slot, ItemStack stack) { inv.setStack(slot, stack); }
    @Override public int size() { return inv.size(); }
}
