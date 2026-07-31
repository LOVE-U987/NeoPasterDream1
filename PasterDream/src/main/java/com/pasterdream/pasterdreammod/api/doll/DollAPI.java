package com.pasterdream.pasterdreammod.api.doll;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.DollBlock;
import com.pasterdream.pasterdreammod.block.entity.DollBlockEntity;
import com.pasterdream.pasterdreammod.item.DollDisplayItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 玩偶注册 API 门面（Facade）
 * <p>
 * 提供统一入口注册玩偶，并支持运行时根据方块实例反查配置。
 * 因玩偶渲染与方块实体强依赖主模块，故放在 {@code PasterDream} 主模块。
 */
public final class DollAPI {

    /**
     * 玩偶方块 DeferredRegister
     */
    public static final DeferredRegister.Blocks BLOCK_REGISTRY = DeferredRegister.createBlocks(PasterDreamMod.MOD_ID);

    /**
     * 玩偶物品 DeferredRegister
     */
    public static final DeferredRegister.Items ITEM_REGISTRY = DeferredRegister.createItems(PasterDreamMod.MOD_ID);

    private static final Map<String, DollResult> REGISTRATIONS = new HashMap<>();
    private static final IdentityHashMap<Block, DollConfig> CONFIG_CACHE = new IdentityHashMap<>();
    private static final IdentityHashMap<Block, DeferredHolder<BlockEntityType<?>, BlockEntityType<DollBlockEntity>>> BE_CACHE = new IdentityHashMap<>();

    private DollAPI() {
        throw new UnsupportedOperationException("DollAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 创建一个玩偶 Builder
     *
     * @param name 玩偶注册名
     * @return {@link DollBuilder}
     */
    public static DollBuilder create(String name) {
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
    public static DollResult registerDoll(String namespace, String name, String model, String texture, boolean canHoldItems, String holdingModel) {
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
    public static DollResult registerDollWithSkin(String namespace, String name, String skinTexture, boolean canHoldItems) {
        String holdingModel = canHoldItems ? "pasterdream:geo/block/eoul_doll_holding.geo.json" : null;
        return registerDoll(
                namespace,
                name,
                "pasterdream:geo/block/eoul_doll.geo.json",
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
    public static Optional<DeferredHolder<BlockEntityType<?>, BlockEntityType<DollBlockEntity>>> getBlockEntityHolder(Block block) {
        DeferredHolder<BlockEntityType<?>, BlockEntityType<DollBlockEntity>> cached = BE_CACHE.get(block);
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
    public static Optional<BlockEntityType<DollBlockEntity>> getBlockEntityType(Block block) {
        return getBlockEntityHolder(block).map(DeferredHolder::get);
    }

    /**
     * 获取所有已注册玩偶的方块 DeferredHolder
     *
     * @return 方块 DeferredHolder 列表
     */
    public static Collection<DeferredBlock<DollBlock>> getBlocks() {
        return REGISTRATIONS.values().stream().map(DollResult::block).toList();
    }

    /**
     * 获取所有已注册玩偶的物品 DeferredHolder
     *
     * @return 物品 DeferredHolder 列表
     */
    public static Collection<DeferredItem<DollDisplayItem>> getItems() {
        return REGISTRATIONS.values().stream().map(DollResult::item).toList();
    }
}
