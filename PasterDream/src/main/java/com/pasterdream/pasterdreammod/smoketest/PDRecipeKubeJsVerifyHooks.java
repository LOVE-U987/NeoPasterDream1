package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.recipe.DreamBrewingRecipe;
import com.pasterdream.pasterdreammod.recipe.WeaponWorkshopRecipeInput;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;
import java.util.function.Consumer;

/**
 * KubeJS 配方联动运行时校验钩子（暗影高炉 shadow_blasting + 精铸工坊 weapon_forge
 * + 梦境炼药锅 dream_brewing）。
 * <p>
 * 专项套件（<b>不在 all 默认集合</b>；须 {@code PASTERDREAM_VERIFY_SUITES=kubejs-recipes}），
 * 依赖两个前提：
 * <ol>
 *   <li>dev 环境以 localRuntime 挂载 KubeJS（发布 jar 不携带）；</li>
 *   <li>{@code run/kubejs/server_scripts/pd_recipe_kubejs_test.js} 测试脚本在场。</li>
 * </ol>
 * 覆盖：RecipeType/Serializer 注册、数据包配方装载（weapon_forge ≥13 /
 * shadow_blasting ≥5 / dream_brewing ≥5）、
 * KubeJS 脚本配方注入（kubejs 命名空间在 RecipeManager 中可见）、
 * KubeJS 配方实际可匹配（getRecipeFor 命中脚本注册的高炉/工坊/炼药锅配方）。
 * 供 {@link PDPortingVerifyTest} 调用。
 */
public final class PDRecipeKubeJsVerifyHooks {

    /** 单条断言结果 */
    public record Result(boolean pass, String name, String detail) {
    }

    private PDRecipeKubeJsVerifyHooks() {
    }

    /**
     * 运行 KubeJS 配方联动全部校验
     *
     * @param player 服务端玩家（提供 server / level；可为 null，null 时跳过行为测）
     * @param out    断言输出
     */
    public static void verify(ServerPlayer player, Consumer<Result> out) {
        verifyRecipeTypes(out);
        verifyDatapackRecipes(player, out);
        boolean kubeJsLoaded = verifyKubeJsLoaded(out);
        if (kubeJsLoaded && player != null) {
            verifyKubeJsInjected(player, out);
            verifyKubeJsShadowBlastingMatch(player, out);
            verifyKubeJsWeaponForgeMatch(player, out);
            verifyKubeJsDreamBrewingMatch(player, out);
        }
    }

    // ==================== 注册与数据包 ====================

    /**
     * 断言两个配方类型的 DeferredHolder 均已注册就绪
     *
     * @param out 断言输出
     */
    private static void verifyRecipeTypes(Consumer<Result> out) {
        String missing = "";
        try {
            if (PDRecipeTypes.SHADOW_BLASTING.get() == null) {
                missing += " SHADOW_BLASTING";
            }
            if (PDRecipeTypes.SHADOW_BLASTING_SERIALIZER.get() == null) {
                missing += " SHADOW_BLASTING_SERIALIZER";
            }
            if (PDRecipeTypes.WEAPON_FORGE.get() == null) {
                missing += " WEAPON_FORGE";
            }
            if (PDRecipeTypes.WEAPON_FORGE_SERIALIZER.get() == null) {
                missing += " WEAPON_FORGE_SERIALIZER";
            }
        } catch (Exception e) {
            missing += " deferred:" + e.getClass().getSimpleName();
        }
        out.accept(new Result(missing.isEmpty(),
                "shadow_blasting / weapon_forge RecipeType+Serializer 注册齐全",
                missing.isEmpty() ? "ok" : "缺失" + missing));
    }

