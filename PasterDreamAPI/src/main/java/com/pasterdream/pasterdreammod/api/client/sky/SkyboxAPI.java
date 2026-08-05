package com.pasterdream.pasterdreammod.api.client.sky;

import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * 天空盒 API 门面 —— 代码注册自定义天空内容的统一入口
 * <p>
 * 采用 Facade 模式，屏蔽 {@link SkyboxRegistry} 细节。数据驱动的
 * 天空盒（推荐）由主模块的数据包重载监听器自动加载
 * {@code data/<namespace>/skyboxes/*.json}，一般无需代码注册；
 * 本门面供需要完全程序化控制天空内容的场景使用。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 在客户端初始化时注册一条按群系显示的自定义内容
 * SkyboxAPI.register(
 *     new MySkyContent(ResourceLocation.fromNamespaceAndPath("pasterdream", "sky/my_content")),
 *     SkyCondition.biome(PDBiomes.BIOME_DYEDREAM_0)
 * );
 * }</pre>
 *
 * @see SkyboxRegistry
 * @see SkyCondition
 */
public final class SkyboxAPI {

    private SkyboxAPI() {
    }

    /**
     * 构建天空渲染用的 PoseStack（含相机旋转）
     * <p>
     * ⚠️ {@code AFTER_SKY} 事件的 {@code getPoseStack()} 为 {@code null}，
     * 直接使用会把天体当裁剪空间顶点画（贴在玩家视角）。
     * 此方法仿照原版 {@code renderSky} 内部构建含相机旋转的矩阵栈，
     * 使天体定位在世界空间天空盒上。
     *
     * @param event {@code AFTER_SKY} 阶段事件
     * @return 含相机旋转的 PoseStack
     */
    public static PoseStack buildSkyPoseStack(RenderLevelStageEvent event) {
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(event.getModelViewMatrix());
        return poseStack;
    }

    /**
     * 判断渲染阶段事件是否为 AFTER_SKY（天空渲染完成、云之前）
     * <p>
     * ⚠️ Iris 光影下必须用 AFTER_SKY 事件渲染天空内容；
     * {@code renderSky} Mixin 注入会把内容当世界几何体处理（变黑/消失）。
     *
     * @param event 渲染阶段事件
     * @return 是否为 AFTER_SKY
     */
    public static boolean isAfterSky(RenderLevelStageEvent event) {
        return event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY;
    }

    /**
     * 注册一条天空内容（默认淡入速度 0.08）
     *
     * @param content   内容实现（主模块 {@code client/sky/content} 包）
     * @param condition 激活条件
     */
    public static void register(SkyContent content, SkyCondition condition) {
        SkyboxRegistry.register(content, condition);
    }

    /**
     * 注册一条天空内容（自定义淡入速度）
     *
     * @param content   内容实现
     * @param condition 激活条件
     * @param fadeSpeed 淡入淡出速度（越大切换越快）
     */
    public static void register(SkyContent content, SkyCondition condition, float fadeSpeed) {
        SkyboxRegistry.register(content, condition, fadeSpeed);
    }

    /**
     * 获取当前全部已注册条目（代码 + 数据），按优先级升序
     *
     * @return 条目只读列表
     */
    public static java.util.List<SkyboxEntry> entries() {
        return SkyboxRegistry.entries();
    }
}
