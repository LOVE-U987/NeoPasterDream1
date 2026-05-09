package net.pasterdream.procedures;

import net.pasterdream.PasterdreamMod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import java.util.List;
import java.util.Comparator;

public class SculkHeartPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof Player) {
					if (entityiterator instanceof ServerPlayer _plr1 && _plr1.level() instanceof ServerLevel
							&& _plr1.getAdvancements().getOrStartProgress(_plr1.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_start"))).isDone() && !(entityiterator instanceof ServerPlayer _plr2
									&& _plr2.level() instanceof ServerLevel && _plr2.getAdvancements().getOrStartProgress(_plr2.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_7"))).isDone())) {
						if (entityiterator instanceof ServerPlayer _player) {
							Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_7"));
							AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
							if (!_ap.isDone()) {
								for (String criteria : _ap.getRemainingCriteria())
									_player.getAdvancements().award(_adv, criteria);
							}
						}
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(
									Component.literal("\u00A75\u4F60\u773C\u524D\u7684\u4E8B\u7269\u7A81\u7136\u6A21\u7CCA\u4E86\u8D77\u6765\uFF0C\u9ED1\u6697\u7684\u8FF7\u96FE\u6162\u6162\u7684\u8986\u76D6\u4E86\u4F60\u7684\u77B3\u5B54\u3002"),
									false);
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(
									Component.literal("\u00A75\u8111\u6D77\u91CC\u95EA\u8FC7\u4E00\u4E9B\u4E0D\u5C5E\u4E8E\u81EA\u5DF1\u7684\u8BB0\u5FC6\uFF0C\u968F\u4E4B\u611F\u53D7\u5230\u4E86\u5F3A\u70C8\u7684\u75B2\u60EB\u611F\u3002"), false);
						if (entityiterator instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(
									Component.literal("\u00A75\u4F60\u56DE\u60F3\u5230\u4E86\u66FE\u7ECF\u4E5F\u6709\u8FC7\u7C7B\u4F3C\u7684\u611F\u89C9\uFF0C\u4E5F\u8BB8\u5E94\u8BE5\u8EBA\u5012\u5E8A\u4E0A\u4F11\u606F\u4E00\u4E0B..."), false);
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 140, 0, false, false));
						PasterdreamMod.queueServerWork(20, () -> {
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0, false, false));
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false));
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
						});
					}
				}
			}
		}
	}
}
