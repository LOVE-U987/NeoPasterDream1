package com.pasterdream.pasterdreammod.api.itemmigration.builder;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 物品构建器的抽象基类 —— 采用"模板方法 + 自类型泛型"模式
 * <p>
 * 提供物品注册所需的公共属性（注册名、物品属性、描述文本）和链式设置方法。
 * 子类通过继承 {@code BaseItemBuilder<子类名>} 获得正确的链式返回类型，
 * 只需实现 {@link #build()} 方法完成具体的物品创建和注册逻辑。
 * </p>
 * <p>
 * <b>重要：</b>所有子类的 {@link #build()} 方法必须使用延迟注册（Supplier）方式创建物品，
 * 绝不能直接创建 Item 实例。因为 itemmigration API 拥有独立的 {@code DeferredRegister}，
 * 在 PDItems 加载时物品尚未进入注册阶段，直接创建 Item 会触发
 * "Registry is already frozen" 错误。
 * 正确的做法是将物品创建逻辑封装在 {@code () -> new Item(props)} 这样的 Supplier 中。
 * </p>
 *
 * @param <T> 子类自身的类型，用于支持链式调用返回正确子类类型
 */
public abstract class BaseItemBuilder<T extends BaseItemBuilder<T>> {

    /** 物品注册器引用 */
    protected final DeferredRegister.Items registry;

    /** 物品注册名（snake_case 格式） */
    protected final String registryName;

    /** 物品的基础属性，通过链式方法逐步构建 */
    protected Item.Properties properties = new Item.Properties();

    /** 物品描述文本行列表，调用 {@link #tooltip(String...)} 添加 */
    protected List<String> tooltipLines = new ArrayList<>();

    /**
     * 构造基础构建器
     *
     * @param registry     物品注册器
     * @param registryName 物品注册名
     */
    public BaseItemBuilder(DeferredRegister.Items registry, String registryName) {
        this.registry = registry;
        this.registryName = registryName;
    }

    /**
     * 设置最大堆叠数
     *
     * @param stackSize 最大堆叠数（1 ~ 99）
     * @return 当前构建器实例
     */
    @SuppressWarnings("unchecked")
    public T stacksTo(int stackSize) {
        this.properties = this.properties.stacksTo(stackSize);
        return (T) this;
    }

    /**
     * 设置物品稀有度
     *
     * @param rarity 稀有度枚举值
     * @return 当前构建器实例
     */
    @SuppressWarnings("unchecked")
    public T rarity(Rarity rarity) {
        this.properties = this.properties.rarity(rarity);
        return (T) this;
    }

    /**
     * 设置物品防火（不会被熔岩或火焰摧毁）
     *
     * @return 当前构建器实例
     */
    @SuppressWarnings("unchecked")
    public T fireResistant() {
        this.properties = this.properties.fireResistant();
        return (T) this;
    }

    /**
     * 添加物品描述文本行
     * <p>
     * 每行文本会以 {@link Component#literal(String)} 的形式在物品提示中显示。
     * 支持 Minecraft 颜色代码（如 "§7"、"§e" 等）。
     * </p>
     *
     * @param lines 描述文本行，可传入多个字符串
     * @return 当前构建器实例
     */
    @SuppressWarnings("unchecked")
    public T tooltip(String... lines) {
        this.tooltipLines.addAll(List.of(lines));
        return (T) this;
    }

    /**
     * 执行物品创建和注册
     * <p>
     * 子类必须实现此方法，在其中完成具体物品的实例化，
     * 并通过 {@code registry.register(registryName, supplier)} 注册，
     * 返回对应的 {@link DeferredItem}。
     * </p>
     * <p>
     * <b>要求：</b>必须使用 Supplier 延迟创建 Item 实例，
     * 绝不能直接 {@code new Item(props)} 然后传入 supplier，
     * 否则在静态初始化阶段会触发注册表冻结错误。
     * </p>
     *
     * @return 注册完成的物品代理对象
     */
    public abstract DeferredItem<? extends Item> build();

    /**
     * 应用当前已设置的属性，返回最终的 {@link Item.Properties} 实例
     *
     * @return 已配置的物品属性
     */
    protected Item.Properties applyProperties() {
        return this.properties;
    }

    /**
     * 根据当前 tooltip 设置，返回一个延迟创建带描述文本的 Item 的 Supplier
     * <p>
     * 如果 {@link #tooltipLines} 为空，直接返回创建普通 Item 的 Supplier；
     * 否则返回一个继承自 {@code Item} 的匿名子类的 Supplier，
     * 覆写 {@link Item#appendHoverText(ItemStack, Item.TooltipContext, List, TooltipFlag)}
     * 添加预设的描述文本。
     * </p>
     *
     * @param props 物品属性，由 {@link #applyProperties()} 提供
     * @return 延迟创建 Item 的 Supplier
     */
    protected Supplier<Item> createTooltipSupplier(Item.Properties props) {
        if (tooltipLines.isEmpty()) {
            return () -> new Item(props);
        }
        List<String> lines = List.copyOf(this.tooltipLines);
        return () -> new Item(props) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                        List<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                for (String line : lines) {
                    tooltip.add(Component.literal(line));
                }
            }
        };
    }
}
