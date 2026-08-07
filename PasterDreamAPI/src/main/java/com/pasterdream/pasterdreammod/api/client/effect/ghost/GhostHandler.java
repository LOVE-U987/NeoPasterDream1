package com.pasterdream.pasterdreammod.api.client.effect.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pasterdream.pasterdreammod.api.client.util.ColoredVertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 客户端残影处理器 —— 为指定实体生成半透明残影拖尾
 * <p>
 * 借鉴开源模组 FDBosses（作者 FINDERFEED）的 {@code EntityGhostParticle} 设计思路
 * （独立实现，非复制）：
 * <ul>
 *   <li>{@link #start(int, int, int)}：服务端包落地时开启残影源；</li>
 *   <li>{@link #tickAll()}：每 tick 采样目标实体位置生成一个残影快照
 *       （记录位置 + 当时 yaw + 初始 alpha）；</li>
 *   <li>{@link #renderAll(org.joml.Matrix4f, Camera, float)}：渲染阶段对每个快照
 *       用实体渲染器重渲染实体副本，经 {@link ColoredVertexConsumer} 强制半透明白，
 *       <b>alpha 按每个快照自身的 age 独立渐出</b>（拖尾越靠后越淡）。</li>
 * </ul>
 * <p>
 * 说明：残影的位置/朝向冻结在采样点，但实体骨骼动画是实时渲染的（GeckoLib/
 * 玩家渲染器机制），残影会随动画运动——这是本实现的固有行为。
 * <p>
 * 本类为客户端专用，仅由 {@code api/client/**} 路径持有；渲染由主模
 * {@code PDEffectClientEvents} 在 {@code RenderLevelStageEvent.AFTER_ENTITIES} 驱动。
 */
@OnlyIn(Dist.CLIENT)
public final class GhostHandler {

    /** 残影初始透明度默认值 */
    public static final int DEFAULT_ALPHA = 50;

    /** 单个残影快照的寿命（tick）：拖尾长度 */
    private static final int TRAIL_LIFETIME = 12;

    /**
     * 最小采样间距（格²）：实体位移不足该距离时跳过采样。
     * <p>
     * 位移小的实体（如暗影之手 6 格冲刺，每 tick 约 0.6 格）若每 tick 都采样，
     * 快照全叠在一起看不出渐变；间距过滤让 6 格位移内生成约 6 个隔开约 1 格的
     * 快照，渐变更清晰。位移大的实体（BOSS 冲锋）不受影响。
     */
    private static final double MIN_SAMPLE_DIST_SQ = 1.0 * 1.0;

    /** 活跃残影快照列表 */
    private static final List<GhostTrail> TRAILS = new ArrayList<>();

    /** 当前活跃的残影源参数 */
    private static ActiveGhost active;

    /** 上一次采样位置（间距过滤用，null 表示尚未采样） */
    private static Vec3 lastSamplePos;

    private GhostHandler() {
        throw new UnsupportedOperationException("GhostHandler 是纯静态门面类，不可实例化");
    }

    /**
     * 开启残影源
     *
     * @param entityId 目标实体网络 id
     * @param duration 残影持续 tick 数（源存在期间每 tick 采样）
     * @param alpha    初始透明度（0-255）
     */
    public static void start(int entityId, int duration, int alpha) {
        active = new ActiveGhost(entityId, duration, alpha);
        lastSamplePos = null; // 重置间距过滤，新残影源首次采样必生成
    }

    /**
     * 是否残影激活
     *
     * @return 激活返回 {@code true}
     */
    public static boolean isActive() {
        return active != null;
    }

    /**
     * 客户端每 tick 采样实体位置生成残影快照（由 PDEffectClientEvents 驱动）
     */
    public static void tickAll() {
        // 清理过期快照 + 推进各快照 age
        Iterator<GhostTrail> it = TRAILS.iterator();
        while (it.hasNext()) {
            GhostTrail trail = it.next();
            if (trail.tick()) {
                it.remove();
            }
        }

        if (active == null) {
            return;
        }

        Entity entity = getEntity(active.entityId);
        if (entity != null) {
            Vec3 pos = entity.position();
            // 最小间距过滤：位移不足时跳过采样，避免快照重叠看不出渐变。
            // 首次采样（lastSamplePos 为 null）必定生成。
            if (lastSamplePos == null || pos.distanceToSqr(lastSamplePos) >= MIN_SAMPLE_DIST_SQ) {
                // 每个快照记录采样时的初始 alpha，渲染时按自身 age 独立渐出。
                TRAILS.add(new GhostTrail(active.entityId, pos,
                        entity.getYRot(), active.alpha, TRAIL_LIFETIME));
                lastSamplePos = pos;
            }
        }

        active.age++;
        if (active.age > active.duration) {
            active = null;
            lastSamplePos = null;
        }
    }

    /**
     * 渲染全部残影快照（由 PDEffectClientEvents 在 AFTER_ENTITIES 阶段驱动）
     * <p>
     * 矩阵方式：<b>只 translate 相对相机的偏移（pos - camPos），不乘 modelView</b>。
     * {@code RenderType} 的 shader 会用当前全局 ModelViewMat（相机 view）做变换，
     * 若再手动乘 modelView 会双重变换导致残影跑到视野外不可见。
     *
     * @param modelView 模型视图矩阵（保留参数以对齐调用签名，实际不使用）
     * @param camera    相机（取位置计算相对偏移）
     * @param partialTick 部分 tick
     */
    public static void renderAll(org.joml.Matrix4f modelView, Camera camera, float partialTick) {
        if (TRAILS.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Vec3 camPos = camera.getPosition();

        for (GhostTrail trail : TRAILS) {
            Entity entity = getEntity(trail.entityId);
            if (entity == null || entity.isRemoved()) {
                continue;
            }

            // 该快照当前 alpha：按自身 age 独立渐出（越新越亮）
            float fade = 1.0f - trail.age / (float) trail.lifetime;
            int alpha = Math.max(2, (int) (trail.initialAlpha * fade));

            // 相对相机偏移（shader 的 view 负责相机变换）
            PoseStack poseStack = new PoseStack();
            Vec3 offset = trail.pos.subtract(camPos);
            poseStack.translate(offset.x, offset.y, offset.z);

            EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
            @SuppressWarnings("unchecked")
            EntityRenderer<Entity> renderer = (EntityRenderer<Entity>) (EntityRenderer<?>) dispatcher.getRenderer(entity);
            if (renderer == null) {
                continue;
            }

            // 半透明白过滤所有顶点颜色
            MultiBufferSource bufferSource = ColoredVertexConsumer.wrapBufferSource(
                    mc.renderBuffers().bufferSource(), 255, 255, 255, alpha);
            // partialTick 固定 0：残影定格在采样时刻的姿态，避免 GeckoLib 实时动画
            // 让所有残影同步运动、透明度差异被淹没（拖尾渐变更清晰）。
            renderer.render(entity, trail.yRot, 0.0f, poseStack, bufferSource,
                    trail.light);
        }
    }

    /**
     * 玩家登出/世界卸载时清空
     */
    public static void clearAll() {
        TRAILS.clear();
        active = null;
        lastSamplePos = null;
    }

    /** 测试辅助：清空 */
    public static void resetForTesting() {
        clearAll();
    }

    /** 按 id 获取实体 */
    private static Entity getEntity(int id) {
        return Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.getEntity(id) : null;
    }

    /** 残影源参数 */
    private static class ActiveGhost {
        final int entityId;
        final int duration;
        final int alpha;
        int age;

        ActiveGhost(int entityId, int duration, int alpha) {
            this.entityId = entityId;
            this.duration = Math.max(1, duration);
            this.alpha = alpha;
        }
    }

    /** 单个残影快照 */
    private static class GhostTrail {
        final int entityId;
        final Vec3 pos;
        final float yRot;
        final int initialAlpha;
        final int light;
        final int lifetime;
        int age;

        GhostTrail(int entityId, Vec3 pos, float yRot, int initialAlpha, int lifetime) {
            this.entityId = entityId;
            this.pos = pos;
            this.yRot = yRot;
            this.initialAlpha = initialAlpha;
            this.light = 0xF000F0; // 全亮度
            this.lifetime = lifetime;
            this.age = 0;
        }

        /**
         * 每 tick 推进，返回是否已过期
         *
         * @return 过期返回 {@code true}（应移除）
         */
        boolean tick() {
            return ++age >= lifetime;
        }
    }
}
