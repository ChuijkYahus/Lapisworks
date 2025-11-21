package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.blocks.entities.LiveJukeboxEntity;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.LapisworksIDs.LIVE_JUKEBOX_BLOCK;
import static com.luxof.lapisworks.LapisworksIDs.NOTELIST;
import static com.luxof.lapisworks.LapisworksIDs.NOTELIST_MOFO;
import static com.luxof.lapisworks.LapisworksIDs.NOTELIST_OUTOFRANGE;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class TeachSong implements SpellAction {
    public int getArgc() {
        return 3;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        BlockPos liveJukeboxPos = OperatorUtils.getBlockPos(args, 0, getArgc());
        try { ctx.assertPosInRange(liveJukeboxPos); }
        catch (Mishap mishap) { MishapThrowerJava.throwMishap(mishap); }

        LiveJukeboxEntity blockEntity = MishapThrowerJava.throwIfEmpty(
            ctx.getWorld().getBlockEntity(liveJukeboxPos, ModBlocks.LIVE_JUKEBOX_ENTITY_TYPE),
            new MishapBadBlock(liveJukeboxPos, LIVE_JUKEBOX_BLOCK)
        );

        SpellList iotaList = OperatorUtils.getList(args, 1, getArgc());
        List<Integer> notes = new ArrayList<>();
        int mishapOnIndex = 1;
        Iota mishapOnIota = args.get(mishapOnIndex);
        iotaList.forEach(iota -> {
            if (iota instanceof DoubleIota) {
                double doubleNote = ((DoubleIota)iota).getDouble();
                double roundedNote = (int)Math.round(doubleNote);
                if (Math.abs(doubleNote - roundedNote) > DoubleIota.TOLERANCE) {
                    MishapThrowerJava.throwMishap(
                        new MishapInvalidIota(
                            mishapOnIota,
                            mishapOnIndex,
                            NOTELIST_MOFO
                        )
                    );
                } else if (roundedNote < 0.0 || roundedNote > 24.0) {
                    MishapThrowerJava.throwMishap(
                        new MishapInvalidIota(
                            mishapOnIota,
                            mishapOnIndex,
                            NOTELIST_OUTOFRANGE
                        )
                    );
                }
                notes.add((int)roundedNote);
            } else {
                MishapThrowerJava.throwMishap(
                    new MishapInvalidIota(
                        mishapOnIota,
                        mishapOnIndex,
                        NOTELIST
                    )
                );
            }
        });

        int frequency = OperatorUtils.getIntBetween(args, 2, 0, 20, getArgc());

        return new SpellAction.Result(
            new Spell(blockEntity, notes, frequency),
            MediaConstants.SHARD_UNIT,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final LiveJukeboxEntity blockEntity;
        public final List<Integer> notes;
        public final int frequency;

        public Spell(LiveJukeboxEntity blockEntity, List<Integer> notes, int frequency) {
            this.blockEntity = blockEntity;
            this.notes = notes;
            this.frequency = frequency;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            this.blockEntity.notes = List.copyOf(this.notes);
            this.blockEntity.frequency = this.frequency;
            this.blockEntity.playingNotes = List.of();
            this.blockEntity.hasBeenTimeBetweenNotes = 0;
            this.blockEntity.markDirty();
		}

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }

    @Override
    public boolean awardsCastingStat(CastingEnvironment ctx) {
        return SpellAction.DefaultImpls.awardsCastingStat(this, ctx);
    }

    @Override
    public Result executeWithUserdata(List<? extends Iota> args, CastingEnvironment env, NbtCompound userData) {
        return SpellAction.DefaultImpls.executeWithUserdata(this, args, env, userData);
    }

    @Override
    public boolean hasCastingSound(CastingEnvironment ctx) {
        return SpellAction.DefaultImpls.hasCastingSound(this, ctx);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return SpellAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }

    @Nullable
    public static ServerPlayerEntity getPlayerOrNull(CastingEnvironment ctx) {
        return ctx.getCastingEntity() != null ? (ServerPlayerEntity)ctx.getCastingEntity() : null;
    }
}
