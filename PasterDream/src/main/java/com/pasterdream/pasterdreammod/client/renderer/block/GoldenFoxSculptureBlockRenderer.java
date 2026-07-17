package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.GoldenFoxSculptureBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 狐狸雕像方块渲染器 (Golden Fox Sculpture Block Renderer)
 * 使用 GeckoLib 渲染雕像的 3D 模型
 */
public class GoldenFoxSculptureBlockRenderer extends GeoBlockRenderer<GoldenFoxSculptureBlockEntity> {

    private static final String NAME = "golden_fox_sculpture";

    /**
     * 构造狐狸雕像方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public GoldenFoxSculptureBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[GoldenFoxSculptureBlockRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png",
                NAME, NAME, NAME);
    }
}
