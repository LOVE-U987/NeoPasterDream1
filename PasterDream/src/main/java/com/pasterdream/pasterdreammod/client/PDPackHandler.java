package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

/**
 * 内置资源包注册 (PD Pack Handler)
 * <p>
 * 还原自原版 PackHandler：注册模组内嵌的「帕斯特梦境风格 vanilla UI」资源包
 * （packs/paster_vanilla_ui —— 血条/图标/快捷栏/物品栏等 vanilla 界面的梦境粉紫重绘）。
 * <p>
 * 迁移说明：原 1.20.1 的 icons/widgets/bars 等整图已用 Mojang 官方 Slicer 工具
 * 切分为 1.21.1 的 gui/sprites 图集布局（157 个精灵图），options_background
 * 平铺纹理同时映射到 1.20.5+ 的 menu_background/menu_list_background 新命名。
 * 与原版一致：默认强制启用（alwaysActive）、置顶顺序、内置来源。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public final class PDPackHandler {

    private PDPackHandler() {
    }

    /**
     * 注册内嵌 UI 资源包
     *
     * @param event 资源包查找器注册事件（MOD 总线）
     */
    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "packs/paster_vanilla_ui"),
                PackType.CLIENT_RESOURCES,
                Component.translatable("pack.pasterdream.vanilla.title"),
                PackSource.BUILT_IN,
                true,
                Pack.Position.TOP);
    }
}
