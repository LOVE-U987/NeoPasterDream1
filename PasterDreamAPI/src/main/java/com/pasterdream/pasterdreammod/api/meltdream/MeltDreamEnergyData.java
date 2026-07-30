package com.pasterdream.pasterdreammod.api.meltdream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

/**
 * 融梦能量玩家数据。
 * <p>
 * 此记录类位于 PasterDreamAPI，供融梦能量系统及其附属模组共享访问。
 * 移植自原版 Forge Capability，1.21.1 下改为不可变记录 + AttachmentType。
 *
 * @param meltDreamEnergy 融梦能量值，默认 0，钳制 0~100
 * @param noNeedConsume   免消耗计数器，默认 0；>0 表示存在免消耗来源
 */
public record MeltDreamEnergyData(double meltDreamEnergy, int noNeedConsume) {

    /** 能量下限 */
    public static final double MIN_ENERGY = 0.0D;
    /** 能量上限 */
    public static final double MAX_ENERGY = 100.0D;
    /** 指令用免消耗叠加值 */
    public static final int COMMAND_NO_NEED_CONSUME = 100000000;

    /** 默认数据：能量 0、免消耗计数 0 */
    public static final MeltDreamEnergyData DEFAULT = new MeltDreamEnergyData(0.0D, 0);

    /**
     * 序列化编解码器。
     * <p>
     * 字段名沿用原版 NBT 键，确保与旧存档兼容。
     */
    public static final Codec<MeltDreamEnergyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("pasterdreammeltdreamenergyvalue", 0.0D)
                    .forGetter(MeltDreamEnergyData::meltDreamEnergy),
            Codec.INT.optionalFieldOf("pasterdreammeltdreamenergynoneedconsume", 0)
                    .forGetter(MeltDreamEnergyData::noNeedConsume)
    ).apply(instance, MeltDreamEnergyData::new));

    /**
     * 紧凑构造器：能量钳制 0~100，免消耗计数不小于 0。
     */
    public MeltDreamEnergyData {
        meltDreamEnergy = Mth.clamp(meltDreamEnergy, MIN_ENERGY, MAX_ENERGY);
        noNeedConsume = Math.max(0, noNeedConsume);
    }

    /**
     * 是否处于免消耗状态。
     *
     * @return 计数器大于 0 时为 true
     */
    public boolean isNoNeedConsume() {
        return this.noNeedConsume > 0;
    }

    /**
     * 是否由指令开启免消耗。
     *
     * @return 计数器达到指令叠加值时为 true
     */
    public boolean isNoNeedConsumeByCommand() {
        return this.noNeedConsume >= COMMAND_NO_NEED_CONSUME;
    }

    /**
     * 返回替换能量值后的副本（自动钳制）。
     *
     * @param newEnergy 新能量值
     * @return 新数据
     */
    public MeltDreamEnergyData withEnergy(double newEnergy) {
        return new MeltDreamEnergyData(newEnergy, this.noNeedConsume);
    }

    /**
     * 返回增减能量后的副本（自动钳制）。
     *
     * @param delta 变化量（可为负）
     * @return 新数据
     */
    public MeltDreamEnergyData addEnergy(double delta) {
        return new MeltDreamEnergyData(this.meltDreamEnergy + delta, this.noNeedConsume);
    }

    /**
     * 返回免消耗计数 +1/-1 后的副本。
     *
     * @param noNeedConsumeFlag true 计数 +1，false 计数 -1
     * @return 新数据
     */
    public MeltDreamEnergyData withNoNeedConsume(boolean noNeedConsumeFlag) {
        return new MeltDreamEnergyData(this.meltDreamEnergy, this.noNeedConsume + (noNeedConsumeFlag ? 1 : -1));
    }

    /**
     * 返回直接设置免消耗计数后的副本。
     *
     * @param value 新计数值（不小于 0）
     * @return 新数据
     */
    public MeltDreamEnergyData withNoNeedConsumeValue(int value) {
        return new MeltDreamEnergyData(this.meltDreamEnergy, value);
    }

    /**
     * 返回指令开关免消耗后的副本。
     *
     * @param flag true 开启，false 关闭
     * @return 新数据
     */
    public MeltDreamEnergyData withNoNeedConsumeByCommand(boolean flag) {
        if (this.noNeedConsume >= COMMAND_NO_NEED_CONSUME && !flag) {
            return new MeltDreamEnergyData(this.meltDreamEnergy, this.noNeedConsume - COMMAND_NO_NEED_CONSUME);
        } else if (this.noNeedConsume < COMMAND_NO_NEED_CONSUME && flag) {
            return new MeltDreamEnergyData(this.meltDreamEnergy, this.noNeedConsume + COMMAND_NO_NEED_CONSUME);
        }
        return this;
    }
}
