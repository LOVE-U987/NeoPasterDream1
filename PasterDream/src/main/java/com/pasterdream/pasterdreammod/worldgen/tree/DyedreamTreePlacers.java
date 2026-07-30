package com.pasterdream.pasterdreammod.worldgen.tree;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.worldgen.decor.TreePlacerAPI;
import com.pasterdream.pasterdreammod.worldgen.tree.decorator.DyedreamFallenLeavesDecorator;
import com.pasterdream.pasterdreammod.worldgen.tree.decorator.DyedreamHangingVineDecorator;
import com.pasterdream.pasterdreammod.worldgen.tree.decorator.DyedreamRootDecorator;
import com.pasterdream.pasterdreammod.worldgen.tree.foliage.DyedreamCloudFoliagePlacer;
import com.pasterdream.pasterdreammod.worldgen.tree.foliage.DyedreamSpheroidFoliagePlacer;
import com.pasterdream.pasterdreammod.worldgen.tree.foliage.DyedreamWeepingFoliagePlacer;
import com.pasterdream.pasterdreammod.worldgen.tree.trunk.DyedreamBranchingTrunkPlacer;
import com.pasterdream.pasterdreammod.worldgen.tree.trunk.DyedreamColossalTrunkPlacer;
import com.pasterdream.pasterdreammod.worldgen.tree.trunk.DyedreamMegaTrunkPlacer;
import com.pasterdream.pasterdreammod.worldgen.tree.trunk.DyedreamStraightTrunkPlacer;
import com.pasterdream.pasterdreammod.worldgen.tree.trunk.DyedreamWorldTreeTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 染梦树 Placer 与 Decorator 注册中心
 * <p>
 * 集中注册自定义 TrunkPlacer / FoliagePlacer / TreeDecorator 类型，供 configured_feature JSON 引用。
 * DR 创建走 API {@link TreePlacerAPI}；具体 codec/实现仍为本内容。
 */
public class DyedreamTreePlacers {

    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = TreePlacerAPI.trunkPlacers(PasterDreamMod.MOD_ID);
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS = TreePlacerAPI.foliagePlacers(PasterDreamMod.MOD_ID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = TreePlacerAPI.treeDecorators(PasterDreamMod.MOD_ID);

    // TrunkPlacers
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<DyedreamStraightTrunkPlacer>> STRAIGHT_TRUNK_PLACER =
            TRUNK_PLACERS.register("dyedream_straight_trunk_placer", () -> new TrunkPlacerType<>(DyedreamStraightTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<DyedreamBranchingTrunkPlacer>> BRANCHING_TRUNK_PLACER =
            TRUNK_PLACERS.register("dyedream_branching_trunk_placer", () -> new TrunkPlacerType<>(DyedreamBranchingTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<DyedreamMegaTrunkPlacer>> MEGA_TRUNK_PLACER =
            TRUNK_PLACERS.register("dyedream_mega_trunk_placer", () -> new TrunkPlacerType<>(DyedreamMegaTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<DyedreamColossalTrunkPlacer>> COLOSSAL_TRUNK_PLACER =
            TRUNK_PLACERS.register("dyedream_colossal_trunk_placer", () -> new TrunkPlacerType<>(DyedreamColossalTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<DyedreamWorldTreeTrunkPlacer>> WORLD_TREE_TRUNK_PLACER =
            TRUNK_PLACERS.register("dyedream_world_tree_trunk_placer", () -> new TrunkPlacerType<>(DyedreamWorldTreeTrunkPlacer.CODEC));

    // FoliagePlacers
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<DyedreamCloudFoliagePlacer>> CLOUD_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("dyedream_cloud_foliage_placer", () -> new FoliagePlacerType<>(DyedreamCloudFoliagePlacer.CODEC));
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<DyedreamSpheroidFoliagePlacer>> SPHEROID_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("dyedream_spheroid_foliage_placer", () -> new FoliagePlacerType<>(DyedreamSpheroidFoliagePlacer.CODEC));
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<DyedreamWeepingFoliagePlacer>> WEEPING_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("dyedream_weeping_foliage_placer", () -> new FoliagePlacerType<>(DyedreamWeepingFoliagePlacer.CODEC));

    // TreeDecorators
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<DyedreamRootDecorator>> ROOT_DECORATOR =
            TREE_DECORATORS.register("dyedream_root_decorator", () -> new TreeDecoratorType<>(DyedreamRootDecorator.CODEC));
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<DyedreamHangingVineDecorator>> HANGING_VINE_DECORATOR =
            TREE_DECORATORS.register("dyedream_hanging_vine_decorator", () -> new TreeDecoratorType<>(DyedreamHangingVineDecorator.CODEC));
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<DyedreamFallenLeavesDecorator>> FALLEN_LEAVES_DECORATOR =
            TREE_DECORATORS.register("dyedream_fallen_leaves_decorator", () -> new TreeDecoratorType<>(DyedreamFallenLeavesDecorator.CODEC));
}
