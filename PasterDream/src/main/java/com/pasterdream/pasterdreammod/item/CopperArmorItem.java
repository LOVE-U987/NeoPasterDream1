package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

/**
 * 铜盔甲物品类
 * 提供套装效果：穿上全套铜盔甲时获得挖掘疲劳抗性
 */
public class CopperArmorItem extends ArmorItem {

    /**
     * 构造铜盔甲物品
     * @param type 盔甲类型（头盔/胸甲/护腿/靴子）
     */
    public CopperArmorItem(ArmorItem.Type type) {
        super(PDArmorMaterials.COPPER_ARMOR, type, new Properties());
    }

    /**
     * 检查并应用套装效果
     * 穿上全套铜盔甲时，玩家对挖掘疲劳效果免疫
     * @param entity 穿戴盔甲的生物实体
     */
    private void checkAndApplySetEffect(LivingEntity entity) {
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = entity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);

        boolean hasFullSet = helmet.getItem() == PDItems.COPPER_ARMOR_HELMET.get()
                && chestplate.getItem() == PDItems.COPPER_ARMOR_CHESTPLATE.get()
                && leggings.getItem() == PDItems.COPPER_ARMOR_LEGGINGS.get()
                && boots.getItem() == PDItems.COPPER_ARMOR_BOOTS.get();

        if (hasFullSet) {
            entity.removeEffect(net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN);
        }
    }

    @Override
    public void inventoryTick(ItemStack itemstack, net.minecraft.world.level.Level world, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);
        if (!world.isClientSide() && entity instanceof LivingEntity livingEntity) {
            checkAndApplySetEffect(livingEntity);
        }
    }
}