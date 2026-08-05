package com.pasterdream.pasterdreammod.api.client.sky;

import net.minecraft.resources.ResourceLocation;

/**
 * 天空盒内容接口 —— 一种可绘制在天空中的装饰元素（星空/行星/极光/光带等）
 * <p>
 * 每种内容实现负责将自身绘制到单位球（半径 100）上，由渲染器按优先级
 * 逐层调用。内容本身不关心"何时显示"（那是 {@link SkyCondition} 的职责），
 * 只负责"如何绘制"。
 *
 * @see SkyboxRegistry
 * @see SkyCondition
 */
public interface SkyContent {

    /**
     * 内容唯一标识（JSON 文件路径派生，如 {@code pasterdream:skyboxes/warm/layer_0}）
     *
     * @return 资源路径
     */
    ResourceLocation id();

    /**
     * 绘制优先级（越小越先绘制、越靠底层）
     *
     * @return 优先级
     */
    default int priority() {
        return 0;
    }

    /**
     * 目标透明度 —— 内容在给定上下文下的期望强度（0~1）
     * <p>
     * 默认等于总可见度（夜晚才亮、雨天变暗）。特殊内容（如彩虹按时间窗、
     * 极光按太阳高度）可覆写此方法实现更精细的显隐控制。
     *
     * @param context 渲染上下文
     * @return 目标透明度 0~1
     */
    default float targetAlpha(SkyboxRenderContext context) {
        return context.visibility();
    }

    /**
     * 绘制内容
     * <p>
     * 由渲染器在设置好混合/深度状态后调用，实现负责使用当前 shader
     * 与 {@code context.poseStack()} 提交几何数据。
     *
     * @param context 渲染上下文
     * @param alpha   当前实际透明度（已含淡入淡出插值，0~1）
     */
    void render(SkyboxRenderContext context, float alpha);
}
