package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.blocks.Mind;
import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;

import java.util.List;
import java.util.Optional;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** you could make like 10 / (6.666.. * 60) = 0.025 dust per second per mind from this
 * so hexal wisp eating isn't overran */
public class MindLiquefaction implements SpellAction {

    public int getArgc() {
        return 1;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        Optional<LivingEntity> casterOp = Optional.of(ctx.getCastingEntity());
        if (casterOp.isEmpty()) { MishapThrowerJava.throwMishap(new MishapBadCaster()); }
        LivingEntity caster = casterOp.get();


        BlockPos mindPos = OperatorUtils.getBlockPos(args, 0, getArgc());
        try { ctx.assertPosInRange(mindPos); }
        catch (Mishap mishap) { MishapThrowerJava.throwMishap(mishap); }
        MishapBadBlock needMind = new MishapBadBlock(mindPos, Text.translatable("block.lapisworks.mind"));
        if (!(ctx.getWorld().getBlockState(mindPos).getBlock() instanceof Mind)) {
            MishapThrowerJava.throwMishap(needMind);
        }


        Optional<BlockEntity> blockEntityOpt = ctx
            .getWorld()
            .getBlockEntity(mindPos, ModBlocks.MIND_ENTITY_TYPE);
        if (blockEntityOpt.isEmpty()) { MishapThrowerJava.throwMishap(needMind); }
        MindEntity blockEntity = (MindEntity)blockEntityOpt.get();


        Mishap needRechargeable = MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), "rechargeable");
        HeldItemInfo heldStackInfo = ctx.getHeldItemToOperateOn((stack) -> { return true; });
        if (heldStackInfo == null) {
            MishapThrowerJava.throwMishap(needRechargeable);
        }
        ADMediaHolder mediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(heldStackInfo.component1());
        if (mediaHolder == null || !mediaHolder.canRecharge()) {
            MishapThrowerJava.throwMishap(needRechargeable);
        }
        

        return new SpellAction.Result(
            new Spell(blockEntity, heldStackInfo.component1()),
            0L,
            List.of(ParticleSpray.burst(caster.getPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final MindEntity blockEntity;
        public final ItemStack heldStack;

        public Spell(MindEntity blockEntity, ItemStack heldStack) {
            this.blockEntity = blockEntity;
            this.heldStack = heldStack;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ADMediaHolder media = IXplatAbstractions.INSTANCE.findMediaHolder(this.heldStack);
            media.insertMedia(
                Math.min(
                    media.insertMedia(-1, true),
                    this.blockEntity.getMaxMediaGainFromAbsorption() * ((long)blockEntity.mindCompletion / 100L)
                ),
                false
            );
            this.blockEntity.mindCompletion = 0;
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
}
