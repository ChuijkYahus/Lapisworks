package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.google.common.collect.ImmutableSet;

import com.luxof.lapisworks.interop.hexal.actions.WithdrawIntoWisp;
import com.luxof.lapisworks.media.LinkableMediaBlock;
import com.luxof.lapisworks.media.MediaTransferInterface;
import com.luxof.lapisworks.media.MTIMediaHolder;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.HEXAL_INTEROP;
import static com.luxof.lapisworks.Lapisworks.fullLinkableMediaBlocksInteraction;
import static com.luxof.lapisworks.Lapisworks.interactWithLinkableMediaBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class Withdraw extends SpellActionNCT {
    public int getArgc() {
        return 2;
    }

    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        MediaTransferInterface from = stack.getMediaTransferInterface(0);
        long amount = (long)(stack.getPositiveDouble(1) * MediaConstants.DUST_UNIT);

        MediaTransferInterface into;
        if (HEXAL_INTEROP && WithdrawIntoWisp.isWisp(ctx)) {
            into = WithdrawIntoWisp.getCastingWispAsMTI(ctx);

        } else {
            // how do i make it not warn...
            @SuppressWarnings("unchecked")
            HeldItemInfo heldInfo = getHeldItemMatchingPredicates(
                new Pair<>(
                    itemStack -> itemStack.getItem() instanceof MediaHolderItem,
                    "rechargeable"
                )
            );
            if (heldInfo == null)
                throw MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), "rechargeable");

            ItemStack intoStack = heldInfo.component1();
            into = new MTIMediaHolder(intoStack);
        }

        amount = Math.min(amount, into.getMaxMedia() - into.getMediaHere());


        List<ParticleSpray> particles = new ArrayList<>(List.of(
            ParticleSpray.cloud(ctx.mishapSprayPos(), 3, 20)
        ));

        if (from instanceof LinkableMediaBlock lmb) {
            BlockPos pos = lmb.getThisPos();
            Pair<Long, Set<BlockPos>> interactSimResult = fullLinkableMediaBlocksInteraction(
                ctx.getWorld(),
                Set.of(pos),
                amount,
                false,
                true
            );
            long realAmount = interactSimResult.getLeft();

            particles.addAll(interactSimResult.getRight().stream().map(
                position -> ParticleSpray.cloud(position.toCenterPos(), 3, 10)
            ).toList());

            return new SpellAction.Result(
                new LMBSpell(pos, realAmount, into),
                realAmount + (long)(realAmount * 0.1),
                particles,
                1
            );
        }

        long realAmount = Math.min(
            amount,
            from.getMaxMedia() - from.getMediaHere()
        );


        return new SpellAction.Result(
            new MTISpell(from, realAmount, into),
            (long)(realAmount * 0.1),
            particles,
            1
        );
    }

    public class MTISpell implements RenderedSpellNCT {
        public final MediaTransferInterface from;
        public final long amount;
        public final MediaTransferInterface into;

        public MTISpell(MediaTransferInterface from, long amount, MediaTransferInterface into) {
            this.from = from;
            this.amount = amount;
            this.into = into;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            into.depositMedia(from.withdrawMedia(amount));
        }
    }

    public class LMBSpell implements RenderedSpellNCT {
        public final BlockPos pos;
        public final long amount;
        public final MediaTransferInterface into;

        public LMBSpell(BlockPos pos, long amount, MediaTransferInterface into) {
            this.pos = pos;
            this.amount = amount;
            this.into = into;
        }

        @Override
		public void cast(CastingEnvironment ctx) {
            into.depositMedia(
                interactWithLinkableMediaBlocks(
                    ctx.getWorld(),
                    ImmutableSet.of(pos),
                    amount,
                    true,
                    false
                )
            );
		}
    }
}
