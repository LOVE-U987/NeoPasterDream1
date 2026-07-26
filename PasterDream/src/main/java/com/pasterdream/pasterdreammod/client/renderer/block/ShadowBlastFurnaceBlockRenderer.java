package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.ShadowBlastFurnaceBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 暗影高炉方块渲染器 (Shadow Blast Furnace Block Renderer)
 * 使用 GeckoLib 渲染暗影高炉的 3D 模型与动画（原版使用半透明渲染层）
 *
 * 资源引用：DefaultedBlockGeoModel 会自动查找
 * - geo/block/shadow_blast_furnace.geo.json
 * - textures/block/shadow_blast_furnace.png
 * - animations/block/shadow_blast_furnace.animation.json
 */
public class ShadowBlastFurnaceBlockRenderer extends GeoBlockRenderer<ShadowBlastFurnaceBlockEntity> {

    private static final String NAME = "shadow_blast_furnace";

    /**
     * 构造暗影高炉方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public ShadowBlastFurnaceBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }

    @Override
    public RenderType getRenderType(ShadowBlastFurnaceBlockEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        // 与原版 ShadowBlastFurnaceTileRenderer 一致：半透明渲染层
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
