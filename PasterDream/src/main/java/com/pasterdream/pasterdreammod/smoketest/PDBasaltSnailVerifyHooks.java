package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.entity.mob.BasaltSnailEntity;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 玄武岩蜗牛独立自然生成配额验收，不并入默认 all。
 * 先检查分类、群系池及 15/16 边界，再观察真实自然调度；强制入口只在观察结束后调用。
 */
public final class PDBasaltSnailVerifyHooks {
    /** 六个动物生成调度周期，必须经过真实服务端 tick。 */
    public static final int OBSERVATION_TICKS = 2400;
    private static final int MAX_POINTS = 60;

    /** 单项验收结果。 */
    public record Result(boolean pass, String name, String detail) {
    }

    /** 一次自然观察的上下文，夹具只在本次验收结束时清理。 */
    private static final class Observation {
        final ServerLevel level;
        final ServerPlayer player;
        final List<BlockPos> points;
        final List<Mob> blockers;
        final Consumer<Result> out;
        final int initialSnails;
        int peakSnails;
        boolean sawIndependentSpawn;

        Observation(ServerLevel level, ServerPlayer player, List<BlockPos> points,
                List<Mob> blockers, Consumer<Result> out) {
            this.level = level;
            this.player = player;
            this.points = points;
            this.blockers = blockers;
            this.out = out;
            this.initialSnails = count(level, BasaltSnailEntity.class);
            this.peakSnails = this.initialSnails;
        }
    }

    private PDBasaltSnailVerifyHooks() {
    }

    /**
     * 运行独立分类、自然配额和真实自然生成验收。
     * @param server 服务端
     * @param player 测试玩家
     * @param out 结果收集器
     */
    public static void verify(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        if (server == null || player == null || server.getLevel(Level.NETHER) == null) {
            out.accept(new Result(false, "蜗牛验收环境", "需要服务端、玩家及下界，未执行不计通过"));
            return;
        }
        ServerLevel level = server.getLevel(Level.NETHER);
        verifyCategoryAndBiome(server, out);
        BlockPos center = locateCenter(level, player.blockPosition(), out);
        if (center == null) {
            return;
        }
        List<BlockPos> ground = findGroundPoints(level, center, out);
        if (ground.isEmpty()) {
            return;
        }
        BlockPos anchor = ground.get(0);
        player.teleportTo(level, anchor.getX() + 0.5, anchor.getY() + 1, anchor.getZ() + 0.5, 0, 0);
        List<BlockPos> points = findCandidates(level, player, ground, out);
        if (points.isEmpty()) {
            return;
        }
        verifyCapBoundary(level, points.get(0), out);
        startObservation(level, player, points, out);
    }

