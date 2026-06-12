package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.renderer.item.DreamAccumulatorDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.DreamCauldronDisplayItemRenderer;
import com.pasterdream.pasterdreammod.client.renderer.item.DreamMeterItemRenderer;
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
        PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 注册显示物品: dream_accumulator → DreamAccumulatorDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.LIFE_CRYSTAL.get(), new LifeCrystalDisplayItemRenderer());
        PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 注册显示物品: life_crystal → LifeCrystalDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.SHADOW_CHEST.get(), new ShadowChestDisplayItemRenderer());
        PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 注册显示物品: shadow_chest → ShadowChestDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.MELTDREAM_CHEST.get(), new MeltdreamChestDisplayItemRenderer());
        PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 注册显示物品: meltdream_chest → MeltdreamChestDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.MELTDREAM_CHEST_OPEN.get(), new MeltdreamChestOpenDisplayItemRenderer());
        PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 注册显示物品: meltdream_chest_open → MeltdreamChestOpenDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.DREAM_CAULDRON.get(), new DreamCauldronDisplayItemRenderer());
        PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 注册显示物品: dream_cauldron → DreamCauldronDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), new TheEndlessBookOfDreamSeekersDisplayItemRenderer());
        PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 注册显示物品: the_endless_book_of_dream_seekers → TheEndlessBookOfDreamSeekersDisplayItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.DREAM_METER.get(), new DreamMeterItemRenderer());
        PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 注册显示物品: dream_meter → DreamMeterItemRenderer（GeckoLib 3D）");

        registerDisplayItem(event, PDItems.MELTDREAM_LIQUID_BUCKET.get(), new MeltdreamLiquidBucketRenderer());
        PasterDreamMod.LOGGER.info("[PDClientItemExtensions] 注册物品: meltdream_liquid_bucket → MeltdreamLiquidBucketRenderer（BEWLR 流体覆盖层兼容修复）");
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