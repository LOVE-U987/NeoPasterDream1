package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 染梦土壤作物花方块（crop_0b / crop_2b / crop_3b 共用）
 * <p>
 * 与 {@link DyedreamFlowerBlock} 的区别：只允许种植在染梦泥土与染梦草方块上
 * （与原版 Crop0b/2b/3bBlock 的 mayPlaceOn 完全一致），触碰效果可自定义。
 */
public class DyedreamCropFlowerBlock extends FlowerBlock {

    /**
     * 构造染梦土壤作物花
     *
     * @param effect     触碰时获得的药水效果
     * @param duration   效果持续时间（tick）
     * @param properties 方块属性
     */
    public DyedreamCropFlowerBlock(Holder<MobEffect> effect, int duration, BlockBehaviour.Properties properties) {
        super(effect, duration, properties);
    }

    /**
     * 只允许种植在染梦泥土 / 染梦草方块上（与原版逻辑一致）
     *
     * @param state 地面方块状态
     * @param level 世界访问器
     * @param pos   地面方块位置
     * @return 是否允许放置
     */
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(PDBlockTags.PLANTABLE_ON);
    }
}
