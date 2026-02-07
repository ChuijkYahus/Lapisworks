package com.luxof.lapisworks.mixin;

import net.minecraft.entity.ai.brain.task.LoseJobOnSiteLossTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.luxof.lapisworks.init.ModEntities;

@Mixin(LoseJobOnSiteLossTask.class)
public class LoseJobOnSiteLossTaskMixin {
    // not even lambdas are safe from my murderous fucking talons!
    @Inject(
        method = "method_47038",
        at = @At("TAIL"),
        cancellable = true
    )
    private static void lapisworks$YouDontHaveToBeTheEmployedGuyWeCanStayHomeAndDoNothingAllDayTogether(
        ServerWorld world,
        VillagerEntity villager,
        long time,
        CallbackInfoReturnable<Boolean> cir
    ) {
        VillagerData villagerData = villager.getVillagerData();
        if (
            villagerData.getProfession() != VillagerProfession.NONE &&
            villagerData.getProfession() != VillagerProfession.NITWIT &&
            villagerData.getType() == ModEntities.JACK
        ) {
            villager.setVillagerData(villagerData.withProfession(VillagerProfession.NONE));
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
}
