package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandChestBlockEntity;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosLefthand0Entity;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosRighthand0Entity;
import com.pasterdream.pasterdreammod.entity.mob.TerrorbeakEntity;
import com.pasterdream.pasterdreammod.world.PortalInfectionData;
import com.pasterdream.pasterdreammod.world.PortalRestorationHandler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 亚伦柯斯竞技场 BOSS 战斗管理器 —— 追踪左右手 BOSS 存活状态并生成战利品箱
 * <p>
 * 功能：
 * <ul>
 *   <li>使用维度持久化数据存储 BOSS 存活状态和战斗阶段</li>
 *   <li>检测两只手都死亡后生成战利品箱</li>
 *   <li>触发成就、移除效果、传送玩家回主世界</li>
 *   <li>管理战斗阶段：未召唤 → 召唤中 → 战斗中 → 已胜利</li>
 *   <li>胜利仅一条离开倒计时；开箱后取消强制离场，未开箱到期补发背包</li>
 * </ul>
 * <p>
 * 工作流程：
 * <ol>
 *   <li>玩家进入竞技场时初始化为未召唤状态</li>
 *   <li>玩家右键召唤方块后进入召唤中状态，播放 spawn 动画</li>
 *   <li>召唤动画结束后进入战斗中状态，BOSS AI 激活</li>
 *   <li>两只手都死亡后触发胜利序列（放箱 + 唯一倒计时）</li>
 *   <li>右键开箱掉落并取消强制离场；或未开箱 410t 补发并回主</li>
 * </ol>
 */
public class PDArenaBossManager {

    /**
     * BOSS 战斗阶段枚举
     */
    public enum BossFightPhase {
        /** 未召唤 —— 玩家进入竞技场后的初始状态 */
        NOT_SUMMONED,
        /** 召唤中 —— 正在播放 spawn 动画，AI 禁用 */
        SUMMONING,
        /** 战斗中 —— BOSS 正常战斗 */
        FIGHTING,
        /** 已胜利 —— 两只手都已死亡 */
        VICTORY
    }

    /** 竞技场中心坐标 */
    private static final BlockPos ARENA_CENTER = new BlockPos(0, 70, 0);

    /** BOSS 检测半径（99 格） */
    private static final double BOSS_CHECK_RADIUS = 99.0;

    /**
     * 维度持久化数据键名 —— 左手存活状态
     */
    private static final String LEFT_HAND_ALIVE_KEY = "AaroncosLeftHandAlive";

    /**
     * 维度持久化数据键名 —— 右手存活状态
     */
    private static final String RIGHT_HAND_ALIVE_KEY = "AaroncosRightHandAlive";

    /**
     * 维度持久化数据键名 —— 战斗阶段
     */
    private static final String BOSS_FIGHT_PHASE_KEY = "BossFightPhase";

    /**
     * 维度持久化数据键名 —— 是否仍调度强制离场
     */
    private static final String FORCE_LEAVE_ACTIVE_KEY = "ForceLeaveActive";

    /**
     * 维度持久化数据键名 —— 强制离场代际（作废陈旧 410t 回调）
     */
    private static final String FORCE_LEAVE_GEN_KEY = "ForceLeaveGen";

    /**
     * 维度持久化数据键名 —— 胜利后玩家返回的传送门位置
     */
    private static final String RETURN_PORTAL_POS_KEY = "ReturnPortalPos";

    /**
     * 初始化 BOSS 战斗状态（未召唤状态）
     * <p>
     * 玩家进入竞技场时调用，初始化为未召唤状态。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    public static void initializeBossFight(ServerLevel arenaLevel) {
        ArenaBossData data = getArenaBossData(arenaLevel);
        data.setLeftHandAlive(false);
        data.setRightHandAlive(false);
        data.setPhase(BossFightPhase.NOT_SUMMONED);
        // 抬升代际，使上一轮仍排队的 410t / 倒计时文案全部 no-op
        data.setForceLeaveActive(false);
        data.setForceLeaveGen(data.getForceLeaveGen() + 1);
        data.setDirty();
        PDDebugLogger.mainDebug("[PDArenaBossManager] ⚔️ 已初始化 BOSS 战斗状态（未召唤）");
    }

    /**
     * 玩家右键开箱后调用：取消本轮强制离场倒计时。
     * <p>
     * 只保留胜利瞬间调度的那一条倒计时；开箱后改由玩家自行捡物，
     * 再右键之眼离场（不再另启 10 秒强制传出）。
     */
    public static void cancelForceLeaveOnChestOpen(ServerLevel arenaLevel) {
        ArenaBossData data = getArenaBossData(arenaLevel);
        if (!data.isForceLeaveActive()) {
            return;
        }
        data.setForceLeaveActive(false);
        // 抬升代际：已排队的 410t 回调 gen 不匹配 → no-op
        data.setForceLeaveGen(data.getForceLeaveGen() + 1);
        data.setDirty();
        for (Player player : new ArrayList<>(arenaLevel.players())) {
            player.displayClientMessage(
                    Component.translatable("arena.pasterdream.loot_opened_leave_via_eye"), true);
        }
        PDDebugLogger.mainInfo("[PDArenaBossManager] 📦 已开箱，取消强制离场倒计时");
    }

