package com.pasterdream.pasterdreammod.api.doll;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.blockentity.BlockEntityAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

import static com.pasterdream.pasterdreammod.api.PasterDreamAPI.LOGGER;

/**
 * 玩偶注册 Builder
 * <p>
 * 采用流式 API，支持设置模型、纹理、抱物开关等属性，最后调用
 * {@link #register()} 完成注册。
 * <p>
 * 本类位于 {@code PasterDreamAPI} 模块；方块/物品/方块实体的具体实现类由
 * {@code PasterDream} 主模组经 {@link DollImplementations} 注入，本类不引用任何实现类。
 */
public class DollBuilder {

    private final String name;

    private String namespace;

    private ResourceLocation model;

    private ResourceLocation texture;

    private ResourceLocation holdingModel;

    private boolean canHoldItems = false;

    private DollModelType modelType = DollModelType.NEW;

    private Item.Properties itemProperties;

    private BlockBehaviour.Properties blockProperties;

    /**
     * 构造玩偶 Builder
     *
     * @param name 玩偶注册名（snake_case）
     */
    DollBuilder(String name) {
        this.name = name;
        // 默认命名空间 = 注入的主模组 modId（DollAPI.create 已保证实现已安装）
        this.namespace = DollAPI.modId();
    }

    /**
     * 设置注册命名空间
     *
     * @param namespace 命名空间（如 "kubejs" 或主模组命名空间）
     * @return 当前 Builder
     */
    public DollBuilder namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    /**
     * 设置基础模型路径
     *
     * @param model 模型 ResourceLocation
     * @return 当前 Builder
     */
    public DollBuilder model(ResourceLocation model) {
        this.model = model;
        return this;
    }

    /**
     * 设置皮肤纹理路径
     *
     * @param texture 纹理 ResourceLocation
     * @return 当前 Builder
     */
    public DollBuilder texture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    /**
     * 设置是否允许抱物
     *
     * @param canHoldItems true 表示开启抱物功能
     * @return 当前 Builder
     */
    public DollBuilder canHoldItems(boolean canHoldItems) {
        this.canHoldItems = canHoldItems;
        return this;
    }

    /**
     * 设置抱物模型路径
     *
     * @param holdingModel 抱物模型 ResourceLocation
     * @return 当前 Builder
     */
    public DollBuilder holdingModel(ResourceLocation holdingModel) {
        this.holdingModel = holdingModel;
        return this;
    }

    /**
     * 设置玩偶模型类型
     *
     * @param modelType 模型类型
     * @return 当前 Builder
     */
    public DollBuilder modelType(DollModelType modelType) {
        this.modelType = modelType;
        return this;
    }

    /**
     * 使用旧模型（MemorialDollBlock 约定），等价于 {@code modelType(DollModelType.LEGACY)}。
     *
     * @return 当前 Builder
     */
    public DollBuilder legacy() {
        this.modelType = DollModelType.LEGACY;
        return this;
    }

    /**
     * 设置物品属性
     *
     * @param itemProperties 物品属性
     * @return 当前 Builder
     */
    public DollBuilder itemProperties(Item.Properties itemProperties) {
        this.itemProperties = itemProperties;
        return this;
    }

    /**
     * 设置方块属性
     *
     * @param blockProperties 方块属性
     * @return 当前 Builder
     */
    public DollBuilder blockProperties(BlockBehaviour.Properties blockProperties) {
        this.blockProperties = blockProperties;
        return this;
    }

    /**
     * 执行注册，将玩偶加入对应的 DeferredRegister。
     * <p>
     * 适用于在模组构造阶段（DeferredRegister 仍可接受条目时）调用。
     *
     * @return 注册结果 {@link DollResult}
     */
    public DollResult register() {
        Objects.requireNonNull(name, "[DollBuilder] name 不能为空");

        ResourceLocation modelLoc = this.model != null ? this.model : defaultModel(name);
        ResourceLocation textureLoc = this.texture != null ? this.texture : defaultTexture(name);
        ResourceLocation holdingLoc = this.holdingModel != null ? this.holdingModel : defaultHoldingModel(name);

        if (!canHoldItems) {
            holdingLoc = modelLoc;
        }

        BlockBehaviour.Properties blockProps = this.blockProperties != null
                ? this.blockProperties
                : defaultBlockProperties();
        Item.Properties itemProps = this.itemProperties != null
                ? this.itemProperties
                : new Item.Properties();

        DollImplementations impls = DollAPI.requireImpls();

        DeferredBlock<? extends Block> block = DollAPI.blocks().register(name,
                () -> impls.blockFactory().apply(blockProps));
        DeferredItem<? extends Item> item = DollAPI.items().register(name,
                () -> impls.itemFactory().apply(block.get(), itemProps));
        // 显式局部类型：避免 lambda + 目标通配的嵌套泛型推断失败（capture 逃逸）
        Supplier<BlockEntityType<BlockEntity>> beTypeSupplier =
                () -> BlockEntityType.Builder.of(impls.blockEntityFactory(), block.get()).build(null);
        DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntity>> blockEntityType =
                BlockEntityAPI.register(name, beTypeSupplier);

        DollConfig config = new DollConfig(name, modelLoc, textureLoc, holdingLoc, canHoldItems, modelType);
        DollResult result = new DollResult(name, block, item, blockEntityType, config);
        DollAPI.putRegistration(name, result);
        DollAPI.registerLootItem(item);
        return result;
    }

