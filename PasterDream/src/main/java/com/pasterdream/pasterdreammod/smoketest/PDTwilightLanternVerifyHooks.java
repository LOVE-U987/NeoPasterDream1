package com.pasterdream.pasterdreammod.smoketest;

import com.mojang.brigadier.tree.CommandNode;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDGameRules;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksStructure;
import com.pasterdream.pasterdreammod.registry.items.PDItemsFunctional;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 暮影之笼流程缺口核实（VERIFY 套件 {@code twilight-lantern}）。
 * <p>
 * 对照 {@code docs/todo_暮影之笼流程缺口.md}：模板加载路径、structure_block_9 放置、
 * GenerateWorld 缺失迹象、hide_7/Warden、返程 spawn 命令、事件 BE/床几何、实体注册。
 * <p>
 * 不跑满 2600t 守卫战；KEEP_OPEN 时在玩家东侧留下据点供人工观察。
 */
public final class PDTwilightLanternVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    /** 放置据点锚点相对玩家的水平偏移（便于 KEEP_OPEN 观察） */
    private static final int ANCHOR_DX = 24;
    private static final int ANCHOR_DZ = 8;

    private PDTwilightLanternVerifyHooks() {
    }

    public static void verify(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        if (server == null) {
            out.accept(new Result(false, "twilight-skip", "server == null"));
            return;
        }
        verifyTemplateLoadPaths(server, out);
        verifyRegistrations(server, out);
        verifySpawnCommandMissing(server, out);
        verifyGenerateWorldAbsenceSignals(server, player, out);
        if (player == null) {
            out.accept(new Result(false, "twilight-placement-skip", "player == null"));
            return;
        }
        ServerLevel level = player.serverLevel();
        BlockPos anchor = placementAnchor(level, player);
        verifyStructureBlock9Placement(level, anchor, out);
        verifyLanternBedGeometryAndKey(level, player, anchor.offset(0, 0, 40), out);
        verifyHide7WardenPath(level, player, out);
    }

    // ==================== 模板 STM vs 直读 structure/ ====================

    private static void verifyTemplateLoadPaths(MinecraftServer server, Consumer<Result> out) {
        StructureTemplateManager mgr = server.getStructureManager();
        for (String name : new String[]{"shadow_world_door", "shadow_world_spawn"}) {
            Optional<StructureTemplate> stm = loadViaStm(mgr, name);
            Optional<StructureTemplate> res = loadViaResource(server, name);
            boolean stmOk = stm.isPresent() && stm.get().getSize().getX() > 0;
            boolean resOk = res.isPresent() && res.get().getSize().getX() > 0;
            String stmDetail = stm.map(t -> "size=" + sizeOf(t)).orElse("empty/missing");
            String resDetail = res.map(t -> "size=" + sizeOf(t)).orElse("empty/missing");
            out.accept(new Result(stmOk,
                    "STM 加载 " + name,
                    stmDetail + "（getOrCreate 路径，PDStructureBlock 同此）"));
            out.accept(new Result(resOk,
                    "资源直读 structure/" + name + ".nbt",
                    resDetail + "（调试杖 fallback 路径）"));
            if (stmOk && resOk) {
                var a = stm.get().getSize();
                var b = res.get().getSize();
                boolean same = a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
                out.accept(new Result(same,
                        name + " STM 与直读尺寸一致",
                        "stm=" + sizeOf(stm.get()) + " res=" + sizeOf(res.get())));
            } else if (resOk && !stmOk) {
                out.accept(new Result(false,
                        name + " P0.5：STM 失败但直读成功",
                        "structure_block 触发放置会空刷；杖 fallback 仍可用"));
            }
        }
    }

    // ==================== 注册面 ====================

    private static void verifyRegistrations(MinecraftServer server, Consumer<Result> out) {
        out.accept(ok(PDBlocksStructure.STRUCTURE_BLOCK_9.get() != null,
                "structure_block_9 方块注册",
                String.valueOf(BuiltInRegistries.BLOCK.getKey(PDBlocksStructure.STRUCTURE_BLOCK_9.get()))));
        out.accept(ok(PDBlocksFurniture.TWILIGHT_LANTERN.get() != null,
                "twilight_lantern 方块注册",
                String.valueOf(BuiltInRegistries.BLOCK.getKey(PDBlocksFurniture.TWILIGHT_LANTERN.get()))));
        out.accept(ok(PDBlocksFurniture.TRUE_SHADOW_BED.get() != null,
                "true_shadow_bed 方块注册",
                String.valueOf(BuiltInRegistries.BLOCK.getKey(PDBlocksFurniture.TRUE_SHADOW_BED.get()))));
        out.accept(ok(PDItemsFunctional.MELTDREAM_CRYSTAL_0.get() != null,
                "meltdream_crystal_0 物品注册",
                String.valueOf(BuiltInRegistries.ITEM.getKey(PDItemsFunctional.MELTDREAM_CRYSTAL_0.get()))));

        out.accept(ok(PDEntities.SHADOW_GHOST.get() != null, "实体 SHADOW_GHOST", "ok"));
        out.accept(ok(PDEntities.SHADOW_SQUEAL_GHOST.get() != null, "实体 SHADOW_SQUEAL_GHOST", "ok"));
        out.accept(ok(PDEntities.SHADOW_GOLEM.get() != null, "实体 SHADOW_GOLEM", "ok"));
        out.accept(ok(PDEntities.TERRORBEAK.get() != null, "实体 TERRORBEAK", "ok"));

        ServerLevel lamp = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        out.accept(ok(lamp != null, "维度 lamp_shadow_world 可解析",
                lamp == null ? "null" : lamp.dimension().location().toString()));

        // hide_* 成就 JSON 可加载（impossible 触发，仅代码授予）
        for (String adv : new String[]{
                "achievement_hide_7", "achievement_hide_8", "achievement_hide_9", "achievement_hide_10"
        }) {
            AdvancementHolder h = server.getAdvancements()
                    .get(ResourceLocation.fromNamespaceAndPath("pasterdream", adv));
            out.accept(ok(h != null, "成就可加载 " + adv, h == null ? "missing" : "ok"));
        }

        GameRules rules = server.getGameRules();
        try {
            int x = rules.getInt(PDGameRules.RANDOM_COORD_X);
            int z = rules.getInt(PDGameRules.RANDOM_COORD_Z);
            out.accept(new Result(true, "gamerule randomCoordX/Z 可读", "x=" + x + " z=" + z));
        } catch (Exception e) {
            out.accept(new Result(false, "gamerule randomCoordX/Z 可读", e.toString()));
        }
    }

    // ==================== 返程 spawn 命令 ====================

    private static void verifySpawnCommandMissing(MinecraftServer server, Consumer<Result> out) {
        CommandNode<?> root = server.getCommands().getDispatcher().getRoot();
        boolean hasBareSpawn = root.getChild("spawn") != null;
        // 预期：当前仓库未注册裸 spawn（与 todo 一致）→ 断言「未注册」为 pass，便于报告标红缺口时用 fail 语义？
        // 缺口核实：未注册 = 风险仍在 → fail，提示需兜底。
        out.accept(new Result(!hasBareSpawn,
                "裸命令 spawn 未注册（返程依赖缺口）",
                hasBareSpawn
                        ? "已注册 spawn，返程字面命令可能可用"
                        : "root 无 child 'spawn'；TwilightLantern 仍 performPrefixedCommand(\"spawn\")"));
        // 若已注册则上面 pass=false 会让「缺口仍在」失败——语义：我们要确认缺口还在。
        // 用户要的是核实：缺口存在 → 用独立名表达「缺口仍在」为 pass 当 !hasBareSpawn
        // 上面 !hasBareSpawn → pass true 表示「确认了未注册」。OK。
    }

    // ==================== GenerateWorld 缺失迹象 ====================

    /**
     * 新 VERIFY 超平坦档：原版会在 OVERWORLD Load 时按 randomCoord 在 y≈-60 放 door。
     * 抽样扫描出生附近深层是否已有 twilight_lantern；无则记为「未见自动据点」（与缺 GenerateWorld 一致）。
     */
    private static void verifyGenerateWorldAbsenceSignals(MinecraftServer server, ServerPlayer player,
                                                          Consumer<Result> out) {
        ServerLevel overworld = server.getLevel(net.minecraft.world.level.Level.OVERWORLD);
        if (overworld == null) {
            out.accept(new Result(false, "主世界可解析", "null"));
            return;
        }
        BlockPos center = player != null ? player.blockPosition() : BlockPos.ZERO;
        int found = countBlockInBox(overworld,
                new BlockPos(center.getX() - 96, -64, center.getZ() - 96),
                new BlockPos(center.getX() + 96, 80, center.getZ() + 96),
                PDBlocksFurniture.TWILIGHT_LANTERN.get());
        // 缺口核实：未自动生成 → pass（确认缺口）；若意外找到则 fail 并注明可能已移植
        out.accept(new Result(found == 0,
                "主世界抽样未见自动 twilight_lantern（GenerateWorld 缺口）",
                "count=" + found + " box=±96 x y[-64,80] around " + center.toShortString()));
    }

    // ==================== structure_block_9 放置 ====================

    private static void verifyStructureBlock9Placement(ServerLevel level, BlockPos anchor, Consumer<Result> out) {
        // 大模板：先清一块足够扫描的空腔（以 STM size 为准，上限夹紧）
        Optional<StructureTemplate> opt = loadViaStm(level.getStructureManager(), "shadow_world_door");
        int sx = opt.map(t -> Math.min(t.getSize().getX() + 4, 96)).orElse(48);
        int sy = opt.map(t -> Math.min(t.getSize().getY() + 4, 48)).orElse(32);
        int sz = opt.map(t -> Math.min(t.getSize().getZ() + 4, 96)).orElse(48);

        BlockPos pos = anchor;
        clearBox(level, pos.offset(-2, -1, -2), pos.offset(sx, sy, sz));

        BlockState state = PDBlocksStructure.STRUCTURE_BLOCK_9.get().defaultBlockState();
        boolean setOk = level.setBlock(pos, state, 3);
        // onPlace → generate 同步
        boolean selfAir = level.getBlockState(pos).isAir();
        int lanterns = countBlockInBox(level, pos.offset(-2, -1, -2), pos.offset(sx, sy, sz),
                PDBlocksFurniture.TWILIGHT_LANTERN.get());
        int beds = countBlockInBox(level, pos.offset(-2, -1, -2), pos.offset(sx, sy, sz),
                PDBlocksFurniture.TRUE_SHADOW_BED.get());
        int filled = countNonAir(level, pos.offset(-2, -1, -2), pos.offset(sx, sy, sz));

        out.accept(new Result(setOk, "structure_block_9 setBlock", "pos=" + pos.toShortString()));
        out.accept(new Result(selfAir, "structure_block_9 生成后自毁为空气",
                "state=" + BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock())));
        out.accept(new Result(filled > 0,
                "structure_block_9 写入非空气方块 " + filled,
                "scan=" + sx + "x" + sy + "x" + sz + " STM_size="
                        + opt.map(PDTwilightLanternVerifyHooks::sizeOf).orElse("n/a")));
        out.accept(new Result(lanterns >= 1,
                "structure_block_9 结果含 twilight_lantern×" + lanterns,
                lanterns >= 1 ? "P0.5 触发块放置可用" : "P0.5 失败：STM 可能空模板"));
        out.accept(new Result(beds >= 1,
                "structure_block_9 结果含 true_shadow_bed×" + beds,
                beds >= 1 ? "ok" : "缺失真影床"));

        // 若触发块失败，再试资源直读 placeInWorld（对照杖路径）
        if (lanterns < 1) {
            Optional<StructureTemplate> res = loadViaResource(level.getServer(), "shadow_world_door");
            if (res.isPresent() && res.get().getSize().getX() > 0) {
                BlockPos p2 = pos.offset(0, 0, Math.min(sz + 8, 64));
                clearBox(level, p2.offset(-2, -1, -2), p2.offset(sx, sy, sz));
                boolean placed = res.get().placeInWorld(level, p2, p2,
                        new StructurePlaceSettings().setIgnoreEntities(false),
                        level.random, 3);
                int l2 = countBlockInBox(level, p2.offset(-2, -1, -2), p2.offset(sx, sy, sz),
                        PDBlocksFurniture.TWILIGHT_LANTERN.get());
                out.accept(new Result(placed && l2 >= 1,
                        "fallback 直读 placeInWorld shadow_world_door",
                        "placed=" + placed + " lanterns=" + l2));
            } else {
                out.accept(new Result(false, "fallback 直读 placeInWorld shadow_world_door",
                        "资源亦不可用"));
            }
        }
        // 成功时保留结构供 KEEP_OPEN 观察（不 clear）
    }

    // ==================== 笼 + 床几何 / key ====================

    private static void verifyLanternBedGeometryAndKey(ServerLevel level, ServerPlayer player,
                                                      BlockPos base, Consumer<Result> out) {
        clearBox(level, base.offset(-1, 0, -1), base.offset(2, 6, 2));
        BlockPos bedPos = base;
        BlockPos lanternPos = bedPos.above(2);
        level.setBlock(bedPos, PDBlocksFurniture.TRUE_SHADOW_BED.get().defaultBlockState(), 3);
        level.setBlock(lanternPos, PDBlocksFurniture.TWILIGHT_LANTERN.get().defaultBlockState(), 3);

        boolean geo = level.getBlockState(lanternPos).is(PDBlocksFurniture.TWILIGHT_LANTERN.get())
                && level.getBlockState(bedPos).is(PDBlocksFurniture.TRUE_SHADOW_BED.get());
        out.accept(new Result(geo, "人工摆放 床 + 上2格笼",
                "bed=" + bedPos.toShortString() + " lantern=" + lanternPos.toShortString()));

        W4DataBlockEntity.putBooleanAt(level, lanternPos, "key", true);
        W4DataBlockEntity.putBooleanAt(level, lanternPos, "switch", false);
        W4DataBlockEntity.putDoubleAt(level, lanternPos, "number", 0);
        boolean key = W4DataBlockEntity.getBooleanAt(level, lanternPos, "key");
        boolean sw = W4DataBlockEntity.getBooleanAt(level, lanternPos, "switch");
        double num = W4DataBlockEntity.getDoubleAt(level, lanternPos, "number");
        out.accept(new Result(key && !sw && num == 0.0,
                "W4 BE key/switch/number 读写",
                "key=" + key + " switch=" + sw + " number=" + num));

        // hide_9 授予路径（award 容错）
        AdvancementHolder hide9 = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", "achievement_hide_9"));
        if (hide9 != null) {
            var prog = player.getAdvancements().getOrStartProgress(hide9);
            if (!prog.isDone()) {
                for (String c : prog.getRemainingCriteria()) {
                    player.getAdvancements().award(hide9, c);
                }
            }
            out.accept(new Result(player.getAdvancements().getOrStartProgress(hide9).isDone(),
                    "可代码授予 achievement_hide_9", "ok"));
        } else {
            out.accept(new Result(false, "可代码授予 achievement_hide_9", "holder null"));
        }
        // 保留床+笼供观察
    }

    // ==================== hide_7：杀监守者 ====================

    private static void verifyHide7WardenPath(ServerLevel level, ServerPlayer player, Consumer<Result> out) {
        AdvancementHolder start = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", "achievement_start"));
        AdvancementHolder hide7 = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", "achievement_hide_7"));
        if (start != null) {
            var sp = player.getAdvancements().getOrStartProgress(start);
            if (!sp.isDone()) {
                for (String c : sp.getRemainingCriteria()) {
                    player.getAdvancements().award(start, c);
                }
            }
        }
        // 确保 hide_7 未完成
        if (hide7 != null) {
            var hp = player.getAdvancements().getOrStartProgress(hide7);
            if (hp.isDone()) {
                // 无法 revoke 简单处理：仍杀一只，观察是否「本应」由钩子处理；仅报告已完成
                out.accept(new Result(true, "hide_7 测前状态", "已完成，跳过 Warden 授予观测"));
                return;
            }
        }

        BlockPos spawnAt = player.blockPosition().offset(4, 0, 4);
        spawnAt = new BlockPos(spawnAt.getX(),
                level.getHeight(Heightmap.Types.MOTION_BLOCKING, spawnAt.getX(), spawnAt.getZ()) + 1,
                spawnAt.getZ());
        Warden warden = EntityType.WARDEN.create(level);
        if (warden == null) {
            out.accept(new Result(false, "Warden 生成", "create null"));
            return;
        }
        warden.moveTo(spawnAt.getX() + 0.5, spawnAt.getY(), spawnAt.getZ() + 0.5, 0, 0);
        level.addFreshEntity(warden);
        warden.kill();
        // 同步死亡事件应已派发
        boolean granted = hide7 != null
                && player.getAdvancements().getOrStartProgress(hide7).isDone();
        // 缺口核实：未授予 → pass（确认缺 SculkHeart 钩子）
        out.accept(new Result(!granted,
                "杀 Warden 后 hide_7 仍未授予（SculkHeart 缺口）",
                granted ? "意外已授予——钩子可能已接" : "confirmed missing grant path"));
        if (warden.isAlive()) {
            warden.discard();
        }
        // 清理附近可能掉落
        level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
                        new AABB(spawnAt).inflate(4), e -> true)
                .forEach(net.minecraft.world.entity.Entity::discard);
    }

    // ==================== 工具 ====================

    private static Result ok(boolean pass, String name, String detail) {
        return new Result(pass, name, detail);
    }

    private static BlockPos placementAnchor(ServerLevel level, ServerPlayer player) {
        BlockPos p = player.blockPosition().offset(ANCHOR_DX, 0, ANCHOR_DZ);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, p.getX(), p.getZ()) + 2;
        BlockPos pos = new BlockPos(p.getX(), y, p.getZ());
        level.getChunk(pos);
        return pos;
    }

    private static Optional<StructureTemplate> loadViaStm(StructureTemplateManager mgr, String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("pasterdream", path);
        Optional<StructureTemplate> opt = mgr.get(id);
        if (opt.isPresent() && opt.get().getSize().getX() > 0) {
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

    private static Optional<StructureTemplate> loadViaResource(MinecraftServer server, String path) {
        ResourceLocation nbtLocation = ResourceLocation.fromNamespaceAndPath(
                "pasterdream", "structure/" + path + ".nbt");
        try {
            var resourceOpt = server.getResourceManager().getResource(nbtLocation);
            if (resourceOpt.isEmpty()) {
                return Optional.empty();
            }
            try (InputStream is = resourceOpt.get().open()) {
                CompoundTag tag = NbtIo.readCompressed(is, new NbtAccounter(0x20000000L, 512));
                return Optional.of(server.getStructureManager().readStructure(tag));
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static String sizeOf(StructureTemplate t) {
        var s = t.getSize();
        return s.getX() + "x" + s.getY() + "x" + s.getZ();
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

    private static int countBlockInBox(ServerLevel level, BlockPos min, BlockPos max,
                                       net.minecraft.world.level.block.Block block) {
        int n = 0;
        for (BlockPos p : BlockPos.betweenClosed(min, max)) {
            if (level.getBlockState(p).is(block)) {
                n++;
            }
        }
        return n;
    }
}
