package com.pasterdream.pasterdreammod.registry.blocks;

import com.pasterdream.pasterdreammod.block.CalleCardBlock;
import com.pasterdream.pasterdreammod.block.ChristmasLightsBlock;
import com.pasterdream.pasterdreammod.block.ClayPot0Block;
import com.pasterdream.pasterdreammod.block.ClaypanPlateBlock;
import com.pasterdream.pasterdreammod.block.DreamSpawner1Block;
import com.pasterdream.pasterdreammod.block.DyedreamCropFlowerBlock;
import com.pasterdream.pasterdreammod.block.FigVineBlock;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;


/**
 * 杂项补全方块注册（收集/剧情装饰、陶器、作物 B 系、藤蔓植被等）。
 * <p>
 * 注册名与原版完全一致，属性逐一对照原版 block/*.java 的 Properties 链。
 *
 * @see PDBlocks
 */
public class PDBlocksMisc {


    // ==================== 发光与装饰 ====================

    /** 光球（发光 13、无碰撞、自发光渲染） */
    public static final DeferredBlock<Block> LIGHTBALL = PDBlocks.BLOCKS.registerBlock("lightball",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.SWEET_BERRY_BUSH)
                    .instabreak()
                    .lightLevel(s -> 13)
                    .noCollission()
                    .noOcclusion()
                    .hasPostProcess((bs, br, bp) -> true)
                    .emissiveRendering((bs, br, bp) -> true)
                    .isRedstoneConductor((bs, br, bp) -> false));

    /**
     * 陶罐（世界生成硬依赖：ground_feature_shadow_7 引用 facing+waterlogged 状态）
     * 破坏按战利品表随机掉落杂物
     */
    public static final DeferredBlock<ClayPot0Block> CLAY_POT_0 = PDBlocks.BLOCKS.registerBlock("clay_pot_0",
            ClayPot0Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.BONE_BLOCK)
                    .strength(0.5f, 0.1f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 陶盘通用属性：石音效、硬度 0.5/10、需正确工具 */
    private static BlockBehaviour.Properties claypanProps() {
        return BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.STONE)
                .strength(0.5f, 10f)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false);
    }

    /** 陶盘 0 号（claypan_1 带方块实体逻辑，移交 GUI 波次）—— 3 像素高薄盘碰撞箱 */
    public static final DeferredBlock<ClaypanPlateBlock> CLAYPAN_0 = PDBlocks.BLOCKS.registerBlock("claypan_0",
            ClaypanPlateBlock::new, claypanProps());

    /** 陶盘 2 号 —— 3 像素高薄盘碰撞箱 */
    public static final DeferredBlock<ClaypanPlateBlock> CLAYPAN_2 = PDBlocks.BLOCKS.registerBlock("claypan_2",
            ClaypanPlateBlock::new, claypanProps());

    /** 凯尔卡牌方块（收集系统地面卡牌，右键交互移交收集波次） */
    public static final DeferredBlock<CalleCardBlock> CALLE_CARD_BLOCK = PDBlocks.BLOCKS.registerBlock("calle_card_block",
            CalleCardBlock::new, BlockBehaviour.Properties.of()
                    .ignitedByLava()
                    .sound(SoundType.CANDLE)
                    .instabreak()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 圣诞彩灯（贴墙灯串，发光 12） */
    public static final DeferredBlock<ChristmasLightsBlock> CHRISTMAS_LIGHTS = PDBlocks.BLOCKS.registerBlock("christmas_lights",
            ChristmasLightsBlock::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.HAT)
                    .sound(SoundType.LANTERN)
                    .strength(1f, 10f)
                    .lightLevel(s -> 12)
                    .noCollission()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 梦境刷怪机 1 号（纯装饰外壳，0 号带刷怪 BE 移交 GUI 波次） */
    public static final DeferredBlock<DreamSpawner1Block> DREAM_SPAWNER_1 = PDBlocks.BLOCKS.registerBlock("dream_spawner_1",
            DreamSpawner1Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
                    .strength(5f, 10f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    // ==================== 作物 B 系与植被 ====================

    /** 作物花通用属性（无随机刻版本） */
    private static BlockBehaviour.Properties cropProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .sound(SoundType.GRASS)
                .instabreak()
                .noCollission()
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .pushReaction(PushReaction.DESTROY);
    }

    /** 作物 0b（再生效果，只能种在染梦泥土/草上） */
    public static final DeferredBlock<DyedreamCropFlowerBlock> CROP_0B = PDBlocks.BLOCKS.registerBlock("crop_0b",
            p -> new DyedreamCropFlowerBlock(MobEffects.REGENERATION, 100, p), cropProps());

    /** 作物 1b（速度效果，带随机刻，可种在原版泥土/草方块上） */
    public static final DeferredBlock<FlowerBlock> CROP_1B = PDBlocks.BLOCKS.registerBlock("crop_1b",
            p -> new FlowerBlock(MobEffects.MOVEMENT_SPEED, 100, p), cropProps().randomTicks());

    /** 作物 2b（夜视效果，微光 3 + 自发光渲染，只能种在染梦泥土/草上） */
    public static final DeferredBlock<DyedreamCropFlowerBlock> CROP_2B = PDBlocks.BLOCKS.registerBlock("crop_2b",
            p -> new DyedreamCropFlowerBlock(MobEffects.NIGHT_VISION, 100, p), cropProps()
                    .hasPostProcess((bs, br, bp) -> true)
                    .emissiveRendering((bs, br, bp) -> true)
                    .lightLevel(s -> 3));

    /** 作物 3b（再生效果，只能种在染梦泥土/草上） */
    public static final DeferredBlock<DyedreamCropFlowerBlock> CROP_3B = PDBlocks.BLOCKS.registerBlock("crop_3b",
            p -> new DyedreamCropFlowerBlock(MobEffects.REGENERATION, 100, p), cropProps());

    /** 作物 4b（速度效果，带随机刻，甜浆果丛音效 + XZ 偏移） */
    public static final DeferredBlock<FlowerBlock> CROP_4B = PDBlocks.BLOCKS.registerBlock("crop_4b",
            p -> new FlowerBlock(MobEffects.MOVEMENT_SPEED, 100, p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .randomTicks()
                    .sound(SoundType.SWEET_BERRY_BUSH)
                    .instabreak()
                    .noCollission()
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY));

    /**
     * 丛林孢子植株（世界生成硬依赖：configured_feature/jungle_spore_plant 引用）
     * 触碰中毒效果，破坏掉落丛林孢子
     */
    public static final DeferredBlock<FlowerBlock> JUNGLE_SPORE_PLANT = PDBlocks.BLOCKS.registerBlock("jungle_spore_plant",
            p -> new FlowerBlock(MobEffects.POISON, 100, p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .sound(SoundType.GRASS)
                    .instabreak()
                    .noCollission()
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY));

    /** 四叶草（幸运效果，睡莲音效） */
    public static final DeferredBlock<FlowerBlock> FOURLEAF_CLOVER = PDBlocks.BLOCKS.registerBlock("fourleaf_clover",
            p -> new FlowerBlock(MobEffects.LUCK, 100, p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .sound(SoundType.LILY_PAD)
                    .instabreak()
                    .noCollission()
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY));

    /** 无花果藤（树冠藤幕，破坏掉落无花果） */
    public static final DeferredBlock<FigVineBlock> FIG_VINE = PDBlocks.BLOCKS.registerBlock("fig_vine",
            FigVineBlock::new, BlockBehaviour.Properties.of()
                    .ignitedByLava()
                    .sound(SoundType.GRASS)
                    .strength(0.01f, 0.1f)
                    .noCollission()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 纪念物 11 号（花型剧情道具方块，速度效果） */
    public static final DeferredBlock<FlowerBlock> MEMENTO_ITEM_11 = PDBlocks.BLOCKS.registerBlock("memento_item_11",
            p -> new FlowerBlock(MobEffects.MOVEMENT_SPEED, 100, p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .sound(SoundType.GRASS)
                    .instabreak()
                    .noCollission()
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY));
}
