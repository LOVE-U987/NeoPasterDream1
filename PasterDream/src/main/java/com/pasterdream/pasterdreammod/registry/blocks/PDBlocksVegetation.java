package com.pasterdream.pasterdreammod.registry.blocks;

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.api.block.builder.VariantSetResult;
import com.pasterdream.pasterdreammod.block.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Map;


/**
 * 植被/植物/云朵/Phase1 作物类方块注册。
 *
 * @see PDBlocks
 */
public class PDBlocksVegetation {


    // ==================== 自定义模型方块 ====================

    /**
     * 木板屏风 (dyedream_planks_pane)
     * 继承 IronBarsBlock，类似玻璃板的连接逻辑，木质纹理
     */
    public static final DeferredBlock<DyedreamPlanksPaneBlock> DYEDREAM_PLANKS_PANE = PDBlocks.BLOCKS.registerBlock("dyedream_planks_pane",
            p -> new DyedreamPlanksPaneBlock());

    /**
     * 粉丁菇 0~3 号变种 (pinkagaric_0/1/2/3)
     * 粉色蘑菇，不同变种有不同的发光等级
     */
    public static final DeferredBlock<Block> PINKAGARIC_0 = PDBlocks.BLOCKS.registerBlock("pinkagaric_0",
            p -> new PinkagaricBlock(p, () -> 0), pinkagaricProps());
    public static final DeferredBlock<Block> PINKAGARIC_1 = PDBlocks.BLOCKS.registerBlock("pinkagaric_1",
            p -> new PinkagaricBlock(p, () -> 8), pinkagaricProps());
    public static final DeferredBlock<Block> PINKAGARIC_2 = PDBlocks.BLOCKS.registerBlock("pinkagaric_2",
            p -> new PinkagaricBlock(p.noOcclusion(), () -> 0), pinkagaricProps());
    public static final DeferredBlock<Block> PINKAGARIC_3 = PDBlocks.BLOCKS.registerBlock("pinkagaric_3",
            p -> new PinkagaricBlock(p.lightLevel(s -> 15), () -> 15), pinkagaricProps());

    private static BlockBehaviour.Properties pinkagaricProps() {
        return BlockBehaviour.Properties.of()
                .ignitedByLava()
                .instrument(NoteBlockInstrument.BASS)
                .sound(SoundType.WART_BLOCK)
                .strength(0.3f, 0.1f)
                .jumpFactor(1.2f);
    }

    /**
     * 花蕾 0~2 号变种 (dyedream_bud_0/1/2)
     * SimpleWaterloggedBlock，AXIS 轴向旋转，发光等级10
     */
    public static final DeferredBlock<DyedreamBudBlock> DYEDREAM_BUD_0 = PDBlocks.BLOCKS.registerBlock("dyedream_bud_0",
            p -> new DyedreamBudBlock(p, 0), budProps());
    public static final DeferredBlock<DyedreamBudBlock> DYEDREAM_BUD_1 = PDBlocks.BLOCKS.registerBlock("dyedream_bud_1",
            p -> new DyedreamBudBlock(p, 1), budProps());
    public static final DeferredBlock<DyedreamBudBlock> DYEDREAM_BUD_2 = PDBlocks.BLOCKS.registerBlock("dyedream_bud_2",
            p -> new DyedreamBudBlock(p, 2), budProps());

