package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.block.DyedreamFlower16Block;
import com.pasterdream.pasterdreammod.block.DyedreamFlower17Block;
import com.pasterdream.pasterdreammod.block.DyedreamGardenDecryptFlowerBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLotusBlock;
import com.pasterdream.pasterdreammod.block.GoldenFoxSculptureBlock;
import com.pasterdream.pasterdreammod.entity.mob.GoldenFoxEntity;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

/**
 * 染梦世界专项 VERIFY（{@code dyedream} / {@code dye-dream} / {@code dream-world}）。
 * <p>
 * 在真实 {@code dyedream_world} 上覆盖：
 * <ol>
 *   <li><b>狐狸雕像仪式</b>：四角 ±9 迷梦冶梦莲（{@code flower_12}）+ 主手荧光浆果
 *       + 日出窗口 → 召唤 {@code golden_fox}、拆除雕像与四角花；
 *       缺花/错时/空手失败消息。</li>
 *   <li><b>迷梦冶梦莲多方块</b>：双格植物在染梦土上下半成对；
 *       非染梦土 {@code canSurvive} 失败；剪刀/园艺钳破坏上下半格均掉落。</li>
 *   <li><b>染梦莲花</b>：仅水面可放置（{@link DyedreamLotusBlock}）。</li>
 * </ol>
 * <b>不</b>并入默认 {@code all}。
 */
