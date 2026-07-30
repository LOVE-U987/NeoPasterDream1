package com.pasterdream.pasterdreammod.api.doll;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.blockentity.BlockEntityAPI;
import com.pasterdream.pasterdreammod.block.DollBlock;
import com.pasterdream.pasterdreammod.block.entity.DollBlockEntity;
import com.pasterdream.pasterdreammod.item.DollDisplayItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Objects;

/**
 * 玩偶注册 Builder
 * <p>
 * 采用流式 API，支持设置模型、纹理、抱物开关等属性，最后调用
 * {@link #register()} 完成注册。
 */
public class DollBuilder {

    private final String name;
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

        DeferredBlock<DollBlock> block = DollAPI.BLOCK_REGISTRY.register(name, () -> new DollBlock(blockProps));
        DeferredItem<DollDisplayItem> item = DollAPI.ITEM_REGISTRY.register(name, () -> new DollDisplayItem(block.get(), itemProps));
        DeferredHolder<BlockEntityType<?>, BlockEntityType<DollBlockEntity>> blockEntityType =
                BlockEntityAPI.register(name, () -> BlockEntityType.Builder.of(DollBlockEntity::new, block.get()).build(null));

        DollConfig config = new DollConfig(name, modelLoc, textureLoc, holdingLoc, canHoldItems, modelType);
        DollResult result = new DollResult(name, block, item, blockEntityType, config);
        DollAPI.putRegistration(name, result);
        return result;
    }

    private static ResourceLocation defaultModel(String name) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/block/" + name + ".geo.json");
    }

    private static ResourceLocation defaultTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/block/" + name + ".png");
    }

    private static ResourceLocation defaultHoldingModel(String name) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/block/" + name + "_holding.geo.json");
    }

    private static BlockBehaviour.Properties defaultBlockProperties() {
        return BlockBehaviour.Properties.of()
                .strength(1.0f)
                .sound(SoundType.DECORATED_POT)
                .noOcclusion();
    }
}
