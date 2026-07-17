package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDArmorMaterials;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Qym盔甲物品类
 * 提供套装效果：穿上全套Qym盔甲时获得多种强力效果
 */
public class QymArmorItem extends ArmorItem {

    public QymArmorItem(ArmorItem.Type type) {
        super(PDArmorMaterials.QIN_ARMOR, type, new Properties());
    }

    private void checkAndApplySetEffect(LivingEntity entity) {
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = entity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);

        boolean hasFullSet = helmet.getItem() == PDItems.QIN_ARMOR_HELMET.get()
                && chestplate.getItem() == PDItems.QIN_ARMOR_CHESTPLATE.get()
                && leggings.getItem() == PDItems.QIN_ARMOR_LEGGINGS.get()
                && boots.getItem() == PDItems.QIN_ARMOR_BOOTS.get();

        if (hasFullSet) {
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.REGENERATION, 10, 3, false, false));
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 10, 3, false, false));
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 10, 0, false, false));
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.JUMP, 10, 3, false, false));
        } else {
            entity.removeEffect(net.minecraft.world.effect.MobEffects.REGENERATION);
            entity.removeEffect(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE);
            entity.removeEffect(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE);
            entity.removeEffect(net.minecraft.world.effect.MobEffects.JUMP);
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