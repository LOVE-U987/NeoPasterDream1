package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.ResearchTableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 研究台方块渲染器 (Research Table Block Renderer)
 * 使用 GeckoLib 渲染研究台的 3D 模型与动画（原版使用半透明渲染层）
 *
 * 资源引用：DefaultedBlockGeoModel 会自动查找
 * - geo/block/research_table.geo.json
 * - textures/block/research_table.png
 * - animations/block/research_table.animation.json
 */
public class ResearchTableBlockRenderer extends GeoBlockRenderer<ResearchTableBlockEntity> {

    private static final String NAME = "research_table";

    /**
     * 构造研究台方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public ResearchTableBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }

    @Override
    public RenderType getRenderType(ResearchTableBlockEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        // 与原版 ResearchTableTileRenderer 一致：半透明渲染层
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
