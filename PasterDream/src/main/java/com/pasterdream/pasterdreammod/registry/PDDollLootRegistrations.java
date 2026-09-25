package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.doll.DollAPI;

/**
 * 旧玩偶/雕像的战利品池登记。
 * <p>
 * 将未走 DollAPI 注册的 5 个旧玩偶登记进 {@link DollAPI} 的可掉落战利品池，
 * 使融梦水晶箱的玩偶附加掉落无需硬编码具体物品；DollAPI 自定义玩偶由
 * {@code DollBuilder#register()} 自动登记。
 *
 * @author PasterDream
 */
public final class PDDollLootRegistrations {

    private PDDollLootRegistrations() {
        throw new UnsupportedOperationException("工具类不可实例化");
    }

    /**
     * 登记旧玩偶/雕像。必须在模组构造阶段（注册阶段）调用。
     */
    public static void register() {
        DollAPI.registerLootItem(PDItems.QIN_DOLL_0);
        DollAPI.registerLootItem(PDItems.LITTLE_PURPLE_DOLL_0);
        DollAPI.registerLootItem(PDItems.GOLDEN_FOX_SCULPTURE);
        DollAPI.registerLootItem(PDItems.LOVE_U_DOLL);
        DollAPI.registerLootItem(PDItems.EOUL_DOLL);
    }
}
