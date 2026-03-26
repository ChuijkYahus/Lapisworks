package com.luxof.lapisworks.blocks.stuff;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;

import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;
import com.luxof.lapisworks.mixinsupport.RitualsUtil;

import static com.luxof.lapisworks.Lapisworks.getFacingWithRespectToDown;

import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/** makes this block entity cast when deposited into by Deposit Media. */
public interface IChalkBE extends UnlinkableMediaBlock {
    /** happens on media deposited by the Deposit Media spell. */
    default public void startCast(long amount, PlayerBasedCastEnv ctx) {
        ServerPlayerEntity player = ctx.getCaster();

        ((RitualsUtil)getWorld()).addRitual(new OneTimeRitualExecutionState(
            getThisPos(),
            getFacingWithRespectToDown(player.getRotationVector(), getAttachedTo()),
            new CastingImage(),
            player.getUuid(),
            HexAPI.instance().getColorizer(player),
            amount,
            List.of()
        ));
    }
    public World getWorld();
    public Direction getAttachedTo();
    public BlockPos getPos();
    default public BlockPos getThisPos() { return getPos(); }
    default public long depositMedia(long amount, boolean simulate) { return amount; }
    default public long depositMediaViaSpell(long amount, boolean simulate) { return amount; }
    default public long withdrawMedia(long amount, boolean simulate) { return 0L; }
    default public long withdrawMediaViaSpell(long amount, boolean simulate) { return 0L; }
    default public void setMediaHere(long media) {}
    default public long getMediaHere() { return 0L; }
}
