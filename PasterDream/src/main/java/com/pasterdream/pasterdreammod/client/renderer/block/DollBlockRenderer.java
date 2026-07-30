package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.block.entity.DollBlockEntity;
import com.pasterdream.pasterdreammod.client.model.DollBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/**
 * 通用玩偶方块渲染器
 */
public class DollBlockRenderer extends MemorialDollBlockRenderer<DollBlockEntity> {

    /**
     * 构造通用玩偶方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public DollBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new DollBlockModel());
    }
}
