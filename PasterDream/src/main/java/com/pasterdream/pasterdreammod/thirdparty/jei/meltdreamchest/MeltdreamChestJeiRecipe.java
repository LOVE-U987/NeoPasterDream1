package com.pasterdream.pasterdreammod.thirdparty.jei.meltdreamchest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 融梦水晶箱 JEI 战利品条目（每池一条）。
 * <p>
 * 数据完全来自客户端资源包中的战利品表 JSON 实时解析（染梦主表 / 风旅客主表 /
 * 传说附加表），不在 Java 中硬编码任何物品与概率——数据包改动后 JEI 重载即自动同步，
 * 与 {@code ShadowBlastFurnaceJeiCategory} 的运行时数据模式一致。
 * <p>
 * 概率口径为"单次抽取概率"：{@code 该条目权重 / 池内全部条目权重之和}；
 * 空占位条目（{@code pasterdream:tabitem_1}，set_count 0）的权重计入分母但不展示，
 * 以还原"该池大概率不出实物"的真实概率。
 *
 * @author PasterDream
 * @see MeltdreamChestJeiCategory
 */
public final class MeltdreamChestJeiRecipe {

    /**
     * 单条掉落物。
     *
     * @param stack      物品栈（数量为 set_count 最小值）
     * @param chance     单次抽取概率（0..1，分母含空占位权重）
     * @param countText  数量范围文本（如 "×1"、"×4–8"）
     * @param quality    品质文本（取自 lang 的 {@code tooltip.pasterdream.<id>.quality}，缺失为 null）
     */
    public record Entry(ItemStack stack, double chance, String countText, Component quality) {
    }

    /** 池标题（本地化，如"染梦 · 饰品池"） */
    private final Component poolTitle;
    /** 池内掉落物列表 */
    private final List<Entry> entries;

    /**
     * 构造单池战利品条目
     *
     * @param poolTitle 池标题
     * @param entries   掉落物列表
     */
    private MeltdreamChestJeiRecipe(Component poolTitle, List<Entry> entries) {
        this.poolTitle = poolTitle;
        this.entries = entries;
    }

    /**
     * 获取池标题
     *
     * @return 池标题
     */
    public Component poolTitle() {
        return poolTitle;
    }

    /**
     * 获取掉落物列表
     *
     * @return 掉落物列表（不含空占位）
     */
    public List<Entry> entries() {
        return entries;
    }

