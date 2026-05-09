package net.pasterdream.procedures;

import net.pasterdream.network.PasterdreamModVariables;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;

public class WorkshopGrindRecipe0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.DREAM_WAND_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.DREAM_WAND.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.SHADOW_EROSION_SWORD_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.SHADOW_EROSION_SWORD.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.SHADOW_EROSION_PICKAXE_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.SHADOW_EROSION_PICKAXE.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.SHADOW_EROSION_AXE_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.SHADOW_EROSION_AXE.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.SHADOW_EROSION_SHOVEL_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.SHADOW_EROSION_SHOVEL.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.SHADOW_EROSION_HOE_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.SHADOW_EROSION_HOE.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.TERRASWORD_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.TERRA_SWORD.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.WHITE_SWORD_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.WHITE_SWORD.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.SHADOW_SWORD_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.SHADOW_SWORD.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.STAR_WISH_ROD_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.STAR_WISH_ROD.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.getItem() == PasterdreamModItems.ICESHADOW_HAMMER_EMBRYO.get()) {
			if (entity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.ICESHADOW_HAMMER.get());
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
		}
		WorkshopNbtcopy0Procedure.execute(entity);
	}
}
