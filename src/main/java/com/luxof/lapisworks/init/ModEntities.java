package com.luxof.lapisworks.init;

import com.google.common.collect.ImmutableSet;

import static com.luxof.lapisworks.Lapisworks.id;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import org.jetbrains.annotations.Nullable;

public class ModEntities {
    // "I'm gonna go fight MC codebase"
    // "Damn MC codebase got hands"
    // made by imbuing a simple mind into a flayed villager
    public static VillagerProfession JACK_PROFESSION;

    /** :clueless: */
    public static void doSomethingFun() {
        JACK_PROFESSION = registerProf(
            "jack",
            any -> true, // stick around, until ye find another job.
            VillagerProfession.IS_ACQUIRABLE_JOB_SITE,
            (SoundEvent)null
        );
    }

    /*private static VillagerProfession registerProf(
        String id,
        RegistryEntry<PointOfInterestType> workStation,
        @Nullable SoundEvent workSound
    ) {
        return registerProf(
            id,
            any -> true,
            any -> true,
            workSound
        );
    }*/

    /** Yeah, I dunno what <code>acquirableWorkStation</code> means either. */
    private static VillagerProfession registerProf(
        String id,
        Predicate<RegistryEntry<PointOfInterestType>> workStation,
        Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkStation,
        @Nullable SoundEvent workSound
    ) {
        return registerProf(
            id,
            workStation,
            acquirableWorkStation,
            ImmutableSet.of(),
            ImmutableSet.of(),
            workSound
        );
    }

    private static VillagerProfession registerProf(
        String ID,
        Predicate<RegistryEntry<PointOfInterestType>> workStation,
        Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkStation,
        ImmutableSet<Item> gatherableItems,
        ImmutableSet<Block> secondaryJobSites,
        @Nullable SoundEvent workSound
    ) {
        return Registry.register(
            Registries.VILLAGER_PROFESSION,
            id(ID),
            new VillagerProfession(
                ID,
                workStation,
                acquirableWorkStation,
                gatherableItems,
                secondaryJobSites,
                workSound
            )
        );
    }
}
