package com.luxof.lapisworks.mixinsupport;

import java.util.ArrayList;
import java.util.HashMap;

import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;

import at.petrak.hexcasting.api.casting.iota.Iota;
import net.minecraft.util.math.BlockPos;

public interface RitualsUtil {
    public ArrayList<OneTimeRitualExecutionState> getRituals();
    public void addRitual(OneTimeRitualExecutionState ritual);
    public HashMap<Iota, ArrayList<BlockPos>> getTuneables();
    public ArrayList<BlockPos> getTuneables(Iota key);
    public void addTuneable(Iota key, BlockPos positionOfTuneable);
    public void addTuneables(Iota key, ArrayList<BlockPos> positionsOfTuneables);
    public void removeTuneable(Iota previousKey, BlockPos positionOfTuneable);
}
