package com.pasterdream.pasterdreammod.api.spell;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 法术接口。
 * <p>
 * 位于 PasterDreamAPI，定义法术物品的最小契约。具体法术实现由各附属模组提供。
 *
 * @param <T> 实现类类型
 */
public interface ISpell<T extends ISpell<T>> {

    /**
     * 获取法术唯一标识名。
     *
     * @return 法术名
     */
    String getSpellName();

    /**
     * 获取法术冷却时间（tick）。
     *
     * @return 冷却 tick 数
     */
    int getCooldown();

    /**
     * 获取法术消耗（融梦能量或法力，由实现决定）。
     *
     * @return 消耗量
     */
    double getCost();

    /**
     * 尝试施放法术。
     *
     * @param level  世界
     * @param player 施法玩家
     * @param hand   施法手
     * @return 是否成功施放
     */
    boolean cast(Level level, Player player, InteractionHand hand);
}
