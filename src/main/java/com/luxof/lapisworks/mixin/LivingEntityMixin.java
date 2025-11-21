package com.luxof.lapisworks.mixin;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import static com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.getAttackRange;
import static com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.getReachDistance;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
 
import com.luxof.lapisworks.mixinsupport.DamageSupportInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LapisworksInterface, DamageSupportInterface {
	public LivingEntityMixin(EntityType<?> type, World world) { super(type, world); }

	@Shadow @Final
	private AttributeContainer attributes;

	public AttributeContainer juicedUpVals = new AttributeContainer(
		DefaultAttributeContainer.builder()
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0) // fists
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
		if (attribute == ReachEntityAttributes.REACH) {
			return getReachDistance((LivingEntity)(Object)this, 0);
		} else if (attribute == ReachEntityAttributes.ATTACK_RANGE) {
			return getAttackRange((LivingEntity)(Object)this, 0);
		}
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
		if (!(target instanceof LivingEntity) || target.getWorld().isClient) return;
		if (this.getEnchant(AllEnchantments.fireyFists) == 1) {
			((LivingEntity)target).setOnFireFor(3);
		}
		int lightningbendingLevel = this.getEnchant(AllEnchantments.lightningBending);
		ServerWorld world = (ServerWorld)target.getWorld();
		Vec3d targetPos = target.getPos();

		if ((lightningbendingLevel == 1 && world.isThundering()) ||
			(lightningbendingLevel == 2 && (world.isRaining() || world.isRaining())) ||
			lightningbendingLevel == 3) {
			LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
			lightning.setPos(targetPos.x, targetPos.y, targetPos.z);
			world.tryLoadEntity(lightning);
		}
	}

	@WrapMethod(method = {"computeFallDamage"})
	public int computeFallDamage(float fallDistance, float damageMultiplier, Operation<Integer> og) {
		return og.call(
			Math.max(
				fallDistance - 10 * this.getEnchant(AllEnchantments.fallDmgRes),
				0
			),
			damageMultiplier
		);
	}

	// no refMap loaded??
	/*@Inject(
		method = "getNextAirUnderwater",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "net/minecraft/enchantment/EnchantmentHelper.getRespiration(Lnet/minecraft/entity/LivingEntity;)I",
			shift = At.Shift.AFTER
		)
	)
	public void getNextAirUnderwater(
		int air,
		CallbackInfoReturnable<Integer> cir,
		@Local LocalRef<Integer> i
	) {
		LOGGER.info("i at first: " + i.get());
		i.set(i.get() + this.getEnchant(AllEnchantments.longBreath) * 2);
		LOGGER.info("i now: " + i.get());
	}*/
	@ModifyVariable(
		method = "getNextAirUnderwater",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "net/minecraft/enchantment/EnchantmentHelper.getRespiration(Lnet/minecraft/entity/LivingEntity;)I",
			shift = At.Shift.AFTER
		),
		index = 2 // why is this supposed to be 2? there are only 2 variables in the entire method.
	)
	private int getLongBreathEffectUnderwater(int original) {
		// TODO: according to logging, getEnchant sometimes returns 0 here. investigate, maybe?????
		return original + this.getEnchant(AllEnchantments.longBreath) * 2;
	}

	@ModifyConstant(method = "getNextAirOnLand", constant = @Constant(intValue = 4))
	private int getLongBreathEffectOnLand(int original) {
		return original + this.getEnchant(AllEnchantments.longBreath) * 2;
	}

	@Inject(at = @At("HEAD"), method = "damage", cancellable = true)
	public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!this.damageHelper(source, amount, (LivingEntity)(Object)this, this.getEnchantments())) {
			cir.setReturnValue(false);
		}
	}
}
