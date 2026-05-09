package net.pasterdream.world;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.init.PasterdreamModItems;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID)
public class ArmorHelper {
    @SubscribeEvent
    public static void onEquipChange(LivingEquipmentChangeEvent event)
    {
        LivingEntity entity = event.getEntity();
        if(entity instanceof ServerPlayer pl)
        {
            if(event.getSlot() == EquipmentSlot.LEGS)
            {
                //qym_armor_leggings
                boolean flag_from = event.getFrom().getItem() == PasterdreamModItems.QYM_ARMOR_LEGGINGS.get();//原先装备
                boolean flag_to = event.getTo().getItem() == PasterdreamModItems.QYM_ARMOR_LEGGINGS.get();//现在装备
                if(flag_from != flag_to)
                {
                    MeltDreamEnergyCapability.setPlayerMeltDreamEnergyNoNeedConsume(pl,flag_to);
                }
            }
            if(event.getSlot() == EquipmentSlot.CHEST)
            {
                //qym_armor_chestplate
                boolean flag_from = event.getFrom().getItem() == PasterdreamModItems.QYM_ARMOR_CHESTPLATE.get();//原先装备
                boolean flag_to = event.getTo().getItem() == PasterdreamModItems.QYM_ARMOR_CHESTPLATE.get();//现在装备
                if(flag_from != flag_to && !pl.isCreative())
                {
                    pl.getAbilities().mayfly = flag_to;
                    pl.onUpdateAbilities();
                }
            }
        }
    }
}
