package com.pasterdream.pasterdreammod.api.doll;

import net.minecraft.resources.ResourceLocation;

/**
 * 玩偶配置记录
 * <p>
 * 每个通过 {@link DollAPI} 注册的玩偶都会生成一份配置，供方块交互、
 * 客户端渲染与 KubeJS 事件查询。
 *
 * @param name         玩偶注册名
 * @param model        基础模型路径
 * @param texture      皮肤纹理路径
 * @param holdingModel 抱物模型路径
 * @param canHoldItems 是否允许抱物
 * @param modelType    模型类型：新模型（{@link DollModelType#NEW}）或旧模型（{@link DollModelType#LEGACY}）
 */
public record DollConfig(
        String name,
        ResourceLocation model,
        ResourceLocation texture,
        ResourceLocation holdingModel,
        boolean canHoldItems,
        DollModelType modelType
) {
}
