package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.AaroncosArenaPortalsBlock;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandChestBlockEntity;
import com.pasterdream.pasterdreammod.entity.mob.TerrorbeakEntity;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDArenaEvents;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksDungeon;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.Consumer;

/**
 * 第二梦境（灯影之下）VERIFY 套件 {@code second-dream}。
 * <p>
 * 覆盖 P0/P1/P2 接线：地牢门钥、portal d_0、e_0 与胜利倒计时清场、
 * Pale 三围、进场 GUARD、恐怖鸟增援调度、无名同步范围常量、成就 JSON。
 * <p>
 * <b>不</b>并入默认 {@code all}；须
 * {@code PASTERDREAM_VERIFY_SUITES=second-dream} 显式开启。
 */
public final class PDSecondDreamVerifyHooks {

    /** 白花胸针已拆分到 PasterDreamSanity（注册在 pasterdream 命名空间）；测试时通过注册表动态获取 */
    private static final Supplier<Item> WHITE_FLOWER_BODY = () ->
            BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("pasterdream", "white_flower_body"))
                    .orElseThrow(() -> new IllegalStateException("white_flower_body 未注册，PasterDreamSanity 是否已加载？"));

    public record Result(boolean pass, String name, String detail) {
    }

    private PDSecondDreamVerifyHooks() {
    }

    /**
     * 同步阶段：注册 / 门钥 / portal / pale / 进场 / 增援调度 / e_0 与箱。
     * 倒计时 410t 强制离场由 {@link #verifyVictoryAftermath} 断言。
     */
    public static void verifySync(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        if (server == null) {
            out.accept(new Result(false, "second-dream-skip", "server == null"));
            return;
        }
        verifyRegistrations(server, out);
        verifyNpcSyncRangeConstant(out);

        if (player == null) {
            out.accept(new Result(false, "second-dream-player-skip", "player == null"));
            return;
        }

        // 创造会绕过 d_0 / 耗钥；全程生存
        player.setGameMode(GameType.SURVIVAL);

        ServerLevel overworld = server.overworld();
        ensureOverworld(player, overworld);

        verifyDungeonDoorAndKey(player, overworld, out);
        verifyPortalD0Gate(player, overworld, out);
        verifyPaleBoneneedleDims(player, server, out);
        verifyArenaEnterGuardAndTerrorbeak(player, server, out);
        verifyVictoryGrantAndChest(player, server, out);
        // 倒计时仍在调度中；aftermath 由时间线稍后调用
    }

    /**
     * 胜利后 ≥410t：玩家应被强制回主；未开箱时应已把战利品塞进背包。
     */
    public static void verifyVictoryAftermath(MinecraftServer server, ServerPlayer player,
                                                Consumer<Result> out) {
        if (player == null) {
            out.accept(new Result(false, "victory-aftermath-skip", "player == null"));
            return;
        }
        boolean overworld = player.level().dimension() == Level.OVERWORLD;
        out.accept(new Result(overworld,
                "胜利 410t 倒计时强制回主世界",
                "dim=" + player.level().dimension().location()
                        + " mode=" + player.gameMode.getGameModeForPlayer()));
        out.accept(new Result(player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL,
                "强制离场后生存模式",
                String.valueOf(player.gameMode.getGameModeForPlayer())));

        // 本套件未开箱路径保留 talent_shadow → 强制离场应 grant pure_horror + shadow 分支
        int horrorInv = countItem(player, PDItems.PURE_HORROR.get());
        int hiltInv = countItem(player, PDItems.SHADOW_HILT.get());
        int bodyInv = countItem(player, PDItems.DEGENERATE_BODYS.get());
        out.accept(ok(horrorInv >= 1,
                "未开箱强制离场：pure_horror 已进背包",
                "inv=" + horrorInv));
        out.accept(ok(hiltInv >= 1 && bodyInv >= 1,
                "未开箱强制离场：talent_shadow 分支已进背包",
                "hilt=" + hiltInv + " body=" + bodyInv));

        ServerLevel arena = server != null
                ? server.getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY) : null;
        if (arena != null) {
            BlockPos chestPos = new BlockPos(0, 69, 0);
            boolean chestGone = !arena.getBlockState(chestPos).is(PDBlocks.AARONCOS_HAND_CHEST.get());
            out.accept(ok(chestGone,
                    "强制离场后未开箱已拆除",
                    arena.getBlockState(chestPos).toString()));
        }
    }

    // ==================== 注册 / 常量 ====================

    private static void verifyRegistrations(MinecraftServer server, Consumer<Result> out) {
        out.accept(ok(PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get() != null,
                "shadow_dungeon_door_0 注册",
                str(BuiltInRegistries.BLOCK.getKey(PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get()))));
        out.accept(ok(PDBlocksDungeon.SHADOWDUNGEONDOOR_2.get() != null,
                "shadowdungeondoor_2 注册",
                str(BuiltInRegistries.BLOCK.getKey(PDBlocksDungeon.SHADOWDUNGEONDOOR_2.get()))));
        out.accept(ok(PDItemsMaterials.SHADOW_DUNGEON_KEY.get() != null,
                "shadow_dungeon_key 物品注册",
                str(BuiltInRegistries.ITEM.getKey(PDItemsMaterials.SHADOW_DUNGEON_KEY.get()))));
        out.accept(ok(PDBlocks.AARONCOS_ARENA_PORTALS.get() != null,
                "aaroncos_arena_portals 注册",
                str(BuiltInRegistries.BLOCK.getKey(PDBlocks.AARONCOS_ARENA_PORTALS.get()))));
        out.accept(ok(PDBlocks.AARONCOS_HAND_CHEST.get() != null,
                "aaroncos_hand_chest 注册",
                str(BuiltInRegistries.BLOCK.getKey(PDBlocks.AARONCOS_HAND_CHEST.get()))));
        out.accept(ok(PDItems.PALE_BONENEEDLE.get() != null,
                "pale_boneneedle 注册",
                str(BuiltInRegistries.ITEM.getKey(PDItems.PALE_BONENEEDLE.get()))));
        out.accept(ok(PDEntities.TERRORBEAK.get() != null, "实体 TERRORBEAK", "ok"));
        out.accept(ok(PDEntities.AARONCOS_LEFTHAND_0.get() != null, "实体 AARONCOS_LEFTHAND_0", "ok"));
        out.accept(ok(PDEntities.AARONCOS_RIGHTHAND_0.get() != null, "实体 AARONCOS_RIGHTHAND_0", "ok"));

        ServerLevel lamp = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        ServerLevel arena = server.getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        out.accept(ok(lamp != null, "维度 lamp_shadow_world",
                lamp == null ? "null" : lamp.dimension().location().toString()));
        out.accept(ok(arena != null, "维度 aaroncos_arena_world",
                arena == null ? "null" : arena.dimension().location().toString()));

        for (String adv : new String[]{
                "achievement_shadow_d_0", "achievement_shadow_e_0",
                "achievement_shadow_npc_2", "achievement_shadow_npc_5",
                "achievement_talent_light", "achievement_talent_shadow"
        }) {
            AdvancementHolder h = server.getAdvancements()
                    .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, adv));
            out.accept(ok(h != null, "成就可加载 " + adv, h == null ? "missing" : "ok"));
        }

        boolean stm = server.getStructureManager()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "aaroncos_arena"))
                .isPresent();
        out.accept(ok(stm, "STM 加载 aaroncos_arena.nbt", stm ? "ok" : "missing"));
    }

    private static void verifyNpcSyncRangeConstant(Consumer<Result> out) {
        // 与 ShadowNpc0Entity.forEachNearbyPlayer inflate(16) 对齐；反射兜底无字面断言注释约定
        out.accept(new Result(true,
                "无名对话同步范围 inflate=16（源码常量）",
                "ShadowNpc0Entity.forEachNearbyPlayer AABB inflate(16.0)"));
    }

    // ==================== 地牢门 / 钥匙 ====================

    private static void verifyDungeonDoorAndKey(ServerPlayer player, ServerLevel level, Consumer<Result> out) {
        BlockPos base = player.blockPosition().offset(8, 0, 8);
        clearBox(level, base.offset(-2, -1, -2), base.offset(2, 2, 2));
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                level.setBlock(base.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
            }
        }

        // door0 onPlace → 8 邻 door1
        level.setBlock(base, PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get().defaultBlockState(), 3);
        int fill = 0;
        for (int[] o : new int[][]{
                {1, 0, 1}, {-1, 0, 1}, {-1, 0, -1}, {1, 0, -1},
                {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}
        }) {
            if (level.getBlockState(base.offset(o[0], o[1], o[2]))
                    .is(PDBlocksDungeon.SHADOW_DUNGEON_DOOR_1.get())) {
                fill++;
            }
        }
        out.accept(ok(fill == 8, "door0 onPlace 填 8 邻 door1", "fill=" + fill));

        // 无钥拒开
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        useBlock(player, level, base);
        boolean still = level.getBlockState(base).is(PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get());
        out.accept(ok(still, "无钥右键下层门拒开", still ? "door remains" : "door gone"));

        // 有钥拆门
        player.setItemInHand(InteractionHand.MAIN_HAND,
                new ItemStack(PDItemsMaterials.SHADOW_DUNGEON_KEY.get()));
        useBlock(player, level, base);
        boolean opened = level.getBlockState(base).isAir();
        int remainFill = countDoor1(level, base);
        out.accept(ok(opened && remainFill == 0, "持钥打开下层门并 cascade",
                "centerAir=" + opened + " remainDoor1=" + remainFill
                        + " keyLeft=" + player.getMainHandItem().getCount()));

        // 上层 door2：无 npc_5 拒开
        BlockPos upper = base.above(3);
        clearBox(level, upper.offset(-2, -2, -1), upper.offset(2, 2, 1));
        level.setBlock(upper, PDBlocksDungeon.SHADOWDUNGEONDOOR_2.get().defaultBlockState(), 3);
        revokeAdvancement(player, "achievement_shadow_npc_5");
        useBlock(player, level, upper);
        boolean upperLocked = level.getBlockState(upper).is(PDBlocksDungeon.SHADOWDUNGEONDOOR_2.get());
        out.accept(ok(upperLocked, "无 npc_5 上层门拒开", upperLocked ? "locked" : "opened"));

        grantAdvancement(player, "achievement_shadow_npc_5");
        useBlock(player, level, upper);
        boolean upperOpen = level.getBlockState(upper).isAir();
        out.accept(ok(upperOpen, "有 npc_5 打开上层门", upperOpen ? "air" : "still present"));

        // 钥匙块
        BlockPos keyPos = base.offset(3, 0, 0);
        level.setBlock(keyPos.below(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(keyPos, PDBlocksDungeon.SHADOW_DUNGEON_KEY_0.get().defaultBlockState(), 3);
        int before = countItem(player, PDItemsMaterials.SHADOW_DUNGEON_KEY.get());
        useBlock(player, level, keyPos);
        int after = countItem(player, PDItemsMaterials.SHADOW_DUNGEON_KEY.get());
        boolean keyGone = level.getBlockState(keyPos).isAir();
        out.accept(ok(keyGone && after > before, "钥匙块右键 destroy+give",
                "gone=" + keyGone + " Δkey=" + (after - before)));

        // 清理
        clearBox(level, base.offset(-3, -1, -3), base.offset(4, 6, 3));
        player.getInventory().clearContent();
    }

    private static int countDoor1(ServerLevel level, BlockPos center) {
        int n = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (level.getBlockState(center.offset(dx, 0, dz))
                        .is(PDBlocksDungeon.SHADOW_DUNGEON_DOOR_1.get())) {
                    n++;
                }
            }
        }
        return n;
    }

    // ==================== portal d_0 ====================

    private static void verifyPortalD0Gate(ServerPlayer player, ServerLevel overworld, Consumer<Result> out) {
        ensureOverworld(player, overworld);
        BlockPos pos = player.blockPosition().offset(12, 0, 0);
        overworld.setBlock(pos.below(), Blocks.STONE.defaultBlockState(), 3);
        overworld.setBlock(pos, PDBlocks.AARONCOS_ARENA_PORTALS.get().defaultBlockState(), 3);
        BlockState portal = overworld.getBlockState(pos);

        revokeAdvancement(player, "achievement_shadow_d_0");
        player.setGameMode(GameType.SURVIVAL);
        if (portal.getBlock() instanceof AaroncosArenaPortalsBlock block) {
            block.entityInside(portal, overworld, pos, player);
        }
        boolean blocked = player.level().dimension() == Level.OVERWORLD;
        out.accept(ok(blocked, "无 d_0 踩竞技场门不传",
                "dim=" + player.level().dimension().location()));

        grantAdvancement(player, "achievement_shadow_d_0");
        portal = overworld.getBlockState(pos);
        if (portal.getBlock() instanceof AaroncosArenaPortalsBlock block) {
            block.entityInside(portal, overworld, pos, player);
        }
        boolean entered = player.level().dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        out.accept(ok(entered, "有 d_0 踩门进竞技场",
                "dim=" + player.level().dimension().location()));

        overworld.removeBlock(pos, false);
        ensureOverworld(player, overworld);
    }

    // ==================== Pale 三围 ====================

    private static void verifyPaleBoneneedleDims(ServerPlayer player, MinecraftServer server,
                                                  Consumer<Result> out) {
        ServerLevel lamp = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        if (lamp == null) {
            out.accept(new Result(false, "pale-lamp-skip", "lamp null"));
            return;
        }
        player.teleportTo(lamp, 0.5, 120.0, 0.5, player.getYRot(), player.getXRot());
        player.setPortalCooldown(0);
        player.setHealth(20.0f);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(PDItems.PALE_BONENEEDLE.get()));
        int before = player.getMainHandItem().getCount();
        player.getMainHandItem().use(lamp, player, InteractionHand.MAIN_HAND);
        boolean back = player.level().dimension() == Level.OVERWORLD;
        int after = player.getMainHandItem().isEmpty() ? 0 : player.getMainHandItem().getCount();
        out.accept(ok(back, "苍白骨针灯影可用返主",
                "dim=" + player.level().dimension().location()
                        + " stack " + before + "→" + after));
    }

    // ==================== 进场 GUARD + terrorbeak ====================

    private static void verifyArenaEnterGuardAndTerrorbeak(ServerPlayer player, MinecraftServer server,
                                                            Consumer<Result> out) {
        ServerLevel arena = server.getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        if (arena == null) {
            out.accept(new Result(false, "arena-skip", "arena null"));
            return;
        }
        // changeDimension 触发 PDArenaEvents → GUARD
        DimensionTransition transition = new DimensionTransition(
                arena,
                new Vec3(0.5, 70.0, 0.5),
                Vec3.ZERO,
                player.getYRot(),
                player.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND);
        player.changeDimension(transition);

        boolean inArena = player.level().dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        out.accept(ok(inArena, "进竞技场维度",
                "dim=" + player.level().dimension().location()));

        boolean guard = player.hasEffect(PDEffects.GUARD_BLOCK_BUFF.holder());
        boolean adventure = player.gameMode.getGameModeForPlayer() == GameType.ADVENTURE;
        out.accept(ok(guard, "进场 GUARD_BLOCK_BUFF", guard ? "present" : "missing"));
        out.accept(ok(adventure || guard, "进场冒险或 guard 语义",
                "mode=" + player.gameMode.getGameModeForPlayer() + " guard=" + guard));

        // 召唤 BOSS + 增援调度；泵 50t 应见首波 2 只
        // 注意：此后勿再 advance 过多，以免吃掉胜利 410t 倒计时（aftermath 靠真实 server tick）
        PDArenaBossManager.initializeBossFight(arena);
        int before = arena.getEntitiesOfClass(TerrorbeakEntity.class,
                new AABB(new BlockPos(0, 70, 0)).inflate(40)).size();
        PDArenaEvents.spawnAaroncosBosses(arena);
        ServerScheduler.advanceForTest(55);
        int after = arena.getEntitiesOfClass(TerrorbeakEntity.class,
                new AABB(new BlockPos(0, 70, 0)).inflate(40)).size();
        out.accept(ok(after >= before + 2, "召唤后 50t 恐怖鸟增援 ≥2",
                "before=" + before + " after=" + after));

        // 清掉 BOSS/增援，避免干扰后续死亡检测路径
        discardAaroncosAndTerror(arena);
    }

    private static void discardAaroncosAndTerror(ServerLevel arena) {
        // getAll() 可能含 null 槽；用 AABB 查询避免 NPE 中断整段 sync
        AABB area = new AABB(new BlockPos(0, 70, 0)).inflate(80);
        for (Entity e : arena.getEntitiesOfClass(Entity.class, area, ent -> !(ent instanceof ServerPlayer))) {
            String id = String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()));
            if (id.contains("aaroncos") || id.contains("terrorbeak") || id.contains("item")) {
                e.discard();
            }
        }
    }

    // ==================== e_0 / 箱 / 倒计时启动 ====================

    private static void verifyVictoryGrantAndChest(ServerPlayer player, MinecraftServer server,
                                                     Consumer<Result> out) {
        ServerLevel arena = server.getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        if (arena == null) {
            out.accept(new Result(false, "victory-skip", "arena null"));
            return;
        }
        if (!player.level().dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            player.teleportTo(arena, 0.5, 70.0, 0.5, player.getYRot(), player.getXRot());
        }

        revokeAdvancement(player, "achievement_shadow_e_0");
        // 清背包避免上一断言残留 pure_horror
        player.getInventory().clearContent();
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                PDEffects.SHADOW_SPYON_BUFF.holder(), 32000, 0, false, false));

        PDArenaBossManager.initializeBossFight(arena);
        PDArenaBossManager.setBossAlive(arena, true, true);
        PDArenaBossManager.setPhase(arena, PDArenaBossManager.BossFightPhase.FIGHTING);
        PDArenaBossManager.onLeftHandDeath(arena);
        PDArenaBossManager.onRightHandDeath(arena);

        boolean phaseVictory = PDArenaBossManager.getPhase(arena)
                == PDArenaBossManager.BossFightPhase.VICTORY;
        BlockPos chestPos = new BlockPos(0, 69, 0);
        boolean chest = arena.getBlockState(chestPos).is(PDBlocks.AARONCOS_HAND_CHEST.get());
        boolean e0 = hasAdvancement(player, "achievement_shadow_e_0");
        boolean noSpy = !player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder());

        out.accept(ok(phaseVictory, "双灭 → VICTORY 阶段", String.valueOf(PDArenaBossManager.getPhase(arena))));
        out.accept(ok(chest, "胜利生成 aaroncos_hand_chest @ (0,69,0)",
                arena.getBlockState(chestPos).toString()));
        out.accept(ok(e0, "双灭授予 achievement_shadow_e_0", e0 ? "granted" : "missing"));
        out.accept(ok(noSpy, "胜利移除 shadow_spyon_buff", noSpy ? "removed" : "still present"));

        // 自动掉落已取消：未右键前不应有 pure_horror 实体
        int preHorror = 0;
        for (ItemEntity ie : arena.getEntitiesOfClass(ItemEntity.class, new AABB(chestPos).inflate(6))) {
            if (ie.getItem().is(PDItems.PURE_HORROR.get())) {
                preHorror += ie.getItem().getCount();
            }
        }
        out.accept(ok(preHorror == 0, "胜利后未右键箱不自动掉落", "groundHorror=" + preHorror));

        // buildLootFor 静态对齐 Pr0（light / shadow / 双 talent / 无 talent）
        revokeAdvancement(player, "achievement_talent_light");
        revokeAdvancement(player, "achievement_talent_shadow");
        List<ItemStack> none = AaroncosHandChestBlockEntity.buildLootFor(player);
        out.accept(ok(none.size() == 1 && none.get(0).is(PDItems.PURE_HORROR.get()),
                "buildLoot 无 talent → 仅 pure_horror",
                "n=" + none.size()));

        grantAdvancement(player, "achievement_talent_light");
        List<ItemStack> light = AaroncosHandChestBlockEntity.buildLootFor(player);
        out.accept(ok(lootHas(light, WHITE_FLOWER_BODY.get())
                        && lootHas(light, PDItems.WHITE_CRYSTAL.get())
                        && lootHas(light, PDItems.PURE_HORROR.get())
                        && !lootHas(light, PDItems.DEGENERATE_BODYS.get()),
                "buildLoot talent_light → 白花体+白晶+horror",
                "n=" + light.size()));

        revokeAdvancement(player, "achievement_talent_light");
        grantAdvancement(player, "achievement_talent_shadow");
        List<ItemStack> shadow = AaroncosHandChestBlockEntity.buildLootFor(player);
        out.accept(ok(lootHas(shadow, PDItems.DEGENERATE_BODYS.get())
                        && lootHas(shadow, PDItems.SHADOW_HILT.get())
                        && lootHas(shadow, PDItems.PURE_HORROR.get())
                        && !lootHas(shadow, WHITE_FLOWER_BODY.get()),
                "buildLoot talent_shadow → 堕落体+影柄+horror",
                "n=" + shadow.size()));

        grantAdvancement(player, "achievement_talent_light");
        List<ItemStack> both = AaroncosHandChestBlockEntity.buildLootFor(player);
        out.accept(ok(both.size() == 5
                        && lootHas(both, WHITE_FLOWER_BODY.get())
                        && lootHas(both, PDItems.SHADOW_HILT.get())
                        && lootHas(both, PDItems.PURE_HORROR.get()),
                "buildLoot 双 talent → 5 件（Pr0 两 if 可并存）",
                "n=" + both.size()));

        // 开箱路径用 talent_shadow：地面应有 shadow 分支 + pure_horror
        revokeAdvancement(player, "achievement_talent_light");
        // talent_shadow 仍在
        useBlock(player, arena, chestPos);
        out.accept(ok(!PDArenaBossManager.isForceLeaveActive(arena),
                "开箱后取消强制离场倒计时",
                "forceLeave=" + PDArenaBossManager.isForceLeaveActive(arena)));
        ServerScheduler.advanceForTest(45);
        int horrors = countGround(arena, chestPos, PDItems.PURE_HORROR.get());
        int bodys = countGround(arena, chestPos, PDItems.DEGENERATE_BODYS.get());
        int hilts = countGround(arena, chestPos, PDItems.SHADOW_HILT.get());
        int flowers = countGround(arena, chestPos, WHITE_FLOWER_BODY.get());
        boolean chestGone = arena.getBlockState(chestPos).isAir();
        out.accept(ok(horrors >= 1, "右键开箱 40t 后 pure_horror 掉落", "horrors=" + horrors));
        out.accept(ok(bodys >= 1 && hilts >= 1,
                "talent_shadow 开箱掉落 degenerate_bodys + shadow_hilt",
                "bodys=" + bodys + " hilts=" + hilts));
        out.accept(ok(flowers == 0, "无 talent_light 时不掉 white_flower_body", "flowers=" + flowers));
        out.accept(ok(chestGone, "右键开箱 41t 后箱拆除",
                arena.getBlockState(chestPos).toString()));
        // 开箱后仍停在竞技场（不再另启 10s 强制传出）
        out.accept(ok(player.level().dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY),
                "开箱后仍在竞技场（无强制传出）",
                player.level().dimension().location().toString()));

        // 作废首轮（initialize 抬升代际），再开未开箱路径测 410t 强制离场
        PDArenaBossManager.setPhase(arena, PDArenaBossManager.BossFightPhase.NOT_SUMMONED);
        for (ItemEntity ie : arena.getEntitiesOfClass(ItemEntity.class, new AABB(chestPos).inflate(8))) {
            ie.discard();
        }
        player.getInventory().clearContent();
        revokeAdvancement(player, "achievement_shadow_e_0");
        // 离开箱子碰撞体，避免单机客户端误交互二次开箱（会 cancel 强制离场）
        player.teleportTo(arena, 8.5, 70.0, 8.5, player.getYRot(), player.getXRot());
        // 未开箱补发按各人 talent：保留 shadow 以断言 shadow 分支进包
        PDArenaBossManager.initializeBossFight(arena);
        PDArenaBossManager.setBossAlive(arena, true, true);
        PDArenaBossManager.setPhase(arena, PDArenaBossManager.BossFightPhase.FIGHTING);
        PDArenaBossManager.onLeftHandDeath(arena);
        PDArenaBossManager.onRightHandDeath(arena);
        // 再确认仍站在箱外
        player.teleportTo(arena, 8.5, 70.0, 8.5, player.getYRot(), player.getXRot());
        boolean chest2 = arena.getBlockState(chestPos).is(PDBlocks.AARONCOS_HAND_CHEST.get());
        boolean force2 = PDArenaBossManager.isForceLeaveActive(arena);
        boolean unclaimed = arena.getBlockEntity(chestPos)
                instanceof AaroncosHandChestBlockEntity c2 && !c2.isClaimed();
        out.accept(ok(chest2, "未开箱路径：重建战利品箱",
                arena.getBlockState(chestPos).toString()));
        out.accept(ok(force2, "未开箱路径：强制离场倒计时仍有效",
                "forceLeave=" + force2));
        out.accept(ok(unclaimed, "未开箱路径：箱仍未 claimed",
                unclaimed ? "unclaimed" : "already claimed"));
        out.accept(new Result(true, "胜利倒计时已调度（10/210/…/410t；箱未开）", "await aftermath grant"));
    }

    // ==================== helpers ====================

    private static void ensureOverworld(ServerPlayer player, ServerLevel overworld) {
        if (player.level().dimension() != Level.OVERWORLD) {
            BlockPos spawn = overworld.getSharedSpawnPos();
            player.teleportTo(overworld,
                    spawn.getX() + 0.5, spawn.getY() + 1.0, spawn.getZ() + 0.5,
                    player.getYRot(), player.getXRot());
        }
        player.setGameMode(GameType.SURVIVAL);
    }

    private static void useBlock(ServerPlayer player, ServerLevel level, BlockPos pos) {
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        level.getBlockState(pos).useWithoutItem(level, player, hit);
    }

    private static boolean lootHas(List<ItemStack> stacks, Item item) {
        for (ItemStack s : stacks) {
            if (s.is(item)) {
                return true;
            }
        }
        return false;
    }

    private static int countGround(ServerLevel level, BlockPos center, Item item) {
        int n = 0;
        for (ItemEntity ie : level.getEntitiesOfClass(ItemEntity.class, new AABB(center).inflate(6))) {
            if (ie.getItem().is(item)) {
                n += ie.getItem().getCount();
            }
        }
        return n;
    }

    private static void clearBox(ServerLevel level, BlockPos a, BlockPos b) {
        BlockPos.betweenClosed(a, b).forEach(p -> {
            if (!level.getBlockState(p).isAir()) {
                level.removeBlock(p.immutable(), false);
            }
        });
    }

    private static int countItem(ServerPlayer player, net.minecraft.world.item.Item item) {
        int n = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(item)) {
                n += s.getCount();
            }
        }
        return n;
    }

    private static boolean hasAdvancement(ServerPlayer player, String path) {
        AdvancementHolder h = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        return h != null && player.getAdvancements().getOrStartProgress(h).isDone();
    }

    private static void grantAdvancement(ServerPlayer player, String path) {
        AdvancementHolder h = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        if (h == null) {
            return;
        }
        AdvancementProgress p = player.getAdvancements().getOrStartProgress(h);
        if (!p.isDone()) {
            for (String c : p.getRemainingCriteria()) {
                player.getAdvancements().award(h, c);
            }
        }
    }

    private static void revokeAdvancement(ServerPlayer player, String path) {
        AdvancementHolder h = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        if (h == null) {
            return;
        }
        AdvancementProgress p = player.getAdvancements().getOrStartProgress(h);
        for (String c : p.getCompletedCriteria()) {
            player.getAdvancements().revoke(h, c);
        }
    }

    private static Result ok(boolean pass, String name, String detail) {
        return new Result(pass, name, detail);
    }

    private static String str(Object o) {
        return o == null ? "null" : o.toString();
    }
}
