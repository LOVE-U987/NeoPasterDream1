package com.pasterdream.pasterdreammod.client.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxEntry;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRegistry;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRenderContext;
import com.pasterdream.pasterdreammod.client.sky.content.ConstellationSkyContent;
import com.pasterdream.pasterdreammod.client.sky.content.SkyLinkContent;
import com.pasterdream.pasterdreammod.client.sky.content.TexturedPlanetSystemSkyContent;
import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
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

    /** 各条目当前透明度缓存（id → alpha） */
    private static final Map<String, Float> ALPHAS = new HashMap<>();

    /** 候选切换防抖时长（tick） */
    private static final float SKYBOX_SWITCH_DELAY_TICKS = 60.0F;

    private static Object lastLevel;
    private static Candidate activeCandidate;
    private static Candidate pendingCandidate;
    private static float pendingSince;

    /** 玩家连线星体内容（星空枕创建，独立于数据驱动候选渲染） */
    private static final SkyLinkContent SKY_LINK =
            new SkyLinkContent(ResourceLocation.fromNamespaceAndPath("pasterdream", "skylink"));
    /** 连线星体当前透明度（向目标 lerp，实现淡入淡出） */
    private static float skylinkAlpha;

    /** 上次记录的白天时间（用于检测"新的一天"→ 每晚随机换天空） */
    private static float lastDayTime;
    /** 已检测到新的一天（下次进入夜晚时随机换） */
    private static boolean newDayDetected;
    /** 当前群系已随机选中的候选键（同群系每晚保持，跨群系重新随机） */
    private static String randomBiomeKey;
    /** 会话内已提交的客户端成就路径（防重复提交到服务端线程） */
    private static final java.util.Set<String> CLIENT_AWARDED = new java.util.HashSet<>();
    /** 上次群系切换的时刻（过渡窗口内放慢 alpha 交叉，实现平滑淡入淡出） */
    private static float lastBiomeSwitchTime = Float.NEGATIVE_INFINITY;
    /** 群系切换过渡窗口（tick） */
    private static final float BIOME_TRANSITION_WINDOW_TICKS = 40.0F;
    /** 上次是否为夜晚（用于检测黎明边沿 → 回退玩家夜间操作） */
    private static boolean lastNightState;
    /** 黎明检测是否已初始化（维度切换后重置） */
    private static boolean dayStateInitialized;
    /** 白天回退提示是否已发送（防重复刷屏） */
    private static boolean dayResetNotified;

    private SkyboxRenderer() {
    }

    /**
     * 检测并回退玩家的夜间操作（黎明触发一次）
     * <p>
     * 当时间从夜晚过渡到白天（夜晚因子降至 0.5 以下）时：
     * <ul>
     *   <li>清空所有玩家用星空枕绘制的连线星体（{@link PlayerSkyLinkData#clearAll}）</li>
     *   <li>重置连线星体透明度缓存</li>
     *   <li>提示本地玩家"天亮了，夜空星体已消散"</li>
     * </ul>
     * 回退只在黎明边沿执行一次，避免每帧重复清理。
     */
    private static void checkDayRollback() {
        boolean night = isNight();
        // 首次调用/维度切换后：只记录当前状态，不触发回退
        if (!dayStateInitialized) {
            dayStateInitialized = true;
            lastNightState = night;
            dayResetNotified = night;
            return;
        }
        // 夜晚 → 白天边沿（黎明）：回退所有夜间操作
        if (lastNightState && !night) {
            lastNightState = false;
            skylinkAlpha = 0.0F;
            PlayerSkyLinkData.clearAll();
            // 提示本地玩家（仅提示一次，避免多帧刷屏）
            if (MC.player != null && !dayResetNotified) {
                dayResetNotified = true;
                MC.player.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable("message.pasterdream.skylink.day_reset"),
                        true
                );
            }
        }
        // 白天 → 夜晚边沿（黄昏）：重置提示标记，允许下一个黎明再次提示
        if (!lastNightState && night) {
            lastNightState = true;
            dayResetNotified = false;
        }
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
     * 判断当前是否为夜晚（天象可见时段）
     * <p>
     * 基于 {@link #getNightFactor}，夜晚因子 &gt; 0.5 视为夜晚。
     * 用于星空枕/占卜等物品的"白天不可用"限制。
     *
     * @return 是否夜晚
     */
    public static boolean isNight() {
        if (MC.level == null) {
            return false;
        }
        return getNightFactor(0.0F) > 0.5F;
    }

    /**
     * 客户端授予成就（单机集成服务器安全路径）
     * <p>
     * 星空枕创建星体、望远镜观星等纯客户端交互无法在服务端直接触发，
     * 此方法将授予操作提交到服务端主线程执行；专用服务器（玩家不在本地）
     * 或非单机时静默跳过。
     *
     * @param path 成就路径（如 {@code achievement_stargaze}）
     */
    public static void awardClient(String path) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        // 会话内只提交一次（渲染线程每帧可能调用，防重复任务堆积）
        if (!CLIENT_AWARDED.add(path)) {
            return;
        }
        MinecraftServer server = mc.getSingleplayerServer();
        if (server == null) {
            return;
        }
        server.execute(() -> {
            ServerPlayer sp = server.getPlayerList().getPlayer(mc.player.getUUID());
            if (sp != null) {
                PDAdvancements.award(sp, path);
            }
        });
    }

    /**
     * 判断天空局部方向是否对准了"天体"（供占卜等交互检测）
     * <p>
     * 覆盖三类目标：玩家连线星体（星空枕创建）、当前候选天空中的
     * 星座星点与行星。方向比较用归一化点积，阈值由调用方指定。
     * <p>
     * ⚠️ 仅夜晚判定（白天天体不可见，不视为对准）。
     *
     * @param localLook 天空局部空间方向（世界视线逆变换后，无需乘半径）
     * @param threshold 对准夹角阈值（弧度）
     * @return 是否对准某天体
     */
    public static boolean isCelestialTargeted(SkyPoint localLook, float threshold) {
        // 白天天体不可见，直接判定未对准（防止占卜/星空枕在白天命中隐藏的连线星体）
        if (!isNight()) {
            return false;
        }
        // 1. 玩家连线星体（星空枕创建）
        if (MC.player != null) {
            for (SkyPoint star : PlayerSkyLinkData.getStars(MC.player.getUUID())) {
                float len = star.length();
                if (len > 0.001F) {
                    float dot = (star.x() * localLook.x() + star.y() * localLook.y() + star.z() * localLook.z())
                            / (len * Math.max(localLook.length(), 0.001F));
                    if (dot > Mth.cos(threshold)) {
                        return true;
                    }
                }
            }
        }
        // 2. 当前候选天空中的星座星点 / 行星
        Candidate selected = activeCandidate;
        for (SkyboxEntry entry : SkyboxRegistry.entries()) {
            if (!matchesCandidate(entry, selected)) {
                continue;
            }
            if (entry.content() instanceof ConstellationSkyContent constellation
                    && constellation.containsStarNear(localLook, threshold)) {
                return true;
            }
            if (entry.content() instanceof TexturedPlanetSystemSkyContent planetSystem
                    && planetSystem.containsPlanetNear(localLook, threshold)) {
                return true;
            }
        }
        return false;
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
        // 每帧检测黎明边沿：白天到来时回退玩家夜间操作（清除连线星体等）
        checkDayRollback();

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

        for (SkyboxEntry entry : entries) {
            String id = entry.content().id().toString();
            float currentAlpha = ALPHAS.getOrDefault(id, 0.0F);
            float targetAlpha = matchesCandidate(entry, selected) ? entry.content().targetAlpha(context) : 0.0F;
            float speed = entry.fadeSpeed() * Math.max(context.partialTick(), 0.25F);
            // 群系切换后的过渡窗口内放慢交叉速度，让星星/天体平滑淡入淡出而非突然消失/出现
            float sinceBiomeSwitch = context.renderTime() - lastBiomeSwitchTime;
            if (sinceBiomeSwitch >= 0.0F && sinceBiomeSwitch < BIOME_TRANSITION_WINDOW_TICKS) {
                speed *= 0.35F;
            }
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
     * 渲染玩家连线星体（星空枕创建，独立于候选机制）
     * <p>
     * 由 AFTER_SKY 事件在主天空渲染完成后调用；数据来自
     * {@link com.pasterdream.pasterdreammod.client.sky.PlayerSkyLinkData}，
     * 按当前可见度（夜晚）淡入淡出，跨维度切换时重置透明度。
     *
     * @param poseStack   矩阵栈（含相机旋转）
     * @param camera      相机
     * @param partialTick 帧间插值
     */
    public static void renderPlayerSkyLinks(PoseStack poseStack, Camera camera, float partialTick) {
        SkyboxRenderContext context = makeContext(poseStack, camera, partialTick);
        if (context == null) {
            return;
        }
        // 应用天空旋转（与主渲染一致：先 X(-90°) 再 Y(skyAngle)），使星体钉在天空上随夜空转动
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(context.skyAngle()));
        float target = SKY_LINK.targetAlpha(context);
        float speed = 0.15F * Math.max(context.partialTick(), 0.25F);
        skylinkAlpha = Mth.lerp(speed, skylinkAlpha, target);
        if (skylinkAlpha > 0.003F) {
            SKY_LINK.render(context, skylinkAlpha);
        }
        poseStack.popPose();
    }

    /**
     * 候选选择与防抖切换
     * <p>
     * 每晚（白天时间回落到清晨）从同群系的多套天空 JSON 候选池中随机选一套，
     * 实现"每个夜晚不同的随机天空效果"。同一群系内避免频繁切换带 60 tick
     * 防抖；跨群系切换立即激活新候选，由逐条目 alpha lerp 完成交叉淡入淡出。
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
            skylinkAlpha = 0.0F;
            lastBiomeSwitchTime = Float.NEGATIVE_INFINITY;
            // 维度切换：重置黎明检测状态，避免误触发回退
            lastNightState = false;
            dayStateInitialized = false;
            dayResetNotified = false;
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

        // 群系变化 → 重置随机键并标记"本次立即过渡"（跳过防抖，由 alpha lerp 完成交叉淡入淡出）
        String biomeKeyStr = String.valueOf(context.biomeKey());
        boolean biomeChanged = randomBiomeKey != null && !randomBiomeKey.equals(biomeKeyStr);
        if (biomeChanged) {
            randomBiomeKey = null;
            newDayDetected = true;
            // 记录群系切换时刻，渲染循环在窗口内放慢过渡实现平滑淡入淡出
            lastBiomeSwitchTime = context.renderTime();
        }

        // 每晚随机：从候选池随机选一套
        Candidate candidate;
        if (matches.size() == 1) {
            candidate = matches.get(0);
        } else if (newDayDetected || activeCandidate == null || randomBiomeKey == null) {
            candidate = matches.get(MC.level.getRandom().nextInt(matches.size()));
            randomBiomeKey = biomeKeyStr;
            newDayDetected = false;
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
        // 群系切换 → 立即激活新候选（旧候选条目淡出、新条目淡入，实现平滑过渡）
        if (biomeChanged) {
            activeCandidate = candidate;
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
     * 基于 {@code getSunAngle} 的太阳高度（cos 值）构建明确昼夜模型：
     * <ul>
     *   <li>太阳低于地平线（cos < 0，含 time 18000 半夜）→ 夜晚因子 = 1（天象全亮峰值）</li>
     *   <li>太阳在地平线附近（黄昏/黎明）→ 0~1 快速过渡</li>
     *   <li>太阳升高（白天）→ 0</li>
     * </ul>
     * ⚠️ 关键：{@code getSunAngle()} 返回 {@code timeOfDay * 2π}，其中 timeOfDay 语义为
     * 正午 = 0、午夜 = π（18000 时恰为 π）。若用 {@code sin} 判断，正午与午夜
     * 的 sin 值都是 0，会导致白天（正午）夜晚因子高达 0.2——星空白天也渲染！
     * 太阳高度必须用 {@code cos}（正午 1、午夜 -1）。
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
        float sunHeight = Mth.cos(MC.level.getSunAngle(partialTick));
        // sunHeight: 1=正午, 0=地平线, -1=午夜
        // 太阳低于地平线后渐入（斜率 3），再用 smoothstep 使黄昏/黎明的淡入淡出更柔和连续
        float night = Mth.clamp(-sunHeight * 3.0F, 0.0F, 1.0F);
        return night * night * (3.0F - 2.0F * night);
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
