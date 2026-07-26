package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.block.entity.SimpleMarkerBlockEntity;

import com.pasterdream.pasterdreammod.api.blockentity.BlockEntityAPI;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandChestBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandSpawnBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.DreamAccumulatorBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.DreamCauldronBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.DreamSpawner0BlockEntity;
import com.pasterdream.pasterdreammod.block.entity.ForcedTowerBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.ResearchTableBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.ShadowBlastFurnaceBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.DyedreamDeskBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.EoulDollBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.LoveUDollBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.GoldenFoxSculptureBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.LifeCrystalBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.MeltdreamChestBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.MeltdreamChestOpenBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.QymDoll0BlockEntity;
import com.pasterdream.pasterdreammod.block.entity.ShadowChestBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.ShadowVortexBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.TheEndlessBookOfDreamSeekersBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.UuzDoll0BlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WeaponTableBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WeaponWorkshopBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WorkshopAnvilBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WorkshopBlastBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WorkshopCauldeonBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WorkshopGrindBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 方块实体注册类
 * <p>
 * 使用 {@link BlockEntityAPI} 统一注册所有 BlockEntityType，避免维护独立的 DeferredRegister。
 */
public class PDBlockEntities {

    /**
     * 亚伦柯斯之手生成激活方块实体类型
     * 用于渲染 GeckoLib 3D 模型和动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AaroncosHandSpawnBlockEntity>> AARONCOS_HAND_SPAWN_BLOCK =
            BlockEntityAPI.<AaroncosHandSpawnBlockEntity>createBlockEntity("aaroncoshandspawnblock")
                    .factory(AaroncosHandSpawnBlockEntity::new)
                    .validBlock(PDBlocks.AARONCOSHANDSPAWNBLOCK)
                    .build();

    /**
     * 亚伦柯斯之触战利品箱方块实体类型
     * 用于渲染 GeckoLib 3D 模型和动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AaroncosHandChestBlockEntity>> AARONCOS_HAND_CHEST =
            BlockEntityAPI.<AaroncosHandChestBlockEntity>createBlockEntity("aaroncos_hand_chest")
                    .factory(AaroncosHandChestBlockEntity::new)
                    .validBlock(PDBlocks.AARONCOS_HAND_CHEST)
                    .build();

    /**
     * 暗影漩涡方块实体类型
     * 用于渲染 GeckoLib 3D 模型和动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShadowVortexBlockEntity>> SHADOW_VORTEX =
            BlockEntityAPI.<ShadowVortexBlockEntity>createBlockEntity("shadow_vortex")
                    .factory(ShadowVortexBlockEntity::new)
                    .validBlock(PDBlocks.SHADOW_VORTEX)
                    .build();

    /**
     * 蓄梦池方块实体类型
     * 用于渲染 GeckoLib 动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DreamAccumulatorBlockEntity>> DREAM_ACCUMULATOR =
            BlockEntityAPI.<DreamAccumulatorBlockEntity>createBlockEntity("dream_accumulator")
                    .factory(DreamAccumulatorBlockEntity::new)
                    .validBlock(PDBlocks.DREAM_ACCUMULATOR)
                    .build();

    /**
     * 生命水晶方块实体类型
     * 用于渲染 GeckoLib 浮动和旋转动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LifeCrystalBlockEntity>> LIFE_CRYSTAL =
            BlockEntityAPI.<LifeCrystalBlockEntity>createBlockEntity("life_crystal")
                    .factory(LifeCrystalBlockEntity::new)
                    .validBlock(PDBlocks.LIFE_CRYSTAL)
                    .build();

    /**
     * 娇小琴雨梦玩偶方块实体类型
     * 用于渲染 GeckoLib 3D 模型
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QymDoll0BlockEntity>> QIN_DOLL_0 =
            BlockEntityAPI.<QymDoll0BlockEntity>createBlockEntity("qin_doll_0")
                    .factory(QymDoll0BlockEntity::new)
                    .validBlock(PDBlocks.QIN_DOLL_0)
                    .build();

    /**
     * 娇小幼幼紫玩偶方块实体类型
     * 用于渲染 GeckoLib 3D 模型
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UuzDoll0BlockEntity>> LITTLE_PURPLE_DOLL_0 =
            BlockEntityAPI.<UuzDoll0BlockEntity>createBlockEntity("little_purple_doll_0")
                    .factory(UuzDoll0BlockEntity::new)
                    .validBlock(PDBlocks.LITTLE_PURPLE_DOLL_0)
                    .build();

    /**
     * 狐狸雕像方块实体类型
     * 用于渲染 GeckoLib 3D 模型
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GoldenFoxSculptureBlockEntity>> GOLDEN_FOX_SCULPTURE =
            BlockEntityAPI.<GoldenFoxSculptureBlockEntity>createBlockEntity("golden_fox_sculpture")
                    .factory(GoldenFoxSculptureBlockEntity::new)
                    .validBlock(PDBlocks.GOLDEN_FOX_SCULPTURE)
                    .build();

    /**
     * 琴雨梦纪念玩偶方块实体类型
     * 用于渲染 GeckoLib 3D 模型与抱物状态
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LoveUDollBlockEntity>> LOVE_U_DOLL =
            BlockEntityAPI.<LoveUDollBlockEntity>createBlockEntity("love_u_doll")
                    .factory(LoveUDollBlockEntity::new)
                    .validBlock(PDBlocks.LOVE_U_DOLL)
                    .build();

    /**
     * 幼幼紫纪念玩偶方块实体类型
     * 用于渲染 GeckoLib 3D 模型与抱物状态
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EoulDollBlockEntity>> EOUL_DOLL =
            BlockEntityAPI.<EoulDollBlockEntity>createBlockEntity("eoul_doll")
                    .factory(EoulDollBlockEntity::new)
                    .validBlock(PDBlocks.EOUL_DOLL)
                    .build();

    /**
     * 影之箱方块实体类型
     * 用于渲染 GeckoLib 开盖动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShadowChestBlockEntity>> SHADOW_CHEST =
            BlockEntityAPI.<ShadowChestBlockEntity>createBlockEntity("shadow_chest")
                    .factory(ShadowChestBlockEntity::new)
                    .validBlock(PDBlocks.SHADOW_CHEST)
                    .build();

    /**
     * 寻梦者的永恒书卷方块实体类型
     * 1 格库存，用于渲染 GeckoLib 动画和 GUI
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TheEndlessBookOfDreamSeekersBlockEntity>> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS =
            BlockEntityAPI.<TheEndlessBookOfDreamSeekersBlockEntity>createBlockEntity("the_endless_book_of_dream_seekers")
                    .factory(TheEndlessBookOfDreamSeekersBlockEntity::new)
                    .validBlock(PDBlocks.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS)
                    .build();

    /**
     * 染梦书桌方块实体类型
     * 1 格库存（最大堆叠 1），支持 GUI 菜单和物品展示
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DyedreamDeskBlockEntity>> DYEDREAM_DESK =
            BlockEntityAPI.<DyedreamDeskBlockEntity>createBlockEntity("dyedream_desk")
                    .factory(DyedreamDeskBlockEntity::new)
                    .validBlock(PDBlocks.DYEDREAM_DESK)
                    .build();

    /**
     * 融梦水晶箱方块实体类型（关闭状态）
     * 用于渲染 GeckoLib 开启动画，9 格库存
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MeltdreamChestBlockEntity>> MELTDREAM_CHEST =
            BlockEntityAPI.<MeltdreamChestBlockEntity>createBlockEntity("meltdream_chest")
                    .factory(MeltdreamChestBlockEntity::new)
                    .validBlock(PDBlocks.MELTDREAM_CHEST)
                    .build();

    /**
     * 融梦水晶箱方块实体类型（打开状态）
     * 9 格库存，支持 GUI，无动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MeltdreamChestOpenBlockEntity>> MELTDREAM_CHEST_OPEN =
            BlockEntityAPI.<MeltdreamChestOpenBlockEntity>createBlockEntity("meltdream_chest_open")
                    .factory(MeltdreamChestOpenBlockEntity::new)
                    .validBlock(PDBlocks.MELTDREAM_CHEST_OPEN)
                    .build();

    /**
     * 梦境炼药锅方块实体类型
     * GeckoLib 动画 + 4 格库存（3 输入 + 1 输出），支持 GUI 菜单
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DreamCauldronBlockEntity>> DREAM_CAULDRON =
            BlockEntityAPI.<DreamCauldronBlockEntity>createBlockEntity("dream_cauldron")
                    .factory(DreamCauldronBlockEntity::new)
                    .validBlock(PDBlocks.DREAM_CAULDRON)
                    .build();

    // ==================== [分区W] 武器工坊群 ====================

    /**
     * 精铸工作台方块实体类型
     * 仅承担 GeckoLib 3D 模型渲染
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WeaponTableBlockEntity>> WEAPON_TABLE =
            BlockEntityAPI.<WeaponTableBlockEntity>createBlockEntity("weapon_table")
                    .factory(WeaponTableBlockEntity::new)
                    .validBlock(PDBlocks.WEAPON_TABLE)
                    .build();

    /**
     * 精铸工坊方块实体类型
     * GeckoLib 3D 模型 + 7 格锻造库存与镶嵌结算，支持 GUI 菜单
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WeaponWorkshopBlockEntity>> WEAPON_WORKSHOP =
            BlockEntityAPI.<WeaponWorkshopBlockEntity>createBlockEntity("weapon_workshop")
                    .factory(WeaponWorkshopBlockEntity::new)
                    .validBlock(PDBlocks.WEAPON_WORKSHOP)
                    .build();

    /**
     * 工坊冷却盆方块实体类型
     * 淬火随机强化（右键交互驱动，无库存）
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorkshopCauldeonBlockEntity>> WORKSHOP_CAULDEON =
            BlockEntityAPI.<WorkshopCauldeonBlockEntity>createBlockEntity("workshop_cauldeon")
                    .factory(WorkshopCauldeonBlockEntity::new)
                    .validBlock(PDBlocks.WORKSHOP_CAULDEON)
                    .build();

    /**
     * 工坊锻炉方块实体类型
     * 5 格库存 + 4000mB 岩浆储罐，每 10 tick 推进煅烧，支持 GUI 菜单
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorkshopBlastBlockEntity>> WORKSHOP_BLAST =
            BlockEntityAPI.<WorkshopBlastBlockEntity>createBlockEntity("workshop_blast")
                    .factory(WorkshopBlastBlockEntity::new)
                    .validBlock(PDBlocks.WORKSHOP_BLAST)
                    .build();

    /**
     * 工坊铁砧方块实体类型
     * 2 格库存 + 数字小游戏状态，每 10 tick 推进结算，支持 GUI 菜单
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorkshopAnvilBlockEntity>> WORKSHOP_ANVIL =
            BlockEntityAPI.<WorkshopAnvilBlockEntity>createBlockEntity("workshop_anvil")
                    .factory(WorkshopAnvilBlockEntity::new)
                    .validBlock(PDBlocks.WORKSHOP_ANVIL)
                    .build();

    /**
     * 工坊磨石方块实体类型
     * 打磨随机强化（右键交互驱动，无库存）
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorkshopGrindBlockEntity>> WORKSHOP_GRIND =
            BlockEntityAPI.<WorkshopGrindBlockEntity>createBlockEntity("workshop_grind")
                    .factory(WorkshopGrindBlockEntity::new)
                    .validBlock(PDBlocks.WORKSHOP_GRIND)
                    .build();

    // ==================== [分区R] 研究台组 ====================

    /**
     * 研究台方块实体类型
     * GeckoLib 3D 模型 + 6 格研究库存（复制/研究按钮），支持 GUI 菜单
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResearchTableBlockEntity>> RESEARCH_TABLE =
            BlockEntityAPI.<ResearchTableBlockEntity>createBlockEntity("research_table")
                    .factory(ResearchTableBlockEntity::new)
                    .validBlock(PDBlocks.RESEARCH_TABLE)
                    .build();

    /**
     * 暗影高炉方块实体类型
     * GeckoLib 3D 模型 + 6 格冶炼库存与 9000mB 暗影液体储罐，
     * 双端 ticker 驱动 shadow_blasting 数据包配方冶炼，支持 GUI 菜单
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShadowBlastFurnaceBlockEntity>> SHADOW_BLAST_FURNACE =
            BlockEntityAPI.<ShadowBlastFurnaceBlockEntity>createBlockEntity("shadow_blast_furnace")
                    .factory(ShadowBlastFurnaceBlockEntity::new)
                    .validBlock(PDBlocks.SHADOW_BLAST_FURNACE)
                    .build();

    /**
     * 强征传送塔方块实体类型
     * GeckoLib 3D 模型 + 传送链接数据（coord_x/y/z + switch）
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ForcedTowerBlockEntity>> FORCED_TOWER =
            BlockEntityAPI.<ForcedTowerBlockEntity>createBlockEntity("forced_tower")
                    .factory(ForcedTowerBlockEntity::new)
                    .validBlock(PDBlocks.FORCED_TOWER)
                    .build();

    /**
     * 构梦刷怪笼方块实体类型
     * 1 格刷怪蛋库存 + 刷怪状态（first/player_range/number）
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DreamSpawner0BlockEntity>> DREAM_SPAWNER_0 =
            BlockEntityAPI.<DreamSpawner0BlockEntity>createBlockEntity("dream_spawner_0")
                    .factory(DreamSpawner0BlockEntity::new)
                    .validBlock(PDBlocks.DREAM_SPAWNER_0)
                    .build();

    // ==================== 行为缺口补齐：萤火虫巢 / 阴影蘑菇 ====================

    /**
     * 萤火虫巢方块实体（存 firefly_nest 日间蓄能标记）
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4DataBlockEntity>> FIREFLY_NEST =
            BlockEntityAPI.<W4DataBlockEntity>createBlockEntity("firefly_nest")
                    .factory((pos, state) -> new W4DataBlockEntity(PDBlockEntities.FIREFLY_NEST.get(), pos, state))
                    .validBlock(PDBlocks.FIREFLY_NEST)
                    .build();

    /**
     * 阴影蘑菇方块实体（存 number 菌树变体 1..8）
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4DataBlockEntity>> SHADOW_FUNGUS =
            BlockEntityAPI.<W4DataBlockEntity>createBlockEntity("shadow_fungus")
                    .factory((pos, state) -> new W4DataBlockEntity(PDBlockEntities.SHADOW_FUNGUS.get(), pos, state))
                    .validBlock(PDBlocks.SHADOW_FUNGUS)
                    .build();

    // ==================== 原版空壳 BE（闭包对齐） ====================

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleMarkerBlockEntity>> DYEDREAM_CRACK =
            BlockEntityAPI.<SimpleMarkerBlockEntity>createBlockEntity("dyedream_crack")
                    .factory((pos, state) -> new SimpleMarkerBlockEntity(PDBlockEntities.DYEDREAM_CRACK.get(), pos, state))
                    .validBlock(PDBlocks.DYEDREAM_CRACK)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleMarkerBlockEntity>> DYEDREAM_SAPLING =
            BlockEntityAPI.<SimpleMarkerBlockEntity>createBlockEntity("dyedream_sapling")
                    .factory((pos, state) -> new SimpleMarkerBlockEntity(PDBlockEntities.DYEDREAM_SAPLING.get(), pos, state))
                    .validBlock(PDBlocks.DYEDREAM_SAPLING)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleMarkerBlockEntity>> FLOWER_17 =
            BlockEntityAPI.<SimpleMarkerBlockEntity>createBlockEntity("flower_17")
                    .factory((pos, state) -> new SimpleMarkerBlockEntity(PDBlockEntities.FLOWER_17.get(), pos, state))
                    .validBlock(PDBlocks.FLOWER_17)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleMarkerBlockEntity>> GRASS_14 =
            BlockEntityAPI.<SimpleMarkerBlockEntity>createBlockEntity("grass_14")
                    .factory((pos, state) -> new SimpleMarkerBlockEntity(PDBlockEntities.GRASS_14.get(), pos, state))
                    .validBlock(PDBlocks.GRASS_14)
                    .build();
}
