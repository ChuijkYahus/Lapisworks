package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughMedia;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.google.common.collect.ImmutableSet;

import com.luxof.lapisworks.media.LinkableMediaBlock;
import com.luxof.lapisworks.media.MediaTransferInterface;
import com.luxof.lapisworks.mixin.PlayerBasedCastEnvAccessor;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.fullLinkableMediaBlocksInteraction;
import static com.luxof.lapisworks.Lapisworks.interactWithLinkableMediaBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class Deposit extends SpellActionNCT {
    public int argc = 2;

    private void assertNoOvercast(long cost) {
        if (
            ctx instanceof PlayerBasedCastEnv pbcenv &&
            ((PlayerBasedCastEnvAccessor)pbcenv).lapisworks$invokeCanOvercast() &&
            ctx.extractMedia(-1, true) - 20*MediaConstants.DUST_UNIT < cost &&
            !((ServerPlayerEntity)pbcenv.getCastingEntity()).isCreative() &&
            !((ServerPlayerEntity)pbcenv.getCastingEntity()).isSpectator()
        ) {
            throw new MishapNotEnoughMedia(cost);
        }
    }

    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        MediaTransferInterface MTI = stack.getMediaTransferInterface(0);
        long amount = (long)(stack.getPositiveDouble(1) * MediaConstants.DUST_UNIT);

        List<ParticleSpray> particles = new ArrayList<>(List.of(
            ParticleSpray.cloud(ctx.mishapSprayPos(), 3, 20)
        ));

        if (MTI instanceof LinkableMediaBlock lmb) {

            BlockPos pos = lmb.getThisPos();
            Pair<Long, Set<BlockPos>> interactSimResult = fullLinkableMediaBlocksInteraction(
                ctx.getWorld(),
                Set.of(pos),
                amount,
                true,
                true
            );
            long realAmount = interactSimResult.getLeft();


            particles.addAll(interactSimResult.getRight().stream().map(
                position -> ParticleSpray.cloud(position.toCenterPos(), 3, 10)
            ).toList());


            long cost = (long)(1.1*realAmount);
            assertNoOvercast(cost);


            return new SpellAction.Result(
                new LMBSpell(pos, realAmount),
                cost,
                particles,
                1
            );
        }

        long realAmount = Math.min(
            amount,
            MTI.getMaxMedia() - MTI.getMediaHere()
        );

        long cost = (long)(1.1*realAmount);
        assertNoOvercast(cost);

        return new SpellAction.Result(
            new MTISpell(MTI, realAmount),
            realAmount + (long)(realAmount * 0.1),
            particles,
            1
        );
    }

    public class MTISpell implements RenderedSpellNCT {
        public final MediaTransferInterface MTI;
        public final long amount;

        public MTISpell(MediaTransferInterface MTI, long amount) {
            this.MTI = MTI;
            this.amount = amount;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            MTI.depositMedia(amount);
        }
    }

    public class LMBSpell implements RenderedSpellNCT {
        public final BlockPos pos;
        public final long amount;

        public LMBSpell(BlockPos pos, long amount) {
            this.pos = pos;
            this.amount = amount;
        }

        @Override
		public void cast(CastingEnvironment ctx) {
            interactWithLinkableMediaBlocks(
                ctx.getWorld(),
                ImmutableSet.of(pos),
                amount,
                true
            );
		}
    }
}
