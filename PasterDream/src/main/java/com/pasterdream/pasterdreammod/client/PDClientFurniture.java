package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.renderer.block.ShadowDungeonPortalGeoModel;
import com.pasterdream.pasterdreammod.client.renderer.block.W4GeoBlockRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.W4GeoDisplayItemRenderer;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import com.pasterdream.pasterdreammod.client.screen.PicnicBasketScreen;
import com.pasterdream.pasterdreammod.client.screen.ShadowDeskScreen;
import com.pasterdream.pasterdreammod.client.screen.WindmoorCrateScreen;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDMenusFurniture;
import com.pasterdream.pasterdreammod.registry.items.PDItemsFurniture;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 容器/家具/杂项方块组客户端接线（[分区F]，波次 W4）。
 * <p>
 * 独立事件订阅类，不依赖 ClientSetup/RendererRegistry：
 * <ul>
 *   <li>GeckoLib 方块实体渲染器（罐/篮/灯/陷阱/传送门/唤醒台等 17 个）；</li>
 *   <li>GUI 屏幕绑定（野餐篮/影之桌/风泊木箱）；</li>
 *   <li>GeckoLib 显示物品的 IClientItemExtensions（3D 手持渲染）。</li>
 * </ul>
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDClientFurniture {

    /**
     * 注册 GeckoLib 方块实体渲染器
     *
     * @param event 渲染器注册事件
     */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (int i = 0; i < 5; i++) {
            final String name = "wind_knight_spawnblock_" + i;
            event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.WIND_KNIGHT_SPAWNBLOCKS.get(i).get(),
                    context -> new W4GeoBlockRenderer(name));
        }
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.ECOLOGY_GLASS_JAR.get(),
                context -> new W4GeoBlockRenderer("ecology_glass_jar"));
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.FIREFLY_GLASS_JAR.get(),
                context -> new W4GeoBlockRenderer("firefly_glass_jar"));
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.LIGHT_FIREFLY_GLASS_JAR.get(),
                context -> new W4GeoBlockRenderer("light_firefly_glass_jar"));
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.PICNIC_BASKET.get(),
                context -> new W4GeoBlockRenderer("picnic_basket"));
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.BIRDS_NEST.get(),
                context -> new W4GeoBlockRenderer("birds_nest"));
        // 使用自定义 GeoModel 根据 animation 属性切换破碎/修复纹理与模型
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.BROKEN_SHADOW_DUNGEON_PROTAL.get(),
                context -> new GeoBlockRenderer<>(new ShadowDungeonPortalGeoModel()));
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.DESERT_HERO_TOMB.get(),
                context -> new W4GeoBlockRenderer("desert_hero_tomb"));
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.GUARD_CRYSTAL.get(),
                context -> new W4GeoBlockRenderer("guard_crystal"));
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.SHADOW_BRAZIER.get(),
                context -> new W4GeoBlockRenderer("shadow_brazier"));
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.SHADOW_TRAP_0.get(),
                context -> new W4GeoBlockRenderer("shadow_trap_0"));
        event.registerBlockEntityRenderer(PDBlockEntitiesFurniture.TWILIGHT_LANTERN.get(),
                context -> new W4GeoBlockRenderer("twilight_lantern"));
        PDDebugLogger.mainDebug("[PDClientFurniture] W4 GeckoLib 方块渲染器注册完成");
    }

    /**
     * 绑定 GUI 屏幕
     *
     * @param event 菜单屏幕注册事件
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PDMenusFurniture.PICNIC_BASKET.get(), PicnicBasketScreen::new);
        event.register(PDMenusFurniture.SHADOW_DESK.get(), ShadowDeskScreen::new);
        event.register(PDMenusFurniture.WINDMOOR_CRATE.get(), WindmoorCrateScreen::new);
        PDDebugLogger.mainDebug("[PDClientFurniture] W4 GUI 屏幕绑定完成");
    }

    /**
     * 注册 GeckoLib 显示物品的客户端扩展
     *
     * @param event 客户端扩展注册事件
     */
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        for (int i = 0; i < 5; i++) {
            registerDisplayItem(event, PDItemsFurniture.class, "wind_knight_spawnblock_" + i,
                    switch (i) {
                        case 0 -> PDItemsFurniture.WIND_KNIGHT_SPAWNBLOCK_0.get();
                        case 1 -> PDItemsFurniture.WIND_KNIGHT_SPAWNBLOCK_1.get();
                        case 2 -> PDItemsFurniture.WIND_KNIGHT_SPAWNBLOCK_2.get();
                        case 3 -> PDItemsFurniture.WIND_KNIGHT_SPAWNBLOCK_3.get();
                        default -> PDItemsFurniture.WIND_KNIGHT_SPAWNBLOCK_4.get();
                    });
        }
        registerDisplayItem(event, PDItemsFurniture.class, "ecology_glass_jar", PDItemsFurniture.ECOLOGY_GLASS_JAR.get());
        registerDisplayItem(event, PDItemsFurniture.class, "firefly_glass_jar", PDItemsFurniture.FIREFLY_GLASS_JAR.get());
        registerDisplayItem(event, PDItemsFurniture.class, "light_firefly_glass_jar", PDItemsFurniture.LIGHT_FIREFLY_GLASS_JAR.get());
        registerDisplayItem(event, PDItemsFurniture.class, "picnic_basket", PDItemsFurniture.PICNIC_BASKET.get());
        registerDisplayItem(event, PDItemsFurniture.class, "birds_nest", PDItemsFurniture.BIRDS_NEST.get());
        registerDisplayItem(event, PDItemsFurniture.class, "broken_shadow_dungeon_protal", PDItemsFurniture.BROKEN_SHADOW_DUNGEON_PROTAL.get());
        registerDisplayItem(event, PDItemsFurniture.class, "desert_hero_tomb", PDItemsFurniture.DESERT_HERO_TOMB.get());
        registerDisplayItem(event, PDItemsFurniture.class, "guard_crystal", PDItemsFurniture.GUARD_CRYSTAL.get());
        registerDisplayItem(event, PDItemsFurniture.class, "shadow_brazier", PDItemsFurniture.SHADOW_BRAZIER.get());
        registerDisplayItem(event, PDItemsFurniture.class, "shadow_trap_0", PDItemsFurniture.SHADOW_TRAP_0.get());
        registerDisplayItem(event, PDItemsFurniture.class, "twilight_lantern", PDItemsFurniture.TWILIGHT_LANTERN.get());
        PDDebugLogger.mainDebug("[PDClientFurniture] W4 显示物品客户端扩展注册完成");
    }

    /** 为单个显示物品绑定 GeckoLib 渲染器 */
    private static void registerDisplayItem(RegisterClientExtensionsEvent event, Class<?> owner,
                                            String name, Item item) {
        BlockEntityWithoutLevelRenderer renderer = new W4GeoDisplayItemRenderer(name);
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, item);
    }
}
