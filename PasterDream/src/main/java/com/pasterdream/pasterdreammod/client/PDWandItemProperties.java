package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 法杖武器模型谓词注册类 (Wand Item Properties)
 * <p>
 * 还原原版 ClientEvent#ItemPropertiesRegister：为占星者的祈愿注册
 * {@code pasterdream:cast} 模型谓词——物品自定义数据 cast 为 true 时返回 1，
 * 驱动 star_wish_rod.json 的 overrides 切换为抛竿贴图（star_wish_rod_cast）。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDWandItemProperties {

    /**
     * 客户端初始化时注册模型谓词
     *
     * @param event 客户端初始化事件
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    PDItems.STAR_WISH_ROD.get(),
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "cast"),
                    (stack, clientLevel, livingEntity, seed) ->
                            PasterItemData.getBoolean(stack, "cast") ? 1.0F : 0.0F);
            PDDebugLogger.mainDebug("[PDWandItemProperties] 注册模型谓词: star_wish_rod → pasterdream:cast");
        });
    }
}
