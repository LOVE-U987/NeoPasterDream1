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
 * 暗影地牢/竞技场方块注册。
 *
 * @see PDBlocks
 */
public class PDBlocksDungeon {


    // ==================== 暗影地牢方块（BOSS 竞技场场地） ====================

    /** 暗影地牢砖 0 — 基础砖（不可破坏，竞技场墙体） */
    public static final DeferredBlock<Block> SHADOW_DUNGEON_BLOCK_0 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_block_0",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(-1.0f, 3600000.0f)
                    .lightLevel((bs) -> 0));

    /** 暗影地牢砖 1 — 带顶底花纹砖（不可破坏，竞技场墙体） */
    public static final DeferredBlock<Block> SHADOW_DUNGEON_BLOCK_1 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_block_1",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(-1.0f, 3600000.0f));

    /** 暗影地牢砖 2 — 花纹砖（不可破坏，竞技场墙体） */
    public static final DeferredBlock<Block> SHADOW_DUNGEON_BLOCK_2 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_block_2",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(-1.0f, 3600000.0f));

    /** 暗影地牢砖 3 — 雕刻砖（不可破坏，竞技场墙体） */
    public static final DeferredBlock<Block> SHADOW_DUNGEON_BLOCK_3 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_block_3",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(-1.0f, 3600000.0f));

    /** 暗影地牢砖 4 — 发光砖（不可破坏，竞技场光源） */
    public static final DeferredBlock<Block> SHADOW_DUNGEON_BLOCK_4 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_block_4",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(-1.0f, 3600000.0f)
                    .lightLevel((bs) -> 8));

    /** 暗影地牢砖 5 — 楼梯形态（不可破坏，竞技场楼梯） */
    public static final DeferredBlock<StairBlock> SHADOW_DUNGEON_BLOCK_5 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_block_5",
            p -> new StairBlock(SHADOW_DUNGEON_BLOCK_2.get().defaultBlockState(), p),
            BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(-1.0f, 3600000.0f));

    /** 暗影地牢砖 6 — 台阶形态（不可破坏，竞技场台阶） */
    public static final DeferredBlock<SlabBlock> SHADOW_DUNGEON_BLOCK_6 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_block_6",
            SlabBlock::new,
            BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(-1.0f, 3600000.0f));

    /** 暗影竞技场地面砖 0 — 竞技场地面（不可破坏） */
    public static final DeferredBlock<Block> SHADOW_ARENA_BLOCK_0 = PDBlocks.BLOCKS.registerBlock("shadow_arena_block_0",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(-1.0f, 3600000.0f));

    // ==================== 暗影地牢功能性方块 ====================

    /** 松动暗影地牢砖 — 可破坏版地牢砖（需正确工具） */
    public static final DeferredBlock<Block> LOOSE_SHADOW_DUNGEON_BLOCK = PDBlocks.BLOCKS.registerBlock("loose_shadow_dungeon_block",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(10.0f)
                    .requiresCorrectToolForDrops());

    /** 暗影地牢门 0 — 水平薄板门（不可破坏，铁链声） */
    public static final DeferredBlock<ShadowDungeonDoorBlock> SHADOW_DUNGEON_DOOR_0 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_door_0",
            p -> new ShadowDungeonDoorBlock(p, Block.box(0, 7, 0, 16, 9, 16)),
            BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.CHAIN)
                    .strength(-1.0f, 3600000.0f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 暗影地牢门 1 — 门0的无交互版本 */
    public static final DeferredBlock<ShadowDungeonDoorBlock> SHADOW_DUNGEON_DOOR_1 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_door_1",
            p -> new ShadowDungeonDoorBlock(p, Block.box(0, 7, 0, 16, 9, 16)),
            BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.CHAIN)
                    .strength(-1.0f, 3600000.0f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 暗影地牢门 2 — 整高门（不可破坏，石声） */
    public static final DeferredBlock<ShadowDungeonDoorBlock> SHADOWDUNGEONDOOR_2 = PDBlocks.BLOCKS.registerBlock("shadowdungeondoor_2",
            p -> new ShadowDungeonDoorBlock(p, Block.box(0, 0, 4, 16, 16, 12)),
            BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(-1.0f, 3600000.0f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 暗影地牢门 3 — 门2的无交互版本（深板岩声） */
    public static final DeferredBlock<ShadowDungeonDoorBlock> SHADOWDUNGEONDOOR_3 = PDBlocks.BLOCKS.registerBlock("shadowdungeondoor_3",
            p -> new ShadowDungeonDoorBlock(p, Block.box(0, 0, 4, 16, 16, 12)),
            BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.DEEPSLATE)
                    .strength(-1.0f, 3600000.0f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 暗影地牢钥匙 0 — 墙挂式（可破坏，掉钥匙物品） */
    public static final DeferredBlock<ShadowDungeonKeyBlock> SHADOW_DUNGEON_KEY_0 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_key_0",
            p -> new ShadowDungeonKeyBlock(p, true),
            BlockBehaviour.Properties.of()
                    .sound(SoundType.CHAIN)
                    .strength(0.1f, 50.0f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 暗影地牢钥匙 1 — 地置式（可破坏，掉钥匙物品） */
    public static final DeferredBlock<ShadowDungeonKeyBlock> SHADOW_DUNGEON_KEY_1 = PDBlocks.BLOCKS.registerBlock("shadow_dungeon_key_1",
            p -> new ShadowDungeonKeyBlock(p, false),
            BlockBehaviour.Properties.of()
                    .sound(SoundType.CHAIN)
                    .strength(0.1f, 50.0f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 暗影蜡烛 — 发光等级13，易破坏 */
    public static final DeferredBlock<Block> SHADOWCANDLE = PDBlocks.BLOCKS.registerBlock("shadowcandle",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.CANDLE)
                    .strength(0.1f, 0.0f)
                    .lightLevel(s -> 13)
                    .noOcclusion()
                    .emissiveRendering((bs, br, bp) -> true)
                    .hasPostProcess((bs, br, bp) -> true)
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 暗影高炉核心 — 多方块结构核心（可破坏，金属声） */
    public static final DeferredBlock<Block> SHADOW_BLAST_FURNACE_CORE = PDBlocks.BLOCKS.registerBlock("shadow_blast_furnace_core",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
                    .strength(3.0f, 1.0f)
                    .requiresCorrectToolForDrops());

    // ==================== 暗影书架系列（4种样式） ====================

    /** 暗影书架 0 — 朝向方块，可被岩浆点燃 */
    public static final DeferredBlock<ShadowshelfBlock> SHADOWSHELF_0 = PDBlocks.BLOCKS.registerBlock("shadowshelf_0",
            ShadowshelfBlock::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD)
                    .strength(2.0f, 3.0f)
                    .ignitedByLava()
                    .requiresCorrectToolForDrops());

    /** 暗影书架 1 */
    public static final DeferredBlock<ShadowshelfBlock> SHADOWSHELF_1 = PDBlocks.BLOCKS.registerBlock("shadowshelf_1",
            ShadowshelfBlock::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD)
                    .strength(2.0f, 3.0f)
                    .ignitedByLava()
                    .requiresCorrectToolForDrops());

    /** 暗影书架 2 */
    public static final DeferredBlock<ShadowshelfBlock> SHADOWSHELF_2 = PDBlocks.BLOCKS.registerBlock("shadowshelf_2",
            ShadowshelfBlock::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD)
                    .strength(2.0f, 3.0f)
                    .ignitedByLava()
                    .requiresCorrectToolForDrops());

    /** 暗影书架 3 */
    public static final DeferredBlock<ShadowshelfBlock> SHADOWSHELF_3 = PDBlocks.BLOCKS.registerBlock("shadowshelf_3",
            ShadowshelfBlock::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD)
                    .strength(2.0f, 3.0f)
                    .ignitedByLava()
                    .requiresCorrectToolForDrops());

    // ==================== 暗影裂隙系列（6种发光等级） ====================

    /** 暗影裂隙 0 — 发光等级4，完全挡光 */
    public static final DeferredBlock<Block> SHADOW_FISSURE_0 = PDBlocks.BLOCKS.registerBlock("shadow_fissure_0",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.GLASS)
                    .strength(-1.0f, 3600000.0f)
                    .lightLevel(s -> 4)
                    .emissiveRendering((bs, br, bp) -> true)
                    .hasPostProcess((bs, br, bp) -> true));

    /** 暗影裂隙 1 — 发光等级4，玻璃式透明 */
    public static final DeferredBlock<Block> SHADOW_FISSURE_1 = PDBlocks.BLOCKS.registerBlock("shadow_fissure_1",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.GLASS)
                    .strength(-1.0f, 3600000.0f)
                    .lightLevel(s -> 4)
                    .noOcclusion()
                    .emissiveRendering((bs, br, bp) -> true)
                    .hasPostProcess((bs, br, bp) -> true)
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 暗影裂隙 2 — 发光等级7，完全挡光 */
    public static final DeferredBlock<Block> SHADOW_FISSURE_2 = PDBlocks.BLOCKS.registerBlock("shadow_fissure_2",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.GLASS)
                    .strength(-1.0f, 3600000.0f)
                    .lightLevel(s -> 7)
                    .emissiveRendering((bs, br, bp) -> true)
                    .hasPostProcess((bs, br, bp) -> true));

    /** 暗影裂隙 3 — 发光等级7，玻璃式透明 */
    public static final DeferredBlock<Block> SHADOW_FISSURE_3 = PDBlocks.BLOCKS.registerBlock("shadow_fissure_3",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.GLASS)
                    .strength(-1.0f, 3600000.0f)
                    .lightLevel(s -> 7)
                    .noOcclusion()
                    .emissiveRendering((bs, br, bp) -> true)
                    .hasPostProcess((bs, br, bp) -> true)
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 暗影裂隙 4 — 发光等级10，完全挡光 */
    public static final DeferredBlock<Block> SHADOW_FISSURE_4 = PDBlocks.BLOCKS.registerBlock("shadow_fissure_4",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.GLASS)
                    .strength(-1.0f, 3600000.0f)
                    .lightLevel(s -> 10)
                    .emissiveRendering((bs, br, bp) -> true)
                    .hasPostProcess((bs, br, bp) -> true));

    /** 暗影裂隙 5 — 发光等级10，玻璃式透明 */
    public static final DeferredBlock<Block> SHADOW_FISSURE_5 = PDBlocks.BLOCKS.registerBlock("shadow_fissure_5",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.GLASS)
                    .strength(-1.0f, 3600000.0f)
                    .lightLevel(s -> 10)
                    .noOcclusion()
                    .emissiveRendering((bs, br, bp) -> true)
                    .hasPostProcess((bs, br, bp) -> true)
                    .isRedstoneConductor((bs, br, bp) -> false));
}
