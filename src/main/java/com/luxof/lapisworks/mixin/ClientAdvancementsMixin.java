package com.luxof.lapisworks.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Mixin;

import vazkii.patchouli.client.base.ClientAdvancements;

@Mixin(value = ClientAdvancements.class, remap = false)
public class ClientAdvancementsMixin {
    @WrapMethod(method = {"hasDone"})
    private static boolean hasDone(String advancements, Operation<Boolean> og) {
        if (advancements.startsWith("!")) return !og.call(advancements.substring(1));
        else if (!advancements.startsWith("&") && !advancements.startsWith("|"))
            return og.call(advancements);

        String[] advs = advancements.substring(1).split(Pattern.quote(","));
        LOGGER.info("advs: " + advs.toString());
        boolean andOp = advancements.startsWith("&") ? true : false;
        boolean ret = andOp;
        for (String adv : advs) {
            boolean curr;
            if (adv.startsWith("!")) curr = !og.call(adv.substring(1));
            else curr = og.call(adv);
            ret = andOp ? ret && curr : ret || curr;
        }

        return ret;
    }
}
