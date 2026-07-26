package com.pasterdream.pasterdreammod.client.gui;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * HUD 叠加层注册类（仅客户端，MOD 总线）
 * <p>
 * 移植自原版 {@code PasterdreamHud.java}（RegisterGuiOverlaysEvent →
 * 1.21.1 {@link RegisterGuiLayersEvent}），层 ID 与注册顺序与原版一致：
 * <ul>
 *   <li>meltdreamenergy_bar — 融梦能量条（{@link MeltdreamEnergyTankOverlay}，FOOD_LEVEL 之上）</li>
 *   <li>san_bar — San 精神值条（{@link SanTankOverlay}，FOOD_LEVEL 之上）</li>
 *   <li>lose_mind_gui — 失智全屏渐显（{@link LoseMindOverlay}，FOOD_LEVEL 之上）</li>
 *   <li>couldmist_hud — 云雾浓度全屏渐显（{@link CloudmistHudOverlay}，FOOD_LEVEL 之上，
 *       层名沿用原版拼写）</li>
 *   <li>pd_health — 主题血条（{@link PlayerHealthHudOverlay}，PLAYER_HEALTH 之上；
 *       vanilla 血条的按配置隐藏见 {@link PDHudEvents}）</li>
 * </ul>
 * 原版另有一层 aaroncos_lefthand_boss_bar，已由 {@code AaroncosBossBarOverlay} 替代。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDHudLayers {

    /**
     * 注册 HUD 叠加层
     *
     * @param event GUI 层注册事件
     */
    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL,
                layerId("meltdreamenergy_bar"), new MeltdreamEnergyTankOverlay());
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL,
                layerId("san_bar"), new SanTankOverlay());
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL,
                layerId("lose_mind_gui"), new LoseMindOverlay());
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL,
                layerId("couldmist_hud"), new CloudmistHudOverlay());
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH,
                layerId("pd_health"), new PlayerHealthHudOverlay());
        PasterDreamMod.LOGGER.debug(
                "[PDHudLayers] 注册 HUD 叠加层: meltdreamenergy_bar, san_bar, lose_mind_gui, couldmist_hud, pd_health");
    }

    /**
     * 构建层 ID
     *
     * @param name 层名（与原版层名一致）
     * @return 资源位置
     */
    private static ResourceLocation layerId(String name) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name);
    }
}
