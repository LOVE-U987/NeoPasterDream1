package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosLefthand0Entity;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosRighthand0Entity;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * 亚伦柯斯竞技场 BOSS 战斗管理器 —— 追踪左右手 BOSS 存活状态并生成战利品箱
 * <p>
 * 功能：
 * <ul>
 *   <li>使用维度持久化数据存储 BOSS 存活状态和战斗阶段</li>
 *   <li>检测两只手都死亡后生成战利品箱</li>
 *   <li>触发成就、移除效果、传送玩家回主世界</li>
 *   <li>管理战斗阶段：未召唤 → 召唤中 → 战斗中 → 已胜利</li>
 * </ul>
 * <p>
 * 工作流程：
 * <ol>
 *   <li>玩家进入竞技场时初始化为未召唤状态</li>
 *   <li>玩家右键召唤方块后进入召唤中状态，播放 spawn 动画</li>
 *   <li>召唤动画结束后进入战斗中状态，BOSS AI 激活</li>
 *   <li>两只手都死亡后触发胜利序列</li>
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
        PasterDreamMod.LOGGER.debug("[PDArenaBossManager] ⚔️ 已初始化 BOSS 战斗状态（未召唤）");
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
        PasterDreamMod.LOGGER.debug("[PDArenaBossManager] 🔄 战斗阶段切换为: {}", phase);
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
            PasterDreamMod.LOGGER.info("[PDArenaBossManager] ⚔️ BOSS 召唤完成，进入战斗阶段！");
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

        PasterDreamMod.LOGGER.debug("[PDArenaBossManager] 💀 左手 BOSS 已死亡");

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

        PasterDreamMod.LOGGER.debug("[PDArenaBossManager] 💀 右手 BOSS 已死亡");

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
        PasterDreamMod.LOGGER.info("[PDArenaBossManager] 🎉 两只手都已死亡，触发胜利序列！");

        // 🏆 切换到 VICTORY 阶段（玩家右键召唤方块离开）
        setPhase(arenaLevel, BossFightPhase.VICTORY);

        // 🎁 生成战利品箱（竞技场中心下方一格）
        BlockPos chestPos = ARENA_CENTER.below();
        arenaLevel.setBlockAndUpdate(chestPos, PDBlocks.AARONCOS_HAND_CHEST.get().defaultBlockState());

        // 💫 战利品箱生成粒子效果
        arenaLevel.sendParticles(ParticleTypes.END_ROD,
                chestPos.getX() + 0.5, chestPos.getY() + 0.5, chestPos.getZ() + 0.5,
                16, 1, 1, 1, 0.2);
        arenaLevel.sendParticles(ParticleTypes.SMOKE,
                chestPos.getX() + 0.5, chestPos.getY() + 0.5, chestPos.getZ() + 0.5,
                24, 1, 1, 1, 0.2);

        // 🔊 播放战利品箱音效（使用 shadow0 音效）
        arenaLevel.playSound(null, chestPos,
                PDSounds.SHADOW_0.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);

        // 🎁 调度 40 tick 后掉落成就分支战利品（参考原模组 AaroncosHandChestPr0Procedure）
        spawnLootDrops(arenaLevel, chestPos);

        // 🎯 向所有玩家发送胜利提示
        for (Player player : arenaLevel.players()) {
            player.displayClientMessage(Component.translatable("arena.pasterdream.summon_victory"), true);
        }
    }

    /**
     * 调度战利品掉落任务（成就分支）
     * <p>
     * 在战利品箱生成 40 tick 后掉落物品，逻辑参考原模组
     * {@code AaroncosHandChestPr0Procedure}：
     * <ul>
     *   <li>必定掉落 {@code PURE_HORROR} ×1</li>
     *   <li>竞技场内任意玩家完成 {@code achievement_talent_light} 成就 → 额外掉落
     *       {@code WHITE_FLOWER_BODY} ×1 和 {@code WHITE_CRYSTAL} ×1</li>
     *   <li>竞技场内任意玩家完成 {@code achievement_talent_shadow} 成就 → 额外掉落
     *       {@code DEGENERATE_BODYS} ×1 和 {@code SHADOW_HILT} ×1</li>
     * </ul>
     * <p>
     * 若成就未注册（{@code Advancement} 为 null），安全跳过对应分支不掉落。
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @param chestPos   战利品箱位置（用于掉落坐标）
     */
    private static void spawnLootDrops(ServerLevel arenaLevel, BlockPos chestPos) {
        arenaLevel.getServer().tell(new TickTask(
                arenaLevel.getServer().getTickCount() + 40, () -> {
                    double dropX = chestPos.getX() + 0.5;
                    double dropY = chestPos.getY() + 0.5;
                    double dropZ = chestPos.getZ() + 0.5;

                    // 必定掉落 PURE_HORROR ×1
                    spawnItemAt(arenaLevel, new ItemStack(PDItems.PURE_HORROR.get()), dropX, dropY, dropZ);

                    // 检查竞技场内任意玩家是否完成 achievement_talent_light
                    boolean lightDone = hasAnyPlayerCompletedAdvancement(arenaLevel,
                            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "achievement_talent_light"));
                    if (lightDone) {
                        spawnItemAt(arenaLevel, new ItemStack(PDItems.WHITE_FLOWER_BODY.get()), dropX, dropY, dropZ);
                        spawnItemAt(arenaLevel, new ItemStack(PDItems.WHITE_CRYSTAL.get()), dropX, dropY, dropZ);
                        PasterDreamMod.LOGGER.info("[PDArenaBossManager] ✨ 已掉落光明天赋分支战利品");
                    }

                    // 检查竞技场内任意玩家是否完成 achievement_talent_shadow
                    boolean shadowDone = hasAnyPlayerCompletedAdvancement(arenaLevel,
                            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "achievement_talent_shadow"));
                    if (shadowDone) {
                        spawnItemAt(arenaLevel, new ItemStack(PDItems.DEGENERATE_BODYS.get()), dropX, dropY, dropZ);
                        spawnItemAt(arenaLevel, new ItemStack(PDItems.SHADOW_HILT.get()), dropX, dropY, dropZ);
                        PasterDreamMod.LOGGER.info("[PDArenaBossManager] 🌑 已掉落暗影天赋分支战利品");
                    }

                    PasterDreamMod.LOGGER.debug("[PDArenaBossManager] 🎁 战利品已掉落于 ({}, {}, {})", dropX, dropY, dropZ);
                }));
    }

    /**
     * 检查竞技场内是否有任意玩家完成指定成就
     * <p>
     * 若成就未注册（返回 null），返回 false 跳过该分支。
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @param advId      成就 ResourceLocation
     * @return 任意玩家完成返回 true，否则返回 false
     */
    private static boolean hasAnyPlayerCompletedAdvancement(ServerLevel arenaLevel, ResourceLocation advId) {
        AdvancementHolder advancement = arenaLevel.getServer().getAdvancements().get(advId);
        if (advancement == null) {
            PasterDreamMod.LOGGER.debug("[PDArenaBossManager] ⚠️ 成就 {} 未注册，跳过对应战利品分支", advId);
            return false;
        }
        for (ServerPlayer player : arenaLevel.players()) {
            if (player.getAdvancements().getOrStartProgress(advancement).isDone()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在指定坐标生成一个物品掉落实体
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @param stack      要掉落的物品堆栈
     * @param x          掉落坐标 X
     * @param y          掉落坐标 Y
     * @param z          掉落坐标 Z
     */
    private static void spawnItemAt(ServerLevel arenaLevel, ItemStack stack, double x, double y, double z) {
        ItemEntity itemEntity = new ItemEntity(arenaLevel, x, y, z, stack);
        itemEntity.setDefaultPickUpDelay();
        arenaLevel.addFreshEntity(itemEntity);
    }

    /**
     * 传送单个玩家至主世界出生点并切换为生存模式
     * <p>
     * 当玩家在 VICTORY 阶段右键召唤方块时调用。
     *
     * @param arenaLevel 竞技场维度服务端世界
     * @param player     要传送的玩家
     */
    public static void teleportPlayersToOverworld(ServerLevel arenaLevel, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ServerLevel overworld = arenaLevel.getServer().overworld();
        BlockPos spawnPos = overworld.getSharedSpawnPos();
        // 切换游戏模式为生存
        serverPlayer.setGameMode(GameType.SURVIVAL);
        // 传送到主世界出生点
        serverPlayer.teleportTo(overworld,
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                serverPlayer.getYRot(), serverPlayer.getXRot());
        PasterDreamMod.LOGGER.debug("[PDArenaBossManager] 🚪 已传送玩家 {} 至主世界出生点并切换生存模式",
                serverPlayer.getName().getString());
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
        PasterDreamMod.LOGGER.debug("[PDArenaBossManager] 🧹 已清理竞技场内 {} 个非玩家实体", entities.size());
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

        @Override
        public CompoundTag save(CompoundTag compound, HolderLookup.Provider registryLookup) {
            compound.putBoolean(LEFT_HAND_ALIVE_KEY, this.leftHandAlive);
            compound.putBoolean(RIGHT_HAND_ALIVE_KEY, this.rightHandAlive);
            compound.putString(BOSS_FIGHT_PHASE_KEY, this.phase.name());
            return compound;
        }
    }
}