    /**
     * 使用 {@link Registry#register} 直接注册方块/物品/方块实体。
     * <p>
     * 适用于在 KubeJS 启动脚本等已过注册阶段的场景调用。
     * 与 {@link #register()} 不同，此方法不依赖 DeferredRegister，
     * 而是通过 {@link BuiltInRegistries} 直接写入，确保注册始终生效。
     * <p>
     * <b>时序限制（重要）</b>：NeoForge 在注册阶段结束后会锁定/冻结内置注册表，
     * 此后任何 {@code Registry.register} 写入都会抛出 {@link IllegalStateException}。
     * 因此 {@code registerDirect} 仅可在注册表尚未冻结的时机调用（例如 KubeJS
     * 启动脚本、{@code FMLCommonSetupEvent} 之前）。若在游戏运行中（服务器已启动）
     * 调用将失败并抛出带此说明的异常，请改用 {@link #register()}（DeferredRegister 路径，
     * 需在模组构造阶段调用）。
     *
     * @return 注册结果 {@link DollResult}
     */
    public DollResult registerDirect() {
        Objects.requireNonNull(name, "[DollBuilder] name 不能为空");
        Objects.requireNonNull(namespace, "[DollBuilder] namespace 不能为空");

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, name);
        ResourceLocation modelLoc = this.model != null ? this.model : defaultModel(name);
        ResourceLocation textureLoc = this.texture != null ? this.texture : defaultTexture(name);
        ResourceLocation holdingLoc = this.holdingModel != null ? this.holdingModel : defaultHoldingModel(name);

        if (!canHoldItems) {
            holdingLoc = modelLoc;
        }

        BlockBehaviour.Properties blockProps = this.blockProperties != null
                ? this.blockProperties
                : defaultBlockProperties();
        Item.Properties itemProps = this.itemProperties != null
                ? this.itemProperties
                : new Item.Properties();

        DollImplementations impls = DollAPI.requireImpls();

        Block dollBlock = registerBlockDirect(impls, id, blockProps);
        registerItemDirect(impls, id, dollBlock, itemProps);
        registerBlockEntityDirect(impls, id, dollBlock);

        DeferredBlock<? extends Block> deferredBlock = DeferredBlock.createBlock(id);
        DeferredItem<? extends Item> deferredItem = DeferredItem.createItem(id);
        DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> deferredBe =
                DeferredHolder.create(Registries.BLOCK_ENTITY_TYPE, id);

        DollConfig config = new DollConfig(name, modelLoc, textureLoc, holdingLoc, canHoldItems, modelType);
        DollResult result = new DollResult(name, deferredBlock, deferredItem, deferredBe, config);
        DollAPI.putRegistration(name, result);
        DollAPI.registerLootItem(deferredItem);

        // 非本模组命名空间时，自动生成 KubeJS 资源目录下的 blockstates、item model 与战利品表兜底，
        // 避免方块/物品因缺少 JSON 而显示紫黑占位、或破坏后无掉落。
        if (!DollAPI.modId().equals(namespace)) {
            generateKubeJsRuntimeAssets(namespace, name);
        }

