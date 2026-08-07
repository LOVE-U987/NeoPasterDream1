package com.pasterdream.pasterdreammod.api.client.effect.atmosphere;

import com.pasterdream.pasterdreammod.api.network.AtmospherePayload;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 客户端氛围处理器 —— 阻尼推进雾色/暗化状态并修改雾色
 * <p>
 * 借鉴开源模组 FDBosses（作者 FINDERFEED）的 {@code BossClientEvents} 中
 * {@code chesedGazeEffectTick} 阻尼推进思路（独立实现，非复制）：
 * <ul>
 *   <li>{@link #start(AtmospherePayload)}：服务端包落地时设置目标强度；</li>
 *   <li>{@link #tick()}：当前强度平滑趋近目标强度（阻尼插值），
 *       duration 到期后目标归零自动衰减退出；</li>
 *   <li>{@link #modifyFogColor(float, float, float)}：按当前强度修改雾色
 *       （kind=0 暗化 → 灰雾；kind=1 血色 → 混入红）。</li>
 * </ul>
 * 本类为客户端专用，仅由 {@code api/client/**} 路径持有；由主模
 * {@code PDEffectClientEvents} 在客户端 tick 与 {@code ViewportEvent.ComputeFogColor} 驱动。
 */
@OnlyIn(Dist.CLIENT)
public final class AtmosphereHandler {

    /** 阻尼插值系数（每 tick 移动比例） */
    private static final float DAMPING = 0.15f;

    private static int activeKind = -1;
    private static float currentStrength;
    private static float targetStrength;
    private static int remaining;

    private AtmosphereHandler() {
        throw new UnsupportedOperationException("AtmosphereHandler 是纯静态门面类，不可实例化");
    }

    /**
     * 开启氛围
     *
     * @param payload 氛围包
     */
    public static void start(AtmospherePayload payload) {
        activeKind = payload.kind();
        targetStrength = Mth.clamp(payload.strength(), 0, 1);
        remaining = Math.max(1, payload.duration());
    }

    /**
     * 是否氛围激活（当前强度仍可见）
     *
     * @return 激活返回 {@code true}
     */
    public static boolean isActive() {
        return currentStrength > 0.005f;
    }

    /**
     * 客户端每 tick 推进阻尼状态（由 PDEffectClientEvents 驱动）
     */
    public static void tick() {
        if (remaining > 0) {
            remaining--;
            if (remaining == 0) {
                // 到期：目标归零，进入衰减
                targetStrength = 0;
            }
        }
        // 阻尼趋近目标
        currentStrength += (targetStrength - currentStrength) * DAMPING;
        if (currentStrength < 0.005f && targetStrength == 0) {
            // 衰减完成，彻底关闭
            activeKind = -1;
            currentStrength = 0;
        }
    }

    /**
     * 按当前强度修改雾色（由 PDEffectClientEvents 在 ComputeFogColor 调用）
     *
     * @param r 原始雾色红
     * @param g 原始雾色绿
     * @param b 原始雾色蓝
     * @return 修改后的雾色（长度 3）
     */
    public static float[] modifyFogColor(float r, float g, float b) {
        float p = currentStrength;
        if (p <= 0.005f) {
            return new float[]{r, g, b};
        }
        if (activeKind == AtmospherePayload.KIND_BLOOD_FOG) {
            // 血色雾：混入红色
            float nr = Mth.lerp(p, r, 0.7f);
            float ng = Mth.lerp(p, g, 0.1f);
            float nb = Mth.lerp(p, b, 0.1f);
            return new float[]{nr, ng, nb};
        }
        // 默认暗化：雾色压到极低亮度（近黑）。
        // 目标固定 0.04（近乎全黑），强度 p 时 = lerp(原色, 0.04, p)。
        // 强度 0.7 时已接近全黑，满足「从开始到最后暗度极低」的压迫感需求。
        float nearBlack = 0.04f;
        return new float[]{
                Mth.lerp(p, r, nearBlack),
                Mth.lerp(p, g, nearBlack),
                Mth.lerp(p, b, nearBlack)
        };
    }

    /**
     * 玩家登出/世界卸载时清空
     */
    public static void clearAll() {
        activeKind = -1;
        currentStrength = 0;
        targetStrength = 0;
        remaining = 0;
    }

    /** 测试辅助：清空 */
    public static void resetForTesting() {
        clearAll();
    }
}
