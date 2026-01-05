package com.luxof.lapisworks.blocks.entities;

import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;

import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.mixinsupport.ControlCircleTickSpeed;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class SimpleImpetusEntity extends BlockEntityAbstractImpetus {
    private HexPattern pattern = null;
    // because bookkeeper's - exists
    private boolean tuned = false;
    private UUID plr = null;

    public static final String TAG_TUNED_PAT = "tuned_pattern";
    public static final String TAG_IS_TUNED = "tuned";
    public static final String TAG_PLAYER = "player";

    public SimpleImpetusEntity(
        BlockPos pWorldPosition, BlockState pBlockState
    ) { super(ModBlocks.SIMPLE_IMPETUS_ENTITY_TYPE, pWorldPosition, pBlockState); }

    @Override
    public void startExecution(@Nullable ServerPlayerEntity sp) {
        super.startExecution(getPlayer());

        // check if we're actually going to cast
        if (this.executionState == null) return;

        ((ControlCircleTickSpeed)this.executionState).setForcedTPT(4);
        CastingImage img = this.executionState.currentImage;
        this.executionState.currentImage.copy(
            List.of(new EntityIota(sp)),
            img.getParenCount(),
            img.getParenthesized(),
            img.getEscapeNext(),
            img.getOpsConsumed(),
            img.getUserData()
        );
    }

    @Override
    protected void saveModData(NbtCompound nbt) {
        super.saveModData(nbt);
        if (pattern != null)
            nbt.put(TAG_TUNED_PAT, pattern.serializeToNBT());
        nbt.putBoolean(TAG_IS_TUNED, tuned);
        if (plr != null) {
            nbt.putUuid(TAG_PLAYER, plr);
        } else {
            LOGGER.warn("Player was null in a Simple Impetus. Don't do that!");
        }
    }

    @Override
    protected void loadModData(NbtCompound nbt) {
        super.loadModData(nbt);
        if (nbt.contains(TAG_TUNED_PAT))
            pattern = HexPattern.fromNBT(nbt.getCompound(TAG_TUNED_PAT));
        tuned = nbt.getBoolean(TAG_IS_TUNED);
        if (nbt.contains(TAG_PLAYER)) {
            plr = nbt.getUuid(TAG_PLAYER);
        } else {
            LOGGER.warn("Player was null in a Simple Impetus. Don't do that!");
        }
    }

    /** returns whether or not the simple impetus was tuned to the pattern in question.
     * This return value is useful for making certain patterns do no-ops when they were tuned.
     * <p><code>isValidPat</code> makes sure an untuned Simple Impetus doesn't go off on an invalid
     * pattern. */
    public boolean tryTrigger(String pat, boolean isValidPat, @Nullable ServerPlayerEntity sp) {
        boolean signatureMatches = pattern.anglesSignature().equals(pat);

        if ((!tuned && isValidPat) || signatureMatches) startExecution(sp);
        return tuned && signatureMatches;
    }

    public void tune(HexPattern pattern, boolean tuneOrNot) {
        // looks explainable in the game data when i clear angSig too.
        tuned = tuneOrNot;
        pattern = tuneOrNot ? pattern : null;
    }

    public String getTuned() { return pattern.anglesSignature(); }
    public HexPattern getTunedPattern() { return pattern; }
    public boolean getIsTuned() { return tuned; }

    @Nullable
    public ServerPlayerEntity getPlayer() {
        if (plr == null) return null;
        return (ServerPlayerEntity)this.world.getPlayerByUuid(plr);
    }

    public void setPlayer(ServerPlayerEntity sp) { plr = sp.getUuid(); }
}
