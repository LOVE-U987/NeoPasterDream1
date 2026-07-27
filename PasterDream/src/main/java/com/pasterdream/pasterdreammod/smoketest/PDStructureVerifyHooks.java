package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.PDStructureBlock;
import com.pasterdream.pasterdreammod.block.entity.DyedreamDeskBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDRuinsRegistration;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * 世界结构生成子系统运行时校验钩子（供 {@link PDPortingVerifyTest} 调用）。
 * <p>
 * 覆盖面：
 * <ol>
 *     <li>数据包 STRUCTURE / STRUCTURE_SET / TEMPLATE_POOL 加载规模</li>
 *     <li>{@link PDRuinsRegistration} RuinAPI 注册表</li>
 *     <li>{@code structure_block_0..23} 方块注册与 SPECS 模板 NBT 可加载</li>
 *     <li>关键 NBT 模板尺寸非空</li>
 *     <li>确定性结构方块（structure_block_1）放置后生成并自毁</li>
 *     <li>直接 {@code placeInWorld} 小模板（crystal_ball_0）验证模板管线</li>
 * </ol>
 */
public final class PDStructureVerifyHooks {

    /** 单条断言结果 */
    public record Result(boolean pass, String name, String detail) {
    }

    /**
     * SPECS / 主路径代表性模板（须存在于 {@code data/pasterdream/structure/*.nbt}）
     */
    private static final String[] CRITICAL_TEMPLATES = {
            "dream_train",
            "dream_train_platform",
            "dream_church_0",
            "dream_church_8",
            "dream_church_9",
            "dream_church_10",
            "dyedream_laboratory_0",
            "pinkagaric_house_0",
            "dyedream_worldtree",
            "dyedream_worldtree_true",
            "shadow_world_door",
            "shadow_dungeon_door",
            "shadow_dungeon_0",
            "windmoor_tree_0",
            "crystal_ball_0",
            "desert_fortress",
            "christmas_tree_0",
            "aaroncos_arena_portals",
    };

    /**
     * 数据包配置结构（须出现在 STRUCTURE 注册表）
     */
    private static final String[] CRITICAL_STRUCTURES = {
            "dream_train",
            "dream_church_0",
            "dream_church_8",
            "dream_church_9",
            "dream_church_10",
            "dyedream_laboratory_0",
            "pinkagaric_house_0",
            "dyedream_worldtree_0",
            "dyedream_worldtree_1",
            "shadow_dungeon",
            "windmoor_tree_0",
            "aaroncos_arena_portals",
            "desert_cottage_0",
    };

    private PDStructureVerifyHooks() {
    }

    /**
     * 运行全部结构校验，通过 consumer 回传每条断言（与 verify 主框架 {@code checkDetail} 对接）。
     *
     * @param server 服务端
     * @param player 玩家（用于取附近平坦放置点；可为 null，则跳过放置行为测）
     * @param out    断言输出
     */
    public static void verify(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        verifyDatapackRegistries(server, out);
        verifyRuinApi(out);
        verifyStructureBlocksRegistered(out);
        verifySpecsTemplatesLoadable(server, out);
        verifyCriticalTemplates(server, out);
        verifyCriticalStructuresPresent(server, out);
        if (player != null) {
            verifyStructureBlockPlacement(player.serverLevel(), player, out);
            verifyDirectTemplatePlace(player.serverLevel(), player, out);
            verifyStructureContainerContents(player.serverLevel(), player, out);
        } else {
            out.accept(new Result(false, "结构放置行为测跳过", "player == null"));
        }
    }

    // ==================== 数据包注册表 ====================

    private static void verifyDatapackRegistries(MinecraftServer server, Consumer<Result> out) {
        Set<String> structures = modPaths(server, Registries.STRUCTURE);
        Set<String> sets = modPaths(server, Registries.STRUCTURE_SET);
        Set<String> pools = modPaths(server, Registries.TEMPLATE_POOL);

        // 资源目录统计：structure 114 / structure_set 114 / template_pool 114（允许少量 JSON 未进表）
        out.accept(detail(structures.size() >= 100,
                "STRUCTURE 注册加载 " + structures.size(),
                "期望 ≥100（资源目录 114）"));
        out.accept(detail(sets.size() >= 100,
                "STRUCTURE_SET 注册加载 " + sets.size(),
                "期望 ≥100（资源目录 114）"));
        out.accept(detail(pools.size() >= 100,
                "TEMPLATE_POOL 注册加载 " + pools.size(),
                "期望 ≥100（资源目录 114）"));
    }

