package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.WeaponWorkshopBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 精铸工坊方块渲染器 (Weapon Workshop Block Renderer)
 * 使用 GeckoLib 渲染精铸工坊的 3D 模型与动画
 * （整组工坊——含四座卫星工位——的可见外观都由本渲染器绘制）
 *
 * 资源引用：DefaultedBlockGeoModel 会自动查找
 * - geo/block/weapon_workshop.geo.json
 * - textures/block/weapon_workshop.png
 * - animations/block/weapon_workshop.animation.json
 */
public class WeaponWorkshopBlockRenderer extends GeoBlockRenderer<WeaponWorkshopBlockEntity> {

    private static final String NAME = "weapon_workshop";

    /**
     * 构造精铸工坊方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public WeaponWorkshopBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
