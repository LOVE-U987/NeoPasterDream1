package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;

/**
 * 成就（Advancement）引用常量类
 * 定义所有自定义成就的 ResourceLocation，便于在代码中引用
 * <p>
 * 实际的成就数据定义在 data/pasterdream/advancements/ 下的 JSON 文件中
 * 参考 STORYLINE.md 中的成就链设计
 * <p>
 * 成就链概览（后续完善）：
 * - achievement_start（梦的开始）
 * - achievement_a_0（染梦裂隙）
 * - achievement_c_1（梦境果汁）
 * - ... 更多成就
 */
public class PDAdvancements {

    // ==================== 成就 ID 常量 ====================

    /*
     * 示例：成就 ResourceLocation 定义
     *
     * /** 梦的开始 /
     * public static final ResourceLocation START = ResourceLocation.fromNamespaceAndPath(
     *         PasterDreamMod.MOD_ID, "achievement_start");
     *
     * /** 染梦裂隙 /
     * public static final ResourceLocation A_0 = ResourceLocation.fromNamespaceAndPath(
     *         PasterDreamMod.MOD_ID, "achievement_a_0");
     *
     * /** 梦境果汁 /
     * public static final ResourceLocation C_1 = ResourceLocation.fromNamespaceAndPath(
     *         PasterDreamMod.MOD_ID, "achievement_c_1");
     */

    // ==================== 成就 Holder 获取方法 ====================

    /*
     * 根据 ResourceLocation 获取 AdvancementHolder 的工具方法
     *
     * public static AdvancementHolder get(ServerAdvancementManager manager, ResourceLocation id) {
     *     return manager.get(id);
     * }
     */

    /**
     * 私有构造函数，防止实例化
     */
    private PDAdvancements() {
    }
}