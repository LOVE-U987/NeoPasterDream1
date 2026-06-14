package com.pasterdream.pasterdreammod.client.audio;

import net.minecraft.resources.ResourceLocation;

/**
 * 切换冷却管理器 —— 防止群系边界反复横跳导致的 BGM 错乱
 * <p>
 * 职责：
 * <ul>
 *   <li>管理群系切换冷却期</li>
 *   <li>检测冷却是否结束</li>
 *   <li>支持冷却重置（进入另一个新群系时）</li>
 *   <li>支持冷却取消（回到原群系时）</li>
 * </ul>
 * <p>
 * 玩家进入新群系后，需等待冷却结束后才开始交叉淡化。
 * 冷却期间原 BGM 持续播放，有效防止群系边界反复横跳导致的 BGM 错乱。
 */
public class CooldownManager {

    private final int switchCooldownTicks;

    /** 是否处于切换冷却期 */
    private boolean isInCooldown = false;

    /** 冷却期记录的目标群系 ID（冷却结束后要切换到该群系） */
    private ResourceLocation pendingBiomeId;

    /** 冷却期记录的目标音乐名称 */
    private String pendingMusicName;

    /** 冷却期开始的游戏 tick 数 */
    private long cooldownStartTick;

    /**
     * @param switchCooldownTicks 冷却 tick 数（20 tick ≈ 1 秒），至少 1 tick
     */
    public CooldownManager(int switchCooldownTicks) {
        this.switchCooldownTicks = Math.max(1, switchCooldownTicks);
    }

    /**
     * 进入冷却期
     *
     * @param biomeId   目标群系 ID
     * @param musicName 目标音乐名称
     * @param currentTick 当前游戏 tick
     */
    public void enterCooldown(ResourceLocation biomeId, String musicName, long currentTick) {
        this.isInCooldown = true;
        this.pendingBiomeId = biomeId;
        this.pendingMusicName = musicName;
        this.cooldownStartTick = currentTick;
    }

    /**
     * 在冷却期间更新状态
     * <p>
     * 三种场景：
     * <ul>
     *   <li>仍在目标群系中 → 检查冷却是否结束</li>
     *   <li>回到了原群系 → 取消冷却</li>
     *   <li>又进入了另一个新群系 → 重置冷却</li>
     * </ul>
     *
     * @param currentBiomeId  当前群系 ID
     * @param previousBiomeId 上一个 tick 的群系 ID
     * @param currentTick     当前游戏 tick
     * @return true 表示冷却结束，应触发交叉淡化
     */
    public boolean updateCooldown(ResourceLocation currentBiomeId, ResourceLocation previousBiomeId, long currentTick) {
        if (!isInCooldown) return false;

        if (currentBiomeId.equals(pendingBiomeId)) {
            // 仍在目标群系中 → 检查冷却是否结束
            if (currentTick - cooldownStartTick >= switchCooldownTicks) {
                isInCooldown = false;
                return true; // 冷却结束
            }
        } else if (previousBiomeId != null && currentBiomeId.equals(previousBiomeId)) {
            // 回到了原群系 → 取消冷却
            cancelCooldown();
        } else {
            // 又进入了另一个新群系 → 重置冷却
            pendingBiomeId = currentBiomeId;
            // pendingMusicName 需要由外部更新
            cooldownStartTick = currentTick;
        }
        return false;
    }

    /**
     * 重置冷却的待处理音乐名称（当冷却期间进入新群系时由外部更新）
     */
    public void setPendingMusicName(String musicName) {
        this.pendingMusicName = musicName;
    }

    /**
     * 取消冷却期
     */
    public void cancelCooldown() {
        isInCooldown = false;
        pendingBiomeId = null;
        pendingMusicName = null;
    }

    public boolean isInCooldown() {
        return isInCooldown;
    }

    public ResourceLocation getPendingBiomeId() {
        return pendingBiomeId;
    }

    public String getPendingMusicName() {
        return pendingMusicName;
    }
}