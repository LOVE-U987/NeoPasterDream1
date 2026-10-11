package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosLefthand0Entity;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosRighthand0Entity;
import com.pasterdream.pasterdreammod.entity.mob.TerrorbeakEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 竞技场维度事件处理类 —— 处理玩家进入亚伦柯斯竞技场时的初始化逻辑
 * <p>
 * 监听 {@link PlayerEvent.PlayerChangedDimensionEvent}，
 * 当玩家切换到亚伦柯斯竞技场维度时：
 * <ol>
 *   <li>在 (0, 70, 0) 位置放置 aaroncos_arena.nbt 结构</li>
 *   <li>清除 99 格半径内非玩家实体</li>
 *   <li>传送玩家到 (0, 70, 0)</li>
 *   <li>赋予玩家 30 秒缓降效果</li>
 * </ol>
 * <p>
 * 事件监听器需在 {@code PasterDreamMod} 构造器中通过
 * {@code NeoForge.EVENT_BUS.addListener(PDArenaEvents::onPlayerChangedDimension)}
 * 手动注册，因为 {@code PlayerChangedDimensionEvent} 在 Game 总线上分发。
 */
public class PDArenaEvents {

    /**
     * 竞技场结构文件资源路径
     * 对应文件位置：data/pasterdream/structure/aaroncos_arena.nbt
     */
    private static final ResourceLocation ARENA_STRUCTURE_ID = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "aaroncos_arena");

    /** 竞技场中心坐标 */
    private static final BlockPos ARENA_CENTER = new BlockPos(0, 70, 0);

    /** 非玩家实体清除半径 */
    private static final double CLEAR_RADIUS = 99.0;

    /** BOSS 生成位置偏移（左手右侧 +12，右手左侧 -12） */
    private static final int BOSS_SPREAD_DISTANCE = 12;

    /**
     * 监听玩家维度变化事件
     * <p>
     * 当玩家从任何维度传送到亚伦柯斯竞技场时触发。
     * 若竞技场中没有其他玩家（首个进入者），重新生成竞技场结构、清除非玩家实体
     * 并初始化 BOSS 战斗状态；已有其他玩家在场时不重置（保护进行中的战斗与胜利战利品箱）。
     * 所有玩家都会被传送到竞技场中心点并获得缓降效果。
     *
     * @param event 玩家维度变化事件
     */
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        // 检查目标维度是否为亚伦柯斯竞技场
        if (!event.getTo().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel arenaLevel)) {
            return;
        }

        // 仅当竞技场中没有其他玩家时才执行初始化：
        // 1) 首个进入者重置竞技场与 BOSS 状态——同时清除持久化残留的 VICTORY 阶段，
        //    避免"未召唤即被判定已胜利/右键即被传送"（SavedData 的 phase 跨会话保存）；
        // 2) 已有其他玩家在场（含胜利者仍留场开箱）时不重置，保护既有战斗与胜利战利品箱。
        boolean hasOtherPlayers = false;
        for (Player p : arenaLevel.players()) {
            if (p != entity) {
                hasOtherPlayers = true;
                break;
            }
        }
        if (!hasOtherPlayers) {
            placeArenaStructure(arenaLevel);
            clearNonPlayerEntities(arenaLevel);
            // ⚔️ 初始化 BOSS 战斗状态（未召唤状态）
            PDArenaBossManager.initializeBossFight(arenaLevel);
        }

        // 传送当前玩家到竞技场中心点（所有进入的玩家都执行）
        teleportPlayerToArena(entity);

        // 赋予缓降效果（30 秒，不显示粒子）—— 所有进入的玩家都执行
        if (entity instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0, false, false));
            // 原版进场 ADVENTURE：经 GUARD_BLOCK_BUFF onApply 切换（长时；结构内 guard 可续）
            player.addEffect(new MobEffectInstance(PDEffects.GUARD_BLOCK_BUFF.holder(), 72000, 0, false, false));
        }
    }

    /**
     * 在竞技场中放置 aaroncos_arena 结构
     * <p>
     * 使用结构模板管理器从 data/pasterdream/structure/aaroncos_arena.nbt
     * 加载结构并放置在世界中。
     * 使用同步方式确保结构模板在传送前正确加载。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    private static void placeArenaStructure(ServerLevel arenaLevel) {
        // 竞技场放置位置（结构左下角）
        BlockPos structurePos = new BlockPos(-35, 0, -35);

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false);

        // 结构资源位置（对应 data/pasterdream/structure/aaroncos_arena.nbt）
        // 使用 StructureManager.getOrCreate() 加载，路径不包含 .nbt 扩展名
        ResourceLocation structureId = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "aaroncos_arena");

        try {
            StructureTemplate template = arenaLevel.getStructureManager().getOrCreate(structureId);

            // centerPos 是结构中心相对于 structurePos 的偏移
            Vec3i size = template.getSize();
            BlockPos centerPos = structurePos.offset(size.getX() - 1, size.getY() - 1, size.getZ() - 1);

            PDDebugLogger.mainInfo("[PDArenaEvents] 🔍 结构尺寸: {}, 放置位置: {}, 中心偏移: {}",
                    size, structurePos, centerPos);

            boolean placed = template.placeInWorld(arenaLevel, structurePos, centerPos,
                    settings, arenaLevel.random, 3);
            if (placed) {
                PDDebugLogger.mainInfo("[PDArenaEvents] 🏟️ 已放置竞技场结构: {} → {}",
                        structureId, structurePos);
            } else {
                PasterDreamMod.LOGGER.warn("[PDArenaEvents] ⚠️ 结构放置返回 false，可能没有方块被放置");
            }
        } catch (Exception e) {
            // 边界层兜底：结构加载/放置涉及外部资源，单点失败不应导致竞技场初始化流程崩溃
            PasterDreamMod.LOGGER.error("[PDArenaEvents] ❌ 加载或放置竞技场结构时发生异常", e);
        }
    }

    /**
     * 清除竞技场中 99 格半径内的所有非玩家实体
     * <p>
     * 以 (0, 50, 0) 为中心清除区域，用于重置竞技场状态，
     * 移除上一次战斗残留的掉落物、弹射物等。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    private static void clearNonPlayerEntities(ServerLevel arenaLevel) {
        BlockPos clearCenter = new BlockPos(0, 50, 0);
        // Iterable<Entity> 不支持 stream()，使用 for-each 循环遍历
        for (Entity entity : arenaLevel.getEntities().getAll()) {
            if (!(entity instanceof ServerPlayer)
                    && entity.distanceToSqr(clearCenter.getX(), clearCenter.getY(), clearCenter.getZ())
                    <= CLEAR_RADIUS * CLEAR_RADIUS) {
                entity.discard();
            }
        }

        PDDebugLogger.mainDebug("[PDArenaEvents] 🧹 已清除竞技场非玩家实体");
    }

    /**
     * 传送玩家到竞技场中心点 (0, 70, 0)
     * <p>
     * 执行两次传送（带 1 tick 延迟），确保玩家位置准确同步到客户端。
     *
     * @param entity 要传送的实体（玩家）
     */
    private static void teleportPlayerToArena(Entity entity) {
        entity.teleportTo(ARENA_CENTER.getX() + 0.5, ARENA_CENTER.getY(), ARENA_CENTER.getZ() + 0.5);
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.teleport(
                    ARENA_CENTER.getX() + 0.5, ARENA_CENTER.getY(), ARENA_CENTER.getZ() + 0.5,
                    entity.getYRot(), entity.getXRot()
            );
        }
    }

    // ==================== BOSS 战斗逻辑 ====================

    /**
     * 在竞技场生成亚伦柯斯左右手 BOSS
     * <p>
     * 左手生成在 (12, 地面, 0)，右手生成在 (-12, 地面, 0)，
     * 两只手间隔 24 格，生成在地板上而非空中。
     * <p>
     * 生成时 BOSS 处于召唤状态，播放 spawn 动画，AI 和技能被禁用，
     * 动画结束后自动激活。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    public static void spawnAaroncosBosses(ServerLevel arenaLevel) {
        // 左手生成位置（右侧）—— 找到地面高度
        BlockPos leftHandPos = findGroundPosition(
                arenaLevel, ARENA_CENTER.offset(BOSS_SPREAD_DISTANCE, 0, 0));
        // 右手生成位置（左侧）—— 找到地面高度
        BlockPos rightHandPos = findGroundPosition(
                arenaLevel, ARENA_CENTER.offset(-BOSS_SPREAD_DISTANCE, 0, 0));

        AaroncosLefthand0Entity leftHand = null;
        AaroncosRighthand0Entity rightHand = null;

        // 生成左手 BOSS（召唤状态）
        leftHand = PDEntities.AARONCOS_LEFTHAND_0.get().create(arenaLevel);
        if (leftHand != null) {
            leftHand.moveTo(leftHandPos.getX() + 0.5, leftHandPos.getY(), leftHandPos.getZ() + 0.5,
                    180.0F, 0.0F); // 面朝中心
            leftHand.setSummoning(true);
            leftHand.finalizeSpawn(arenaLevel, arenaLevel.getCurrentDifficultyAt(leftHandPos),
                    MobSpawnType.MOB_SUMMONED, null);
            arenaLevel.addFreshEntity(leftHand);
            PDDebugLogger.mainInfo("[PDArenaEvents] 🖐️ 已生成左手 BOSS（召唤状态）: {}", leftHandPos);
        }

        // 生成右手 BOSS（召唤状态）
        rightHand = PDEntities.AARONCOS_RIGHTHAND_0.get().create(arenaLevel);
        if (rightHand != null) {
            rightHand.moveTo(rightHandPos.getX() + 0.5, rightHandPos.getY(), rightHandPos.getZ() + 0.5,
                    0.0F, 0.0F); // 面朝中心
            rightHand.setSummoning(true);
            rightHand.finalizeSpawn(arenaLevel, arenaLevel.getCurrentDifficultyAt(rightHandPos),
                    MobSpawnType.MOB_SUMMONED, null);
            arenaLevel.addFreshEntity(rightHand);
            PDDebugLogger.mainInfo("[PDArenaEvents] 🤚 已生成右手 BOSS（召唤状态）: {}", rightHandPos);
        }

        // 生成时的召唤粒子效果（两只手同时触发）
        arenaLevel.sendParticles(ParticleTypes.EXPLOSION,
                leftHandPos.getX() + 0.5, leftHandPos.getY() + 1.0, leftHandPos.getZ() + 0.5,
                32, 2, 2, 2, 0.5);
        arenaLevel.sendParticles(ParticleTypes.EXPLOSION,
                rightHandPos.getX() + 0.5, rightHandPos.getY() + 1.0, rightHandPos.getZ() + 0.5,
                32, 2, 2, 2, 0.5);

        // 播放召唤音效（对每个在场玩家——ARENA_CENTER 高空位置受 16 格衰减限制听不到）
        playArenaSoundForAll(arenaLevel, PDSounds.AARONCOS_SPAWN.get(), SoundSource.HOSTILE);

        // 播放 BOSS 战 BGM（亚伦柯斯之触）—— 战斗中循环，胜利/离场自动停止
        startBossMusicLoop(arenaLevel);

        // 更新战斗管理器中的存活状态
        PDArenaBossManager.setBossAlive(arenaLevel, leftHand != null, rightHand != null);

        // 原 handspawn Pr1：召唤后时序恐怖鸟增援
        spawnTerrorbeakReinforcements(arenaLevel);
    }

    /**
     * 调度 Terrorbeak 增援任务
     * <p>
     * 在 BOSS 生成后，分别于 50/100/150 tick 时各召唤 2 只 Terrorbeak，
     * 增援位置为竞技场中心南北两侧 (0, 70, 12) 和 (0, 70, -12)，
     * 与原模组 {@code AaroncoshandspawnblockPr1Procedure} 的增援时机一致。
     *
     * @param serverLevel 竞技场维度服务端世界
     */
    private static void spawnTerrorbeakReinforcements(ServerLevel serverLevel) {
        for (int delay : new int[]{50, 100, 150}) {
            ServerScheduler.schedule(delay, () -> {
                        spawnTerrorbeakAt(serverLevel, 0, 70, 12);
                        spawnTerrorbeakAt(serverLevel, 0, 70, -12);
                    });
        }
        PDDebugLogger.mainDebug("[PDArenaEvents] 🦅 已调度 Terrorbeak 增援：50/100/150 tick 后各召唤 2 只");
    }

    /**
     * 在指定坐标生成一只 Terrorbeak
     * <p>
     * 使用 {@link EntityType#create(ServerLevel)} 创建实体后调用 {@link Entity#moveTo(double, double, double)}
     * 设置位置，再通过 {@link ServerLevel#addFreshEntity(Entity)} 加入世界。
     *
     * @param serverLevel 竞技场维度服务端世界
     * @param x           生成坐标 X
     * @param y           生成坐标 Y
     * @param z           生成坐标 Z
     */
    private static void spawnTerrorbeakAt(ServerLevel serverLevel, double x, double y, double z) {
        TerrorbeakEntity terrorbeak = PDEntities.TERRORBEAK.get().create(serverLevel);
        if (terrorbeak != null) {
            terrorbeak.moveTo(x, y, z, serverLevel.random.nextFloat() * 360.0F, 0.0F);
            serverLevel.addFreshEntity(terrorbeak);
            PDDebugLogger.mainDebug("[PDArenaEvents] 🦅 已生成 Terrorbeak 增援于 ({}, {}, {})", x, y, z);
        } else {
            PasterDreamMod.LOGGER.warn("[PDArenaEvents] ⚠️ Terrorbeak 实体创建失败，无法生成增援");
        }
    }

    /**
     * 找到指定位置下方的地面位置（从给定位置向下查找第一个实心方块上方）
     * <p>
     * 从给定位置的 Y 坐标开始向下扫描，找到第一个不是空气的方块，
     * 返回该方块上方一格的位置。如果找不到地面，则返回原始位置。
     *
     * @param level    服务端世界
     * @param pos      起始位置（使用 X 和 Z 坐标）
     * @return 地面上方的位置
     */
    private static BlockPos findGroundPosition(ServerLevel level, BlockPos pos) {
        int startY = pos.getY();
        // 从起始位置向下查找，最低到 Y=-64（Minecraft 1.21 最低建筑高度）
        for (int y = startY; y >= -64; y--) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());
            if (!level.getBlockState(checkPos).isAir()) {
                // 找到实心方块，返回其上方一格
                BlockPos groundPos = checkPos.above();
                PDDebugLogger.mainDebug("[PDArenaEvents] 📍 找到地面位置: {} -> {}", pos, groundPos);
                return groundPos;
            }
        }
        // 没找到地面，返回原始位置
        PasterDreamMod.LOGGER.warn("[PDArenaEvents] ⚠️ 未找到地面位置，使用原始位置: {}", pos);
        return pos;
    }

    /**
     * 播放亚伦柯斯战斗音乐（单次，对竞技场所有在场玩家）
     * <p>
     * 使用 {@link ServerPlayer#playNotifySound} 播放：无位置、无距离衰减，
     * 全场清晰可闻（背景音乐语义）。
     * <p>
     * 不能用 {@code arenaLevel.playSound}：音源若放 {@code ARENA_CENTER}(0,70,0) 高空，
     * 玩家在 y≈42 战斗，距离 28 格远超 aaroncos_music 默认 16 格衰减范围，
     * 客户端音量衰减到 0 → BGM 从一开始就听不到。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    private static void playBossMusic(ServerLevel arenaLevel) {
        playArenaSoundForAll(arenaLevel, PDSounds.AARONCOS_MUSIC.get(), SoundSource.WEATHER);
        PDDebugLogger.mainDebug("[PDArenaEvents] 🎵 已播放亚伦柯斯战斗音乐");
    }

    /**
     * 对竞技场所有在场玩家播放指定音效（无位置、无衰减）
     * <p>
     * 遍历当前在竞技场维度的玩家逐个 {@link ServerPlayer#playNotifySound}，
     * 避免位置音效因音源过高/过远被距离衰减吞掉。
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @param event      要播放的音效
     * @param source     音源分类（决定客户端音量滑块归属）
     */
    private static void playArenaSoundForAll(ServerLevel arenaLevel, SoundEvent event, SoundSource source) {
        for (ServerPlayer player : new ArrayList<>(arenaLevel.players())) {
            player.playNotifySound(event, source, 1.0F, 1.0F);
        }
    }

    /**
     * BGM 循环重播间隔（tick）。
     * <p>
     * {@code aaroncos_music.ogg} 时长为 149.4 秒 ≈ 2988 tick；
     * 取 2980 tick 使其略短于整曲，重播时上一实例已近尾声，避免静音间隙。
     */
    private static final int BOSS_MUSIC_REPLAY_INTERVAL = 2980;

    /**
     * BGM 循环代际号 —— 每次召唤递增；旧代际的排队重播任务作废。
     * <p>
     * 防重复召唤（VICTORY 后再次右键）叠加多条 BGM 循环链。
     */
    private static int bossMusicLoopGen = 0;

    /**
     * 启动 BOSS 战 BGM 循环播放
     * <p>
     * 原模组 {@code AaroncoshandspawnblockPr1Procedure} 用 {@code time0} 计数器每 150 tick
     * 周期性重新播放 aaroncos_music 实现战斗循环；单次 {@link #playBossMusic} 播放完
     * 149 秒即静音，无法覆盖整场战斗，故改为服务端定时重播。
     * <p>
     * 循环规则：召唤时立即播放一次，之后每 {@link #BOSS_MUSIC_REPLAY_INTERVAL} tick 重播一次，
     * 直到战斗阶段离开 SUMMONING/FIGHTING（VICTORY 或初始化重置）或竞技场无玩家为止。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    private static void startBossMusicLoop(ServerLevel arenaLevel) {
        int gen = ++bossMusicLoopGen;
        playBossMusic(arenaLevel);
        scheduleBossMusicReplay(arenaLevel, gen);
    }

    /**
     * 调度下一轮 BGM 重播（内部循环链）
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @param gen        本轮召唤的 BGM 循环代际号
     */
    private static void scheduleBossMusicReplay(ServerLevel arenaLevel, int gen) {
        ServerScheduler.schedule(BOSS_MUSIC_REPLAY_INTERVAL, () -> {
            // 新召唤已开启新循环 → 旧代际作废
            if (gen != bossMusicLoopGen) {
                return;
            }
            if (arenaLevel == null || arenaLevel.isClientSide) {
                return;
            }
            // 战斗结束（VICTORY / 重新初始化）或竞技场无人 → 停止循环
            PDArenaBossManager.BossFightPhase phase = PDArenaBossManager.getPhase(arenaLevel);
            if (phase != PDArenaBossManager.BossFightPhase.SUMMONING
                    && phase != PDArenaBossManager.BossFightPhase.FIGHTING) {
                PDDebugLogger.mainDebug("[PDArenaEvents] 🎵 BGM 循环停止（phase={}）", phase);
                return;
            }
            if (arenaLevel.players().isEmpty()) {
                return;
            }
            playBossMusic(arenaLevel);
            scheduleBossMusicReplay(arenaLevel, gen);
        });
    }

}
