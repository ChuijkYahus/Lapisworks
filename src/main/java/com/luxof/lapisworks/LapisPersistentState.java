package com.luxof.lapisworks;

import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.server.world.ServerWorld;

public class LapisPersistentState extends PersistentState {
    public HashMap<String, ArrayList<OneTimeRitualExecutionState>> rituals = new HashMap<>();

    public ArrayList<OneTimeRitualExecutionState> getRituals(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();

        if (!rituals.containsKey(key)) rituals.put(key, new ArrayList<>());
        return rituals.get(key);
    }

    private NbtList nbtListOf(List<? extends NbtElement> list) {
        NbtList nbtList = new NbtList();
        nbtList.addAll(list);
        return nbtList;
    }
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        for (String world : rituals.keySet()) {
            nbt.put(
                world,
                nbtListOf(rituals.get(world).stream().map(ritual -> ritual.save()).toList())
            );
        }

        return nbt;
    }
    
    public static LapisPersistentState readNbt(NbtCompound nbt, ServerWorld world) {
        LOGGER.info("Reading from nbt!");
        LapisPersistentState state = new LapisPersistentState();

        for (String str : nbt.getKeys()) {
            state.rituals.put(
                str,
                new ArrayList<>(
                    nbt.getList(str, NbtElement.COMPOUND_TYPE).stream().map(
                        ritual -> OneTimeRitualExecutionState.load((NbtCompound)ritual, world)
                    ).toList()
                )
            );
        }

        LOGGER.info("How many rituals?!");
        for (Entry<String, ArrayList<OneTimeRitualExecutionState>> entry : state.rituals.entrySet()) {
            LOGGER.info(entry.getKey() + ": " + String.valueOf(entry.getValue().size()));
        }

        return state;
    }

    /** the reason this takes a world is so that rituals in the nether aren't ticked for
     * the overworld. */
    public static LapisPersistentState getState(ServerWorld world) {
        PersistentStateManager psm = world.getPersistentStateManager();

        LapisPersistentState state = psm.getOrCreate(
            nbt -> LapisPersistentState.readNbt(nbt, world),
            LapisPersistentState::new,
            "lapisworks_rituals"
        );

        return state;
    }
}
