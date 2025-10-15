package com.luxof.lapisworks.interop.hexical;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

// FUCKING STUPID BITCHASS FUCK FUCK FUCK SHIT
// JUST MAKE THE HITBOX BIGGER FUCKING FUCK
// AAAAARRRRRRRRGGGGGGGGHHHHHHHHHHH
public class LargeItemEntity extends ItemEntity {
    public LargeItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public LargeItemEntity(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return new EntityDimensions(1.0f, 1.0f, true);
    }

    public Box getHitBox() {
        return new Box(
            this.getX() - 0.5,
            this.getY(),
            this.getZ() - 0.5,
            this.getX() + 0.5,
            this.getY() + 1.0,
            this.getZ() + 0.5
        );
    }

    @Override
    public void tick() {
        super.tick();
        this.setBoundingBox(this.getHitBox());
    }
}
