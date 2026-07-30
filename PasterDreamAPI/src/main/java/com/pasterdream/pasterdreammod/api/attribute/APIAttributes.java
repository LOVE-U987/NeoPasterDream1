package com.pasterdream.pasterdreammod.api.attribute;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * PasterDream 共享玩家属性注册中心。
 * <p>
 * 位于 PasterDreamAPI，供主模组及 San/融梦/法术等附属模组共享。
 * 将原本散落在主模组 PDAttributes 中的属性上提到 API，避免附属模组为了读取属性而反向依赖主模组。
 *
 * @author PasterDream
 */
public final class APIAttributes {

    /** 属性延迟注册器（注册到 pasterdream 数据命名空间以保持兼容性） */
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, PasterDreamAPI.DATA_NAMESPACE);

    // ==================== 战技体系 ====================

    /** 战技伤害倍率 (skillmultiplier)：默认 1，范围 0~10 */
    public static final DeferredHolder<Attribute, Attribute> SKILLMULTIPLIER =
            register("skillmultiplier", 1, 0, 10);

    /** 战技冷却 (skillcd)：默认 1，范围 0~2 */
    public static final DeferredHolder<Attribute, Attribute> SKILLCD =
            register("skillcd", 1, 0, 2);

    // ==================== 瞬身术体系 ====================

    /** 瞬身术冷却 (teleportationcd)：默认 1，范围 0~10 */
    public static final DeferredHolder<Attribute, Attribute> TELEPORTATIONCD =
            register("teleportationcd", 1, 0, 10);

    /** 瞬身术消耗 (teleportationconsume)：默认 1，范围 0~10 */
    public static final DeferredHolder<Attribute, Attribute> TELEPORTATIONCONSUME =
            register("teleportationconsume", 1, 0, 10);

    /** 瞬身术距离 (teleportationrange)：默认 1，范围 0~10 */
    public static final DeferredHolder<Attribute, Attribute> TELEPORTATIONRANGE =
            register("teleportationrange", 1, 0, 10);

    // ==================== 理智 / 幸运 ====================

    /** 理智光环 (san_variability)：每分钟的 San 变化率，默认 0，范围 -120000~120000 */
    public static final DeferredHolder<Attribute, Attribute> SAN_VARIABILITY =
            register("san_variability", 0, -120000, 120000);

    /** 幸运 (luck)：默认 0，范围 -100~1000 */
    public static final DeferredHolder<Attribute, Attribute> LUCK =
            register("luck", 0, -100, 1000);

    // ==================== 法术体系 ====================

    /** 法术冷却 (magiccd)：默认 1，范围 0~10 */
    public static final DeferredHolder<Attribute, Attribute> MAGICCD =
            register("magiccd", 1, 0, 10);

    /** 法术强度 (magicpower)：默认 0，范围 -10~10 */
    public static final DeferredHolder<Attribute, Attribute> MAGICPOWER =
            register("magicpower", 0, -10, 10);

    // ==================== 治疗 ====================

    /** 总治疗量倍率 (total_healing)：默认 1，范围 0~10 */
    public static final DeferredHolder<Attribute, Attribute> TOTALHEALING =
            register("total_healing", 1, 0, 10);

    private APIAttributes() {
        throw new UnsupportedOperationException("APIAttributes 是常量类，不可实例化");
    }

    /**
     * 注册单个有范围属性。
     * <p>
     * 描述键沿用 {@code attribute.pasterdream.<注册名>} 格式，全部开启客户端同步。
     *
     * @param name         注册名（与原版一致）
     * @param defaultValue 默认值
     * @param min          最小值
     * @param max          最大值
     * @return 属性 Holder
     */
    private static DeferredHolder<Attribute, Attribute> register(String name, double defaultValue, double min, double max) {
        return ATTRIBUTES.register(name, () ->
                new RangedAttribute("attribute." + PasterDreamAPI.DATA_NAMESPACE + "." + name, defaultValue, min, max)
                        .setSyncable(true));
    }
}
