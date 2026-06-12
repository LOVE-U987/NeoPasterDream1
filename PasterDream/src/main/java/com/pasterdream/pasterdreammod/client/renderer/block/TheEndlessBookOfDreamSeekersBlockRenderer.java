package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.TheEndlessBookOfDreamSeekersBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * 寻梦者的永恒书卷方块渲染器 (The Endless Book of Dream Seekers Block Renderer)
 * 使用 GeckoLib 渲染 3D 模型和循环动画
 */
public class TheEndlessBookOfDreamSeekersBlockRenderer extends GeoBlockRenderer<TheEndlessBookOfDreamSeekersBlockEntity> {

    private static final String NAME = "the_endless_book_of_dream_seekers";

    /**
     * 构造寻梦者的永恒书卷方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public TheEndlessBookOfDreamSeekersBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, NAME)));
    }
}
