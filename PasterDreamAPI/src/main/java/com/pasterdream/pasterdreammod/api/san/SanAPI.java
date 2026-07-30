package com.pasterdream.pasterdreammod.api.san;

import com.pasterdream.pasterdreammod.api.attachment.PDPlayerAttachments;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * San 值系统 API 门面。
 * <p>
 * 提供对玩家 San 数据的安全访问。当 San 附属模组未加载时，
 * 对数值的修改操作会安全回退为对默认附件的只读/写操作，
 * 但 UI、效果、环境逻辑等应由各模组自行注册事件回调实现。
 */
public final class SanAPI {

    private SanAPI() {
        throw new UnsupportedOperationException("SanAPI 是门面类，不可实例化");
    }

    /**
     * 读取玩家当前 San 值。
     *
     * @param player 玩家
     * @return San 值；若玩家未附加数据则返回默认值 100
     */
    public static double getSanValue(@Nullable Player player) {
        if (player == null) return SanData.DEFAULT.sanValue();
        return player.getData(PDPlayerAttachments.PLAYER_SAN).sanValue();
    }

    /**
     * 读取玩家 San 检查开关。
     *
     * @param player 玩家
     * @return 是否启用 San 检查
     */
    public static boolean isSanCheckEnabled(@Nullable Player player) {
        if (player == null) return SanData.DEFAULT.sanCheck();
        return player.getData(PDPlayerAttachments.PLAYER_SAN).sanCheck();
    }

    /**
     * 设置玩家 San 值（自动钳制）。
     *
     * @param player 玩家
     * @param value  新 San 值
     */
    public static void setSanValue(Player player, double value) {
        if (player == null) return;
        player.setData(PDPlayerAttachments.PLAYER_SAN,
                player.getData(PDPlayerAttachments.PLAYER_SAN).withSanValue(value));
    }

    /**
     * 增减玩家 San 值（自动钳制）。
     *
     * @param player 玩家
     * @param delta  变化量
     */
    public static void addSanValue(Player player, double delta) {
        if (player == null) return;
        player.setData(PDPlayerAttachments.PLAYER_SAN,
                player.getData(PDPlayerAttachments.PLAYER_SAN).addSanValue(delta));
    }

    /**
     * 带开关检查的增减 San 值。
     * <p>
     * 仅当 San 检查开启且总开关启用时才执行。
     *
     * @param player      玩家
     * @param delta       变化量
     * @param masterSwitch 总开关（通常来自配置）
     */
    public static void addPlayerSanWithCheck(Player player, double delta, Supplier<Boolean> masterSwitch) {
        if (player == null || !masterSwitch.get() || !isSanCheckEnabled(player)) return;
        addSanValue(player, delta);
    }

    /**
     * 带开关检查的设置 San 值。
     *
     * @param player       玩家
     * @param value        新 San 值
     * @param masterSwitch 总开关
     */
    public static void setPlayerSanWithCheck(Player player, double value, Supplier<Boolean> masterSwitch) {
        if (player == null || !masterSwitch.get() || !isSanCheckEnabled(player)) return;
        setSanValue(player, value);
    }

    /**
     * 设置 San 检查开关。
     *
     * @param player 玩家
     * @param check  新开关值
     */
    public static void setSanCheck(Player player, boolean check) {
        if (player == null) return;
        player.setData(PDPlayerAttachments.PLAYER_SAN,
                player.getData(PDPlayerAttachments.PLAYER_SAN).withSanCheck(check));
    }

    /**
     * 获取 San 数据 Optional 包装。
     *
     * @param player 玩家
     * @return San 数据 Optional
     */
    public static Optional<SanData> getSanData(@Nullable Player player) {
        if (player == null) return Optional.empty();
        return Optional.of(player.getData(PDPlayerAttachments.PLAYER_SAN));
    }
}
