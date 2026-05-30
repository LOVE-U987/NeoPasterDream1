package com.pasterdream.pasterdreammod.api.itemmigration.builder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.pasterdream.pasterdreammod.api.itemmigration.model.AttributeModSpec;
import com.pasterdream.pasterdreammod.api.itemmigration.model.CurioSpec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.function.Supplier;

/**
 * 饰品（Curio）物品构建器 —— 用于注册 Curios API 饰品
 * <p>
 * 创建实现 {@link ICurioItem} 接口的匿名物品类，
 * 支持设置饰品槽位、属性修饰器和描述文本。
 * 属性修饰器通过 {@link AttributeModSpec} 定义，
 * 自动映射到 Curios API 的 {@code getAttributeModifiers} 方法。
 * </p>
 * <p>
 * {@link #build()} 方法通过 Supplier 延迟创建 Curio 饰品实例，
 * 确保在注册阶段才实际构造物品对象，避免注册表冻结问题。
 * </p>
 *
 * 使用示例：
 * <pre>{@code
 * ItemMigrationAPI.curioItem("embryo_ring")
 *     .slot("ring")
 *     .attribute("minecraft:generic.attack_damage",
 *         "a1b2c3d4-...", 2.0, 0)
 *     .tooltip("§7胚胎之戒")
 *     .build();
 * }</pre>
 */
public class CurioItemBuilder extends BaseItemBuilder<CurioItemBuilder> {

    /** 饰品属性规范 */
    private CurioSpec curioSpec;

    /**
     * 构造饰品物品构建器
     *
     * @param registry     物品注册器
     * @param registryName 物品注册名（snake_case 格式）
     */
    public CurioItemBuilder(DeferredRegister.Items registry, String registryName) {
        super(registry, registryName);
        this.curioSpec = new CurioSpec("ring", "", List.of());
    }

    /**
     * 设置饰品槽位类型
     *
     * @param slot 槽位标识，如 "ring"、"necklace"、"belt"、"charm"、"head" 等
     * @return 当前构建器实例
     */
    public CurioItemBuilder slot(String slot) {
        this.curioSpec = new CurioSpec(slot, curioSpec.translationKey(), curioSpec.attributeMods());
        return this;
    }

    /**
     * 添加属性修饰器
     * <p>
     * 当玩家佩戴此饰品时，会自动应用指定的属性修饰效果。
     * 多个属性修饰器可重复调用此方法添加。
     * </p>
     *
     * @param attributeName 属性注册名，如 "minecraft:generic.attack_damage"
     * @param id            修饰器的唯一标识符（UUID 十六进制字符串）
     * @param amount        修饰数值，正数为增益，负数为减益
     * @param operation     运算类型：0 = ADDITION（加法），1 = MULTIPLY_BASE，2 = MULTIPLY_TOTAL
     * @return 当前构建器实例
     */
    public CurioItemBuilder attribute(String attributeName, String id, double amount, int operation) {
        List<AttributeModSpec> mods = new java.util.ArrayList<>(curioSpec.attributeMods());
        mods.add(new AttributeModSpec(attributeName, id, amount, operation));
        this.curioSpec = new CurioSpec(curioSpec.curioSlot(), curioSpec.translationKey(), List.copyOf(mods));
        return this;
    }

    /**
     * 将操作类型整数值转换为 {@link AttributeModifier.Operation} 枚举
     *
     * @param operation 操作类型值（0、1、2）
     * @return 对应的 AttributeModifier.Operation 枚举
     */
    private static AttributeModifier.Operation parseOperation(int operation) {
        return switch (operation) {
            case 1 -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case 2 -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
            default -> AttributeModifier.Operation.ADD_VALUE;
        };
    }

    /**
     * 执行饰品物品的创建和注册
     * <p>
     * 通过 Supplier 延迟创建实现 {@link ICurioItem} 的局部内部类实例：
     * <ul>
     *   <li>如果设置了 {@code attributeMods}，覆写 {@code getAttributeModifiers} 返回属性修饰器映射</li>
     *   <li>如果设置了 {@code tooltipLines}，覆写 {@code appendHoverText} 添加描述文本</li>
     * </ul>
     * </p>
     *
     * @return 注册完成的 DeferredItem，泛型为 {@link Item}
     */
    @Override
    public DeferredItem<Item> build() {
        Item.Properties props = applyProperties();
        List<AttributeModSpec> mods = List.copyOf(this.curioSpec.attributeMods());
        List<String> lines = List.copyOf(this.tooltipLines);

        class CurioAnonymousItem extends Item implements ICurioItem {
            CurioAnonymousItem(Item.Properties properties) {
                super(properties);
            }

            @Override
            public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
                    SlotContext slotContext, ResourceLocation id, ItemStack stack) {
                Multimap<Holder<Attribute>, AttributeModifier> result = HashMultimap.create();
                for (AttributeModSpec mod : mods) {
                    Holder<Attribute> attribute = BuiltInRegistries.ATTRIBUTE
                            .getHolder(ResourceLocation.parse(mod.attributeName()))
                            .orElse(null);
                    if (attribute != null) {
                        ResourceLocation modifierId = ResourceLocation.parse(
                                "pasterdream:" + mod.id().replace("-", "_").toLowerCase());
                        AttributeModifier modifier = new AttributeModifier(
                                modifierId, mod.amount(), parseOperation(mod.operation()));
                        result.put(attribute, modifier);
                    }
                }
                return result;
            }

            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                        List<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                for (String line : lines) {
                    tooltip.add(Component.literal(line));
                }
            }
        }

        return registry.register(registryName, () -> new CurioAnonymousItem(props));
    }

    /**
     * 快速创建并注册饰品物品的静态工厂方法
     * <p>
     * 使用指定的槽位创建一个饰品物品并立即注册。
     * 如需自定义属性（属性修饰器、tooltip 等）请使用构造器 + 链式方法。
     * </p>
     *
     * @param registry     物品注册器
     * @param registryName 物品注册名
     * @param curioSlot    饰品槽位标识
     * @return 注册完成的 DeferredItem
     */
    public static DeferredItem<Item> create(DeferredRegister.Items registry, String registryName, String curioSlot) {
        return new CurioItemBuilder(registry, registryName).slot(curioSlot).build();
    }
}
