package com.luxof.lapisworks.nocarpaltunnel;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.casting.mishaps.MishapUnenlightened;

import static at.petrak.hexcasting.api.misc.MediaConstants.DUST_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.SHARD_UNIT;

import java.util.function.Predicate;

import static at.petrak.hexcasting.api.misc.MediaConstants.CRYSTAL_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.QUENCHED_SHARD_UNIT;
import static at.petrak.hexcasting.api.misc.MediaConstants.QUENCHED_BLOCK_UNIT;

import com.luxof.lapisworks.VAULT.VAULT;

import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

public abstract class PatternNCTBase {
    public CastingEnvironment ctx;
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
    protected void _assertIsEnlightenedIfRequiresEnlightenment() {
        if (getRequiresEnlightenment() && !ctx.isEnlightened())
            throw new MishapUnenlightened();
    }

    private MishapBadOffhandItem mishapOffhand(ItemStack stack, Object name) {
        return name instanceof String
            ? MishapBadOffhandItem.of(stack, (String)name)
            : new MishapBadOffhandItem(stack, (Text)name);
    }
    /** Takes some predicates and displays different errors for all of them.
     * <p>The second element of the pairs may be a <code>String</code>
     * (for <code>MishapBadOffhandItem.of</code>) or <code>Text</code>
     * (for <code>new MishapBadOffhandItem</code>) */
    @SuppressWarnings("unchecked")
    protected HeldItemInfo getHeldItemMatchingPredicates(
        Pair<Predicate<ItemStack>, Object>... predicates
    ) {
        HeldItemInfo initial = ctx.getHeldItemToOperateOn(
            predicates[0].getLeft()
        );
        if (initial == null)
            throw mishapOffhand(ItemStack.EMPTY, initial);

        ItemStack stack = initial.component1();

        for (var predicate : predicates) {
            // let it duuuupe, let it duuuupe!
            if (!predicate.getLeft().test(stack))
                mishapOffhand(stack, predicate.getRight());
        }
        return initial;
    }

    protected abstract boolean getRequiresEnlightenment();
}
