package com.pasterdream.pasterdreammod.api.block.builder;

import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.registries.DeferredBlock;

import javax.annotation.Nullable;

/**
 * 建筑变体族的注册结果记录 —— 包含所有已注册的变体方块引用
 * <p>
 * 由 {@link VariantSetBuilder#build()} 返回，提供类型安全的访问方法。
 * 未启用的变体对应字段为 {@code null}。
 *
 * @param baseName      基础方块名称前缀
 * @param stairs        楼梯变体（未启用则为 null）
 * @param slab          台阶变体
 * @param wall          墙变体
 * @param fence         栅栏变体
 * @param fenceGate     栅栏门变体
 * @param door          门变体
 * @param trapdoor      活板门变体
 * @param pressurePlate 压力板变体
 * @param button        按钮变体
 */
public record VariantSetResult(
        String baseName,
        @Nullable DeferredBlock<StairBlock> stairs,
        @Nullable DeferredBlock<SlabBlock> slab,
        @Nullable DeferredBlock<WallBlock> wall,
        @Nullable DeferredBlock<FenceBlock> fence,
        @Nullable DeferredBlock<FenceGateBlock> fenceGate,
        @Nullable DeferredBlock<DoorBlock> door,
        @Nullable DeferredBlock<TrapDoorBlock> trapdoor,
        @Nullable DeferredBlock<PressurePlateBlock> pressurePlate,
        @Nullable DeferredBlock<ButtonBlock> button
) {

    /**
     * 检查楼梯变体是否已注册
     *
     * @return true 如果存在楼梯变体
     */
    public boolean hasStairs() {
        return stairs != null;
    }

    /**
     * 检查台阶变体是否已注册
     *
     * @return true 如果存在台阶变体
     */
    public boolean hasSlab() {
        return slab != null;
    }

    /**
     * 检查墙变体是否已注册
     *
     * @return true 如果存在墙变体
     */
    public boolean hasWall() {
        return wall != null;
    }

    /**
     * 检查栅栏变体是否已注册
     *
     * @return true 如果存在栅栏变体
     */
    public boolean hasFence() {
        return fence != null;
    }

    /**
     * 检查栅栏门变体是否已注册
     *
     * @return true 如果存在栅栏门变体
     */
    public boolean hasFenceGate() {
        return fenceGate != null;
    }

    /**
     * 检查门变体是否已注册
     *
     * @return true 如果存在门变体
     */
    public boolean hasDoor() {
        return door != null;
    }

    /**
     * 检查活板门变体是否已注册
     *
     * @return true 如果存在活板门变体
     */
    public boolean hasTrapdoor() {
        return trapdoor != null;
    }

    /**
     * 检查压力板变体是否已注册
     *
     * @return true 如果存在压力板变体
     */
    public boolean hasPressurePlate() {
        return pressurePlate != null;
    }

    /**
     * 检查按钮变体是否已注册
     *
     * @return true 如果存在按钮变体
     */
    public boolean hasButton() {
        return button != null;
    }
}