    /**
     * 检查实体分类及 modifier 自动注入的目标池。
     * @param server 服务端
     * @param out 结果收集器
     */
    private static void verifyCategoryAndBiome(MinecraftServer server, Consumer<Result> out) {
        EntityType<BasaltSnailEntity> type = PDEntities.BASALT_SNAIL.get();
        MobCategory category = type.getCategory();
        out.accept(new Result(category != MobCategory.CREATURE && category.getMaxInstancesPerChunk() == 16
                && category.isFriendly() && category.isPersistent(), "蜗牛独立类别与16只配额",
                "category=" + category.getSerializedName() + " cap=" + category.getMaxInstancesPerChunk()
                        + " friendly=" + category.isFriendly() + " persistent=" + category.isPersistent()));
        var biome = server.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.BASALT_DELTAS);
        var pool = biome.value().getMobSettings().getMobs(category).unwrap();
        boolean expected = pool.stream().anyMatch(d -> d.type == type && d.getWeight().asInt() == 20
                && d.minCount == 2 && d.maxCount == 4);
        boolean inCreature = biome.value().getMobSettings().getMobs(MobCategory.CREATURE).unwrap().stream()
                .anyMatch(d -> d.type == type);
        out.accept(new Result(expected && !inCreature, "玄武岩三角洲独立刷怪池",
                "entries=" + pool + " snailInCreature=" + inCreature));
        out.accept(new Result(type.canSummon() && DefaultAttributes.getSupplier(type) != null,
                "蜗牛可创建且属性可解析", "canSummon=" + type.canSummon()));
    }

    /**
     * 定位群系，保留逐区块生成后的兜底扫描。
     * @param level 下界
     * @param origin 搜索原点
     * @param out 结果收集器
     * @return 群系位置，未找到时为 null
     */
    private static BlockPos locateCenter(ServerLevel level, BlockPos origin, Consumer<Result> out) {
        var located = level.findClosestBiome3d(h -> h.is(Biomes.BASALT_DELTAS), origin, 6400, 32, 8);
        if (located != null) {
            out.accept(new Result(true, "玄武岩三角洲定位", located.getFirst().toShortString()));
            return located.getFirst();
        }
        int scanned = 0;
        for (int ring = 0; ring <= 10 && scanned < 400; ring++) {
            for (int dx = -ring; dx <= ring && scanned < 400; dx++) {
                for (int dz = -ring; dz <= ring && scanned < 400; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) {
                        continue;
                    }
                    int cx = (origin.getX() >> 4) + dx * 12;
                    int cz = (origin.getZ() >> 4) + dz * 12;
                    level.getChunk(cx, cz);
                    BlockPos pos = new BlockPos(cx << 4, 64, cz << 4);
                    scanned++;
                    if (level.getBiome(pos).is(Biomes.BASALT_DELTAS)) {
                        out.accept(new Result(true, "玄武岩三角洲兜底定位", "chunks=" + scanned + " pos=" + pos));
                        return pos;
                    }
                }
            }
        }
        out.accept(new Result(false, "玄武岩三角洲定位", "未命中，扫描区块=" + scanned));
        return null;
    }

    /**
     * 扫描实际地面，排除下界基岩顶盖。
     * @param level 下界
     * @param center 群系中心
     * @param out 结果收集器
     * @return 地面位置
     */
    private static List<BlockPos> findGroundPoints(ServerLevel level, BlockPos center, Consumer<Result> out) {
        List<BlockPos> points = new ArrayList<>();
        for (int dx = -96; dx <= 96 && points.size() < MAX_POINTS; dx += 8) {
            for (int dz = -96; dz <= 96 && points.size() < MAX_POINTS; dz += 8) {
                BlockPos pos = findGround(level, center.getX() + dx, center.getZ() + dz);
                if (pos != null && level.getBiome(pos).is(Biomes.BASALT_DELTAS)) {
                    points.add(pos);
                }
            }
        }
        out.accept(new Result(!points.isEmpty(), "真实地面扫描", "points=" + points.size()));
        return points;
    }

    /**
     * 找到一列中可站立的真实下界地面。
     * @param level 下界
     * @param x 横坐标
     * @param z 纵坐标
     * @return 地面上方位置，未找到时为 null
     */
    private static BlockPos findGround(ServerLevel level, int x, int z) {
        level.getChunk(x >> 4, z >> 4);
        int top = Math.min(level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z), level.getMaxBuildHeight() - 2);
        for (int y = top; y > level.getMinBuildHeight() + 2; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            var below = level.getBlockState(pos.below());
            if (!below.is(Blocks.BEDROCK) && below.isFaceSturdy(level, pos.below(), Direction.UP)
                    && level.getFluidState(pos.below()).isEmpty() && level.getBlockState(pos).isAir()
                    && level.getBlockState(pos.above()).isAir()) {
                return pos;
            }
        }
        return null;
    }

    /**
     * 按实际玩家位置和放置规则筛选。
     * @param level 下界
     * @param player 玩家
     * @param ground 地面位置
     * @param out 结果收集器
     * @return 有效位置
     */
    private static List<BlockPos> findCandidates(ServerLevel level, ServerPlayer player,
            List<BlockPos> ground, Consumer<Result> out) {
        List<BlockPos> points = new ArrayList<>();
        for (BlockPos pos : ground) {
            double distance = player.distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            if (distance > 24 * 24 && distance <= 128 * 128
                    && SpawnPlacements.isSpawnPositionOk(PDEntities.BASALT_SNAIL.get(), level, pos)) {
                points.add(pos);
            }
        }
        out.accept(new Result(!points.isEmpty(), "放置规则与玩家距离", "points=" + points.size()));
        return points;
    }

    /**
     * 在同一服务端 tick 中验证真实实体生成方法的15/16边界。
     * @param level 下界
     * @param pos 有效地面点
     * @param out 结果收集器
     */
    private static void verifyCapBoundary(ServerLevel level, BlockPos pos, Consumer<Result> out) {
        int before = count(level, BasaltSnailEntity.class);
        int stridersBefore = count(level, Strider.class);
        int cap = PDEntities.BASALT_SNAIL.get().getCategory().getMaxInstancesPerChunk();
        List<Mob> fixtures = new ArrayList<>();
        BasaltSnailEntity probe = Objects.requireNonNull(PDEntities.BASALT_SNAIL.get().create(level));
        try {
            if (before >= cap) {
                throw new IllegalStateException("已有蜗牛达到上限，不能伪造15只边界通过");
            }
            fillStriders(level, pos, fixtures);
            for (int i = before; i < cap - 1; i++) {
                fixtures.add(addFixture(PDEntities.BASALT_SNAIL.get(), level, pos));
            }
            boolean at15 = count(level, BasaltSnailEntity.class) == 15 && probe.checkSpawnRules(level, MobSpawnType.NATURAL);
            out.accept(new Result(at15, "炽足兽满配额时15只蜗牛仍允许自然生成", "snails=" + count(level, BasaltSnailEntity.class)));
            Mob last = addFixture(PDEntities.BASALT_SNAIL.get(), level, pos);
            fixtures.add(last);
            out.accept(new Result(count(level, BasaltSnailEntity.class) == 16
                    && !probe.checkSpawnRules(level, MobSpawnType.NATURAL), "16只蜗牛拒绝下一只自然生成", "cap=" + cap));
            out.accept(new Result(probe.checkSpawnRules(level, MobSpawnType.SPAWN_EGG),
                    "满配额不限制刷怪蛋生成规则", "仅限制NATURAL"));
            last.setPersistenceRequired();
            out.accept(new Result(count(level, BasaltSnailEntity.class) == 15
                    && probe.checkSpawnRules(level, MobSpawnType.NATURAL), "持久化蜗牛不占自然配额", "count=" + count(level, BasaltSnailEntity.class)));
        } catch (RuntimeException e) {
            out.accept(new Result(false, "配额边界夹具", e.toString()));
        } finally {
            fixtures.forEach(Entity::discard);
            probe.discard();
            out.accept(new Result(count(level, BasaltSnailEntity.class) == before && count(level, Strider.class) == stridersBefore,
                    "边界夹具清理且原有实体数量不变", "snailsBefore=" + before + " stridersBefore=" + stridersBefore));
        }
    }

    /**
     * 添加可明确清理的非持久化测试生物。
     * @param type 实体类型
     * @param level 世界
     * @param pos 位置
     * @param <T> 生物类型
     * @return 已加入世界的夹具
     */
    private static <T extends Mob> T addFixture(EntityType<T> type, ServerLevel level, BlockPos pos) {
        T mob = Objects.requireNonNull(type.create(level), "fixture create");
        mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        mob.setNoAi(true);
        mob.setInvulnerable(true);
        if (!level.addFreshEntity(mob)) {
            mob.discard();
            throw new IllegalStateException("fixture add failed");
        }
        return mob;
    }

    /**
     * 确保对照的动物类别配额被占满，不改变原有个体。
     * @param level 世界
     * @param pos 位置
     * @param fixtures 仅记录本次新增夹具
     */
    private static void fillStriders(ServerLevel level, BlockPos pos, List<Mob> fixtures) {
        int cap = MobCategory.CREATURE.getMaxInstancesPerChunk();
        int missing = cap - countCategory(level, MobCategory.CREATURE);
        for (int i = 0; i < missing; i++) {
            fixtures.add(addFixture(EntityType.STRIDER, level, pos));
        }
        if (countCategory(level, MobCategory.CREATURE) < cap) {
            throw new IllegalStateException("CREATURE fixture quota was not filled");
        }
    }

    /**
     * 设置受控炽足兽背景，先观察正常调度。
     * @param level 下界
     * @param player 玩家
     * @param points 候选点
     * @param out 结果收集器
     */
    private static void startObservation(ServerLevel level, ServerPlayer player,
            List<BlockPos> points, Consumer<Result> out) {
        List<Mob> blockers = new ArrayList<>();
        try {
            fillStriders(level, player.blockPosition(), blockers);
            Observation observation = new Observation(level, player, points, blockers, out);
            ServerScheduler.schedule(100, () -> verifyContext(observation));
            for (int delay = 400; delay <= OBSERVATION_TICKS; delay += 400) {
                int elapsed = delay;
                ServerScheduler.schedule(delay, () -> sampleObservation(observation, elapsed));
            }
            ServerScheduler.schedule(OBSERVATION_TICKS + 1, () -> finishObservation(observation));
        } catch (RuntimeException e) {
            blockers.forEach(Entity::discard);
            out.accept(new Result(false, "自然观察启动", e.toString()));
        }
    }

    /**
     * 区块就绪后检查实际候选池与玩家资格。
     * @param observation 当前观察
     */
    private static void verifyContext(Observation observation) {
        ServerLevel level = observation.level;
        MobCategory category = PDEntities.BASALT_SNAIL.get().getCategory();
        int poolMatches = 0;
        int ticking = 0;
        for (BlockPos pos : observation.points) {
            var base = level.getChunkSource().getGenerator().getMobsAt(level.getBiome(pos), level.structureManager(), category, pos);
            var pool = net.neoforged.neoforge.event.EventHooks.getPotentialSpawns(level, category, pos, base);
            if (pool.unwrap().stream().anyMatch(d -> d.type == PDEntities.BASALT_SNAIL.get())) {
                poolMatches++;
            }
            if (level.isNaturalSpawningAllowed(pos)) {
                ticking++;
            }
        }
        boolean eligible = level.players().contains(observation.player) && !observation.player.isSpectator()
                && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
        observation.out.accept(new Result(eligible && poolMatches > 0 && ticking > 0, "自然入口上下文",
                "eligible=" + eligible + " poolMatches=" + poolMatches + " ticking=" + ticking));
    }

    /**
     * 记录整个已加载维度的真实自然产出及两类配额。
     * @param observation 当前观察
     * @param elapsed 真实 tick 数
     */
    private static void sampleObservation(Observation observation, int elapsed) {
        ServerLevel level = observation.level;
        MobCategory category = PDEntities.BASALT_SNAIL.get().getCategory();
        int snails = count(level, BasaltSnailEntity.class);
        observation.peakSnails = Math.max(observation.peakSnails, snails);
        var state = level.getChunkSource().getLastSpawnState();
        int creatures = state == null ? 0 : state.getMobCategoryCounts().getInt(MobCategory.CREATURE);
        int snailQuota = state == null ? 0 : state.getMobCategoryCounts().getInt(category);
        int creatureCap = state == null ? 0 : MobCategory.CREATURE.getMaxInstancesPerChunk() * state.getSpawnableChunkCount() / 289;
        if (creatureCap > 0 && creatures >= creatureCap && snails > observation.initialSnails) {
            observation.sawIndependentSpawn = true;
        }
        observation.out.accept(new Result(snails <= category.getMaxInstancesPerChunk(), "全维度自然配额快照 " + elapsed + " tick",
                "snails=" + snails + " snailQuota=" + snailQuota + " snailCap=" + category.getMaxInstancesPerChunk()
                        + " creatures=" + creatures + " creatureCap=" + creatureCap));
    }

    /**
     * 验证自然观察结果，再运行有严格上限保护的原版调试入口。
     * @param observation 当前观察
     */
    private static void finishObservation(Observation observation) {
        try {
            int snails = count(observation.level, BasaltSnailEntity.class);
            observation.out.accept(new Result(observation.peakSnails > observation.initialSnails, "真实正常调度产出蜗牛",
                    "before=" + observation.initialSnails + " peak=" + observation.peakSnails
                            + " after=" + snails + " ticks=" + OBSERVATION_TICKS));
            observation.out.accept(new Result(observation.sawIndependentSpawn, "CREATURE满配额不阻断蜗牛自然生成",
                    "饱和CREATURE与蜗牛新增同时见于真实调度快照=" + observation.sawIndependentSpawn));
            verifyForcedSpawn(observation);
        } finally {
            observation.blockers.forEach(Entity::discard);
            observation.out.accept(new Result(observation.blockers.stream().allMatch(Entity::isRemoved),
                    "自然观察对照夹具清理", "created=" + observation.blockers.size()));
        }
    }

    /**
     * 即使绕过原版类别配额，逐只保护也不能突破16。
     * @param observation 当前观察
     */
    private static void verifyForcedSpawn(Observation observation) {
        ServerLevel level = observation.level;
        MobCategory category = PDEntities.BASALT_SNAIL.get().getCategory();
        int before = count(level, BasaltSnailEntity.class);
        for (BlockPos pos : observation.points) {
            NaturalSpawner.spawnCategoryForPosition(category, level, pos);
        }
        int after = count(level, BasaltSnailEntity.class);
        boolean producedOrFull = before >= category.getMaxInstancesPerChunk() || after > before;
        observation.out.accept(new Result(producedOrFull && after <= category.getMaxInstancesPerChunk(),
                "原版调试入口也遵守逐只上限", "before=" + before + " after=" + after + " attempts=" + observation.points.size()));
    }

    /**
     * 计数整个已加载维度的非持久化目标生物。
     * @param level 世界
     * @param type 生物类
     * @return 配额计数
     */
    private static int count(ServerLevel level, Class<? extends Mob> type) {
        int count = 0;
        for (Entity entity : level.getAllEntities()) {
            if (type.isInstance(entity) && entity instanceof Mob mob
                    && !mob.isPersistenceRequired() && !mob.requiresCustomPersistence()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 计数原版动物类别，作为独立性对照。
     * @param level 世界
     * @param category 类别
     * @return 配额计数
     */
    private static int countCategory(ServerLevel level, MobCategory category) {
        int count = 0;
        for (Entity entity : level.getAllEntities()) {
            if (entity.getClassification(true) == category && entity instanceof Mob mob
                    && !mob.isPersistenceRequired() && !mob.requiresCustomPersistence()) {
                count++;
            }
        }
        return count;
    }
}
