package com.luxof.lapisworks.mixin;

import com.google.common.collect.ImmutableMultimap;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import static com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.ATTACK_RANGE;
import static com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.REACH;
import static com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.getAttackRange;
import static com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.getReachDistance;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.luxof.lapisworks.actions.MoarReachYouBitch;
import com.luxof.lapisworks.mixinsupport.DamageSupportInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.LapisworksIDs.REACH_ENHANCEMENT_UUID;

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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

	@Unique private AttributeContainer juicedUpVals = new AttributeContainer(
		DefaultAttributeContainer.builder()
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0) // fists
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 0) // skin
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0) // feet
			.build()
	);
	@Unique private List<Integer> enchantments = new ArrayList<Integer>(List.of(0, 0, 0, 0, 0));

	@Unique
	private void expandEnchantmentsIfNeeded(int idx) {
		while (idx > this.enchantments.size() - 1) { this.enchantments.add(0); }
	}

	// specification details
	@Unique @Override
	public double getAmountOfAttrJuicedUpByAmel(EntityAttribute attribute) {
		if (attribute == ReachEntityAttributes.REACH) {
			return getReachDistance((LivingEntity)(Object)this, 0);
		} else if (attribute == ReachEntityAttributes.ATTACK_RANGE) {
			return getAttackRange((LivingEntity)(Object)this, 0);
		}
		return this.juicedUpVals.getCustomInstance(attribute).getBaseValue();
	}

	@Unique @Override
	public void setAmountOfAttrJuicedUpByAmel(EntityAttribute attribute, double value) {
		setAmountOfAttrJuicedUpByAmel(attribute, value, false);
	}
	@Unique
	public void setAmountOfAttrJuicedUpByAmel(
		EntityAttribute attribute,
		double value,
		boolean readingNbt
	) {

		EntityAttributeInstance juicedAttrInst = this.juicedUpVals.getCustomInstance(attribute);
		EntityAttributeInstance attrInst = attributes.getCustomInstance(attribute);

		if (attrInst != null && !readingNbt) {
			LOGGER.info("attrInst was not null!");
			attrInst.setBaseValue(attrInst.getBaseValue() - juicedAttrInst.getBaseValue() + value);
		}
		juicedAttrInst.setBaseValue(value);

	}

	@Unique @Override
	public void setAllJuicedUpAttrsToZero() {
		this.juicedUpVals.getAttributesToSend().forEach(
			(EntityAttributeInstance inst) -> {
				inst.setBaseValue(0);
			}
		);
	}

	@Unique @Override
	public AttributeContainer getLapisworksAttributes() { return this.juicedUpVals; }
	@Unique @Override
	public void setLapisworksAttributes(AttributeContainer attributes) {
		setLapisworksAttributes(attributes, false);
	}
	@Unique
	public void setLapisworksAttributes(AttributeContainer attributes, boolean readingNbt) {
		setAmountOfAttrJuicedUpByAmel(
			EntityAttributes.GENERIC_ATTACK_DAMAGE,
			attributes.getBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE),
			readingNbt
		);
		setAmountOfAttrJuicedUpByAmel(
			EntityAttributes.GENERIC_MAX_HEALTH,
			attributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH),
			readingNbt
		);
		setAmountOfAttrJuicedUpByAmel(
			EntityAttributes.GENERIC_MOVEMENT_SPEED,
			attributes.getBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED),
			readingNbt
		);
	}

	@Unique @Override
	public int getEnchant(int whatEnchant) {
		expandEnchantmentsIfNeeded(whatEnchant);
		return this.enchantments.get(whatEnchant);
	}

	@Unique @Override
	public void setEnchantmentLevel(int whatEnchant, int level) {
		this.expandEnchantmentsIfNeeded(whatEnchant);
		this.enchantments.set(whatEnchant, level);
	}

	// still not DRYer than your dms
	@Unique @Override
	public void incrementEnchant(int whatEnchant) { this.incrementEnchant(whatEnchant, 1); }
	@Unique @Override
	public void incrementEnchant(int whatEnchant, int amount) {
		this.setEnchantmentLevel(
			whatEnchant,
			this.getEnchant(whatEnchant) + amount
		);
	}
	@Unique @Override
	public void decrementEnchant(int whatEnchant) { this.incrementEnchant(whatEnchant, -1); }
	@Unique @Override
	public void decrementEnchant(int whatEnchant, int amount) { this.incrementEnchant(whatEnchant, -amount); }

	@Unique @Override
	public List<Integer> getEnchantments() {
		return List.copyOf(this.enchantments);
	}

	@Unique @Override
	public int[] getEnchantmentsArray() {
		return this.enchantments.stream().mapToInt(Integer::intValue).toArray();
	}

	@Unique @Override
	public void setEnchantments(int[] levels) {
		for (int i = 0; i < levels.length && i < this.enchantments.size(); i++) {
			this.enchantments.set(i, levels[i]);
		}
	}

	@Unique @Override
	public void setAllEnchantsToZero() {
		for (int i = 0; i < this.enchantments.size(); i++) { this.enchantments.set(i, 0); }
	}

	// may be changed in the future, idk.
	@Unique @Override
	public void copyCrossDeath(ServerPlayerEntity oldplr) {}

	@Unique @Override
	public void copyCrossDimensional(ServerPlayerEntity oldplr) {
		LapisworksInterface old = (LapisworksInterface)oldplr;
		this.setLapisworksAttributes(old.getLapisworksAttributes());
		this.setEnchantments(old.getEnchantmentsArray());
	}


	@Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
	public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		if (attributes != null && this.getWorld() != null && !this.getWorld().isClient) {

			setLapisworksAttributes(
				new AttributeContainer(DefaultAttributeContainer.builder()
					.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, nbt.getDouble("LAPISWORKS_JUICED_FISTS"))
					.add(EntityAttributes.GENERIC_MAX_HEALTH, nbt.getDouble("LAPISWORKS_JUICED_SKIN"))
					.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, nbt.getDouble("LAPISWORKS_JUICED_FEET"))
					.build()),
				true
			);

			attributes.addTemporaryModifiers(
				nbt.getBoolean("LAPISWORKS_JUICED_REACH") ?
					ImmutableMultimap.of(
						REACH, MoarReachYouBitch.REACH_MODIFIER,
						ATTACK_RANGE, MoarReachYouBitch.ATTACK_REACH_MODIFIER
					) : ImmutableMultimap.of()
			);

		}
		setEnchantments(nbt.getIntArray("LAPISWORKS_ENCHANTMENTS"));
	}

	@Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
	public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		AttributeContainer attrs = juicedUpVals;
		nbt.putDouble("LAPISWORKS_JUICED_FISTS", attrs.getBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
		nbt.putDouble("LAPISWORKS_JUICED_SKIN", attrs.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH));
		nbt.putDouble("LAPISWORKS_JUICED_FEET", attrs.getBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
		nbt.putIntArray("LAPISWORKS_ENCHANTMENTS", getEnchantments());
		nbt.putBoolean(
			"LAPISWORKS_JUICED_REACH",
			attributes.hasModifierForAttribute(REACH, REACH_ENHANCEMENT_UUID)
		);
	}

	// not sure i even need this
	@Inject(at = @At("HEAD"), method = "onDeath")
	public void onDeath(DamageSource damageSource, CallbackInfo ci) {
		this.setAllJuicedUpAttrsToZero();
		this.setAllEnchantsToZero();
	}

	@Inject(at = @At("HEAD"), method = "onAttacking")
	public void onAttacking(Entity target, CallbackInfo ci) {

		if (!(target instanceof LivingEntity) || target.getWorld().isClient)
			return;
		if (this.getEnchant(AllEnchantments.fireyFists) == 1)
			((LivingEntity)target).setOnFireFor(3);

		int lightningbendingLevel = this.getEnchant(AllEnchantments.lightningBending);
		ServerWorld world = (ServerWorld)target.getWorld();
		Vec3d targetPos = target.getPos();

		if (
			(lightningbendingLevel == 1 && world.isThundering()) ||
			(lightningbendingLevel == 2 && (world.isRaining() || world.isRaining())) ||
			lightningbendingLevel == 3
		) {
			LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
			lightning.setPos(targetPos.x, targetPos.y, targetPos.z);
			world.tryLoadEntity(lightning);
		}
	}

	@WrapMethod(method = {"computeFallDamage"})
	public int computeFallDamage(float fallDistance, float damageMultiplier, Operation<Integer> og) {
		return og.call(
			Math.max(
				fallDistance - 20 * this.getEnchant(AllEnchantments.fallDmgRes),
				0
			),
			damageMultiplier
		);
	}

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
