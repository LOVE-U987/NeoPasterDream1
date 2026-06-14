package com.pasterdream.pasterdreammod.api.curio;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioAttributeMod;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 饰品构建器 —— 链式配置并注册一个 Curios 饰品
 * <p>
 * 使用 {@link CurioAPI#create(String)} 获取实例。
 * </p>
 */
public class CurioBuilder {

    private final DeferredRegister.Items registry;
    private final String name;
    private final String fullName;

    // 基础属性
    private int maxStackSize = 1;
    private Rarity rarity = Rarity.COMMON;
    private final List<CurioAttributeMod> attributes = new ArrayList<>();
    private final List<String> tooltipLines = new ArrayList<>();

    // 槽位
    private String slotId = "curio";

    // 渲染
    private String renderType = "none";

    // 自定义属性配置器（用于更复杂的属性设置）
    private Consumer<Item.Properties> propertiesCustomizer = null;

    // 自定义物品工厂（替代默认的通用 CurioItem）
    private CurioItemFactory itemFactory = null;

    // 自定义渲染器供应商
    private Supplier<ICurioRenderer> rendererSupplier = null;

    /**
     * @param registry 物品注册器
     * @param name     注册名（不含命名空间）
     */
    CurioBuilder(DeferredRegister.Items registry, String name) {
        this.registry = registry;
        this.name = name;
        this.fullName = PasterDreamAPI.MOD_ID + ":" + name;
    }

    // ======================== 基础属性设置 ========================

    /**
     * 设置饰品槽位类型（使用枚举）。
     *
     * @param slot 槽位枚举
     * @return 当前构建器
     */
    public CurioBuilder slot(CurioSlot slot) {
        this.slotId = slot.getSlotId();
        return this;
    }

    /**
     * 设置饰品槽位类型（使用自定义字符串）。
     *
     * @param slotId 槽位标识，如 "ring"、"necklace"
     * @return 当前构建器
     */
    public CurioBuilder slot(String slotId) {
        this.slotId = Objects.requireNonNull(slotId, "slotId 不能为空");
        return this;
    }

    /**
     * 设置最大堆叠数（默认为 1，饰品通常为 1）。
     *
     * @param maxStackSize 最大堆叠数
     * @return 当前构建器
     */
    public CurioBuilder stacksTo(int maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    /**
     * 设置饰品品质。
     *
     * @param rarity 品质枚举
     * @return 当前构建器
     */
    public CurioBuilder rarity(Rarity rarity) {
        this.rarity = Objects.requireNonNull(rarity, "rarity 不能为空");
        return this;
    }

    /**
     * 添加一个属性修饰器。
     *
     * @param attributeId 属性注册名（如 "minecraft:generic.attack_damage"）
     * @param uuid        修饰器 UUID 字符串
     * @param amount      修饰值
     * @param operation   修饰运算方式（AttributeModifier.Operation）
     * @return 当前构建器
     */
    public CurioBuilder attribute(String attributeId, String uuid, double amount,
                                  AttributeModifier.Operation operation) {
        this.attributes.add(new CurioAttributeMod(
                attributeId, uuid, amount, operation.ordinal()));
        return this;
    }

    /**
     * 添加一个已构建的属性修饰器。
     *
     * @param mod 属性修饰器规范
     * @return 当前构建器
     */
    public CurioBuilder attribute(CurioAttributeMod mod) {
        Objects.requireNonNull(mod, "attribute mod 不能为空");
        this.attributes.add(mod);
        return this;
    }

    /**
     * 批量添加属性修饰器。
     *
     * @param mods 属性修饰器列表
     * @return 当前构建器
     */
    public CurioBuilder attributes(List<CurioAttributeMod> mods) {
        this.attributes.addAll(mods);
        return this;
    }

    /**
     * 设置物品属性自定义器（用于设置火焰抗性、耐久度等复杂属性）。
     * <pre>{@code
     * .customizeProperties(props -> props.fireResistant().durability(100))
     * }</pre>
     *
     * @param customizer 属性自定义器
     * @return 当前构建器
     */
    public CurioBuilder customizeProperties(Consumer<Item.Properties> customizer) {
        this.propertiesCustomizer = customizer;
        return this;
    }

    /**
     * 添加一行教程文本。
     *
     * @param line 文本内容（支持 § 颜色代码）
     * @return 当前构建器
     */
    public CurioBuilder tooltip(String line) {
        this.tooltipLines.add(line);
        return this;
    }

    /**
     * 批量添加教程文本。
     *
     * @param lines 文本数组
     * @return 当前构建器
     */
    public CurioBuilder tooltip(String... lines) {
        this.tooltipLines.addAll(Arrays.asList(lines));
        return this;
    }

    // ======================== 渲染设置 ========================

    /**
     * 启用 Curios 默认身体渲染。
     * <p>
     * 饰品将在玩家身体对应槽位显示物品的平面贴图。
     * </p>
     *
     * @return 当前构建器
     */
    public CurioBuilder renderBuiltin() {
        this.renderType = "builtin";
        return this;
    }

    /**
     * 使用自定义渲染器（需在客户端注册）。
     * <p>
     * 调用此方法后，需要在客户端代码中通过
     * {@code CurioRenderers.addRenderer(item, yourRenderer)} 注册实际渲染器。
     * </p>
     *
     * @return 当前构建器
     */
    public CurioBuilder renderCustom() {
        this.renderType = "custom";
        return this;
    }

    /**
     * 设置渲染类型（高级用法）。
     *
     * @param renderType 渲染类型标识
     * @return 当前构建器
     */
    public CurioBuilder renderType(String renderType) {
        this.renderType = Objects.requireNonNull(renderType, "renderType 不能为空");
        return this;
    }

    /**
     * 注册自定义身体渲染器。
     * <p>
     * 使用 Curios API 的 {@link top.theillusivec4.curios.api.client.CuriosRendererRegistry#register}
     * 在后端注册渲染器。渲染器将在客户端初始化时自动注册。
     * </p>
     * <pre>{@code
     * .renderer(() -> new MyCrownRenderer())
     * }</pre>
     * <p>
     * 如果使用 GeckoLib 模型渲染，推荐使用 {@link CurioGeckoLibRenderer} 工具类。
     * </p>
     *
     * @param renderer 渲染器供应商
     * @return 当前构建器
     */
    public CurioBuilder renderer(Supplier<ICurioRenderer> renderer) {
        this.rendererSupplier = Objects.requireNonNull(renderer, "rendererSupplier 不能为空");
        this.renderType = "custom";
        return this;
    }

    // ======================== 自定义物品工厂 ========================

    /**
     * 使用自定义物品类替代默认的通用 CurioItem。
     * <pre>{@code
     * .withItemClass(() -> new MyCustomCurioItem())
     * }</pre>
     *
     * @param factory 物品工厂
     * @return 当前构建器
     */
    public CurioBuilder withItemClass(CurioItemFactory factory) {
        this.itemFactory = factory;
        return this;
    }

    // ======================== 注册 ========================

    /**
     * 注册饰品到游戏。
     * <p>
     * 物品实例创建被推迟到 DeferredRegister 的回调中执行，
     * 避免在注册表冻结时调用 {@code Item.<init>} 导致崩溃。
     * </p>
     *
     * @return 注册后的物品持有者
     */
    public net.neoforged.neoforge.registries.DeferredItem<Item> register() {
        // 将物品创建推迟到 DeferredRegister 回调中，避免注册表冻结问题
        var deferredItem = registry.register(name, () -> {
            // 构建物品（此时注册表已就绪，Item.<init> 可正常调用）
            Item created = (itemFactory != null)
                    ? itemFactory.create()
                    : new CurioAPIBuiltItem(buildProperties(), attributes, tooltipLines);

            // 记录渲染信息
            if (!"none".equals(renderType)) {
                CurioAPI.RENDERER_REGISTRY.put(fullName, renderType);
            }

            // 存储渲染器供应商（用于客户端注册）
            if (rendererSupplier != null) {
                CurioAPI.RENDERER_SUPPLIERS.put(fullName, rendererSupplier);
            }

            // 记录注册信息
            CurioAPI.REGISTERED_CURIOS.add(new CurioAPI.CurioRegistration(
                    name, fullName, slotId, created, renderType));

            return created;
        });

        return deferredItem;
    }

    // ======================== 内部方法 ========================

    private Item.Properties buildProperties() {
        Item.Properties props = new Item.Properties()
                .stacksTo(maxStackSize)
                .rarity(rarity);
        if (propertiesCustomizer != null) {
            propertiesCustomizer.accept(props);
        }
        return props;
    }

    // ======================== 内部通用饰品类 ========================

    /**
     * 通用的 API 构建饰品 —— 实现 ICurioItem 接口，
     * 支持注入属性修饰器和工具文本。
     */
    public static class CurioAPIBuiltItem extends Item implements ICurioItem {

        private final List<CurioAttributeMod> attributeMods;
        private final List<String> tooltipLines;

        public CurioAPIBuiltItem(Item.Properties properties,
                                 List<CurioAttributeMod> attributeMods,
                                 List<String> tooltipLines) {
            super(properties);
            this.attributeMods = Collections.unmodifiableList(
                    new ArrayList<>(attributeMods));
            this.tooltipLines = Collections.unmodifiableList(
                    new ArrayList<>(tooltipLines));
        }

        @Override
        public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
                SlotContext slotContext, UUID uuid, ItemStack stack) {
            Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();
            for (CurioAttributeMod mod : attributeMods) {
                ResourceLocation attrId = ResourceLocation.parse(mod.attributeId());
                Holder<Attribute> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attrId).orElse(null);
                if (attribute != null) {
                    String modName = getDescriptionId(stack) + "_" + mod.attributeId().replace(':', '_');
                    modifiers.put(attribute, new AttributeModifier(
                            ResourceLocation.parse(mod.uuid()),
                            mod.amount(),
                            mod.toOperation()));
                }
            }
            return modifiers;
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context,
                                    List<Component> tooltipComponents,
                                    TooltipFlag tooltipFlag) {
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            for (String line : tooltipLines) {
                tooltipComponents.add(Component.literal(line));
            }
        }
    }

    // ======================== 函数式接口 ========================

    /**
     * 自定义物品工厂 —— 用于创建复杂的自定义饰品实例。
     */
    @FunctionalInterface
    public interface CurioItemFactory {
        /**
         * 创建物品实例。
         *
         * @return 新的物品实例（必须实现 ICurioItem）
         */
        Item create();
    }
}