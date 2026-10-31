package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

/**
 * 玻璃罐饮品标签与合成残留验证钩子。
 * <p>
 * 挂在 {@code PASTERDREAM_VERIFY=1} 的 {@code core} 套件，断言：
 * 牛奶罐/水瓶罐的 c 饮品子标签归属、父标签聚合、NeoForge 默认值保留、
 * 合成残留返还空玻璃罐，以及玻璃罐/玻璃杯无残留的负向回归。
 * </p>
 */
public final class PDGlassJarVerifyHooks {

    /** 单条断言结果。 */
    public record Result(boolean pass, String name, String detail) {}

    private static final TagKey<Item> C_DRINKS = tag("drinks");
    private static final TagKey<Item> C_DRINKS_MILK = tag("drinks/milk");
    private static final TagKey<Item> C_DRINKS_WATER = tag("drinks/water");
    private static final TagKey<Item> C_DRINKS_WATERY = tag("drinks/watery");
    private static final TagKey<Item> C_DRINKS_HONEY = tag("drinks/honey");
    private static final TagKey<Item> C_DRINKS_OMINOUS = tag("drinks/ominous");

    private PDGlassJarVerifyHooks() {
    }

    /**
     * 构建 {@code c} 命名空间下的物品标签键。
     *
     * @param path 标签路径（如 {@code drinks/milk}）
     * @return 对应的物品标签键
     */
    private static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    /**
     * 执行全部玻璃罐相关断言。
     *
     * @param out 断言结果消费者
     */
    public static void verify(Consumer<Result> out) {
        ItemStack milkJar = new ItemStack(PDItems.MILK_GLASSJAR.get());
        ItemStack waterJar = new ItemStack(PDItems.WATER_GLASSJAR.get());

        // 1. 标签归属
        check(out, "milk_glassjar in #c:drinks", milkJar.is(C_DRINKS));
        check(out, "milk_glassjar in #c:drinks/milk", milkJar.is(C_DRINKS_MILK));
        check(out, "water_glassjar in #c:drinks", waterJar.is(C_DRINKS));
        check(out, "water_glassjar in #c:drinks/water", waterJar.is(C_DRINKS_WATER));
        check(out, "water_glassjar in #c:drinks/watery", waterJar.is(C_DRINKS_WATERY));

        // 2. 合并而非覆盖：NeoForge 默认值仍在，父标签仍聚合其它子标签
        check(out, "minecraft:milk_bucket still in #c:drinks/milk",
                new ItemStack(Items.MILK_BUCKET).is(C_DRINKS_MILK));
        check(out, "minecraft:honey_bottle still in #c:drinks and #c:drinks/honey",
                new ItemStack(Items.HONEY_BOTTLE).is(C_DRINKS)
                        && new ItemStack(Items.HONEY_BOTTLE).is(C_DRINKS_HONEY));
        check(out, "minecraft:ominous_bottle still in #c:drinks and #c:drinks/ominous",
                new ItemStack(Items.OMINOUS_BOTTLE).is(C_DRINKS)
                        && new ItemStack(Items.OMINOUS_BOTTLE).is(C_DRINKS_OMINOUS));

        // 3. 合成残留：牛奶罐/水瓶罐返还空玻璃罐
        Item glassJar = PDItems.GLASSJAR.get();
        checkRemainder(out, "milk_glassjar", milkJar, glassJar);
        checkRemainder(out, "water_glassjar", waterJar, glassJar);

        // 4. 负向回归：玻璃罐与玻璃杯本身不得有合成残留
        check(out, "glassjar has no crafting remainder",
                !new ItemStack(glassJar).hasCraftingRemainingItem());
        check(out, "glass_cup has no crafting remainder",
                !new ItemStack(PDItems.GLASS_CUP.get()).hasCraftingRemainingItem());
    }

    /**
     * 断言物品栈的合成残留为指定物品且数量为 1。
     *
     * @param out      断言结果消费者
     * @param label    物品标签名（用于报告）
     * @param stack    待检查物品栈
     * @param expected 期望的残留物品
     */
    private static void checkRemainder(Consumer<Result> out, String label, ItemStack stack, Item expected) {
        String name = label + " crafting remainder";
        if (!stack.hasCraftingRemainingItem()) {
            out.accept(new Result(false, name, "hasCraftingRemainingItem() == false"));
            return;
        }
        ItemStack remainder = stack.getCraftingRemainingItem();
        boolean ok = remainder.is(expected) && remainder.getCount() == 1;
        out.accept(new Result(ok, name, remainder.getItem() + " x" + remainder.getCount()));
    }

    /**
     * 记录一条布尔断言。
     *
     * @param out  断言结果消费者
     * @param name 断言名
     * @param ok   是否通过
     */
    private static void check(Consumer<Result> out, String name, boolean ok) {
        out.accept(new Result(ok, name, ok ? "OK" : "FAILED"));
    }
}
