package com.luxof.lapisworks.actions;

import java.util.List;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;
import com.luxof.lapisworks.mixinsupport.GetVAULT;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

// "too late" (lazy) to make this use EntityAttributeModifiers instead
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

        VAULT vault = ((GetVAULT)ctx).grabVAULT();
        int availableAmel = vault.fetch(Mutables::isAmel, Flags.PRESET_Stacks_InvItem_UpToHotbar);

        double currentCombinedVal = entity.getAttributes()
            .getCustomInstance(this.modifyAttribute)
            .getBaseValue();
        double currentJuicedUpVal = ((LapisworksInterface)entity).getAmountOfAttrJuicedUpByAmel(
            this.modifyAttribute
        );
        double defaultVal = currentCombinedVal - currentJuicedUpVal;
        double defaultValCompensated = defaultVal * this.attrCompensateMult;
        double baseLimit = defaultValCompensated * this.limitModifier + this.limitOffset;
        double currentLimit = baseLimit - currentCombinedVal;

        double addToVal = Math.min(
            Math.min(defaultValCompensated + count, baseLimit) - defaultValCompensated,
            currentLimit
        );
        int expendedAmel = (int)Math.ceil(addToVal * this.expendedAmelModifier);

        if (availableAmel < expendedAmel) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughItems(
                AMEL,
                availableAmel,
                expendedAmel
            ));
        }

        // 0 in case some genius actually exploited the bug from 1.5.5.5,
        // we don't want shit softlocking til death
        if (expendedAmel < 0) {
            if (entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)entity).sendMessage(Text.translatable("notif.lapisworks.you_used_the_enchant_bug"), true);
            }
            expendedAmel = 0;
            addToVal = 0;
            entity.getAttributes().getCustomInstance(this.modifyAttribute).setBaseValue(defaultVal);
        }

        return new SpellAction.Result(
            // caster is kinda being operated on but that's not the main effect so 2nd prio
            new Spell(
                entity, vault, this.modifyAttribute,
                expendedAmel, addToVal / this.attrCompensateMult),
            Math.max(MediaConstants.SHARD_UNIT * expendedAmel, 0),
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 25)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final LivingEntity entity;
        public final VAULT vault;
        public final EntityAttribute attr;
        public final int expendedAmel;
        public final double addVal;

        public Spell(
            LivingEntity entity,
            VAULT vault,
            EntityAttribute attr,
            int expendedAmel,
            double addVal
        ) {
            this.entity = entity;
            this.vault = vault;
            this.expendedAmel = expendedAmel;
            this.addVal = addVal;
            this.attr = attr;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            vault.drain(Mutables::isAmel, expendedAmel, Flags.PRESET_Stacks_InvItem_UpToHotbar);
            EntityAttributeInstance AttrInst = this.entity.getAttributes().getCustomInstance(this.attr);
            AttrInst.setBaseValue(AttrInst.getBaseValue() + this.addVal);
            double juicedUpAttr = ((LapisworksInterface)this.entity).getAmountOfAttrJuicedUpByAmel(this.attr);
            ((LapisworksInterface)this.entity).setAmountOfAttrJuicedUpByAmel(
                this.attr,
                juicedUpAttr + this.addVal
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
