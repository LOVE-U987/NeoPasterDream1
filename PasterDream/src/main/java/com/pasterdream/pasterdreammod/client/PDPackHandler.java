package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.List;

/**
 * 内置资源包注册 (PD Pack Handler)
 * <p>
 * 还原自原版 PackHandler：注册模组内嵌的「帕斯特梦境风格 vanilla UI」资源包
 * （packs/paster_vanilla_ui —— 血条/图标/快捷栏/物品栏等 vanilla 界面的梦境粉紫重绘）。
 * <p>
 * 迁移说明：原 1.20.1 的 icons/widgets/bars 等整图已用 Mojang 官方 Slicer 工具
 * 切分为 1.21.1 的 gui/sprites 图集布局（157 个精灵图），options_background
 * 平铺纹理同时映射到 1.20.5+ 的 menu_background/menu_list_background 新命名。
 * <p>
 * 与原版的区别：资源包以<strong>可选</strong>方式注册（{@code alwaysActive=false}），
 * 实际是否加载由客户端配置 {@link PDClientConfig#ENABLE_MOD_UI} 控制；这样玩家
 * 在游戏内关闭「启用模组 UI」后，可以通过资源包重载立即生效，无需重启游戏。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public final class PDPackHandler {

    /** 内嵌 UI 资源包在 PackRepository 中的 ID（NeoForge 格式：mod/<namespace>:<path>） */
    public static final String PACK_ID = "mod/pasterdream:packs/paster_vanilla_ui";
    /** 旧版内置包 ID（用于清理旧 options.txt 中的残留条目） */
    private static final String LEGACY_PACK_ID = "builtin/paster_vanilla_ui";
    /** 内嵌 UI 资源包在模组资源目录下的位置 */
    public static final ResourceLocation PACK_LOCATION = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "packs/paster_vanilla_ui");

    private PDPackHandler() {
    }

    /**
     * 注册内嵌 UI 资源包
     *
     * @param event 资源包查找器注册事件（MOD 总线）
     */
    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }
        // 以可选方式注册，默认不强制启用；由 ENABLE_MOD_UI 控制是否加载
        event.addPackFinders(
                PACK_LOCATION,
                PackType.CLIENT_RESOURCES,
                Component.translatable("pack.pasterdream.vanilla.title"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP);
    }

    /**
     * 根据配置开关同步内嵌 UI 资源包的加载状态，并在状态变化时触发资源包重载。
     * <p>
     * 调用方应确保在退出配置界面后调用本方法，这样 {@link Minecraft#reloadResourcePacks()}
     * 弹出的资源包重载屏能正确覆盖当前屏幕。
     *
     * @param enable 是否启用内嵌 UI 资源包
     */
    public static void applyPackState(boolean enable) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            PasterDreamMod.LOGGER.warn("[PDPackHandler] Minecraft 实例为空，无法同步资源包状态");
            return;
        }
        PackRepository repo = mc.getResourcePackRepository();
        // 重新发现可用包并从 options.txt 同步已选列表，确保后续 add/remove 操作基于最新状态
        repo.reload();

        if (!repo.isAvailable(PACK_ID)) {
            PasterDreamMod.LOGGER.warn("[PDPackHandler] 内嵌 UI 资源包未在仓库中发现：{}，可用包：{}",
                    PACK_ID, repo.getAvailableIds());
            return;
        }

        boolean currentlySelected = repo.getSelectedPacks().stream()
                .anyMatch(p -> p.getId().equals(PACK_ID));
        PasterDreamMod.LOGGER.debug("[PDPackHandler] 目标状态={}，当前选中={}", enable, currentlySelected);

        boolean changed = false;
        if (enable && !currentlySelected) {
            changed = repo.addPack(PACK_ID);
            PasterDreamMod.LOGGER.debug("[PDPackHandler] addPack 返回 {}", changed);
        } else if (!enable && currentlySelected) {
            changed = repo.removePack(PACK_ID);
            PasterDreamMod.LOGGER.debug("[PDPackHandler] removePack 返回 {}", changed);
        }

        // 清理旧版 options.txt 中可能残留的 builtin/paster_vanilla_ui 条目，
        // 避免升级用户看到失效的内置包选中记录。
        if (mc.options.resourcePacks.contains(LEGACY_PACK_ID)) {
            mc.options.resourcePacks.remove(LEGACY_PACK_ID);
            changed = true;
            PasterDreamMod.LOGGER.info("[PDPackHandler] 已清理旧版内置包条目 {}", LEGACY_PACK_ID);
        }

        if (!changed) {
            PasterDreamMod.LOGGER.debug("[PDPackHandler] 内嵌 UI 资源包状态无需变更：enable={}", enable);
            return;
        }

        // 将新的选中列表按实际顺序写回 options 并保存，随后触发资源包重载
        List<String> selectedIds = repo.getSelectedPacks().stream()
                .map(Pack::getId)
                .toList();
        mc.options.resourcePacks.clear();
        mc.options.resourcePacks.addAll(selectedIds);
        mc.options.save();
        PasterDreamMod.LOGGER.info("[PDPackHandler] 内嵌 UI 资源包状态已切换为 {}，正在重载资源包", enable);
        mc.reloadResourcePacks();
    }
}
