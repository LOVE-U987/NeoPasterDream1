package com.pasterdream.pasterdreammod.registry.blocks;

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.block.SnowyColdDomainGrassBlock;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Map;

/**
 * 冷域维度方块注册。
 * <p>
 * 对应新维度 cold_domain_world 的地表素材：
 * <ul>
 *   <li>{@code cold_domain_dirt} —— 冷域泥土（普通泥土，可被雪地草坪蔓延）</li>
 *   <li>{@code cold_domain_log} —— 冷域木头（轴向方块，可燃烧）</li>
 *   <li>{@code stripped_cold_domain_log} —— 去皮冷域木头（轴向方块，可燃烧）</li>
 *   <li>{@code cold_domain_leaves} —— 冷域树叶（树叶衰变行为）</li>
 *   <li>{@code snowy_cold_domain_grass} —— 雪地草坪（可蔓延/雪覆盖/退化冷域泥土）</li>
 * </ul>
 * 简单方块（泥土/木头）走 BlockAPI 批量注册，自动生成模型/方块状态/mineable 标签；
 * 特殊方块（树叶/草坪）手写注册 + 手写模型资源。
 * <p>
 * 注意：染梦耕地（dyedream_farmland）属于染梦维度，注册在
 * {@link PDBlocksSimple}，不在此类中。
 *
 * @see PDBlocks
 */
public class PDBlocksColdDomain {

    // ==================== 简单换皮方块（API 批量注册） ====================

    private static final Map<String, DeferredBlock<Block>> COLD_DOMAIN_SIMPLE = BlockAPI.registerSimpleBlocks()
            .add("cold_domain_dirt", Blocks.DIRT, BlockConfig.of()
                    .mineable("shovel").model("cube_all")
                    .tex("all", "pasterdream:block/cold_domain_dirt"))
            .add("cold_domain_log", Blocks.OAK_LOG, BlockConfig.of()
                    .mineable("axe").model("cube_column")
                    .tex("end", "pasterdream:block/cold_domain_log_top")
                    .tex("side", "pasterdream:block/cold_domain_log")
                    .blockFactory(RotatedPillarBlock::new))
            .add("stripped_cold_domain_log", Blocks.STRIPPED_OAK_LOG, BlockConfig.of()
                    .mineable("axe").model("cube_column")
                    .tex("end", "pasterdream:block/stripped_cold_domain_log_top")
                    .tex("side", "pasterdream:block/stripped_cold_domain_log")
                    .blockFactory(RotatedPillarBlock::new))
            .build();

    /** 冷域泥土 */
    public static final DeferredBlock<Block> COLD_DOMAIN_DIRT = COLD_DOMAIN_SIMPLE.get("cold_domain_dirt");
    /** 冷域木头 */
    public static final DeferredBlock<Block> COLD_DOMAIN_LOG = COLD_DOMAIN_SIMPLE.get("cold_domain_log");
    /** 去皮冷域木头 */
    public static final DeferredBlock<Block> STRIPPED_COLD_DOMAIN_LOG = COLD_DOMAIN_SIMPLE.get("stripped_cold_domain_log");

    // ==================== 特殊方块（手写注册） ====================

    /** 雪地草坪：可蔓延到冷域泥土、被雪覆盖切换 snowy 状态、退化时变为冷域泥土 */
    public static final DeferredBlock<SnowyColdDomainGrassBlock> SNOWY_COLD_DOMAIN_GRASS = PDBlocks.BLOCKS.registerBlock(
            "snowy_cold_domain_grass", SnowyColdDomainGrassBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK));

    /** 冷域树叶：原版树叶行为（随距离衰变、可被剪刀/锄头采集） */
    public static final DeferredBlock<LeavesBlock> COLD_DOMAIN_LEAVES = PDBlocks.BLOCKS.registerBlock(
            "cold_domain_leaves", LeavesBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES));

    // ==================== 补充配置（仅 mineable 标签，模型手写） ====================

    static {
        // 雪地草坪与树叶的模型特殊（含 snowy/叶形裁切），不走 BlockAPI 模型生成，
        // 仅补充工具挖掘标签。
        BlockAPI.putConfig("snowy_cold_domain_grass", BlockConfig.of().mineable("shovel"));
        BlockAPI.putConfig("cold_domain_leaves", BlockConfig.of().mineable("hoe"));
    }
}
