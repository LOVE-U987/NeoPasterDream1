package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CameraPos;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CurveType;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneAPI;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneData;
import com.pasterdream.pasterdreammod.api.effect.cutscene.EasingType;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandChestBlockEntity;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 亚伦柯斯竞技场 BOSS 战斗管理器 —— 追踪左右手 BOSS 存活状态并生成战利品箱
 * <p>
 * 功能：
 * <ul>
 *   <li>使用维度持久化数据存储 BOSS 存活状态和战斗阶段</li>
 *   <li>两只手都死亡后进入 VICTORY 阶段并生成战利品箱</li>
 *   <li>触发成就、移除效果；玩家手动右键召唤方块返回主世界</li>
 *   <li>管理战斗阶段：未召唤 → 召唤中 → 战斗中 → 已胜利</li>
 *   <li>胜利后不自动传送玩家；唯一离场途径为 VICTORY 阶段右键竞技场中心召唤方块</li>
 * </ul>
 * <p>
 * 工作流程：
 * <ol>
 *   <li>玩家进入竞技场时初始化为未召唤状态</li>
 *   <li>玩家右键召唤方块后进入召唤中状态，播放 spawn 动画</li>
 *   <li>召唤动画结束后进入战斗中状态，BOSS AI 激活</li>
 *   <li>两只手都死亡后进入 VICTORY：生成战利品箱，玩家留在竞技场</li>
 *   <li>玩家右键开箱捡物，再手动右键召唤方块返回主世界（未开箱则离场时补发背包）</li>
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

    /**
     * 胜利战利品箱生成位置 —— 竞技场中心地面上方（世界 (0, 42, 0)）。
     * <p>
     * 结构 aaroncos_arena.nbt 从 (-35, 0, -35) 放置，中心地面为：
     * <ul>
     *   <li>y=42：边缘实心（BOSS 战斗区地面），中心凹陷为空气</li>
     *   <li>y=41：中心 {@code shadow_fissure_5}（完整方块，可站立的地面）</li>
     *   <li>y=39：全层实心 {@code shadow_arena_block_0} 地基</li>
     * </ul>
     * 箱子放在 y=41 地面之上（y=42 格），底部贴合地面、完整露出，玩家从战斗层走一步即达。
     * 切勿直接放 y=41：那会替换 {@code shadow_fissure_5}，箱子嵌进地面装饰层（卡地里）。
     * 旧实现用 {@code ARENA_CENTER.below()}（(0,69,0)）在结构内部空中生成，箱子悬浮。
     */
    public static final BlockPos VICTORY_CHEST_POS = new BlockPos(0, 42, 0);

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
     * 维度持久化数据键名 —— 胜利后玩家返回的传送门位置
     */
    private static final String RETURN_PORTAL_POS_KEY = "ReturnPortalPos";

    /**
     * 玩家持久化数据键名 —— 离场冷却截止 gameTime。
     * <p>
     * 胜利/离场传送会把玩家送到主世界返回传送门正上方，传送门为无碰撞半砖，
     * 玩家下落穿过时 {@code entityInside} 会再次传送进竞技场并重新初始化
     * BOSS 遗迹（胜利→立即重进循环 bug）。冷却期内传送门对离场玩家不响应。
     */
    public static final String ARENA_EXIT_COOLDOWN_KEY = "pd_arena_exit_cooldown";

    /** 离场冷却时长（tick）：100 tick = 5 秒，足够玩家离开返回传送门 */
    private static final long ARENA_EXIT_COOLDOWN_TICKS = 100;

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
        data.setDirty();
        PDDebugLogger.mainDebug("[PDArenaBossManager] ⚔️ 已初始化 BOSS 战斗状态（未召唤）");
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

        // 召唤瞬间向竞技场内玩家播放过场动画（相机环绕竞技场中心 + 渐暗）
        Vec3 center = new Vec3(ARENA_CENTER.getX() + 0.5, ARENA_CENTER.getY(), ARENA_CENTER.getZ() + 0.5);
        CutsceneAPI.startCutsceneForPlayers(arenaLevel, center, 99.0,
                CutsceneData.create()
                        .time(80)
                        .moveCurveType(CurveType.CATMULLROM)
                        .timeEasing(EasingType.SMOOTHSTEP)
                        .addCameraPos(CameraPos.of(center.add(0, 10, 22), center))
                        .addCameraPos(CameraPos.of(center.add(0, 16, 0), center))
                        .addCameraPos(CameraPos.of(center.add(0, 10, -22), center)));

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
     * 仅在 {@code FIGHTING} 战斗阶段接受死亡判定：未召唤 / 召唤中 / 已胜利阶段
     * 收到死亡回调一律忽略（残留 BOSS 或非本场战斗实体的死亡不得误判胜利）。
     * 战斗阶段内更新维度数据并检测是否两只手都已死亡。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    public static void onLeftHandDeath(ServerLevel arenaLevel) {
        if (getPhase(arenaLevel) != BossFightPhase.FIGHTING) {
            PDDebugLogger.mainDebug("[PDArenaBossManager] ⛔ 非战斗阶段收到左手死亡回调，忽略（phase={}）",
                    getPhase(arenaLevel));
            return;
        }
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
     * 仅在 {@code FIGHTING} 战斗阶段接受死亡判定：未召唤 / 召唤中 / 已胜利阶段
     * 收到死亡回调一律忽略（残留 BOSS 或非本场战斗实体的死亡不得误判胜利）。
     * 战斗阶段内更新维度数据并检测是否两只手都已死亡。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    public static void onRightHandDeath(ServerLevel arenaLevel) {
        if (getPhase(arenaLevel) != BossFightPhase.FIGHTING) {
            PDDebugLogger.mainDebug("[PDArenaBossManager] ⛔ 非战斗阶段收到右手死亡回调，忽略（phase={}）",
                    getPhase(arenaLevel));
            return;
        }
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
     * 触发胜利序列 —— 生成战利品箱，玩家留在竞技场自行离开
     * <p>
     * 根据原模组逻辑（AaroncoshandspawnblockPr1Procedure），执行：
     * <ol>
     *   <li>在竞技场中心地面（{@link #VICTORY_CHEST_POS}）生成战利品箱方块</li>
     *   <li>播放音效和粒子效果</li>
     *   <li>触发成就（achievement_shadow_e_0）</li>
     *   <li>移除暗影窥视效果</li>
     *   <li>显示胜利提示，玩家手动右键召唤方块离开</li>
     * </ol>
     * <p>
     * 胜利后<b>不自动传送</b>、<b>不启动强制离场倒计时</b>：
     * 玩家留在竞技场捡取战利品，再手动右键中心召唤方块返回主世界。
     * 已处于 VICTORY 阶段时直接返回（双 BOSS 同 tick 死亡时防重复触发）。
     *
     * @param arenaLevel 竞技场维度服务端世界
     */
    private static void triggerVictorySequence(ServerLevel arenaLevel) {
        // 🛡 幂等保护：双 BOSS 同 tick 死亡时左右手回调先后触发，第二次直接忽略
        if (getPhase(arenaLevel) == BossFightPhase.VICTORY) {
            PDDebugLogger.mainDebug("[PDArenaBossManager] 🛡 已在 VICTORY 阶段，忽略重复的胜利序列触发");
            return;
        }

        PDDebugLogger.mainInfo("[PDArenaBossManager] 🎉 两只手都已死亡，触发胜利序列！");

        // 🏆 切换到 VICTORY 阶段（玩家右键召唤方块离开）
        setPhase(arenaLevel, BossFightPhase.VICTORY);

        // 🎁 生成战利品箱（竞技场中心地面）
        BlockPos chestPos = VICTORY_CHEST_POS;
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

        // 不自动传送、不启动强制倒计时：玩家留在竞技场开箱捡物，
        // 再手动右键中心召唤方块（AaroncosHandSpawnBlock VICTORY 分支）返回主世界。
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
     * 传送单个玩家至主世界对应传送门位置并切换为生存模式 —— 竞技场唯一的离场途径。
     * <p>
     * 当玩家在 VICTORY 阶段右键中心召唤方块时调用；
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
        // 未开箱则给该玩家一份，避免空手回主
        BlockPos chestPos = VICTORY_CHEST_POS;
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

        // 离场冷却：返回传送门就在主世界脚下，玩家下落穿过时会再次触发 entityInside 进竞技场，
        // 冷却期内传送门不响应，避免「胜利→传送回→立即重进→竞技场重新初始化」循环
        serverPlayer.getPersistentData().putLong(ARENA_EXIT_COOLDOWN_KEY,
                arenaLevel.getGameTime() + ARENA_EXIT_COOLDOWN_TICKS);

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
            if (this.returnPortalPos != null) {
                compound.putLong(RETURN_PORTAL_POS_KEY, this.returnPortalPos.asLong());
            }
            return compound;
        }
    }
}