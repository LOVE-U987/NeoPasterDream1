package com.pasterdream.pasterdreammod.api.doll;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 玩偶注册 API 门面（Facade）
 * <p>
 * 提供统一入口注册玩偶，并支持运行时根据方块实例反查配置。
 * <p>
 * 本类位于 {@code PasterDreamAPI} 模块；玩偶的具体实现类（方块/物品/方块实体）由
 * {@code PasterDream} 主模组在启动时通过 {@link #install(DollImplementations)} 注入
 * （Provider 模式），API 层不依赖任何实现类。
 * <p>
 * 时序约定：注册相关入口必须在实现安装后调用——主模组在构造阶段最前自动完成安装；
 * KubeJS 事件链路径由 {@code DollJSBuilder} 构造器守卫兜底；第三方模组直接调用本 API
 * 必须声明对 PasterDream 的运行时依赖（保证主模组先构造，安装先于调用）。
 */
public final class DollAPI {

    /** 已安装的实现注入（null 表示未安装） */
    private static DollImplementations implementations;

    /** 玩偶方块 DeferredRegister（install 时按注入 modId 创建） */
    private static DeferredRegister.Blocks blockRegistry;

    /** 玩偶物品 DeferredRegister（install 时按注入 modId 创建） */
    private static DeferredRegister.Items itemRegistry;

    private static final Map<String, DollResult> REGISTRATIONS = new HashMap<>();

    private static final IdentityHashMap<Block, DollConfig> CONFIG_CACHE = new IdentityHashMap<>();

    private static final IdentityHashMap<Block, DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>>> BE_CACHE =
            new IdentityHashMap<>();

    /** 可掉落战利品池（懒解析 Supplier，避免注册阶段 item.get() 未就绪） */
    private static final LinkedHashSet<Supplier<? extends Item>> LOOT_ITEMS = new LinkedHashSet<>();

    private DollAPI() {
        throw new UnsupportedOperationException("DollAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 安装玩偶实现（由主模组调用，幂等）。
     * <p>
     * 首次调用生效；重复调用且参数一致（record equals）时为无操作（debug 日志）；
     * 重复调用且参数不一致时抛 {@link IllegalStateException}（禁止运行期替换实现）。
     * <p>
     * DeferredRegister 在本方法内按注入 modId 创建：注册命名空间必须为主模组 MOD_ID
     * （"pasterdream"），不得使用 PasterDreamAPI.MOD_ID（"pasterdreamapi"）。
     *
     * @param implementations 实现注入（含注册命名空间 modId，作为唯一真相源）
     */
    public static void install(DollImplementations implementations) {
        Objects.requireNonNull(implementations, "[DollAPI] implementations 不能为空");
        if (isInstalled()) {
            if (DollAPI.implementations.equals(implementations)) {
                PasterDreamAPI.LOGGER.debug("[DollAPI] 重复 install 且参数一致，跳过");
                return;
            }
            throw new IllegalStateException("[DollAPI] 玩偶实现已安装且参数不一致，禁止运行期替换实现");
        }
        DollAPI.implementations = implementations;
        blockRegistry = DeferredRegister.createBlocks(implementations.modId());
        itemRegistry = DeferredRegister.createItems(implementations.modId());
        PasterDreamAPI.LOGGER.info("[DollAPI] 玩偶实现已安装，注册命名空间: {}", implementations.modId());
    }

    /**
     * 查询玩偶实现是否已安装。
     *
     * @return true 表示已通过 {@link #install(DollImplementations)} 安装
     */
    public static boolean isInstalled() {
        return implementations != null;
    }

    /**
     * 获取已安装实现（未安装时抛出可行动的异常）。
     *
     * @return 实现注入 record
     * @throws IllegalStateException 未安装时抛出
     */
    static DollImplementations requireImpls() {
        if (implementations == null) {
            throw new IllegalStateException(
                    "[DollAPI] 玩偶实现未安装：PasterDream 主模组会在构造阶段自动调用 DollAPI.install(...)；"
                            + "第三方模组请声明对 PasterDream 的依赖后再使用本 API");
        }
        return implementations;
    }

    /**
     * 获取注册命名空间（未安装时抛出异常）。
     *
     * @return 主模组注册命名空间（由 DollImplementations.modId 注入）
     */
    static String modId() {
        return requireImpls().modId();
    }

    /**
     * 获取玩偶方块 DeferredRegister（未安装时抛出异常）。
     *
     * @return 方块 DeferredRegister
     */
    public static DeferredRegister.Blocks blocks() {
        requireImpls();
        return blockRegistry;
    }

    /**
     * 获取玩偶物品 DeferredRegister（未安装时抛出异常）。
     *
     * @return 物品 DeferredRegister
     */
    public static DeferredRegister.Items items() {
        requireImpls();
        return itemRegistry;
    }

    /**
     * 创建一个玩偶 Builder
     *
     * @param name 玩偶注册名
     * @return {@link DollBuilder}
     */
    public static DollBuilder create(String name) {
        requireImpls();
        return new DollBuilder(name);
    }

    /**
     * KubeJS 专用便捷注册方法
     * <p>
     * 绕过 DeferredRegister 与自定义事件，直接在启动脚本中调用即可注册玩偶。
     * 所有路径参数均为字符串，方法内部会解析为 {@link ResourceLocation}。
     *
     * @param namespace    注册命名空间（如 "kubejs"）
     * @param name         玩偶注册名
     * @param model        模型路径字符串（可传 null 使用默认路径）
     * @param texture      纹理路径字符串（可传 null 使用默认路径）
     * @param canHoldItems 是否允许抱物
     * @param holdingModel 抱物模型路径字符串（可传 null）
     * @return 注册结果 {@link DollResult}
     */
    public static DollResult registerDoll(String namespace, String name, String model, String texture,
                                          boolean canHoldItems, String holdingModel) {
        DollBuilder builder = create(name).namespace(namespace);
        if (model != null) {
            builder.model(ResourceLocation.parse(model));
        }
        if (texture != null) {
            builder.texture(ResourceLocation.parse(texture));
        }
        builder.canHoldItems(canHoldItems);
        if (holdingModel != null) {
            builder.holdingModel(ResourceLocation.parse(holdingModel));
        }
        return builder.registerDirect();
    }

    /**
     * 使用默认 eoul_doll 模型快速注册自定义皮肤玩偶
     * <p>
     * 只需提供命名空间、注册名和皮肤纹理路径，即可复用模组内置的玩偶骨骼与抱物模型。
     * 适合 KubeJS 玩家仅想替换皮肤、不想自己制作 GeckoLib 模型的场景。
     *
     * @param namespace    注册命名空间（如 "kubejs"）
     * @param name         玩偶注册名
     * @param skinTexture  皮肤纹理路径字符串（如 "pasterdream:textures/block/new_skin_1.png"）
     * @param canHoldItems 是否允许抱物
     * @return 注册结果 {@link DollResult}
     */
    public static DollResult registerDollWithSkin(String namespace, String name, String skinTexture,
                                                  boolean canHoldItems) {
        String holdingModel = canHoldItems ? modId() + ":geo/block/eoul_doll_holding.geo.json" : null;
        return registerDoll(
                namespace,
                name,
                modId() + ":geo/block/eoul_doll.geo.json",
                skinTexture,
                canHoldItems,
                holdingModel
        );
    }

    /**
     * 保存注册结果（由 {@link DollBuilder#register()} 调用）
     *
     * @param name   注册名
     * @param result 注册结果
     */
    static void putRegistration(String name, DollResult result) {
        REGISTRATIONS.put(name, result);
    }

    /**
     * 获取所有已注册玩偶
     *
     * @return 不可变的注册结果集合
     */
    public static Collection<DollResult> getRegistrations() {
        return Collections.unmodifiableCollection(REGISTRATIONS.values());
    }

    /**
     * 根据注册名查询玩偶
     *
     * @param name 注册名
     * @return {@link DollResult} 的 Optional
     */
    public static Optional<DollResult> getRegistration(String name) {
        return Optional.ofNullable(REGISTRATIONS.get(name));
    }

    /**
     * 根据方块实例查询玩偶配置
     *
     * @param block 方块实例
     * @return {@link DollConfig} 的 Optional
     */
    public static Optional<DollConfig> getConfig(Block block) {
        DollConfig cached = CONFIG_CACHE.get(block);
        if (cached != null) {
            return Optional.of(cached);
        }
        for (DollResult result : REGISTRATIONS.values()) {
            if (result.block().get() == block) {
                CONFIG_CACHE.put(block, result.config());
                return Optional.of(result.config());
            }
        }
        return Optional.empty();
    }

    /**
     * 根据方块实例查询对应的方块实体类型 DeferredHolder
     *
     * @param block 方块实例
     * @return 方块实体类型 DeferredHolder 的 Optional
     */
    public static Optional<DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>>> getBlockEntityHolder(Block block) {
        DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> cached = BE_CACHE.get(block);
        if (cached != null) {
            return Optional.of(cached);
        }
        for (DollResult result : REGISTRATIONS.values()) {
            if (result.block().get() == block) {
                BE_CACHE.put(block, result.blockEntityType());
                return Optional.of(result.blockEntityType());
            }
        }
        return Optional.empty();
    }

    /**
     * 根据方块实例查询对应的方块实体类型
     *
     * @param block 方块实例
     * @return 方块实体类型的 Optional
     */
    public static Optional<BlockEntityType<?>> getBlockEntityType(Block block) {
        return getBlockEntityHolder(block).map(h -> (BlockEntityType<?>) h.get());
    }

    /**
     * 将玩偶物品登记进"可掉落战利品池"。
     * <p>支持 DeferredItem/Supplier：注册阶段仅保存 supplier，开箱时懒解析。</p>
     *
     * @param supplier 玩偶物品供应器
     */
    public static void registerLootItem(Supplier<? extends Item> supplier) {
        if (supplier != null) {
            LOOT_ITEMS.add(supplier);
        }
    }

    /**
     * 获取可掉落玩偶物品列表（懒解析，过滤未注册/空气/重复项）。
     *
     * @return 玩偶物品列表
     */
    public static List<Item> getLootItems() {
        List<Item> items = new ArrayList<>();
        Set<Item> seen = new HashSet<>();
        for (Supplier<? extends Item> supplier : LOOT_ITEMS) {
            Item item;
            try {
                item = supplier.get();
            } catch (Exception e) {
                PasterDreamAPI.LOGGER.warn("[DollAPI] 玩偶战利品物品解析失败，跳过：{}", e.toString());
                continue;
            }
            if (item == null || item == Items.AIR || !seen.add(item)) {
                continue;
            }
            items.add(item);
        }
        return items;
    }

    /**
     * 获取所有已注册玩偶的方块 DeferredHolder
     *
     * @return 方块 DeferredHolder 列表
     */
    public static Collection<DeferredBlock<? extends Block>> getBlocks() {
        return REGISTRATIONS.values().stream().map(DollResult::block).toList();
    }

    /**
     * 获取所有已注册玩偶的物品 DeferredHolder
     *
     * @return 物品 DeferredHolder 列表
     */
    public static Collection<DeferredItem<? extends Item>> getItems() {
        return REGISTRATIONS.values().stream().map(DollResult::item).toList();
    }
}
