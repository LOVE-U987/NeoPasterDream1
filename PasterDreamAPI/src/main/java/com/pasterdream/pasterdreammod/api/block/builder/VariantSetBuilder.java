package com.pasterdream.pasterdreammod.api.block.builder;

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * 建筑变体族构建器（模式二）—— 基于基础方块一键生成全套建筑变体
 * <p>
 * 解决 {@code PDBlocks.java} 中楼梯、台阶、墙、栅栏等变体需要逐一手动注册的问题。
 * 只需指定基础方块名称和引用，通过链式调用选择所需变体类型，
 * 即可自动注册所有变体。
 * <p>
 * 使用示例：
 * <pre>{@code
 * VariantSetResult result = BlockAPI.createVariantSet("dyedream_planks", Blocks.OAK_PLANKS)
 *     .withStairs()
 *     .withSlab()
 *     .withFence()
 *     .withFenceGate(WoodType.OAK)
 *     .withDoor(BlockSetType.OAK)
 *     .withTrapdoor(BlockSetType.OAK)
 *     .build();
 * }</pre>
 */
public class VariantSetBuilder {

    private final DeferredRegister.Blocks registry;
    private final String baseName;
    private final Supplier<? extends Block> baseBlock;

    private boolean hasStairs;
    private boolean hasSlab;
    private boolean hasWall;
    private boolean hasFence;
    private boolean hasFenceGate;
    private boolean hasDoor;
    private boolean hasTrapdoor;
    private boolean hasPressurePlate;
    private boolean hasButton;

    @Nullable
    private String mineable;

    @Nullable
    private Block stairsReference;

    @Nullable
    private Block slabReference;

    @Nullable
    private Block wallReference;

    @Nullable
    private Block fenceReference;

    @Nullable
    private Block fenceGateReference;

    @Nullable
    private Block doorReference;

    @Nullable
    private Block trapdoorReference;

    @Nullable
    private Block pressurePlateReference;

    @Nullable
    private Block buttonReference;

    @Nullable
    private WoodType fenceGateWoodType;

    @Nullable
    private BlockSetType doorBlockSetType;

    @Nullable
    private BlockSetType trapdoorBlockSetType;

    @Nullable
    private BlockSetType pressurePlateBlockSetType;

    @Nullable
    private BlockSetType buttonBlockSetType;

    private int buttonTickDelay = 30;

    /**
     * 构造建筑变体族构建器
     *
     * @param registry  方块注册器
     * @param baseName  基础方块名称前缀
     * @param baseBlock 基础方块引用
     */
    public VariantSetBuilder(DeferredRegister.Blocks registry, String baseName,
                             Supplier<? extends Block> baseBlock) {
        this.registry = registry;
        this.baseName = baseName;
        this.baseBlock = baseBlock;
    }

    // ======================== 变体启用方法 ========================

    /**
     * 启用楼梯变体
     * <p>
     * 注册 {@code {baseName}_stairs}，使用原版对应类型的楼梯属性作为参考。
     *
     * @return 当前构建器实例
     */
    public VariantSetBuilder withStairs() {
        this.hasStairs = true;
        return this;
    }

    /**
     * 启用楼梯变体，并指定属性参考方块
     *
     * @param reference 属性参考方块（如 {@code Blocks.STONE_STAIRS}）
     * @return 当前构建器实例
     */
    public VariantSetBuilder withStairs(Block reference) {
        this.hasStairs = true;
        this.stairsReference = reference;
        return this;
    }

    /**
     * 启用台阶变体
     *
     * @return 当前构建器实例
     */
    public VariantSetBuilder withSlab() {
        this.hasSlab = true;
        return this;
    }

    /**
     * 启用台阶变体，并指定属性参考方块
     *
     * @param reference 属性参考方块
     * @return 当前构建器实例
     */
    public VariantSetBuilder withSlab(Block reference) {
        this.hasSlab = true;
        this.slabReference = reference;
        return this;
    }

    /**
     * 启用墙变体
     *
     * @return 当前构建器实例
     */
    public VariantSetBuilder withWall() {
        this.hasWall = true;
        return this;
    }

    /**
     * 启用墙变体，并指定属性参考方块
     *
     * @param reference 属性参考方块
     * @return 当前构建器实例
     */
    public VariantSetBuilder withWall(Block reference) {
        this.hasWall = true;
        this.wallReference = reference;
        return this;
    }

    /**
     * 启用栅栏变体
     *
     * @return 当前构建器实例
     */
    public VariantSetBuilder withFence() {
        this.hasFence = true;
        return this;
    }

    /**
     * 启用栅栏变体，并指定属性参考方块
     *
     * @param reference 属性参考方块
     * @return 当前构建器实例
     */
    public VariantSetBuilder withFence(Block reference) {
        this.hasFence = true;
        this.fenceReference = reference;
        return this;
    }

    /**
     * 启用栅栏门变体
     *
     * @param woodType 木材类型（决定材质和声音）
     * @return 当前构建器实例
     */
    public VariantSetBuilder withFenceGate(WoodType woodType) {
        this.hasFenceGate = true;
        this.fenceGateWoodType = woodType;
        return this;
    }

