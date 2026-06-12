package com.pasterdream.pasterdreammod.api.itemmigration.builder;

import com.pasterdream.pasterdreammod.api.itemmigration.model.ToolSpec;
import com.pasterdream.pasterdreammod.api.itemmigration.model.ToolSpec.ToolType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

/**
 * 工具/武器物品构建器 —— 用于注册剑、镐、斧、锹、锄、锤、法杖等
 * <p>
 * 通过 {@link ToolType} 枚举指定工具类型，自动选择对应的 Minecraft 工具基类
 * （{@link SwordItem}、{@link PickaxeItem}、{@link AxeItem}、{@link ShovelItem}、{@link HoeItem}）。
 * 内部使用 {@link Tier} 封装工具的耐久、挖掘速度、攻击伤害等基础属性，
 * 并通过 {@link TieredItem#createAttributes(Tier, float, float)} 生成属性修饰器。
 * </p>
 * <p>
 * {@link #build()} 方法通过 Supplier 延迟创建工具实例，避免注册表冻结问题。
 * 所有工具属性在注册阶段延迟解析，确保与 NeoForge 注册生命周期兼容。
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * ItemMigrationAPI.toolItem("copper_axe")
 *     .type(ToolType.AXE).durability(225)
 *     .attackDamage(7.0f).attackSpeed(-3.15f)
 *     .repairWith(new ItemStack(Items.COPPER_INGOT))
 *     .build();
 * }</pre>
 */
public class ToolItemBuilder extends BaseItemBuilder<ToolItemBuilder> {

    /** 工具属性规范 */
    private ToolSpec toolSpec;

    /**
     * 构造工具物品构建器
     *
     * @param registry     物品注册器
     * @param registryName 物品注册名（snake_case 格式）
     */
    public ToolItemBuilder(DeferredRegister.Items registry, String registryName) {
        super(registry, registryName);
        this.toolSpec = new ToolSpec(ToolType.SWORD, 250, 2.0f, 1.0f, -2.4f, 5,
                "minecraft:incorrect_for_wooden_tool", () -> Ingredient.EMPTY);
    }

    /**
     * 设置工具类型
     *
     * @param type 工具类型，可选值：{@link ToolType#SWORD}、{@link ToolType#PICKAXE}、{@link ToolType#AXE}、
     *             {@link ToolType#SHOVEL}、{@link ToolType#HOE}、{@link ToolType#HAMMER}、{@link ToolType#WAND}
     * @return 当前构建器实例
     */
    public ToolItemBuilder type(ToolType type) {
        this.toolSpec = new ToolSpec(type, toolSpec.durability(), toolSpec.miningSpeed(),
                toolSpec.attackDamage(), toolSpec.attackSpeed(), toolSpec.enchantmentValue(),
                toolSpec.incorrectTag(), toolSpec.repairIngredient());
        return this;
    }

    /**
     * 设置工具耐久度
     *
     * @param durability 耐久度值
     * @return 当前构建器实例
     */
    public ToolItemBuilder durability(int durability) {
        this.toolSpec = new ToolSpec(toolSpec.type(), durability, toolSpec.miningSpeed(),
                toolSpec.attackDamage(), toolSpec.attackSpeed(), toolSpec.enchantmentValue(),
                toolSpec.incorrectTag(), toolSpec.repairIngredient());
        return this;
    }

    /**
     * 设置工具挖掘速度
     *
     * @param speed 挖掘速度值
     * @return 当前构建器实例
     */
    public ToolItemBuilder miningSpeed(float speed) {
        this.toolSpec = new ToolSpec(toolSpec.type(), toolSpec.durability(), speed,
                toolSpec.attackDamage(), toolSpec.attackSpeed(), toolSpec.enchantmentValue(),
                toolSpec.incorrectTag(), toolSpec.repairIngredient());
        return this;
    }

    /**
     * 设置工具基础攻击伤害（不含工具类型固有的默认伤害）
     *
     * @param damage 额外攻击伤害值
     * @return 当前构建器实例
     */
    public ToolItemBuilder attackDamage(float damage) {
        this.toolSpec = new ToolSpec(toolSpec.type(), toolSpec.durability(), toolSpec.miningSpeed(),
                damage, toolSpec.attackSpeed(), toolSpec.enchantmentValue(),
                toolSpec.incorrectTag(), toolSpec.repairIngredient());
        return this;
    }

