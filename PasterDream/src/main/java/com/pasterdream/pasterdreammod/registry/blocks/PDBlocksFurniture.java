package com.pasterdream.pasterdreammod.registry.blocks;

import com.pasterdream.pasterdreammod.block.AuraBuffBlock;
import com.pasterdream.pasterdreammod.block.BirdsNestBlock;
import com.pasterdream.pasterdreammod.block.BrokenShadowDungeonProtalBlock;
import com.pasterdream.pasterdreammod.block.Claypan1Block;
import com.pasterdream.pasterdreammod.block.DesertHeroTombBlock;
import com.pasterdream.pasterdreammod.block.GlassJarBlock;
import com.pasterdream.pasterdreammod.block.GuardCrystalBlock;
import com.pasterdream.pasterdreammod.block.LostSwordBlockBlock;
import com.pasterdream.pasterdreammod.block.PicnicBasketBlock;
import com.pasterdream.pasterdreammod.block.ShadowBedBlock;
import com.pasterdream.pasterdreammod.block.ShadowBrazierBlock;
import com.pasterdream.pasterdreammod.block.ShadowDeskBlock;
import com.pasterdream.pasterdreammod.block.ShadowTrap0Block;
import com.pasterdream.pasterdreammod.block.TrueShadowBedBlock;
import com.pasterdream.pasterdreammod.block.TwilightLanternBlock;
import com.pasterdream.pasterdreammod.block.WindKnightSpawnblockBlock;
import com.pasterdream.pasterdreammod.block.WindmoorCrateBlock;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.List;

/**
 * 容器/家具/杂项方块组注册（[分区F]，波次 W4）。
 * <p>
 * 方块属性逐一对照原版 {@code net.pasterdream.block.*}：
 * 风之骑士唤醒台 ×5、玻璃罐 ×3、容器 GUI 方块 ×3（野餐篮/影之桌/风泊木箱）、
 * 影之床 ×2、以及鸟巢/破损传送门/干裂粘土/荒漠英雄之墓/守护方块组/
 * 遗落之剑/阴影火盆/暗影地牢传送门/阴影陷阱/暮影之笼等杂项功能方块。
 *
 * @see PDBlocks
 */
public class PDBlocksFurniture {

