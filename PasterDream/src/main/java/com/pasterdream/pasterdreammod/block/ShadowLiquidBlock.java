package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * 熔融阴影流体方块 (shadow_liquid)
 * <p>
 * 继承 LiquidBlock，属性完全对照原版 ShadowLiquidBlock：
 * 地图色 WATER、强度 100、无碰撞、无战利品表、液态、活塞破坏、可替换。
 */
public class ShadowLiquidBlock extends LiquidBlock {

    /**
     * 构造熔融阴影流体方块
     */
    public ShadowLiquidBlock() {
        super(PDFluids.SHADOW_LIQUID.get(), BlockBehaviour.Properties.of()
                .mapColor(MapColor.WATER)
                .strength(100f)
                .noCollission()
                .noLootTable()
                .liquid()
                .pushReaction(PushReaction.DESTROY)
                .sound(SoundType.EMPTY)
                .replaceable());
    }
}
