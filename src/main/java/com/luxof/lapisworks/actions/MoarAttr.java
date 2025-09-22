package com.luxof.lapisworks.actions;

import java.util.List;

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
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.init.Mutables;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

public class MoarAttr implements SpellAction {
    // i always keep my shit public in case someone needs to do something cursed
    public EntityAttribute modifyAttribute;
    public double limitModifier;
    public double limitOffset; // "only give mobs +60 +2xbase hp at max!"
    public double attrCompensateMult; // base plr speed is 0.1? set this to 10 or smth.
    public int expendedAmelModifier;
    public boolean playerOnly;

    public MoarAttr(
        EntityAttribute modifyAttribute,
        double limitModifier,
        double limitOffset,
        double attrCompensateMult,
        int expendedAmelModifier,
        boolean playerOnly
    ) {
        this.modifyAttribute = modifyAttribute;
        this.limitModifier = limitModifier;
        this.limitOffset = limitOffset;
        this.attrCompensateMult = attrCompensateMult;
        this.expendedAmelModifier = expendedAmelModifier;
        this.playerOnly = playerOnly;
    }

    public int getArgc() {
        return 2;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        LivingEntity entity;
        if (!playerOnly) { entity = OperatorUtils.getLivingEntityButNotArmorStand(args, 0, getArgc()); }
        else { entity = OperatorUtils.getPlayer(args, 0, getArgc()); }
        double count = OperatorUtils.getPositiveDouble(args, 1, getArgc());

        HeldItemInfo AmelInfo = ctx.getHeldItemToOperateOn(Mutables::isAmel);
        if (AmelInfo == null) {
            MishapThrowerJava.throwMishap(MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), "amel"));
        }
        ItemStack offHandItems = AmelInfo.stack();
        Hand hand = AmelInfo.hand();

        double currentCombinedVal = entity.getAttributes()
            .getCustomInstance(this.modifyAttribute)
            .getBaseValue();
        double currentJuicedUpVal = ((LapisworksInterface)entity).getAmountOfAttrJuicedUpByAmel(
            this.modifyAttribute
        );
        double defaultVal = currentCombinedVal - currentJuicedUpVal;
        double defaultValCompensated = defaultVal * this.attrCompensateMult;
        double limit = defaultValCompensated * this.limitModifier + this.limitOffset;

        double addToVal = Math.min(defaultValCompensated + count, limit) - defaultValCompensated;
        int expendedAmel = (int)Math.ceil(addToVal * this.expendedAmelModifier);

        if (offHandItems.getCount() < expendedAmel) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughItems(offHandItems, expendedAmel));
        }

        LOGGER.info("Expended Amel: " + expendedAmel);

        return new SpellAction.Result(
            // caster is kinda being operated on but that's not the main effect so 2nd prio
            new Spell(entity, expendedAmel, addToVal / this.attrCompensateMult, this.modifyAttribute, hand),
            MediaConstants.SHARD_UNIT * expendedAmel,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 25)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final LivingEntity entity;
        public final int expendedAmel;
        public final double addVal;
        public final EntityAttribute attr;
        public final Hand hand;

        public Spell(
            LivingEntity entity,
            int expendedAmel,
            double addVal,
            EntityAttribute attr,
            Hand hand
        ) {
            this.entity = entity;
            this.expendedAmel = expendedAmel;
            this.addVal = addVal;
            this.attr = attr;
            this.hand = hand;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            EntityAttributeInstance AttrInst = this.entity.getAttributes().getCustomInstance(this.attr);
            AttrInst.setBaseValue(AttrInst.getBaseValue() + this.addVal);
            double juicedUpAttr = ((LapisworksInterface)this.entity).getAmountOfAttrJuicedUpByAmel(this.attr);
            ((LapisworksInterface)this.entity).setAmountOfAttrJuicedUpByAmel(
                this.attr,
                juicedUpAttr + this.addVal
            );
            ItemStack amelStack = ctx.getHeldItemToOperateOn(Mutables::isAmel).stack();

            ctx.replaceItem(Mutables::isAmel, new ItemStack(
                    amelStack.getItem(),
                    amelStack.getCount() - this.expendedAmel
            ), this.hand);
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
