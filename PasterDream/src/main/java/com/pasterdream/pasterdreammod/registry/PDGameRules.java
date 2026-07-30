package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.san.APISanGameRules;
import net.minecraft.world.level.GameRules;

/**
 * 自定义游戏规则注册类。
 * <p>
 * 移植自原版 {@code init/PasterdreamModGameRules.java}。其中 San 相关的三条规则
 * （{@code pasterdreamSanSystem}、{@code pasterdreamStartSanOnRevive}、
 * {@code pasterdreamSanVariabilityPerTick}）已上提到 PasterDreamAPI 的
 * {@link APISanGameRules}，供主模组与 PasterDreamSanity 共享；本类保留为兼容门面，
 * 其他非 San 规则仍在此注册。
 * <p>
 * 注意：{@code GameRules.register} 为静态注册，本类在主类构造器中通过
 * {@link #register()} 触发类加载完成注册。
 */
public class PDGameRules {

    /** 随机坐标 X (randomCoordX)：默认 0 */
    public static final GameRules.Key<GameRules.IntegerValue> RANDOM_COORD_X =
            GameRules.register("randomCoordX", GameRules.Category.SPAWNING, GameRules.IntegerValue.create(0));

    /** 随机坐标 Z (randomCoordZ)：默认 0 */
    public static final GameRules.Key<GameRules.IntegerValue> RANDOM_COORD_Z =
            GameRules.register("randomCoordZ", GameRules.Category.SPAWNING, GameRules.IntegerValue.create(0));

    /** 帕斯特之梦调试模式 (pasterdreamDebugmode)：默认 false */
    public static final GameRules.Key<GameRules.BooleanValue> PASTERDREAM_DEBUG_MODE =
            GameRules.register("pasterdreamDebugmode", GameRules.Category.MISC, GameRules.BooleanValue.create(false));

    /** 风向 (pasterdreamWindDirection)：默认 0（风之旅途维度机制） */
    public static final GameRules.Key<GameRules.IntegerValue> WIND_DIRECTION =
            GameRules.register("pasterdreamWindDirection", GameRules.Category.MISC, GameRules.IntegerValue.create(0));

    /** San 理智系统开关 (pasterdreamSanSystem)：默认 true（代理至 API） */
    public static final GameRules.Key<GameRules.BooleanValue> SAN_CHECK_SYSTEM = APISanGameRules.SAN_CHECK_SYSTEM;

    /** 死亡重生后的初始 San (pasterdreamStartSanOnRevive)：默认 90（代理至 API） */
    public static final GameRules.Key<GameRules.IntegerValue> START_SAN_ON_REVIVE = APISanGameRules.START_SAN_ON_REVIVE;

    /** San 变化执行间隔 (pasterdreamSanVariabilityPerTick)：默认 5 tick（代理至 API） */
    public static final GameRules.Key<GameRules.IntegerValue> SAN_VARIABILITY_PER_TICK = APISanGameRules.SAN_VARIABILITY_PER_TICK;

    /**
     * 触发类加载以完成静态注册。
     * <p>
     * 由于 San 规则已在 API 层静态注册，本方法主要确保非 San 规则被加载。
     */
    public static void register() {
        // 静态字段初始化即完成 GameRules.register，无需其他操作
    }
}
