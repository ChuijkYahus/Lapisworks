package com.luxof.lapisworks.mixinsupport;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public interface CollarControllable {
    public ItemStack getCollar();
    public ItemStack setCollar(ItemStack collar);

    public static class CollarMindControlGoal extends Goal {

        private final MobEntity mob;
        private final ADIotaHolder iotaHolder;
        private Vec3d previouslyGoingTo = null;
        private Vec3d goingTo = null;

        public CollarMindControlGoal(CollarControllable collarable) {
            this.mob = (MobEntity)collarable;
            this.iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(mob);
        }

        @Override
        public boolean canStart() {
            
            if (iotaHolder == null) return false;
            NbtCompound iota = iotaHolder.readIotaTag();
            if (iota == null) return false;
            IotaType<?> iotaType = IotaType.getTypeFromTag(iota);

            if (iotaType != Vec3Iota.TYPE) return false;

            goingTo = Vec3Iota.deserialize(iota.get(HexIotaTypes.KEY_DATA)).getVec3();
            return true;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (previouslyGoingTo == null) previouslyGoingTo = goingTo;
            if (goingTo == null) return;

            if (previouslyGoingTo.equals(goingTo)) return;
            else previouslyGoingTo = goingTo;

            if (mob instanceof TameableEntity tameable)
                tameable.setInSittingPose(false);
            mob.getNavigation().startMovingTo(goingTo.x, goingTo.y, goingTo.z, 1.1F);
        }
    }
}
