package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.math.HexPattern;

import com.mojang.datafixers.util.Either;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class SpellActionNoCarpalTunnel implements SpellAction {
    //protected int argc; // can't have protected
    public abstract Result execute(hexStack stack, CastingEnvironment ctx);

    public class hexStack {
        private List<? extends Iota> stack;

        public hexStack(List<? extends Iota> stack) { this.stack = stack; }

        public BlockPos getBlockPos(int idx) { return OperatorUtils.getBlockPos(stack, idx, getArgc()); }
        public boolean getBool(int idx) { return OperatorUtils.getBool(stack, idx, getArgc()); }
        public double getDouble(int idx) { return OperatorUtils.getDouble(stack, idx, getArgc()); }
        public double getDoubleBetween(int idx, double min, double max) { return OperatorUtils.getDoubleBetween(stack, idx, min, max, getArgc()); }
        public Entity getEntity(int idx) { return OperatorUtils.getEntity(stack, idx, getArgc()); }
        public int getInt(int idx) { return OperatorUtils.getInt(stack, idx, getArgc()); }
        public int getIntBetween(int idx, int min, int max) { return OperatorUtils.getIntBetween(stack, idx, min, max, getArgc()); }
        public ItemEntity getItemEntity(int idx) { return OperatorUtils.getItemEntity(stack, idx, getArgc()); }
        public SpellList getList(int idx) { return OperatorUtils.getList(stack, idx, getArgc()); }
        public LivingEntity getLivingEntityButNotArmorStand(int idx) { return OperatorUtils.getLivingEntityButNotArmorStand(stack, idx, getArgc()); }
        public long getLong(int idx) { return OperatorUtils.getLong(stack, idx, getArgc()); }
        public Either<Long, SpellList> getLongOrList(int idx) { return OperatorUtils.getLongOrList(stack, idx, getArgc()); }
        public MobEntity getMob(int idx) { return OperatorUtils.getMob(stack, idx, getArgc()); }
        public Either<Double, Vec3d> getNumOrVec(int idx) { return OperatorUtils.getNumOrVec(stack, idx, getArgc()); }
        public HexPattern getPattern(int idx) { return OperatorUtils.getPattern(stack, idx, getArgc()); }
        public ServerPlayerEntity getPlayer(int idx) { return OperatorUtils.getPlayer(stack, idx, getArgc()); }
        public double getPositiveDouble(int idx) { return OperatorUtils.getPositiveDouble(stack, idx, getArgc()); }
        public double getPositiveDoubleUnder(int idx, double under) { return OperatorUtils.getPositiveDoubleUnder(stack, idx, under, getArgc()); }
        public double getPositiveDoubleUnderInclusive(int idx, double under) { return OperatorUtils.getPositiveDoubleUnderInclusive(stack, idx, under, getArgc()); }
        public int getPositiveInt(int idx) { return OperatorUtils.getPositiveInt(stack, idx, getArgc()); }
        public int getPositiveIntUnder(int idx, int under) { return OperatorUtils.getPositiveIntUnder(stack, idx, under, getArgc()); }
        public int getPositiveIntUnderInclusive(int idx, int under) { return OperatorUtils.getPositiveIntUnderInclusive(stack, idx, under, getArgc()); }
        public long getPositiveLong(int idx) { return OperatorUtils.getPositiveLong(stack, idx, getArgc()); }
        public Vec3d getVec3(int idx) { return OperatorUtils.getVec3(stack, idx, getArgc()); }
    }

    public interface RenderedSpellNoCarpalTunnel extends RenderedSpell {
        default CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
        
    }

    @Override
    public Result execute(List<? extends Iota> stack, CastingEnvironment ctx) {
        return execute(new hexStack(stack), ctx);
    }



    @Override
    public boolean awardsCastingStat(CastingEnvironment arg0) {
        return SpellAction.DefaultImpls.awardsCastingStat(this, arg0);
    }

    @Override
    public Result executeWithUserdata(List<? extends Iota> arg0, CastingEnvironment arg1, NbtCompound arg2) {
        return SpellAction.DefaultImpls.executeWithUserdata(this, arg0, arg1, arg2);
    }

    @Override
    public boolean hasCastingSound(CastingEnvironment arg0) {
        return SpellAction.DefaultImpls.hasCastingSound(this, arg0);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return SpellAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }

    @Override
    public int getArgc() {
        try {
            return this.getClass().getField("argc").getInt(this);
        } catch (NoSuchFieldException e) {
            LOGGER.error("you must have an argc field in the first place.", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("your argc field must be accessible.", e);
        }
        return 0;
    }
}
