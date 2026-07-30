package com.pasterdream.pasterdreammod.attachment;

import com.pasterdream.pasterdreammod.api.attachment.PDPlayerAttachments;
import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyConfigRegistry;
import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyData;
import com.pasterdream.pasterdreammod.api.network.MeltDreamEnergyPayload;
import com.pasterdream.pasterdreammod.api.san.SanData;
import com.pasterdream.pasterdreammod.api.san.SanHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * 玩家数据附件兼容门面。
 * <p>
 * 自 1.21.1 重构后，附件类型已上收到 PasterDreamAPI 的 {@link PDPlayerAttachments}，
 * 本类保留为 PasterDream 主模组内部的兼容入口，所有工具方法行为不变。
 * 死亡保留策略等细节已在 API 层统一实现。
 */
public class PDAttachments {

    /**
     * San 理智数据附件（代理至 API 层）。
     */
    public static final Supplier<AttachmentType<SanData>> PLAYER_SAN = PDPlayerAttachments.PLAYER_SAN;

    /**
     * 融梦能量数据附件（代理至 API 层）。
     */
    public static final Supplier<AttachmentType<MeltDreamEnergyData>> PLAYER_MELTDREAM_ENERGY =
            PDPlayerAttachments.PLAYER_MELTDREAM_ENERGY;

    // ==================== 读取 ====================

    /**
     * 获取玩家 San 数据
     *
     * @param player 玩家（双端可用）
     * @return San 数据
     */
    public static SanData getSan(Player player) {
        return SanHelper.getSan(player);
    }

    /**
     * 获取玩家融梦能量数据
     *
     * @param player 玩家（双端可用）
     * @return 融梦能量数据
     */
    public static MeltDreamEnergyData getMeltDreamEnergy(Player player) {
        return player.getData(PLAYER_MELTDREAM_ENERGY);
    }

    /**
     * 判断 San 理智系统是否启用（双端可用，对应原版 IsSanCheckSystem）
     * <p>
     * 服务端读游戏规则 {@code pasterdreamSanSystem}（权威值）；
     * 客户端读附件中的镜像开关（由 S2C 包同步）。
     *
     * @param player 玩家
     * @return 理智系统是否启用
     */
    public static boolean isSanCheckEnabled(Player player) {
        return SanHelper.isSanCheckEnabled(player);
    }

    // ==================== San 修改（带游戏规则检查，对应原版公开 API） ====================

    /**
     * 设置玩家 San 值（仅当游戏规则与配置均开启，对应原版 setPlayerSanWithCheck）
     *
     * @param player 玩家
     * @param san    新理智值
     */
    public static void setPlayerSanWithCheck(Player player, double san) {
        SanHelper.setPlayerSanWithCheck(player, san);
    }

    /**
     * 增减玩家 San 值（仅当游戏规则与配置均开启，对应原版 addPlayerSanWithCheck）
     *
     * @param player 玩家
     * @param san    变化量（可为负）
     */
    public static void addPlayerSanWithCheck(Player player, double san) {
        SanHelper.addPlayerSanWithCheck(player, san);
    }

    // ==================== 融梦能量修改（对应原版公开 API） ====================

    /**
     * 设置玩家融梦能量并同步（对应原版 setPlayerMeltDreamEnergy）
     *
     * @param player 玩家
     * @param value  新能量值
     */
    public static void setPlayerMeltDreamEnergy(Player player, double value) {
        if (player instanceof ServerPlayer sp && Boolean.TRUE.equals(MeltDreamEnergyConfigRegistry.get().enabled().get())) {
            sp.setData(PLAYER_MELTDREAM_ENERGY, sp.getData(PLAYER_MELTDREAM_ENERGY).withEnergy(value));
            syncMeltDreamEnergy(sp);
        }
    }

    /**
     * 增减玩家融梦能量并同步（对应原版 addPlayerMeltDreamEnergy）
     *
     * @param player 玩家
     * @param value  变化量（可为负）
     */
    public static void addPlayerMeltDreamEnergy(Player player, double value) {
        if (player instanceof ServerPlayer sp && Boolean.TRUE.equals(MeltDreamEnergyConfigRegistry.get().enabled().get())) {
            sp.setData(PLAYER_MELTDREAM_ENERGY, sp.getData(PLAYER_MELTDREAM_ENERGY).addEnergy(value));
            syncMeltDreamEnergy(sp);
        }
    }