    /** 不可破坏石质（noOcclusion）——结构触发/剧情类方块通用 */
    private static BlockBehaviour.Properties unbreakableStone() {
        return BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(-1, 3600000)
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false);
    }

    // ==================== 风之骑士唤醒台（wind_knight_spawnblock_0..4） ====================

    public static final DeferredBlock<WindKnightSpawnblockBlock> WIND_KNIGHT_SPAWNBLOCK_0 =
            PDBlocks.BLOCKS.registerBlock("wind_knight_spawnblock_0",
                    p -> new WindKnightSpawnblockBlock(0, p), unbreakableStone());
    public static final DeferredBlock<WindKnightSpawnblockBlock> WIND_KNIGHT_SPAWNBLOCK_1 =
            PDBlocks.BLOCKS.registerBlock("wind_knight_spawnblock_1",
                    p -> new WindKnightSpawnblockBlock(1, p), unbreakableStone());
    public static final DeferredBlock<WindKnightSpawnblockBlock> WIND_KNIGHT_SPAWNBLOCK_2 =
            PDBlocks.BLOCKS.registerBlock("wind_knight_spawnblock_2",
                    p -> new WindKnightSpawnblockBlock(2, p), unbreakableStone());
    public static final DeferredBlock<WindKnightSpawnblockBlock> WIND_KNIGHT_SPAWNBLOCK_3 =
            PDBlocks.BLOCKS.registerBlock("wind_knight_spawnblock_3",
                    p -> new WindKnightSpawnblockBlock(3, p), unbreakableStone());
    public static final DeferredBlock<WindKnightSpawnblockBlock> WIND_KNIGHT_SPAWNBLOCK_4 =
            PDBlocks.BLOCKS.registerBlock("wind_knight_spawnblock_4",
                    p -> new WindKnightSpawnblockBlock(4, p), unbreakableStone());

    // ==================== 玻璃罐（ecology/firefly/light_firefly_glass_jar） ====================

    public static final DeferredBlock<GlassJarBlock> ECOLOGY_GLASS_JAR =
            PDBlocks.BLOCKS.registerBlock("ecology_glass_jar",
                    p -> new GlassJarBlock(GlassJarBlock.Kind.ECOLOGY, p),
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.GLASS)
                            .strength(0.1f)
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    public static final DeferredBlock<GlassJarBlock> FIREFLY_GLASS_JAR =
            PDBlocks.BLOCKS.registerBlock("firefly_glass_jar",
                    p -> new GlassJarBlock(GlassJarBlock.Kind.FIREFLY, p),
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.GLASS)
                            .strength(0.1f)
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    public static final DeferredBlock<GlassJarBlock> LIGHT_FIREFLY_GLASS_JAR =
            PDBlocks.BLOCKS.registerBlock("light_firefly_glass_jar",
                    p -> new GlassJarBlock(GlassJarBlock.Kind.LIGHT_FIREFLY, p),
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.GLASS)
                            .strength(0.1f)
                            .lightLevel(s -> 12)
                            .noOcclusion()
                            .hasPostProcess((bs, br, bp) -> true)
                            .emissiveRendering((bs, br, bp) -> true)
                            .isRedstoneConductor((bs, br, bp) -> false));

    // ==================== 容器 GUI 方块 ====================

    /** 野餐篮 (picnic_basket)：15 格 GUI，GeckoLib 渲染 */
    public static final DeferredBlock<PicnicBasketBlock> PICNIC_BASKET =
            PDBlocks.BLOCKS.registerBlock("picnic_basket", PicnicBasketBlock::new,
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.SCAFFOLDING)
                            .strength(0.4f)
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    /** 影之桌 (shadow_desk)：1 格展示 GUI */
    public static final DeferredBlock<ShadowDeskBlock> SHADOW_DESK =
            PDBlocks.BLOCKS.registerBlock("shadow_desk", ShadowDeskBlock::new,
                    BlockBehaviour.Properties.of()
                            .ignitedByLava()
                            .instrument(NoteBlockInstrument.BASS)
                            .sound(SoundType.WOOD)
                            .strength(1f)
                            .requiresCorrectToolForDrops()
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    /** 风泊木箱 (windmoor_crate)：15 格 GUI + 调试战利品粒子 */
    public static final DeferredBlock<WindmoorCrateBlock> WINDMOOR_CRATE =
            PDBlocks.BLOCKS.registerBlock("windmoor_crate", WindmoorCrateBlock::new,
                    BlockBehaviour.Properties.of()
                            .ignitedByLava()
                            .instrument(NoteBlockInstrument.BASS)
                            .sound(SoundType.WOOD)
                            .strength(1f)
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    // ==================== 影之床 ====================

    /** 影之床 (shadow_bed)：夜寝进入灯影世界 */
    public static final DeferredBlock<ShadowBedBlock> SHADOW_BED =
            PDBlocks.BLOCKS.registerBlock("shadow_bed", ShadowBedBlock::new,
                    BlockBehaviour.Properties.of()
                            .ignitedByLava()
                            .instrument(NoteBlockInstrument.BASS)
                            .sound(SoundType.WOOD)
                            .strength(2f, 1f)
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    /** 真·影之床 (true_shadow_bed)：暮影共鸣 + 影之抉择入口 */
    public static final DeferredBlock<TrueShadowBedBlock> TRUE_SHADOW_BED =
            PDBlocks.BLOCKS.registerBlock("true_shadow_bed", TrueShadowBedBlock::new,
                    BlockBehaviour.Properties.of()
                            .ignitedByLava()
                            .instrument(NoteBlockInstrument.BASS)
                            .sound(SoundType.WOOD)
                            .strength(-1, 3600000)
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    // ==================== 杂项功能方块 ====================

    /** 鸟巢 (birds_nest)：随机刻孵化鹦鹉 */
    public static final DeferredBlock<BirdsNestBlock> BIRDS_NEST =
            PDBlocks.BLOCKS.registerBlock("birds_nest", BirdsNestBlock::new,
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.CAVE_VINES)
                            .strength(1f, 10f)
                            .noOcclusion()
                            .randomTicks()
                            .isRedstoneConductor((bs, br, bp) -> false));

    /** 破损的暗影地牢传送门 (broken_shadow_dungeon_protal，保留原版拼写) */
    public static final DeferredBlock<BrokenShadowDungeonProtalBlock> BROKEN_SHADOW_DUNGEON_PROTAL =
            PDBlocks.BLOCKS.registerBlock("broken_shadow_dungeon_protal",
                    BrokenShadowDungeonProtalBlock::new, unbreakableStone());

    /** 干裂粘土层·湿润 (claypan_1)：晾晒转化 claypan_2 */
    public static final DeferredBlock<Claypan1Block> CLAYPAN_1 =
            PDBlocks.BLOCKS.registerBlock("claypan_1", Claypan1Block::new,
                    BlockBehaviour.Properties.of()
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .sound(SoundType.STONE)
                            .strength(0.5f, 10f)
                            .requiresCorrectToolForDrops()
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    /** 荒漠英雄之墓 (desert_hero_tomb)：亡魂任务链 */
    public static final DeferredBlock<DesertHeroTombBlock> DESERT_HERO_TOMB =
            PDBlocks.BLOCKS.registerBlock("desert_hero_tomb", DesertHeroTombBlock::new, unbreakableStone());

    /** 守护者方块 (guard_block)：范围禁止改造 buff */
    public static final DeferredBlock<AuraBuffBlock> GUARD_BLOCK =
            PDBlocks.BLOCKS.registerBlock("guard_block",
                    p -> new AuraBuffBlock(PDEffects.GUARD_BLOCK_BUFF::holder, 16,
                            () -> PDBlockEntitiesFurniture.GUARD_BLOCK.get(),
                            List.of("§4不可破坏",
                                    "§7在规则§epasterdreamDebugmode§7处于关闭状态时",
                                    "限制方块NBT:range边长范围内玩家的破坏和放置行为"), p),
                    BlockBehaviour.Properties.of()
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .sound(SoundType.STONE)
                            .strength(-1, 3600000));

    /** 行动抑制方块 (restrainmove_block)：范围行动抑制 buff */
    public static final DeferredBlock<AuraBuffBlock> RESTRAINMOVE_BLOCK =
            PDBlocks.BLOCKS.registerBlock("restrainmove_block",
                    p -> new AuraBuffBlock(PDEffects.RESTRAINMOVE_BLOCK_BUFF::holder, 0,
                            () -> PDBlockEntitiesFurniture.RESTRAINMOVE_BLOCK.get(),
                            List.of("§7在规则§epasterdreamDebugmode§7处于关闭状态时",
                                    "限制方块NBT:range边长范围内玩家的瞬身术 跳跃提升 飞行行为"), p),
                    BlockBehaviour.Properties.of()
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .sound(SoundType.STONE)
                            .strength(-1, 3600000));

    /** 守护者水晶 (guard_crystal)：右键自毁解除区域保护 */
    public static final DeferredBlock<GuardCrystalBlock> GUARD_CRYSTAL =
            PDBlocks.BLOCKS.registerBlock("guard_crystal", GuardCrystalBlock::new,
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.STONE)
                            .strength(100f)
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    /** 遗落之剑 (lost_sword_block)：力量 II 撬动 5 次得剑胚 */
    public static final DeferredBlock<LostSwordBlockBlock> LOST_SWORD_BLOCK =
            PDBlocks.BLOCKS.registerBlock("lost_sword_block", LostSwordBlockBlock::new,
                    BlockBehaviour.Properties.of()
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .sound(SoundType.STONE)
                            .strength(-1, 3600000)
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    /** 阴影火盆 (shadow_brazier)：影烛点燃触发敌潮，燃尽掉钥匙 */
    public static final DeferredBlock<ShadowBrazierBlock> SHADOW_BRAZIER =
            PDBlocks.BLOCKS.registerBlock("shadow_brazier", ShadowBrazierBlock::new, unbreakableStone());

    /** 阴影陷阱 (shadow_trap_0)：踩踏束缚 + 破坏刷暗影之手 */
    public static final DeferredBlock<ShadowTrap0Block> SHADOW_TRAP_0 =
            PDBlocks.BLOCKS.registerBlock("shadow_trap_0", ShadowTrap0Block::new,
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.GRAVEL)
                            .strength(1f)
                            .noCollission()
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));

    /** 暮影之笼 (twilight_lantern)：暮影事件 + 灯影世界返程 */
    public static final DeferredBlock<TwilightLanternBlock> TWILIGHT_LANTERN =
            PDBlocks.BLOCKS.registerBlock("twilight_lantern", TwilightLanternBlock::new,
                    BlockBehaviour.Properties.of()
                            .sound(SoundType.LANTERN)
                            .strength(-1, 3600000)
                            .lightLevel(s -> 8)
                            .noOcclusion()
                            .isRedstoneConductor((bs, br, bp) -> false));
}
