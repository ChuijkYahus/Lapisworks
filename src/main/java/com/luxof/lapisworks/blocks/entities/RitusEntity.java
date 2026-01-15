package com.luxof.lapisworks.blocks.entities;

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;

import com.luxof.lapisworks.blocks.Ritus;
import com.luxof.lapisworks.blocks.stuff.AttachedBE;
import com.luxof.lapisworks.blocks.stuff.UnlinkableMediaBlock;
import com.luxof.lapisworks.chalk.MultiUseRitualExecutionState;
import com.luxof.lapisworks.chalk.RitualCastEnv;
import com.luxof.lapisworks.chalk.RitualComponent;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.Lapisworks.nbtListOf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.jetbrains.annotations.Nullable;

public class RitusEntity extends BlockEntity implements AttachedBE, RitualComponent, UnlinkableMediaBlock {

    public RitusEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.RITUS_ENTITY_TYPE, pos, state);
    }

    public long media = 0L;
    @Nullable private NbtCompound tunedFrequency = null;
    private ArrayList<MultiUseRitualExecutionState> rituals = new ArrayList<>();
    @Nullable private ItemStack displayItem = null;
    @Nullable private Text displayMessage = null;

    @Nullable public Iota getTunedFrequency(ServerWorld world) {
        if (tunedFrequency == null) return null;
        return IotaType.deserialize(tunedFrequency, world);
    }
    @Nullable public Text getTunedFrequencyDisplay() {
        if (tunedFrequency == null) return null;
        return IotaType.getDisplay(tunedFrequency);
    }
    public void setTunedFrequency(Iota iota) {
        tunedFrequency = IotaType.serialize(iota);
    }

    /** returns success. */
    public boolean addRitual(MultiUseRitualExecutionState ritual) {
        // in the future i may wanna add multiple ritual capacity
        if (this.rituals.size() >= 1) return false;
        this.rituals.add(ritual);
        save();
        return true;
    }

    @Nullable
    public Pair<ItemStack, Text> getDisplay() {
        if (displayItem != null && displayMessage != null) {
            return new Pair<>(displayItem, displayMessage);
        } else {
            return null;
        }
    }

    public void postDisplay(ItemStack item, Text message) {
        this.displayItem = item;
        this.displayMessage = message;
        save();
    }

    public void postMishap(Text mishapDisplay) {
        this.postDisplay(new ItemStack(Items.MUSIC_DISC_11), mishapDisplay);
    }

    public void postPrint(Text message) {
        this.postDisplay(new ItemStack(Items.BOOK), message);
    }

    public void save() {
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("media", media);
        if (displayItem != null)
            nbt.put("displayItem", displayItem.writeNbt(new NbtCompound()));
        if (displayMessage != null)
            nbt.putString("displayMessage", Text.Serializer.toJson(displayMessage));
        if (tunedFrequency != null)
            nbt.put("tunedFrequency", tunedFrequency);
        
        nbt.put(
            "rituals",
            nbtListOf(rituals.stream().map(ritual -> ritual.save()).toList())
        );
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        media = nbt.getLong("media");
        if (nbt.contains("displayItem"))
            displayItem = ItemStack.fromNbt(nbt.getCompound("displayItem"));
        else
            displayItem = null;
        if (nbt.contains("displayMessage"))
            displayMessage = Text.Serializer.fromJson(nbt.getString("displayMessage"));
        else
            displayMessage = null;
        if (nbt.contains("tunedFrequency"))
            tunedFrequency = nbt.getCompound("tunedFrequency");
        else
            tunedFrequency = null;

        if (world.isClient) return;
        nbt.getList("rituals", NbtElement.COMPOUND_TYPE).stream()
            .map(
                (NbtElement ritualNbt) -> MultiUseRitualExecutionState.load(
                    (NbtCompound)ritualNbt,
                    (ServerWorld)world
                )
            )
            .toList();
    }

    @Override @Nullable public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt() { return createNbt(); }

    @Override
    public Direction getAttachedTo() {
        return world.getBlockState(pos).get(Ritus.ATTACHED);
    }

    @Override
    public List<BlockPos> getPossibleNextBlocks(ServerWorld world, @Nullable Direction forward) {
        return getPossibleNextBlocksGeneric(world, forward, pos);
    }

    @Override
    public @Nullable Pair<BlockPos, CastingImage> execute(RitualCastEnv env) {
        world.setBlockState(
            pos,
            world.getBlockState(pos).with(Ritus.POWERED, true)
        );

        return new Pair<>(
            getNextBlockDuringExecutionHelper(env),
            env.ritual().currentImage
        );
    }

    @Override
    public void unpower() {
        world.setBlockState(
            pos,
            world.getBlockState(pos).with(Ritus.POWERED, false)
        );
    }

    @Override
    public boolean executionCanFlowTo(ServerWorld world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof AttachedBE attached)) return true;
        return attached.getAttachedTo() == getAttachedTo();
    }


    @Override public BlockPos getThisPos() { return pos; }
    @Override public void setMedia(long media) { this.media = media; save(); }
    @Override public long getMediaHere() { return media; }
}
