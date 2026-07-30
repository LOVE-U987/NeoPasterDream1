package com.pasterdream.pasterdreammod.api.san;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

/**
 * San 值玩家数据。
 * <p>
 * 此记录类位于 PasterDreamAPI，供 San 系统及其附属模组共享访问。
 * 采用不可变记录 + AttachmentType 设计，数值变化时返回新实例。
 *
 * @param sanValue 当前 San 值
 * @param sanCheck 是否启用 San 检查（游戏规则同步位）
 */
public record SanData(double sanValue, boolean sanCheck) {

    /** San 值下限 */
    public static final double MIN_SAN = 0.0D;
    /** San 值上限（默认 100，可被配置覆盖，但数据层只负责钳制到 100） */
    public static final double MAX_SAN = 100.0D;

    /** 默认数据：San 值 100、检查开启 */
    public static final SanData DEFAULT = new SanData(100.0D, true);

    /**
     * 序列化编解码器。
     * <p>
     * 字段名沿用原版 NBT 键，确保与旧存档兼容。
     */
    public static final Codec<SanData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("pasterdreamsanvalue", 100.0D)
                    .forGetter(SanData::sanValue),
            Codec.BOOL.optionalFieldOf("pasterdreamsancheck", true)
                    .forGetter(SanData::sanCheck)
    ).apply(instance, SanData::new));

    /**
     * 紧凑构造器：San 值钳制到 [0, 100]。
     */
    public SanData {
        sanValue = Mth.clamp(sanValue, MIN_SAN, MAX_SAN);
    }

    /**
     * 返回替换 San 值后的副本（自动钳制）。
     *
     * @param newValue 新 San 值
     * @return 新数据
     */
    public SanData withSanValue(double newValue) {
        return new SanData(newValue, this.sanCheck);
    }

    /**
     * 返回增减 San 值后的副本（自动钳制）。
     *
     * @param delta 变化量（可为负）
     * @return 新数据
     */
    public SanData addSanValue(double delta) {
        return new SanData(this.sanValue + delta, this.sanCheck);
    }

    /**
     * 返回切换 San 检查开关后的副本。
     *
     * @param newCheck 新开关值
     * @return 新数据
     */
    public SanData withSanCheck(boolean newCheck) {
        return new SanData(this.sanValue, newCheck);
    }
}
