package com.luxof.lapisworks.actions;

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

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.items.shit.FullyAmelInterface;
import com.luxof.lapisworks.items.shit.PartiallyAmelInterface;
import com.luxof.lapisworks.mishaps.MishapBadMainhandItem;
import com.luxof.lapisworks.mishaps.MishapNotEnoughOffhandItems;

import static com.luxof.lapisworks.Lapisworks.getFullAmelFromNorm;
import static com.luxof.lapisworks.Lapisworks.getPartAmelFromNorm;
import static com.luxof.lapisworks.Lapisworks.getRequiredAmelToComplete;
import static com.luxof.lapisworks.Lapisworks.isAmel;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class ImbueAmel implements SpellAction {

    public int getArgc() {
        return 1;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        Optional<LivingEntity> casterOption = Optional.of(ctx.getCastingEntity());
        if (casterOption.isEmpty()) {
            MishapThrowerJava.throwMishap(new MishapBadCaster());
        }
        LivingEntity caster = casterOption.get();
        int infuseAmount = OperatorUtils.getInt(args, 0, getArgc());

        ItemStack offHandItems = caster.getOffHandStack();
        ItemStack mainHandItems = caster.getMainHandStack();

        MishapBadOffhandItem needAmel = MishapBadOffhandItem.of(offHandItems, "amel");
        MishapBadMainhandItem needImbueable = new MishapBadMainhandItem(
            mainHandItems,
            Text.translatable(
                "mishaps.lapisworks.bad_item.mainhand.imbueable"
            )
        );

        if (offHandItems.isEmpty()) {
            MishapThrowerJava.throwMishap(needAmel);
        } else if (!isAmel(offHandItems)) {
            MishapThrowerJava.throwMishap(needAmel);
        } else if (mainHandItems.isEmpty()) {
            MishapThrowerJava.throwMishap(needImbueable);
        }

        Integer requiredAmelToComplete = getRequiredAmelToComplete(mainHandItems);
        if (requiredAmelToComplete == null) {
            MishapThrowerJava.throwMishap(needImbueable);
        }
        infuseAmount = Math.min(infuseAmount, requiredAmelToComplete);

        FullyAmelInterface fullAmelItem = getFullAmelFromNorm(mainHandItems.getItem());

        if (offHandItems.getCount() < infuseAmount) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughOffhandItems(offHandItems, infuseAmount));
        } else if (infuseAmount < requiredAmelToComplete && fullAmelItem.noPartAmelPhase()) {
            MishapThrowerJava.throwMishap(new MishapNotEnoughOffhandItems(offHandItems, requiredAmelToComplete));
        }

        ItemStack changeToItem;
        if (infuseAmount == requiredAmelToComplete) {
            changeToItem = new ItemStack((Item)fullAmelItem, 1);
        } else if (infuseAmount > 0) { // don't fuck up my shit by trying to infuse 0 amel into a ring
            PartiallyAmelInterface partAmelItem = getPartAmelFromNorm(mainHandItems.getItem());
            changeToItem = new ItemStack((Item)partAmelItem, 1);
            // no i won't explain my math
            changeToItem.setDamage(
                Math.max(
                    partAmelItem.getMaxDurability() -
                        partAmelItem.getAmelWorthInDurability() * infuseAmount -
                        (mainHandItems.getItem() instanceof PartiallyAmelInterface ?
                            mainHandItems.getDamage() : 0),
                    0
                )
            );
        } else {
            changeToItem = mainHandItems;
        }

        return new SpellAction.Result(
            new Spell(caster, changeToItem, infuseAmount),
            MediaConstants.SHARD_UNIT * 2 * offHandItems.getCount(),
            List.of(ParticleSpray.burst(caster.getPos(), 1, 10 + offHandItems.getCount())),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final LivingEntity caster;
        public final ItemStack changeToItem;
        public final int count;

        public Spell(LivingEntity caster, ItemStack changeToItem, int count) {
            this.caster = caster;
            this.changeToItem = changeToItem;
            this.count = count;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ItemStack offHandItems = this.caster.getOffHandStack();
            this.caster.setStackInHand(Hand.OFF_HAND, new ItemStack(
                offHandItems.getItem(),
                offHandItems.getCount() - this.count
            ));
            this.caster.setStackInHand(Hand.MAIN_HAND, this.changeToItem);
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
