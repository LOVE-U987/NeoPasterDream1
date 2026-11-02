package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.registry.items.PDItemsDreamnotes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/**
 * 染梦世界进维事件：未完成 achievement_b_0 时发放 dreamnotes_2（寻梦者笔记）。
 * <p>
 * 移植自原版 {@code DyedreamWorldDimension#onPlayerChangedDimension} →
 * {@code DyedreamWorldPr0Procedure}：玩家进入染梦世界维度且尚未完成
 * {@code pasterdream:achievement_b_0} 时，自动获得 1 张 {@code dreamnotes_2}，
 * 并伴随提示消息与延迟 120 tick 播放的音乐。
 * <p>
 * 该链路是主线关键：{@code dreamnotes_2} 使用后授予 {@code achievement_b_0}，
 * 而 {@code achievement_hide_16}（进风之旅途的另一前提）与 {@code achievement_c_0}~{@code c_4}
 * 均以 {@code b_0} 为父节点，缺失将级联锁死整条风之旅途主线。
 * <p>
 * 由 {@code PasterDreamMod} 构造器通过 {@code NeoForge.EVENT_BUS.addListener} 显式接线。
 * <p>
 * 与原版的差异见计划 {@code .opencode/plans/2026-11-02-dyedream-note-grant-fix.md} §5：
 * 提示消息改为可翻译语言键、音乐播放位置改为玩家当前位置、延迟回调增加防御性守卫。
 */
public final class DyedreamWorldEvents {

    /** 原版发放延迟（tick）：queueServerWork(120, ...) */
    private static final int MUSIC_DELAY_TICKS = 120;

    private DyedreamWorldEvents() {
    }

    /**
     * 跨维度：进入染梦世界且未完成 b_0 时发放 dreamnotes_2（原版 DyedreamWorldPr0Procedure）。
     * <p>
     * 原版即为「每次进维且未完成 b_0 都发放」，不加去重标记。
     *
     * @param event 跨维度事件
     */
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!event.getTo().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY)) {
            return;
        }
        if (hasRawB0(player)) {
            return;
        }
        // 1. 发放 dreamnotes_2 x1
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(PDItemsDreamnotes.DREAMNOTES_2.get()));
        // 2. 提示消息（原版为硬编码中文「你发现了一张寻梦者笔记，并收进了你的背包」）
        player.displayClientMessage(
                Component.translatable("message.pasterdream.dyedream.note_found"), false);
        // 3. 延迟 120 tick 播放 sweetdream 音乐（有意差异：原版在固定坐标 (0,120,0) 播放，
        //    玩家通常不可闻，改为玩家当前位置）
        ServerScheduler.schedule(MUSIC_DELAY_TICKS, () -> {
            if (player.getServer() == null || !player.getServer().isRunning() || !player.isAlive()) {
                return;
            }
            player.serverLevel().playSound(null, player.blockPosition(),
                    PDSounds.SWEETDREAM_MUSIC.get(), SoundSource.MUSIC, 0.5F, 1.0F);
        });
    }

    /**
     * 裸 isDone 判定：b_0 holder 存在且进度完成（原版对齐）。
     * <p>
     * 刻意不用锁感知的 {@link PDAdvancements#has}：成就锁配置关闭时 {@code has} 恒为 true
     * 会跳过发放，而进风维判定（{@code PDEffects#fondillusionTick}）为裸 isDone，
     * 两个口径不一致会使主线在「锁关闭 + 未完成 b_0」组合下重新断裂。
     *
     * @param player 服务端玩家
     * @return 已完成 achievement_b_0 时 true；holder 缺失或未完成时 false
     */
    private static boolean hasRawB0(ServerPlayer player) {
        AdvancementHolder holder = player.server.getAdvancements().get(PDAdvancements.B_0);
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }
}
