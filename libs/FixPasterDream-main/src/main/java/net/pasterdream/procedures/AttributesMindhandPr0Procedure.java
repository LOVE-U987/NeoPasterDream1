package net.pasterdream.procedures;

import net.pasterdream.network.PasterdreamModVariables;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModEnchantments;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

import java.util.UUID;

@Mod.EventBusSubscriber
public class AttributesMindhandPr0Procedure {
	@SubscribeEvent
	public static void addAttributeModifier(ItemAttributeModifierEvent event) {
		execute(event, event.getItemStack());
	}

	public static void execute(ItemStack itemstack) {
		execute(null, itemstack);
	}

	private static void execute(@Nullable Event event, ItemStack itemstack) {
		if (itemstack.is(ItemTags.create(new ResourceLocation("pasterdream:paster_weapon"))) || itemstack.is(ItemTags.create(new ResourceLocation("pasterdream:paster_tool")))) {
			if (itemstack.getOrCreateTag().getBoolean("paster_attack_damage_number") == true) {
				if (event instanceof ItemAttributeModifierEvent _event && _event.getSlotType() == EquipmentSlot.MAINHAND) {
					PasterdreamModVariables.paster_attack_damage_number = itemstack.getOrCreateTag().getDouble("paster_attack_damage_number");
					PasterdreamModVariables.paster_attack_damage = new AttributeModifier(UUID.fromString("ba20fc3f-7e63-4f54-ac00-7bdbe07c231b"), "paster_attack_damage", (PasterdreamModVariables.paster_attack_damage_number * 0.1),
							AttributeModifier.Operation.ADDITION);
					_event.addModifier(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, PasterdreamModVariables.paster_attack_damage);
				}
			}
			if (itemstack.getOrCreateTag().getBoolean("paster_attack_speed_number") == true) {
				if (event instanceof ItemAttributeModifierEvent _event && _event.getSlotType() == EquipmentSlot.MAINHAND) {
					PasterdreamModVariables.paster_attack_speed_number = itemstack.getOrCreateTag().getDouble("paster_attack_speed_number");
					PasterdreamModVariables.paster_attack_speed = new AttributeModifier(UUID.fromString("0a7297cb-dc03-4b9b-9de6-500450e0f655"), "paster_attack_speed", (PasterdreamModVariables.paster_attack_speed_number * 0.01),
							AttributeModifier.Operation.ADDITION);
					_event.addModifier(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, PasterdreamModVariables.paster_attack_speed);
				}
			}
			if (itemstack.getOrCreateTag().getBoolean("paster_movement_speed_number") == true) {
				if (event instanceof ItemAttributeModifierEvent _event && _event.getSlotType() == EquipmentSlot.MAINHAND) {
					PasterdreamModVariables.paster_movement_speed_number = itemstack.getOrCreateTag().getDouble("paster_movement_speed_number");
					PasterdreamModVariables.paster_movement_speed = new AttributeModifier(UUID.fromString("ca7dd05d-0a01-479d-b143-e69811fd76ba"), "paster_movement_speed", (PasterdreamModVariables.paster_movement_speed_number * 0.001),
							AttributeModifier.Operation.ADDITION);
					_event.addModifier(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, PasterdreamModVariables.paster_movement_speed);
				}
			}
			if (itemstack.getOrCreateTag().getBoolean("paster_luck_number") == true) {
				if (event instanceof ItemAttributeModifierEvent _event && _event.getSlotType() == EquipmentSlot.MAINHAND) {
					PasterdreamModVariables.paster_luck_number = itemstack.getOrCreateTag().getDouble("paster_luck_number");
					PasterdreamModVariables.paster_luck = new AttributeModifier(UUID.fromString("b6e20f8a-699c-45c4-8dc6-e0374c480243"), "paster_luck", (PasterdreamModVariables.paster_luck_number * 0.001), AttributeModifier.Operation.ADDITION);
					_event.addModifier(PasterdreamModAttributes.LUCK.get(), PasterdreamModVariables.paster_luck);
				}
			}
		}
		if (itemstack.is(ItemTags.create(new ResourceLocation("pasterdream:shadow_erosion_tool")))) {
			if (event instanceof ItemAttributeModifierEvent _event && _event.getSlotType() == EquipmentSlot.MAINHAND) {
				PasterdreamModVariables.paster_reach_distance = new AttributeModifier(UUID.fromString("28029167-e5ad-4793-b114-22e68749aab5"), "paster_reach_distance", 0.5, AttributeModifier.Operation.ADDITION);
				_event.addModifier(ForgeMod.BLOCK_REACH.get(), PasterdreamModVariables.paster_reach_distance);
			}
		}
		if (itemstack.getItem() == PasterdreamModItems.THERMAL_DAGGER.get()) {
			if (event instanceof ItemAttributeModifierEvent _event && _event.getSlotType() == EquipmentSlot.MAINHAND) {
				PasterdreamModVariables.paster_swim_speed = new AttributeModifier(UUID.fromString("ac285f47-aa8d-4bfb-9ba2-b0d37c503bab"), "paster_swim_speed", 0.5, AttributeModifier.Operation.ADDITION);
				_event.addModifier(ForgeMod.SWIM_SPEED.get(), PasterdreamModVariables.paster_swim_speed);
			}
		}
		/*if (itemstack.getItem() == PasterdreamModItems.SHADOW_SWORD.get()) {
			if (event instanceof ItemAttributeModifierEvent _event && _event.getSlotType() == EquipmentSlot.MAINHAND) {
				PasterdreamModVariables.paster_attack_speed_number = itemstack.getOrCreateTag().getDouble("san");
				PasterdreamModVariables.paster_attack_speed = new AttributeModifier(UUID.fromString("b903dcc1-d025-407b-b994-6fea809928d4"), "paster_attack_speed", ((100 - PasterdreamModVariables.paster_attack_speed_number) * 0.008),
						AttributeModifier.Operation.ADDITION);
				_event.addModifier(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, PasterdreamModVariables.paster_attack_speed);
			}
		}*/
		if (EnchantmentHelper.getItemEnchantmentLevel(PasterdreamModEnchantments.ENCHANTMENT_ATKSPD.get(), itemstack) != 0) {
			if (event instanceof ItemAttributeModifierEvent _event && _event.getSlotType() == EquipmentSlot.MAINHAND) {
				PasterdreamModVariables.paster_enchantment_atkspd_number = itemstack.getEnchantmentLevel(PasterdreamModEnchantments.ENCHANTMENT_ATKSPD.get());
				PasterdreamModVariables.paster_enchantment_atkspd = new AttributeModifier(UUID.fromString("f1474901-2dee-429a-8296-5b253e06b747"), "paster_attack_speed", (0.02 + PasterdreamModVariables.paster_enchantment_atkspd_number * 0.02),
						AttributeModifier.Operation.ADDITION);
				_event.addModifier(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, PasterdreamModVariables.paster_enchantment_atkspd);
			}
		}
	}
}
