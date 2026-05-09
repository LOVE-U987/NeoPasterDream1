package net.pasterdream.procedures;

import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.entity.WhiteSwordRainProjectileEntity;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicReference;

public class WhiteSwordPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Player entity) {
		if (entity == null)
			return;
		if (entity.getAbilities().instabuild || entity instanceof ServerPlayer _plr1 && _plr1.level() instanceof ServerLevel
				&& _plr1.getAdvancements().getOrStartProgress(_plr1.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_talent_light"))).isDone()) {
			if (MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(entity,0.1)) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:white_sword_rain")), SoundSource.PLAYERS, (float) 0.7, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:white_sword_rain")), SoundSource.PLAYERS, (float) 0.7, 1, false);
					}
				}
                entity.swing(InteractionHand.MAIN_HAND, true);

				{
					AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
					entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
					if (_iitemhandlerref.get() != null) {
						for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
							ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
							if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:skill")))) {
                                entity.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (84 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get()).getValue()));
							}
						}
					}
				}
				PasterdreamMod.queueServerWork(1, () -> {
					PasterdreamMod.queueServerWork(8, () -> {
						for (int index0 = 0; index0 < 6; index0++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
						for (int index1 = 0; index1 < 4; index1++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
					});
					PasterdreamMod.queueServerWork(11, () -> {
						for (int index2 = 0; index2 < 6; index2++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
						for (int index3 = 0; index3 < 4; index3++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
					});
					PasterdreamMod.queueServerWork(14, () -> {
						for (int index4 = 0; index4 < 6; index4++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
						for (int index5 = 0; index5 < 4; index5++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1);
							} else {
								_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1, false);
							}
						}
					});
					PasterdreamMod.queueServerWork(17, () -> {
						for (int index6 = 0; index6 < 5; index6++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
						for (int index7 = 0; index7 < 4; index7++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1);
							} else {
								_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1, false);
							}
						}
					});
					PasterdreamMod.queueServerWork(20, () -> {
						for (int index8 = 0; index8 < 5; index8++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
						for (int index9 = 0; index9 < 3; index9++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1);
							} else {
								_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1, false);
							}
						}
					});
					PasterdreamMod.queueServerWork(23, () -> {
						for (int index10 = 0; index10 < 4; index10++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -3.5, 3.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
						for (int index11 = 0; index11 < 3; index11++) {
							if (world instanceof ServerLevel projectileLevel) {
								Projectile _entityToSpawn = new Object() {
									public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
										AbstractArrow entityToSpawn = new WhiteSwordRainProjectileEntity(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
										entityToSpawn.setOwner(shooter);
										entityToSpawn.setBaseDamage(damage);
										entityToSpawn.setKnockback(knockback);
										entityToSpawn.setSilent(true);
										entityToSpawn.setPierceLevel(piercing);
										return entityToSpawn;
									}
								}.getArrow(projectileLevel, entity, (float) (3 + 0.4 * ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue()), 1, (byte) 1);
								_entityToSpawn.setPos(
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)),
										(y + Mth.nextDouble(RandomSource.create(), 13, 15)),
										(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(9)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
												+ Mth.nextDouble(RandomSource.create(), -2.5, 2.5)));
								_entityToSpawn.shoot(0, (-1), 0, 1, (float) 0.1);
								projectileLevel.addFreshEntity(_entityToSpawn);
							}
						}
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1);
							} else {
								_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1, false);
							}
						}
					});
					PasterdreamMod.queueServerWork(26, () -> {
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1);
							} else {
								_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1, false);
							}
						}
					});
					PasterdreamMod.queueServerWork(29, () -> {
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1);
							} else {
								_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_cluster.place")), SoundSource.PLAYERS, 2, 1, false);
							}
						}
					});
				});
			} else {
				if (!entity.level().isClientSide())
                    entity.displayClientMessage(Component.literal("\u878D\u68A6\u80FD\u91CF\u4E0D\u8DB3"), false);
			}
		} else {
			if (!entity.level().isClientSide())
                entity.displayClientMessage(Component.literal("\u672A\u9009\u5219<\u660E\u706F> \u65E0\u6CD5\u4F7F\u7528\u6B64\u5251"), false);
		}
	}
}
