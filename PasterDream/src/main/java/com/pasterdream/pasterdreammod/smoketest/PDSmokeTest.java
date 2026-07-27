package com.pasterdream.pasterdreammod.smoketest;

import com.mojang.logging.LogUtils;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.DreamCauldronBlockEntity;
import com.pasterdream.pasterdreammod.menu.DreamCauldronMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

/**
 * 启动自动冒烟测试驱动（PDSmokeTest）。
 * <p>
 * 仅在设置环境变量 {@code PASTERDREAM_SMOKETEST=1}（或 JVM 参数
 * {@code -Dpasterdream.smoketest=true}）时激活；正常游戏完全不受影响。
 * <p>
 * 激活后游戏启动全自动执行：
 * <ol>
 *     <li>标题界面 → 自动创建超平坦创造世界 "test-audit" 并进入（见 {@link Client}）</li>
 *     <li>注册表体检：Phase2 方块/物品、5 个法术物品、炼药锅链路物品、3 个成就</li>
 *     <li>发放测试物品到快捷栏、放置炼药锅与 Phase2 方块</li>
 *     <li>打开炼药锅 GUI → 注入融梦液体桶（验证 1000mB + 空桶回收）→
 *         填入引导药剂+矢车菊+红石+阴暗云 → 触发合成按钮 →
 *         验证 GUI 关闭、液体 900mB、材料消耗、闪电法术弹出</li>
 *     <li>关键节点自动截图到 run/screenshots/，全部结果以 [PDSmokeTest] 前缀写入日志</li>
 *     <li>完成后自动安全退出游戏进程</li>
 * </ol>
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class PDSmokeTest {

    /** 是否启用冒烟测试（环境变量或系统属性开关） */
    public static final boolean ENABLED =
            "1".equals(System.getenv("PASTERDREAM_SMOKETEST"))
                    || Boolean.getBoolean("pasterdream.smoketest");

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG = "[PDSmokeTest] ";

    // ==================== 测试阶段（客户端读取以决定截图时机） ====================
    static final int STAGE_NONE = 0;
    static final int STAGE_PREPARED = 1;
    static final int STAGE_GUI_OPENED = 2;
    static final int STAGE_BUCKET_DONE = 3;
    static final int STAGE_SLOTS_FILLED = 4;
    static final int STAGE_CRAFT_CLICKED = 5;
    static final int STAGE_DONE = 7;

    /** 当前阶段（服务端线程写、客户端渲染线程读） */
    static volatile int stage = STAGE_NONE;

    /** 冒烟测试中的玩家 UUID（登录事件记录） */
    private static UUID playerId;
    /** 玩家登录后的服务端 tick 计数 */
    private static int ticks = -1;
    /** 炼药锅放置位置 */
    private static BlockPos cauldronPos;
    /** 通过/失败计数 */
    private static int passCount;
    private static int failCount;

    /** Phase2 补全的 29 个方块 ID（用于注册表体检） */
    private static final List<String> PHASE2_BLOCK_IDS = List.of(
            "big_bubble", "breakwind_curtain", "carve_clarity_glass", "carve_clarity_glasspane",
            "chiseled_cyan_stone_bricks", "clarity_glass", "clarity_glasspane", "congeal_wind_block",
            "cyan_moss_stone", "cyan_stone", "cyan_stone_brick_slab", "cyan_stone_brick_stairs",
            "cyan_stone_brick_wall", "cyan_stone_bricks", "cyan_stone_button", "cyan_stone_pillar",
            "cyan_stone_pressure_plate", "frame_clarity_glass", "frame_clarity_glasspane",
            "mossy_cyan_stone_brick_slab", "mossy_cyan_stone_brick_stairs", "mossy_cyan_stone_brick_wall",
            "mossy_cyan_stone_bricks", "salt_block", "starcall_block", "starcall_crack",
            "white_sand", "windiron_bars", "windrunner_crystal_block");

    /** 5 个法术物品 ID */
    private static final List<String> SPELL_ITEM_IDS = List.of(
            "lightning_spell", "poison_spell", "healing_spell", "fury_spell", "ice_spell");

    /** 3 个本次修复的成就 ID */
    private static final List<String> ACHIEVEMENT_IDS = List.of(
            "achievement_shadow_c_0", "achievement_shadow_d_0", "achievement_shadow_e_0");

    private PDSmokeTest() {
    }

    // ==================== 断言与日志 ====================

    private static void check(boolean ok, String what) {
        if (ok) {
            passCount++;
            LOGGER.info(TAG + "PASS: {}", what);
        } else {
            failCount++;
            LOGGER.error(TAG + "FAIL: {}", what);
        }
    }

    private static Item item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    private static Block block(String id) {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id));
    }

    // ==================== 服务端事件 ====================

    /**
     * 玩家进入世界后启动测试时间线
     *
     * @param event 登录事件
     */
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!ENABLED || !(event.getEntity() instanceof ServerPlayer sp)) {
            return;
        }
        playerId = sp.getUUID();
        ticks = 0;
        LOGGER.info(TAG + "player logged in, smoke test timeline started");
    }

    /**
     * 服务端 tick 状态机：按固定时间线执行体检、放置、GUI 与炼药流程
     *
     * @param event 服务端 tick 事件
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!ENABLED || ticks < 0) {
            return;
        }
        MinecraftServer server = event.getServer();
        ServerPlayer player = playerId == null ? null : server.getPlayerList().getPlayer(playerId);
        if (player == null) {
            return;
        }
        ticks++;
        ServerLevel level = player.serverLevel();

        switch (ticks) {
            case 60 -> {
                level.setDayTime(6000);
                runRegistryChecks(server);
                giveItems(player);
            }
            case 80 -> placeBlocks(player, level);
            case 100 -> {
                if (level.getBlockEntity(cauldronPos) instanceof DreamCauldronBlockEntity cauldron) {
                    player.openMenu(cauldron, cauldronPos);
                    check(player.containerMenu instanceof DreamCauldronMenu, "cauldron GUI opened via openMenu");
                    stage = STAGE_GUI_OPENED;
                } else {
                    check(false, "cauldron block entity present at " + cauldronPos);
                    stage = STAGE_DONE;
                }
            }
            case 160 -> {
                if (menu(player) instanceof DreamCauldronMenu m) {
                    check(m.getFluidAmount() == 0, "initial fluid amount is 0mb (actual " + m.getFluidAmount() + ")");
                    cauldron(level).getItemHandler()
                            .setStackInSlot(4, new ItemStack(item("pasterdream:meltdream_liquid_bucket")));
                    LOGGER.info(TAG + "meltdream_liquid_bucket inserted into slot 4");
                }
            }
            case 180 -> {
                DreamCauldronBlockEntity be = cauldron(level);
                check(be.getFluidAmount() == 1000,
                        "fluid amount is 1000mb after bucket (actual " + be.getFluidAmount() + ")");
                check(be.getItemHandler().getStackInSlot(5).is(Items.BUCKET),
                        "empty bucket returned to slot 5");
                check(be.getItemHandler().getStackInSlot(4).isEmpty(), "bucket input slot 4 cleared");
                stage = STAGE_BUCKET_DONE;
            }
            case 240 -> {
                DreamCauldronBlockEntity be = cauldron(level);
                be.getItemHandler().setStackInSlot(0, new ItemStack(item("pasterdream:guiding_drug")));
                be.getItemHandler().setStackInSlot(1, new ItemStack(Items.CORNFLOWER));
                be.getItemHandler().setStackInSlot(2, new ItemStack(Items.REDSTONE));
                be.getItemHandler().setStackInSlot(3, new ItemStack(item("pasterdream:dark_cloud")));
                LOGGER.info(TAG + "lightning spell recipe placed: guiding_drug + cornflower + redstone + dark_cloud");
                stage = STAGE_SLOTS_FILLED;
            }
            case 300 -> {
                if (menu(player) instanceof DreamCauldronMenu m) {
                    boolean handled = m.clickMenuButton(player, DreamCauldronMenu.BUTTON_CRAFT);
                    check(handled, "craft button click handled by menu");
                } else {
                    check(false, "cauldron menu still open before craft click");
                }
                stage = STAGE_CRAFT_CLICKED;
            }
            case 303 -> {
                check(!(menu(player) instanceof DreamCauldronMenu), "GUI closed after craft started");
                check(cauldron(level).getFluidAmount() == 900,
                        "fluid amount is 900mb after craft start (actual " + cauldron(level).getFluidAmount() + ")");
            }
            case 380 -> {
                DreamCauldronBlockEntity be = cauldron(level);
                boolean consumed = true;
                for (int slot = 0; slot <= 3; slot++) {
                    consumed &= be.getItemHandler().getStackInSlot(slot).isEmpty();
                }
                check(consumed, "ingredient slots 0-3 consumed after craft");
                List<ItemEntity> drops = level.getEntitiesOfClass(ItemEntity.class,
                        new AABB(cauldronPos).inflate(4),
                        e -> e.getItem().is(item("pasterdream:lightning_spell")));
                check(!drops.isEmpty(), "lightning_spell item entity ejected above cauldron");
                check(be.getItemHandler().getStackInSlot(6).isEmpty(), "result slot 6 cleared after ejection");
            }
            case 420 -> {
                LOGGER.info(TAG + "==================================================");
                LOGGER.info(TAG + "SUMMARY: {} passed, {} failed", passCount, failCount);
                LOGGER.info(TAG + (failCount == 0 ? "RESULT: ALL PASS" : "RESULT: HAS FAILURES"));
                LOGGER.info(TAG + "COMPLETE");
                stage = STAGE_DONE;
            }
            default -> {
            }
        }
    }

    /** 当前玩家打开的菜单 */
    private static Object menu(ServerPlayer player) {
        return player.containerMenu;
    }

    /** 获取炼药锅方块实体（时间线内保证存在） */
    private static DreamCauldronBlockEntity cauldron(ServerLevel level) {
        return (DreamCauldronBlockEntity) level.getBlockEntity(cauldronPos);
    }

    /**
     * 注册表体检：Phase2 方块与对应物品、法术物品、炼药锅链路物品、粒子、成就
     *
     * @param server 服务器实例
     */
    private static void runRegistryChecks(MinecraftServer server) {
        int missingBlocks = 0;
        int missingItems = 0;
        for (String id : PHASE2_BLOCK_IDS) {
            ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, id);
            if (!BuiltInRegistries.BLOCK.containsKey(rl)) {
                missingBlocks++;
                LOGGER.error(TAG + "missing phase2 BLOCK registration: {}", rl);
            }
            if (!BuiltInRegistries.ITEM.containsKey(rl)) {
                missingItems++;
                LOGGER.error(TAG + "missing phase2 ITEM registration: {}", rl);
            }
        }
        check(missingBlocks == 0, "all 29 phase2 blocks registered (missing " + missingBlocks + ")");
        check(missingItems == 0, "all 29 phase2 block items registered (missing " + missingItems + ")");

        for (String id : SPELL_ITEM_IDS) {
            check(BuiltInRegistries.ITEM.containsKey(
                            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, id)),
                    "spell item registered: " + id);
        }
        for (String id : List.of("dream_cauldron", "guiding_drug", "meltdream_liquid_bucket", "dark_cloud")) {
            check(BuiltInRegistries.ITEM.containsKey(
                            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, id)),
                    "cauldron chain item registered: " + id);
        }
        check(BuiltInRegistries.PARTICLE_TYPE.containsKey(
                        ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dust_0_particle")),
                "particle type registered: dust_0_particle");
        check(server.registryAccess().registryOrThrow(Registries.BLOCK) != null, "registry access alive");

        for (String id : ACHIEVEMENT_IDS) {
            check(server.getAdvancements().get(
                            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, id)) != null,
                    "advancement loaded: " + id);
        }
    }

    /**
     * 发放测试物品到快捷栏（同时用于截图验证物品贴图）
     *
     * @param player 玩家
     */
    private static void giveItems(ServerPlayer player) {
        String[] hotbar = {
                "pasterdream:dream_cauldron", "pasterdream:guiding_drug",
                "pasterdream:meltdream_liquid_bucket", "minecraft:cornflower", "minecraft:redstone",
                "pasterdream:dark_cloud", "pasterdream:cyan_stone", "pasterdream:clarity_glass",
                "pasterdream:cyan_stone_bricks"};
        for (int i = 0; i < hotbar.length; i++) {
            Item it = item(hotbar[i]);
            check(it != Items.AIR, "give item resolvable: " + hotbar[i]);
            player.getInventory().setItem(i, new ItemStack(it));
        }
        LOGGER.info(TAG + "hotbar items granted");
        stage = STAGE_PREPARED;
    }

    /**
     * 放置炼药锅与 Phase2 样本方块，并把玩家传送到固定观察位
     *
     * @param player 玩家
     * @param level  服务端世界
     */
    private static void placeBlocks(ServerPlayer player, ServerLevel level) {
        BlockPos base = player.blockPosition();
        cauldronPos = base.offset(3, 0, 0);
        level.setBlockAndUpdate(cauldronPos, block("pasterdream:dream_cauldron").defaultBlockState());
        check(level.getBlockState(cauldronPos).is(block("pasterdream:dream_cauldron")),
                "dream_cauldron placed at " + cauldronPos);

        String[] samples = {"cyan_stone", "cyan_stone_bricks", "clarity_glass", "cyan_stone_pillar",
                "breakwind_curtain"};
        int[] zOffsets = {-2, -1, 1, 2, 3};
        for (int i = 0; i < samples.length; i++) {
            BlockPos pos = base.offset(3, 0, zOffsets[i]);
            Block b = block("pasterdream:" + samples[i]);
            check(b != net.minecraft.world.level.block.Blocks.AIR, "phase2 block resolvable: " + samples[i]);
            level.setBlockAndUpdate(pos, b.defaultBlockState());
            check(level.getBlockState(pos).is(b), "phase2 block placed: " + samples[i]);
        }
        player.teleportTo(level, base.getX() + 0.5, base.getY(), base.getZ() + 0.5, -90.0f, 15.0f);
        LOGGER.info(TAG + "blocks placed, player positioned for screenshots");
    }

    // ==================== 客户端：自动建世界 + 截图 + 退出 ====================

    /**
     * 客户端钩子：标题界面自动创建测试世界、按阶段截图、测试完成后安全退出
     */
    @EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
    public static final class Client {

        /** 是否已发起自动建世界 */
        private static boolean worldLaunched;
        /** 客户端观察到的最新阶段 */
        private static int seenStage = STAGE_NONE;
        /** 阶段延时截图倒计时（-1 = 无待办） */
        private static int shotCountdown = -1;
        /** 待截图文件名 */
        private static String shotName;
        /** 完成后退出倒计时 */
        private static int stopCountdown = -1;

        private Client() {
        }

        /**
         * 客户端 tick：驱动自动建世界、阶段截图与退出
         *
         * @param event 客户端 tick 事件
         */
        @SubscribeEvent
        public static void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
            // 移植验证（PDPortingVerifyTest）单独运行时复用本类的自动建世界逻辑
            if (!ENABLED && !PDPortingVerifyTest.ENABLED) {
                return;
            }
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            mc.options.pauseOnLostFocus = false;

            // 标题界面出现且尚未建世界 → 删除旧 test-audit 后自动创建超平坦创造世界
            if (!worldLaunched && mc.level == null
                    && mc.screen instanceof net.minecraft.client.gui.screens.TitleScreen) {
                worldLaunched = true;
                final String worldName = "test-audit";
                deleteExistingWorld(mc, worldName);
                LOGGER.info(TAG + "creating superflat creative test world '{}'", worldName);
                net.minecraft.world.level.LevelSettings settings = new net.minecraft.world.level.LevelSettings(
                        worldName,
                        net.minecraft.world.level.GameType.CREATIVE,
                        false,
                        net.minecraft.world.Difficulty.PEACEFUL,
                        true,
                        gameRules(),
                        net.minecraft.world.level.WorldDataConfiguration.DEFAULT);
                mc.createWorldOpenFlows().createFreshLevel(
                        worldName,
                        settings,
                        new net.minecraft.world.level.levelgen.WorldOptions(System.currentTimeMillis(), false, false),
                        registryAccess -> registryAccess
                                .registryOrThrow(Registries.WORLD_PRESET)
                                .getHolderOrThrow(net.minecraft.world.level.levelgen.presets.WorldPresets.FLAT)
                                .value().createWorldDimensions(),
                        mc.screen);
                return;
            }

            // 测试过程中若因失焦弹出暂停菜单则自动关闭，避免集成服暂停
            if (mc.level != null && stage != STAGE_NONE && stage != STAGE_DONE
                    && mc.screen instanceof net.minecraft.client.gui.screens.PauseScreen) {
                mc.setScreen(null);
            }

            // 阶段变化 → 安排延时截图
            if (stage != seenStage) {
                seenStage = stage;
                switch (stage) {
                    case STAGE_GUI_OPENED -> schedule(30, "smoketest_1_gui_0mb.png");
                    case STAGE_BUCKET_DONE -> schedule(10, "smoketest_2_gui_1000mb.png");
                    case STAGE_SLOTS_FILLED -> schedule(10, "smoketest_3_gui_filled.png");
                    case STAGE_CRAFT_CLICKED -> schedule(75, "smoketest_4_world_after_craft.png");
                    case STAGE_DONE -> {
                        schedule(20, "smoketest_5_final_view.png");
                        // 双开模式下等待移植验证测试完成后再退出（否则会截断其时间线）
                        // KEEP_OPEN 时永不自动退出，供人工观察方块总览
                        if (!PDPortingVerifyTest.ENABLED && !PDPortingVerifyTest.KEEP_OPEN) {
                            stopCountdown = 80;
                        }
                    }
                    default -> {
                    }
                }
            }

            if (shotCountdown > 0) {
                shotCountdown--;
            } else if (shotCountdown == 0) {
                shotCountdown = -1;
                String name = shotName;
                net.minecraft.client.Screenshot.grab(mc.gameDirectory, name,
                        mc.getMainRenderTarget(),
                        component -> LOGGER.info(TAG + "screenshot saved: {}", name));
            }

            // 双开模式：verify 完成后由此统一收尾退出（KEEP_OPEN 则保持打开）
            if (PDPortingVerifyTest.ENABLED && ENABLED
                    && stage == STAGE_DONE && PDPortingVerifyTest.done && stopCountdown < 0
                    && !PDPortingVerifyTest.KEEP_OPEN) {
                stopCountdown = 80;
            }

            if (stopCountdown > 0) {
                stopCountdown--;
            } else if (stopCountdown == 0) {
                stopCountdown = -1;
                LOGGER.info(TAG + "smoke test finished, stopping client");
                mc.stop();
            }
        }

        /**
         * 删除 saves/&lt;name&gt; 旧档，保证每次 VERIFY/SMOKE 都是全新世界。
         * 优先走 LevelStorageSource#deleteLevel；失败时递归删目录。
         */
        private static void deleteExistingWorld(net.minecraft.client.Minecraft mc, String worldName) {
            try {
                var levelSource = mc.getLevelSource();
                // 1.21 LevelStorageSource 提供 levelExists / deleteLevel
                boolean exists = false;
                try {
                    exists = (boolean) levelSource.getClass()
                            .getMethod("levelExists", String.class)
                            .invoke(levelSource, worldName);
                } catch (ReflectiveOperationException ignored) {
                    // 回退到文件系统探测
                    java.nio.file.Path dir = mc.gameDirectory.toPath().resolve("saves").resolve(worldName);
                    exists = java.nio.file.Files.isDirectory(dir);
                }
                if (!exists) {
                    LOGGER.info(TAG + "no existing world '{}' to delete", worldName);
                    return;
                }
                LOGGER.info(TAG + "deleting previous test world '{}'", worldName);
                try {
                    levelSource.getClass().getMethod("deleteLevel", String.class)
                            .invoke(levelSource, worldName);
                    LOGGER.info(TAG + "deleted world via LevelStorageSource: {}", worldName);
                    return;
                } catch (ReflectiveOperationException ex) {
                    LOGGER.warn(TAG + "LevelStorageSource.deleteLevel 不可用，改文件系统删除: {}", ex.toString());
                }
                java.nio.file.Path dir = mc.gameDirectory.toPath().resolve("saves").resolve(worldName);
                deleteRecursively(dir);
                LOGGER.info(TAG + "deleted world directory: {}", dir);
            } catch (Exception e) {
                LOGGER.error(TAG + "failed to delete old world '{}'", worldName, e);
            }
        }

        private static void deleteRecursively(java.nio.file.Path root) throws java.io.IOException {
            if (!java.nio.file.Files.exists(root)) {
                return;
            }
            try (var walk = java.nio.file.Files.walk(root)) {
                walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                    try {
                        java.nio.file.Files.deleteIfExists(p);
                    } catch (java.io.IOException ex) {
                        throw new java.io.UncheckedIOException(ex);
                    }
                });
            } catch (java.io.UncheckedIOException uio) {
                throw uio.getCause();
            }
        }

        /** 组装测试世界游戏规则（固定晴天白昼；关闭随机刻避免展台作物/树苗在观察期生长炸服） */
        private static net.minecraft.world.level.GameRules gameRules() {
            net.minecraft.world.level.GameRules rules = new net.minecraft.world.level.GameRules();
            rules.getRule(net.minecraft.world.level.GameRules.RULE_DAYLIGHT).set(false, null);
            rules.getRule(net.minecraft.world.level.GameRules.RULE_WEATHER_CYCLE).set(false, null);
            // 0 = 禁用随机刻（树苗/作物/火焰等）；人工观察方块总览时更稳定
            rules.getRule(net.minecraft.world.level.GameRules.RULE_RANDOMTICKING).set(0, null);
            return rules;
        }

        private static void schedule(int delayTicks, String fileName) {
            shotCountdown = delayTicks;
            shotName = fileName;
        }
    }
}
