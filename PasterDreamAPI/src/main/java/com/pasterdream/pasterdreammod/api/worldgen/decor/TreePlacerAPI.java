package com.pasterdream.pasterdreammod.api.worldgen.decor;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 树 Trunk / Foliage / Decorator Type 的 DeferredRegister 门面。
 * <p>
 * 具体 placer 实现与内容键仍属主模；本类只统一创建/挂总线，避免各模组复制三套 DR 样板。
 * {@link TreeRegistry} 管理的是 configured/placed feature 资源键，与本门面正交。
 */
public final class TreePlacerAPI {

    private TreePlacerAPI() {
    }

    public static DeferredRegister<TrunkPlacerType<?>> trunkPlacers(String modId) {
        return DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, modId);
    }

    public static DeferredRegister<FoliagePlacerType<?>> foliagePlacers(String modId) {
        return DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, modId);
    }

    public static DeferredRegister<TreeDecoratorType<?>> treeDecorators(String modId) {
        return DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, modId);
    }

    /** 一次挂接 trunk + foliage + decorator 三个 DR */
    public static void registerAll(
            IEventBus modEventBus,
            DeferredRegister<TrunkPlacerType<?>> trunks,
            DeferredRegister<FoliagePlacerType<?>> foliage,
            DeferredRegister<TreeDecoratorType<?>> decorators) {
        if (trunks != null) {
            trunks.register(modEventBus);
        }
        if (foliage != null) {
            foliage.register(modEventBus);
        }
        if (decorators != null) {
            decorators.register(modEventBus);
        }
    }
}
