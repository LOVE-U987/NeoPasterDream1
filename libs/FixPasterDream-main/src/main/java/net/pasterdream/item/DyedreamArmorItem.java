
package net.pasterdream.item;

import net.pasterdream.procedures.DyedreamArmorPr0Procedure;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.List;

import com.google.common.collect.Iterables;

public abstract class DyedreamArmorItem extends ArmorItem {
	public DyedreamArmorItem(ArmorItem.Type type, Item.Properties properties) {
		super(new ArmorMaterial() {
			@Override
			public int getDurabilityForType(ArmorItem.Type type) {
				return new int[]{13, 15, 16, 11}[type.getSlot().getIndex()] * 40;
			}

			@Override
			public int getDefenseForType(ArmorItem.Type type) {
				return new int[]{3, 6, 8, 3}[type.getSlot().getIndex()];
			}

			@Override
			public int getEnchantmentValue() {
				return 22;
			}

			@Override
			public SoundEvent getEquipSound() {
				return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.armor.equip_diamond"));
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModItems.DYEDREAM_INGOT.get()));
			}

			@Override
			public String getName() {
				return "dyedream_armor";
			}

			@Override
			public float getToughness() {
				return 3f;
			}

			@Override
			public float getKnockbackResistance() {
				return 0.1f;
			}
		}, type, properties);
	}

	public static class Helmet extends DyedreamArmorItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A7d\u67D3\u68A6\u5B88\u62A4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u6700\u5927\u751F\u547D\u503C+4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u968F\u65F6\u95F4\u6D41\u901D\u83B7\u5F97\u62A4\u76FE"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u589E\u5F3A\u624B\u6301\u7684\u67D3\u68A6\u5DE5\u5177"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/ran_meng___layer_1.png";
		}

		@Override
		public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
			super.inventoryTick(itemstack, world, entity, slot, selected);
			if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
				DyedreamArmorPr0Procedure.execute(entity);
			}
		}
	}

	public static class Chestplate extends DyedreamArmorItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A7d\u67D3\u68A6\u5B88\u62A4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u6700\u5927\u751F\u547D\u503C+4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u968F\u65F6\u95F4\u6D41\u901D\u83B7\u5F97\u62A4\u76FE"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u589E\u5F3A\u624B\u6301\u7684\u67D3\u68A6\u5DE5\u5177"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/ran_meng___layer_1.png";
		}
	}

	public static class Leggings extends DyedreamArmorItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A7d\u67D3\u68A6\u5B88\u62A4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u6700\u5927\u751F\u547D\u503C+4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u968F\u65F6\u95F4\u6D41\u901D\u83B7\u5F97\u62A4\u76FE"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u589E\u5F3A\u624B\u6301\u7684\u67D3\u68A6\u5DE5\u5177"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/ran_meng___layer_2.png";
		}
	}

	public static class Boots extends DyedreamArmorItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A7d\u67D3\u68A6\u5B88\u62A4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u6700\u5927\u751F\u547D\u503C+4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u968F\u65F6\u95F4\u6D41\u901D\u83B7\u5F97\u62A4\u76FE"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u589E\u5F3A\u624B\u6301\u7684\u67D3\u68A6\u5DE5\u5177"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/ran_meng___layer_1.png";
		}
	}
}
