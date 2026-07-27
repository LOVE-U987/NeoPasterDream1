package com.pasterdream.pasterdreammod.block;

import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 染梦落叶层方块
 * <p>
 * 继承 CarpetBlock，高度为 1 像素，不阻碍移动，装饰树干周围地表。
 */
public class DyedreamFallenLeavesBlock extends CarpetBlock {

    /**
     * 使用默认属性构造
     */
    public DyedreamFallenLeavesBlock() {
        super(BlockBehaviour.Properties.of()
                .replaceable()
                .noCollission()
                .strength(0.1f)
                .sound(SoundType.GRASS)
                .ignitedByLava());
    }
}