    /**
     * 构建全部融梦水晶箱战利品条目（每池一条），JEI 每次重建运行时现解析。
     * <p>
     * 两级数据源：
     * <ol>
     *   <li>第一优先：逻辑服务端资源管理器（{@code getSingleplayerServer()}）——
     *       支持存档数据包覆盖；主菜单/联机客户端不可用；</li>
     *   <li>回退：模组 {@code SecureJar} 物理文件直读——实测部分环境下服务端资源管理器
     *       的 {@code getResource} 读不到模组 data/（同环境 vanilla 配方/战利品表加载正常，
     *       {@code AddReloadListenerEvent} 的 prepare 同样失败），物理路径不受 pack 机制影响；
     *       代价是不反映存档数据包覆盖（展示的是模组默认表）。</li>
     * </ol>
     * 全程 INFO/WARN 日志自证，便于 latest.log 定位。
     *
     * @return 战利品条目列表（主菜单 / 联机客户端为空列表）
     */
    public static List<MeltdreamChestJeiRecipe> build() {
        List<MeltdreamChestJeiRecipe> out = new ArrayList<>();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            PasterDreamMod.LOGGER.debug("[JEI] 客户端世界未就绪，融梦水晶箱战利品待 JEI 进存档重建后解析");
            return out;
        }
        var server = mc.getSingleplayerServer();
        if (server == null) {
            PasterDreamMod.LOGGER.debug("[JEI] 联机客户端无数据包资源（data/），融梦水晶箱战利品页不可用");
            return out;
        }
        // 路径必须带 loot_table/ 前缀：ResourceLocation 的 path 映射为 data/<namespace>/<path>.json，
        // registry 层的 chests/… 快捷 ID 会自动补目录，直接读文件不会
        collect(server.getResourceManager(), out,
                "loot_table/chests/loots_meltdream_chest_0", "jei.pasterdream.meltdream_chest.pool.dyedream");
        collect(server.getResourceManager(), out,
                "loot_table/chests/loots_meltdream_chest_1", "jei.pasterdream.meltdream_chest.pool.wind");
        collect(server.getResourceManager(), out,
                "loot_table/chests/loots_meltdream_chest_bonus_legendary", "jei.pasterdream.meltdream_chest.pool.bonus");
        if (out.isEmpty()) {
            PasterDreamMod.LOGGER.info("[JEI] 服务端资源管理器未读到融梦箱表，回退模组物理文件直读");
            readFromModFiles(out);
        }
        PasterDreamMod.LOGGER.info("[JEI] 融梦水晶箱战利品解析完成：{} 个池 / {} 件物品",
                out.size(), out.stream().mapToInt(r -> r.entries().size()).sum());
        return out;
    }

    /**
     * 回退路径：经类加载器（{@link Class#getResource}）直读本模组 classpath 资源——
     * dev 下 classpath 含 {@code build/resources/main}，prod 下模组 jar 由自身
     * classloader 加载，一个调用点覆盖两环境，完全不经过资源管理器 pack 机制。
     *
     * @param out 输出列表
     */
    private static void readFromModFiles(List<MeltdreamChestJeiRecipe> out) {
        String base = "/data/" + PasterDreamMod.MOD_ID + "/";
        readClasspath(out, base + "loot_table/chests/loots_meltdream_chest_0.json",
                "loot_table/chests/loots_meltdream_chest_0", "jei.pasterdream.meltdream_chest.pool.dyedream");
        readClasspath(out, base + "loot_table/chests/loots_meltdream_chest_1.json",
                "loot_table/chests/loots_meltdream_chest_1", "jei.pasterdream.meltdream_chest.pool.wind");
        readClasspath(out, base + "loot_table/chests/loots_meltdream_chest_bonus_legendary.json",
                "loot_table/chests/loots_meltdream_chest_bonus_legendary", "jei.pasterdream.meltdream_chest.pool.bonus");
    }

    /**
     * 经类加载器读取单张战利品表并解析
     *
     * @param out          输出列表
     * @param classpathPath classpath 绝对路径（如 {@code /data/pasterdream/loot_table/….json}）
     * @param tablePath    表资源路径（仅用于日志）
     * @param titleKeyBase 池名 lang 键前缀
     */
    private static void readClasspath(List<MeltdreamChestJeiRecipe> out, String classpathPath, String tablePath, String titleKeyBase) {
        var url = MeltdreamChestJeiRecipe.class.getResource(classpathPath);
        if (url == null) {
            PasterDreamMod.LOGGER.warn("[JEI] 融梦水晶箱战利品表在 classpath 中缺失: {}", classpathPath);
            return;
        }
        try (InputStream in = url.openStream()) {
            parseTable(in, out, tablePath, titleKeyBase);
        } catch (Exception e) {
            PasterDreamMod.LOGGER.warn("[JEI] 融梦水晶箱战利品表解析失败，跳过: {}", classpathPath, e);
        }
    }

    /**
     * 经服务端（数据包）资源管理器读取单张战利品表并解析。
     *
     * @param rm           逻辑服务端资源管理器
     * @param out          输出列表
     * @param tablePath    战利品表资源路径（如 {@code loot_table/chests/loots_meltdream_chest_0}）
     * @param titleKeyBase 池名 lang 键前缀（实际键 = 前缀 + "." + 池下标）
     */
    private static void collect(ResourceManager rm, List<MeltdreamChestJeiRecipe> out, String tablePath, String titleKeyBase) {
        ResourceLocation tableId = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, tablePath);
        Optional<Resource> resource = rm.getResource(tableId);
        if (resource.isEmpty()) {
            PasterDreamMod.LOGGER.warn("[JEI] 融梦水晶箱战利品表在服务端资源管理器中缺失: {}", tableId);
            return;
        }
        try (InputStream in = resource.get().open()) {
            parseTable(in, out, tablePath, titleKeyBase);
        } catch (Exception e) {
            PasterDreamMod.LOGGER.warn("[JEI] 融梦水晶箱战利品表解析失败，跳过: {}", tableId, e);
        }
    }

    /**
     * 解析战利品表输入流：每个池生成一条 {@link MeltdreamChestJeiRecipe} 加入输出列表
     *
     * @param in           表 JSON 输入流
     * @param out          输出列表
     * @param tablePath    表资源路径（仅用于日志）
     * @param titleKeyBase 池名 lang 键前缀
     */
    private static void parseTable(InputStream in, List<MeltdreamChestJeiRecipe> out, String tablePath, String titleKeyBase) {
        JsonObject root = JsonParser.parseReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        JsonArray pools = root.getAsJsonArray("pools");
        for (int i = 0; i < pools.size(); i++) {
            List<Entry> entries = parsePool(pools.get(i).getAsJsonObject());
            if (entries.isEmpty()) {
                continue;
            }
            Component title = Component.translatableWithFallback(
                    titleKeyBase + "." + i, "战利品池 " + (i + 1));
            out.add(new MeltdreamChestJeiRecipe(title, entries));
        }
    }

    /**
     * 解析单池条目：过滤空占位与非物品条目，按权重计算单次抽取概率。
     *
     * @param pool 池 JSON 对象
     * @return 掉落物列表（无有效条目时为空列表）
     */
    private static List<Entry> parsePool(JsonObject pool) {
        JsonArray arr = pool.getAsJsonArray("entries");
        // 第一轮：统计全部条目权重（含空占位 tabitem，保证概率口径真实）
        long totalWeight = 0;
        for (JsonElement e : arr) {
            JsonObject obj = e.getAsJsonObject();
            if (!isDisplayable(obj)) {
                continue;
            }
            totalWeight += weightOf(obj);
        }
        if (totalWeight <= 0) {
            return List.of();
        }
        // 第二轮：构造条目
        List<Entry> entries = new ArrayList<>(arr.size());
        for (JsonElement e : arr) {
            JsonObject obj = e.getAsJsonObject();
            if (!isDisplayable(obj)) {
                continue;
            }
            Item item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(obj.get("name").getAsString()))
                    .orElse(null);
            if (item == null) {
                continue;
            }
            long weight = weightOf(obj);
            double chance = (double) weight / totalWeight;
            int[] count = countRangeOf(obj);
            ItemStack stack = new ItemStack(item, Math.max(1, count[0]));
            String countText = count[0] == count[1] ? "×" + count[0] : "×" + count[0] + "–" + count[1];
            entries.add(new Entry(stack, chance, countText, qualityOf(item)));
        }
        return entries;
    }

    /**
     * 判断条目是否可展示：物品条目且非空占位（tabitem_1，set_count 0）。
     *
     * @param entry 条目 JSON 对象
     * @return 可展示返回 true
     */
    private static boolean isDisplayable(JsonObject entry) {
        if (!entry.has("name") || !entry.has("type")
                || !"minecraft:item".equals(entry.get("type").getAsString())) {
            return false;
        }
        return !"pasterdream:tabitem_1".equals(entry.get("name").getAsString());
    }

    /**
     * 读取条目权重（缺省 1）。
     *
     * @param entry 条目 JSON 对象
     * @return 权重
     */
    private static long weightOf(JsonObject entry) {
        return entry.has("weight") ? Math.max(1, entry.get("weight").getAsLong()) : 1;
    }

    /**
     * 读取 set_count 数量范围（兼容 {min,max} 对象、纯数字与无 function 三种写法；
     * function 名兼容 "set_count" 与 "minecraft:set_count"）。
     *
     * @param entry 条目 JSON 对象
     * @return {@code [min, max]}（无 function 时为 {@code [1, 1]}）
     */
    private static int[] countRangeOf(JsonObject entry) {
        if (!entry.has("functions")) {
            return new int[]{1, 1};
        }
        for (JsonElement e : entry.getAsJsonArray("functions")) {
            JsonObject fn = e.getAsJsonObject();
            String fnName = fn.has("function") ? fn.get("function").getAsString() : "";
            if (!fnName.equals("set_count") && !fnName.equals("minecraft:set_count")) {
                continue;
            }
            JsonElement count = fn.get("count");
            if (count != null && count.isJsonObject()) {
                int min = count.getAsJsonObject().has("min") ? count.getAsJsonObject().get("min").getAsInt() : 1;
                int max = count.getAsJsonObject().has("max") ? count.getAsJsonObject().get("max").getAsInt() : min;
                return new int[]{min, max};
            }
            if (count != null) {
                int v = count.getAsInt();
                return new int[]{v, v};
            }
        }
        return new int[]{1, 1};
    }

    /**
     * 读取物品品质文本（lang 键 {@code tooltip.pasterdream.<路径>.quality}，缺失返回 null）。
     *
     * @param item 物品
     * @return 品质 Component（未定义时为 null）
     */
    private static Component qualityOf(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        String key = "tooltip.pasterdream." + id.getPath() + ".quality";
        if (!Language.getInstance().has(key)) {
            return null;
        }
        // 品质行追加灰色弱化前缀，与物品本体 tooltip 视觉区分
        return Component.translatable(key).withStyle(ChatFormatting.GRAY);
    }
}
