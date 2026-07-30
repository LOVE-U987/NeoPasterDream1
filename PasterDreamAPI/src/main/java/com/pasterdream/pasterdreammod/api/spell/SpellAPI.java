package com.pasterdream.pasterdreammod.api.spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 法术系统 API 门面。
 * <p>
 * 提供轻量级的法术注册与查询能力，具体法术行为由附属模组通过 {@link #registerSpell} 注入。
 */
public final class SpellAPI {

    /** 已注册法术表：key = 法术 ResourceLocation */
    private static final Map<ResourceLocation, ISpell<?>> SPELL_REGISTRY = new HashMap<>();

    /** 玩家施法前回调：可用于扣除 San/融梦能量等 */
    private static final Map<ResourceLocation, Consumer<Player>> PRE_CAST_CALLBACKS = new HashMap<>();

    private SpellAPI() {
        throw new UnsupportedOperationException("SpellAPI 是门面类，不可实例化");
    }

    /**
     * 注册一个法术实例。
     *
     * @param id    法术 ID
     * @param spell 法术实例
     */
    public static void registerSpell(ResourceLocation id, ISpell<?> spell) {
        SPELL_REGISTRY.put(id, spell);
    }

    /**
     * 查询已注册的法术。
     *
     * @param id 法术 ID
     * @return 法术实例 Optional
     */
    public static Optional<ISpell<?>> getSpell(ResourceLocation id) {
        return Optional.ofNullable(SPELL_REGISTRY.get(id));
    }

    /**
     * 判断指定法术是否已注册。
     *
     * @param id 法术 ID
     * @return 是否已注册
     */
    public static boolean hasSpell(ResourceLocation id) {
        return SPELL_REGISTRY.containsKey(id);
    }

    /**
     * 注册施法前回调（通常由 San/融梦模组注册，用于消耗资源）。
     *
     * @param id       法术 ID
     * @param callback 回调
     */
    public static void registerPreCastCallback(ResourceLocation id, Consumer<Player> callback) {
        PRE_CAST_CALLBACKS.put(id, callback);
    }

    /**
     * 执行施法前回调。
     *
     * @param id     法术 ID
     * @param player 玩家
     */
    public static void runPreCastCallbacks(ResourceLocation id, @Nullable Player player) {
        if (player == null) return;
        Consumer<Player> callback = PRE_CAST_CALLBACKS.get(id);
        if (callback != null) {
            callback.accept(player);
        }
    }

    /**
     * 清空注册表（测试/重载用，不建议在生产环境调用）。
     */
    public static void clear() {
        SPELL_REGISTRY.clear();
        PRE_CAST_CALLBACKS.clear();
    }
}
