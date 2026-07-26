package com.pasterdream.pasterdreammod.registry.blocks;

import com.pasterdream.pasterdreammod.block.DreamSpawner0Block;
import com.pasterdream.pasterdreammod.block.ForcedTowerBlock;
import com.pasterdream.pasterdreammod.block.ResearchTableBlock;
import com.pasterdream.pasterdreammod.block.ShadowBlastFurnaceBlock;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * 研究台组方块注册（[分区R]）。
 * <p>
 * 研究台 + 暗影高炉 + 强征传送塔 + 构梦刷怪笼。方块属性逐一对照原版
 * {@code net.pasterdream.block.*}：
 * <ul>
 *   <li>研究台：木质音效，1/0.2 强度，GeckoLib 模型；</li>
 *   <li>暗影高炉：深板岩音效，10 强度需正确工具，由核心多方块搭建产生；</li>
 *   <li>强征传送塔：玻璃音效，2/5 强度需正确工具；</li>
 *   <li>构梦刷怪笼：金属音效，5/10 强度需正确工具，不掉落。</li>
 * </ul>
 * （暗影高炉核心 SHADOW_BLAST_FURNACE_CORE 已在 {@link PDBlocksDungeon} 注册，
 * 本波次为其换装多方块搭建交互类。）
 *
 * @see PDBlocks
 */
public class PDBlocksResearch {

    /**
     * 研究台 (research_table)
     * GeckoLib 3D 模型；研究/复制寻梦者笔记的 GUI 工作台
     */
    public static final DeferredBlock<ResearchTableBlock> RESEARCH_TABLE = PDBlocks.BLOCKS.registerBlock("research_table",
            ResearchTableBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOD)
                    .strength(1f, 0.2f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /**
     * 暗影高炉 (shadow_blast_furnace)
     * GeckoLib 3D 模型；shadow_blasting 数据包配方冶炼炉（暗影液体 + 梦魇燃料）
     */
    public static final DeferredBlock<ShadowBlastFurnaceBlock> SHADOW_BLAST_FURNACE = PDBlocks.BLOCKS.registerBlock("shadow_blast_furnace",
            ShadowBlastFurnaceBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.DEEPSLATE)
                    .strength(10f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /**
     * 强征传送塔 (forced_tower)
     * GeckoLib 3D 模型；聚梦法杖链接双塔，空手右键消耗融梦能量传送
     */
    public static final DeferredBlock<ForcedTowerBlock> FORCED_TOWER = PDBlocks.BLOCKS.registerBlock("forced_tower",
            ForcedTowerBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(2f, 5f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /**
     * 构梦刷怪笼 (dream_spawner_0)
     * 带方块实体的"真"刷怪机：右键写入刷怪蛋，玩家靠近时按批量生成生物
     */
    public static final DeferredBlock<DreamSpawner0Block> DREAM_SPAWNER_0 = PDBlocks.BLOCKS.registerBlock("dream_spawner_0",
            DreamSpawner0Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
                    .strength(5f, 10f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));
}