    /**
     * @return 本轮胜利强制离场倒计时是否仍有效
     */
    public static boolean isForceLeaveActive(ServerLevel arenaLevel) {
        return getArenaBossData(arenaLevel).isForceLeaveActive();
    }

    /**
     * 获取当前战斗阶段
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @return 当前战斗阶段
     */
    public static BossFightPhase getPhase(ServerLevel arenaLevel) {
        ArenaBossData data = getArenaBossData(arenaLevel);
        return data.getPhase();
    }

    /**
     * 设置战斗阶段
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @param phase      新的战斗阶段
     */
    public static void setPhase(ServerLevel arenaLevel, BossFightPhase phase) {
        ArenaBossData data = getArenaBossData(arenaLevel);
        data.setPhase(phase);
        data.setDirty();
        PDDebugLogger.mainDebug("[PDArenaBossManager] 🔄 战斗阶段切换为: {}", phase);
    }

    /**
     * 触发 BOSS 召唤流程
     * <p>
     * 当玩家右键召唤方块时调用，将阶段切换为 SUMMONING，
     * 然后调用 PDArenaEvents 生成 BOSS。
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @return 是否成功触发召唤
     */
    public static boolean triggerBossSummon(ServerLevel arenaLevel) {
        BossFightPhase currentPhase = getPhase(arenaLevel);
        if (currentPhase != BossFightPhase.NOT_SUMMONED && currentPhase != BossFightPhase.VICTORY) {
            PasterDreamMod.LOGGER.warn("[PDArenaBossManager] ⚠️ 无法召唤 BOSS，当前阶段: {}", currentPhase);
            return false;
        }

        // 切换到召唤中阶段
        setPhase(arenaLevel, BossFightPhase.SUMMONING);

        // 调用 PDArenaEvents 生成 BOSS
        PDArenaEvents.spawnAaroncosBosses(arenaLevel);

        return true;
    }

    /**
     * 通知 BOSS 召唤动画完成，进入战斗阶段
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    public static void onSpawnAnimationComplete(ServerLevel arenaLevel) {
        if (getPhase(arenaLevel) == BossFightPhase.SUMMONING) {
            setPhase(arenaLevel, BossFightPhase.FIGHTING);
            PDDebugLogger.mainInfo("[PDArenaBossManager] ⚔️ BOSS 召唤完成，进入战斗阶段！");
        }
    }

    /**
     * 设置 BOSS 存活状态
     *
     * @param arenaLevel    竞技场维度服务端世界
     * @param leftHandAlive 左手是否存活
     * @param rightHandAlive 右手是否存活
     */
    public static void setBossAlive(ServerLevel arenaLevel, boolean leftHandAlive, boolean rightHandAlive) {
        ArenaBossData data = getArenaBossData(arenaLevel);
        data.setLeftHandAlive(leftHandAlive);
        data.setRightHandAlive(rightHandAlive);
        data.setDirty();
    }

