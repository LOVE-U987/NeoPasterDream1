package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandSpawnBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 亚伦柯斯之手生成激活方块渲染器
 * <p>
 * 使用 GeckoLib 渲染 3D 模型和动画。
 * 资源路径：
 * <ul>
 *   <li>模型: {@code geo/block/aaroncos_hand_spawn_block.geo.json}</li>
 *   <li>纹理: {@code textures/block/aaroncos_hand_spawn_block.png}</li>
 *   <li>动画: {@code animations/block/aaroncos_hand_spawn_block.animation.json}</li>
 * </ul>
 */
public class AaroncosHandSpawnBlockRenderer extends GeoBlockRenderer<AaroncosHandSpawnBlockEntity> {

    private static final String NAME = "aaroncos_hand_spawn_block";

    /**
     * 构造亚伦柯斯之手生成激活方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public AaroncosHandSpawnBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
        PasterDreamMod.LOGGER.debug("[AaroncosHandSpawnBlockRenderer] 初始化完成，资源名: {} | 模型=geo/block/{}.geo.json 纹理=textures/block/{}.png 动画=animations/block/{}.animation.json",
                NAME, NAME, NAME, NAME);
    }
}
