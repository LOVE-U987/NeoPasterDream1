package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.block.entity.EoulDollBlockEntity;
import com.pasterdream.pasterdreammod.client.model.MemorialDollBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/**
 * 幼幼紫纪念玩偶方块渲染器 (Eoul Doll Block Renderer)
 * <p>
 * 使用 {@link MemorialDollBlockModel} 动态切换空/抱物模型。
 */
public class EoulDollBlockRenderer extends MemorialDollBlockRenderer<EoulDollBlockEntity> {

    /**
     * 构造幼幼紫纪念玩偶方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public EoulDollBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new MemorialDollBlockModel<>("eoul_doll") {});
    }
}
