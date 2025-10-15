package com.luxof.lapisworks.VAULT;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;

import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.LapisworksIDs.CASTENV_VAULT;

import java.util.function.Predicate;

import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CastEnvVAULT extends VAULT {
    private final CastingEnvironment castEnv;
    private boolean inited = false;
    private VAULT playerVAULT = null;
    protected CastEnvVAULT(CastingEnvironment castEnv) {
        this.castEnv = castEnv;
        /*
        // cannot init here, execState is null.
        if (castEnv.getCastingEntity() != null &&
            castEnv.getCastingEntity() instanceof ServerPlayerEntity plr) {
            playerVAULT = ((GetVAULT)plr).grabVAULT();
        } else {
            playerVAULT = null;
        }*/
    }
    @Override public Identifier getKindOfVault() { return CASTENV_VAULT; }

    // guaranteed to run only when the castenv is ready
    public void initInnerServPlayerVAULT() {
        if (inited) return;
        inited = true;
        playerVAULT = castEnv.getCastingEntity() instanceof ServerPlayerEntity plr ?
            ((GetVAULT)plr).grabVAULT() : null;
    }

    @Override
    public int fetch(Predicate<Item> item, Flags flags) {
        if (!flags.canSearch(Flags.HANDS)) return 0;
        int runningSum = 0;
        Flags currFlags = flags.getKindsOfFlag(Flags.HANDS);
        for (HeldItemInfo heldInfo : ((GetStacks)this.castEnv).getHeldStacks()) {
            if (currFlags.canWorkIn(Flags.INVITEM, Flags.HANDS)) {
                runningSum += this.handleFetchInvItem(heldInfo.stack(), item, currFlags);
            }
            if (currFlags.canWorkIn(Flags.STACKS, Flags.HANDS)) {
                runningSum += this.handleFetchStack(heldInfo.stack(), item, currFlags);
            }
        }
        if (playerVAULT != null) {
            runningSum += playerVAULT.fetch(item, flags);
        }
        return runningSum;
    }


    @Override
    public int drain(Predicate<Item> item, int amount, Flags flags) {
        if (!flags.canSearch(Flags.HANDS)) return amount;
        int left = amount;
        Flags currFlags = flags.getKindsOfFlag(Flags.HANDS);
        for (HeldItemInfo heldInfo : ((GetStacks)this.castEnv).getHeldStacks()) {
            if (currFlags.canWorkIn(Flags.INVITEM, Flags.HANDS)) {
                left = this.handleDrainInvItem(heldInfo.stack(), item, amount, currFlags);
            }
            if (currFlags.canWorkIn(Flags.STACKS, Flags.HANDS)) {
                left = this.handleDrainStack(heldInfo.stack(), item, amount, heldInfo.hand(), (hand, stack) -> {
                    this.castEnv.replaceItem(whatever -> true, stack, hand);
                });
            }
            if (left == 0) return 0;
            else if (left < 0) {
                LOGGER.error("CRITICAL ERROR: VAULT DRAINING MADE \"left\" < 0!!");
                return 0;
            }
        }
        if (playerVAULT != null) {
            left = playerVAULT.drain(item, amount, flags);
        }
        return left;
    }


    @Override
    public int give(Predicate<Item> item, int amount, Flags flags) {
        if (!flags.canSearch(Flags.HANDS)) return amount;
        int left = amount;
        Flags currFlags = flags.getKindsOfFlag(Flags.HANDS);
        for (HeldItemInfo heldInfo : ((GetStacks)this.castEnv).getHeldStacks()) {
            if (currFlags.canWorkIn(Flags.INVITEM, Flags.HANDS)) {
                left = this.handleGiveInvItem(heldInfo.stack(), item, amount, currFlags);
            }
            if (currFlags.canWorkIn(Flags.STACKS, Flags.HANDS)) {
                left = this.handleGiveStack(heldInfo.stack(), item, amount, heldInfo.hand(), (hand, stack) -> {
                    this.castEnv.replaceItem(whatever -> true, stack, hand);
                });
            }
            if (left == 0) return 0;
            else if (left < 0) {
                LOGGER.error("CRITICAL ERROR: VAULT GIVING MADE \"left\" < 0!!");
                return 0;
            }
        }
        if (playerVAULT != null) {
            left = playerVAULT.give(item, amount, flags);
        }
        return left;
    }
    
}
