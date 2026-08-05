package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.blockentity.BlockEntityAPI;
import com.pasterdream.pasterdreammod.block.entity.PicnicBasketBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.ShadowDeskBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WindmoorCrateBlockEntity;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksStructure;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 容器/家具/杂项方块组方块实体注册（[分区F]，波次 W4）。
 * <p>
 * 与原版一一对应的 49 个 BlockEntityType：structure_block_0..23、
 * wind_knight_spawnblock、玻璃罐 ×3、容器 ×3、影之床 ×2 及杂项。
 * 数据型 BE 由 {@link W4DataBlockEntity}、GeckoLib 型由
 * {@link W4GeoDataBlockEntity} 统一承载，按类型区分注册名。
 */
public class PDBlockEntitiesFurniture {

    /** structure_block_0..23 方块实体类型（存 number 随机数） */
    public static final List<DeferredHolder<BlockEntityType<?>, BlockEntityType<W4DataBlockEntity>>> STRUCTURE_BLOCKS;

    /** wind_knight_spawnblock 方块实体类型（GeckoLib 渲染，样式由方块 STAGE 属性决定） */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> WIND_KNIGHT_SPAWNBLOCK;

    static {
        List<DeferredHolder<BlockEntityType<?>, BlockEntityType<W4DataBlockEntity>>> structures = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            final int index = i;
            structures.add(BlockEntityAPI.<W4DataBlockEntity>createBlockEntity("structure_block_" + i)
                    .factory((pos, state) -> new W4DataBlockEntity(
                            PDBlockEntitiesFurniture.STRUCTURE_BLOCKS.get(index).get(), pos, state))
                    .validBlock(PDBlocksStructure.STRUCTURE_BLOCKS.get(i))
                    .build());
        }
        STRUCTURE_BLOCKS = Collections.unmodifiableList(structures);

