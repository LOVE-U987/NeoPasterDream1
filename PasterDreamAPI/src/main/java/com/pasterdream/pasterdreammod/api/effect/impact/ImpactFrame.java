package com.pasterdream.pasterdreammod.api.effect.impact;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * 打击帧数据 —— 描述一次命中瞬间的全屏灰度冲击效果
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ImpactFrame} 设计思路
 * （独立实现，非复制）。参数：
 * <ul>
 *   <li><b>threshold</b>：灰度阈值（像素亮度高于此值视为冲击高亮）；</li>
 *   <li><b>thresholdLerp</b>：阈值过渡宽度（0 为硬边界）；</li>
 *   <li><b>duration</b>：持续 tick 数；</li>
 *   <li><b>invert</b>：是否反相（true 为黑场冲击）。</li>
 * </ul>
 * 用于 BOSS 重击/暴击瞬间的视觉反馈。
 *
 * @param threshold    灰度阈值
 * @param thresholdLerp 阈值过渡宽度
 * @param duration     持续 tick 数
 * @param invert       是否反相
 */
public record ImpactFrame(float threshold, float thresholdLerp, int duration, boolean invert) {

    /** 网络编解码器 */
    public static final StreamCodec<FriendlyByteBuf, ImpactFrame> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ImpactFrame::threshold,
            ByteBufCodecs.FLOAT, ImpactFrame::thresholdLerp,
            ByteBufCodecs.VAR_INT, ImpactFrame::duration,
            ByteBufCodecs.BOOL, ImpactFrame::invert,
            ImpactFrame::new
    );

    /**
     * 构造打击帧（默认非反相）
     *
     * @param threshold    灰度阈值
     * @param thresholdLerp 阈值过渡宽度
     * @param duration     持续 tick 数
     */
    public ImpactFrame(float threshold, float thresholdLerp, int duration) {
        this(threshold, thresholdLerp, duration, false);
    }

    /**
     * 便捷构造：默认过渡宽度 0.05、持续 1 tick
     *
     * @param threshold 灰度阈值
     */
    public ImpactFrame(float threshold) {
        this(threshold, 0.05f, 1, false);
    }
}