    /**
     * 处理左手 BOSS 死亡事件
     * <p>
     * 更新维度数据并检测是否两只手都死亡。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    public static void onLeftHandDeath(ServerLevel arenaLevel) {
        ArenaBossData data = getArenaBossData(arenaLevel);
        data.setLeftHandAlive(false);
        data.setDirty();

        PDDebugLogger.mainDebug("[PDArenaBossManager] 💀 左手 BOSS 已死亡");

        // 检测是否两只手都死亡
        if (!data.isLeftHandAlive() && !data.isRightHandAlive()) {
            triggerVictorySequence(arenaLevel);
        }
    }

    /**
     * 处理右手 BOSS 死亡事件
     * <p>
     * 更新维度数据并检测是否两只手都死亡。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    public static void onRightHandDeath(ServerLevel arenaLevel) {
        ArenaBossData data = getArenaBossData(arenaLevel);
        data.setRightHandAlive(false);
        data.setDirty();

        PDDebugLogger.mainDebug("[PDArenaBossManager] 💀 右手 BOSS 已死亡");

        // 检测是否两只手都死亡
        if (!data.isLeftHandAlive() && !data.isRightHandAlive()) {
            triggerVictorySequence(arenaLevel);
        }
    }

    /**
     * 触发胜利序列 —— 生成战利品箱，等待玩家右键离开
     * <p>
     * 根据原模组逻辑（AaroncoshandspawnblockPr1Procedure），执行：
     * <ol>
     *   <li>在 (0, 69, 0) 生成战利品箱方块</li>
     *   <li>播放音效和粒子效果</li>
     *   <li>触发成就（achievement_shadow_e_0）</li>
     *   <li>移除暗影窥视效果</li>
     *   <li>显示胜利提示，玩家右键召唤方块离开</li>
     * </ol>
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    private static void triggerVictorySequence(ServerLevel arenaLevel) {
        PDDebugLogger.mainInfo("[PDArenaBossManager] 🎉 两只手都已死亡，触发胜利序列！");

        // 🏆 切换到 VICTORY 阶段（玩家右键召唤方块离开）
        setPhase(arenaLevel, BossFightPhase.VICTORY);

        // 🎁 生成战利品箱（竞技场中心下方一格）
        BlockPos chestPos = ARENA_CENTER.below();
        arenaLevel.setBlockAndUpdate(chestPos, PDBlocks.AARONCOS_HAND_CHEST.get().defaultBlockState());

        // 💫 战利品箱生成粒子效果（掉落改由玩家右键箱子触发，对齐 AaroncosHandChestPr0）
        arenaLevel.sendParticles(ParticleTypes.END_ROD,
                chestPos.getX() + 0.5, chestPos.getY() + 0.5, chestPos.getZ() + 0.5,
                16, 1, 1, 1, 0.2);
        arenaLevel.sendParticles(ParticleTypes.SMOKE,
                chestPos.getX() + 0.5, chestPos.getY() + 0.5, chestPos.getZ() + 0.5,
                24, 1, 1, 1, 0.2);

        // 🔊 播放战利品箱音效（使用 shadow0 音效）
        arenaLevel.playSound(null, chestPos,
                PDSounds.SHADOW_0.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);

        // 🏅 授予 e_0 + 移除暗影窥视（原 AaroncoshandspawnblockPr1 全场玩家）
        for (Player player : new ArrayList<>(arenaLevel.players())) {
            if (player instanceof ServerPlayer serverPlayer) {
                grantAdvancement(serverPlayer, "achievement_shadow_e_0");
                serverPlayer.removeEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
            }
            player.displayClientMessage(Component.translatable("arena.pasterdream.summon_victory"), true);
        }

        // 🌀 确定玩家返回的传送门位置（取主世界中第一个有感染记录的传送门）
        ServerLevel overworld = arenaLevel.getServer().overworld();
        PortalInfectionData infectionData = PortalInfectionData.get(overworld);
        List<BlockPos> portalPositions = infectionData.getPortalPositions();
        BlockPos returnPortal = portalPositions.isEmpty() ? null : portalPositions.get(0);

        ArenaBossData data = getArenaBossData(arenaLevel);
        data.setReturnPortalPos(returnPortal);
        data.setDirty();

        // 🌿 同步启动地形回滚：将主世界中所有被感染的传送门区域恢复为原始地形
        PortalRestorationHandler.startRestoration(overworld, portalPositions);

        // 🚪 立即将所有玩家从传送门位置送回主世界，与回滚同时开始
        teleportAllPlayersToOverworld(arenaLevel);

        // ⏱ 保留一条离场倒计时作为兜底：若有玩家因异常未传送，410t 后强制清场
        scheduleVictoryCountdown(arenaLevel);
    }

    /**
     * 唯一离开倒计时：10 / 210 / 310 / 350 / 400 tick 提示，410 tick 强制回主 + 清场。
     * 开箱 / 重开战后代际抬升，陈旧回调 no-op。
     */
    private static void scheduleVictoryCountdown(ServerLevel arenaLevel) {
        ArenaBossData data = getArenaBossData(arenaLevel);
        data.setForceLeaveActive(true);
        int gen = data.getForceLeaveGen() + 1;
        data.setForceLeaveGen(gen);
        data.setDirty();

        scheduleArenaMessage(arenaLevel, 10, gen, "离开倒计时 20秒");
        scheduleArenaMessage(arenaLevel, 210, gen, "离开倒计时 10秒");
        scheduleArenaMessage(arenaLevel, 310, gen, "离开倒计时 5秒");
        scheduleArenaMessage(arenaLevel, 350, gen, "离开倒计时 3秒");
        scheduleArenaMessage(arenaLevel, 400, gen, "离开倒计时 1秒");
        ServerScheduler.schedule(410, () -> {
            if (getPhase(arenaLevel) != BossFightPhase.VICTORY) {
                return;
            }
            ArenaBossData d = getArenaBossData(arenaLevel);
            if (!d.isForceLeaveActive() || d.getForceLeaveGen() != gen) {
                PDDebugLogger.mainDebug(
                        "[PDArenaBossManager] ⏱ 强制离场已取消或代际过期 gen={} current={}",
                        gen, d.getForceLeaveGen());
                return;
            }
            d.setForceLeaveActive(false);
            d.setDirty();
            // 未右键开箱：先把战利品塞进仍在场玩家背包，再 TP（原版 410t 会 discard 地面物）
            grantUnclaimedChestLoot(arenaLevel);
            teleportAllPlayersToOverworld(arenaLevel);
            cleanupArena(arenaLevel);
            PDDebugLogger.mainInfo("[PDArenaBossManager] ⏱ 胜利倒计时结束，已强制回主并清场");
        });
    }

