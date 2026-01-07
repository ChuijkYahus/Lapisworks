package com.luxof.lapisworks.persistentstate;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class PersistentStateRituals extends PersistentState {
    public HashMap<String, ArrayList<OneTimeRitualExecutionState>> rituals = new HashMap<>();
    public HashMap<String, HashMap<Iota, ArrayList<BlockPos>>> tuneables = new HashMap<>();

    public ArrayList<OneTimeRitualExecutionState> getRituals(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();

        if (!rituals.containsKey(key)) rituals.put(key, new ArrayList<>());
        return rituals.get(key);
    }


    public HashMap<Iota, ArrayList<BlockPos>> getTuneables(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();

        if (!tuneables.containsKey(key)) tuneables.put(key, new HashMap<>());
        return tuneables.get(key);
    }


    public ArrayList<BlockPos> getTuneables(ServerWorld world, Iota key) {
        var map = getTuneables(world);

        if (!map.containsKey(key)) map.put(key, new ArrayList<>());
        return map.get(key);
    }


    private NbtList nbtListOf(List<? extends NbtElement> list) {
        NbtList nbtList = new NbtList();
        nbtList.addAll(list);
        return nbtList;
    }
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        // HashMap<String, ArrayList<OneTimeRitualExecutionState>>
        NbtCompound ritualsNbt = new NbtCompound();
        for (String world : rituals.keySet()) {
            ritualsNbt.put(
                world,
                nbtListOf(rituals.get(world).stream().map(ritual -> ritual.save()).toList())
            );
        }

        // HashMap<String, HashMap<Iota, ArrayList<BlockPos>>>
        NbtCompound tuneablesNbt = new NbtCompound();
        for (String world : tuneables.keySet()) {
            tuneablesNbt.put(
                world,
                nbtListOf(
                    tuneables.get(world).entrySet().stream().map(
                        entry -> {
                            NbtCompound iota = IotaType.serialize(entry.getKey());
                            NbtList poses = nbtListOf(
                                entry.getValue().stream().map(Lapisworks::serializeBlockPos).toList()
                            );
                            NbtCompound entryNbt = new NbtCompound();

                            entryNbt.put("iota", iota);
                            entryNbt.put("poses", poses);
                            return entryNbt;
                        }
                    ).toList()
                )
            );
        }

        nbt.put("rituals", ritualsNbt);
        nbt.put("tuneables", tuneablesNbt);

        return nbt;
    }

    public static PersistentStateRituals readNbt(NbtCompound nbt, ServerWorld world) {
        PersistentStateRituals state = new PersistentStateRituals();

        // HashMap<String, ArrayList<OneTimeRitualExecutionState>>
        NbtCompound ritualsNbt = nbt.getCompound("rituals");
        for (String worldKey : ritualsNbt.getKeys()) {
            state.rituals.put(
                worldKey,
                new ArrayList<>(
                    ritualsNbt.getList(worldKey, NbtElement.COMPOUND_TYPE).stream().map(
                        ritual -> OneTimeRitualExecutionState.load((NbtCompound)ritual, world)
                    ).toList()
                )
            );
        }

        // HashMap<String, HashMap<Iota, ArrayList<BlockPos>>>
        NbtCompound tuneablesNbt = nbt.getCompound("tuneables");
        for (String worldKey : tuneablesNbt.getKeys()) {
            state.tuneables.put(
                worldKey,
                new HashMap<>(
                    tuneablesNbt.getList(worldKey, NbtElement.COMPOUND_TYPE).stream()
                    .collect(
                        Collectors.toMap(
                            entry -> IotaType.deserialize(
                                ((NbtCompound)entry).getCompound("iota"),
                                world
                            ),
                            entry -> new ArrayList<>(
                                ((NbtCompound)entry).getList("poses", NbtElement.COMPOUND_TYPE)
                                .stream().map(Lapisworks::deserializeBlockPos).toList()
                            )
                        )
                    )
                )
            );
        }

        return state;
    }

    /** the reason this takes a world is so that rituals in the nether aren't ticked for
     * the overworld. */
    public static PersistentStateRituals getState(ServerWorld world) {
        PersistentStateManager psm = world.getPersistentStateManager();

        PersistentStateRituals state = psm.getOrCreate(
            nbt -> PersistentStateRituals.readNbt(nbt, world),
            PersistentStateRituals::new,
            "lapisworks_rituals"
        );

        return state;
    }
}
