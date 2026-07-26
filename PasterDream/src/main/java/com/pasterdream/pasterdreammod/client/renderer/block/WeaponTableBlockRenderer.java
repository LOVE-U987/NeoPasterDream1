package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.WeaponTableBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 精铸工作台方块渲染器 (Weapon Table Block Renderer)
 * 使用 GeckoLib 渲染精铸工作台的 3D 模型与空闲动画
 *
 * 资源引用：DefaultedBlockGeoModel 会自动查找
 * - geo/block/weapon_table.geo.json
 * - textures/block/weapon_table.png
 * - animations/block/weapon_table.animation.json
 */
public class WeaponTableBlockRenderer extends GeoBlockRenderer<WeaponTableBlockEntity> {

    private static final String NAME = "weapon_table";

    /**
     * 构造精铸工作台方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public WeaponTableBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
