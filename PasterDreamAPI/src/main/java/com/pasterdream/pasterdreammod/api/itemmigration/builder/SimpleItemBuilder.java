package com.pasterdream.pasterdreammod.api.itemmigration.builder;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 简易物品构建器 —— 用于注册基础材料、普通物品
 * <p>
 * 继承 {@link BaseItemBuilder}，复用属性设置和 tooltip 逻辑。
 * {@link #build()} 方法通过 Supplier 延迟创建 {@link Item} 实例并注册，
 * 避免在静态初始化阶段触发注册表冻结错误。
 * 支持通过 {@link #stacksTo(int)}、{@link #rarity(Rarity)}、{@link #fireResistant()} 等
 * 链式方法配置物品属性。
 * <p>
 * 使用示例：
 * <pre>{@code
 * ItemMigrationAPI.simpleItem("titanium_ingot")
 *     .rarity(Rarity.UNCOMMON)
 *     .stacksTo(16)
 *     .build();
 * }</pre>
 */
public class SimpleItemBuilder extends BaseItemBuilder<SimpleItemBuilder> {

    /**
     * 构造简易物品构建器
     *
     * @param registry     物品注册器
     * @param registryName 物品注册名（snake_case 格式）
     */
    public SimpleItemBuilder(DeferredRegister.Items registry, String registryName) {
        super(registry, registryName);
    }

    /**
     * 执行简易物品的创建和注册
     * <p>
     * 通过 Supplier 延迟创建 {@link Item} 实例，确保在注册阶段才实际构造物品对象。
     * 如果设置了 tooltip 描述文本，会自动创建带描述文本的匿名子类。
     * </p>
     *
     * @return 注册完成的 DeferredItem，泛型为 {@link Item}
     */
    @Override
    public DeferredItem<Item> build() {
        Item.Properties props = applyProperties();
        Supplier<Item> supplier = createTooltipSupplier(props);
        return registry.register(registryName, supplier);
    }

    /**
     * 快速创建并注册简易物品的静态工厂方法
     * <p>
     * 使用默认属性创建一个简易物品并立即注册。
     * 如需自定义属性（稀有度、堆叠数、tooltip 等）请使用构造器 + 链式方法。
     * </p>
     *
     * @param registry     物品注册器
     * @param registryName 物品注册名
     * @return 注册完成的 DeferredItem
     */
    public static DeferredItem<Item> create(DeferredRegister.Items registry, String registryName) {
        return new SimpleItemBuilder(registry, registryName).build();
    }
}
