package com.pasterdream.pasterdreammod.entity.damage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 伤害免疫配置类 —— 统一管理所有实体的伤害免疫规则
 * <p>
 * 核心思路：
 * <ul>
 *   <li>将散布在 27 个实体类中的重复 {@code hurt()} 逻辑集中到此处</li>
 *   <li>通过 {@link #configureImmunity(EntityType, Set)} 为每个实体配置免疫规则</li>
 *   <li>通过 {@link #isImmune(Entity, DamageSource)} 统一查询</li>
 * </ul>
 * <p>
 * 预定义的常用免疫组合参见 {@link Preset}。
 * <p>
 * 使用方式：
 * <ul>
 *   <li>{@link #getInstance()} 获取全局单例实例</li>
 *   <li>继承 {@link ConfigurableImmunityEntity} 的实体自动获得免疫功能</li>
 *   <li>非 {@link ConfigurableImmunityEntity} 的实体（如投射物）可直接调用 {@link #isImmune(Entity, DamageSource)} 进行检查</li>
 * </ul>
 */
public class DamageImmunityConfig {

    /** 全局单例实例 —— 供所有实体共享配置 */
    private static final DamageImmunityConfig INSTANCE = new DamageImmunityConfig();

    /** 预定义的常用免疫规则组合 */
    public enum Preset {
        /** 完全免疫 —— 免疫除虚空外的几乎所有伤害类型 */
        FULL_IMMUNITY(Set.of(
                DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.LAVA,
                DamageTypes.ARROW, DamageTypes.PLAYER_ATTACK, DamageTypes.FALL,
                DamageTypes.CACTUS, DamageTypes.DROWN, DamageTypes.LIGHTNING_BOLT,
                DamageTypes.EXPLOSION, DamageTypes.TRIDENT, DamageTypes.FALLING_ANVIL,
                DamageTypes.DRAGON_BREATH, DamageTypes.WITHER, DamageTypes.WITHER_SKULL,
                DamageTypes.THROWN, DamageTypes.INDIRECT_MAGIC
        )),

        /** 元素免疫 —— 免疫火焰、岩浆、溺水、闪电 */
        ELEMENTAL_IMMUNITY(Set.of(
                DamageTypes.IN_FIRE, DamageTypes.ON_FIRE, DamageTypes.LAVA,
                DamageTypes.DROWN, DamageTypes.LIGHTNING_BOLT
        )),

        /** 物理免疫 —— 免疫箭矢、玩家攻击、摔落、仙人掌、三叉戟、铁砧 */
        PHYSICAL_IMMUNITY(Set.of(
                DamageTypes.ARROW, DamageTypes.PLAYER_ATTACK, DamageTypes.FALL,
                DamageTypes.CACTUS, DamageTypes.TRIDENT, DamageTypes.FALLING_ANVIL
        )),

        /** 凋零免疫 —— 免疫凋零和凋零骷髅伤害 */
        WITHER_IMMUNITY(Set.of(
                DamageTypes.WITHER, DamageTypes.WITHER_SKULL
        )),

        /** 无免疫 */
        NONE(Set.of());

        private final Set<ResourceKey<DamageType>> immunities;

        Preset(Set<ResourceKey<DamageType>> immunities) {
            this.immunities = immunities;
        }

        public Set<ResourceKey<DamageType>> getImmunities() {
            return immunities;
        }
    }

    /** 实体特定的免疫规则映射 —— EntityType → 免疫的伤害类型集合 */
    private final Map<EntityType<?>, Set<ResourceKey<DamageType>>> entityImmunities = new HashMap<>();

    /**
     * 为指定实体配置自定义免疫规则
     *
     * @param entityType 实体类型
     * @param immunities 该实体免疫的伤害类型集合
     */
    public void configureImmunity(EntityType<?> entityType, Set<ResourceKey<DamageType>> immunities) {
        entityImmunities.put(entityType, immunities);
    }

    /**
     * 为指定实体配置预设免疫规则组合
     *
     * @param entityType 实体类型
     * @param preset     预设免疫规则
     */
    public void configurePreset(EntityType<?> entityType, Preset preset) {
        entityImmunities.put(entityType, preset.getImmunities());
    }

    /**
     * 为指定实体配置多个预设免疫规则组合的并集
     *
     * @param entityType 实体类型
     * @param presets    预设免疫规则序列
     */
    public void configurePresets(EntityType<?> entityType, Preset... presets) {
        Set<ResourceKey<DamageType>> merged = new java.util.HashSet<>();
        for (Preset p : presets) {
            merged.addAll(p.getImmunities());
        }
        entityImmunities.put(entityType, merged);
    }

    /**
     * 检查实体是否对给定的伤害来源免疫
     *
     * @param entity 受伤害的实体
     * @param source 伤害来源
     * @return 如果该实体类型的免疫配置中包含此伤害类型，返回 {@code true}
     */
    public boolean isImmune(Entity entity, DamageSource source) {
        Set<ResourceKey<DamageType>> immunities = entityImmunities.get(entity.getType());
        if (immunities == null || immunities.isEmpty()) {
            return false;
        }
        for (ResourceKey<DamageType> damageType : immunities) {
            if (source.is(damageType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有已配置的实体免疫规则（不可变视图）
     *
     * @return EntityType → 免疫伤害类型集合 的映射
     */
    public Map<EntityType<?>, Set<ResourceKey<DamageType>>> getConfiguredImmunities() {
        return Map.copyOf(entityImmunities);
    }

    /**
     * 获取全局单例实例
     * <p>
     * 供所有实体共享同一个配置实例，确保配置的一致性。
     *
     * @return 全局 {@link DamageImmunityConfig} 单例实例
     */
    public static DamageImmunityConfig getInstance() {
        return INSTANCE;
    }
}