public final class PDDyedreamVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    private static final int PAD = 24;

    private PDDyedreamVerifyHooks() {
    }

    public static void verify(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        if (server == null) {
            out.accept(new Result(false, "dyedream.server", "server == null"));
            return;
        }
        if (player == null) {
            out.accept(new Result(false, "dyedream.player", "player == null"));
            return;
        }
        ServerLevel dream = server.getLevel(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY);
        if (dream == null) {
            out.accept(new Result(false, "dyedream.dim", "dyedream_world getLevel == null"));
            return;
        }

        // 进染梦维，落在开阔垫层上
        BlockPos base = new BlockPos(0, 80, 0);
        preparePad(dream, base);
        player.teleportTo(dream, base.getX() + 0.5, base.getY() + 1.1, base.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        boolean inDream = player.level().dimension().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY);
        out.accept(new Result(inDream, "dyedream.teleport",
                "dim=" + player.level().dimension().location()));
        if (!inDream) {
            return;
        }

        verifyFoxRitual(dream, player, base.offset(0, 1, 0), out);
        verifyFlower12Multiblock(dream, base.offset(16, 1, 0), out);
        verifyDoublePlantDrops(dream, player, base.offset(8, 1, 0), out);
        verifyDyedreamLotus(dream, base.offset(-16, 1, 0), out);
        // 染梦解密玩法：花园解密（flower_11→12）、雪傀儡解密（flower_16→17）、冻结之花（flower_17 随机刻）
        verifyGardenDecryption(dream, player, base.offset(16, 1, 16), out);
        verifyFlower16Decryption(dream, player, base.offset(-16, 1, 16), out);
        verifyFlower17Freeze(dream, base.offset(-16, 1, -16), out);
    }

    // ==================== 狐狸雕像 ====================

    private static void verifyFoxRitual(ServerLevel level, ServerPlayer player, BlockPos center, Consumer<Result> out) {
        // 负例：无花 + 有浆果 + 正确时间 → 失败
        setupFoxPad(level, center, false);
        forceSunriseWindow(level);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLOW_BERRIES, 8));
        boolean failNoFlower = !GoldenFoxSculptureBlock.tryActivateRitual(level, center, player);
        out.accept(new Result(failNoFlower && level.getBlockState(center).is(PDBlocks.GOLDEN_FOX_SCULPTURE.get()),
                "dyedream.fox.fail_no_flower",
                "expect fail+sculpture remains; fail=" + failNoFlower
                        + " day=" + (level.getDayTime() % 24000L)));

        // 布局：四角 flower_12
        setupFoxPad(level, center, true);
        out.accept(new Result(GoldenFoxSculptureBlock.hasRitualFlowers(level, center),
                "dyedream.fox.flowers_layout",
                "四角 flower_12 @ ±" + GoldenFoxSculptureBlock.RITUAL_OFFSET));

        // 负例：有花 + 无浆果
        forceSunriseWindow(level);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        boolean failNoBerry = !GoldenFoxSculptureBlock.tryActivateRitual(level, center, player);
        out.accept(new Result(failNoBerry && level.getBlockState(center).is(PDBlocks.GOLDEN_FOX_SCULPTURE.get()),
                "dyedream.fox.fail_no_berry",
                "fail=" + failNoBerry + " day=" + (level.getDayTime() % 24000L)));

        // 负例：错时（正午）。日时写主世界 levelData（各维共享），两端都 set 并回读校验。
        setupFoxPad(level, center, true);
        forceDayTime(level, 6000L);
        long todNoon = level.getDayTime() % 24000L;
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLOW_BERRIES, 8));
        boolean failTime = !GoldenFoxSculptureBlock.tryActivateRitual(level, center, player);
        out.accept(new Result(todNoon > GoldenFoxSculptureBlock.RITUAL_DAYTIME_MAX
                        && failTime
                        && level.getBlockState(center).is(PDBlocks.GOLDEN_FOX_SCULPTURE.get()),
                "dyedream.fox.fail_wrong_time",
                "dayTime%24000=" + todNoon + " fail=" + failTime
                        + " sculpture=" + level.getBlockState(center).is(PDBlocks.GOLDEN_FOX_SCULPTURE.get())));

        // 正例：日出窗口 + 浆果 + 四花 → 金狐（每步独立重建，避免前序副作用）
        setupFoxPad(level, center, true);
        forceSunriseWindow(level);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLOW_BERRIES, 8));
        int berriesBefore = player.getMainHandItem().getCount();
        long foxBefore = countFox(level, center, 8);
        long todOk = level.getDayTime() % 24000L;
        boolean ok = GoldenFoxSculptureBlock.tryActivateRitual(level, center, player);
        long foxAfter = countFox(level, center, 8);
        boolean sculptureGone = level.getBlockState(center).isAir();
        boolean flowersGone = !GoldenFoxSculptureBlock.hasRitualFlowers(level, center);
        int berriesAfter = player.getMainHandItem().getCount();
        out.accept(new Result(ok && sculptureGone && flowersGone && foxAfter > foxBefore && berriesAfter == berriesBefore - 1,
                "dyedream.fox.success",
                "ok=" + ok + " foxΔ=" + (foxAfter - foxBefore)
                        + " sculptureGone=" + sculptureGone + " flowersGone=" + flowersGone
                        + " berry " + berriesBefore + "→" + berriesAfter
                        + " day=" + todOk));

        // 交互路径：useItemOn
        setupFoxPad(level, center, true);
        forceSunriseWindow(level);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLOW_BERRIES, 1));
        long fox2 = countFox(level, center, 8);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(center), Direction.UP, center, false);
        level.getBlockState(center).useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit);
        long fox2After = countFox(level, center, 8);
        out.accept(new Result(fox2After > fox2 && level.getBlockState(center).isAir(),
                "dyedream.fox.useItemOn",
                "foxΔ=" + (fox2After - fox2) + " air=" + level.getBlockState(center).isAir()));
    }

    /** 垫染梦土 + 雕像；可选铺四角迷梦冶梦莲。 */
    private static void setupFoxPad(ServerLevel level, BlockPos center, boolean withFlowers) {
        for (int dx = -11; dx <= 11; dx++) {
            for (int dz = -11; dz <= 11; dz++) {
                level.setBlock(center.offset(dx, -1, dz), PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
                level.setBlock(center.offset(dx, 0, dz), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(center.offset(dx, 1, dz), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        level.setBlock(center, PDBlocks.GOLDEN_FOX_SCULPTURE.get().defaultBlockState(), 3);
        if (withFlowers) {
            int o = GoldenFoxSculptureBlock.RITUAL_OFFSET;
            placeFlower12(level, center.offset(o, 0, o));
            placeFlower12(level, center.offset(-o, 0, -o));
            placeFlower12(level, center.offset(o, 0, -o));
            placeFlower12(level, center.offset(-o, 0, o));
        }
    }

    private static void forceSunriseWindow(ServerLevel level) {
        // dayTime % 24000 ∈ [0,450]
        forceDayTime(level, 200L);
    }

    /**
     * 写入世界日时。各维共享 PrimaryLevelData 时以 overworld 为准，两边都 set 并 updateSkyBrightness。
     */
    private static void forceDayTime(ServerLevel level, long time) {
        MinecraftServer server = level.getServer();
        if (server != null) {
            server.overworld().setDayTime(time);
            server.overworld().updateSkyBrightness();
        }
        level.setDayTime(time);
        level.updateSkyBrightness();
    }

    private static void placeFlower12(ServerLevel level, BlockPos lower) {
        // 下为染梦土，双格 flower_12
        level.setBlock(lower.below(), PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
        BlockState plant = PDBlocks.FLOWER_12.get().defaultBlockState();
        level.setBlock(lower, plant.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(lower.above(), plant.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 3);
    }

    // ==================== flower_12 多方块 ====================

    private static void verifyFlower12Multiblock(ServerLevel level, BlockPos origin, Consumer<Result> out) {
        clearArea(level, origin, 4);
        BlockPos soil = origin.below();
        level.setBlock(soil, PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
        BlockState plant = PDBlocks.FLOWER_12.get().defaultBlockState();
        level.setBlock(origin, plant.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(origin.above(), plant.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 3);

        boolean lowerOk = level.getBlockState(origin).is(PDBlocks.FLOWER_12.get())
                && level.getBlockState(origin).getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER;
        boolean upperOk = level.getBlockState(origin.above()).is(PDBlocks.FLOWER_12.get())
                && level.getBlockState(origin.above()).getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
        boolean survive = level.getBlockState(origin).canSurvive(level, origin);
        out.accept(new Result(lowerOk && upperOk && survive,
                "dyedream.flower12.pair",
                "lower=" + lowerOk + " upper=" + upperOk + " survive=" + survive));

        // 非染梦土不可存活
        BlockPos bad = origin.offset(3, 0, 0);
        level.setBlock(bad.below(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(bad, plant.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 3);
        boolean noSurviveOnStone = !level.getBlockState(bad).canSurvive(level, bad);
        out.accept(new Result(noSurviveOnStone, "dyedream.flower12.need_dyedream_soil",
                "canSurvive on stone=" + !noSurviveOnStone));
    }

    /**
     * 双层植物采集：剪刀/园艺钳破坏上下半格均掉 1，空手上下半格均不掉。
     *
     * @param level  染梦世界
     * @param player 验证玩家
     * @param origin 测试原点（下半格）
     * @param out    结果接收器
     */
    private static void verifyDoublePlantDrops(ServerLevel level, ServerPlayer player, BlockPos origin,
                                               Consumer<Result> out) {
        GameType oldMode = player.gameMode.getGameModeForPlayer();
        player.setGameMode(GameType.SURVIVAL);
        try {
            ItemStack shears = new ItemStack(Items.SHEARS);
            ItemStack pliers = new ItemStack(PDItems.PLIERS.get());
            boolean flower11Tool = bothHalvesDrop(level, player, PDBlocks.FLOWER_11.get(), origin, shears, 1)
                    && bothHalvesDrop(level, player, PDBlocks.FLOWER_11.get(), origin, pliers, 1);
            boolean flower11Empty = bothHalvesDrop(level, player, PDBlocks.FLOWER_11.get(), origin, ItemStack.EMPTY, 0);
            out.accept(new Result(flower11Tool, "dyedream.flower11.drops", "shears/pliers lower+upper=1"));
            out.accept(new Result(flower11Empty, "dyedream.flower11.no_drop_empty", "empty lower+upper=0"));

            boolean flower12Tool = bothHalvesDrop(level, player, PDBlocks.FLOWER_12.get(), origin, shears, 1)
                    && bothHalvesDrop(level, player, PDBlocks.FLOWER_12.get(), origin, pliers, 1);
            boolean flower12Empty = bothHalvesDrop(level, player, PDBlocks.FLOWER_12.get(), origin, ItemStack.EMPTY, 0);
            out.accept(new Result(flower12Tool, "dyedream.flower12.drops", "shears/pliers lower+upper=1"));
            out.accept(new Result(flower12Empty, "dyedream.flower12.no_drop_empty", "empty lower+upper=0"));
        } finally {
            clearItemEntities(level, origin, 6);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            player.setGameMode(oldMode);
        }
    }

    /**
     * 分别破坏上下半格，检查掉落数量是否均符合预期。
     *
     * @param level    染梦世界
     * @param player   验证玩家
     * @param block    双层植物方块
     * @param origin   下半格坐标
     * @param tool     主手工具
     * @param expected 每次破坏的期望掉落数量
     * @return 上下半格掉落数量均匹配返回 true
     */
    private static boolean bothHalvesDrop(ServerLevel level, ServerPlayer player, Block block, BlockPos origin,
                                          ItemStack tool, int expected) {
        return breakDropCount(level, player, block, origin, false, tool) == expected
                && breakDropCount(level, player, block, origin, true, tool) == expected;
    }

    /**
     * 重新摆放双层植物、破坏指定半格并统计本体掉落数量。
     *
     * @param level     染梦世界
     * @param player    验证玩家
     * @param block     双层植物方块
     * @param lowerPos  下半格坐标
     * @param breakUpper 是否破坏上半格
     * @param tool      主手工具
     * @return 本体掉落数量
     */
    private static int breakDropCount(ServerLevel level, ServerPlayer player, Block block, BlockPos lowerPos,
                                      boolean breakUpper, ItemStack tool) {
        placeDoublePlant(level, block, lowerPos);
        clearItemEntities(level, lowerPos, 4);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        player.gameMode.destroyBlock(breakUpper ? lowerPos.above() : lowerPos);
        int count = 0;
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(lowerPos).inflate(4))) {
            if (entity.getItem().is(block.asItem())) {
                count += entity.getItem().getCount();
            }
            entity.discard();
        }
        return count;
    }

    /**
     * 在指定下半格位置摆放双层植物的上下两半。
     *
     * @param level    染梦世界
     * @param block    双层植物方块
     * @param lowerPos 下半格坐标
     */
    private static void placeDoublePlant(ServerLevel level, Block block, BlockPos lowerPos) {
        clearArea(level, lowerPos, 2);
        level.setBlock(lowerPos.below(), PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
        BlockState plant = block.defaultBlockState();
        level.setBlock(lowerPos, plant.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(lowerPos.above(), plant.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 3);
    }

    /**
     * 清理指定范围内的掉落物实体。
     *
     * @param level  染梦世界
     * @param center 中心坐标
     * @param range  范围半径
     */
    private static void clearItemEntities(ServerLevel level, BlockPos center, double range) {
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(center).inflate(range))) {
            entity.discard();
        }
    }

    // ==================== dyedream_lotus ====================

    private static void verifyDyedreamLotus(ServerLevel level, BlockPos origin, Consumer<Result> out) {
        clearArea(level, origin, 3);
        // 水面上
        level.setBlock(origin.below(), Blocks.WATER.defaultBlockState(), 3);
        BlockState lotus = PDBlocks.DYEDREAM_LOTUS.get().defaultBlockState();
        boolean canOnWater = lotus.canSurvive(level, origin);
        level.setBlock(origin, lotus, 3);
        boolean placed = level.getBlockState(origin).is(PDBlocks.DYEDREAM_LOTUS.get());
        out.accept(new Result(canOnWater && placed, "dyedream.lotus.on_water",
                "canSurvive=" + canOnWater + " placed=" + placed));

        // 土上不可
        BlockPos dirtPos = origin.offset(2, 0, 0);
        level.setBlock(dirtPos.below(), Blocks.DIRT.defaultBlockState(), 3);
        boolean noOnDirt = !lotus.canSurvive(level, dirtPos);
        out.accept(new Result(noOnDirt, "dyedream.lotus.not_on_dirt", "canSurvive on dirt=" + !noOnDirt));
    }

    // ==================== 工具 ====================

    private static void preparePad(ServerLevel level, BlockPos base) {
        for (int dx = -PAD; dx <= PAD; dx++) {
            for (int dz = -PAD; dz <= PAD; dz++) {
                for (int dy = -2; dy <= 4; dy++) {
                    BlockPos p = base.offset(dx, dy, dz);
                    if (dy < 0) {
                        level.setBlock(p, PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
                    } else if (dy == 0) {
                        level.setBlock(p, PDBlocks.DYEDREAM_GRASS.get().defaultBlockState(), 3);
                    } else {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private static void clearArea(ServerLevel level, BlockPos center, int r) {
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                for (int dy = -1; dy <= 3; dy++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    if (dy < 0) {
                        level.setBlock(p, PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
                    } else {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    // ==================== 花园解密（flower_11 → flower_12） ====================

    private static void verifyGardenDecryption(ServerLevel level, ServerPlayer player, BlockPos origin, Consumer<Result> out) {
        // 负例：花阵缺边 → 右键无反应
        setupGardenDecryption(level, origin, false);
        useBlock(level, player, origin);
        boolean still11 = level.getBlockState(origin).is(PDBlocks.FLOWER_11.get());
        boolean deskStill = level.getBlockState(origin.below(3)).is(PDBlocks.DYEDREAM_DESK.get());
        out.accept(new Result(still11 && deskStill, "dyedream.garden.fail_wrong_layout",
                "flower_11=" + still11 + " desk=" + deskStill));

        // 正例：完整花阵 → 延迟 1 tick 花阵销毁、2 tick 花 11→12，书桌保留
        setupGardenDecryption(level, origin, true);
        useBlock(level, player, origin);
        // 0 tick：花阵尚未销毁（queueServerWork(1) 未到期）
        boolean intact0 = isGardenLayoutIntact(level, origin);
        ServerScheduler.advanceForTest(1);
        boolean flowersGone = !isGardenLayoutIntact(level, origin);
        ServerScheduler.advanceForTest(1);
        boolean lower12 = level.getBlockState(origin).is(PDBlocks.FLOWER_12.get())
                && level.getBlockState(origin).getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER;
        boolean upper12 = level.getBlockState(origin.above()).is(PDBlocks.FLOWER_12.get())
                && level.getBlockState(origin.above()).getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
        boolean successDeskStill = level.getBlockState(origin.below(3)).is(PDBlocks.DYEDREAM_DESK.get());
        out.accept(new Result(intact0 && flowersGone && lower12 && upper12 && successDeskStill,
                "dyedream.garden.success",
                "intact0=" + intact0 + " flowersGone=" + flowersGone
                        + " flower12=" + lower12 + "/" + upper12 + " deskStill=" + successDeskStill));

        // 正例：点击上半格 → 与点击下半格等价
        setupGardenDecryption(level, origin, true);
        useBlock(level, player, origin.above());
        boolean upperIntact0 = isGardenLayoutIntact(level, origin);
        ServerScheduler.advanceForTest(1);
        boolean upperFlowersGone = !isGardenLayoutIntact(level, origin);
        ServerScheduler.advanceForTest(1);
        boolean upperLower12 = level.getBlockState(origin).is(PDBlocks.FLOWER_12.get())
                && level.getBlockState(origin).getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER;
        boolean upperUpper12 = level.getBlockState(origin.above()).is(PDBlocks.FLOWER_12.get())
                && level.getBlockState(origin.above()).getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
        boolean upperDeskStill = level.getBlockState(origin.below(3)).is(PDBlocks.DYEDREAM_DESK.get());
        out.accept(new Result(upperIntact0 && upperFlowersGone && upperLower12 && upperUpper12 && upperDeskStill,
                "dyedream.garden.success_from_upper",
                "intact0=" + upperIntact0 + " flowersGone=" + upperFlowersGone
                        + " flower12=" + upperLower12 + "/" + upperUpper12 + " deskStill=" + upperDeskStill));
    }

    /** 搭建花园解密布局：书桌（下 3 格）+ flower_11 双格 + 可选 8 格花阵 */
    private static void setupGardenDecryption(ServerLevel level, BlockPos origin, boolean fullLayout) {
        clearArea(level, origin, 3);
        level.setBlock(origin.below(4), PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
        level.setBlock(origin.below(3), PDBlocks.DYEDREAM_DESK.get().defaultBlockState(), 3);
        level.setBlock(origin.below(2), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(origin.below(1), PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
        BlockState plant = PDBlocks.FLOWER_11.get().defaultBlockState();
        level.setBlock(origin, plant.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(origin.above(), plant.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 3);
        if (fullLayout) {
            level.setBlock(origin.offset(-2, 0, -2), PDBlocks.FLOWER_13.get().defaultBlockState(), 3);
            level.setBlock(origin.offset(2, 0, -2), PDBlocks.CROP_3A.get().defaultBlockState(), 3);
            level.setBlock(origin.offset(2, 0, 2), PDBlocks.FLOWER_8.get().defaultBlockState(), 3);
            level.setBlock(origin.offset(-2, 0, 2), PDBlocks.CROP_2A.get().defaultBlockState(), 3);
            level.setBlock(origin.offset(2, 0, 0), PDBlocks.GRASS_3.get().defaultBlockState(), 3);
            level.setBlock(origin.offset(-2, 0, 0), PDBlocks.GRASS_3.get().defaultBlockState(), 3);
            level.setBlock(origin.offset(0, 0, 2), PDBlocks.GRASS_3.get().defaultBlockState(), 3);
            level.setBlock(origin.offset(0, 0, -2), PDBlocks.GRASS_3.get().defaultBlockState(), 3);
        }
    }

    /** 8 格花阵是否完整（flower_13/crop_3a/flower_8/crop_2a + grass_3×4） */
    private static boolean isGardenLayoutIntact(ServerLevel level, BlockPos origin) {
        return level.getBlockState(origin.offset(-2, 0, -2)).is(PDBlocks.FLOWER_13.get())
                && level.getBlockState(origin.offset(2, 0, -2)).is(PDBlocks.CROP_3A.get())
                && level.getBlockState(origin.offset(2, 0, 2)).is(PDBlocks.FLOWER_8.get())
                && level.getBlockState(origin.offset(-2, 0, 2)).is(PDBlocks.CROP_2A.get())
                && level.getBlockState(origin.offset(2, 0, 0)).is(PDBlocks.GRASS_3.get())
                && level.getBlockState(origin.offset(-2, 0, 0)).is(PDBlocks.GRASS_3.get())
                && level.getBlockState(origin.offset(0, 0, 2)).is(PDBlocks.GRASS_3.get())
                && level.getBlockState(origin.offset(0, 0, -2)).is(PDBlocks.GRASS_3.get());
    }

    // ==================== 雪傀儡解密（flower_16 → flower_17） ====================

    private static void verifyFlower16Decryption(ServerLevel level, ServerPlayer player, BlockPos origin, Consumer<Result> out) {
        // 负例：四方位花阵齐全但无雪傀儡/悦灵 → 右键无反应
        setupFlower16(level, origin, true);
        clearMobs(level, origin);
        useBlock(level, player, origin);
        boolean still16 = level.getBlockState(origin).is(PDBlocks.FLOWER_16.get());
        boolean deskStill = level.getBlockState(origin.below(3)).is(PDBlocks.DYEDREAM_DESK.get());
        out.accept(new Result(still16 && deskStill, "dyedream.snowpuzzle.fail_no_mobs",
                "flower_16=" + still16 + " desk=" + deskStill));

        // 正例：四方位花阵 + 雪傀儡/悦灵 → 同步解密
        setupFlower16(level, origin, true);
        clearMobs(level, origin);
        EntityType.SNOW_GOLEM.spawn(level, origin.offset(2, 0, 0), MobSpawnType.COMMAND);
        EntityType.ALLAY.spawn(level, origin.offset(0, 0, 2), MobSpawnType.COMMAND);
        useBlock(level, player, origin);
        boolean now17 = level.getBlockState(origin).is(PDBlocks.FLOWER_17.get());
        boolean successDeskStill = level.getBlockState(origin.below(3)).is(PDBlocks.DYEDREAM_DESK.get());
        boolean arrayGone = !level.getBlockState(origin.offset(0, 0, 5)).is(PDBlocks.CROP_0A.get())
                && !level.getBlockState(origin.offset(0, 0, -5)).is(PDBlocks.DYEDREAM_SAPLING.get())
                && !level.getBlockState(origin.offset(5, 0, 0)).is(PDBlocks.FLOWER_14.get())
                && !level.getBlockState(origin.offset(-5, 0, 0)).is(PDBlocks.FLOWER_9.get());
        boolean mobsGone = level.getEntitiesOfClass(SnowGolem.class, new AABB(origin).inflate(15)).isEmpty()
                && level.getEntitiesOfClass(Allay.class, new AABB(origin).inflate(15)).isEmpty();
        out.accept(new Result(now17 && successDeskStill && arrayGone && mobsGone,
                "dyedream.snowpuzzle.success",
                "flower17=" + now17 + " deskStill=" + successDeskStill + " arrayGone=" + arrayGone
                        + " mobsGone=" + mobsGone));
    }

    /** 搭建雪傀儡解密布局：书桌（下 3 格）+ flower_16 + 可选四方位 5 格花阵 */
    private static void setupFlower16(ServerLevel level, BlockPos origin, boolean fullLayout) {
        clearArea(level, origin, 7);
        level.setBlock(origin.below(4), PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
        level.setBlock(origin.below(3), PDBlocks.DYEDREAM_DESK.get().defaultBlockState(), 3);
        level.setBlock(origin.below(2), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(origin.below(1), PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
        level.setBlock(origin, PDBlocks.FLOWER_16.get().defaultBlockState(), 3);
        if (fullLayout) {
            level.setBlock(origin.offset(0, 0, 5), PDBlocks.CROP_0A.get().defaultBlockState(), 3);
            level.setBlock(origin.offset(0, 0, -5), PDBlocks.DYEDREAM_SAPLING.get().defaultBlockState(), 3);
            level.setBlock(origin.offset(5, 0, 0), PDBlocks.FLOWER_14.get().defaultBlockState(), 3);
            level.setBlock(origin.offset(-5, 0, 0), PDBlocks.FLOWER_9.get().defaultBlockState(), 3);
        }
    }

    /** 清理验证区域内的雪傀儡/悦灵残留 */
    private static void clearMobs(ServerLevel level, BlockPos origin) {
        level.getEntitiesOfClass(SnowGolem.class, new AABB(origin).inflate(15)).forEach(e -> e.discard());
        level.getEntitiesOfClass(Allay.class, new AABB(origin).inflate(15)).forEach(e -> e.discard());
    }

    // ==================== 冻结之花（flower_17 随机刻） ====================

    private static void verifyFlower17Freeze(ServerLevel level, BlockPos origin, Consumer<Result> out) {
        clearArea(level, origin, 5);
        // 铺染梦土支撑 + 左半空气、右半水（保证随机偏移高概率同时命中空气与水）
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                level.setBlock(origin.offset(dx, -1, dz), PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), 3);
                level.setBlock(origin.offset(dx, 0, dz), dx > 0 ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
            }
        }
        level.setBlock(origin, PDBlocks.FLOWER_17.get().defaultBlockState(), 3);
        boolean hasBe = level.getBlockEntity(origin) != null;
        // 触发随机刻冻结效果（对应 Flower17Pr0 + 8×Pr1）
        DyedreamFlower17Block.freezeAround(level, origin, level.getRandom());
        // 统计冻结结果
        int snow = 0;
        int ice = 0;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = -1; dy <= 0; dy++) {
                    BlockState s = level.getBlockState(origin.offset(dx, dy, dz));
                    if (s.is(Blocks.SNOW)) {
                        snow++;
                    }
                    if (s.is(Blocks.ICE)) {
                        ice++;
                    }
                }
            }
        }
        out.accept(new Result(hasBe && snow > 0 && ice > 0, "dyedream.flower17.freeze",
                "be=" + hasBe + " snow=" + snow + " ice=" + ice));
    }

    /** 以空手右键指定方块（走 BlockState.useItemOn 交互路径） */
    private static void useBlock(ServerLevel level, ServerPlayer player, BlockPos pos) {
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        level.getBlockState(pos).useItemOn(ItemStack.EMPTY, level, player, InteractionHand.MAIN_HAND, hit);
    }

    private static long countFox(ServerLevel level, BlockPos center, double range) {
        List<GoldenFoxEntity> list = level.getEntitiesOfClass(
                GoldenFoxEntity.class,
                new AABB(center).inflate(range),
                GoldenFoxEntity::isAlive);
        return list.size();
    }
}
