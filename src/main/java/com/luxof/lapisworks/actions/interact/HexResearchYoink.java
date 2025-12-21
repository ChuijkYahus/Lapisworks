package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
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
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.EulerPathFinder;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
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
import static com.luxof.lapisworks.LapisworksIDs.FULL_SIMPLE_MIND;
import static com.luxof.lapisworks.LapisworksIDs.GREAT_SCROLL;
import static com.luxof.lapisworks.LapisworksIDs.MIND_BLOCK;
import static com.luxof.lapisworks.LapisworksIDs.PAT_SCROLL;
import static com.luxof.lapisworks.LapisworksIDs.SCROLL;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

/** yes lmao */
public class HexResearchYoink implements SpellAction {
    @Nullable
    public HexPattern getPerWorldPatternByShape(HexPattern pattern, CastingEnvironment ctx) {
        Registry<ActionRegistryEntry> registry = IXplatAbstractions.INSTANCE.getActionRegistry();

        for (RegistryKey<ActionRegistryEntry> key : registry.getKeys()) {
            ActionRegistryEntry action = registry.get(key);

            if (!HexUtils.isOfTag(registry, key, HexTags.Actions.PER_WORLD_PATTERN)) continue;

            HexPattern scrungled = EulerPathFinder.findAltDrawing(
                action.prototype(),
                ctx.getWorld().getSeed()
            );
            LOGGER.info("Scrungled name: \"" + key.getValue().toString() + "\"");

            if (scrungled.anglesSignature().equals(pattern.anglesSignature())) { return scrungled; }
            else if (matchShape(scrungled, pattern)) { return scrungled; }

            if (!key.getValue().toString().equals("hexthingy:smite")) continue;
            LOGGER.info("invalid!");
            LOGGER.info("pattern given: " + pattern.anglesSignature() + " " + pattern.getStartDir().toString());
            LOGGER.info("smite        : " + scrungled.anglesSignature() + " " + scrungled.getStartDir().toString());
        };
        return null;
    }
    
    public int getArgc() {
        return 1;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        BlockPos mindPos = OperatorUtils.getBlockPos(args, 0, getArgc());
        try { ctx.assertPosInRange(mindPos); }
        catch (Mishap mishap) { MishapThrowerJava.throwMishap(mishap); }
        MishapBadBlock needMind = new MishapBadBlock(mindPos, MIND_BLOCK);
        if (!(ctx.getWorld().getBlockState(mindPos).getBlock() instanceof Mind)) {
            MishapThrowerJava.throwMishap(needMind);
        }

        MindEntity blockEntity = MishapThrowerJava.throwIfEmpty(
            ctx.getWorld().getBlockEntity(mindPos, ModBlocks.MIND_ENTITY_TYPE),
            needMind
        );
        if (blockEntity.mindCompletion < 100f) {
            MishapThrowerJava.throwMishap(new MishapBadBlock(mindPos, FULL_SIMPLE_MIND));
        }


        HeldItemInfo held = ctx.getHeldItemToOperateOn(
            stack -> (stack.getItem() instanceof ItemScroll)
        );
        if (held == null) {
            MishapThrowerJava.throwMishap(new MishapBadOffhandItem(
                ItemStack.EMPTY.copy(),
                SCROLL
            ));
            return null; // VSCode complains
        }

        ItemStack offHandItems = held.stack();
        ItemScroll scroll = (ItemScroll)offHandItems.getItem();


        Iota iota = scroll.readIota(offHandItems, ctx.getWorld());
        // imagine someone funny says "what if scroll stores number"
        if (!(iota instanceof PatternIota)) {
            MishapThrowerJava.throwMishap(new MishapBadOffhandItem(
                offHandItems,
                PAT_SCROLL
            ));
        }


        HexPattern realPattern = getPerWorldPatternByShape(((PatternIota)iota).getPattern(), ctx);
        if (realPattern == null) {
            MishapThrowerJava.throwMishap(new MishapBadOffhandItem(
                offHandItems,
                GREAT_SCROLL
            ));
        }

        return new SpellAction.Result(
            new Spell(offHandItems, blockEntity, realPattern, scroll),
            MediaConstants.CRYSTAL_UNIT,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final ItemStack stack;
        public final MindEntity blockEntity;
        public final HexPattern pattern;
        public final ItemScroll scroll;

        public Spell(ItemStack stack, MindEntity blockEntity, HexPattern pattern, ItemScroll scroll) {
            this.stack = stack;
            this.blockEntity = blockEntity;
            this.pattern = pattern;
            this.scroll = scroll;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            this.blockEntity.mindCompletion -= 50.0f;
            this.blockEntity.markDirty();
            if (ctx.getWorld().random.nextInt(4) > 2) { return; } // 3/5 chance (number is 0-4 inclusive)
            this.scroll.writeDatum(stack, new PatternIota(this.pattern));
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
