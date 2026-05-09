
package net.pasterdream.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.procedures.QymArmorPr3Procedure;
import net.pasterdream.procedures.QymArmorPr1Procedure;
import net.pasterdream.procedures.QymArmorPr0Procedure;
import net.pasterdream.init.PasterdreamModItems;

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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;

public abstract class QymArmorItem extends ArmorItem {
	public QymArmorItem(ArmorItem.Type type, Item.Properties properties) {
		super(new ArmorMaterial() {
			@Override
			public int getDurabilityForType(ArmorItem.Type type) {
				return new int[]{13, 15, 16, 11}[type.getSlot().getIndex()] * 0;
			}

			@Override
			public int getDefenseForType(ArmorItem.Type type) {
				return new int[]{2, 10, 10, 10}[type.getSlot().getIndex()];
			}

			@Override
			public int getEnchantmentValue() {
				return 99;
			}

			@Override
			public SoundEvent getEquipSound() {
				return SoundEvents.EMPTY;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModItems.MEMORY_GEM_0.get()));
			}

			@Override
			public String getName() {
				return "qym_armor";
			}

			@Override
			public float getToughness() {
				return 10f;
			}

			@Override
			public float getKnockbackResistance() {
				return 1f;
			}
		}, type, properties);
	}
    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(getDefaultAttributeModifiers(slot));
        if(slot == this.type.getSlot()) builder.put(PasterdreamModAttributes.SAN_VARIABILITY.get(),new AttributeModifier(UUID.nameUUIDFromBytes(("pasterdream.qym_armor."+slot.getName()).getBytes()),"pasterdream.qym_armor.san_variability",-1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return builder.build();
    }
	public static class Helmet extends QymArmorItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties().fireResistant());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A76\u795E\u8FF9 \u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605"));
			list.add(Component.literal("\u00A77\u25AA \u00A79\u7CBE\u795E\u503C\u59CB\u7EC8\u4E3A\u7CBE\u795E\u503C\u4E0A\u9650"));
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A7d\u6C89\u7720\u957F\u8679\u7684\u5C11\u5973"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u83B7\u5F9780%\u4F24\u5BB3\u51CF\u514D"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u8EAB\u5904\u68A6\u5883\u65F6\u53EF\u4EE5\u56DE\u907F\u4EFB\u4F55\u4F24\u5BB3"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/qym__layer_1.png";
		}

		@Override
		public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
			super.inventoryTick(itemstack, world, entity, slot, selected);
			if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
				QymArmorPr0Procedure.execute(entity);
			}
		}
	}

	public static class Chestplate extends QymArmorItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A76\u795E\u8FF9 \u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605"));
			list.add(Component.literal("\u00A77\u25AA \u00A79\u83B7\u5F97\u98DE\u884C\u80FD\u529B"));
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A7d\u6C89\u7720\u957F\u8679\u7684\u5C11\u5973"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u83B7\u5F9780%\u4F24\u5BB3\u51CF\u514D"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u8EAB\u5904\u68A6\u5883\u65F6\u53EF\u4EE5\u56DE\u907F\u4EFB\u4F55\u4F24\u5BB3"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/qym__layer_1.png";
		}

		@Override
		public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
			super.inventoryTick(itemstack, world, entity, slot, selected);
			if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
				QymArmorPr1Procedure.execute(entity);
			}
		}
	}

	public static class Leggings extends QymArmorItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A76\u795E\u8FF9 \u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605"));
			list.add(Component.literal("\u00A77\u25AA \u00A79\u878D\u68A6\u80FD\u91CF\u59CB\u7EC8\u4E3A\u878D\u68A6\u80FD\u91CF\u4E0A\u9650"));
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A7d\u6C89\u7720\u957F\u8679\u7684\u5C11\u5973"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u83B7\u5F9780%\u4F24\u5BB3\u51CF\u514D"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u8EAB\u5904\u68A6\u5883\u65F6\u53EF\u4EE5\u56DE\u907F\u4EFB\u4F55\u4F24\u5BB3"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/qym__layer_2.png";
		}
	}

	public static class Boots extends QymArmorItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties().fireResistant());
		}

		@Override
		public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
			super.appendHoverText(itemstack, world, list, flag);
			list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A76\u795E\u8FF9 \u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605"));
			list.add(Component.literal("\u00A77\u25AA \u00A79\u77AC\u8EAB\u672F\u4E0D\u518D\u6709\u51B7\u5374\u65F6\u95F4"));
			list.add(Component.literal("\u00A77\u5957\u88C5\u6548\u679C\uFF1A \u00A7d\u6C89\u7720\u957F\u8679\u7684\u5C11\u5973"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u83B7\u5F9780%\u4F24\u5BB3\u51CF\u514D"));
			list.add(Component.literal("\u00A7f\u25AA \u00A79\u8EAB\u5904\u68A6\u5883\u65F6\u53EF\u4EE5\u56DE\u907F\u4EFB\u4F55\u4F24\u5BB3"));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
			return "pasterdream:textures/models/armor/qym__layer_1.png";
		}

		@Override
		public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
			super.inventoryTick(itemstack, world, entity, slot, selected);
			if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
				QymArmorPr3Procedure.execute(entity);
			}
		}
	}
}
