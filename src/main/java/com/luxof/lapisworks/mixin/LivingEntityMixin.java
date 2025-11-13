package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.DamageSupportInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LapisworksInterface, DamageSupportInterface {
	public AttributeContainer juicedUpVals = new AttributeContainer(
		DefaultAttributeContainer.builder()
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0) // fists
			.add(EntityAttributes.GENERIC_ATTACK_SPEED, 0) // dexterity
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 0) // skin
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0) // feet
			.build()
	);
	public List<Integer> enchantments = new ArrayList<Integer>(List.of(0, 0, 0, 0, 0));

	private void expandEnchantmentsIfNeeded(int idx) {
		while (idx > this.enchantments.size() - 1) { this.enchantments.add(0); }
	}

	@Override
	public double getAmountOfAttrJuicedUpByAmel(EntityAttribute attribute) {
		return this.juicedUpVals.getCustomInstance(attribute).getBaseValue();
	}

	@Override
	public void setAmountOfAttrJuicedUpByAmel(EntityAttribute attribute, double value) {
		this.juicedUpVals.getCustomInstance(attribute).setBaseValue(value);
	}

	@Override
	public void setAllJuicedUpAttrsToZero() {
		this.juicedUpVals.getAttributesToSend().forEach(
			(EntityAttributeInstance inst) -> {
				inst.setBaseValue(0);
			}
		);
	}

	@Override
	public AttributeContainer getLapisworksAttributes() { return this.juicedUpVals; }
	@Override
	public void setLapisworksAttributes(AttributeContainer attributes) { this.juicedUpVals = attributes; }

	@Override
	public int getEnchant(int whatEnchant) {
		expandEnchantmentsIfNeeded(whatEnchant);
		return this.enchantments.get(whatEnchant);
	}

	@Override
	public void setEnchantmentLevel(int whatEnchant, int level) {
		this.expandEnchantmentsIfNeeded(whatEnchant);
		this.enchantments.set(whatEnchant, level);
	}

	// still not DRYer than your dms
	@Override
	public void incrementEnchant(int whatEnchant) { this.incrementEnchant(whatEnchant, 1); }
	@Override
	public void incrementEnchant(int whatEnchant, int amount) {
		this.setEnchantmentLevel(
			whatEnchant,
			this.getEnchant(whatEnchant) + amount
		);
	}
	@Override
	public void decrementEnchant(int whatEnchant) { this.incrementEnchant(whatEnchant, -1); }
	@Override
	public void decrementEnchant(int whatEnchant, int amount) { this.incrementEnchant(whatEnchant, -amount); }

	@Override
	public List<Integer> getEnchantments() {
		return List.copyOf(this.enchantments);
	}

	@Override
	public int[] getEnchantmentsArray() {
		return this.enchantments.stream().mapToInt(Integer::intValue).toArray();
	}

	@Override
	public void setEnchantments(int[] levels) {
		for (int i = 0; i < levels.length && i < this.enchantments.size(); i++) {
			this.enchantments.set(i, levels[i]);
		}
	}

	@Override
	public void setAllEnchantsToZero() {
		for (int i = 0; i < this.enchantments.size(); i++) { this.enchantments.set(i, 0); }
	}

	@Override
	public void copyCrossDeath(ServerPlayerEntity oldplr) {}

	@Override
	public void copyCrossDimensional(ServerPlayerEntity oldplr) {
		LapisworksInterface old = (LapisworksInterface)oldplr;
		old.setLapisworksAttributes(old.getLapisworksAttributes());
		old.setEnchantments(this.getEnchantmentsArray());
	}



	// not sure i even need this
	@Inject(at = @At("HEAD"), method = "onDeath")
	public void onDeath(DamageSource damageSource, CallbackInfo ci) {
		this.setAllJuicedUpAttrsToZero();
		this.setAllEnchantsToZero();
	}

	@Inject(at = @At("HEAD"), method = "onAttacking")
	public void onAttacking(Entity target, CallbackInfo ci) {
		if (target instanceof LivingEntity && !target.getWorld().isClient) {
			if (this.getEnchant(AllEnchantments.fireyFists) == 1) {
				((LivingEntity)target).setOnFireFor(3);
			}
			int lightningbendingLevel = this.getEnchant(AllEnchantments.lightningBending);
			World world = target.getWorld();
			Vec3d targetPos = target.getPos();

			if ((lightningbendingLevel == 1 && world.isThundering()) ||
				(lightningbendingLevel == 2 && (world.isRaining() || world.isRaining())) ||
				lightningbendingLevel == 3) {
				LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
				lightning.setPos(targetPos.x, targetPos.y, targetPos.z);
				// why? vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
				world.getServer().getWorld(world.getRegistryKey()).tryLoadEntity(lightning);
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "computeFallDamage", cancellable = true)
	public void computeFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
		if (fallDistance < 10 * this.getEnchant(AllEnchantments.fallDmgRes)) {
			cir.setReturnValue(0);
		}
	}

	@Inject(at = @At("HEAD"), method = "getNextAirUnderwater", cancellable = true)
	public void getNextAirUnderwater(int air, CallbackInfoReturnable<Integer> cir) {
		int i = EnchantmentHelper.getRespiration((LivingEntity)(Object)this); // this looks dangerous to me
		cir.setReturnValue(
			// can't shadow getRandom() for whatever reason
			((LivingEntity)(Object)this).getRandom().nextInt(
				i + (this.getEnchant(AllEnchantments.longBreath) * 2) + 1
			) > 0 ? air : air - 1
		);
	}

	@Inject(at = @At("HEAD"), method = "getNextAirOnLand", cancellable = true)
	public void getNextAirOnLand(int air, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(
			Math.min(
				air + 4 + (2 * this.getEnchant(AllEnchantments.longBreath)),
				// can't shadow getMaxAir either but it's because it's in Entity not LivingEntity
				((LivingEntity)(Object)this).getMaxAir()
			)
		);
	}

	@Inject(at = @At("HEAD"), method = "damage", cancellable = true)
	public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!this.damageHelper(source, amount, (LivingEntity)(Object)this, this.getEnchantments())) {
			cir.setReturnValue(false);
		}
	}
}
