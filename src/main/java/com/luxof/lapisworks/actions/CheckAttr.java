package com.luxof.lapisworks.actions;

import java.util.List;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

public class CheckAttr implements ConstMediaAction {
    public List<EntityAttribute> attributes = List.of(
        EntityAttributes.GENERIC_MAX_HEALTH,
        EntityAttributes.GENERIC_ATTACK_DAMAGE,
        EntityAttributes.GENERIC_MOVEMENT_SPEED,
        ReachEntityAttributes.REACH,
        ReachEntityAttributes.ATTACK_RANGE
    );

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        int chosen = OperatorUtils.getIntBetween(args, 0, 0, 4, getArgc());
        return List.of(
            new DoubleIota(
                ((LapisworksInterface)(
                    chosen == 2 ? // fuck your readability my Java is opinionated to look fancy -old Luxof
                        OperatorUtils.getPlayer(args, 1, getArgc())
                            :
                        OperatorUtils.getLivingEntityButNotArmorStand(args, 1, getArgc())
                    )
                ).getAmountOfAttrJuicedUpByAmel(
                    this.attributes.get(
                        chosen
                    )
                ) * (chosen == 2 ? 10 : 1)
            )
        );
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
    }

    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    public long getMediaCost() {
        return (long)(MediaConstants.DUST_UNIT * 0.01);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
    
}
