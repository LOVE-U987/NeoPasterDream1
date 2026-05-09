
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

public class AllkindsRingItem extends Item implements ICurioItem {
	public AllkindsRingItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	public static final UUID HEALTH_UUID = UUID.fromString("0673e4be-d230-11ed-8917-325096b39f47");
	public static final AttributeModifier modifier = new AttributeModifier(HEALTH_UUID, "generic.maxHealth", 4.0F, AttributeModifier.Operation.ADDITION);
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("0673e536-d230-11ed-8130-325096b39f47");
	public static final AttributeModifier modifier0 = new AttributeModifier(ATTACK_DAMAGE_UUID, "generic.attackDamage", 2.0F, AttributeModifier.Operation.ADDITION);
	public static final UUID ATTACK_SPEED_UUID = UUID.fromString("0673e5a4-d230-11ed-8bb0-325096b39f47");
	public static final AttributeModifier modifier1 = new AttributeModifier(ATTACK_SPEED_UUID, "generic.attackSpeed", 0.1F, AttributeModifier.Operation.ADDITION);
	public static final UUID ENTITY_REACH_UUID = UUID.fromString("0673e608-d230-11ed-8948-325096b39f47");
	public static final AttributeModifier modifier2 = new AttributeModifier(ENTITY_REACH_UUID, "generic.entityReach", 0.2F, AttributeModifier.Operation.ADDITION);
	public static final UUID BLOCK_REACH_UUID = UUID.fromString("0673e66c-d230-11ed-aa81-325096b39f47");
	public static final AttributeModifier modifier3 = new AttributeModifier(BLOCK_REACH_UUID, "generic.blockReach", 0.5F, AttributeModifier.Operation.ADDITION);
	public static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("0673e6c6-d230-11ed-9244-325096b39f47");
	public static final AttributeModifier modifier4 = new AttributeModifier(MOVEMENT_SPEED_UUID, "generic.movementSpeed", 0.05F, AttributeModifier.Operation.MULTIPLY_BASE);
	public static final UUID SKILLCD_UUID = UUID.fromString("0673e734-d230-11ed-8007-325096b39f47");
	public static final AttributeModifier modifier5 = new AttributeModifier(SKILLCD_UUID, "pasterdream.skillCd", -0.05F, AttributeModifier.Operation.ADDITION);
	public static final UUID SKILLMULTIPLIER_UUID = UUID.fromString("0673e798-d230-11ed-b8cb-325096b39f47");
	public static final AttributeModifier modifier6 = new AttributeModifier(SKILLMULTIPLIER_UUID, "pasterdream.skillMultiplier", 0.05F, AttributeModifier.Operation.ADDITION);
	public static final UUID TELEPORTATIONCD_UUID = UUID.fromString("54d5eebc-06cc-4ada-87a5-c04247da2506");
	public static final AttributeModifier modifier7 = new AttributeModifier(TELEPORTATIONCD_UUID, "pasterdream.teleportationCd", -0.05F, AttributeModifier.Operation.ADDITION);

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7c\u4F20\u8BF4 \u2605\u2605\u2605\u2605\u2605\u2605\u2605"));
        list.add(Component.literal("\u00A77\u00A7o\u805A\u4E07\u8C61\u4E4B\u529B \u94F8\u4EE5\u6B64\u6212"));
        list.add(Component.literal("\u00A77\u00A7o-- \u65AF\u5353\u8D1D\u8389\u25AA\u9EDB\u6B27\u6069"));
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
        attributeModifiers.put(Attributes.ATTACK_DAMAGE,modifier0);
        attributeModifiers.put(Attributes.ATTACK_SPEED,modifier1);
        attributeModifiers.put(ForgeMod.ENTITY_REACH.get(),modifier2);
        attributeModifiers.put(ForgeMod.BLOCK_REACH.get(),modifier3);
        attributeModifiers.put(Attributes.MOVEMENT_SPEED,modifier4);
        attributeModifiers.put(PasterdreamModAttributes.SKILLCD.get(),modifier5);
        attributeModifiers.put(PasterdreamModAttributes.SKILLMULTIPLIER.get(),modifier6);
        attributeModifiers.put(PasterdreamModAttributes.TELEPORTATIONCD.get(),modifier7);
        return attributeModifiers;
    }
}