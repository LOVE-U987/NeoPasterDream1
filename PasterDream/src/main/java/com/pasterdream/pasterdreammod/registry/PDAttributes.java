package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义玩家属性注册类
 * <p>
 * 移植自原版 {@code init/PasterdreamModAttributes.java}（Forge 1.20.1），
 * 注册名、默认值、取值范围与原版完全一致，全部属性开启 {@code setSyncable(true)} 同步到客户端。
 * <p>
 * 原版共 12 个属性，其中 {@code meltdreamenergy} 与 {@code san} 两项在原版 1.3 起已
 * {@code @Deprecated} 注释停用（数据迁移至 Capability，本模组对应 attachment 包），
 * 故实际注册 10 个活跃属性：
 * <ul>
 *   <li>skillmultiplier — 战技伤害倍率（默认 1，范围 0~10）</li>
 *   <li>skillcd — 战技冷却（默认 1，范围 0~2）</li>
 *   <li>teleportationcd — 瞬身术冷却（默认 1，范围 0~10）</li>
 *   <li>teleportationconsume — 瞬身术消耗（默认 1，范围 0~10）</li>
 *   <li>teleportationrange — 瞬身术距离（默认 1，范围 0~10）</li>
 *   <li>san_variability — 理智光环，每分钟的 San 变化率（默认 0，范围 -120000~120000）</li>
 *   <li>luck — 幸运（默认 0，范围 -100~1000）</li>
 *   <li>magiccd — 法术冷却（默认 1，范围 0~10）</li>
 *   <li>magicpower — 法术强度（默认 0，范围 -10~10）</li>
 *   <li>total_healing — 总治疗量倍率（默认 1，范围 0~10）</li>
 * </ul>
 * 挂接逻辑与原版一致：仅挂到 {@link EntityType#PLAYER}，
 * 见 {@link #addPlayerAttributes(EntityAttributeModificationEvent)}（MOD 总线，主类构造器中显式监听）。
 */
public class PDAttributes {

    /** 属性延迟注册器（注册到 pasterdream 命名空间） */
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, PasterDreamMod.MOD_ID);

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

    /**
     * 注册单个有范围属性
     * <p>
     * 描述键与原版一致：{@code attribute.pasterdream.<注册名>}；全部开启客户端同步。
     *
     * @param name         注册名（与原版一致）
     * @param defaultValue 默认值
     * @param min          最小值
     * @param max          最大值
     * @return 属性 Holder
     */
    private static DeferredHolder<Attribute, Attribute> register(String name, double defaultValue, double min, double max) {
        return ATTRIBUTES.register(name, () ->
                new RangedAttribute("attribute." + PasterDreamMod.MOD_ID + "." + name, defaultValue, min, max)
                        .setSyncable(true));
    }

    /**
     * 将全部自定义属性挂接到玩家实体
     * <p>
     * 与原版 {@code PasterdreamModAttributes#addAttributes} 一致：仅挂 {@link EntityType#PLAYER}。
     * MOD 总线事件，由主类构造器 {@code modEventBus.addListener(PDAttributes::addPlayerAttributes)} 接线。
     *
     * @param event 实体属性修改事件
     */
    public static void addPlayerAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, SKILLMULTIPLIER);
        event.add(EntityType.PLAYER, SKILLCD);
        event.add(EntityType.PLAYER, TELEPORTATIONCD);
        event.add(EntityType.PLAYER, TELEPORTATIONCONSUME);
        event.add(EntityType.PLAYER, TELEPORTATIONRANGE);
        // 每分钟的 San 变化率（理智光环）
        event.add(EntityType.PLAYER, SAN_VARIABILITY);
        event.add(EntityType.PLAYER, LUCK);
        event.add(EntityType.PLAYER, MAGICCD);
        event.add(EntityType.PLAYER, MAGICPOWER);
        event.add(EntityType.PLAYER, TOTALHEALING);
    }
}
