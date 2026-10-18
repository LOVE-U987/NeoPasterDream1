package com.pasterdream.pasterdreammod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 沙漠剑 (desert_sword) — 代价型被动
 * <p>
 * 对应原版 {@code DesertSwordItem}（属性与新模组既有 ItemAPI 注册逐项一致：
 * 耐久 1561 / 挖速 2 / 伤害+10 / 攻速 -3.1 / 附魔 8）。
 * 手持时每 tick 刷新缓速与抗性（原版 DesertSwordPr0Procedure：移速 -15%、
 * 抗性 +20%，短时长无粒子——高攻速高伤的代价型平衡）。
 * 修复材料为空（原版不可铁砧修复）。
 */
public class DesertSwordItem extends SwordItem {

    /** 被动刷新时长（tick），原版 2 tick（短时长持续刷新，不显示粒子） */
    private static final int PASSIVE_REFRESH_TICKS = 2;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为空） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1561, 2.0f, 10.0f, 8,
            () -> Ingredient.EMPTY);

    public DesertSwordItem(Properties properties) {
        // 攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.attributes(SwordItem.createAttributes(TIER, 0, -3.1f)));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        // 原版 DesertSwordPr0Procedure：手持时刷新缓速+抗性（ambient/visible 均 false，无粒子）
        if (isSelected && entity instanceof LivingEntity living && !level.isClientSide()) {
            applyDesertPassive(living);
        }
    }

    /**
     * 沙漠被动：刷新缓速与抗性（无粒子短时长）。
     * 包内静态方法，供真沙漠剑（true_desert_sword）被动复用。
     *
     * @param living 手持者
     */
    static void applyDesertPassive(LivingEntity living) {
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                PASSIVE_REFRESH_TICKS, 0, false, false));
        living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,
                PASSIVE_REFRESH_TICKS, 0, false, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.desert_sword.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.desert_sword.passive_buff"));
    }
}