    /**
     * 叠加/撤销一个免消耗来源并同步（对应原版 setPlayerMeltDreamEnergyNoNeedConsume）
     *
     * @param player 玩家
     * @param value  true 叠加一层，false 撤销一层
     */
    public static void setPlayerMeltDreamEnergyNoNeedConsume(Player player, boolean value) {
        if (player instanceof ServerPlayer sp && Boolean.TRUE.equals(MeltDreamEnergyConfigRegistry.get().enabled().get())) {
            sp.setData(PLAYER_MELTDREAM_ENERGY, sp.getData(PLAYER_MELTDREAM_ENERGY).withNoNeedConsume(value));
            syncMeltDreamEnergy(sp);
        }
    }

    /**
     * 指令开关免消耗并同步（对应原版 setPlayerMeltDreamEnergyNoNeedConsumeByCommand）
     *
     * @param player 玩家
     * @param value  true 开启，false 关闭
     */
    public static void setPlayerMeltDreamEnergyNoNeedConsumeByCommand(Player player, boolean value) {
        if (player instanceof ServerPlayer sp && Boolean.TRUE.equals(MeltDreamEnergyConfigRegistry.get().enabled().get())) {
            sp.setData(PLAYER_MELTDREAM_ENERGY, sp.getData(PLAYER_MELTDREAM_ENERGY).withNoNeedConsumeByCommand(value));
            syncMeltDreamEnergy(sp);
        }
    }

    /**
     * 尝试消耗玩家融梦能量（对应原版 consumePlayerMeltDreamEnergy）
     * <p>
     * 系统关闭、免消耗或创造模式直接成功；能量大于消耗值时扣除并同步。
     * 客户端调用仅做预检查、不修改数据（与原版一致）。
     *
     * @param player 玩家（双端可用）
     * @param value  消耗量
     * @return true 表示消耗成功（或通过预检查）
     */
    public static boolean consumePlayerMeltDreamEnergy(Player player, double value) {
        MeltDreamEnergyData data = player.getData(PLAYER_MELTDREAM_ENERGY);
        if (player instanceof ServerPlayer sp) {
            if (!Boolean.TRUE.equals(MeltDreamEnergyConfigRegistry.get().enabled().get())
                    || data.isNoNeedConsume() || sp.isCreative()) {
                return true;
            }
            if (data.meltDreamEnergy() > value) {
                sp.setData(PLAYER_MELTDREAM_ENERGY, data.addEnergy(-value));
                syncMeltDreamEnergy(sp);
                return true;
            }
            return false;
        }
        // 客户端：只检查不修改
        if (data.isNoNeedConsume() || player.isCreative()) {
            return true;
        }
        return data.meltDreamEnergy() > value;
    }

    // ==================== S2C 同步 ====================

    /**
     * 全量同步 San 数据（对应原版 sync）
     * <p>
     * 与原版一致：发送前先用游戏规则 {@code pasterdreamSanSystem} 刷新服务端的开关镜像值。
     *
     * @param sp 目标玩家
     */
    public static void syncSan(ServerPlayer sp) {
        SanHelper.syncSan(sp);
    }

    /**
     * 仅同步 San 数值（对应原版 syncOnlyValue）
     *
     * @param sp 目标玩家
     */
    public static void syncSanValueOnly(ServerPlayer sp) {
        SanHelper.syncSanValueOnly(sp);
    }

    /**
     * 仅同步 San 开关（取当前游戏规则值，对应原版 syncOnlyCheck）
     *
     * @param sp 目标玩家
     */
    public static void syncSanCheckOnly(ServerPlayer sp) {
        SanHelper.syncSanCheckOnly(sp);
    }

    /**
     * 全量同步融梦能量数据（对应原版 sync）
     *
     * @param sp 目标玩家
     */
    public static void syncMeltDreamEnergy(ServerPlayer sp) {
        MeltDreamEnergyData data = sp.getData(PLAYER_MELTDREAM_ENERGY);
        PacketDistributor.sendToPlayer(sp, new MeltDreamEnergyPayload(data.meltDreamEnergy(), data.noNeedConsume()));
    }
}
