package com.pasterdream.pasterdreammod.registry.blocks;

import com.pasterdream.pasterdreammod.block.PDStructureBlock;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 结构生成方块组注册（[分区F]，波次 W4）。
 * <p>
 * structure_block_0..23，全部为不可破坏的放置触发式结构生成方块，
 * 规格数据见 {@link PDStructureBlock#SPECS}。
 *
 * @see PDBlocks
 */
public class PDBlocksStructure {

    /** structure_block_0..23（按下标索引） */
    public static final List<DeferredBlock<PDStructureBlock>> STRUCTURE_BLOCKS;

    static {
        List<DeferredBlock<PDStructureBlock>> list = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            final int index = i;
            list.add(PDBlocks.BLOCKS.registerBlock("structure_block_" + i,
                    p -> new PDStructureBlock(index, p),
                    BlockBehaviour.Properties.of()
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .sound(SoundType.STONE)
                            .strength(-1, 3600000)
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false)));
        }
        STRUCTURE_BLOCKS = Collections.unmodifiableList(list);
    }

    // ==================== 命名引用（便于 PDBlocks re-export / 创造标签） ====================

    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_0 = STRUCTURE_BLOCKS.get(0);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_1 = STRUCTURE_BLOCKS.get(1);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_2 = STRUCTURE_BLOCKS.get(2);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_3 = STRUCTURE_BLOCKS.get(3);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_4 = STRUCTURE_BLOCKS.get(4);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_5 = STRUCTURE_BLOCKS.get(5);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_6 = STRUCTURE_BLOCKS.get(6);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_7 = STRUCTURE_BLOCKS.get(7);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_8 = STRUCTURE_BLOCKS.get(8);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_9 = STRUCTURE_BLOCKS.get(9);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_10 = STRUCTURE_BLOCKS.get(10);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_11 = STRUCTURE_BLOCKS.get(11);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_12 = STRUCTURE_BLOCKS.get(12);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_13 = STRUCTURE_BLOCKS.get(13);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_14 = STRUCTURE_BLOCKS.get(14);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_15 = STRUCTURE_BLOCKS.get(15);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_16 = STRUCTURE_BLOCKS.get(16);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_17 = STRUCTURE_BLOCKS.get(17);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_18 = STRUCTURE_BLOCKS.get(18);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_19 = STRUCTURE_BLOCKS.get(19);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_20 = STRUCTURE_BLOCKS.get(20);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_21 = STRUCTURE_BLOCKS.get(21);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_22 = STRUCTURE_BLOCKS.get(22);
    public static final DeferredBlock<PDStructureBlock> STRUCTURE_BLOCK_23 = STRUCTURE_BLOCKS.get(23);
}
