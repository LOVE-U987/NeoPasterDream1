package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.block.entity.LoveUDollBlockEntity;
import com.pasterdream.pasterdreammod.client.model.MemorialDollBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/**
 * 琴雨梦纪念玩偶方块渲染器 (Love U Doll Block Renderer)
 * <p>
 * 使用 {@link MemorialDollBlockModel} 动态切换空/抱物模型。
 */
public class LoveUDollBlockRenderer extends MemorialDollBlockRenderer<LoveUDollBlockEntity> {

    /**
     * 构造琴雨梦纪念玩偶方块渲染器
     *
     * @param context 渲染器提供者上下文
     */
    public LoveUDollBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new MemorialDollBlockModel<>("love_u_doll") {});
    }
}
