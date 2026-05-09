
package net.pasterdream.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.pasterdream.init.PasterdreamModAttributes;

import java.util.ArrayList;
import java.util.List;


public class OppressionMobEffect extends MobEffect {
	private final static String uuid = "cd91f5ed-1634-42e7-8084-54c2108ea28b";
	public OppressionMobEffect() {
		super(MobEffectCategory.HARMFUL, 0x0);
		this.addAttributeModifier(PasterdreamModAttributes.SAN_VARIABILITY.get(), uuid, -9.6, AttributeModifier.Operation.ADDITION);
	}
    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
