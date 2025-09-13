package com.luxof.lapisworks.actions.great;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.casting.mishaps.MishapUnenlightened;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.init.EnchantCountKeeper;
import com.luxof.lapisworks.mishaps.MishapAlreadyHasEnchantment;
import com.luxof.lapisworks.mishaps.MishapNotEnoughOffhandItems;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import static com.luxof.lapisworks.Lapisworks.isAmel;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

/** responsibility of mixin to make enchantment do something falls on the user of this.
 * Also take this' enchantmentIdx if you want the index you need to give to LapisworksInterface's stuff. */
public class GenericEnchant implements SpellAction {
    public final int enchantmentIdx;
    public final int maxLevel;
    public final int requiredAmel;
    public final long requiredMedia;
    public final Text enchantmentLangKey;

    public GenericEnchant(
        int maxLevel,
        int requiredAmel,
        long requiredMedia,
        String enchantmentLangKey
    ) {
        this.enchantmentIdx = EnchantCountKeeper.registerMyEnchantment();
        this.maxLevel = maxLevel;
        this.requiredAmel = requiredAmel;
        this.requiredMedia = requiredMedia;
        this.enchantmentLangKey = Text.translatable(enchantmentLangKey);
    }
    public GenericEnchant(
        int maxLevel,
        int requiredAmel,
        long requiredMedia,
        Text enchantmentLangKey
    ) {
        this.enchantmentIdx = EnchantCountKeeper.registerMyEnchantment();
        this.maxLevel = maxLevel;
        this.requiredAmel = requiredAmel;
        this.requiredMedia = requiredMedia;
        this.enchantmentLangKey = enchantmentLangKey;
    }

    public int getArgc() {
        return 1;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        if (!ctx.isEnlightened()) {
            MishapThrowerJava.throwMishap(new MishapUnenlightened());
        }
        LivingEntity entity = OperatorUtils.getPlayer(args, 0, getArgc());

        if (((LapisworksInterface)entity).getEnchant(this.enchantmentIdx) >= this.maxLevel) {
            MishapThrowerJava.throwMishap(
                new MishapAlreadyHasEnchantment(
                    entity,
                    this.enchantmentLangKey,
                    this.enchantmentIdx,
                    this.maxLevel
                )
            );
        }

        Optional<LivingEntity> casterOption = Optional.of(ctx.getCastingEntity());
        if (casterOption.isEmpty()) {
            MishapThrowerJava.throwMishap(new MishapBadCaster());
        }
        LivingEntity caster = casterOption.get();

        ItemStack offHandItems = caster.getOffHandStack();
        if (offHandItems.isEmpty()) {
            MishapThrowerJava.throwMishap(MishapBadOffhandItem.of(offHandItems, "amel"));
        } else if (!isAmel(offHandItems)) {
            MishapThrowerJava.throwMishap(MishapBadOffhandItem.of(offHandItems, "amel"));
        } else if (offHandItems.getCount() < 10) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughOffhandItems(offHandItems, this.requiredAmel));
        }

        return new SpellAction.Result(
            new Spell(entity, caster),
            this.requiredMedia,
            List.of(ParticleSpray.burst(entity.getPos(), 3, 25)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final LivingEntity entity;
        public final LivingEntity caster;

        public Spell(LivingEntity entity, LivingEntity caster) {
            this.entity = entity;
            this.caster = caster;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            this.caster.setStackInHand(
                Hand.OFF_HAND,
                caster.getOffHandStack().getCount() == requiredAmel ?
                    ItemStack.EMPTY :
                    new ItemStack(
                        caster.getOffHandStack().getItem(),
                        caster.getOffHandStack().getCount() - requiredAmel
                    )
            );
            ((LapisworksInterface)this.entity).incrementEnchant(enchantmentIdx);
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