    /**
     * 若场内仍有未开启的战利品箱，按各人 talent 将 loot 塞进背包后拆除箱
     * （防强制离场 + cleanup 吞掉未捡掉落）。
     */
    private static void grantUnclaimedChestLoot(ServerLevel arenaLevel) {
        BlockPos chestPos = ARENA_CENTER.below();
        if (!(arenaLevel.getBlockEntity(chestPos) instanceof AaroncosHandChestBlockEntity chest)) {
            return;
        }
        if (chest.isClaimed()) {
            return;
        }
        List<ServerPlayer> recipients = new ArrayList<>(arenaLevel.players());
        if (recipients.isEmpty()) {
            return;
        }
        // 先按各人成就建包再统一 mark claimed（grantUnclaimedTo 单人路径会立刻 claimed）
        for (ServerPlayer sp : recipients) {
            for (ItemStack stack : AaroncosHandChestBlockEntity.buildLootFor(sp)) {
                ItemHandlerHelper.giveItemToPlayer(sp, stack);
            }
        }
        chest.markClaimedWithoutDrop();
        if (arenaLevel.getBlockState(chestPos).is(PDBlocks.AARONCOS_HAND_CHEST.get())) {
            arenaLevel.destroyBlock(chestPos, false);
        }
        PDDebugLogger.mainInfo("[PDArenaBossManager] 📦 强制离场：未开箱战利品已分给 {} 人",
                recipients.size());
    }

    private static void scheduleArenaMessage(ServerLevel arenaLevel, int delay, int gen, String text) {
        ServerScheduler.schedule(delay, () -> {
            if (getPhase(arenaLevel) != BossFightPhase.VICTORY) {
                return;
            }
            ArenaBossData d = getArenaBossData(arenaLevel);
            if (!d.isForceLeaveActive() || d.getForceLeaveGen() != gen) {
                return;
            }
            for (Player player : new ArrayList<>(arenaLevel.players())) {
                player.displayClientMessage(Component.literal(text), true);
            }
        });
    }

    private static void grantAdvancement(ServerPlayer player, String path) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        if (holder == null) {
            PDDebugLogger.mainDebug("[PDArenaBossManager] 成就 {} 未注册，跳过授予", path);
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (!progress.isDone()) {
            for (String criteria : progress.getRemainingCriteria()) {
                player.getAdvancements().award(holder, criteria);
            }
        }
    }

