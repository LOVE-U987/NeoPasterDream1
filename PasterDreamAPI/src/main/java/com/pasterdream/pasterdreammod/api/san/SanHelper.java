package com.pasterdream.pasterdreammod.api.san;

import com.pasterdream.pasterdreammod.api.attachment.PDPlayerAttachments;
import com.pasterdream.pasterdreammod.api.network.SanDataPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * San 值系统操作帮助类。
 * <p>
 * 位于 PasterDreamAPI，封装对玩家 San 附件的读取、修改与同步逻辑，
 * 供 PasterDream 主模组与 PasterDreamSanity 等附属模组共享。
 * 所有修改入口均会检查游戏规则 {@link APISanGameRules#SAN_CHECK_SYSTEM}
 * 与 {@link SanConfigRegistry} 配置开关，未启用时直接跳过，确保无附属模组时安全降级。
 *
 * @author PasterDream
 */
public final class SanHelper {

    private SanHelper() {
        throw new UnsupportedOperationException("SanHelper 是静态帮助类，不可实例化");
    }

    /**
     * 获取玩家 San 数据。
     *
     * @param player 玩家（双端可用）
     * @return San 数据
     */
    public static SanData getSan(Player player) {
        return player.getData(PDPlayerAttachments.PLAYER_SAN);
    }

    /**
     * 判断 San 检查是否启用（服务端读游戏规则，客户端读附件镜像值）。
     *
     * @param player 玩家
     * @return 是否启用 San 检查
     */
    public static boolean isSanCheckEnabled(Player player) {
        if (player instanceof ServerPlayer sp) {
            return sp.serverLevel().getGameRules().getBoolean(APISanGameRules.SAN_CHECK_SYSTEM);
        }
        return getSan(player).sanCheck();
    }

    /**
     * 直接设置玩家 San 值并同步（不检查开关，仅内部使用）。
     *
     * @param sp  服务端玩家
     * @param san 新 San 值
     */
    private static void setPlayerSan(ServerPlayer sp, double san) {
        sp.setData(PDPlayerAttachments.PLAYER_SAN, sp.getData(PDPlayerAttachments.PLAYER_SAN).withSanValue(san));
        syncSanValueOnly(sp);
    }

    /**
     * 直接增减玩家 San 值并同步（不检查开关，仅内部使用）。
     *
     * @param sp  服务端玩家
     * @param san 变化量（可为负）
     */
    private static void addPlayerSan(ServerPlayer sp, double san) {
        sp.setData(PDPlayerAttachments.PLAYER_SAN, sp.getData(PDPlayerAttachments.PLAYER_SAN).addSanValue(san));
        syncSanValueOnly(sp);
    }

    /**
     * 设置玩家 San 值（仅当游戏规则与配置均开启）。
     *
     * @param player 玩家
     * @param san    新 San 值
     */
    public static void setPlayerSanWithCheck(Player player, double san) {
        if (player instanceof ServerPlayer sp
                && sp.level().getGameRules().getBoolean(APISanGameRules.SAN_CHECK_SYSTEM)
                && Boolean.TRUE.equals(SanConfigRegistry.get().enabled().get())) {
            setPlayerSan(sp, san);
        }
    }

    /**
     * 增减玩家 San 值（仅当游戏规则与配置均开启）。
     *
     * @param player 玩家
     * @param san    变化量（可为负）
     */
    public static void addPlayerSanWithCheck(Player player, double san) {
        if (player instanceof ServerPlayer sp
                && sp.level().getGameRules().getBoolean(APISanGameRules.SAN_CHECK_SYSTEM)
                && Boolean.TRUE.equals(SanConfigRegistry.get().enabled().get())) {
            addPlayerSan(sp, san);
        }
    }

    /**
     * 全量同步 San 数据到客户端。
     * <p>
     * 同步前先用游戏规则刷新附件中的开关镜像值。
     *
     * @param sp 目标玩家
     */
    public static void syncSan(ServerPlayer sp) {
        boolean check = sp.serverLevel().getGameRules().getBoolean(APISanGameRules.SAN_CHECK_SYSTEM);
        SanData data = sp.getData(PDPlayerAttachments.PLAYER_SAN).withSanCheck(check);
        sp.setData(PDPlayerAttachments.PLAYER_SAN, data);
        PacketDistributor.sendToPlayer(sp, SanDataPayload.full(data.sanValue(), data.sanCheck()));
    }

    /**
     * 仅同步 San 数值到客户端。
     *
     * @param sp 目标玩家
     */
    public static void syncSanValueOnly(ServerPlayer sp) {
        PacketDistributor.sendToPlayer(sp, SanDataPayload.valueOnly(sp.getData(PDPlayerAttachments.PLAYER_SAN).sanValue()));
    }

    /**
     * 仅同步 San 开关到客户端。
     *
     * @param sp 目标玩家
     */
    public static void syncSanCheckOnly(ServerPlayer sp) {
        boolean check = sp.serverLevel().getGameRules().getBoolean(APISanGameRules.SAN_CHECK_SYSTEM);
        PacketDistributor.sendToPlayer(sp, SanDataPayload.checkOnly(check));
    }
}