    /**
     * 设置工具攻击速度
     *
     * @param speed 攻击速度值（通常为负数，如 -2.4f）
     * @return 当前构建器实例
     */
    public ToolItemBuilder attackSpeed(float speed) {
        this.toolSpec = new ToolSpec(toolSpec.type(), toolSpec.durability(), toolSpec.miningSpeed(),
                toolSpec.attackDamage(), speed, toolSpec.enchantmentValue(),
                toolSpec.incorrectTag(), toolSpec.repairIngredient());
        return this;
    }

    /**
     * 设置工具附魔能力
     *
     * @param value 附魔能力值（越高越容易获得好附魔）
     * @return 当前构建器实例
     */
    public ToolItemBuilder enchantment(int value) {
        this.toolSpec = new ToolSpec(toolSpec.type(), toolSpec.durability(), toolSpec.miningSpeed(),
                toolSpec.attackDamage(), toolSpec.attackSpeed(), value,
                toolSpec.incorrectTag(), toolSpec.repairIngredient());
        return this;
    }

    /**
     * 设置不适用标签 ID
     * <p>
     * 用于标记该工具无法有效挖掘的方块。
     * 常用值：
     * "minecraft:incorrect_for_wooden_tool"（木）
     * "minecraft:incorrect_for_stone_tool"（石）
     * "minecraft:incorrect_for_iron_tool"（铁）
     * "minecraft:incorrect_for_gold_tool"（金）
     * "minecraft:incorrect_for_diamond_tool"（钻石）
     * "minecraft:incorrect_for_netherite_tool"（下界合金）
     * </p>
     *
     * @param tagName 不适用标签 ID
     * @return 当前构建器实例
     */
    public ToolItemBuilder incorrectTag(String tagName) {
        this.toolSpec = new ToolSpec(toolSpec.type(), toolSpec.durability(), toolSpec.miningSpeed(),
                toolSpec.attackDamage(), toolSpec.attackSpeed(), toolSpec.enchantmentValue(),
                tagName, toolSpec.repairIngredient());
        return this;
    }

    /**
     * 设置工具修复材料
     *
     * @param stacks 用于铁砧修复的物品栈，如 {@code new ItemStack(Items.COPPER_INGOT)}
     * @return 当前构建器实例
     */
    public ToolItemBuilder repairWith(ItemStack... stacks) {
        if (stacks.length == 0) return this;
        List<ItemStack> stackList = List.of(stacks);
        this.toolSpec = new ToolSpec(toolSpec.type(), toolSpec.durability(), toolSpec.miningSpeed(),
                toolSpec.attackDamage(), toolSpec.attackSpeed(), toolSpec.enchantmentValue(),
                toolSpec.incorrectTag(), () -> Ingredient.of(stackList.stream()));
        return this;
    }

    /**
     * 根据当前 toolSpec 构建 {@link SimpleTier}
     * <p>
     * Tier 的 getAttackDamageBonus() 使用 toolSpec.attackDamage()，
     * 这样攻击伤害由 Tier 本身承载，符合原版 Tier 的设计模式。
     * createAttributes() 中的攻击伤害参数设为 0，避免伤害重复叠加。
     * </p>
     *
     * @return 工具等级实例
     */
    private Tier buildTier() {
        TagKey<Block> incorrectTag = parseIncorrectTag(toolSpec.incorrectTag());
        return new SimpleTier(
                incorrectTag,
                toolSpec.durability(),
                toolSpec.miningSpeed(),
                toolSpec.attackDamage(),
                toolSpec.enchantmentValue(),
                toolSpec.repairIngredient()
        );
    }

