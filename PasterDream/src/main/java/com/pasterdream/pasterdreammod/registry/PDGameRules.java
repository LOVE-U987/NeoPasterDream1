package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.network.SanDataPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 自定义游戏规则注册类
 * <p>
 * 移植自原版 {@code init/PasterdreamModGameRules.java}，7 条规则的
 * 规则名、分类、默认值与原版完全一致：
 * <ul>
 *   <li>randomCoordX / randomCoordZ — 随机坐标（SPAWNING，默认 0）</li>
 *   <li>pasterdreamDebugmode — 调试模式（MISC，默认 false）</li>
 *   <li>pasterdreamWindDirection — 风向（MISC，默认 0，风之旅途维度机制使用）</li>
 *   <li>pasterdreamSanSystem — San 理智系统开关（PLAYER，默认 true，变更时向全体玩家
 *       发送 {@link SanDataPayload} 开关同步包，与原版回调一致）</li>
 *   <li>pasterdreamStartSanOnRevive — 死亡重生后的初始 San（PLAYER，默认 90）</li>
 *   <li>pasterdreamSanVariabilityPerTick — 每多少 tick 执行一次 San 变化，
 *       总量保持不变、仅影响执行频率（PLAYER，默认 5）</li>
 * </ul>
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

    /**
     * San 理智系统开关 (pasterdreamSanSystem)：默认 true
     * <p>
     * 规则变更时向全体在线玩家发送"仅开关"同步包（对应原版注册时挂的回调）。
     */
    public static final GameRules.Key<GameRules.BooleanValue> SAN_CHECK_SYSTEM =
            GameRules.register("pasterdreamSanSystem", GameRules.Category.PLAYER,
                    GameRules.BooleanValue.create(true, (server, rule) -> {
                        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                            PacketDistributor.sendToPlayer(serverPlayer, SanDataPayload.checkOnly(rule.get()));
                        }
                    }));

    /** 死亡重生后的初始 San (pasterdreamStartSanOnRevive)：默认 90 */
    public static final GameRules.Key<GameRules.IntegerValue> START_SAN_ON_REVIVE =
            GameRules.register("pasterdreamStartSanOnRevive", GameRules.Category.PLAYER, GameRules.IntegerValue.create(90));

    /** San 变化执行间隔 (pasterdreamSanVariabilityPerTick)：默认 5（tick） */
    public static final GameRules.Key<GameRules.IntegerValue> SAN_VARIABILITY_PER_TICK =
            GameRules.register("pasterdreamSanVariabilityPerTick", GameRules.Category.PLAYER, GameRules.IntegerValue.create(5));

    /**
     * 触发类加载以完成静态注册
     * <p>
     * 在主类构造器中调用，确保所有规则在世界创建前注册完毕。
     */
    public static void register() {
        // 静态字段初始化即完成 GameRules.register，无需其他操作
    }
}
