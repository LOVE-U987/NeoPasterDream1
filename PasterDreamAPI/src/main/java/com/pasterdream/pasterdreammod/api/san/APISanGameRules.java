package com.pasterdream.pasterdreammod.api.san;

import com.pasterdream.pasterdreammod.api.network.SanDataPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * San 值系统游戏规则注册中心。
 * <p>
 * 位于 PasterDreamAPI，供 PasterDream 主模组与 PasterDreamSanity 附属模组共享。
 * 将原本定义在主模组 {@code PDGameRules} 中的 San 相关规则上提到 API 层，
 * 避免附属模组为了读取规则而反向依赖主模组。
 *
 * @author PasterDream
 */
public final class APISanGameRules {

    /** San 理智系统总开关 (pasterdreamSanSystem)：默认 true */
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

    /** San 变化执行间隔 (pasterdreamSanVariabilityPerTick)：默认 5 tick */
    public static final GameRules.Key<GameRules.IntegerValue> SAN_VARIABILITY_PER_TICK =
            GameRules.register("pasterdreamSanVariabilityPerTick", GameRules.Category.PLAYER, GameRules.IntegerValue.create(5));

    private APISanGameRules() {
        throw new UnsupportedOperationException("APISanGameRules 是静态注册类，不可实例化");
    }
}
