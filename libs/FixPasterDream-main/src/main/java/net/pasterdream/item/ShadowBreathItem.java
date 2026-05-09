
package net.pasterdream.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.pasterdream.capability.SanCapability;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;


import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;

public class ShadowBreathItem extends Item implements ICurioItem {
	public ShadowBreathItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7b\u7CBE\u826F \u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5728\u7CBE\u795E\u503C\u5206\u522B\u4F4E\u4E8E60/40/20\u65F6"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u589E\u52A01/2/4\u70B9\u653B\u51FB\u529B"));
	}
    static String name = "pasterdream.shadowbreath.level";
	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
        Entity entity = slotContext.entity();
        if(entity instanceof ServerPlayer pl && pl.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).isPresent())
        {
            pl.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).ifPresent( cap ->{
                double san = cap.getSanValue();
                if(san <= 20) {
                    stack.getOrCreateTag().putInt(name,3);
                }else if(san <= 40) {
                    stack.getOrCreateTag().putInt(name,2);
                }else if(san <= 60) {
                    stack.getOrCreateTag().putInt(name,1);
                }
                else{
                    stack.getOrCreateTag().remove(name);
                }
            });
        }
        else
        {
            stack.getOrCreateTag().remove(name);
        }
	}

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        int num = stack.getOrCreateTag().getInt("name");
        HashMultimap<Attribute, AttributeModifier> map = HashMultimap.create();
        if(num > 0)
        {
            map.put(Attributes.ATTACK_DAMAGE,new AttributeModifier(uuid,"pasterdream.shadowbreath",1 << (num - 1), AttributeModifier.Operation.ADDITION));
        }
        return map;
    }
}
