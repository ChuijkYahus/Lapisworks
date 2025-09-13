package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.EulerPathFinder;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.items.storage.ItemScroll;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.blocks.Mind;
import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.Lapisworks.matchShape;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** my excuses:
 *  - HexResearch is still stuck at 1.19.2
 *  - I think the artificial mind makes more sense in the context of Lapisworks' "materials
 *      interact with media" shtick (recipe-wise and lore-wise too)
 *  - artificial minds can't thought sieve in HexResearch and can only be flayed to make budding amethyst
 * but ultimately, those are just excuses.
 * sorry lmao
 */
public class HexResearchYoink implements SpellAction {
    @Nullable
    public HexPattern getPerWorldPatternByShape(HexPattern pattern, CastingEnvironment ctx) {
        Registry<ActionRegistryEntry> registry = IXplatAbstractions.INSTANCE.getActionRegistry();
        for (RegistryKey<ActionRegistryEntry> key : registry.getKeys()) {
            ActionRegistryEntry action = registry.get(key);
            if (HexUtils.isOfTag(registry, key, HexTags.Actions.PER_WORLD_PATTERN)) {
                HexPattern scrungled = EulerPathFinder.findAltDrawing(
                    action.prototype(),
                    ctx.getWorld().getSeed()
                );
                LOGGER.info("Scrungled name: " + key.getValue().toString());
                if (scrungled.anglesSignature() == pattern.anglesSignature()) { return scrungled; }
                else if (matchShape(scrungled, pattern)) { return scrungled; }
            }
        };
        return null;
    }
    
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
        if (blockEntity.mindCompletion < 100f) {
            MishapThrowerJava.throwMishap(
                new MishapBadBlock(mindPos, Text.translatable("mishaps.lapisworks.bad_block.full_mind"))
            );
        }


        ItemStack offHandItems = caster.getOffHandStack();
        if (!(offHandItems.getItem() instanceof ItemScroll)) {
            MishapThrowerJava.throwMishap(new MishapBadOffhandItem(
                offHandItems,
                Text.translatable("mishaps.lapisworks.bad_item.offhand.scrolls")
            ));
        }
        ItemScroll scroll = (ItemScroll)offHandItems.getItem();


        Iota iota = scroll.readIota(offHandItems, ctx.getWorld());
        // imagine someone funny says "what if scroll stores number"
        if (!(iota instanceof PatternIota)) {
            MishapThrowerJava.throwMishap(new MishapBadOffhandItem(
                offHandItems,
                Text.translatable("mishaps.lapisworks.bad_item.offhand.scroll_with_pattern")
            ));
        }


        // maybe i shouldn't calculate the pattern regardless of if it's going to even write it?
        HexPattern realPattern = getPerWorldPatternByShape(((PatternIota)iota).getPattern(), ctx);
        if (realPattern == null) {
            MishapThrowerJava.throwMishap(new MishapBadOffhandItem(
                offHandItems,
                Text.translatable("mishaps.lapisworks.bad_item.offhand.scroll_with_great_spell")
            ));
        }

        return new SpellAction.Result(
            new Spell(caster, blockEntity, realPattern, scroll),
            MediaConstants.CRYSTAL_UNIT,
            List.of(ParticleSpray.burst(caster.getPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final LivingEntity caster;
        public final MindEntity blockEntity;
        public final HexPattern pattern;
        public final ItemScroll scroll;

        public Spell(LivingEntity caster, MindEntity blockEntity, HexPattern pattern, ItemScroll scroll) {
            this.caster = caster;
            this.blockEntity = blockEntity;
            this.pattern = pattern;
            this.scroll = scroll;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            this.blockEntity.mindCompletion -= 25.0f;
            this.blockEntity.markDirty();
            if (ctx.getWorld().random.nextInt(4) > 2) { return; } // 3/5 chance (number is 0-4 inclusive)
            this.scroll.writeDatum(caster.getOffHandStack(), new PatternIota(this.pattern));
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
