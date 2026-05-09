package net.pasterdream.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.potion.MeltDreamEnergyVaryMobEffect;
import net.pasterdream.potion.SanVaryMobEffect;

import java.util.List;

public class PasterDreamFoodItem extends Item {
    final private int describe_num;
    final private boolean forced_display;
    public PasterDreamFoodItem(Properties properties,int num,boolean forced_Display) {
        super(properties);
        describe_num = num;
        forced_display = forced_Display;
    }
    public PasterDreamFoodItem(Properties properties,int num) {
        this(properties,num,false);
    }
    public PasterDreamFoodItem(Properties properties) {
        this(properties,0);
    }
    @Override
    public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
        assert this.getFoodProperties() != null;
        if(!this.getFoodProperties().getEffects().isEmpty() || forced_display)
        {
            list.add(Component.translatable("item.pasterdream.buncake.hovertext").withStyle(ChatFormatting.GRAY));
            if(!this.getFoodProperties().getEffects().isEmpty())
            {
                for(Pair<MobEffectInstance, Float> effect : this.getFoodProperties().getEffects())
                {
                    MobEffectInstance mobeffectinstance = effect.getFirst();
                    if(!SanCapability.IsSanCheckSystem() && SanCapability.checkSanEffect(mobeffectinstance.getEffect()))
                    {
                        continue;
                    }
                    MutableComponent mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
                    MobEffect mobeffect = mobeffectinstance.getEffect();
                    if(mobeffect instanceof SanVaryMobEffect || mobeffect instanceof MeltDreamEnergyVaryMobEffect)
                    {
                        mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, mobeffectinstance.getAmplifier() + 1);
                    }
                    else{
                        if (mobeffectinstance.getAmplifier() > 0) {
                            mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, mobeffectinstance.getAmplifier() <=5 ? Component.translatable("potion.potency." + mobeffectinstance.getAmplifier()):mobeffectinstance.getAmplifier() + 1);
                        }
                    }
                    if (!mobeffectinstance.endsWithin(20)) {
                        mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, 1.0F));
                    }
                    list.add(Component.literal("\u25AA ").withStyle(ChatFormatting.GRAY).append(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting())) );
                }
            }
        }
        for(int i = 0; i < describe_num; ++i)
        {
            list.add(Component.translatable(this.getDescriptionId() + ".describe."+ i).withStyle(ChatFormatting.GRAY));
        }
    }
}
