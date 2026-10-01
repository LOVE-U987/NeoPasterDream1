package com.pasterdream.pasterdreammod.api.worldgen.island;

/**
 * 岛系原型 —— 决定一座岛系的形貌生成规则
 * <p>
 * 原型由 {@link IslandLayoutManager} 依据世界种子与集群坐标确定性推导，
 * 供地形生成器、群系源与装饰 Feature 共同查询，保证三方判定一致。
 *
 * @author PasterDream Team
 */
public enum IslandArchetype {

    /** 普通原型：主岛 + 环岛 + 云托完整结构，允许风柏森林与蛀空生态圈 */
    NORMAL,

    /**
     * 风蚀原型：长年大风侵蚀，环岛已丢失；向风面圆滑、背风面凹凸不平，
     * 并有吹落的主岛方块形成的拖尾；地面几乎不生长植被。
     */
    WIND_ERODED
}
