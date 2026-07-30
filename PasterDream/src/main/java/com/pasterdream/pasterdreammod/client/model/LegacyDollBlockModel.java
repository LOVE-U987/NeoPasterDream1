package com.pasterdream.pasterdreammod.client.model;

import com.pasterdream.pasterdreammod.block.entity.DollBlockEntity;

/**
 * 旧模型玩偶方块模型
 * <p>
 * 继承 {@link MemorialDollBlockModel}，沿用 <code>&lt;name&gt;_holding.geo.json</code>
 * 切换抱物模型的约定，用于 {@code DollAPI} 注册的旧模型玩偶。
 */
public class LegacyDollBlockModel extends MemorialDollBlockModel<DollBlockEntity> {

    /**
     * 构造旧模型玩偶方块模型
     *
     * @param name 模型基础注册名，如 {@code love_u_doll}
     */
    public LegacyDollBlockModel(String name) {
        super(name);
    }
}
