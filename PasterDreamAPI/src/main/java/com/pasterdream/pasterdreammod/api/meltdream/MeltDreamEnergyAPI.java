package com.pasterdream.pasterdreammod.api.meltdream;

import com.pasterdream.pasterdreammod.api.attachment.PDPlayerAttachments;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 融梦能量系统 API 门面。
 * <p>
 * 提供对玩家融梦能量的安全访问。当融梦能量附属模组未加载时，
 * 对数值的修改操作会安全回退为对默认附件的只读/写操作。
 */
public final class MeltDreamEnergyAPI {

    private MeltDreamEnergyAPI() {
        throw new UnsupportedOperationException("MeltDreamEnergyAPI 是门面类，不可实例化");
    }

    /**
     * 读取玩家当前融梦能量。
     *
     * @param player 玩家
     * @return 融梦能量值
     */
    public static double getEnergy(@Nullable Player player) {
        if (player == null) return MeltDreamEnergyData.DEFAULT.meltDreamEnergy();
        return player.getData(PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY).meltDreamEnergy();
    }

    /**
     * 读取玩家融梦能量数据。
     *
     * @param player 玩家
     * @return 融梦能量数据
     */
    public static MeltDreamEnergyData getData(@Nullable Player player) {
        if (player == null) return MeltDreamEnergyData.DEFAULT;
        return player.getData(PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY);
    }

    /**
     * 设置玩家融梦能量（自动钳制）。
     *
     * @param player 玩家
     * @param value  新能量值
     */
    public static void setEnergy(Player player, double value) {
        if (player == null) return;
        player.setData(PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY,
                player.getData(PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY).withEnergy(value));
    }

    /**
     * 增减玩家融梦能量（自动钳制）。
     *
     * @param player 玩家
     * @param delta  变化量
     */
    public static void addEnergy(Player player, double delta) {
        if (player == null) return;
        player.setData(PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY,
                player.getData(PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY).addEnergy(delta));
    }

    /**
     * 带开关检查的增减融梦能量。
     *
     * @param player       玩家
     * @param delta        变化量
     * @param masterSwitch 总开关
     */
    public static void addPlayerEnergyWithCheck(Player player, double delta, Supplier<Boolean> masterSwitch) {
        if (player == null || !masterSwitch.get()) return;
        addEnergy(player, delta);
    }

    /**
     * 带开关检查的设置融梦能量。
     *
     * @param player       玩家
     * @param value        新能量值
     * @param masterSwitch 总开关
     */
    public static void setPlayerEnergyWithCheck(Player player, double value, Supplier<Boolean> masterSwitch) {
        if (player == null || !masterSwitch.get()) return;
        setEnergy(player, value);
    }

    /**
     * 尝试消耗融梦能量。
     * <p>
     * 处于免消耗状态或能量充足时返回 true，并扣除相应能量。
     *
     * @param player 玩家
     * @param cost   消耗量
     * @return 是否消耗成功
     */
    public static boolean consumeEnergy(Player player, double cost) {
        if (player == null) return false;
        MeltDreamEnergyData data = getData(player);
        if (data.isNoNeedConsume() || data.meltDreamEnergy() >= cost) {
            if (!data.isNoNeedConsume()) {
                setEnergy(player, data.meltDreamEnergy() - cost);
            }
            return true;
        }
        return false;
    }

    /**
     * 设置免消耗计数。
     *
     * @param player 玩家
     * @param value  新计数值
     */
    public static void setNoNeedConsumeValue(Player player, int value) {
        if (player == null) return;
        player.setData(PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY,
                player.getData(PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY).withNoNeedConsumeValue(value));
    }

    /**
     * 增减免消耗计数。
     *
     * @param player 玩家
     * @param flag   true 增加，false 减少
     */
    public static void updateNoNeedConsume(Player player, boolean flag) {
        if (player == null) return;
        player.setData(PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY,
                player.getData(PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY).withNoNeedConsume(flag));
    }

    /**
     * 获取融梦能量数据 Optional 包装。
     *
     * @param player 玩家
     * @return 融梦能量数据 Optional
     */
    public static Optional<MeltDreamEnergyData> getEnergyData(@Nullable Player player) {
        if (player == null) return Optional.empty();
        return Optional.of(getData(player));
    }
}
