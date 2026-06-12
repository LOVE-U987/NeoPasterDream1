package com.pasterdream.pasterdreammod.block;

import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

/**
 * 染梦木板栅栏方块 —— 继承铁栏杆行为，但使用木质属性。
 * 可用于染梦木板的栅栏/玻璃板变种，拥有木质的强度和声音表现。
 *
 * @author PasterDream
 */
public class DyedreamPlanksPaneBlock extends IronBarsBlock {
    public DyedreamPlanksPaneBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .instrument(NoteBlockInstrument.BASS)
                .sound(SoundType.WOOD)
                .strength(2f, 3f)
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false));
    }
}