    private static Set<String> modPaths(MinecraftServer server,
                                        net.minecraft.resources.ResourceKey<? extends net.minecraft.core.Registry<?>> key) {
        Set<String> paths = new TreeSet<>();
        server.registryAccess().registry(key).ifPresent(reg -> reg.keySet().forEach(rl -> {
            if (PasterDreamMod.MOD_ID.equals(rl.getNamespace())) {
                paths.add(rl.getPath());
            }
        }));
        return paths;
    }

    // ==================== RuinAPI ====================

    private static void verifyRuinApi(Consumer<Result> out) {
        var all = PDRuinsRegistration.getAllRegisteredStructures();
        // 含 dream_church_0~10 全 11 变体 + worldtree_0/1 → ≥42
        out.accept(detail(all.size() >= 42,
                "RuinAPI 注册遗迹 " + all.size(),
                "期望 ≥42（含教堂 0–10、世界树 0/1）"));
        for (String name : List.of("dream_train", "dyedream_worldtree_0", "dyedream_worldtree_1", "pinkagaric_house_0",
                "dream_church_0", "dream_church_8", "dream_church_9", "dream_church_10",
                "aaroncos_arena_portals", "desert_cottage_0")) {
            boolean ok = PDRuinsRegistration.getRegisteredStructure(name) != null;
            out.accept(detail(ok, "RuinAPI 含 " + name, ok ? "present" : "missing"));
        }
    }

    // ==================== structure_block 注册 ====================

