package com.pasterdream.pasterdreammod.api.effect.shake;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * 屏幕晃动数据 —— 描述一次屏幕晃动（投影矩阵随机偏移，随时间衰减）
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code FDShakeData} 设计思路
 * （独立实现，非复制）：
 * <ul>
 *   <li><b>inTime / stayTime / outTime</b>：晃动强度三阶段（渐入→峰值→渐出）；</li>
 *   <li><b>amplitude</b>：最大偏移幅度（投影空间单位，建议 0.05~0.2）；</li>
 *   <li><b>frequency</b>：晃动频率（保留字段，控制随机采样密度）。</li>
 * </ul>
 * 适合 BOSS 终结技/爆炸等大冲击时刻的屏幕震动。
 *
 * @param inTime    渐入 tick 数
 * @param stayTime  持续 tick 数
 * @param outTime   渐出 tick 数
 * @param amplitude 最大偏移幅度
 * @param frequency 晃动频率
 */
public record ScreenShakeData(int inTime, int stayTime, int outTime, float amplitude, float frequency) {

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, ScreenShakeData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ScreenShakeData::inTime,
            ByteBufCodecs.VAR_INT, ScreenShakeData::stayTime,
            ByteBufCodecs.VAR_INT, ScreenShakeData::outTime,
            ByteBufCodecs.FLOAT, ScreenShakeData::amplitude,
            ByteBufCodecs.FLOAT, ScreenShakeData::frequency,
            ScreenShakeData::new
    );

    /**
     * 创建晃动数据构建器
     *
     * @return 构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 晃动总时长（tick）
     *
     * @return in+stay+out
     */
    public int duration() {
        return inTime + stayTime + outTime;
    }

    /**
     * 晃动数据构建器
     */
    public static class Builder {

        private int inTime = 2;
        private int stayTime = 6;
        private int outTime = 10;
        private float amplitude = 0.1f;
        private float frequency = 1.0f;

        private Builder() {
        }

        /**
         * 设置渐入时长
         *
         * @param ticks 渐入 tick 数
         * @return 当前构建器
         */
        public Builder inTime(int ticks) {
            this.inTime = ticks;
            return this;
        }

        /**
         * 设置持续时长
         *
         * @param ticks 持续 tick 数
         * @return 当前构建器
         */
        public Builder stayTime(int ticks) {
            this.stayTime = ticks;
            return this;
        }

        /**
         * 设置渐出时长
         *
         * @param ticks 渐出 tick 数
         * @return 当前构建器
         */
        public Builder outTime(int ticks) {
            this.outTime = ticks;
            return this;
        }

        /**
         * 设置最大偏移幅度
         *
         * @param amp 幅度（投影空间单位，建议 0.05~0.2）
         * @return 当前构建器
         */
        public Builder amplitude(float amp) {
            this.amplitude = amp;
            return this;
        }

        /**
         * 设置晃动频率
         *
         * @param freq 频率
         * @return 当前构建器
         */
        public Builder frequency(float freq) {
            this.frequency = freq;
            return this;
        }

        /**
         * 构建晃动数据
         *
         * @return 数据
         */
        public ScreenShakeData build() {
            return new ScreenShakeData(inTime, stayTime, outTime, amplitude, frequency);
        }
    }
}