    /**
     * 启用栅栏门变体，并指定属性参考方块
     *
     * @param woodType  木材类型
     * @param reference 属性参考方块
     * @return 当前构建器实例
     */
    public VariantSetBuilder withFenceGate(WoodType woodType, Block reference) {
        this.hasFenceGate = true;
        this.fenceGateWoodType = woodType;
        this.fenceGateReference = reference;
        return this;
    }

    /**
     * 启用手门变体
     *
     * @param blockSetType 方块类型（决定材质和声音）
     * @return 当前构建器实例
     */
    public VariantSetBuilder withDoor(BlockSetType blockSetType) {
        this.hasDoor = true;
        this.doorBlockSetType = blockSetType;
        return this;
    }

    /**
     * 启用手门变体，并指定属性参考方块
     *
     * @param blockSetType 方块类型
     * @param reference    属性参考方块
     * @return 当前构建器实例
     */
    public VariantSetBuilder withDoor(BlockSetType blockSetType, Block reference) {
        this.hasDoor = true;
        this.doorBlockSetType = blockSetType;
        this.doorReference = reference;
        return this;
    }

    /**
     * 启用手活板门变体
     *
     * @param blockSetType 方块类型
     * @return 当前构建器实例
     */
    public VariantSetBuilder withTrapdoor(BlockSetType blockSetType) {
        this.hasTrapdoor = true;
        this.trapdoorBlockSetType = blockSetType;
        return this;
    }

    /**
     * 启用手活板门变体，并指定属性参考方块
     *
     * @param blockSetType 方块类型
     * @param reference    属性参考方块
     * @return 当前构建器实例
     */
    public VariantSetBuilder withTrapdoor(BlockSetType blockSetType, Block reference) {
        this.hasTrapdoor = true;
        this.trapdoorBlockSetType = blockSetType;
        this.trapdoorReference = reference;
        return this;
    }

    /**
     * 启用手压力板变体
     *
     * @param blockSetType 方块类型
     * @return 当前构建器实例
     */
    public VariantSetBuilder withPressurePlate(BlockSetType blockSetType) {
        this.hasPressurePlate = true;
        this.pressurePlateBlockSetType = blockSetType;
        return this;
    }

    /**
     * 启用手压力板变体，并指定属性参考方块
     *
     * @param blockSetType 方块类型
     * @param reference    属性参考方块
     * @return 当前构建器实例
     */
    public VariantSetBuilder withPressurePlate(BlockSetType blockSetType, Block reference) {
        this.hasPressurePlate = true;
        this.pressurePlateBlockSetType = blockSetType;
        this.pressurePlateReference = reference;
        return this;
    }

    /**
     * 启用手按钮变体
     *
     * @param blockSetType 方块类型
     * @param tickDelay    按钮持续时长（tick）
     * @return 当前构建器实例
     */
    public VariantSetBuilder withButton(BlockSetType blockSetType, int tickDelay) {
        this.hasButton = true;
        this.buttonBlockSetType = blockSetType;
        this.buttonTickDelay = tickDelay;
        return this;
    }

    /**
     * 启用手按钮变体，并指定属性参考方块
     *
     * @param blockSetType 方块类型
     * @param tickDelay    按钮持续时长
     * @param reference    属性参考方块
     * @return 当前构建器实例
     */
    public VariantSetBuilder withButton(BlockSetType blockSetType, int tickDelay, Block reference) {
        this.hasButton = true;
        this.buttonBlockSetType = blockSetType;
        this.buttonTickDelay = tickDelay;
        this.buttonReference = reference;
        return this;
    }

    /**
     * 设置所有变体方块所需的工具类型
     * <p>
     * 设置后，每个变体方块会自动注册到对应的 {@code mineable/*} 标签中，
     * 确保 Jade 模组显示正确的挖掘工具，且方块掉落时验证工具是否正确。
     *
     * @param mineable 工具类型，如 {@code "pickaxe"}、{@code "axe"}、{@code "shovel"}、{@code "hoe"}
     * @return 当前构建器实例
     */
    public VariantSetBuilder mineable(String mineable) {
        this.mineable = mineable;
        return this;
    }

    // ======================== 构建 & 注册 ========================