        return result;
    }

    /**
     * 直接注册方块（供 {@link #registerDirect()} 使用）。
     *
     * @param impls      实现注入
     * @param id         注册 ID
     * @param blockProps 方块属性
     * @return 已注册的方块实例（与物品/方块实体绑定，保持同一实例）
     * @throws IllegalStateException 注册表锁定后写入时抛出（带可行动说明）
     */
    private static Block registerBlockDirect(DollImplementations impls, ResourceLocation id,
                                             BlockBehaviour.Properties blockProps) {
        Block dollBlock;
        try {
            dollBlock = impls.blockFactory().apply(blockProps);
            Registry.register(BuiltInRegistries.BLOCK, id, dollBlock);
        } catch (IllegalStateException e) {
            throw new IllegalStateException(
                    "[DollBuilder] registerDirect 无法在注册表锁定后注册方块 " + id
                            + "（NeoForge 注册阶段已结束）；请在 KubeJS 启动脚本/注册阶段内调用，"
                            + "或改用 DollBuilder#register() 走 DeferredRegister 路径", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return dollBlock;
    }

    /**
     * 直接注册物品（供 {@link #registerDirect()} 使用）。
     *
     * @param impls      实现注入
     * @param id         注册 ID
     * @param dollBlock  绑定的方块实例（与方块注册保持同一实例）
     * @param itemProps  物品属性
     * @throws IllegalStateException 注册表锁定后写入时抛出（带可行动说明）
     */
    private static void registerItemDirect(DollImplementations impls, ResourceLocation id, Block dollBlock,
                                           Item.Properties itemProps) {
        try {
            Item displayItem = impls.itemFactory().apply(dollBlock, itemProps);
            Registry.register(BuiltInRegistries.ITEM, id, displayItem);
        } catch (IllegalStateException e) {
            throw new IllegalStateException(
                    "[DollBuilder] registerDirect 无法在注册表锁定后注册物品 " + id
                            + "（NeoForge 注册阶段已结束）；请在 KubeJS 启动脚本/注册阶段内调用，"
                            + "或改用 DollBuilder#register() 走 DeferredRegister 路径", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 直接注册方块实体类型（供 {@link #registerDirect()} 使用）。
     *
     * @param impls      实现注入
     * @param id         注册 ID
     * @param dollBlock  有效方块实例（与方块注册保持同一实例）
     * @throws IllegalStateException 注册表锁定后写入时抛出（带可行动说明）
     */
    private static void registerBlockEntityDirect(DollImplementations impls, ResourceLocation id, Block dollBlock) {
        try {
            BlockEntityType<?> beType = BlockEntityType.Builder.of(impls.blockEntityFactory(), dollBlock).build(null);
            ResourceKey<BlockEntityType<?>> beKey = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, id);
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, beKey, beType);
        } catch (IllegalStateException e) {
            throw new IllegalStateException(
                    "[DollBuilder] registerDirect 无法在注册表锁定后注册方块实体 " + id
                            + "（NeoForge 注册阶段已结束）；请在 KubeJS 启动脚本/注册阶段内调用，"
                            + "或改用 DollBuilder#register() 走 DeferredRegister 路径", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 为 KubeJS 等外部命名空间自动生成必要的运行时资源文件。
     * <p>
     * 文件生成到 {@code <游戏目录>/kubejs/} 下：
     * <ul>
     *     <li>{@code assets/<namespace>/blockstates/<name>.json} — 引用 PasterDream 通用粒子模型</li>
     *     <li>{@code assets/<namespace>/models/item/<name>.json} — 引用 PasterDream 通用显示设置</li>
     *     <li>{@code data/<namespace>/loot_table/blocks/<name>.json} — 战利品表兜底（自掉落，
     *     文件已存在时跳过，保留脚本作者的自定义掉落）</li>
     * </ul>
     *
     * @param namespace 命名空间
     * @param name      注册名
     */
    private static void generateKubeJsRuntimeAssets(String namespace, String name) {
        DollAPI.requireImpls().assetGenerator().accept(namespace, name);
        generateLootTableFallback(namespace, name);
    }

    /**
     * 生成战利品表兜底文件（自掉落），保证 KubeJS 注册的玩偶破坏后掉落本体。
     * <p>
     * 覆盖规则：目标文件已存在时跳过，保留脚本作者手写的自定义掉落；
     * 兜底失败仅记录错误日志，不中断注册。
     *
     * @param namespace 命名空间
     * @param name      注册名
     */
    private static void generateLootTableFallback(String namespace, String name) {
        Path lootDir = FMLPaths.GAMEDIR.get().resolve("kubejs/data")
                .resolve(namespace).resolve("loot_table").resolve("blocks");
        Path lootFile = lootDir.resolve(name + ".json");

        if (Files.exists(lootFile)) {
            LOGGER.info("[DollBuilder] {}:{} 已存在自定义战利品表，跳过兜底生成: {}", namespace, name, lootFile);
            return;
        }

        try {
            Files.createDirectories(lootDir);
            Files.writeString(lootFile, lootTableJson(namespace, name));
            LOGGER.info("[DollBuilder] 为 {}:{} 生成战利品表兜底文件: {}", namespace, name, lootFile);
        } catch (IOException e) {
            LOGGER.error("[DollBuilder] 为 {}:{} 生成战利品表兜底文件失败", namespace, name, e);
        }
    }

    /**
     * 构造标准 1.21.1 自掉落战利品表 JSON（单数 loot_table 目录格式，
     * 与模组内置玩偶战利品表同构，不含 random_sequence——由 vanilla 按战利品表 ID 派生）。
     *
     * @param namespace 命名空间
     * @param name      注册名
     * @return 战利品表 JSON 字符串
     */
    private static String lootTableJson(String namespace, String name) {
        return """
                {
                  "type": "minecraft:block",
                  "pools": [
                    {
                      "rolls": 1,
                      "entries": [
                        {
                          "type": "minecraft:item",
                          "name": "%s:%s"
                        }
                      ],
                      "conditions": [
                        {
                          "condition": "minecraft:survives_explosion"
                        }
                      ]
                    }
                  ]
                }
                """.formatted(namespace, name);
    }

    private static ResourceLocation defaultModel(String name) {
        return DollAPI.requireImpls().defaultConfig().apply(name).model();
    }

    private static ResourceLocation defaultTexture(String name) {
        return DollAPI.requireImpls().defaultConfig().apply(name).texture();
    }

    private static ResourceLocation defaultHoldingModel(String name) {
        return DollAPI.requireImpls().defaultConfig().apply(name).holdingModel();
    }

    private static BlockBehaviour.Properties defaultBlockProperties() {
        return DollAPI.requireImpls().defaultBlockProperties().get();
    }
}
