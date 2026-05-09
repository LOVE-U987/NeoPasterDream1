
package net.pasterdream.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.init.PasterdreamModGameRules;
import net.pasterdream.procedures.ShadowSwordPr0Procedure;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ShadowSwordItem extends SwordItem {
	public ShadowSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 1771;
			}

			public float getSpeed() {
				return 0f;
			}

			public float getAttackDamageBonus() {
				return 8f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 10;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModItems.NIGHTMARE_FUEL.get()));
			}
		}, 3, -2.4f, new Item.Properties().fireResistant());
	}
    static UUID uuid = UUID.fromString("54616704-a99f-4956-ae68-03331f65e0d2");
    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(getDefaultAttributeModifiers(slot));
        if(slot == EquipmentSlot.MAINHAND)
        {
            builder.put(PasterdreamModAttributes.SAN_VARIABILITY.get(),new AttributeModifier(uuid,"pasterdream.shadowsword.san_variability",-3.6, AttributeModifier.Operation.ADDITION));
            double san = stack.getOrCreateTag().getDouble("san");
            if(san > 0.0)
            {
                builder.put(Attributes.ATTACK_SPEED,new AttributeModifier(uuid,"pasterdream.shadowsword.attack_speed",0.5*(1.0 - san/100.0), AttributeModifier.Operation.MULTIPLY_BASE));
                builder.put(Attributes.ATTACK_DAMAGE,new AttributeModifier(uuid,"pasterdream.shadowsword.attack_damage",0.75*(1.0 - san/100.0), AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }
        return builder.build();
    }
	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
        if(SanCapability.IsSanCheckSystem())
        {
            list.add(Component.translatable("item.pasterdream.shadow_sword.hovertext.is_san"));
        }
		list.add(Component.translatable("item.pasterdream.shadow_sword.hovertext.describe"));
	}

	@Override
	public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
		boolean retval = super.onEntitySwing(itemstack, entity);
		ShadowSwordPr0Procedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
		return retval;
	}

    @Override
    public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);
        if(!world.isClientSide)
        {
            if (selected && SanCapability.IsSanCheckSystem())
            {
                double old_san = itemstack.getOrCreateTag().getDouble("san");
                double new_san = entity.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(SanCapability::getSanValue).orElse(100.0);
                if(Math.abs(new_san - old_san) > 1)
                {
                    itemstack.getOrCreateTag().putDouble("san", entity.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(SanCapability::getSanValue).orElse(100.0));
                }
            }
            else
            {
                itemstack.getOrCreateTag().remove("san");
            }
        }
    }

}
