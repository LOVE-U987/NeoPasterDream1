package com.pasterdream.pasterdreammod.attachment;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.network.MeltDreamEnergyPayload;
import com.pasterdream.pasterdreammod.network.SanDataPayload;
import com.pasterdream.pasterdreammod.registry.PDGameRules;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 玩家数据附件注册与访问工具类
 * <p>
 * 以 NeoForge {@code AttachmentType} 重建原版 Forge Capability 玩家变量层：
 * <ul>
 *   <li>{@link #PLAYER_SAN} — San 理智数据（原版 {@code SanCapability}，
 *       挂接键 {@code pasterdream:pasterdreamsan} 与原版一致），死亡<b>不</b>保留，
 *       重生按游戏规则重置（见 {@link PlayerDataEvents}）</li>
 *   <li>{@link #PLAYER_MELTDREAM_ENERGY} — 融梦能量数据（原版 {@code MeltDreamEnergyCapability}，
 *       挂接键 {@code pasterdream:pasterdreammeltdreamenergy}），死亡保留（copyOnDeath）</li>
 * </ul>
 * 静态工具方法与原版两个 Capability 类的公开静态 API 一一对应（含游戏规则前置检查与自动同步），
 * 供后续 San 系统 / 能量戒指 / 指令等模块调用。
 * <p>
 * 同步策略与原版一致：服务端修改后立即向该玩家发送 S2C 包
 * （{@link SanDataPayload} / {@link MeltDreamEnergyPayload}）；
 * 登录 / 重生 / 跨维度 / 克隆时的全量同步见 {@link PlayerDataEvents}。
 */
public class PDAttachments {

    /** 附件类型延迟注册器 */
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, PasterDreamMod.MOD_ID);

    /**
     * San 理智数据附件
     * <p>
     * 不启用 copyOnDeath：死亡时丢弃，由 {@link PlayerDataEvents#onPlayerClone} 按游戏规则
     * {@code pasterdreamStartSanOnRevive} / {@code pasterdreamSanSystem} 重置（对照原版 clone 逻辑）；
     * 非死亡克隆由 NeoForge 自动复制。
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SanData>> PLAYER_SAN =
            ATTACHMENT_TYPES.register("pasterdreamsan", () ->
                    AttachmentType.builder(() -> SanData.DEFAULT)
                            .serialize(SanData.CODEC)
                            .build());

    /**
     * 融梦能量数据附件
     * <p>
     * 启用 copyOnDeath：死亡与非死亡克隆均完整保留（对照原版 clone 逻辑无条件复制）。
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MeltDreamEnergyData>> PLAYER_MELTDREAM_ENERGY =
            ATTACHMENT_TYPES.register("pasterdreammeltdreamenergy", () ->
                    AttachmentType.builder(() -> MeltDreamEnergyData.DEFAULT)
                            .serialize(MeltDreamEnergyData.CODEC)
                            .copyOnDeath()
                            .build());

    // ==================== 读取 ====================

    /**
     * 获取玩家 San 数据
     *
     * @param player 玩家（双端可用）
     * @return San 数据
     */
    public static SanData getSan(Player player) {
        return player.getData(PLAYER_SAN);
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
        if (player instanceof ServerPlayer sp) {
            return sp.serverLevel().getGameRules().getBoolean(PDGameRules.SAN_CHECK_SYSTEM);
        }
        return player.getData(PLAYER_SAN).sanCheck();
    }

    // ==================== San 修改（带游戏规则检查，对应原版公开 API） ====================

    /**
     * 设置玩家 San 值（仅当游戏规则 pasterdreamSanSystem 开启，对应原版 setPlayerSanWithCheck）
     *
     * @param player 玩家
     * @param san    新理智值
     */
    public static void setPlayerSanWithCheck(Player player, double san) {
        if (player instanceof ServerPlayer sp
                && sp.level().getGameRules().getBoolean(PDGameRules.SAN_CHECK_SYSTEM)) {
            setPlayerSan(sp, san);
        }
    }

    /**
     * 增减玩家 San 值（仅当游戏规则 pasterdreamSanSystem 开启，对应原版 addPlayerSanWithCheck）
     *
     * @param player 玩家
     * @param san    变化量（可为负）
     */
    public static void addPlayerSanWithCheck(Player player, double san) {
        if (player instanceof ServerPlayer sp
                && sp.level().getGameRules().getBoolean(PDGameRules.SAN_CHECK_SYSTEM)) {
            addPlayerSan(sp, san);
        }
    }

    /** 直接设置 San 值并同步（原版为 private，仅供 WithCheck 入口调用） */
    private static void setPlayerSan(ServerPlayer sp, double san) {
        sp.setData(PLAYER_SAN, sp.getData(PLAYER_SAN).withSanValue(san));
        syncSanValueOnly(sp);
    }

    /** 直接增减 San 值并同步（原版为 private，仅供 WithCheck 入口调用） */
    private static void addPlayerSan(ServerPlayer sp, double san) {
        sp.setData(PLAYER_SAN, sp.getData(PLAYER_SAN).addSanValue(san));
        syncSanValueOnly(sp);
    }

    // ==================== 融梦能量修改（对应原版公开 API） ====================

    /**
     * 设置玩家融梦能量并同步（对应原版 setPlayerMeltDreamEnergy）
     *
     * @param player 玩家
     * @param value  新能量值
     */
    public static void setPlayerMeltDreamEnergy(Player player, double value) {
        if (player instanceof ServerPlayer sp) {
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
        if (player instanceof ServerPlayer sp) {
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
        if (player instanceof ServerPlayer sp) {
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
        if (player instanceof ServerPlayer sp) {
            sp.setData(PLAYER_MELTDREAM_ENERGY, sp.getData(PLAYER_MELTDREAM_ENERGY).withNoNeedConsumeByCommand(value));
            syncMeltDreamEnergy(sp);
        }
    }

    /**
     * 尝试消耗玩家融梦能量（对应原版 consumePlayerMeltDreamEnergy）
     * <p>
     * 免消耗或创造模式直接成功；能量大于消耗值时扣除并同步。
     * 客户端调用仅做预检查、不修改数据（与原版一致）。
     *
     * @param player 玩家（双端可用）
     * @param value  消耗量
     * @return true 表示消耗成功（或通过预检查）
     */
    public static boolean consumePlayerMeltDreamEnergy(Player player, double value) {
        MeltDreamEnergyData data = player.getData(PLAYER_MELTDREAM_ENERGY);
        if (player instanceof ServerPlayer sp) {
            if (data.isNoNeedConsume() || sp.isCreative()) {
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
        boolean check = sp.serverLevel().getGameRules().getBoolean(PDGameRules.SAN_CHECK_SYSTEM);
        SanData data = sp.getData(PLAYER_SAN).withSanCheck(check);
        sp.setData(PLAYER_SAN, data);
        PacketDistributor.sendToPlayer(sp, SanDataPayload.full(data));
    }

    /**
     * 仅同步 San 数值（对应原版 syncOnlyValue）
     *
     * @param sp 目标玩家
     */
    public static void syncSanValueOnly(ServerPlayer sp) {
        PacketDistributor.sendToPlayer(sp, SanDataPayload.valueOnly(sp.getData(PLAYER_SAN).sanValue()));
    }

    /**
     * 仅同步 San 开关（取当前游戏规则值，对应原版 syncOnlyCheck）
     *
     * @param sp 目标玩家
     */
    public static void syncSanCheckOnly(ServerPlayer sp) {
        boolean check = sp.serverLevel().getGameRules().getBoolean(PDGameRules.SAN_CHECK_SYSTEM);
        PacketDistributor.sendToPlayer(sp, SanDataPayload.checkOnly(check));
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
