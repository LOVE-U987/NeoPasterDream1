package com.pasterdream.pasterdreammod.api.doll;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 玩偶实现注入 record（主模组侧安装，API 侧零实现依赖）。
 * <p>
 * {@code PasterDream} 主模组在构造阶段调用 {@link DollAPI#install(DollImplementations)}
 * 注入玩偶方块/物品/方块实体的具体实现工厂；本 record 的 {@code modId} 为注册命名空间的
 * 唯一真相源（决定方块/物品/战利品表的资源 ID 命名空间，必须为主模组 MOD_ID）。
 *
 * @param modId              注册命名空间（必须为主模组 MOD_ID "pasterdream"，API 不使用 PasterDreamAPI.MOD_ID）
 * @param blockFactory       方块工厂（主模组注入 DollBlock::new）
 * @param itemFactory        物品工厂（主模组注入 DollDisplayItem::new，入参为对应方块与物品属性）
 * @param blockEntityFactory 方块实体工厂（主模组注入 DollBlockEntity::new，供 BlockEntityType.Builder.of 使用；
 *                           非通配 BlockEntity 类型——方法引用协变返回兼容，避免 capture 逃逸破坏泛型推断）
 */
public record DollImplementations(
        String modId,
        Function<BlockBehaviour.Properties, ? extends Block> blockFactory,
        BiFunction<Block, Item.Properties, ? extends Item> itemFactory,
        BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntityFactory) {

    /**
     * 紧凑构造校验：modId 与三个工厂均不可为空。
     *
     * @throws IllegalArgumentException modId 为空或工厂为空时抛出
     */
    public DollImplementations {
        if (modId == null || modId.isBlank()) {
            throw new IllegalArgumentException("[DollImplementations] modId 不能为空");
        }
        java.util.Objects.requireNonNull(blockFactory, "[DollImplementations] blockFactory 不能为空");
        java.util.Objects.requireNonNull(itemFactory, "[DollImplementations] itemFactory 不能为空");
        java.util.Objects.requireNonNull(blockEntityFactory, "[DollImplementations] blockEntityFactory 不能为空");
    }
}