    /**
     * 执行注册，按已启用的变体类型依次注册所有方块
     * <p>
     * 如果未启用任何变体，抛出 {@link IllegalStateException}。
     *
     * @return {@link VariantSetResult} 包含所有已注册的变体方块引用
     */
    public VariantSetResult build() {
        DeferredBlock<StairBlock> stairs = null;
        DeferredBlock<SlabBlock> slab = null;
        DeferredBlock<WallBlock> wall = null;
        DeferredBlock<FenceBlock> fence = null;
        DeferredBlock<FenceGateBlock> fenceGate = null;
        DeferredBlock<DoorBlock> door = null;
        DeferredBlock<TrapDoorBlock> trapdoor = null;
        DeferredBlock<PressurePlateBlock> pressurePlate = null;
        DeferredBlock<ButtonBlock> button = null;

        if (hasStairs) {
            Block ref = stairsReference != null ? stairsReference : net.minecraft.world.level.block.Blocks.OAK_STAIRS;
            stairs = registry.registerBlock(baseName + "_stairs",
                    p -> new StairBlock(baseBlock.get().defaultBlockState(), p),
                    BlockBehaviour.Properties.ofFullCopy(ref));
        }

        if (hasSlab) {
            Block ref = slabReference != null ? slabReference : net.minecraft.world.level.block.Blocks.OAK_SLAB;
            slab = registry.registerBlock(baseName + "_slab",
                    SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(ref));
        }

        if (hasWall) {
            Block ref = wallReference != null ? wallReference : net.minecraft.world.level.block.Blocks.COBBLESTONE_WALL;
            wall = registry.registerBlock(baseName + "_wall",
                    WallBlock::new, BlockBehaviour.Properties.ofFullCopy(ref));
        }

        if (hasFence) {
            Block ref = fenceReference != null ? fenceReference : net.minecraft.world.level.block.Blocks.OAK_FENCE;
            fence = registry.registerBlock(baseName + "_fence",
                    FenceBlock::new, BlockBehaviour.Properties.ofFullCopy(ref));
        }

        if (hasFenceGate) {
            WoodType wt = fenceGateWoodType != null ? fenceGateWoodType : WoodType.OAK;
            Block ref = fenceGateReference != null ? fenceGateReference : net.minecraft.world.level.block.Blocks.OAK_FENCE_GATE;
            fenceGate = registry.registerBlock(baseName + "_fencegate",
                    p -> new FenceGateBlock(wt, p),
                    BlockBehaviour.Properties.ofFullCopy(ref));
        }

        if (hasDoor) {
            BlockSetType bst = doorBlockSetType != null ? doorBlockSetType : BlockSetType.OAK;
            Block ref = doorReference != null ? doorReference : net.minecraft.world.level.block.Blocks.OAK_DOOR;
            door = registry.registerBlock(baseName + "_door",
                    p -> new DoorBlock(bst, p),
                    BlockBehaviour.Properties.ofFullCopy(ref));
        }

        if (hasTrapdoor) {
            BlockSetType bst = trapdoorBlockSetType != null ? trapdoorBlockSetType : BlockSetType.OAK;
            Block ref = trapdoorReference != null ? trapdoorReference : net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR;
            trapdoor = registry.registerBlock(baseName + "_trapdoor",
                    p -> new TrapDoorBlock(bst, p),
                    BlockBehaviour.Properties.ofFullCopy(ref));
        }

        if (hasPressurePlate) {
            BlockSetType bst = pressurePlateBlockSetType != null ? pressurePlateBlockSetType : BlockSetType.OAK;
            Block ref = pressurePlateReference != null ? pressurePlateReference : net.minecraft.world.level.block.Blocks.OAK_PRESSURE_PLATE;
            pressurePlate = registry.registerBlock(baseName + "_pressure_plate",
                    p -> new PressurePlateBlock(bst, p),
                    BlockBehaviour.Properties.ofFullCopy(ref));
        }

        if (hasButton) {
            BlockSetType bst = buttonBlockSetType != null ? buttonBlockSetType : BlockSetType.OAK;
            Block ref = buttonReference != null ? buttonReference : net.minecraft.world.level.block.Blocks.OAK_BUTTON;
            button = registry.registerBlock(baseName + "_button",
                    p -> new ButtonBlock(bst, buttonTickDelay, p),
                    BlockBehaviour.Properties.ofFullCopy(ref));
        }

        if (stairs == null && slab == null && wall == null && fence == null
                && fenceGate == null && door == null && trapdoor == null
                && pressurePlate == null && button == null) {
            throw new IllegalStateException(
                    "VariantSetBuilder: 至少需要启用一个变体（如 .withStairs()）");
        }

        // 自动注册挖掘标签配置（如已设置 mineable 类型）
        if (mineable != null) {
            BlockConfig config = BlockConfig.of().mineable(mineable);
            VariantSetResult result = new VariantSetResult(
                    baseName, stairs, slab, wall, fence, fenceGate,
                    door, trapdoor, pressurePlate, button);

            registerVariantConfig(stairs, "_stairs", config);
            registerVariantConfig(slab, "_slab", config);
            registerVariantConfig(wall, "_wall", config);
            registerVariantConfig(fence, "_fence", config);
            registerVariantConfig(fenceGate, "_fencegate", config);
            registerVariantConfig(door, "_door", config);
            registerVariantConfig(trapdoor, "_trapdoor", config);
            registerVariantConfig(pressurePlate, "_pressure_plate", config);
            registerVariantConfig(button, "_button", config);

            return result;
        }

        return new VariantSetResult(
                baseName, stairs, slab, wall, fence, fenceGate,
                door, trapdoor, pressurePlate, button);
    }

    private void registerVariantConfig(DeferredBlock<?> deferred, String suffix, BlockConfig config) {
        if (deferred != null) {
            BlockAPI.putConfig(baseName + suffix, config);
        }
    }
}