    /**
     * 断言数据包配方已装载：weapon_forge ≥ 13（重构的原硬编码配方表）、
     * shadow_blasting ≥ 5（模组自带数据包配方）
     *
     * @param player 服务端玩家
     * @param out    断言输出
     */
    private static void verifyDatapackRecipes(ServerPlayer player, Consumer<Result> out) {
        if (player == null) {
            out.accept(new Result(false, "数据包配方装载", "player == null"));
            return;
        }
        MinecraftServer server = player.server;
        int weaponForge = server.getRecipeManager().getAllRecipesFor(PDRecipeTypes.WEAPON_FORGE.get()).size();
        out.accept(new Result(weaponForge >= 13,
                "weapon_forge 数据包配方 ≥ 13",
                "actual=" + weaponForge));
        int shadowBlasting = server.getRecipeManager().getAllRecipesFor(PDRecipeTypes.SHADOW_BLASTING.get()).size();
        out.accept(new Result(shadowBlasting >= 5,
                "shadow_blasting 数据包配方 ≥ 5",
                "actual=" + shadowBlasting));
        int dreamBrewing = server.getRecipeManager().getAllRecipesFor(PDRecipeTypes.DREAM_BREWING.get()).size();
        out.accept(new Result(dreamBrewing >= 5,
                "dream_brewing 数据包配方 ≥ 5",
                "actual=" + dreamBrewing + "（dev 环境含 Spells 前置，5 条法术配方应全部解析）"));
    }

    // ==================== KubeJS 注入与匹配 ====================

    /**
     * 断言 KubeJS 已加载（dev 环境 localRuntime）；未加载时行为测跳过（pass=true）
     *
     * @param out 断言输出
     * @return KubeJS 是否在场
     */
    private static boolean verifyKubeJsLoaded(Consumer<Result> out) {
        boolean loaded = ModList.get().isLoaded("kubejs");
        out.accept(new Result(true, "KubeJS 可选前置检查",
                loaded ? "loaded（执行脚本配方断言）" : "未安装（跳过脚本配方断言）"));
        return loaded;
    }

    /**
     * 断言 KubeJS 测试脚本注册的配方已进入 RecipeManager
     * （按配方 id 命名空间 = kubejs 过滤；不锁定具体 id，由 schema uniqueId 自动生成）
     *
     * @param player 服务端玩家
     * @param out    断言输出
     */
    private static void verifyKubeJsInjected(ServerPlayer player, Consumer<Result> out) {
        long sbKube = countByNamespace(player.server, PDRecipeTypes.SHADOW_BLASTING.get(), "kubejs");
        out.accept(new Result(sbKube >= 2,
                "KubeJS 注入 shadow_blasting 配方 ≥ 2",
                "actual=" + sbKube));
        long wwKube = countByNamespace(player.server, PDRecipeTypes.WEAPON_FORGE.get(), "kubejs");
        out.accept(new Result(wwKube >= 2,
                "KubeJS 注入 weapon_forge 配方 ≥ 2",
                "actual=" + wwKube));
        long dbKube = countByNamespace(player.server, PDRecipeTypes.DREAM_BREWING.get(), "kubejs");
        out.accept(new Result(dbKube >= 2,
                "KubeJS 注入 dream_brewing 配方 ≥ 2",
                "actual=" + dbKube));
    }

    /**
     * 断言 KubeJS 注册的暗影高炉测试配方可被 getRecipeFor 匹配
     * （煤炭块 → 钻石，测试脚本配方 1）
     *
     * @param player 服务端玩家
     * @param out    断言输出
     */
    private static void verifyKubeJsShadowBlastingMatch(ServerPlayer player, Consumer<Result> out) {
        // shadow_blasting 配方类型绑定的输入是 SingleRecipeInput（仅匹配单格输入槽）
        net.minecraft.world.item.crafting.SingleRecipeInput input =
                new net.minecraft.world.item.crafting.SingleRecipeInput(new ItemStack(Items.COAL_BLOCK));
        var holder = player.server.getRecipeManager()
                .getRecipeFor(PDRecipeTypes.SHADOW_BLASTING.get(), input, player.serverLevel());
        boolean matched = holder.isPresent()
                && holder.get().value().getResultItem(player.server.registryAccess()).is(Items.DIAMOND);
        out.accept(new Result(matched, "KubeJS 高炉测试配方匹配：coal_block → diamond",
                matched ? "ok" : "未命中（检查 pd_recipe_kubejs_test.js 是否加载）"));
    }

