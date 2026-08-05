package com.pasterdream.pasterdreammod.client.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxEntry;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRegistry;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 天空盒渲染器 —— 候选选择 + 防抖切换 + 逐条目淡入淡出
 * <p>
 * 流程：
 * <ol>
 *   <li>构建 {@link SkyboxRenderContext}（可见度/时间/群系快照）</li>
 *   <li>从注册表所有条目中选出"条件匹配 + 权重最高"的候选（同一天空盒
 *       文件的多个 layer 共享候选键，整套整体切换）</li>
 *   <li>候选切换带 60 tick 防抖，避免跨群系边界时天空抖动</li>
 *   <li>每条目独立透明度向目标值 lerp（{@code fadeSpeed × partialTick}），
 *       实现平滑交叉淡入淡出</li>
 * </ol>
 * 渲染挂载点：{@link com.pasterdream.pasterdreammod.client.sky.SkyboxClientEvents}
 * 的 {@code RenderLevelStageEvent.AFTER_SKY} 阶段。
 */
public final class SkyboxRenderer {

    private static final Minecraft MC = Minecraft.getInstance();

    /** 调试日志记录器 */
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SkyboxRenderer.class);

    /** 各条目当前透明度缓存（id → alpha） */
    private static final Map<String, Float> ALPHAS = new HashMap<>();

    /** 候选切换防抖时长（tick） */
    private static final float SKYBOX_SWITCH_DELAY_TICKS = 60.0F;

    private static Object lastLevel;
    private static Candidate activeCandidate;
    private static Candidate pendingCandidate;
    private static float pendingSince;

    /** 上次记录的白天时间（用于检测"新的一天"→ 每晚随机换天空） */
    private static float lastDayTime;
    /** 已检测到新的一天（下次进入夜晚时随机换） */
    private static boolean newDayDetected;
    /** 当前群系已随机选中的候选键（同群系每晚保持，跨群系重新随机） */
    private static String randomBiomeKey;

    private SkyboxRenderer() {
    }

    /**
     * 天空渲染入口（由 AFTER_SKY 事件调用）
     *
     * @param poseStack   矩阵栈
     * @param camera      相机
     * @param partialTick 帧间插值
     */
    public static void render(PoseStack poseStack, Camera camera, float partialTick) {
        SkyboxRenderContext context = makeContext(poseStack, camera, partialTick);
        if (context != null) {
            render(poseStack, context);
        }
    }

    /**
     * 判断是否需要抑制原版星星（供 LevelRendererMixin 使用）
     *
     * @return 有活跃天空盒内容时返回 true
     */
    public static boolean shouldSuppressVanillaStars() {
        if (MC.level == null || MC.player == null) {
            return false;
        }
        List<SkyboxEntry> entries = SkyboxRegistry.entries();
        if (entries.isEmpty()) {
            return false;
        }
        Candidate selected = activeCandidate;
        for (SkyboxEntry entry : entries) {
            String id = entry.content().id().toString();
            if (matchesCandidate(entry, selected) && entry.content().targetAlpha(emptyContext()) > 0.003F) {
                return true;
            }
            if (ALPHAS.getOrDefault(id, 0.0F) > 0.003F) {
                return true;
            }
        }
        return false;
    }

    /**
     * 构建空上下文占位（仅用于 targetAlpha 粗略判断）
     * <p>
     * 注意：此方法仅在候选判定阶段使用，alpha 精确值由渲染路径决定。
     *
     * @return 空上下文
     */
    private static SkyboxRenderContext emptyContext() {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        return new SkyboxRenderContext(
                mc, mc.level, camera, new PoseStack(), 0.0F,
                mc.level.getGameTime(), mc.level.getTimeOfDay(0.0F) * 360.0F,
                Math.floorMod(mc.level.getDayTime(), 24000L),
                1.0F, 1.0F, 1.0F,
                mc.level.getBiome(camera.getBlockPosition()),
                mc.level.getBiome(camera.getBlockPosition()).unwrapKey().orElse(null)
        );
    }

    /**
     * 主渲染逻辑
     *
     * @param poseStack 矩阵栈
     * @param context   渲染上下文
     */
    private static void render(PoseStack poseStack, SkyboxRenderContext context) {
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(context.skyAngle()));

        List<SkyboxEntry> entries = SkyboxRegistry.entries();
        Candidate selected = updateSelectedCandidate(entries, context);

        // 调试日志：每 5 秒打印一次当前候选与条目数（DEBUG 级别）
        if (Math.floorMod((int) context.renderTime(), 100) == 0) {
            int nightCount = rawCandidates(entries, emptyContext()).size();
            LOGGER.debug("[Skybox] 渲染: entries={} 候选={} 夜晚候选数={} 可见度={} 群系={} 白天时间={}",
                    entries.size(), selected, nightCount, String.format("%.2f", context.visibility()),
                    context.biomeKey(), String.format("%.0f", context.dayTime()));
        }

        for (SkyboxEntry entry : entries) {
            String id = entry.content().id().toString();
            float currentAlpha = ALPHAS.getOrDefault(id, 0.0F);
            float targetAlpha = matchesCandidate(entry, selected) ? entry.content().targetAlpha(context) : 0.0F;
            float speed = entry.fadeSpeed() * Math.max(context.partialTick(), 0.25F);
            float nextAlpha = Mth.lerp(speed, currentAlpha, targetAlpha);
            if (nextAlpha < 0.003F) {
                nextAlpha = 0.0F;
            }
            ALPHAS.put(id, nextAlpha);
            if (nextAlpha > 0.0F) {
                entry.content().render(context, nextAlpha);
            }
        }

        poseStack.popPose();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * 候选选择与防抖切换
     * <p>
     * 每晚（白天时间回落到清晨）从同群系的多套天空 JSON 候选池中随机选一套，
     * 实现"每个夜晚不同的随机天空效果"。跨群系仍带 60 tick 防抖避免抖动。
     *
     * @param entries 全部条目
     * @param context 渲染上下文
     * @return 当前活跃候选（可为 null）
     */
    private static Candidate updateSelectedCandidate(List<SkyboxEntry> entries, SkyboxRenderContext context) {
        if (lastLevel != context.level()) {
            lastLevel = context.level();
            activeCandidate = null;
            pendingCandidate = null;
            pendingSince = context.renderTime();
            randomBiomeKey = null;
            ALPHAS.clear();
        }

        // 检测新的一天：白天时间从高（>20000）回落到低（<1000）
        if (lastDayTime > 20000.0F && context.dayTime() < 1000.0F) {
            newDayDetected = true;
        }
        lastDayTime = context.dayTime();

        // 收集当前匹配的全部候选（同群系可能有多套 JSON 变体）
        List<Candidate> matches = rawCandidates(entries, context);
        if (matches.isEmpty()) {
            activeCandidate = null;
            pendingCandidate = null;
            randomBiomeKey = null;
            return null;
        }

        // 群系变化 → 重置随机键，下次重新随机
        String biomeKeyStr = String.valueOf(context.biomeKey());
        if (randomBiomeKey != null && !randomBiomeKey.equals(biomeKeyStr)) {
            randomBiomeKey = null;
            newDayDetected = true;
        }

        // 每晚随机：从候选池随机选一套
        Candidate candidate;
        if (matches.size() == 1) {
            candidate = matches.get(0);
        } else if (newDayDetected || activeCandidate == null || randomBiomeKey == null) {
            candidate = matches.get(MC.level.getRandom().nextInt(matches.size()));
            randomBiomeKey = biomeKeyStr;
            newDayDetected = false;
            LOGGER.debug("[Skybox] 每晚随机: 群系={} 候选={} (共{}套)", biomeKeyStr, candidate.key(), matches.size());
        } else {
            // 保持当前已随机选中的套（同群系内不频繁切换）
            Candidate keep = null;
            for (Candidate m : matches) {
                if (m.key().equals(activeCandidate != null ? activeCandidate.key() : null)) {
                    keep = m;
                    break;
                }
            }
            candidate = keep != null ? keep : matches.get(0);
        }

        if (candidate.equals(activeCandidate)) {
            pendingCandidate = null;
            return activeCandidate;
        }
        // 从"无候选"状态首次出现 → 立即激活
        if (activeCandidate == null) {
            activeCandidate = candidate;
            pendingCandidate = null;
            return activeCandidate;
        }
        if (!candidate.equals(pendingCandidate)) {
            pendingCandidate = candidate;
            pendingSince = context.renderTime();
            return activeCandidate;
        }
        if (context.renderTime() - pendingSince >= SKYBOX_SWITCH_DELAY_TICKS) {
            activeCandidate = pendingCandidate;
            pendingCandidate = null;
        }
        return activeCandidate;
    }

    /**
     * 原始候选列表（条件匹配 + targetAlpha > 0 的全部候选）
     * <p>
     * 同群系可能有多套 JSON 变体，全部返回供每晚随机选择。
     *
     * @param entries 全部条目
     * @param context 渲染上下文
     * @return 候选列表（可为空）
     */
    private static List<Candidate> rawCandidates(List<SkyboxEntry> entries, SkyboxRenderContext context) {
        List<Candidate> candidates = new java.util.ArrayList<>();
        // 按 skyboxKey 去重（同文件多个 layer 只算一个候选）
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (SkyboxEntry entry : entries) {
            if (entry.condition().matches(context) && entry.content().targetAlpha(context) > 0.003F) {
                String key = skyboxKey(entry.content().id());
                if (seen.add(key)) {
                    candidates.add(new Candidate(key, entry.weight()));
                }
            }
        }
        return candidates;
    }

    /**
     * 条目是否属于指定候选（同 key + 同 weight）
     *
     * @param entry     条目
     * @param candidate 候选
     * @return 是否匹配
     */
    private static boolean matchesCandidate(SkyboxEntry entry, Candidate candidate) {
        return candidate != null && entry.weight() == candidate.weight() && skyboxKey(entry.content().id()).equals(candidate.key());
    }

    /**
     * 提取候选键（去掉 /layer_N 后缀，同一文件所有 layer 共享）
     *
     * @param id 内容资源路径
     * @return 候选键
     */
    private static String skyboxKey(ResourceLocation id) {
        String path = id.getPath();
        int slashIndex = path.indexOf('/');
        if (slashIndex >= 0) {
            path = path.substring(0, slashIndex);
        }
        return id.getNamespace() + ":" + path;
    }

    /**
     * 构建渲染上下文
     *
     * @param poseStack   矩阵栈
     * @param camera      相机
     * @param partialTick 帧间插值
     * @return 上下文（不可见时返回 null）
     */
    private static SkyboxRenderContext makeContext(PoseStack poseStack, Camera camera, float partialTick) {
        if (MC.level == null || MC.player == null || !hasSupportedSky()) {
            return null;
        }
        float occlusion = getSkyOcclusion(camera);
        if (occlusion <= 0.0F) {
            return null;
        }
        BlockPos position = BlockPos.containing(camera.getPosition());
        Holder<Biome> biome = MC.level.getBiome(position);
        ResourceKey<Biome> biomeKey = biome.unwrapKey().orElse(null);
        float nightFactor = getNightFactor(partialTick);
        float weatherFactor = getWeatherFactor(partialTick);
        float visibility = Mth.clamp(nightFactor * weatherFactor * occlusion, 0.0F, 1.0F);
        float renderTime = MC.level.getGameTime() + partialTick;
        float timeOfDay = MC.level.getTimeOfDay(partialTick);
        float skyAngle = timeOfDay * 360.0F;
        float dayTime = Math.floorMod(MC.level.getDayTime(), 24000L) + partialTick;
        if (dayTime >= 24000.0F) {
            dayTime -= 24000.0F;
        }
        return new SkyboxRenderContext(
                MC, MC.level, camera, poseStack, partialTick, renderTime,
                skyAngle, dayTime, visibility, nightFactor, weatherFactor, biome, biomeKey
        );
    }

    /**
     * 支持天空渲染的维度（天空类型为 NORMAL 或 END 即可）
     * <p>
     * 染梦维度注册了 SkyType.NORMAL 特效（{@code pasterdream:dyedream_world}），
     * 主世界与末地同样支持；无天空的维度（竞技场 NONE、影灯无特效）不渲染。
     * 具体维度过滤由天空盒 JSON 的 {@code dimensions} 条件完成——在无匹配
     * 条目的维度中候选为空，自然不绘制任何内容。
     *
     * @return 是否支持
     */
    private static boolean hasSupportedSky() {
        if (MC.level == null) {
            return false;
        }
        DimensionSpecialEffects.SkyType skyType = MC.level.effects().skyType();
        return skyType == DimensionSpecialEffects.SkyType.NORMAL
                || skyType == DimensionSpecialEffects.SkyType.END;
    }

    /**
     * 视线遮挡（水中/岩浆/失明时不可见天空）
     *
     * @param camera 相机
     * @return 0 或 1
     */
    private static float getSkyOcclusion(Camera camera) {
        FogType fogType = camera.getFluidInCamera();
        if (fogType == FogType.LAVA || fogType == FogType.POWDER_SNOW || fogType == FogType.WATER) {
            return 0.0F;
        }
        if (camera.getEntity() instanceof LivingEntity entity
                && (entity.hasEffect(MobEffects.BLINDNESS) || entity.hasEffect(MobEffects.DARKNESS))) {
            return 0.0F;
        }
        return 1.0F;
    }

    /**
     * 夜晚因子（末地恒 1；其余按太阳高度计算昼夜强度）
     * <p>
     * 基于 {@code getSunAngle} 的太阳高度（sin 值）构建明确昼夜模型：
     * <ul>
     *   <li>太阳低于地平线（sin < 0，含 time 18000 半夜）→ 夜晚因子 = 1（天象全亮峰值）</li>
     *   <li>太阳在地平线附近（黄昏/黎明）→ 0~1 快速过渡</li>
     *   <li>太阳升高（白天）→ 0</li>
     * </ul>
     * 相比 {@code getStarBrightness * 2}，此模型不受自定义维度星亮度 API 影响，
     * 且能保证整个夜晚（含半夜 time 18000）所有天象达到最高亮度。
     *
     * @param partialTick 帧间插值
     * @return 0~1
     */
    private static float getNightFactor(float partialTick) {
        if (MC.level == null) {
            return 0.0F;
        }
        if (MC.level.dimension() == Level.END) {
            return 1.0F;
        }
        float sunHeight = Mth.sin(MC.level.getSunAngle(partialTick));
        // sunHeight: 1=正午, 0=地平线, -1=午夜
        // 太阳低于地平线后快速进入全夜模式（斜率 4 + 0.2 基线）
        return Mth.clamp(-sunHeight * 4.0F + 0.2F, 0.0F, 1.0F);
    }

    /**
     * 天气因子（雨天变暗）
     *
     * @param partialTick 帧间插值
     * @return 0~1
     */
    private static float getWeatherFactor(float partialTick) {
        if (MC.level == null) {
            return 0.0F;
        }
        return 1.0F - MC.level.getRainLevel(partialTick) * 0.65F;
    }

    /**
     * 候选记录
     *
     * @param key    候选键（命名空间 + 文件路径）
     * @param weight 权重
     */
    private record Candidate(String key, int weight) {
    }
}
