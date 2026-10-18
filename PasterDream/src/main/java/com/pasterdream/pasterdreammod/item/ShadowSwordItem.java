package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.api.san.SanConfigRegistry;
import com.pasterdream.pasterdreammod.api.san.SanHelper;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 暗影剑 (shadow_sword) — 精神值联动武器
 * <p>
 * 还原自原版 ShadowSwordItem + ShadowSwordPr0Procedure + SanCapability：
 * <ul>
 *   <li>剑属性与既有 ItemAPI 注册逐项一致（耐久 1771、攻击加成 11、攻速 -2.4、附魔 10），
 *       修复材料梦魇燃料，按原版补 fireResistant；挖速按项目口径取 2（原版 0，对剑无实际影响）</li>
 *   <li>精神值联动（原版 getAttributeModifiers + inventoryTick）：手持且理智系统开启时，
 *       玩家 SAN_VARIABILITY -3.6，并按物品同步的精神值加成 攻速 +0.5×(1-san/100)、
 *       攻击 +0.75×(1-san/100)（精神越低越强，MULTIPLY_BASE 语义）；1.21.1 属性组件
 *       系统下原版覆写不可靠，改用瞬时属性修饰符 + tick 维护（与 PDSanityHelper 环境
 *       修饰符同模式），收剑/系统关闭时移除</li>
 *   <li>挥砍门控（原版 Pr0，仅玩家）：拥有"暗影之才"成就或创造模式 → 播放 shadow_sword
 *       音效；否则聊天栏提示"未选则<暗影> 无法使用此剑"并受虚空伤害（剩余 1 血）
 *       与短暂黑暗；无论如何给战技标签物品 40 × 冷却属性；理智系统开启且精神值 ≤0 时
 *       额外受魔法反噬（剩余 1 血）</li>
 * </ul>
 */
public class ShadowSwordItem extends SwordItem {

    /** 物品精神值同步键（原版 NBT san） */
    private static final String KEY_SAN = "san";

