package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDArmorMaterials;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

/**
 * 潜声盔甲物品类
 * 提供套装效果：穿上全套潜声盔甲时获得隐身效果
 */
public class SculkArmorItem extends ArmorItem {

    /**
     * 构造潜声盔甲物品
     * @param type 盔甲类型（头盔/胸甲/护腿/靴子）
     */
    public SculkArmorItem(ArmorItem.Type type) {
        super(PDArmorMaterials.SCULK_ARMOR, type, new Properties());
    }

    /**
     * 检查并应用套装效果
     * 穿上全套潜声盔甲时，玩家获得隐身效果
     * @param entity 穿戴盔甲的生物实体
     */
    private void checkAndApplySetEffect(LivingEntity entity) {
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = entity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);

        boolean hasFullSet = helmet.getItem() == PDItems.SCULK_ARMOR_HELMET.get()
                && chestplate.getItem() == PDItems.SCULK_ARMOR_CHESTPLATE.get()
                && leggings.getItem() == PDItems.SCULK_ARMOR_LEGGINGS.get()
                && boots.getItem() == PDItems.SCULK_ARMOR_BOOTS.get();

        // C2-2 修复：禁止 removeEffect，避免剥掉药水/信标等外来同名 buff；
        // 仅满套时短时效刷新，脱套后效果自然过期（见 2026-08-04-C2-review.md）
        if (hasFullSet) {
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.INVISIBILITY, 10, 0, false, false));
        }
    }

    @Override
    public void inventoryTick(ItemStack itemstack, net.minecraft.world.level.Level world, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);
        if (!world.isClientSide() && entity instanceof LivingEntity livingEntity) {
            // C2-1 修复：护甲槽归属守卫，仅当该件实际穿在对应护甲槽才触发套装检查，
            // 避免背包持有（主背包/副手）每 tick 触发导致剥外来 buff（见 2026-08-04-C2-1修复报告.md）
            if (livingEntity.getItemBySlot(this.getType().getSlot()) != itemstack) {
                return;
            }
            checkAndApplySetEffect(livingEntity);
        }
    }
}