    /**
     * 断言 KubeJS 注册的精铸工坊测试配方可被 getRecipeFor 匹配
     * （五色锭 → 下界合金碎片，测试脚本配方 1）
     *
     * @param player 服务端玩家
     * @param out    断言输出
     */
    private static void verifyKubeJsWeaponForgeMatch(ServerPlayer player, Consumer<Result> out) {
        ItemStackHandler handler = new ItemStackHandler(WeaponWorkshopRecipeInput.SLOT_COUNT);
        handler.setStackInSlot(0, new ItemStack(Items.IRON_INGOT));
        handler.setStackInSlot(1, new ItemStack(Items.GOLD_INGOT));
        handler.setStackInSlot(2, new ItemStack(Items.COPPER_INGOT));
        handler.setStackInSlot(3, new ItemStack(Items.DIAMOND));
        handler.setStackInSlot(4, new ItemStack(Items.EMERALD));
        WeaponWorkshopRecipeInput input = WeaponWorkshopRecipeInput.of(handler);
        var holder = player.server.getRecipeManager()
                .getRecipeFor(PDRecipeTypes.WEAPON_FORGE.get(), input, player.serverLevel());
        boolean matched = holder.isPresent()
                && holder.get().value().getResultItem(player.server.registryAccess()).is(Items.NETHERITE_SCRAP);
        out.accept(new Result(matched, "KubeJS 工坊测试配方匹配：五色锭 → netherite_scrap",
                matched ? "ok" : "未命中（检查 pd_recipe_kubejs_test.js 是否加载）"));
    }

    /**
     * 断言 KubeJS 注册的炼药锅测试配方可被 getRecipeFor 匹配
     * （药引瓶 + 苹果/金锭/荧石粉 → 金苹果，测试脚本配方 1，引导药剂走 codec 默认值）
     *
     * @param player 服务端玩家
     * @param out    断言输出
     */
    private static void verifyKubeJsDreamBrewingMatch(ServerPlayer player, Consumer<Result> out) {
        ItemStack guiding = itemOrAir("guiding_drug");
        DreamBrewingRecipe.DreamBrewingInput input = new DreamBrewingRecipe.DreamBrewingInput(
                guiding,
                new ItemStack(Items.APPLE),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.GLOWSTONE_DUST));
        var holder = player.server.getRecipeManager()
                .getRecipeFor(PDRecipeTypes.DREAM_BREWING.get(), input, player.serverLevel());
        boolean matched = holder.isPresent()
                && holder.get().value().getResultItem(player.server.registryAccess()).is(Items.GOLDEN_APPLE);
        out.accept(new Result(matched, "KubeJS 炼药锅测试配方匹配：药引+苹果/金锭/荧石粉 → golden_apple",
                matched ? "ok" : "未命中（检查 pd_recipe_kubejs_test.js 是否加载）"));
    }

    /**
     * 按注册名查找本模组物品（未注册返回空栈，供输入构造容错）
     *
     * @param path 注册路径
     * @return 物品栈（未注册为空栈）
     */
    private static ItemStack itemOrAir(String path) {
        return new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path))
                .orElse(Items.AIR));
    }

    // ==================== 工具 ====================

    /**
     * 统计指定配方类型下某命名空间的配方数量
     *
     * @param server 服务端
     * @param type   配方类型
     * @param nsName 命名空间（如 kubejs）
     * @return 配方数量
     */
    private static <I extends net.minecraft.world.item.crafting.RecipeInput,
            T extends net.minecraft.world.item.crafting.Recipe<I>> long countByNamespace(
            MinecraftServer server, net.minecraft.world.item.crafting.RecipeType<T> type, String nsName) {
        List<RecipeHolder<T>> all = server.getRecipeManager().getAllRecipesFor(type);
        return all.stream()
                .map(RecipeHolder::id)
                .map(ResourceLocation::getNamespace)
                .filter(nsName::equals)
                .count();
    }
}
