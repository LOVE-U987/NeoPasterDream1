package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import net.minecraft.resources.ResourceLocation;

/**
 * 自定义玩偶注册
 * <p>
 * 通过 {@link DollAPI} 注册玩家皮肤模型的双层皮肤玩偶，支持抱物功能。
 * 纹理文件位于 {@code textures/block/} 目录下。
 *
 * @author PasterDream
 */
public final class PDCustomDolls {

    private PDCustomDolls() {
        throw new UnsupportedOperationException("工具类不可实例化");
    }

    /**
     * 注册所有自定义玩偶
     * <p>
     * 必须在模组主类构造方法中调用，确保在 DeferredRegister 注册到事件总线之后执行。
     */
    public static void register() {
        DollAPI.create("phantom_daze")
                .model(ResourceLocation.fromNamespaceAndPath("pasterdream", "geo/block/phantom_daze.geo.json"))
                .texture(ResourceLocation.fromNamespaceAndPath("pasterdream", "textures/block/phantom_daze.png"))
                .holdingModel(ResourceLocation.fromNamespaceAndPath("pasterdream", "geo/block/phantom_daze_holding.geo.json"))
                .canHoldItems(true)
                .register();

        DollAPI.create("mini_beixu_doll")
                .model(ResourceLocation.fromNamespaceAndPath("pasterdream", "geo/block/mini_beixu_doll.geo.json"))
                .texture(ResourceLocation.fromNamespaceAndPath("pasterdream", "textures/block/mini_beixu_doll.png"))
                .holdingModel(ResourceLocation.fromNamespaceAndPath("pasterdream", "geo/block/mini_beixu_doll_holding.geo.json"))
                .canHoldItems(true)
                .register();

        // 雾於酱玩偶：独立模型（复制 eoul_doll 骨骼），仅替换皮肤纹理
        DollAPI.create("wuyu_doll")
                .model(ResourceLocation.fromNamespaceAndPath("pasterdream", "geo/block/wuyu_doll.geo.json"))
                .texture(ResourceLocation.fromNamespaceAndPath("pasterdream", "textures/block/wuyu_doll.png"))
                .holdingModel(ResourceLocation.fromNamespaceAndPath("pasterdream", "geo/block/wuyu_doll_holding.geo.json"))
                .canHoldItems(true)
                .register();
    }
}
