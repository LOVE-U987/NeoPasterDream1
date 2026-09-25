package com.pasterdream.pasterdreammod.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 融梦水晶箱战利品配置一次性迁移。
 * <p>
 * 旧版本中三个品质物品池的默认值为内置物品池；新版本改为默认空列表，
 * 内置池转为隐藏兜底常量。本迁移仅当配置值仍逐条等于旧默认值时将其清空，
 * 玩家自定义内容一律保留。
 * <p>
 * 只挂 {@link ModConfigEvent.Loading}（不挂 Reloading，避免重入），
 * 并复用 {@code PDConfigScreen#saveLoadedConfig} 的 TomlWriter 直写方案落盘，
 * 不调用 {@code ILoadedConfig.save()}，规避 {@code ModConfigEvent.Reloading} 副作用。
 * <p>注意：NeoForge 1.21.1 起 {@code EventBusSubscriber.bus()} 已被忽略，事件总线按
 * 事件类型自动判定（{@code ModConfigEvent} 实现 {@code IModBusEvent}，自动挂模组总线），
 * 故此处不指定 {@code bus}。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class MeltdreamChestLootConfigMigration {

    /** 幂等守卫 */
    private static final AtomicBoolean MIGRATED = new AtomicBoolean(false);

    private MeltdreamChestLootConfigMigration() {
    }

    /**
     * 配置加载阶段执行迁移（仅 COMMON 配置，仅一次）。
     *
     * @param event 配置加载事件
     */
    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() != PDCommonConfig.SPEC) {
            return;
        }
        if (MIGRATED.getAndSet(true)) {
            return;
        }
        boolean changed = clearIfLegacy(PDCommonConfig.MELTDREAM_CHEST_COMMON_LOOT,
                MeltdreamChestLootConfig.LEGACY_V1_COMMON_LOOT);
        changed |= clearIfLegacy(PDCommonConfig.MELTDREAM_CHEST_RARE_LOOT,
                MeltdreamChestLootConfig.LEGACY_V1_RARE_LOOT);
        changed |= clearIfLegacy(PDCommonConfig.MELTDREAM_CHEST_LEGENDARY_LOOT,
                MeltdreamChestLootConfig.LEGACY_V1_LEGENDARY_LOOT);
        if (changed) {
            PasterDreamMod.LOGGER.info("[MeltdreamChest] 检测到旧默认物品池，已迁移为默认空（内置池转为隐藏兜底）");
            saveCommonConfigDirect();
        }
    }

    /**
     * 若配置值等于旧默认值则清空。
     *
     * @param value  配置项
     * @param legacy 旧默认值
     * @return 是否发生修改
     */
    private static boolean clearIfLegacy(ModConfigSpec.ConfigValue<List<String>> value, List<String> legacy) {
        List<String> current = value.get();
        if (current != null && listEquals(current, legacy)) {
            value.set(List.of());
            return true;
        }
        return false;
    }

    /**
     * 按元素比较两个字符串列表（忽略 List 实现类）。
     *
     * @param a 列表 a
     * @param b 列表 b
     * @return 是否逐元素相等
     */
    private static boolean listEquals(List<String> a, List<String> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 以 TomlWriter 直写 COMMON 配置文件（不触发 Reloading）。
     */
    private static void saveCommonConfigDirect() {
        ModConfig modConfig = PasterDreamMod.commonModConfig;
        if (modConfig == null || modConfig.getLoadedConfig() == null) {
            return;
        }
        CommentedConfig configData = modConfig.getLoadedConfig().config();
        if (configData == null) {
            return;
        }
        try (OutputStream os = new FileOutputStream(modConfig.getFullPath().toFile())) {
            new TomlWriter().write(configData, os);
        } catch (Exception e) {
            PasterDreamMod.LOGGER.error("[MeltdreamChest] 迁移写盘失败", e);
        }
    }
}
