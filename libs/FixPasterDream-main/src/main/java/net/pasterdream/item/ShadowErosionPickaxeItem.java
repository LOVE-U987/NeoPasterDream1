
package net.pasterdream.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ShadowErosionPickaxeItem extends PickaxeItem {
	public ShadowErosionPickaxeItem() {
		super(new Tier() {
			public int getUses() {
				return 1725;
			}

			public float getSpeed() {
				return 13f;
			}

			public float getAttackDamageBonus() {
				return 4f;
			}

			public int getLevel() {
				return 4;
			}

			public int getEnchantmentValue() {
				return 16;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModItems.BLACKMETAL_INGOT.get()));
			}
		}, 1, -2.8f, new Item.Properties());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
	}

    UUID uuid = UUID.fromString("4af23950-0cc5-4115-b92f-e14fd1b95e17");
    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(getDefaultAttributeModifiers(slot));
        if(slot == EquipmentSlot.MAINHAND) builder.put(PasterdreamModAttributes.SAN_VARIABILITY.get(),new AttributeModifier(uuid,"pasterdream.shadow_erosion_axe.san_variability",-1.2, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }
}
