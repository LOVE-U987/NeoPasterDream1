package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.block.entity.DollBlockEntity;
import com.pasterdream.pasterdreammod.client.model.LegacyDollBlockModel;

/**
 * 旧模型玩偶方块渲染器
 * <p>
 * 继承 {@link MemorialDollBlockRenderer}，使用 {@link LegacyDollBlockModel}
 * 按 MemorialDollBlock 约定渲染旧模型玩偶。
 */
public class LegacyDollBlockRenderer extends MemorialDollBlockRenderer<DollBlockEntity> {

    /**
     * 构造旧模型玩偶方块渲染器
     *
     * @param name 玩偶注册名，用于定位模型与纹理资源
     */
    public LegacyDollBlockRenderer(String name) {
        super(new LegacyDollBlockModel(name));
    }
}
