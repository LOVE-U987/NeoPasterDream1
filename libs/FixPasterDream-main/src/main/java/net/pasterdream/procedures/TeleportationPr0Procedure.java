package net.pasterdream.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

public class TeleportationPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(PasterdreamModMobEffects.RESTRAINMOVE_BLOCK_BUFF.get())) == false) {
			if ((entity instanceof Player _plr ? _plr.getAbilities().instabuild : false) == false) {
				if ((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) > 6 && (entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(PasterdreamModMobEffects.TELEPORTATION_BUFF.get())) == false) {
					TeleportationPr1Procedure.execute(entity);
					if (entity instanceof Player _player)
						_player.causeFoodExhaustion((float) (3 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCONSUME.get()).getValue()));
					if ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem() == Items.ELYTRA
							&& (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(PasterdreamModItems.BOBOJI_CURIO.get(), lv).isPresent() : false == false)) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TELEPORTATION_BUFF.get(), (int) (100 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get()).getValue()), 0, false, false));
					} else {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TELEPORTATION_BUFF.get(), (int) (50 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.TELEPORTATIONCD.get()).getValue()), 0, false, false));
					}
					TeleportationPr2Procedure.execute(entity);
				}
			} else {
				TeleportationPr1Procedure.execute(entity);
				TeleportationPr2Procedure.execute(entity);
			}
			entity.getPersistentData().putBoolean("evasion", true);
		}
	}
}