    private static void verifyStructureBlocksRegistered(Consumer<Result> out) {
        List<String> missing = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            ResourceLocation id = rl("structure_block_" + i);
            if (!BuiltInRegistries.BLOCK.containsKey(id)) {
                missing.add("block:" + i);
            }
            if (!BuiltInRegistries.ITEM.containsKey(id)) {
                missing.add("item:" + i);
            }
            if (PDStructureBlock.SPECS.get(i) == null) {
                missing.add("spec:" + i);
            }
        }
        // Deferred 列表长度
        if (PDBlocksStructure.STRUCTURE_BLOCKS.size() != 24) {
            missing.add("listSize=" + PDBlocksStructure.STRUCTURE_BLOCKS.size());
        }
        out.accept(detail(missing.isEmpty(),
                "structure_block_0..23 方块/物品/SPECS 齐全",
                missing.isEmpty() ? "24/24" : "缺失 " + missing));
    }

    // ==================== SPECS 模板可加载 ====================

    private static void verifySpecsTemplatesLoadable(MinecraftServer server, Consumer<Result> out) {
        StructureTemplateManager mgr = server.getStructureManager();
        Set<String> required = new LinkedHashSet<>();
        for (PDStructureBlock.Spec spec : PDStructureBlock.SPECS.values()) {
            for (PDStructureBlock.Placement p : spec.placements()) {
                required.add(p.template());
            }
        }
        List<String> missing = new ArrayList<>();
        List<String> empty = new ArrayList<>();
        for (String name : required) {
            Optional<StructureTemplate> opt = loadTemplate(mgr, name);
            if (opt.isEmpty()) {
                missing.add(name);
            } else if (opt.get().getSize().getX() <= 0 || opt.get().getSize().getY() <= 0) {
                empty.add(name + sizeOf(opt.get()));
            }
        }
        out.accept(detail(missing.isEmpty() && empty.isEmpty(),
                "SPECS 引用模板可加载 " + (required.size() - missing.size() - empty.size()) + "/" + required.size(),
                summarize(missing, empty)));
    }

    private static void verifyCriticalTemplates(MinecraftServer server, Consumer<Result> out) {
        StructureTemplateManager mgr = server.getStructureManager();
        List<String> missing = new ArrayList<>();
        List<String> empty = new ArrayList<>();
        for (String name : CRITICAL_TEMPLATES) {
            Optional<StructureTemplate> opt = loadTemplate(mgr, name);
            if (opt.isEmpty()) {
                missing.add(name);
            } else if (opt.get().getSize().getX() <= 0) {
                empty.add(name);
            }
        }
        out.accept(detail(missing.isEmpty() && empty.isEmpty(),
                "关键 NBT 模板可加载 " + (CRITICAL_TEMPLATES.length - missing.size() - empty.size())
                        + "/" + CRITICAL_TEMPLATES.length,
                summarize(missing, empty)));
    }

    private static void verifyCriticalStructuresPresent(MinecraftServer server, Consumer<Result> out) {
        Set<String> structures = modPaths(server, Registries.STRUCTURE);
        List<String> missing = new ArrayList<>();
        for (String name : CRITICAL_STRUCTURES) {
            if (!structures.contains(name)) {
                missing.add(name);
            }
        }
        out.accept(detail(missing.isEmpty(),
                "关键 STRUCTURE 配置在场 " + (CRITICAL_STRUCTURES.length - missing.size())
                        + "/" + CRITICAL_STRUCTURES.length,
                missing.isEmpty() ? "全部在场" : "缺失: " + missing));
    }

    // ==================== 行为：structure_block_1 放置 ====================

    /**
     * structure_block_1 无随机、单模板 {@code dyedream_laboratory_0}，onPlace 同步 generate 后自毁为空气。
     */
    private static void verifyStructureBlockPlacement(ServerLevel level, ServerPlayer player, Consumer<Result> out) {
        BlockPos base = placementAnchor(level, player, 48, 0);
        // 抬到地表以上，避免埋进基岩层
        BlockPos pos = new BlockPos(base.getX(),
                level.getHeight(Heightmap.Types.MOTION_BLOCKING, base.getX(), base.getZ()) + 2,
                base.getZ());

        // 先清一块空腔，便于统计结构写入的方块
        clearBox(level, pos.offset(-6, -1, -6), pos.offset(12, 10, 12));

        BlockState state = PDBlocksStructure.STRUCTURE_BLOCK_1.get().defaultBlockState();
        boolean setOk = level.setBlock(pos, state, 3);
        // onPlace → generate 已同步执行
        boolean selfDestructed = level.getBlockState(pos).isAir();
        int filled = countNonAir(level, pos.offset(-6, -1, -6), pos.offset(12, 10, 12));

        out.accept(detail(setOk, "structure_block_1 setBlock 成功", "pos=" + pos.toShortString()));
        out.accept(detail(selfDestructed,
                "structure_block_1 生成后自毁为空气",
                "state=" + BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock())));
        out.accept(detail(filled > 0,
                "structure_block_1 写入结构方块 " + filled + " 个",
                "模板 dyedream_laboratory_0，扫描盒非空气计数"));

        // 清理，避免污染后续测例
        clearBox(level, pos.offset(-6, -1, -6), pos.offset(12, 10, 12));
    }

    // ==================== 行为：直接 placeInWorld ====================

    private static void verifyDirectTemplatePlace(ServerLevel level, ServerPlayer player, Consumer<Result> out) {
        BlockPos base = placementAnchor(level, player, 64, 16);
        BlockPos pos = new BlockPos(base.getX(),
                level.getHeight(Heightmap.Types.MOTION_BLOCKING, base.getX(), base.getZ()) + 2,
                base.getZ());
        clearBox(level, pos.offset(-4, 0, -4), pos.offset(8, 8, 8));

        Optional<StructureTemplate> opt = loadTemplate(level.getStructureManager(), "crystal_ball_0");
        if (opt.isEmpty()) {
            out.accept(new Result(false, "crystal_ball_0 模板加载", "empty"));
            return;
        }
        StructureTemplate template = opt.get();
        boolean placed = template.placeInWorld(level, pos, pos,
                new StructurePlaceSettings().setIgnoreEntities(true),
                level.random, 3);
        int filled = countNonAir(level, pos.offset(-4, 0, -4), pos.offset(8, 8, 8));
        out.accept(detail(placed && filled > 0,
                "直接 placeInWorld crystal_ball_0",
                "placed=" + placed + " filled=" + filled + " size=" + sizeOf(template)));

        clearBox(level, pos.offset(-4, 0, -4), pos.offset(8, 8, 8));
    }

    // ==================== 行为：结构内容（书桌成书 / 箱子 LootTable） ====================

    /**
     * garden_decryption_0：书桌预置 written_book（Items）；
     * dyedream_campsite_0：原版箱 LootTable。
     * 校验结构放置后自定义 BE 能吃到 Items、原版容器仍挂战利品表。
     */
    private static void verifyStructureContainerContents(ServerLevel level, ServerPlayer player, Consumer<Result> out) {
        // --- 书桌成书 ---
        BlockPos deskBase = placementAnchor(level, player, 80, 32);
        BlockPos deskOrigin = new BlockPos(deskBase.getX(),
                level.getHeight(Heightmap.Types.MOTION_BLOCKING, deskBase.getX(), deskBase.getZ()) + 2,
                deskBase.getZ());
        clearBox(level, deskOrigin.offset(-2, -1, -2), deskOrigin.offset(12, 18, 12));

        Optional<StructureTemplate> garden = loadTemplate(level.getStructureManager(), "garden_decryption_0");
        if (garden.isEmpty()) {
            out.accept(new Result(false, "garden_decryption_0 模板加载(内容测)", "empty"));
        } else {
            garden.get().placeInWorld(level, deskOrigin, deskOrigin,
                    new StructurePlaceSettings().setIgnoreEntities(true),
                    level.random, 3);
            int bookDesks = 0;
            int desks = 0;
            BlockPos min = deskOrigin.offset(-1, -1, -1);
            BlockPos max = deskOrigin.offset(10, 16, 10);
            for (BlockPos p : BlockPos.betweenClosed(min, max)) {
                BlockEntity be = level.getBlockEntity(p);
                if (be instanceof DyedreamDeskBlockEntity desk) {
                    desks++;
                    if (desk.getItemHandler().getStackInSlot(0).is(Items.WRITTEN_BOOK)) {
                        bookDesks++;
                    }
                }
            }
            out.accept(detail(desks > 0 && bookDesks > 0,
                    "结构书桌保留成书 " + bookDesks + "/" + desks,
                    "garden_decryption_0 dyedream_desk Items→inventory"));
        }
        clearBox(level, deskOrigin.offset(-2, -1, -2), deskOrigin.offset(12, 18, 12));

        // --- 原版箱 LootTable ---
        BlockPos campBase = placementAnchor(level, player, 96, 48);
        BlockPos campOrigin = new BlockPos(campBase.getX(),
                level.getHeight(Heightmap.Types.MOTION_BLOCKING, campBase.getX(), campBase.getZ()) + 2,
                campBase.getZ());
        clearBox(level, campOrigin.offset(-2, -1, -2), campOrigin.offset(18, 20, 18));

        Optional<StructureTemplate> camp = loadTemplate(level.getStructureManager(), "dyedream_campsite_0");
        if (camp.isEmpty()) {
            out.accept(new Result(false, "dyedream_campsite_0 模板加载(内容测)", "empty"));
        } else {
            camp.get().placeInWorld(level, campOrigin, campOrigin,
                    new StructurePlaceSettings().setIgnoreEntities(true),
                    level.random, 3);
            int lootChests = 0;
            BlockPos min = campOrigin.offset(-1, -1, -1);
            BlockPos max = campOrigin.offset(16, 18, 16);
            for (BlockPos p : BlockPos.betweenClosed(min, max)) {
                BlockEntity be = level.getBlockEntity(p);
                if (be instanceof RandomizableContainerBlockEntity container && container.getLootTable() != null) {
                    lootChests++;
                }
            }
            out.accept(detail(lootChests > 0,
                    "结构原版箱保留 LootTable ×" + lootChests,
                    "dyedream_campsite_0 chests/loots_relic_0"));
        }
        clearBox(level, campOrigin.offset(-2, -1, -2), campOrigin.offset(18, 20, 18));
    }

    // ==================== 工具 ====================

    private static Optional<StructureTemplate> loadTemplate(StructureTemplateManager mgr, String path) {
        ResourceLocation id = rl(path);
        // 1.21：get 返回 Optional；部分路径也可能被 getOrCreate 成空壳，故以尺寸二次校验
        Optional<StructureTemplate> opt = mgr.get(id);
        if (opt.isPresent()) {
            return opt;
        }
        try {
            StructureTemplate created = mgr.getOrCreate(id);
            if (created != null && created.getSize().getX() > 0) {
                return Optional.of(created);
            }
        } catch (Exception ignored) {
            // fall through
        }
        return Optional.empty();
    }

    private static BlockPos placementAnchor(ServerLevel level, ServerPlayer player, int dx, int dz) {
        BlockPos p = player.blockPosition().offset(dx, 0, dz);
        // 强制加载区块，避免模板写入未加载区
        level.getChunk(p);
        return p;
    }

    private static void clearBox(ServerLevel level, BlockPos min, BlockPos max) {
        for (BlockPos p : BlockPos.betweenClosed(min, max)) {
            if (!level.getBlockState(p).isAir()) {
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private static int countNonAir(ServerLevel level, BlockPos min, BlockPos max) {
        int n = 0;
        for (BlockPos p : BlockPos.betweenClosed(min, max)) {
            if (!level.getBlockState(p).isAir()) {
                n++;
            }
        }
        return n;
    }

    private static String sizeOf(StructureTemplate t) {
        var s = t.getSize();
        return s.getX() + "x" + s.getY() + "x" + s.getZ();
    }

    private static String summarize(List<String> missing, List<String> empty) {
        if (missing.isEmpty() && empty.isEmpty()) {
            return "全部 OK";
        }
        StringBuilder sb = new StringBuilder();
        if (!missing.isEmpty()) {
            sb.append("缺失 ").append(missing.subList(0, Math.min(8, missing.size())));
        }
        if (!empty.isEmpty()) {
            if (!sb.isEmpty()) {
                sb.append("；");
            }
            sb.append("空壳 ").append(empty.subList(0, Math.min(8, empty.size())));
        }
        return sb.toString();
    }

    private static Result detail(boolean ok, String name, String detail) {
        return new Result(ok, name, detail);
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path);
    }
}
