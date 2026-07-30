package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.block.DyedreamLotusBlock;
import com.pasterdream.pasterdreammod.block.GoldenFoxSculptureBlock;
import com.pasterdream.pasterdreammod.entity.mob.GoldenFoxEntity;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
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
 *   <li><b>迷梦冶梦莲多方块</b>：双格植物在染梦土上下半+上半成对；
 *       非染梦土 {@code canSurvive} 失败；下半掉落、上半不掉落。</li>
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
        verifyDyedreamLotus(dream, base.offset(-16, 1, 0), out);
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

        // 掉落：仅下半
        var lowerDrops = level.getBlockState(origin).getDrops(
                new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(origin))
                        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY));
        var upperDrops = level.getBlockState(origin.above()).getDrops(
                new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(origin.above()))
                        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY));
        boolean lowerDropsSelf = lowerDrops.stream().anyMatch(s -> s.is(PDBlocks.FLOWER_12.get().asItem()));
        boolean upperEmpty = upperDrops.isEmpty();
        out.accept(new Result(lowerDropsSelf && upperEmpty,
                "dyedream.flower12.drops",
                "lowerDropsSelf=" + lowerDropsSelf + " upperEmpty=" + upperEmpty));
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

    private static long countFox(ServerLevel level, BlockPos center, double range) {
        List<GoldenFoxEntity> list = level.getEntitiesOfClass(
                GoldenFoxEntity.class,
                new AABB(center).inflate(range),
                GoldenFoxEntity::isAlive);
        return list.size();
    }
}
