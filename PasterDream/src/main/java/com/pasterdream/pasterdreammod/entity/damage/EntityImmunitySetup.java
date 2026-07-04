package com.pasterdream.pasterdreammod.entity.damage;

import com.pasterdream.pasterdreammod.registry.PDEntities;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.Set;

/**
 * 实体伤害免疫配置集中管理类
 * <p>
 * 所有实体的伤害免疫规则在此类中集中配置，替代原先散布在 27 个实体类中的重复 {@code hurt()} 逻辑。
 * <p>
 * 在模组初始化阶段（如 {@code FMLCommonSetupEvent}）调用 {@link #setupAllImmunities()} 即可。
 * <p>
 * 配置方式：
 * <ul>
 *   <li>{@link #configurePreset(EntityType, Preset)} — 使用预定义的免疫组合</li>
 *   <li>{@link #configureImmunity(EntityType, Set)} — 自定义免疫集合</li>
 *   <li>{@link DamageImmunityConfig.Preset#getImmunities()} — 获取预设中的免疫类型集合</li>
 * </ul>
 */
public class EntityImmunitySetup {

    /**
     * 配置所有实体的伤害免疫规则
     * <p>
     * 在模组初始化时调用（例如 {@code FMLCommonSetupEvent} 中）。
     */
    public static void setupAllImmunities() {
        DamageImmunityConfig config = ConfigurableImmunityEntity.getImmunityConfig();

        // ==================== 完全免疫 ====================
        // 狐火 —— 环境装饰实体，免疫几乎所有伤害
        config.configurePreset(PDEntities.FOX_FIRE.get(),
                DamageImmunityConfig.Preset.FULL_IMMUNITY);

        // ==================== 元素免疫 + Wither ====================
        // 暗影调和图腾 —— 40血大型敌对，免疫火焰/摔落/凋零/魔法
        config.configurePresets(PDEntities.SHADOW_TUNE_TOTEM.get(),
                DamageImmunityConfig.Preset.ELEMENTAL_IMMUNITY,
                DamageImmunityConfig.Preset.WITHER_IMMUNITY);

        // ==================== 自定义完全免疫（含 PLAYER_EXPLOSION） ====================
        // 融梦水晶 —— 漂浮装饰实体，免疫几乎所有伤害（含玩家爆炸）
        config.configureImmunity(PDEntities.MELTDREAM_CRYSTAL.get(), Set.of(
                DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.LAVA,
                DamageTypes.ARROW, DamageTypes.PLAYER_ATTACK, DamageTypes.INDIRECT_MAGIC,
                DamageTypes.FALL, DamageTypes.CACTUS, DamageTypes.DROWN,
                DamageTypes.LIGHTNING_BOLT, DamageTypes.EXPLOSION,
                DamageTypes.PLAYER_EXPLOSION, DamageTypes.TRIDENT,
                DamageTypes.FALLING_ANVIL, DamageTypes.DRAGON_BREATH,
                DamageTypes.WITHER, DamageTypes.WITHER_SKULL
        ));

        // ==================== 完整免疫（不含 PLAYER_EXPLOSION） ====================
        // 震动水晶 —— 50血静止敌对，免疫火焰/箭矢/玩家攻击/摔落/魔法等
        config.configureImmunity(PDEntities.SHAKING_CRYSTAL.get(), Set.of(
                DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.LAVA,
                DamageTypes.ARROW, DamageTypes.THROWN, DamageTypes.PLAYER_ATTACK,
                DamageTypes.INDIRECT_MAGIC, DamageTypes.FALL, DamageTypes.CACTUS,
                DamageTypes.DROWN, DamageTypes.LIGHTNING_BOLT, DamageTypes.EXPLOSION,
                DamageTypes.TRIDENT, DamageTypes.FALLING_ANVIL, DamageTypes.DRAGON_BREATH,
                DamageTypes.WITHER, DamageTypes.WITHER_SKULL
        ));

        // ==================== 暗影魔像（物理免疫） ====================
        // 注意：ShadowGolemEntity 重写了 hurt() 包含技能加速逻辑，伤害免疫仍通过配置生效
        config.configureImmunity(PDEntities.SHADOW_GOLEM.get(), Set.of(
                DamageTypes.ARROW, DamageTypes.THROWN,
                DamageTypes.INDIRECT_MAGIC, DamageTypes.FALL, DamageTypes.CACTUS
        ));

        // ==================== 孢子实体（部分免疫） ====================
        // 1血飞行孢子，免疫摔落和仙人掌，箭矢伤害在原逻辑中单独处理
        config.configureImmunity(PDEntities.SPORE_ENTITY.get(), Set.of(
                DamageTypes.FALL, DamageTypes.CACTUS
        ));

        // ==================== 亚伦柯斯左手 BOSS（火焰免疫） ====================
        // 500HP 飞行 BOSS，免疫火焰伤害
        config.configureImmunity(PDEntities.AARONCOS_LEFTHAND_0.get(), Set.of(
                DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.LAVA
        ));

        // ==================== 亚伦柯斯右手 BOSS（火焰免疫） ====================
        // 500HP 飞行 BOSS，免疫火焰伤害
        config.configureImmunity(PDEntities.AARONCOS_RIGHTHAND_0.get(), Set.of(
                DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.LAVA
        ));

        // ==================== 暗影魔法弹（环境伤害免疫） ====================
        // 飞行投射物实体，免疫火焰/摔落/仙人掌/溺水/闪电/凋零
        // 注：药水弹/药水云免疫在实体类中单独处理（基于实体类型判断）
        config.configureImmunity(PDEntities.SHADOW_MAGICBALL.get(), Set.of(
                DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.LAVA,
                DamageTypes.FALL, DamageTypes.CACTUS, DamageTypes.DROWN,
                DamageTypes.LIGHTNING_BOLT, DamageTypes.WITHER, DamageTypes.WITHER_SKULL
        ));
    }
}
