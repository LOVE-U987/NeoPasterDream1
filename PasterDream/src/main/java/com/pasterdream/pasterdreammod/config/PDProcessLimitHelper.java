package com.pasterdream.pasterdreammod.config;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

/**
 * 进度限制辅助工具
 * <p>
 * 提供便捷方法检查各项进度限制是否应当生效。
 * 优先级规则：
 * <ol>
 *   <li>总开关关闭 → 所有限制不生效</li>
 *   <li>创造模式跳过开启 → 创造模式玩家所有限制不生效</li>
 *   <li>单个限制开关关闭 → 该限制不生效</li>
 * </ol>
 *
 * @author PasterDream
 */
public final class PDProcessLimitHelper {

    private PDProcessLimitHelper() {
    }

    /**
     * 检查玩家是否应当跳过所有进度限制（总开关 + 创造模式）。
     *
     * @param player 玩家
     * @return true = 跳过所有进度限制
     */
    public static boolean shouldBypassAll(Player player) {
        if (!PDCommonConfig.ENABLE_PROCESS_LIMIT.get()) {
            return true;
        }
        if (Boolean.TRUE.equals(PDCommonConfig.CREATIVE_BYPASS_PROCESS_LIMIT.get())
                && player.getAbilities().instabuild) {
            return true;
        }
        return false;
    }

    /**
     * 检查某项具体的进度限制是否应当生效。
     * <p>
     * 在总开关、创造模式跳过、该限制开关均为开启状态时返回 true，
     * 表示该限制应被正常执行。
     *
     * @param player            玩家
     * @param restrictionToggle 该限制的配置开关
     * @return true = 该限制应当生效（玩家需满足前置成就方可操作）
     */
    public static boolean shouldApplyRestriction(Player player,
                                                  Supplier<Boolean> restrictionToggle) {
        if (shouldBypassAll(player)) {
            return false;
        }
        return Boolean.TRUE.equals(restrictionToggle.get());
    }

    /**
     * 检查某项具体的进度限制是否应当生效（重载，接受 ModConfigSpec.ConfigValue）。
     *
     * @param player            玩家
     * @param restrictionToggle 该限制的配置开关
     * @return true = 该限制应当生效
     */
    public static boolean shouldApplyRestriction(Player player,
                                                  ModConfigSpec.ConfigValue<Boolean> restrictionToggle) {
        return shouldApplyRestriction(player, restrictionToggle::get);
    }
}
