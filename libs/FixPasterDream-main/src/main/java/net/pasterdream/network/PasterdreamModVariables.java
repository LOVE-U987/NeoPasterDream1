package net.pasterdream.network;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PasterdreamModVariables {
	public static AttributeModifier paster_attack_damage = null;
	public static double paster_attack_damage_number = 0;
	public static ItemStack weapon_workshop_item = ItemStack.EMPTY;
	public static AttributeModifier paster_movement_speed = null;
	public static double paster_movement_speed_number = 0;
	public static AttributeModifier paster_reach_distance = null;
	public static AttributeModifier paster_swim_speed = null;
	public static ItemStack shadow_blast_furnace = ItemStack.EMPTY;
	public static AttributeModifier paster_attack_speed = null;
	public static double paster_attack_speed_number = 0;
	public static AttributeModifier paster_enchantment_atkspd = null;
	public static double paster_enchantment_atkspd_number = 0;
	public static ItemStack deep_treasure = ItemStack.EMPTY;
	public static AttributeModifier paster_luck = null;
	public static double paster_luck_number = 0;

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
	}
}
