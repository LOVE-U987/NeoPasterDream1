package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.blockentity.BlockEntityAPI;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandChestBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandSpawnBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.DreamAccumulatorBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.DreamCauldronBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.DyedreamDeskBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.LifeCrystalBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.MeltdreamChestBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.MeltdreamChestOpenBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.ShadowChestBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.ShadowVortexBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.TheEndlessBookOfDreamSeekersBlockEntity;
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
            BlockEntityAPI.<AaroncosHandSpawnBlockEntity>createBlockEntity("aaroncos_hand_spawn_block")
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
}