    /**
     * 执行工具/武器物品的创建和注册
     * <p>
     * 通过 Supplier 延迟创建工具实例，确保在注册阶段才实际构造工具对象。
     * 根据 {@link ToolType} 自动选择对应的 Minecraft 工具基类，
     * 并应用工具等级（Tier）和属性修饰器。
     * 如果设置了 tooltip 文本，会自动创建带描述文本的匿名子类。
     * </p>
     *
     * @return 注册完成的 DeferredItem，泛型为 {@link Item}
     */
    @Override
    public DeferredItem<Item> build() {
        Tier tier = buildTier();
        Item.Properties props = applyProperties();
        ToolType type = toolSpec.type();
        float speed = toolSpec.attackSpeed();
        boolean hasTooltip = !tooltipLines.isEmpty();
        List<String> lines = List.copyOf(this.tooltipLines);

        Supplier<Item> supplier = () -> {
            Item.Properties attrProps = switch (type) {
                case SWORD -> props.attributes(SwordItem.createAttributes(tier, 0, speed));
                case PICKAXE, HAMMER ->
                        props.attributes(PickaxeItem.createAttributes(tier, 0, speed));
                case AXE -> props.attributes(AxeItem.createAttributes(tier, 0, speed));
                case SHOVEL -> props.attributes(ShovelItem.createAttributes(tier, 0, speed));
                case HOE -> props.attributes(HoeItem.createAttributes(tier, 0, speed));
                case WAND -> props;
            };

            if (!hasTooltip) {
                return switch (type) {
                    case SWORD -> new SwordItem(tier, attrProps);
                    case PICKAXE, HAMMER -> new PickaxeItem(tier, attrProps);
                    case AXE -> new AxeItem(tier, attrProps);
                    case SHOVEL -> new ShovelItem(tier, attrProps);
                    case HOE -> new HoeItem(tier, attrProps);
                    case WAND -> new Item(attrProps.stacksTo(1));
                };
            }

            return switch (type) {
                case SWORD -> new SwordItem(tier, attrProps) {
                    @Override
                    public void appendHoverText(ItemStack stack, TooltipContext context,
                                                List<Component> tooltip, TooltipFlag flag) {
                        super.appendHoverText(stack, context, tooltip, flag);
                        for (String line : lines) {
                            tooltip.add(Component.literal(line));
                        }
                    }
                };
                case PICKAXE, HAMMER -> new PickaxeItem(tier, attrProps) {
                    @Override
                    public void appendHoverText(ItemStack stack, TooltipContext context,
                                                List<Component> tooltip, TooltipFlag flag) {
                        super.appendHoverText(stack, context, tooltip, flag);
                        for (String line : lines) {
                            tooltip.add(Component.literal(line));
                        }
                    }
                };
                case AXE -> new AxeItem(tier, attrProps) {
                    @Override
                    public void appendHoverText(ItemStack stack, TooltipContext context,
                                                List<Component> tooltip, TooltipFlag flag) {
                        super.appendHoverText(stack, context, tooltip, flag);
                        for (String line : lines) {
                            tooltip.add(Component.literal(line));
                        }
                    }
                };
                case SHOVEL -> new ShovelItem(tier, attrProps) {
                    @Override
                    public void appendHoverText(ItemStack stack, TooltipContext context,
                                                List<Component> tooltip, TooltipFlag flag) {
                        super.appendHoverText(stack, context, tooltip, flag);
                        for (String line : lines) {
                            tooltip.add(Component.literal(line));
                        }
                    }
                };
                case HOE -> new HoeItem(tier, attrProps) {
                    @Override
                    public void appendHoverText(ItemStack stack, TooltipContext context,
                                                List<Component> tooltip, TooltipFlag flag) {
                        super.appendHoverText(stack, context, tooltip, flag);
                        for (String line : lines) {
                            tooltip.add(Component.literal(line));
                        }
                    }
                };
                case WAND -> new Item(attrProps.stacksTo(1));
            };
        };

        return registry.register(registryName, supplier);
    }

    /**
     * 解析不适用标签的字符串标识为 {@link TagKey}
     *
     * @param tagName 标签名，如 "minecraft:incorrect_for_wooden_tool"
     * @return 对应的 TagKey
     */
    private static TagKey<Block> parseIncorrectTag(String tagName) {
        String[] parts = tagName.split(":", 2);
        String namespace = parts.length > 1 ? parts[0] : "minecraft";
        String path = parts.length > 1 ? parts[1] : tagName;
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    /**
     * 快速创建并注册工具/武器物品的静态工厂方法
     *
     * @param registry     物品注册器
     * @param registryName 物品注册名
     * @param type         工具类型
     * @return 注册完成的 DeferredItem
     */
    public static DeferredItem<Item> create(DeferredRegister.Items registry, String registryName, ToolType type) {
        return new ToolItemBuilder(registry, registryName).type(type).build();
    }
}
