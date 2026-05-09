
package net.pasterdream.item;

import net.pasterdream.procedures.SculkArmorPr0Procedure;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

import com.google.common.collect.Iterables;

public abstract class SculkArmorItem extends ArmorItem {
	public SculkArmorItem(ArmorItem.Type type, Item.Properties properties) {
		super(new ArmorMaterial() {
			@Override
			public int getDurabilityForType(ArmorItem.Type type) {
				return new int[]{13, 15, 16, 11}[type.getSlot().getIndex()] * 42;
			}

			@Override
			public int getDefenseForType(ArmorItem.Type type) {
				return new int[]{3, 6, 8, 3}[type.getSlot().getIndex()];
			}

			@Override
			public int getEnchantmentValue() {
				return 9;
			}

			@Override
			public SoundEvent getEquipSound() {
				return SoundEvents.EMPTY;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModItems.SCULK_HEART.get()));
			}

			@Override
			public String getName() {
				return "sculk_armor";
			}

			@Override
			public float getToughness() {
				return 3.5f;
			}

			@Override
			public float getKnockbackResistance() {
				return 0.15f;
			}
		}, type, properties);
	}

	public static class Helmet extends SculkArmorItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties().fireResistant());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A72\u5E7D\u533F\u56DE\u54CD"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u6700\u5927\u751F\u547D\u503C+4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u8EAB\u5904\u6DF1\u5C42\u65F6\u6548\u679C\u589E\u5F3A"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u514D\u75AB\u5E7D\u533F\u7684\u9ED1\u6697\u6050\u60E7"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u5982\u8981\u53D7\u5230\u9ED1\u6697\u6548\u679C\u65F6\u63D0\u9AD8\u79FB\u901F\u548C\u514D\u4F24"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/hui_xiang___layer_1.png";
		}

		@Override
		public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
			return false;
		}

		@Override
		public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
			super.inventoryTick(itemstack, world, entity, slot, selected);
			if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
				SculkArmorPr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity);
			}
		}
	}

	public static class Chestplate extends SculkArmorItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A72\u5E7D\u533F\u56DE\u54CD"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u6700\u5927\u751F\u547D\u503C+4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u8EAB\u5904\u6DF1\u5C42\u65F6\u6548\u679C\u589E\u5F3A"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u514D\u75AB\u5E7D\u533F\u7684\u9ED1\u6697\u6050\u60E7"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u5982\u8981\u53D7\u5230\u9ED1\u6697\u6548\u679C\u65F6\u63D0\u9AD8\u79FB\u901F\u548C\u514D\u4F24"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/hui_xiang___layer_1.png";
		}

		@Override
		public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
			return false;
		}
	}

	public static class Leggings extends SculkArmorItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A72\u5E7D\u533F\u56DE\u54CD"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u6700\u5927\u751F\u547D\u503C+4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u8EAB\u5904\u6DF1\u5C42\u65F6\u6548\u679C\u589E\u5F3A"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u514D\u75AB\u5E7D\u533F\u7684\u9ED1\u6697\u6050\u60E7"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u5982\u8981\u53D7\u5230\u9ED1\u6697\u6548\u679C\u65F6\u63D0\u9AD8\u79FB\u901F\u548C\u514D\u4F24"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/hui_xiang___layer_2.png";
		}

		@Override
		public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
			return false;
		}
	}

	public static class Boots extends SculkArmorItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties().fireResistant());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A72\u5E7D\u533F\u56DE\u54CD"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u6700\u5927\u751F\u547D\u503C+4"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u8EAB\u5904\u6DF1\u5C42\u65F6\u6548\u679C\u589E\u5F3A"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u514D\u75AB\u5E7D\u533F\u7684\u9ED1\u6697\u6050\u60E7"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u5982\u8981\u53D7\u5230\u9ED1\u6697\u6548\u679C\u65F6\u63D0\u9AD8\u79FB\u901F\u548C\u514D\u4F24"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/hui_xiang___layer_1.png";
		}

		@Override
		public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
			return false;
		}
	}
}
