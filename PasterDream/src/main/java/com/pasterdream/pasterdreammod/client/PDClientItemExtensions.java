package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.renderer.item.AaroncosHandChestDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.AaroncosHandSpawnBlockDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.DreamAccumulatorDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.DreamCauldronDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.DreamMeterItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.EoulDollDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.ShadowHandLanternItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.GoldenFoxSculptureDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.LoveUDollDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.QymDoll0DisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.UuzDoll0DisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.LifeCrystalDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.MeltdreamChestDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.MeltdreamChestOpenDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.MeltdreamLiquidBucketRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.ShadowChestDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.TheEndlessBookOfDreamSeekersDisplayItemRenderer;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 客户端物品扩展注册类
 * 负责通过 RegisterClientExtensionsEvent 注册自定义物品的 IClientItemExtensions，
 * 替代已弃用的 Item.initializeClient() 方法。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDClientItemExtensions {

    /**
     * 注册客户端物品扩展
     * 为每个自定义渲染物品注册 IClientItemExtensions，
     * 使其在客户端使用对应的 BlockEntityWithoutLevelRenderer。
     *
     * @param event 客户端扩展注册事件
     */
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        registerDisplayItem(event, PDItems.DREAM_ACCUMULATOR.get(), new DreamAccumulatorDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: dream_accumulator → DreamAccumulatorDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.LIFE_CRYSTAL.get(), new LifeCrystalDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: life_crystal → LifeCrystalDisplayItemRenderer（GeckoLib 3D）");

        // ==================== 玩偶/雕像 ====================

        registerDisplayItem(event, PDItems.QIN_DOLL_0.get(), new QymDoll0DisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: qin_doll_0 → QymDoll0DisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.LITTLE_PURPLE_DOLL_0.get(), new UuzDoll0DisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: little_purple_doll_0 → UuzDoll0DisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.GOLDEN_FOX_SCULPTURE.get(), new GoldenFoxSculptureDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: golden_fox_sculpture → GoldenFoxSculptureDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.SHADOW_CHEST.get(), new ShadowChestDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: shadow_chest → ShadowChestDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.MELTDREAM_CHEST.get(), new MeltdreamChestDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: meltdream_chest → MeltdreamChestDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.MELTDREAM_CHEST_OPEN.get(), new MeltdreamChestOpenDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: meltdream_chest_open → MeltdreamChestOpenDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.DREAM_CAULDRON.get(), new DreamCauldronDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: dream_cauldron → DreamCauldronDisplayItemRenderer（GeckoLib 3D）");

        // ==================== [分区W] 武器工坊群 ====================

        registerDisplayItem(event, PDItems.WEAPON_TABLE.get(),
                new com.pasterdream.pasterdreammod.client.renderer.item.WeaponTableDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: weapon_table → WeaponTableDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.WEAPON_WORKSHOP.get(),
                new com.pasterdream.pasterdreammod.client.renderer.item.WeaponWorkshopDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: weapon_workshop → WeaponWorkshopDisplayItemRenderer（GeckoLib 3D）");

        // ==================== [分区R] 研究台组 ====================

        registerDisplayItem(event, PDItems.RESEARCH_TABLE.get(),
                new com.pasterdream.pasterdreammod.client.renderer.item.ResearchTableDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: research_table → ResearchTableDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.SHADOW_BLAST_FURNACE.get(),
                new com.pasterdream.pasterdreammod.client.renderer.item.ShadowBlastFurnaceDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: shadow_blast_furnace → ShadowBlastFurnaceDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.FORCED_TOWER.get(),
                new com.pasterdream.pasterdreammod.client.renderer.item.ForcedTowerDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: forced_tower → ForcedTowerDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), new TheEndlessBookOfDreamSeekersDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: the_endless_book_of_dream_seekers → TheEndlessBookOfDreamSeekersDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.DREAM_METER.get(), new DreamMeterItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: dream_meter → DreamMeterItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.SHADOW_HAND_LANTERN.get(), new ShadowHandLanternItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: shadow_hand_lantern → ShadowHandLanternItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.LOVE_U_DOLL.get(), new LoveUDollDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: love_u_doll → LoveUDollDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.EOUL_DOLL.get(), new EoulDollDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: eoul_doll → EoulDollDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.MELTDREAM_LIQUID_BUCKET.get(), new MeltdreamLiquidBucketRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册物品: meltdream_liquid_bucket → MeltdreamLiquidBucketRenderer（BEWLR 流体覆盖层兼容修复）");

        // ==================== BOSS 系列 ====================

        registerDisplayItem(event, PDItems.AARONCOS_HAND_CHEST.get(), new AaroncosHandChestDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: aaroncos_hand_chest → AaroncosHandChestDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.AARONCOSHANDSPAWNBLOCK.get(), new AaroncosHandSpawnBlockDisplayItemRenderer());
        PDDebugLogger.mainDebug("[PDClientItemExtensions] 注册显示物品: aaroncoshandspawnblock → AaroncosHandSpawnBlockDisplayItemRenderer（GeckoLib 3D）");

        // ==================== DollAPI 注册的玩偶物品 ====================
        var dollRegs = com.pasterdream.pasterdreammod.api.doll.DollAPI.getRegistrations();
        PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 发现 {} 个已注册玩偶，准备注册物品渲染器", dollRegs.size());
        for (var reg : dollRegs) {
            registerDisplayItem(event, reg.item().get(), new com.pasterdream.pasterdreammod.client.renderer.item.DollItemRenderer());
            PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 注册玩偶显示物品: {} → DollItemRenderer", reg.name());
        }
    }

    /**
     * 注册单个显示物品的客户端扩展
     *
     * @param event    客户端扩展注册事件
     * @param item     要注册的物品
     * @param renderer 对应的渲染器
     */
    private static void registerDisplayItem(RegisterClientExtensionsEvent event,
                                            net.minecraft.world.item.Item item,
                                            net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer renderer) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, item);
    }
}