    /** SAN_VARIABILITY 修饰符 ID（手持 -3.6） */
    private static final ResourceLocation SAN_VAR_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "shadowsword_san_variability");

    /** 攻速乘算修饰符 ID（按精神值动态） */
    private static final ResourceLocation ATTACK_SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "shadowsword_attack_speed");

    /** 攻击伤害乘算修饰符 ID（按精神值动态） */
    private static final ResourceLocation ATTACK_DAMAGE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "shadowsword_attack_damage");

    /** 手持 SAN_VARIABILITY 减量，原版 -3.6 */
    private static final double SAN_VARIABILITY_REDUCTION = -3.6;

    /** 攻速加成上限系数，原版 0.5×(1-san/100) */
    private static final double ATTACK_SPEED_BONUS = 0.5;

    /** 攻击伤害加成上限系数，原版 0.75×(1-san/100) */
    private static final double ATTACK_DAMAGE_BONUS = 0.75;

    /** 精神值同步阈值：变化超过该值才写物品（原版 >1） */
    private static final double SAN_SYNC_THRESHOLD = 1;

    /** 战技冷却基数（tick），原版 40 × SKILLCD = 2 秒 */
    private static final int SKILL_BASE_COOLDOWN_TICKS = 40;

    /** 未达成成就时自伤后的保底血量（原版公式 health-1） */
    private static final double MIN_HEALTH_AFTER_BACKLASH = 1;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为梦魇燃料） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1771, 2.0f, 11.0f, 10,
            () -> Ingredient.of(PDItems.NIGHTMARE_FUEL.get()));

    public ShadowSwordItem(Properties properties) {
        // 原版 fireResistant；攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.fireResistant().attributes(SwordItem.createAttributes(TIER, 0, -2.4f)));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide() || !(entity instanceof Player player)) {
            return;
        }
        // 原版 inventoryTick：手持且理智系统开启时同步精神值并挂属性修饰符，否则清干净
        if (isSelected && SanHelper.isSanCheckEnabled(player)) {
            double newSan = SanHelper.getSan(player).sanValue();
            double oldSan = PasterItemData.getDouble(stack, KEY_SAN);
            if (Math.abs(newSan - oldSan) > SAN_SYNC_THRESHOLD) {
                PasterItemData.putDouble(stack, KEY_SAN, newSan);
            }
            applySanModifiers(player, newSan);
        } else {
            PasterItemData.update(stack, tag -> tag.remove(KEY_SAN));
            removeSanModifiers(player);
        }
    }

    /**
     * 按当前精神值维护手持属性修饰符（原版 getAttributeModifiers 的 tick 等价实现）。
     * <p>
     * SAN_VARIABILITY 无条件 -3.6；精神值 >0 时按原版公式挂攻速/攻击乘算修饰符
     * （精神越低加成越高）；精神值 ≤0 时移除乘算修饰符（原版 san>0 才加）。
     * 全部为瞬时修饰符（不写入存档属性映射），重复刷新前先移除同 ID 旧值。
     *
     * @param player 手持玩家
     * @param san    当前精神值
     */
    private static void applySanModifiers(Player player, double san) {
        AttributeInstance sanVar = player.getAttribute(PDAttributes.SAN_VARIABILITY);
        if (sanVar != null) {
            sanVar.removeModifier(SAN_VAR_MODIFIER_ID);
            sanVar.addTransientModifier(new AttributeModifier(SAN_VAR_MODIFIER_ID,
                    SAN_VARIABILITY_REDUCTION, AttributeModifier.Operation.ADD_VALUE));
        }
        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed != null) {
            attackSpeed.removeModifier(ATTACK_SPEED_MODIFIER_ID);
            if (san > 0) {
                attackSpeed.addTransientModifier(new AttributeModifier(ATTACK_SPEED_MODIFIER_ID,
                        ATTACK_SPEED_BONUS * (1.0 - san / 100.0),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.removeModifier(ATTACK_DAMAGE_MODIFIER_ID);
            if (san > 0) {
                attackDamage.addTransientModifier(new AttributeModifier(ATTACK_DAMAGE_MODIFIER_ID,
                        ATTACK_DAMAGE_BONUS * (1.0 - san / 100.0),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    /**
     * 移除本剑施加的全部瞬时属性修饰符（收剑/理智系统关闭时调用）。
     *
     * @param player 手持玩家
     */
    private static void removeSanModifiers(Player player) {
        AttributeInstance sanVar = player.getAttribute(PDAttributes.SAN_VARIABILITY);
        if (sanVar != null) {
            sanVar.removeModifier(SAN_VAR_MODIFIER_ID);
        }
        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed != null) {
            attackSpeed.removeModifier(ATTACK_SPEED_MODIFIER_ID);
        }
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.removeModifier(ATTACK_DAMAGE_MODIFIER_ID);
        }
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        boolean result = super.onEntitySwing(stack, entity, hand);
        // 原版 ShadowSwordPr0Procedure：挥砍门控（仅玩家）
        if (entity instanceof ServerPlayer player && !entity.level().isClientSide()) {
            applySwingGate(entity.level(), player);
        }
        return result;
    }

    /**
     * 原版 ShadowSwordPr0Procedure：挥砍门控与反噬
     * <p>
     * 拥有"暗影之才"成就（achievement_talent_shadow）或创造模式：播放 shadow_sword 音效；
     * 否则：聊天栏提示"未选则<暗影> 无法使用此剑"、受虚空伤害（剩余 1 血）、
     * 获得 3 秒黑暗。无论是否达成成就，均给战技标签物品施加 40 × 冷却属性的冷却；
     * 理智系统开启且精神值 ≤0 时额外受魔法反噬（剩余 1 血）。
     *
     * @param level  世界
     * @param player 挥砍的玩家（服务端）
     */
    private static void applySwingGate(Level level, ServerPlayer player) {
        boolean allowed = (player instanceof ServerPlayer sp && PDAdvancements.has(sp, PDAdvancements.TALENT_SHADOW))
                || player.getAbilities().instabuild;
        BlockPos pos = BlockPos.containing(player.getX(), player.getY(), player.getZ());
        if (allowed) {
            level.playSound(null, pos, PDSounds.SHADOW_SWORD.get(), SoundSource.PLAYERS, 1f, 1f);
        } else {
            // 原版聊天栏提示（literal → 语言键）
            player.displayClientMessage(Component.translatable("message.pasterdream.shadow_sword.no_talent"), false);
            player.hurt(level.damageSources().fellOutOfWorld(),
                    (float) Math.max(0, player.getHealth() - MIN_HEALTH_AFTER_BACKLASH));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));
        }
        WeaponSkillHelper.applySkillCooldown(player, SKILL_BASE_COOLDOWN_TICKS);
        // 精神值耗尽反噬：魔法伤害至剩 1 血
        if (SanHelper.isSanCheckEnabled(player) && SanHelper.getSan(player).sanValue() <= 0) {
            player.hurt(level.damageSources().magic(),
                    (float) Math.max(0, player.getHealth() - MIN_HEALTH_AFTER_BACKLASH));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText：理智系统开启时追加被动说明行
        // （原版条件为 GameRule，tooltip 上下文无玩家，改用理智配置开关近似）
        if (Boolean.TRUE.equals(SanConfigRegistry.get().enabled().get())) {
            tooltip.add(Component.translatable("item.pasterdream.shadow_sword.hovertext.is_san"));
        }
        tooltip.add(Component.translatable("item.pasterdream.shadow_sword.hovertext.describe"));
    }
}
