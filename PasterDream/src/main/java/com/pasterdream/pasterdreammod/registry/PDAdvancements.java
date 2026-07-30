package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * 成就（Advancement）ID 常量与授予/查询工具。
 * <p>
 * 数据定义在 {@code data/pasterdream/advancement/}（1.21.1 单数目录）。
 * 原版大量 {@code minecraft:impossible} 成就由 procedure 代码授予；
 * 本类集中 {@link #award} / {@link #has}，避免各处复制粘贴。
 */
public final class PDAdvancements {

    // ==================== 主线 / 显示 ====================

    public static final ResourceLocation START = id("achievement_start");
    public static final ResourceLocation A_0 = id("achievement_a_0");
    public static final ResourceLocation A_1 = id("achievement_a_1");
    public static final ResourceLocation B_0 = id("achievement_b_0");
    public static final ResourceLocation B_1 = id("achievement_b_1");
    public static final ResourceLocation B_2 = id("achievement_b_2");
    public static final ResourceLocation B_3 = id("achievement_b_3");
    public static final ResourceLocation C_0 = id("achievement_c_0");
    public static final ResourceLocation C_1 = id("achievement_c_1");
    public static final ResourceLocation C_2 = id("achievement_c_2");
    public static final ResourceLocation C_3 = id("achievement_c_3");
    public static final ResourceLocation C_4 = id("achievement_c_4");
    public static final ResourceLocation D_0 = id("achievement_d_0");
    public static final ResourceLocation END_0 = id("achievement_end_0");
    public static final ResourceLocation NETHER_0 = id("achievement_nether_0");
    public static final ResourceLocation ADVENTURE_0 = id("achievement_adventure_0");
    public static final ResourceLocation SPECIAL_0 = id("achievement_special_0");

    // ==================== 灯影 / 影之抉择 ====================

    public static final ResourceLocation SHADOW_START = id("achievement_shadow_start");
    public static final ResourceLocation SHADOW_A_0 = id("achievement_shadow_a_0");
    public static final ResourceLocation SHADOW_A_1 = id("achievement_shadow_a_1");
    public static final ResourceLocation SHADOW_B_0 = id("achievement_shadow_b_0");
    public static final ResourceLocation SHADOW_C_0 = id("achievement_shadow_c_0");
    public static final ResourceLocation SHADOW_D_0 = id("achievement_shadow_d_0");
    public static final ResourceLocation SHADOW_E_0 = id("achievement_shadow_e_0");
    public static final ResourceLocation TALENT_LIGHT = id("achievement_talent_light");
    public static final ResourceLocation TALENT_SHADOW = id("achievement_talent_shadow");

    public static final ResourceLocation SHADOW_NPC_0 = id("achievement_shadow_npc_0");
    public static final ResourceLocation SHADOW_NPC_1 = id("achievement_shadow_npc_1");
    public static final ResourceLocation SHADOW_NPC_2 = id("achievement_shadow_npc_2");
    public static final ResourceLocation SHADOW_NPC_3 = id("achievement_shadow_npc_3");
    public static final ResourceLocation SHADOW_NPC_4 = id("achievement_shadow_npc_4");
    public static final ResourceLocation SHADOW_NPC_5 = id("achievement_shadow_npc_5");

    // ==================== 宝藏树 ====================

    public static final ResourceLocation TREASURE_START = id("achievement_treasure_start");
    public static final ResourceLocation TREASURE_DYEDREAM = id("achievement_treasure_dyedream");
    public static final ResourceLocation TREASURE_WIND_JOURNEY = id("achievement_treasure_wind_journey");

    // ==================== 隐藏追踪 ====================

    public static final ResourceLocation HIDE_0 = id("achievement_hide_0");
    public static final ResourceLocation HIDE_1 = id("achievement_hide_1");
    public static final ResourceLocation HIDE_2 = id("achievement_hide_2");
    public static final ResourceLocation HIDE_3 = id("achievement_hide_3");
    public static final ResourceLocation HIDE_4 = id("achievement_hide_4");
    /** 首次接触染梦裂隙 */
    public static final ResourceLocation HIDE_5 = id("achievement_hide_5");
    /** 首次登录赠笔记 */
    public static final ResourceLocation HIDE_6 = id("achievement_hide_6");
    public static final ResourceLocation HIDE_7 = id("achievement_hide_7");
    public static final ResourceLocation HIDE_8 = id("achievement_hide_8");
    public static final ResourceLocation HIDE_9 = id("achievement_hide_9");
    public static final ResourceLocation HIDE_10 = id("achievement_hide_10");
    public static final ResourceLocation HIDE_11 = id("achievement_hide_11");
    public static final ResourceLocation HIDE_12 = id("achievement_hide_12");
    public static final ResourceLocation HIDE_13 = id("achievement_hide_13");
    public static final ResourceLocation HIDE_14 = id("achievement_hide_14");
    public static final ResourceLocation HIDE_15 = id("achievement_hide_15");
    public static final ResourceLocation HIDE_16 = id("achievement_hide_16");

    private PDAdvancements() {
    }

    /**
     * 构造本模组成就 {@link ResourceLocation}。
     *
     * @param path 成就路径（如 {@code achievement_start}）
     * @return 命名空间 {@code pasterdream} 的 ID
     */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path);
    }

    /**
     * 查询玩家是否已完成指定成就（路径简写）。
     *
     * @param player 服务端玩家
     * @param path   如 {@code achievement_start}
     * @return 已完成 true；holder 缺失或未完成 false
     */
    public static boolean has(ServerPlayer player, String path) {
        return has(player, id(path));
    }

    /**
     * 查询玩家是否已完成指定成就。
     *
     * @param player 服务端玩家
     * @param advId  成就 ID
     * @return 已完成 true；holder 缺失或未完成 false
     */
    public static boolean has(ServerPlayer player, ResourceLocation advId) {
        if (player == null || advId == null) {
            return false;
        }
        AdvancementHolder holder = player.server.getAdvancements().get(advId);
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    /**
     * 对任意实体安全查询（非服务端玩家恒 false）。
     *
     * @param entity 实体
     * @param path   成就路径
     * @return 是否已完成
     */
    public static boolean has(Entity entity, String path) {
        return entity instanceof ServerPlayer sp && has(sp, path);
    }

    /**
     * 授予成就全部剩余 criteria（路径简写）。
     *
     * @param player 服务端玩家
     * @param path   如 {@code achievement_hide_5}
     * @return 本次新授予 true；已完成/缺失/失败 false
     */
    public static boolean award(ServerPlayer player, String path) {
        return award(player, id(path));
    }

    /**
     * 授予成就全部剩余 criteria。
     *
     * @param player 服务端玩家
     * @param advId  成就 ID
     * @return 本次新授予 true；已完成/缺失/失败 false
     */
    public static boolean award(ServerPlayer player, ResourceLocation advId) {
        if (player == null || advId == null) {
            return false;
        }
        AdvancementHolder holder = player.server.getAdvancements().get(advId);
        if (holder == null) {
            return false;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (progress.isDone()) {
            return false;
        }
        boolean any = false;
        for (String criteria : progress.getRemainingCriteria()) {
            if (player.getAdvancements().award(holder, criteria)) {
                any = true;
            }
        }
        return any || progress.isDone();
    }

    /**
     * 对任意实体安全授予（非服务端玩家恒 false）。
     *
     * @param entity 实体
     * @param path   成就路径
     * @return 是否新授予
     */
    public static boolean award(Entity entity, String path) {
        return entity instanceof ServerPlayer sp && award(sp, path);
    }

    /**
     * {@link Player} 重载（仅服务端玩家生效）。
     *
     * @param player 玩家
     * @param path   成就路径
     * @return 是否已完成
     */
    public static boolean has(Player player, String path) {
        return player instanceof ServerPlayer sp && has(sp, path);
    }
}
