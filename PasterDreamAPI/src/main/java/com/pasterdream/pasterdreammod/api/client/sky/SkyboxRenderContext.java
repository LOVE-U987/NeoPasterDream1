package com.pasterdream.pasterdreammod.api.client.sky;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * 天空盒渲染上下文 —— 每帧渲染时对当前环境状态的一次快照
 * <p>
 * 由渲染器（主模块 {@code client/sky/SkyboxRenderer}）构建并传递给
 * 各个 {@link SkyContent} 的 {@link SkyContent#render} 方法，包含
 * 渲染现场对象、时间信息、可见度因子以及玩家所在群系等关键数据。
 * <p>
 * 字段含义：
 * <ul>
 *   <li>{@code visibility} —— 总可见度 = nightFactor × weatherFactor × occlusion，0~1</li>
 *   <li>{@code skyAngle} —— 天空旋转角（度），由昼夜时间驱动，用于天体随昼夜旋转</li>
 *   <li>{@code dayTime} —— 白天时间（0~24000 + partialTick），用于时间窗判断</li>
 *   <li>{@code biomeKey} —— 相机所在位置的生物群系 Key，用于群系差异化</li>
 * </ul>
 *
 * @param minecraft      Minecraft 单例
 * @param level          客户端世界
 * @param camera         当前相机
 * @param poseStack      渲染用的矩阵栈（已旋转 -90°X 与 skyAngle）
 * @param partialTick    帧间插值（0~1）
 * @param renderTime     游戏刻 + partialTick（动画时间基准）
 * @param skyAngle       天空旋转角（度）
 * @param dayTime        白天时间 0~24000
 * @param visibility     总可见度（0~1）
 * @param nightFactor    夜晚因子（0~1）
 * @param weatherFactor  天气因子（0~1，雨天变小）
 * @param biome          相机所在群系
 * @param biomeKey       相机所在群系 Key
 */
public record SkyboxRenderContext(
        Minecraft minecraft,
        ClientLevel level,
        Camera camera,
        PoseStack poseStack,
        float partialTick,
        float renderTime,
        float skyAngle,
        float dayTime,
        float visibility,
        float nightFactor,
        float weatherFactor,
        Holder<Biome> biome,
        ResourceKey<Biome> biomeKey
) {
}
