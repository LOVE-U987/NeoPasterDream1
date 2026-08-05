package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.client.model.WindKnightSpawnblockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 风之骑士唤醒台方块渲染器
 * <p>
 * 模型由 {@link WindKnightSpawnblockModel} 按方块 STAGE 属性动态切换；
 * GeckoLib 基类 {@link GeoBlockRenderer#getFacing} 自动读取
 * {@code HORIZONTAL_FACING} 属性并旋转模型，实现方向放置。
 */
public class WindKnightSpawnblockBlockRenderer extends GeoBlockRenderer<W4GeoDataBlockEntity> {

    public WindKnightSpawnblockBlockRenderer() {
        super(new WindKnightSpawnblockModel());
    }

    @Override
    public RenderType getRenderType(W4GeoDataBlockEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
