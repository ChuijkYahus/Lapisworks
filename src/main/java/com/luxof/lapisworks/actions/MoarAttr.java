package com.luxof.lapisworks.actions;

import static com.luxof.lapisworks.Lapisworks.isAmel;

import java.util.List;
import java.util.Optional;

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
import at.petrak.hexcasting.api.misc.MediaConstants;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.mishaps.MishapNotEnoughOffhandItems;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

public class MoarAttr implements SpellAction {
    // i always keep my shit public in case someone needs to do something cursed
    public EntityAttribute modifyAttribute;
    public double limitModifier;
    public double limitOffset; // "only give mobs +60 +2xbase hp at max!"
    public int expendedAmelModifier;
    public boolean playerOnly;

    public MoarAttr(EntityAttribute modifyAttribute, double limitModifier, double limitOffset, int expendedAmelModifier, boolean playerOnly) {
        this.modifyAttribute = modifyAttribute;
        this.limitModifier = limitModifier;
        this.limitOffset = limitOffset;
        this.expendedAmelModifier = expendedAmelModifier;
        this.playerOnly = playerOnly;
    }

    public int getArgc() {
        return 2;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        LivingEntity entity;
        if (!playerOnly) {
            entity = OperatorUtils.getLivingEntityButNotArmorStand(args, 0, getArgc());
        } else {
            entity = OperatorUtils.getPlayer(args, 0, getArgc());
        }
        int count = OperatorUtils.getInt(args, 1, getArgc());

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
        }

        EntityAttributeInstance AttrInst = entity.getAttributes().getCustomInstance(this.modifyAttribute);
        double currentAttrVal = AttrInst.getBaseValue();
        double juicedUpAttrVal = ((LapisworksInterface)entity).getAmountOfAttrJuicedUpByAmel(this.modifyAttribute);
        double defaultAttrVal = currentAttrVal - juicedUpAttrVal;
        double limit = defaultAttrVal * this.limitModifier + this.limitOffset;

        double setTo = Math.min(currentAttrVal + count, limit);
        int expendedAmel = (int)Math.ceil((setTo - currentAttrVal) * expendedAmelModifier);

        if (offHandItems.getCount() < expendedAmel) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughOffhandItems(offHandItems, expendedAmel));
        }

        return new SpellAction.Result(
            // caster is kinda being operated on but that's not the main effect so 2nd prio
            new Spell(entity, caster, expendedAmel, setTo, this.modifyAttribute),
            MediaConstants.SHARD_UNIT * expendedAmel,
            List.of(ParticleSpray.burst(caster.getPos(), 2, 25)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final LivingEntity entity;
        public final LivingEntity caster;
        public final int expendedAmel;
        public final double setTo;
        public final EntityAttribute attr;

        public Spell(LivingEntity entity, LivingEntity caster, int expendedAmel, double setTo, EntityAttribute attr) {
            this.entity = entity;
            this.caster = caster;
            this.expendedAmel = expendedAmel;
            this.setTo = setTo;
            this.attr = attr;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            EntityAttributeInstance AttrInst = this.entity.getAttributes().getCustomInstance(this.attr);
            double juicedUpAttrVal = ((LapisworksInterface)this.entity).getAmountOfAttrJuicedUpByAmel(this.attr);
            
            ((LapisworksInterface)this.entity).setAmountOfAttrJuicedUpByAmel(this.attr, juicedUpAttrVal + this.expendedAmel);
            
            AttrInst.setBaseValue(this.setTo);
            this.caster.setStackInHand(
                Hand.OFF_HAND,
                new ItemStack(
                    this.caster.getOffHandStack().getItem(),
                    this.caster.getOffHandStack().getCount() - this.expendedAmel
                )
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
