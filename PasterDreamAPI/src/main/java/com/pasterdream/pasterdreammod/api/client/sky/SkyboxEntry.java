package com.pasterdream.pasterdreammod.api.client.sky;

/**
 * 天空盒条目 —— 一条可独立淡入淡出的天空装饰记录
 *
 * @param content   内容（决定绘制方式）
 * @param condition 激活条件（决定何时显示）
 * @param fadeSpeed 透明度过渡速度（越大切换越快，默认 0.08）
 * @param weight    权重（多个满足条件的条目竞争时，权重最高者胜出）
 */
public record SkyboxEntry(SkyContent content, SkyCondition condition, float fadeSpeed, int weight) {
}
