package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.CollarControllable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin extends TameableEntity implements CollarControllable {

    protected CatEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ItemStack getCollar() {
        if (!getDataTracker().containsKey(COLLAR)) trackCollarInTracker();
        return getDataTracker().get(COLLAR);
    }

    @Override
    public ItemStack setCollar(ItemStack collar) {
        if (!getDataTracker().containsKey(COLLAR)) trackCollarInTracker();
        ItemStack previously = getDataTracker().get(COLLAR);
        getDataTracker().set(COLLAR, collar);
        return previously;
    }


    private static final TrackedData<ItemStack> COLLAR = DataTracker.registerData(
        CatEntity.class,
        TrackedDataHandlerRegistry.ITEM_STACK
    );
    @Unique
    private void trackCollarInTracker() { getDataTracker().startTracking(COLLAR, ItemStack.EMPTY); }

    @Inject(
        at = @At("HEAD"),
        method = "initDataTracker"
    )
    protected void lapisworks$addCollar(CallbackInfo ci) {
        this.dataTracker.startTracking(COLLAR, ItemStack.EMPTY);
    }

    @Inject(
        at = @At("HEAD"),
        method = "writeCustomDataToNbt"
    )
    public void lapisworks$writeCollar(NbtCompound nbt, CallbackInfo ci) {
        getCollar().writeNbt(nbt);
    }
    @Inject(
        at = @At("HEAD"),
        method = "readCustomDataFromNbt"
    )
    public void lapisworks$readCollar(NbtCompound nbt, CallbackInfo ci) {
        setCollar(ItemStack.fromNbt(nbt));
    }


    @Injec


    @Inject(
        at = @At("TAIL"),
        method = "initGoals"
    )
    protected void lapisworks$beCollarMindControlled(CallbackInfo ci) {
        this.goalSelector.add(0, new CollarMindControlGoal(this));
    }
}
