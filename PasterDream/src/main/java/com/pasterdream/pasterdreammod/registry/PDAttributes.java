package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.attribute.APIAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 自定义玩家属性兼容门面。
 * <p>
 * 自 1.21.1 重构后，属性注册已上收到 PasterDreamAPI 的 {@link APIAttributes}，
 * 本类保留为 PasterDream 主模组内部的兼容入口，所有 Holder 均代理至 API 层。
 * 挂接逻辑仍在主模组完成，确保玩家实体获得全部自定义属性。
 *
 * @see APIAttributes
 */
public class PDAttributes {

    // ==================== 战技体系 ====================

    /** 战技伤害倍率 (skillmultiplier) */
    public static final DeferredHolder<Attribute, Attribute> SKILLMULTIPLIER = APIAttributes.SKILLMULTIPLIER;

    /** 战技冷却 (skillcd) */
    public static final DeferredHolder<Attribute, Attribute> SKILLCD = APIAttributes.SKILLCD;

    // ==================== 瞬身术体系 ====================

    /** 瞬身术冷却 (teleportationcd) */
    public static final DeferredHolder<Attribute, Attribute> TELEPORTATIONCD = APIAttributes.TELEPORTATIONCD;

    /** 瞬身术消耗 (teleportationconsume) */
    public static final DeferredHolder<Attribute, Attribute> TELEPORTATIONCONSUME = APIAttributes.TELEPORTATIONCONSUME;

    /** 瞬身术距离 (teleportationrange) */
    public static final DeferredHolder<Attribute, Attribute> TELEPORTATIONRANGE = APIAttributes.TELEPORTATIONRANGE;

    // ==================== 理智 / 幸运 ====================

    /** 理智光环 (san_variability)：每分钟的 San 变化率 */
    public static final DeferredHolder<Attribute, Attribute> SAN_VARIABILITY = APIAttributes.SAN_VARIABILITY;

    /** 幸运 (luck) */
    public static final DeferredHolder<Attribute, Attribute> LUCK = APIAttributes.LUCK;

    // ==================== 法术体系 ====================

    /** 法术冷却 (magiccd) */
    public static final DeferredHolder<Attribute, Attribute> MAGICCD = APIAttributes.MAGICCD;

    /** 法术强度 (magicpower) */
    public static final DeferredHolder<Attribute, Attribute> MAGICPOWER = APIAttributes.MAGICPOWER;

    // ==================== 治疗 ====================

    /** 总治疗量倍率 (total_healing) */
    public static final DeferredHolder<Attribute, Attribute> TOTALHEALING = APIAttributes.TOTALHEALING;

    private PDAttributes() {
        throw new UnsupportedOperationException("PDAttributes 是常量类，不可实例化");
    }

    /**
     * 将全部自定义属性挂接到玩家实体。
     * <p>
     * 实际属性由 API 层注册，本方法仅负责将它们附加到 {@link EntityType#PLAYER}。
     *
     * @param event 实体属性修改事件
     */
    public static void addPlayerAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, SKILLMULTIPLIER);
        event.add(EntityType.PLAYER, SKILLCD);
        event.add(EntityType.PLAYER, TELEPORTATIONCD);
        event.add(EntityType.PLAYER, TELEPORTATIONCONSUME);
        event.add(EntityType.PLAYER, TELEPORTATIONRANGE);
        event.add(EntityType.PLAYER, SAN_VARIABILITY);
        event.add(EntityType.PLAYER, LUCK);
        event.add(EntityType.PLAYER, MAGICCD);
        event.add(EntityType.PLAYER, MAGICPOWER);
        event.add(EntityType.PLAYER, TOTALHEALING);
    }
}
