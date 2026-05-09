package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.DreamAccumulatorBlock;
import com.pasterdream.pasterdreammod.block.DyedreamDeskBlock;
import com.pasterdream.pasterdreammod.block.LifeCrystalBlock;
import com.pasterdream.pasterdreammod.block.ShadowChestBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块注册类
 * 使用 DeferredRegister 模式注册所有方块
 */
public class PDBlocks {

    /**
     * 方块注册器
     */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PasterDreamMod.MOD_ID);

    /**
     * 蓄梦池方块 (dream_accumulator)
     * 核心功能方块，用于收集梦境能量
     * 原模组使用 TESR 特殊渲染，简化版使用普通方块 + 自定义模型
     */
    public static final DeferredBlock<DreamAccumulatorBlock> DREAM_ACCUMULATOR = BLOCKS.register("dream_accumulator",
            () -> new DreamAccumulatorBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.CALCITE)
                    .strength(1.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    /**
     * 染梦书桌方块 (dyedream_desk)
     * 方向性方块，玩家放置时根据朝向旋转
     * 原模组有 GUI 和 TileEntity，简化版暂不包含
     */
    public static final DeferredBlock<DyedreamDeskBlock> DYEDREAM_DESK = BLOCKS.register("dyedream_desk",
            () -> new DyedreamDeskBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOD)
                    .strength(1.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    /**
     * 生命水晶方块 (life_crystal)
     * 站在附近可以缓慢恢复生命值
     * 发光等级12，无TileEntity简化版
     */
    public static final DeferredBlock<LifeCrystalBlock> LIFE_CRYSTAL = BLOCKS.register("life_crystal",
            () -> new LifeCrystalBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(1.0f)
                    .lightLevel(state -> 12)
                    .noOcclusion()));

    /**
     * 影之箱子方块 (shadow_chest)
     * 装饰性方块，无存储功能（简化版）
     * 具有暗影主题的粒子效果
     */
    public static final DeferredBlock<ShadowChestBlock> SHADOW_CHEST = BLOCKS.register("shadow_chest",
            () -> new ShadowChestBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.DEEPSLATE_TILES)
                    .strength(1.0f, 0.5f)
                    .noOcclusion()));
}
