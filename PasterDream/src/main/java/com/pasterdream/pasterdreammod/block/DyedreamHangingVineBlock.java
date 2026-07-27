package com.pasterdream.pasterdreammod.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 染梦垂挂藤蔓方块
 * <p>
 * 继承 VineBlock，支持原版藤蔓的四面方向状态，自带微弱亮度 4。
 */
public class DyedreamHangingVineBlock extends VineBlock {

    /**
     * 使用默认属性构造
     */
    public DyedreamHangingVineBlock() {
        super(BlockBehaviour.Properties.of()
                .replaceable()
                .noCollission()
                .randomTicks()
                .strength(0.2f)
                .sound(SoundType.VINE)
                .lightLevel(state -> 4)
                .ignitedByLava());
    }
}
