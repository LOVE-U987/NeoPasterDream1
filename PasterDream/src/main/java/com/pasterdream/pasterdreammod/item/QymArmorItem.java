package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.api.san.SanHelper;
import com.pasterdream.pasterdreammod.registry.PDArmorMaterials;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Qym盔甲物品类（qin_armor_*，保留注册名 qym/qin 混用历史）。
 * <p>
 * 还原原版 {@code QymArmorPr0Procedure} 套装逻辑：
 * <ul>
 *   <li>仅头盔在 inventoryTick 触发检查（单点，避免每件护甲 ×4 重复执行）；</li>
 *   <li>服务端玩家 San 值持续回满至 100（原版 setPlayerSanWithCheck 语义）；</li>
 *   <li>同时装备靴+腿+胸甲时（头盔为触发源）：
 *       染梦世界/灯影世界 → 回避 buff（20 tick，255 级）；
 *       其他维度 → 抗性提升（20 tick，3 级）。</li>
 * </ul>
 * 效果短时刷新、自然过期，不调用 removeEffect，避免剥掉其他来源的同名效果。
 */
public class QymArmorItem extends ArmorItem {

    public QymArmorItem(ArmorItem.Type type) {
        super(PDArmorMaterials.QIN_ARMOR, type, new Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("§7▪ §9装备整套时精神值始终为上限"));
        tooltip.add(Component.literal("§7▪ §9梦境中获得 80% 伤害减免"));
        tooltip.add(Component.literal("§7▪ §9身处梦境时可以回避任何伤害"));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level.isClientSide() || !(entity instanceof LivingEntity living)) {
            return;
        }
        // 仅头盔作为套装检查触发点，避免四件护甲每 tick 各执行一次
        if (this.getType() != ArmorItem.Type.HELMET) {
            return;
        }
        // 原版 QymArmorPr0Procedure：精神值持续回满
        if (entity instanceof Player player) {
            SanHelper.setPlayerSanWithCheck(player, 100);
        }
        applySetEffect(living);
    }

    /** 三件套（靴+腿+胸甲）+ 维度条件的效果分发（原版 QymArmorPr0Procedure 数值一致） */
    private void applySetEffect(LivingEntity entity) {
        boolean setWorn = entity.getItemBySlot(EquipmentSlot.FEET).getItem() == PDItems.QIN_ARMOR_BOOTS.get()
                && entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == PDItems.QIN_ARMOR_LEGGINGS.get()
                && entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == PDItems.QIN_ARMOR_CHESTPLATE.get();
        if (!setWorn) {
            return;
        }
        Level level = entity.level();
        if (level.dimension() == PDDimensions.DYEDREAM_WORLD_LEVEL_KEY
                || level.dimension() == PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY) {
            // 梦境：回避任何伤害（原版 20 tick + 255 级）
            entity.addEffect(new MobEffectInstance(PDEffects.EVASION_BUFF.holder(), 20, 255, false, false));
        } else {
            // 其他维度：80% 伤害减免（抗性 IV 为 80%，原版 3 级 + 20 tick）
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 3, false, false));
        }
    }
}