    /**
     * 传送单个玩家至主世界对应传送门位置并切换为生存模式。
     * <p>
     * 当玩家在 VICTORY 阶段右键召唤方块时调用；
     * 若箱未开，先把战利品塞进该玩家背包。
     * 优先使用胜利序列记录的返回传送门位置，无记录时回退到主世界出生点。
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @param player     要传送的玩家
     */
    public static void teleportPlayersToOverworld(ServerLevel arenaLevel, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        // 点之眼提前离场：未开箱则给该玩家一份，避免空手回主
        BlockPos chestPos = ARENA_CENTER.below();
        if (arenaLevel.getBlockEntity(chestPos) instanceof AaroncosHandChestBlockEntity chest
                && !chest.isClaimed()) {
            chest.grantUnclaimedTo(serverPlayer);
            if (arenaLevel.getBlockState(chestPos).is(PDBlocks.AARONCOS_HAND_CHEST.get())) {
                arenaLevel.destroyBlock(chestPos, false);
            }
        }
        ServerLevel overworld = arenaLevel.getServer().overworld();
        ArenaBossData data = getArenaBossData(arenaLevel);
        BlockPos returnPortalPos = data.getReturnPortalPos();

        serverPlayer.setGameMode(GameType.SURVIVAL);
        if (returnPortalPos != null) {
            serverPlayer.teleportTo(overworld,
                    returnPortalPos.getX() + 0.5,
                    returnPortalPos.getY() + 1.0,
                    returnPortalPos.getZ() + 0.5,
                    serverPlayer.getYRot(), serverPlayer.getXRot());
            PDDebugLogger.mainDebug("[PDArenaBossManager] 🚪 已传送玩家 {} 至返回传送门位置 {} 并切换生存模式",
                    serverPlayer.getName().getString(), returnPortalPos);
        } else {
            BlockPos spawnPos = overworld.getSharedSpawnPos();
            serverPlayer.teleportTo(overworld,
                    spawnPos.getX() + 0.5,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5,
                    serverPlayer.getYRot(), serverPlayer.getXRot());
            PDDebugLogger.mainDebug("[PDArenaBossManager] 🚪 已传送玩家 {} 至主世界出生点并切换生存模式",
                    serverPlayer.getName().getString());
        }
    }

    /**
     * 传送竞技场内所有玩家至主世界出生点并切换为生存模式
     * <p>
     * 使用副本遍历避免传送过程中玩家列表并发修改异常。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    private static void teleportAllPlayersToOverworld(ServerLevel arenaLevel) {
        for (ServerPlayer serverPlayer : new ArrayList<>(arenaLevel.players())) {
            teleportPlayersToOverworld(arenaLevel, serverPlayer);
        }
    }

    /**
     * 清理竞技场 99 格半径内所有非玩家实体
     * <p>
     * 以竞技场中心 (0, 70, 0) 为基准，使用 AABB 范围查询，
     * 移除 BOSS 战斗残留的实体、掉落物、弹射物等。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    private static void cleanupArena(ServerLevel arenaLevel) {
        AABB cleanupArea = new AABB(ARENA_CENTER).inflate(BOSS_CHECK_RADIUS);
        List<Entity> entities = arenaLevel.getEntitiesOfClass(Entity.class,
                cleanupArea, e -> !(e instanceof Player));
        for (Entity entity : entities) {
            entity.discard();
        }
        PDDebugLogger.mainDebug("[PDArenaBossManager] 🧹 已清理竞技场内 {} 个非玩家实体", entities.size());
    }

    /**
     * 获取竞技场维度持久化数据
     * <p>
     * 使用 SavedData 机制存储 BOSS 存活状态，确保跨 tick 持久化。
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @return 竞技场 BOSS 数据存储对象
     */
    private static ArenaBossData getArenaBossData(ServerLevel arenaLevel) {
        return arenaLevel.getDataStorage().computeIfAbsent(
                ArenaBossData.FACTORY, "pasterdream_aaroncos_boss");
    }

    /**
     * 竞技场 BOSS 数据存储类 —— 维度持久化 SavedData
     * <p>
     * 存储左右手 BOSS 的存活状态和战斗阶段。
     */
    public static class ArenaBossData extends SavedData {

        /** SavedData Factory（用于 computeIfAbsent） */
        private static final SavedData.Factory<ArenaBossData> FACTORY =
                new SavedData.Factory<>(ArenaBossData::new, ArenaBossData::new, null);

        /** 左手存活状态 */
        private boolean leftHandAlive = false;

        /** 右手存活状态 */
        private boolean rightHandAlive = false;