    private static BlockBehaviour.Properties budProps() {
        return BlockBehaviour.Properties.of()
                .sound(SoundType.AMETHYST_CLUSTER)
                .strength(1f, 0f)
                .lightLevel(s -> 6)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false);
    }

    /**
     * 冰蕾 (ice_bud_0)
     * SimpleWaterloggedBlock，FACING 六面朝向，发光等级9
     */
    public static final DeferredBlock<IceBudBlock> ICE_BUD_0 = PDBlocks.BLOCKS.registerBlock("ice_bud_0",
            IceBudBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.AMETHYST_CLUSTER)
                    .strength(1f, 0f)
                    .lightLevel(s -> 5)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .hasPostProcess((bs, br, bp) -> true)
                    .emissiveRendering((bs, br, bp) -> true)
                    .isRedstoneConductor((bs, br, bp) -> false));

    /**
     * 染梦荷叶 (dyedream_lily_pad)
     * 水面植物，继承 FlowerBlock，只能放在水上
     */
    public static final DeferredBlock<DyedreamLilyPadBlock> DYEDREAM_LILY_PAD = PDBlocks.BLOCKS.registerBlock("dyedream_lily_pad",
            p -> new DyedreamLilyPadBlock());

    /**
     * 染梦莲花 (dyedream_lotus)
     * 水面植物，继承 FlowerBlock，只能放在水上
     */
    public static final DeferredBlock<DyedreamLotusBlock> DYEDREAM_LOTUS = PDBlocks.BLOCKS.registerBlock("dyedream_lotus",
            p -> new DyedreamLotusBlock());

    /**
     * 染梦海草 (dyedream_seagrass)
     * SimpleWaterloggedBlock，水下植物，XZ 偏移
     */
    public static final DeferredBlock<DyedreamSeagrassBlock> DYEDREAM_SEAGRASS = PDBlocks.BLOCKS.registerBlock("dyedream_seagrass",
            p -> new DyedreamSeagrassBlock());

    /**
     * 染梦树苗 (dyedream_sapling)
     * 简化版，继承 FlowerBlock，无 EntityBlock
     */
    public static final DeferredBlock<DyedreamSaplingBlock> DYEDREAM_SAPLING = PDBlocks.BLOCKS.registerBlock("dyedream_sapling",
            p -> new DyedreamSaplingBlock());

    /**
     * 染梦裂纹 (dyedream_crack)
     * 简化版，保留 FACING+WATERLOGGED 属性，发光等级14，无 EntityBlock
     */
    public static final DeferredBlock<DyedreamCrackBlock> DYEDREAM_CRACK = PDBlocks.BLOCKS.registerBlock("dyedream_crack",
            p -> new DyedreamCrackBlock());

    // ==================== 云朵方块 ====================
    public static final DeferredBlock<CloudBlock> CLOUD = PDBlocks.BLOCKS.registerBlock("cloud", p -> new CloudBlock());
    public static final DeferredBlock<DarkCloudBlock> DARK_CLOUD = PDBlocks.BLOCKS.registerBlock("dark_cloud", p -> new DarkCloudBlock());
    public static final DeferredBlock<ThickCloudBlock> THICK_CLOUD = PDBlocks.BLOCKS.registerBlock("thick_cloud", p -> new ThickCloudBlock());

    // ==================== 染梦花草（移植自原版模组） ====================

    private static BlockBehaviour.Properties flowerProps() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION);
    }

    private static BlockBehaviour.Properties doublePlantProps() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SUNFLOWER);
    }

    // ========== API 批量注册：花（单格 + 双层） ==========

    private static final Map<String, DeferredBlock<Block>> FLOWERS_SINGLE = BlockAPI.batchRegister("flower")
            .indexList(1, 2, 3, 5, 6, 8, 9, 13, 14, 15, 16, 17)
            .factory((index, props) -> new DyedreamFlowerBlock(MobEffects.HUNGER, 100, props))
            .withProperties(flowerProps())
            .build();

    private static final Map<String, DeferredBlock<Block>> FLOWERS_DOUBLE = BlockAPI.batchRegister("flower")
            .indexList(7, 10, 11, 12, 18)
            .factory((index, props) -> new DyedreamDoublePlantBlock())
            .withProperties(doublePlantProps())
            .build();

    public static final DeferredBlock<Block> FLOWER_1 = FLOWERS_SINGLE.get("flower_1");
    public static final DeferredBlock<Block> FLOWER_2 = FLOWERS_SINGLE.get("flower_2");
    public static final DeferredBlock<Block> FLOWER_3 = FLOWERS_SINGLE.get("flower_3");
    public static final DeferredBlock<Block> FLOWER_5 = FLOWERS_SINGLE.get("flower_5");
    public static final DeferredBlock<Block> FLOWER_6 = FLOWERS_SINGLE.get("flower_6");
    public static final DeferredBlock<Block> FLOWER_7 = FLOWERS_DOUBLE.get("flower_7");
    public static final DeferredBlock<Block> FLOWER_8 = FLOWERS_SINGLE.get("flower_8");
    public static final DeferredBlock<Block> FLOWER_9 = FLOWERS_SINGLE.get("flower_9");
    public static final DeferredBlock<Block> FLOWER_10 = FLOWERS_DOUBLE.get("flower_10");
    public static final DeferredBlock<Block> FLOWER_11 = FLOWERS_DOUBLE.get("flower_11");
    public static final DeferredBlock<Block> FLOWER_12 = FLOWERS_DOUBLE.get("flower_12");
    public static final DeferredBlock<Block> FLOWER_13 = FLOWERS_SINGLE.get("flower_13");
    public static final DeferredBlock<Block> FLOWER_14 = FLOWERS_SINGLE.get("flower_14");
    public static final DeferredBlock<Block> FLOWER_15 = FLOWERS_SINGLE.get("flower_15");
    public static final DeferredBlock<Block> FLOWER_16 = FLOWERS_SINGLE.get("flower_16");
    public static final DeferredBlock<Block> FLOWER_17 = FLOWERS_SINGLE.get("flower_17");
    public static final DeferredBlock<Block> FLOWER_18 = FLOWERS_DOUBLE.get("flower_18");

    // ========== API 批量注册：草（单格 + 双层） ==========

    private static final Map<String, DeferredBlock<Block>> GRASSES_SINGLE = BlockAPI.batchRegister("grass")
            .indexList(1, 2, 3, 5, 6, 7, 8, 9, 11, 12, 13, 14)
            .factory((index, props) -> new DyedreamFlowerBlock(MobEffects.MOVEMENT_SLOWDOWN, 100, props))
            .withProperties(flowerProps())
            .build();

    private static final Map<String, DeferredBlock<Block>> GRASSES_DOUBLE = BlockAPI.batchRegister("grass")
            .indexList(4, 10, 15)
            .factory((index, props) -> new DyedreamDoublePlantBlock())
            .withProperties(doublePlantProps())
            .build();

    public static final DeferredBlock<Block> GRASS_1 = GRASSES_SINGLE.get("grass_1");
    public static final DeferredBlock<Block> GRASS_2 = GRASSES_SINGLE.get("grass_2");
    public static final DeferredBlock<Block> GRASS_3 = GRASSES_SINGLE.get("grass_3");
    public static final DeferredBlock<Block> GRASS_4 = GRASSES_DOUBLE.get("grass_4");
    public static final DeferredBlock<Block> GRASS_5 = GRASSES_SINGLE.get("grass_5");
    public static final DeferredBlock<Block> GRASS_6 = GRASSES_SINGLE.get("grass_6");
    public static final DeferredBlock<Block> GRASS_7 = GRASSES_SINGLE.get("grass_7");
    public static final DeferredBlock<Block> GRASS_8 = GRASSES_SINGLE.get("grass_8");
    public static final DeferredBlock<Block> GRASS_9 = GRASSES_SINGLE.get("grass_9");
    public static final DeferredBlock<Block> GRASS_10 = GRASSES_DOUBLE.get("grass_10");
    public static final DeferredBlock<Block> GRASS_11 = GRASSES_SINGLE.get("grass_11");
    public static final DeferredBlock<Block> GRASS_12 = GRASSES_SINGLE.get("grass_12");
    public static final DeferredBlock<Block> GRASS_13 = GRASSES_SINGLE.get("grass_13");
    public static final DeferredBlock<Block> GRASS_14 = GRASSES_SINGLE.get("grass_14");
    public static final DeferredBlock<Block> GRASS_15 = GRASSES_DOUBLE.get("grass_15");

    // ==================== Phase 1: 移植物块材料 ====================

    // ========== 存储方块 ==========

    public static final DeferredBlock<Block> TITANIUM_BLOCK = PDBlocks.BLOCKS.registerBlock("titanium_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> RAW_TITANIUM_BLOCK = PDBlocks.BLOCKS.registerBlock("raw_titanium_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK));

    public static final DeferredBlock<Block> MOLTENGOLD_BLOCK = PDBlocks.BLOCKS.registerBlock("moltengold_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).lightLevel(s -> 15));

    public static final DeferredBlock<Block> BLACKMETAL_BLOCK = PDBlocks.BLOCKS.registerBlock("blackmetal_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> CHARGED_AMETHYST_BLOCK = PDBlocks.BLOCKS.registerBlock("charged_amethyst_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK));

    public static final DeferredBlock<Block> WIND_IRON_BLOCK = PDBlocks.BLOCKS.registerBlock("wind_iron_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

    // ========== 矿石方块 ==========

    public static final DeferredBlock<Block> DEEPSLATE_TITANIUM_ORE = PDBlocks.BLOCKS.registerBlock("deepslate_titanium_ore", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE));

    public static final DeferredBlock<Block> MOLTENGOLD_ORE = PDBlocks.BLOCKS.registerBlock("moltengold_ore", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_GOLD_ORE));

    public static final DeferredBlock<Block> SOUL_ORE = PDBlocks.BLOCKS.registerBlock("soul_ore", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_SAND).strength(3f, 3f).requiresCorrectToolForDrops());

    // ========== 装饰/植物方块（自定义类） ==========

    public static final DeferredBlock<Pebble0Block> PEBBLE_0 = PDBlocks.BLOCKS.registerBlock("pebble_0", Pebble0Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    public static final DeferredBlock<ShadowLight0Block> SHADOW_LIGHT_0 = PDBlocks.BLOCKS.registerBlock("shadow_light_0", ShadowLight0Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).lightLevel(s -> 15)
                    .requiresCorrectToolForDrops()
                    .hasPostProcess((bs, br, bp) -> true)
                    .emissiveRendering((bs, br, bp) -> true));

    public static final DeferredBlock<Vine0Block> VINE_0 = PDBlocks.BLOCKS.registerBlock("vine_0", Vine0Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.VINE).noCollission().lightLevel(s -> 14));

    public static final DeferredBlock<GoldenrodBlock> GOLDENROD = PDBlocks.BLOCKS.registerBlock("goldenrod",
            GoldenrodBlock::new, flowerProps());

    public static final DeferredBlock<Crop0ABlock> CROP_0A = PDBlocks.BLOCKS.registerBlock("crop_0a",
            Crop0ABlock::new, flowerProps());

    public static final DeferredBlock<Crop1ABlock> CROP_1A = PDBlocks.BLOCKS.registerBlock("crop_1a",
            Crop1ABlock::new, flowerProps());

    public static final DeferredBlock<Crop3ABlock> CROP_3A = PDBlocks.BLOCKS.registerBlock("crop_3a",
            Crop3ABlock::new, flowerProps());

    public static final DeferredBlock<Crop4ABlock> CROP_4A = PDBlocks.BLOCKS.registerBlock("crop_4a",
            Crop4ABlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).noCollission());

    // ========== 作物系列补全 ==========

    public static final DeferredBlock<Crop2ABlock> CROP_2A = PDBlocks.BLOCKS.registerBlock("crop_2a",
            Crop2ABlock::new, flowerProps());
}
