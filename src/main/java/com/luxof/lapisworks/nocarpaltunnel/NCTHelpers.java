package com.luxof.lapisworks.nocarpaltunnel;

import static at.petrak.hexcasting.api.misc.MediaConstants.DUST_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.SHARD_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.CRYSTAL_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.QUENCHED_SHARD_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.QUENCHED_BLOCK_UNIT;

public interface NCTHelpers {
    default long dust(double dust) { return (long)(DUST_UNIT * dust); }
    default long shards(double shards) { return (long)(SHARD_UNIT * shards); }
    /** charged amethyst */
    default long crystals(double crystals) { return (long)(CRYSTAL_UNIT * crystals); }
    /** charged amethyst */
    default long charged(double charged) { return (long)(CRYSTAL_UNIT * charged); }
    default long quenchedShards(double quenchedShards) { return (long)(QUENCHED_SHARD_UNIT * quenchedShards); }
    default long quenchedBlocks(double quenchedBlocks) { return (long)(QUENCHED_BLOCK_UNIT * quenchedBlocks); }
}
