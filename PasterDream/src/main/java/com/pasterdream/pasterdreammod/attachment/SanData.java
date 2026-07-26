package com.pasterdream.pasterdreammod.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

/**
 * San（理智）玩家数据
 * <p>
 * 移植自原版 {@code capability/SanCapability.java}（Forge Capability），
 * 1.21.1 NeoForge 下改为不可变记录 + {@code AttachmentType}（见 {@link PDAttachments#PLAYER_SAN}）。
 * <ul>
 *   <li>{@code sanValue} — 理智值，默认 100，始终钳制在 0~100（与原版 set/add 的钳制一致）</li>
 *   <li>{@code sanCheck} — 理智系统开关（客户端镜像值），默认 true；服务端权威值为游戏规则
 *       {@code pasterdreamSanSystem}</li>
 * </ul>
 * 死亡行为（对照原版 {@code playerClone}）：死亡时<b>不保留</b>，重生后由
 * {@link PlayerDataEvents#onPlayerClone} 依据游戏规则 {@code pasterdreamStartSanOnRevive}（默认 90）
 * 与 {@code pasterdreamSanSystem} 重置；非死亡克隆（如末地返回）由 NeoForge 自动复制。
 *
 * @param sanValue 理智值（0~100）
 * @param sanCheck 理智系统开关（客户端镜像）
 */
public record SanData(double sanValue, boolean sanCheck) {

    /** 理智值下限 */
    public static final double MIN_SAN = 0.0D;
    /** 理智值上限 */
    public static final double MAX_SAN = 100.0D;
    /** 初次进入游戏的默认理智值（原版构造器默认 100） */
    public static final double DEFAULT_SAN = 100.0D;

    /** 默认数据：San=100、开关开启（与原版无参构造器一致） */
    public static final SanData DEFAULT = new SanData(DEFAULT_SAN, true);

    /**
     * 序列化编解码器
     * <p>
     * 字段名沿用原版 NBT 键：{@code pasterdreamsanvalue} / {@code pasterdreamsancheck}。
     */
    public static final Codec<SanData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("pasterdreamsanvalue", DEFAULT_SAN).forGetter(SanData::sanValue),
            Codec.BOOL.optionalFieldOf("pasterdreamsancheck", true).forGetter(SanData::sanCheck)
    ).apply(instance, SanData::new));

    /**
     * 紧凑构造器：钳制理智值到 0~100（对应原版 setSanValue 的 Math.max/Math.min）
     */
    public SanData {
        sanValue = Mth.clamp(sanValue, MIN_SAN, MAX_SAN);
    }

    /**
     * 返回替换理智值后的副本（自动钳制）
     *
     * @param newSanValue 新理智值
     * @return 新数据
     */
    public SanData withSanValue(double newSanValue) {
        return new SanData(newSanValue, this.sanCheck);
    }

    /**
     * 返回增减理智值后的副本（自动钳制，对应原版 addSanValue）
     *
     * @param delta 变化量（可为负）
     * @return 新数据
     */
    public SanData addSanValue(double delta) {
        return new SanData(this.sanValue + delta, this.sanCheck);
    }

    /**
     * 返回替换开关后的副本
     *
     * @param newSanCheck 新开关值
     * @return 新数据
     */
    public SanData withSanCheck(boolean newSanCheck) {
        return new SanData(this.sanValue, newSanCheck);
    }
}