        WIND_KNIGHT_SPAWNBLOCK = BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("wind_knight_spawnblock")
                .factory((pos, state) -> new W4GeoDataBlockEntity(
                        PDBlockEntitiesFurniture.WIND_KNIGHT_SPAWNBLOCK.get(), pos, state))
                .validBlock(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK)
                .build();
    }

    // ==================== 玻璃罐 ====================

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> ECOLOGY_GLASS_JAR =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("ecology_glass_jar")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.ECOLOGY_GLASS_JAR.get(), pos, state))
                    .validBlock(PDBlocksFurniture.ECOLOGY_GLASS_JAR)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> FIREFLY_GLASS_JAR =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("firefly_glass_jar")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.FIREFLY_GLASS_JAR.get(), pos, state))
                    .validBlock(PDBlocksFurniture.FIREFLY_GLASS_JAR)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> LIGHT_FIREFLY_GLASS_JAR =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("light_firefly_glass_jar")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.LIGHT_FIREFLY_GLASS_JAR.get(), pos, state))
                    .validBlock(PDBlocksFurniture.LIGHT_FIREFLY_GLASS_JAR)
                    .build();

    // ==================== 容器 GUI ====================

    /** 野餐篮：15 格 + GeckoLib */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PicnicBasketBlockEntity>> PICNIC_BASKET =
            BlockEntityAPI.<PicnicBasketBlockEntity>createBlockEntity("picnic_basket")
                    .factory(PicnicBasketBlockEntity::new)
                    .validBlock(PDBlocksFurniture.PICNIC_BASKET)
                    .build();

    /** 影之桌：1 格展示 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShadowDeskBlockEntity>> SHADOW_DESK =
            BlockEntityAPI.<ShadowDeskBlockEntity>createBlockEntity("shadow_desk")
                    .factory(ShadowDeskBlockEntity::new)
                    .validBlock(PDBlocksFurniture.SHADOW_DESK)
                    .build();

    /** 风泊木箱：15 格 + new_loots 数据 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindmoorCrateBlockEntity>> WINDMOOR_CRATE =
            BlockEntityAPI.<WindmoorCrateBlockEntity>createBlockEntity("windmoor_crate")
                    .factory(WindmoorCrateBlockEntity::new)
                    .validBlock(PDBlocksFurniture.WINDMOOR_CRATE)
                    .build();

    // ==================== 影之床 ====================

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4DataBlockEntity>> SHADOW_BED =
            BlockEntityAPI.<W4DataBlockEntity>createBlockEntity("shadow_bed")
                    .factory((pos, state) -> new W4DataBlockEntity(
                            PDBlockEntitiesFurniture.SHADOW_BED.get(), pos, state))
                    .validBlock(PDBlocksFurniture.SHADOW_BED)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4DataBlockEntity>> TRUE_SHADOW_BED =
            BlockEntityAPI.<W4DataBlockEntity>createBlockEntity("true_shadow_bed")
                    .factory((pos, state) -> new W4DataBlockEntity(
                            PDBlockEntitiesFurniture.TRUE_SHADOW_BED.get(), pos, state))
                    .validBlock(PDBlocksFurniture.TRUE_SHADOW_BED)
                    .build();

    // ==================== 杂项 ====================

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> BIRDS_NEST =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("birds_nest")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.BIRDS_NEST.get(), pos, state))
                    .validBlock(PDBlocksFurniture.BIRDS_NEST)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> BROKEN_SHADOW_DUNGEON_PROTAL =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("broken_shadow_dungeon_protal")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.BROKEN_SHADOW_DUNGEON_PROTAL.get(), pos, state))
                    .validBlock(PDBlocksFurniture.BROKEN_SHADOW_DUNGEON_PROTAL)
                    .build();

    /** 完整的暗影地牢传送门方块实体 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> SHADOW_DUNGEON_PORTAL =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("shadow_dungeon_portal")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.SHADOW_DUNGEON_PORTAL.get(), pos, state))
                    .validBlock(PDBlocksFurniture.SHADOW_DUNGEON_PORTAL)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4DataBlockEntity>> CLAYPAN_1 =
            BlockEntityAPI.<W4DataBlockEntity>createBlockEntity("claypan_1")
                    .factory((pos, state) -> new W4DataBlockEntity(
                            PDBlockEntitiesFurniture.CLAYPAN_1.get(), pos, state))
                    .validBlock(PDBlocksFurniture.CLAYPAN_1)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> DESERT_HERO_TOMB =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("desert_hero_tomb")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.DESERT_HERO_TOMB.get(), pos, state))
                    .validBlock(PDBlocksFurniture.DESERT_HERO_TOMB)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4DataBlockEntity>> GUARD_BLOCK =
            BlockEntityAPI.<W4DataBlockEntity>createBlockEntity("guard_block")
                    .factory((pos, state) -> new W4DataBlockEntity(
                            PDBlockEntitiesFurniture.GUARD_BLOCK.get(), pos, state))
                    .validBlock(PDBlocksFurniture.GUARD_BLOCK)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4DataBlockEntity>> RESTRAINMOVE_BLOCK =
            BlockEntityAPI.<W4DataBlockEntity>createBlockEntity("restrainmove_block")
                    .factory((pos, state) -> new W4DataBlockEntity(
                            PDBlockEntitiesFurniture.RESTRAINMOVE_BLOCK.get(), pos, state))
                    .validBlock(PDBlocksFurniture.RESTRAINMOVE_BLOCK)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> GUARD_CRYSTAL =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("guard_crystal")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.GUARD_CRYSTAL.get(), pos, state))
                    .validBlock(PDBlocksFurniture.GUARD_CRYSTAL)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4DataBlockEntity>> LOST_SWORD_BLOCK =
            BlockEntityAPI.<W4DataBlockEntity>createBlockEntity("lost_sword_block")
                    .factory((pos, state) -> new W4DataBlockEntity(
                            PDBlockEntitiesFurniture.LOST_SWORD_BLOCK.get(), pos, state))
                    .validBlock(PDBlocksFurniture.LOST_SWORD_BLOCK)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> SHADOW_BRAZIER =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("shadow_brazier")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.SHADOW_BRAZIER.get(), pos, state))
                    .validBlock(PDBlocksFurniture.SHADOW_BRAZIER)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> SHADOW_TRAP_0 =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("shadow_trap_0")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.SHADOW_TRAP_0.get(), pos, state))
                    .validBlock(PDBlocksFurniture.SHADOW_TRAP_0)
                    .build();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<W4GeoDataBlockEntity>> TWILIGHT_LANTERN =
            BlockEntityAPI.<W4GeoDataBlockEntity>createBlockEntity("twilight_lantern")
                    .factory((pos, state) -> new W4GeoDataBlockEntity(
                            PDBlockEntitiesFurniture.TWILIGHT_LANTERN.get(), pos, state))
                    .validBlock(PDBlocksFurniture.TWILIGHT_LANTERN)
                    .build();
}
