package com.pasterdream.pasterdreammod.entity;

import com.pasterdream.pasterdreammod.api.entity.tag.EntityTag;
import com.pasterdream.pasterdreammod.api.entity.tag.EntityTagRegistry;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * 实体标签初始化配置类
 * <p>
 * 集中为模组内实体绑定 {@link EntityTag} 内置标签，替代散落在各实体类中的硬编码逻辑。
 * 在模组初始化阶段调用 {@link #setup()} 完成一次性批量注册。
 * <p>
 * 当前配置：
 * <ul>
 *   <li><b>灯影怪物</b>：暗影主题敌对生物，互相之间不会造成友伤</li>
 *   <li><b>法术无敌实体</b>：治疗/狂暴法术立场，加入世界后自动无敌</li>
 * </ul>
 *
 * @see EntityTagEvents
 * @see EntityTag
 * @see EntityTagRegistry
 */
public final class EntityTagSetup {

    private EntityTagSetup() {
        throw new UnsupportedOperationException("EntityTagSetup 是纯静态配置类，不可实例化");
    }

    /**
     * 配置所有实体标签绑定
     * <p>
     * 应在模组主类构造器或 {@code FMLCommonSetupEvent} 中调用，确保实体类型已注册完毕。
     */
    public static void setup() {
        setupLampShadowMonsters();
        setupSpellInvincibles();
    }

    /**
     * 绑定灯影怪物标签
     * <p>
     * 包含暗影系列敌对生物：暗影魔像、暗影幽灵、暗影尖啸幽灵、暗影之手、暗影调和图腾。
     * 这些怪物在相互攻击时不会造成伤害，避免 AoE 或误伤导致内耗。
     */
    private static void setupLampShadowMonsters() {
        EntityTagRegistry.registerAll(EntityTag.LAMP_SHADOW_MONSTER,
                PDEntities.SHADOW_GOLEM.get(),
                PDEntities.SHADOW_GHOST.get(),
                PDEntities.SHADOW_SQUEAL_GHOST.get(),
                PDEntities.SHADOW_SQUEAL_GHOST_0.get(),
                PDEntities.SHADOW_HAND.get(),
                PDEntities.SHADOW_TUNE_TOTEM.get()
        );
    }

    /**
     * 绑定法术无敌标签
     * <p>
     * 包含治疗法术立场与狂暴法术立场，两者均为短生命周期的展示/效果实体，不应被任何伤害清除。
     */
    private static void setupSpellInvincibles() {
        // 通过 BuiltInRegistries 动态查找 PasterDreamSpells 注册的立场实体
        EntityTagRegistry.registerAll(EntityTag.SPELL_INVINCIBLE,
                BuiltInRegistries.ENTITY_TYPE.getOptional(
                        ResourceLocation.fromNamespaceAndPath("pasterdreamspells", "fury_spell_entity")).orElse(null),
                BuiltInRegistries.ENTITY_TYPE.getOptional(
                        ResourceLocation.fromNamespaceAndPath("pasterdreamspells", "healing_spell_entity")).orElse(null)
        );
    }
}
