package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.pigment.ItemDyePigment;

import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.blocks.ConjuredColorable.COLOR;
import static com.luxof.lapisworks.blocks.ConjuredColorable.PIGMENT;

import java.util.List;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ConjureColor implements SpellAction {

    public int getArgc() {
        return 2;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        List<ParticleSpray> particles;
        Optional<LivingEntity> casterOption = Optional.of(ctx.getCastingEntity());
        if (casterOption.isPresent()) {
            particles = List.of(ParticleSpray.burst(casterOption.get().getPos(), 1, 10));
        } else {
            particles = List.of();
        }

        int color = OperatorUtils.getIntBetween(args, 1, 0, 15, getArgc());
        BlockPos place = OperatorUtils.getBlockPos(args, 0, getArgc());
        ctx.assertPosInRangeForEditing(place);

        AutomaticItemPlacementContext AIPC = new AutomaticItemPlacementContext(
            ctx.getWorld(),
            place,
            Direction.DOWN,
            ItemStack.EMPTY,
            Direction.UP
        );
        if (!ctx.getWorld().getBlockState(place).canReplace(AIPC))
            throw MishapBadBlock.of(place, "replaceable");

        return new SpellAction.Result(
            new Spell(color, place, AIPC),
            MediaConstants.DUST_UNIT * 2,
            particles,
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final int color;
        public final BlockPos place;
        public final AutomaticItemPlacementContext AIPC;

        public Spell(int color, BlockPos place, AutomaticItemPlacementContext AIPC) {
            this.color = color;
            this.place = place;
            this.AIPC = AIPC;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            // wtf are those ixplat and other checks in the og
            // im not doin allat
            if (!ctx.getWorld().canSetBlock(this.place)) { return; }
            // "2 standards is too fucking many!"
            // "agreed, let's add one more and call it ParchmentMC!"
            // "yes! and to make it extra better, let's make it difficult
            //  to map to Yarn!"
            // "oh my god, that's such a brilliant idea i could kiss you
            //  you right now!"
            // and then the sons of satan kissed
            // /s
            Item pigmentItem = ctx.getPigment().item().getItem();
            DyeColor dye;
            // could be ItemAmethystAndCopperPigment or something
            if (!(pigmentItem instanceof ItemDyePigment)) {
                // well, fuck you too!
                dye = DyeColor.PURPLE;
            } else {
                dye = ((ItemDyePigment)pigmentItem).getDyeColor();
            }
            BlockState state = ModBlocks.CONJURED_COLORABLE
                .getPlacementState(this.AIPC)
                .with(COLOR, this.color)
                .with(PIGMENT, (dye));
            ctx.getWorld().setBlockState(
                this.place,
                state,
                Block.NOTIFY_ALL
            );
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
}
