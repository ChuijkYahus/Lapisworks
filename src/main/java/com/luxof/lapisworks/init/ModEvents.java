package com.luxof.lapisworks.init;

import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import dev.onyxstudios.cca.api.v3.entity.PlayerCopyCallback;

public class ModEvents {
    public static void finallyBeUsed() {
        PlayerCopyCallback.EVENT.register((op, np, alive) -> {
            if (!alive) ((LapisworksInterface)np).copyCrossDeath(op);
            else ((LapisworksInterface)np).copyCrossDimensional(op);
        });

        // i'll use events for it.. eventually...
        /*
        AttackEntityCallback.EVENT.register((plr, world, hand, entity, hitRes) -> {
            if (hitRes.getType() == HitResult.Type.MISS) return ActionResult.PASS;
        });
        */
    }
}
