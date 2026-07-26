package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.worldgen.treedecorator.BiomeDyedream0LeaveDecorator;
import com.pasterdream.pasterdreammod.worldgen.treedecorator.BiomeDyedream0TrunkDecorator;
import com.pasterdream.pasterdreammod.worldgen.treedecorator.BiomeDyedream1LeaveDecorator;
import com.pasterdream.pasterdreammod.worldgen.treedecorator.BiomeDyedream1TrunkDecorator;
import com.pasterdream.pasterdreammod.worldgen.treedecorator.BiomeDyedream2LeaveDecorator;
import com.pasterdream.pasterdreammod.worldgen.treedecorator.BiomeDyedream2TrunkDecorator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 树装饰器类型（TreeDecoratorType）注册类
 * <p>
 * 还原自原版 PasterDream 的 6 个染梦树装饰器（原版通过 @Mod.EventBusSubscriber +
 * RegisterEvent 在各装饰器类内部分散注册，此处统一收拢为 DeferredRegister 模式）。
 * <p>
 * 注册名必须与数据包 JSON 引用完全一致：
 * <ul>
 *   <li>{@code data/pasterdream/worldgen/configured_feature/biome_dyedream_0_tree.json}
 *       → biome_dyedream_0_tree_trunk_decorator / biome_dyedream_0_tree_leave_decorator</li>
 *   <li>{@code data/pasterdream/worldgen/configured_feature/biome_dyedream_1_tree.json}
 *       → biome_dyedream_1_tree_trunk_decorator / biome_dyedream_1_tree_leave_decorator</li>
 *   <li>{@code data/pasterdream/worldgen/configured_feature/biome_dyedream_2_tree.json}
 *       → biome_dyedream_2_tree_trunk_decorator / biome_dyedream_2_tree_leave_decorator</li>
 * </ul>
 * 缺失任意一项都会导致数据包 registry 加载失败，进而阻断世界创建（P0）。
 * <p>
 * 注意：需要在 {@code PasterDreamMod} 构造函数中注册到 MOD 事件总线：
 * <pre>{@code
 * PDTreeDecorators.TREE_DECORATOR_TYPES.register(modEventBus);
 * }</pre>
 */
public class PDTreeDecorators {

    /** 树装饰器类型注册器 */
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR_TYPES =
            DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, PasterDreamMod.MOD_ID);

    /** 染梦树 0 号树干装饰器类型（biome_dyedream_0_tree.json 引用） */
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<BiomeDyedream0TrunkDecorator>> BIOME_DYEDREAM_0_TREE_TRUNK_DECORATOR =
            TREE_DECORATOR_TYPES.register("biome_dyedream_0_tree_trunk_decorator",
                    () -> new TreeDecoratorType<>(BiomeDyedream0TrunkDecorator.CODEC));

    /** 染梦树 0 号树叶装饰器类型（biome_dyedream_0_tree.json 引用） */
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<BiomeDyedream0LeaveDecorator>> BIOME_DYEDREAM_0_TREE_LEAVE_DECORATOR =
            TREE_DECORATOR_TYPES.register("biome_dyedream_0_tree_leave_decorator",
                    () -> new TreeDecoratorType<>(BiomeDyedream0LeaveDecorator.CODEC));

    /** 染梦树 1 号树干装饰器类型（biome_dyedream_1_tree.json 引用） */
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<BiomeDyedream1TrunkDecorator>> BIOME_DYEDREAM_1_TREE_TRUNK_DECORATOR =
            TREE_DECORATOR_TYPES.register("biome_dyedream_1_tree_trunk_decorator",
                    () -> new TreeDecoratorType<>(BiomeDyedream1TrunkDecorator.CODEC));

    /** 染梦树 1 号树叶装饰器类型（biome_dyedream_1_tree.json 引用） */
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<BiomeDyedream1LeaveDecorator>> BIOME_DYEDREAM_1_TREE_LEAVE_DECORATOR =
            TREE_DECORATOR_TYPES.register("biome_dyedream_1_tree_leave_decorator",
                    () -> new TreeDecoratorType<>(BiomeDyedream1LeaveDecorator.CODEC));

    /** 染梦树 2 号树干装饰器类型（biome_dyedream_2_tree.json 引用） */
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<BiomeDyedream2TrunkDecorator>> BIOME_DYEDREAM_2_TREE_TRUNK_DECORATOR =
            TREE_DECORATOR_TYPES.register("biome_dyedream_2_tree_trunk_decorator",
                    () -> new TreeDecoratorType<>(BiomeDyedream2TrunkDecorator.CODEC));

    /** 染梦树 2 号树叶装饰器类型（biome_dyedream_2_tree.json 引用） */
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<BiomeDyedream2LeaveDecorator>> BIOME_DYEDREAM_2_TREE_LEAVE_DECORATOR =
            TREE_DECORATOR_TYPES.register("biome_dyedream_2_tree_leave_decorator",
                    () -> new TreeDecoratorType<>(BiomeDyedream2LeaveDecorator.CODEC));
}
