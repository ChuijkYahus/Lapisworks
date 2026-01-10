package com.luxof.lapisworks.nocarpaltunnel;

import static at.petrak.hexcasting.api.misc.MediaConstants.DUST_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.SHARD_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.CRYSTAL_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.QUENCHED_SHARD_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.QUENCHED_BLOCK_UNIT;

import com.luxof.lapisworks.VAULT.VAULT;

import net.minecraft.server.world.ServerWorld;

public abstract class NCTBase {
    public ServerWorld world;
    public VAULT vault;

    protected long dust(double dust) { return (long)(DUST_UNIT * dust); }
    protected long shards(double shards) { return (long)(SHARD_UNIT * shards); }
    /** charged amethyst */
    protected long crystals(double crystals) { return (long)(CRYSTAL_UNIT * crystals); }
    /** charged amethyst */
    protected long charged(double charged) { return (long)(CRYSTAL_UNIT * charged); }
    protected long quenchedShards(double quenchedShards) { return (long)(QUENCHED_SHARD_UNIT * quenchedShards); }
    protected long quenchedBlocks(double quenchedBlocks) { return (long)(QUENCHED_BLOCK_UNIT * quenchedBlocks); }
}
