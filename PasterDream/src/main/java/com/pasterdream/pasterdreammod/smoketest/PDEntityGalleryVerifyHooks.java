package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * 人工终验：实体展台。
 * <ul>
 *   <li><b>刷怪蛋桶排</b>：全部本模组 {@code *_spawn_egg} 装入木桶（每桶 27 格），告示牌标名</li>
 *   <li><b>无蛋实体箱</b>：无刷怪蛋的实体（投射物/MISC 等）用命名纸张装入木桶备查</li>
 *   <li><b>活体玻璃笼</b>：可 {@link Mob} 化的类型 NoAI + 无敌关进玻璃盒，便于对照模型</li>
 * </ul>
 * 展台放在出生点西侧，与方块总览（东/南）错开。
 */
public final class PDEntityGalleryVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    /** 相对出生点：西侧，避开方块总览 ORIGIN_DX/DZ=+24 */
    private static final int ORIGIN_DX = -48;
    private static final int ORIGIN_DZ = 8;
    private static final int BARREL_COLS = 8;
    private static final int BARREL_STRIDE = 2;
    private static final int CAGE_STRIDE = 4;
    private static final int CAGE_COLS = 10;
    /** 单桶容量 */
    private static final int BARREL_SLOTS = 27;

    private PDEntityGalleryVerifyHooks() {
    }

    /**
     * 铺实体展台并写断言。
     *
     * @param player 玩家
     * @param out    断言输出
     * @return 展台原点
     */
    public static BlockPos placeEntityGallery(ServerPlayer player, Consumer<Result> out) {
        if (player == null) {
            out.accept(new Result(false, "实体展台跳过", "player == null"));
            return BlockPos.ZERO;
        }
        ServerLevel level = player.serverLevel().getServer().getLevel(Level.OVERWORLD);
        if (level == null) {
            out.accept(new Result(false, "实体展台跳过", "overworld == null"));
            return BlockPos.ZERO;
        }

        List<ItemStack> spawnEggs = collectSpawnEggs();
        List<String> noEggEntities = collectEntitiesWithoutSpawnEgg(spawnEggs);
        List<EntityType<?>> livingTypes = collectLivingEntityTypes();

        int eggBarrels = Math.max(1, (spawnEggs.size() + BARREL_SLOTS - 1) / BARREL_SLOTS);
        int paperBarrels = Math.max(1, (noEggEntities.size() + BARREL_SLOTS - 1) / BARREL_SLOTS);
        int cageRows = Math.max(1, (livingTypes.size() + CAGE_COLS - 1) / CAGE_COLS);

        int barrelRows = Math.max(1, (eggBarrels + paperBarrels + BARREL_COLS - 1) / BARREL_COLS) + 1;
        int platformW = Math.max(BARREL_COLS * BARREL_STRIDE, CAGE_COLS * CAGE_STRIDE) + 6;
        int platformD = barrelRows * BARREL_STRIDE + 4 + cageRows * CAGE_STRIDE + 8;

        BlockPos spawn = level.getSharedSpawnPos();
        int baseY = Math.max(level.getMinBuildHeight() + 8,
                level.getHeight(Heightmap.Types.MOTION_BLOCKING,
                        spawn.getX() + ORIGIN_DX, spawn.getZ() + ORIGIN_DZ));
        baseY = Math.max(baseY, spawn.getY());
        BlockPos origin = new BlockPos(spawn.getX() + ORIGIN_DX, baseY, spawn.getZ() + ORIGIN_DZ);

        for (int x = 0; x < platformW; x += 16) {
            for (int z = 0; z < platformD; z += 16) {
                level.getChunk(origin.offset(x, 0, z));
            }
        }

        // 平台 + 清空
        BlockPos min = origin.offset(-2, -1, -2);
        BlockPos max = origin.offset(platformW, 6, platformD);
        for (BlockPos p : BlockPos.betweenClosed(min, max)) {
            int relY = p.getY() - origin.getY();
            if (relY == -1) {
                level.setBlock(p, Blocks.SMOOTH_STONE.defaultBlockState(), 2);
            } else {
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            }
        }
        // 边灯
        for (int x = -1; x <= platformW - 1; x++) {
            level.setBlock(origin.offset(x, 0, -1), Blocks.SEA_LANTERN.defaultBlockState(), 2);
            level.setBlock(origin.offset(x, 0, platformD - 3), Blocks.SEA_LANTERN.defaultBlockState(), 2);
        }
        for (int z = -1; z <= platformD - 3; z++) {
            level.setBlock(origin.offset(-1, 0, z), Blocks.SEA_LANTERN.defaultBlockState(), 2);
            level.setBlock(origin.offset(platformW - 3, 0, z), Blocks.SEA_LANTERN.defaultBlockState(), 2);
        }

        // 标题柱
        placeStandingLabel(level, origin.offset(0, 0, 0),
                Component.literal("实体展台"),
                Component.literal("刷怪蛋木桶 + 玻璃笼"),
                Component.literal("蛋 " + spawnEggs.size() + " · 无蛋 " + noEggEntities.size()),
                Component.literal("活体类型 " + livingTypes.size()));

        // —— 刷怪蛋木桶 ——
        int filledEggStacks = 0;
        int eggBarrelPlaced = 0;
        for (int b = 0; b < eggBarrels; b++) {
            int col = b % BARREL_COLS;
            int row = b / BARREL_COLS;
            BlockPos pos = origin.offset(2 + col * BARREL_STRIDE, 0, 3 + row * BARREL_STRIDE);
            int from = b * BARREL_SLOTS;
            int to = Math.min(spawnEggs.size(), from + BARREL_SLOTS);
            List<ItemStack> slice = spawnEggs.subList(from, to);
            if (placeFilledBarrel(level, pos, slice,
                    Component.literal("刷怪蛋 " + (b + 1) + "/" + eggBarrels),
                    Component.literal(from + ".." + (to - 1)))) {
                eggBarrelPlaced++;
                filledEggStacks += slice.size();
            }
        }

        // —— 无蛋实体：命名纸张木桶 ——
        int paperBaseIndex = eggBarrels;
        int paperBarrelPlaced = 0;
        int paperStacks = 0;
        List<ItemStack> papers = new ArrayList<>();
        for (String path : noEggEntities) {
            ItemStack paper = new ItemStack(Items.PAPER);
            paper.set(DataComponents.CUSTOM_NAME, Component.literal(path));
            papers.add(paper);
        }
        for (int b = 0; b < paperBarrels && !papers.isEmpty(); b++) {
            int idx = paperBaseIndex + b;
            int col = idx % BARREL_COLS;
            int row = idx / BARREL_COLS;
            BlockPos pos = origin.offset(2 + col * BARREL_STRIDE, 0, 3 + row * BARREL_STRIDE);
            int from = b * BARREL_SLOTS;
            int to = Math.min(papers.size(), from + BARREL_SLOTS);
            List<ItemStack> slice = papers.subList(from, to);
            if (placeFilledBarrel(level, pos, slice,
                    Component.literal("无蛋实体 " + (b + 1) + "/" + paperBarrels),
                    Component.literal("投射物/MISC 名签"))) {
                paperBarrelPlaced++;
                paperStacks += slice.size();
            }
        }

        // —— 活体玻璃笼（木桶排后方） ——
        int cageBaseZ = 3 + (Math.max(1, (eggBarrels + paperBarrels + BARREL_COLS - 1) / BARREL_COLS) + 1)
                * BARREL_STRIDE + 2;
        placeStandingLabel(level, origin.offset(0, 0, cageBaseZ - 2),
                Component.literal("活体玻璃笼"),
                Component.literal("NoAI · 无敌 · 持久"),
                Component.literal("共 " + livingTypes.size() + " 种"),
                Component.empty());

        int caged = 0;
        int cageFail = 0;
        List<String> cageFailSamples = new ArrayList<>();
        for (int i = 0; i < livingTypes.size(); i++) {
            int col = i % CAGE_COLS;
            int row = i / CAGE_COLS;
            BlockPos floor = origin.offset(2 + col * CAGE_STRIDE, 0, cageBaseZ + row * CAGE_STRIDE);
            EntityType<?> type = livingTypes.get(i);
            try {
                if (placeMobCage(level, floor, type)) {
                    caged++;
                } else {
                    cageFail++;
                    sample(cageFailSamples, typeId(type) + ":null");
                }
            } catch (Exception ex) {
                cageFail++;
                sample(cageFailSamples, typeId(type) + ":" + ex.getClass().getSimpleName());
            }
        }

        // 传送玩家到实体展台起点（覆盖方块展台 TP，便于先看实体；KEEP_OPEN 可再飞回）
        double tpX = origin.getX() - 1.5;
        double tpY = origin.getY() + 2.5;
        double tpZ = origin.getZ() - 1.5;
        if (player.level() != level) {
            player.teleportTo(level, tpX, tpY, tpZ, -45f, 25f);
        } else {
            player.teleportTo(tpX, tpY, tpZ);
            player.setYRot(-45f);
            player.setXRot(25f);
        }
        PDGalleryVerifyHooks.ensureNightVision(player);
        player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);

        // 注册表规模（本模组实体）
        int modEntities = 0;
        for (var e : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
            if (PasterDreamMod.MOD_ID.equals(e.getKey().location().getNamespace())) {
                modEntities++;
            }
        }

        out.accept(detail(spawnEggs.size() >= 30,
                "刷怪蛋收集 " + spawnEggs.size(),
                "装入木桶 stacks=" + filledEggStacks + " barrels=" + eggBarrelPlaced));
        out.accept(detail(eggBarrelPlaced >= 1 && filledEggStacks == spawnEggs.size(),
                "刷怪蛋木桶装填 " + filledEggStacks + "/" + spawnEggs.size(),
                "barrels=" + eggBarrelPlaced));
        out.accept(detail(paperBarrelPlaced >= 1 || noEggEntities.isEmpty(),
                "无蛋实体名签桶 " + paperStacks + "/" + noEggEntities.size(),
                "barrels=" + paperBarrelPlaced
                        + (noEggEntities.isEmpty() ? "（全部有蛋）"
                        : " sample=" + noEggEntities.subList(0, Math.min(6, noEggEntities.size())))));
        out.accept(detail(caged > 0 && caged + cageFail == livingTypes.size()
                        && cageFail <= Math.max(2, livingTypes.size() / 5),
                "活体玻璃笼 " + caged + "/" + livingTypes.size(),
                cageFail == 0 ? "全部入笼" : "fail=" + cageFail + " " + cageFailSamples));
        out.accept(detail(modEntities >= 50,
                "模组实体注册 " + modEntities,
                "origin=" + origin.toShortString()
                        + " eggs=" + spawnEggs.size()
                        + " noEgg=" + noEggEntities.size()
                        + " living=" + livingTypes.size()));

        PasterDreamMod.LOGGER.info(
                "[PDEntityGallery] origin={} eggs={}/{} barrels={} paper={}/{} caged={}/{} modEntities={}",
                origin.toShortString(), filledEggStacks, spawnEggs.size(), eggBarrelPlaced,
                paperStacks, noEggEntities.size(), caged, livingTypes.size(), modEntities);
        return origin;
    }

    // ==================== 收集 ====================

    private static List<ItemStack> collectSpawnEggs() {
        List<ItemStack> list = new ArrayList<>();
        for (var entry : BuiltInRegistries.ITEM.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            if (!PasterDreamMod.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            if (!id.getPath().endsWith("_spawn_egg")) {
                continue;
            }
            Item item = entry.getValue();
            if (item == null || item == Items.AIR) {
                continue;
            }
            list.add(new ItemStack(item));
        }
        list.sort(Comparator.comparing(s -> String.valueOf(BuiltInRegistries.ITEM.getKey(s.getItem()))));
        return list;
    }

    /**
     * 模组实体中找不到对应 {@code {path}_spawn_egg} 或 {@code {path} 去后缀匹配} 的路径列表。
     */
    private static List<String> collectEntitiesWithoutSpawnEgg(List<ItemStack> eggs) {
        java.util.HashSet<String> eggPaths = new java.util.HashSet<>();
        for (ItemStack s : eggs) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(s.getItem());
            if (id == null) {
                continue;
            }
            String p = id.getPath();
            if (p.endsWith("_spawn_egg")) {
                eggPaths.add(p.substring(0, p.length() - "_spawn_egg".length()));
            }
        }
        List<String> missing = new ArrayList<>();
        for (var entry : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            if (!PasterDreamMod.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            String path = id.getPath();
            if (eggPaths.contains(path)) {
                continue;
            }
            // 宽松：egg 名是实体名的前缀（highvoltage vs highvoltage_thundercloud 等已用实体注册名）
            boolean fuzzy = false;
            for (String ep : eggPaths) {
                if (path.startsWith(ep) || ep.startsWith(path) || path.contains(ep) || ep.contains(path)) {
                    fuzzy = true;
                    break;
                }
            }
            if (!fuzzy) {
                missing.add(path);
            }
        }
        missing.sort(String::compareTo);
        return missing;
    }

    private static List<EntityType<?>> collectLivingEntityTypes() {
        List<EntityType<?>> list = new ArrayList<>();
        for (var entry : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            if (!PasterDreamMod.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            EntityType<?> type = entry.getValue();
            MobCategory cat = type.getCategory();
            // 跳过纯投射物 MISC（除已挂 Mob 的立场等，靠 create 后 instanceof Mob 过滤）
            if (cat == MobCategory.MISC) {
                // 仍尝试；placeMobCage 会丢弃非 Mob
                String p = id.getPath();
                if (p.startsWith("projectile_") || p.contains("projectile") || p.contains("magicball")) {
                    continue;
                }
            }
            list.add(type);
        }
        list.sort(Comparator.comparing(PDEntityGalleryVerifyHooks::typeId));
        return list;
    }

    // ==================== 放置 ====================

    private static boolean placeFilledBarrel(ServerLevel level, BlockPos pos, List<ItemStack> stacks,
                                             Component line0, Component line1) {
        level.setBlock(pos, Blocks.BARREL.defaultBlockState()
                .setValue(BarrelBlock.FACING, Direction.UP), 3);
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof Container container)) {
            return false;
        }
        int n = Math.min(stacks.size(), Math.min(BARREL_SLOTS, container.getContainerSize()));
        for (int i = 0; i < n; i++) {
            container.setItem(i, stacks.get(i).copy());
        }
        be.setChanged();
        // 桶北侧告示
        placeWallLabel(level, pos.north(), line0, line1,
                Component.literal(n + " stacks"), Component.empty());
        return n > 0 || stacks.isEmpty();
    }

    private static boolean placeMobCage(ServerLevel level, BlockPos floor, EntityType<?> type) {
        // 地板
        level.setBlock(floor, Blocks.SMOOTH_STONE.defaultBlockState(), 2);
        // 玻璃墙 3×3 环（高 2）+ 顶盖；中心柱中空
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 1; dy <= 2; dy++) {
                    boolean edge = dx != 0 || dz != 0;
                    if (edge) {
                        level.setBlock(floor.offset(dx, dy, dz), Blocks.GLASS.defaultBlockState(), 2);
                    } else {
                        level.setBlock(floor.offset(0, dy, 0), Blocks.AIR.defaultBlockState(), 2);
                    }
                }
                level.setBlock(floor.offset(dx, 3, dz), Blocks.GLASS.defaultBlockState(), 2);
            }
        }
        // 四角灯
        level.setBlock(floor.offset(-1, 0, -1), Blocks.SEA_LANTERN.defaultBlockState(), 2);

        Entity created = type.create(level);
        if (!(created instanceof Mob mob)) {
            if (created != null) {
                created.discard();
            }
            // 非 Mob（部分立场/特殊类型）：笼位保留 + 告示，不计入失败
            placeWallLabel(level, floor.north().above(),
                    Component.literal(shortName(typeId(type))),
                    Component.literal("非Mob·已标"),
                    Component.empty(), Component.empty());
            return true;
        }
        mob.moveTo(floor.getX() + 0.5, floor.getY() + 1.0, floor.getZ() + 0.5, 180f, 0f);
        mob.setNoAi(true);
        mob.setInvulnerable(true);
        mob.setPersistenceRequired();
        mob.setSilent(true);
        mob.setHealth(mob.getMaxHealth());
        boolean added = level.addFreshEntity(mob);
        placeWallLabel(level, floor.north(),
                Component.literal(shortName(typeId(type))),
                Component.literal(type.getCategory().getName()),
                Component.literal(added ? "caged" : "add fail"),
                Component.empty());
        return added;
    }

    private static void placeStandingLabel(ServerLevel level, BlockPos pos,
                                           Component l0, Component l1, Component l2, Component l3) {
        level.setBlock(pos, Blocks.CHISELED_QUARTZ_BLOCK.defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.OAK_SIGN.defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(pos.above());
        if (be instanceof SignBlockEntity sign) {
            SignText text = sign.getText(true)
                    .setMessage(0, l0)
                    .setMessage(1, l1)
                    .setMessage(2, l2)
                    .setMessage(3, l3);
            sign.setText(text, true);
            sign.setAllowedPlayerEditor(null);
            sign.setChanged();
            level.sendBlockUpdated(pos.above(), level.getBlockState(pos.above()),
                    level.getBlockState(pos.above()), 3);
        }
    }

    private static void placeWallLabel(ServerLevel level, BlockPos pos,
                                       Component l0, Component l1, Component l2, Component l3) {
        level.setBlock(pos, Blocks.OAK_WALL_SIGN.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 3);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SignBlockEntity sign) {
            SignText text = sign.getText(true)
                    .setMessage(0, l0)
                    .setMessage(1, l1)
                    .setMessage(2, l2)
                    .setMessage(3, l3);
            sign.setText(text, true);
            sign.setAllowedPlayerEditor(null);
            sign.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
    }

    private static String typeId(EntityType<?> type) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        return id != null ? id.getPath() : String.valueOf(type);
    }

    private static String shortName(String path) {
        if (path.length() <= 15) {
            return path;
        }
        return path.substring(0, 15);
    }

    private static void sample(List<String> samples, String s) {
        if (samples.size() < 10) {
            samples.add(s);
        }
    }

    private static Result detail(boolean ok, String name, String d) {
        return new Result(ok, name, d);
    }
}
