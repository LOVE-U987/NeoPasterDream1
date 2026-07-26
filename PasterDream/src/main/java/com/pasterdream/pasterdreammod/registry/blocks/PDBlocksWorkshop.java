package com.pasterdream.pasterdreammod.registry.blocks;

import com.pasterdream.pasterdreammod.block.WeaponTableBlock;
import com.pasterdream.pasterdreammod.block.WeaponWorkshopBlock;
import com.pasterdream.pasterdreammod.block.WorkshopAnvilBlock;
import com.pasterdream.pasterdreammod.block.WorkshopBlastBlock;
import com.pasterdream.pasterdreammod.block.WorkshopCauldeonBlock;
import com.pasterdream.pasterdreammod.block.WorkshopGrindBlock;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * 武器工坊群方块注册（[分区W]）。
 * <p>
 * 精铸工作台（激活核心）+ 精铸工坊（锻造核心）+ 四座卫星工位
 * （冷却盆/锻炉/铁砧/磨石）。方块属性逐一对照原版
 * {@code net.pasterdream.block.*}：
 * <ul>
 *   <li>精铸工作台：铁砧音效，2/10 强度；</li>
 *   <li>精铸工坊：滴水石音效，10 强度（掉落物走战利品表，返还结构材料）；</li>
 *   <li>四座卫星：泥砖音效，-1/3600000（不可采集，仅随核心销毁联动拆除）。</li>
 * </ul>
 *
 * @see PDBlocks
 */
public class PDBlocksWorkshop {

    // ==================== 激活核心 / 锻造核心 ====================

    /**
     * 精铸工作台 (weapon_table)
     * GeckoLib 3D 模型；手持蓝图右键校验 4 层 5×5 多方块结构并铺设精铸工坊群
     */
    public static final DeferredBlock<WeaponTableBlock> WEAPON_TABLE = PDBlocks.BLOCKS.register("weapon_table",
            () -> new WeaponTableBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.ANVIL)
                    .strength(2f, 10f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)));

    /**
     * 精铸工坊 (weapon_workshop)
     * GeckoLib 3D 模型（渲染整组工坊外观）；7 格锻造 GUI；破坏时连带拆除卫星工位
     */
    public static final DeferredBlock<WeaponWorkshopBlock> WEAPON_WORKSHOP = PDBlocks.BLOCKS.register("weapon_workshop",
            () -> new WeaponWorkshopBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.DRIPSTONE_BLOCK)
                    .strength(10f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)));

    // ==================== 卫星工位（不可采集，外观由核心渲染） ====================

    /**
     * 工坊冷却盆 (workshop_cauldeon)
     * 淬火工位：手持工序 2 原胚右键 → 随机强化 + 工序推进到 3
     */
    public static final DeferredBlock<WorkshopCauldeonBlock> WORKSHOP_CAULDEON = PDBlocks.BLOCKS.register("workshop_cauldeon",
            () -> new WorkshopCauldeonBlock(satelliteProperties()));

    /**
     * 工坊锻炉 (workshop_blast)
     * 煅烧工位：岩浆储罐 + 5 槽 GUI；每 10 tick 推进煅烧
     */
    public static final DeferredBlock<WorkshopBlastBlock> WORKSHOP_BLAST = PDBlocks.BLOCKS.register("workshop_blast",
            () -> new WorkshopBlastBlock(satelliteProperties()));

    /**
     * 工坊铁砧 (workshop_anvil)
     * 锤炼工位：数字小游戏 GUI；每 10 tick 推进结算
     */
    public static final DeferredBlock<WorkshopAnvilBlock> WORKSHOP_ANVIL = PDBlocks.BLOCKS.register("workshop_anvil",
            () -> new WorkshopAnvilBlock(satelliteProperties()));

    /**
     * 工坊磨石 (workshop_grind)
     * 打磨工位：手持工序 3 原胚右键消耗经验打磨，满 9 层出成品
     */
    public static final DeferredBlock<WorkshopGrindBlock> WORKSHOP_GRIND = PDBlocks.BLOCKS.register("workshop_grind",
            () -> new WorkshopGrindBlock(satelliteProperties()));

    /**
     * 卫星工位共用属性（原版四座完全一致：泥砖音效 + 不可破坏 + 无遮挡）
     *
     * @return 方块属性
     */
    private static BlockBehaviour.Properties satelliteProperties() {
        return BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.MUD_BRICKS)
                .strength(-1, 3600000)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false);
    }
}
