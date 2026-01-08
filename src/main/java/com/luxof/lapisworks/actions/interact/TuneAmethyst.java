package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;

import com.luxof.lapisworks.blocks.entities.TuneableAmethystEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class TuneAmethyst extends SpellActionNCT {
    public int argc = 2;

    @Override
    public SpellAction.Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPosInRange(0);
        Iota iota = stack.get(1);

        if (!(ctx.getWorld().getBlockEntity(pos) instanceof TuneableAmethystEntity))
            throw new MishapBadBlock(pos, ModBlocks.TUNEABLE_AMETHYST.getName());

        return new Result(
            new Spell(pos, iota),
            dust(0.01),
            List.of(),
            1
        );
    }

    // i wish record worked with implements/extends
    // or at least, that there were Kotlin-style constructors in Java
    public static final class Spell implements RenderedSpellNCT {
        public final BlockPos pos;
        public final Iota frequency;
        public Spell(BlockPos pos, Iota frequency) { this.pos = pos; this.frequency = frequency; }

        public void cast(CastingEnvironment ctx) {
            ((TuneableAmethystEntity)ctx.getWorld().getBlockEntity(pos)).tune(frequency);
        }
    }
}