        /** 战斗阶段 */
        private BossFightPhase phase = BossFightPhase.NOT_SUMMONED;

        /** 胜利强制离场倒计时是否仍有效（开箱后 false） */
        private boolean forceLeaveActive = false;

        /** 强制离场代际；每次 schedule / cancel / initialize 递增 */
        private int forceLeaveGen = 0;

        /** 胜利后玩家返回的传送门位置；为 null 时回退到主世界出生点 */
        private BlockPos returnPortalPos = null;

        /**
         * 无参构造器（用于新建 SavedData）
         */
        public ArenaBossData() {}

        /**
         * 带 NBT 和注册表查询参数的构造器（用于加载 SavedData）
         *
         * @param tag          NBT 数据
         * @param registryLookup 注册表查询提供者
         */
        public ArenaBossData(CompoundTag tag, HolderLookup.Provider registryLookup) {
            this.leftHandAlive = tag.getBoolean(LEFT_HAND_ALIVE_KEY);
            this.rightHandAlive = tag.getBoolean(RIGHT_HAND_ALIVE_KEY);
            this.forceLeaveActive = tag.getBoolean(FORCE_LEAVE_ACTIVE_KEY);
            this.forceLeaveGen = tag.getInt(FORCE_LEAVE_GEN_KEY);
            // 从 NBT 读取战斗阶段，默认 NOT_SUMMONED
            String phaseStr = tag.getString(BOSS_FIGHT_PHASE_KEY);
            if (!phaseStr.isEmpty()) {
                try {
                    this.phase = BossFightPhase.valueOf(phaseStr);
                } catch (IllegalArgumentException e) {
                    this.phase = BossFightPhase.NOT_SUMMONED;
                }
            }
            // 读取返回传送门位置（可选）
            if (tag.contains(RETURN_PORTAL_POS_KEY, CompoundTag.TAG_LONG)) {
                this.returnPortalPos = BlockPos.of(tag.getLong(RETURN_PORTAL_POS_KEY));
            }
        }

        /**
         * 获取左手存活状态
         *
         * @return 左手是否存活
         */
        public boolean isLeftHandAlive() {
            return leftHandAlive;
        }

        /**
         * 设置左手存活状态
         *
         * @param alive 左手是否存活
         */
        public void setLeftHandAlive(boolean alive) {
            this.leftHandAlive = alive;
        }

        /**
         * 获取右手存活状态
         *
         * @return 右手是否存活
         */
        public boolean isRightHandAlive() {
            return rightHandAlive;
        }

        /**
         * 设置右手存活状态
         *
         * @param alive 右手是否存活
         */
        public void setRightHandAlive(boolean alive) {
            this.rightHandAlive = alive;
        }

        /**
         * 获取战斗阶段
         *
         * @return 当前战斗阶段
         */
        public BossFightPhase getPhase() {
            return phase;
        }

        /**
         * 设置战斗阶段
         *
         * @param phase 新的战斗阶段
         */
        public void setPhase(BossFightPhase phase) {
            this.phase = phase;
        }

        public boolean isForceLeaveActive() {
            return forceLeaveActive;
        }

        public void setForceLeaveActive(boolean forceLeaveActive) {
            this.forceLeaveActive = forceLeaveActive;
        }

        public int getForceLeaveGen() {
            return forceLeaveGen;
        }

        public void setForceLeaveGen(int forceLeaveGen) {
            this.forceLeaveGen = forceLeaveGen;
        }

        public BlockPos getReturnPortalPos() {
            return returnPortalPos;
        }

        public void setReturnPortalPos(BlockPos returnPortalPos) {
            this.returnPortalPos = returnPortalPos;
        }

        @Override
        public CompoundTag save(CompoundTag compound, HolderLookup.Provider registryLookup) {
            compound.putBoolean(LEFT_HAND_ALIVE_KEY, this.leftHandAlive);
            compound.putBoolean(RIGHT_HAND_ALIVE_KEY, this.rightHandAlive);
            compound.putString(BOSS_FIGHT_PHASE_KEY, this.phase.name());
            compound.putBoolean(FORCE_LEAVE_ACTIVE_KEY, this.forceLeaveActive);
            compound.putInt(FORCE_LEAVE_GEN_KEY, this.forceLeaveGen);
            if (this.returnPortalPos != null) {
                compound.putLong(RETURN_PORTAL_POS_KEY, this.returnPortalPos.asLong());
            }
            return compound;
        }
    }
}