package com.pasterdream.pasterdreammod.api.client.effect.shake;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pasterdream.pasterdreammod.api.effect.shake.ScreenShakeData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 客户端屏幕晃动处理器 —— 维护活跃晃动实例并对投影矩阵施加偏移
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ScreenShake}/{@code DefaultShake}
 * 设计思路（独立实现，非复制）：
 * <ul>
 *   <li>{@link #add(ScreenShakeData)}：服务端包落地时加入晃动实例；</li>
 *   <li>{@link #tickAll()}：每 tick 推进实例、过期移除；</li>
 *   <li>{@link #processShakes(PoseStack, float)}：渲染时对投影矩阵 translate
 *       确定性随机偏移（按 tick 种子随机 + 上一帧位置 lerp 平滑），
 *       强度按 in/stay/out 三阶段衰减。</li>
 * </ul>
 * <p>
 * 本类为客户端专用，仅由 {@code api/client/**} 路径持有；由主模
 * {@code GameRendererMixin} 在 {@code bobHurt} 注入点调用 {@link #processShakes}，
 * {@code PDEffectClientEvents} 在客户端 tick 调 {@link #tickAll()}。
 */
@OnlyIn(Dist.CLIENT)
public final class ScreenShakeHandler {

    private static final List<ScreenShakeInstance> SHAKES = new ArrayList<>();

    private ScreenShakeHandler() {
        throw new UnsupportedOperationException("ScreenShakeHandler 是纯静态门面类，不可实例化");
    }

    /**
     * 加入一个屏幕晃动实例
     *
     * @param data 晃动数据
     */
    public static void add(ScreenShakeData data) {
        SHAKES.add(new ScreenShakeInstance(data));
    }

    /**
     * 是否有活跃晃动
     *
     * @return 有返回 {@code true}
     */
    public static boolean isActive() {
        return !SHAKES.isEmpty();
    }

    /**
     * 客户端每 tick 推进晃动实例（由 PDEffectClientEvents 驱动）
     */
    public static void tickAll() {
        Iterator<ScreenShakeInstance> it = SHAKES.iterator();
        while (it.hasNext()) {
            ScreenShakeInstance inst = it.next();
            if (inst.isFinished()) {
                it.remove();
            } else {
                inst.tick();
            }
        }
    }

    /**
     * 渲染时对投影矩阵施加晃动偏移（由 GameRendererMixin 的 bobHurt 注入点调用）
     * <p>
     * 每个晃动实例按当前时间产生确定性随机偏移（X/Y），并对上一帧位置
     * lerp 平滑，避免画面抖闪跳变；强度按 in/stay/out 三阶段衰减。
     *
     * @param projection 投影 PoseStack（相机震动矩阵）
     * @param partialTick 部分 tick
     */
    public static void processShakes(PoseStack projection, float partialTick) {
        if (SHAKES.isEmpty() || Minecraft.getInstance().level == null
                || Minecraft.getInstance().isPaused()) {
            return;
        }
        for (ScreenShakeInstance inst : SHAKES) {
            inst.process(projection, partialTick);
        }
    }

    /**
     * 玩家登出/世界卸载时清空
     */
    public static void clearAll() {
        SHAKES.clear();
    }

    /** 测试辅助：清空 */
    public static void resetForTesting() {
        clearAll();
    }

    /** 单个屏幕晃动实例 */
    @OnlyIn(Dist.CLIENT)
    private static class ScreenShakeInstance {

        private final ScreenShakeData data;
        private int currentTime;
        private double xo;
        private double yo;
        private int lastTime = -1;

        ScreenShakeInstance(ScreenShakeData data) {
            this.data = data;
        }

        void tick() {
            currentTime = Mth.clamp(currentTime + 1, 0, data.duration());
        }

        boolean isFinished() {
            return currentTime >= data.duration();
        }

        void process(PoseStack projection, float partialTick) {
            float power = getPower(currentTime + partialTick);
            if (power <= 0.0001f) {
                return;
            }
            // 确定性随机：以 tick 为种子，保证每帧偏移稳定、平滑
            long t = currentTime + 1;
            Random random = new Random(t * 34324L);
            double x = randomN(random, power);
            double y = randomN(random, power);

            double xd = Mth.lerp(partialTick, xo, x);
            double yd = Mth.lerp(partialTick, yo, y);

            projection.translate(Double.isNaN(xd) ? 0 : xd, Double.isNaN(yd) ? 0 : yd, 0);

            if (lastTime != currentTime) {
                lastTime = currentTime;
                t--;
                Random r = new Random(t * 34324L);
                xo = randomN(r, power);
                yo = randomN(r, power);
            }
        }

        /** 三阶段强度：in 0→1, stay 1, out 1→0 */
        private float getPower(float time) {
            int in = data.inTime();
            int stay = data.stayTime();
            int out = data.outTime();
            if (in > 0 && time <= in) {
                return Mth.clamp(time / in, 0, 1);
            } else if (time <= in + stay) {
                return 1.0f;
            } else if (out > 0 && time <= in + stay + out) {
                return Mth.clamp(1 - (time - in - stay) / out, 0, 1);
            }
            return 0;
        }

        private double randomN(Random random, float power) {
            return (random.nextFloat() * data.amplitude() * 2 - data.amplitude()) * power;
        }
    }
}
