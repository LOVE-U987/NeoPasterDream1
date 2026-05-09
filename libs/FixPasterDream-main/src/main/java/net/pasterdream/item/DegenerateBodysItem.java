
package net.pasterdream.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.pasterdream.init.PasterdreamModTabs;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;

public class DegenerateBodysItem extends Item implements ICurioItem {
	public DegenerateBodysItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	public static final UUID HEALTH_UUID = UUID.fromString("5253b2de-f6fa-4b10-b1fb-42a2de2be49b");
	public static final AttributeModifier modifier = new AttributeModifier(HEALTH_UUID, "generic.maxHealth", -4.0F, AttributeModifier.Operation.ADDITION);
	public static final UUID ENTITY_REACH_UUID = UUID.fromString("b84e7f10-74e3-43f5-95f9-968877248549");
	public static final AttributeModifier modifier0 = new AttributeModifier(ENTITY_REACH_UUID, "generic.entityReach", 0.2F, AttributeModifier.Operation.ADDITION);
	public static final UUID BLOCK_REACH_UUID = UUID.fromString("f55792ba-17c1-43bb-86ac-ff805d06ce3c");
	public static final AttributeModifier modifier1 = new AttributeModifier(BLOCK_REACH_UUID, "generic.blockReach", 1.0F, AttributeModifier.Operation.ADDITION);
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("4b798e40-aac3-43a2-b93b-c927ec3a2c59");
    public static final AttributeModifier modifier2 = new AttributeModifier(ATTACK_DAMAGE_UUID, "generic.attack_damage", 2.0F, AttributeModifier.Operation.ADDITION);
	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7d\u53F2\u8BD7 \u2605\u2605\u2605\u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A7f\u6761\u4EF6:\u6697\u5F71\u4EC6\u4ECE"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u7CBE\u795E\u503C\u5F520\u65F6\u4E0D\u518D\u635F\u5931\u751F\u547D\u503C"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u53EF\u4EE5\u4F7F\u7528\u6697\u5F71\u6CD5\u672F"));
		list.add(Component.literal("\u00A77\u00A7o-- \u6211\u5C06\u6210\u4E3A\u4F60\u7684\u9634\u5F71 \u4E00\u5E76\u4F53\u4F1A\u4F60\u7684\u75DB\u82E6"));
    }
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if(slotContext.entity() != null)
        {
            return CuriosApi.getCuriosInventory(slotContext.entity()).map(handler ->
                            handler.findFirstCurio(stack.getItem()).isEmpty())
                    .orElse(true);
        }
        return true;
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                        UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributeModifiers = HashMultimap.create();
        attributeModifiers.put(Attributes.MAX_HEALTH,modifier);
        attributeModifiers.put(ForgeMod.ENTITY_REACH.get(),modifier0);
        attributeModifiers.put(ForgeMod.BLOCK_REACH.get(),modifier1);
        attributeModifiers.put(Attributes.ATTACK_DAMAGE,modifier2);
        return attributeModifiers;
    }


}