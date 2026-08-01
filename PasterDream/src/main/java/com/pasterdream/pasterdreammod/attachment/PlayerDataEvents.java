package com.pasterdream.pasterdreammod.attachment;

import com.pasterdream.pasterdreammod.api.san.SanData;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.registry.PDGameRules;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.items.PDItemsDreamnotes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 玩家数据生命周期事件处理器（游戏总线）
 * <p>
 * 移植自原版 {@code SanCapability#init} / {@code MeltDreamEnergyCapability#init}
 * 注册的四组事件监听：登录、重生、跨维度、克隆。均由主类构造器通过
 * {@code NeoForge.EVENT_BUS.addListener} 显式接线。
 * <p>
 * 同步时机与原版一致：四个事件均对两类数据做<b>全量</b> S2C 同步。
 * <p>
 * 另含原版登录侧逻辑：
 * <ul>
 *   <li>{@code AnnouncementProcedure} — 可选聊天公告 + 首次登录赠 {@code dreamnotes_0} / {@code achievement_hide_6}</li>
 *   <li>{@code MementoPlayerSpawnPr0} — 未完成 hide_6 时按玩家名掉落纪念物</li>
 * </ul>
 */
public class PlayerDataEvents {

    /**
     * 特定玩家名 → 首次登录纪念物（原版 MementoPlayerPr0/Pr1）。
     * 使用显示名字符串精确匹配，与原版一致。
     */
    private static final Map<String, Supplier<? extends Item>> MEMENTO_BY_NAME = Map.ofEntries(
            Map.entry("Aerolite_Dust", PDItems.MEMENTO_ITEM_01),
            Map.entry("SnowS_Slow", PDItems.MEMENTO_ITEM_02),
            Map.entry("yan_meng211", PDItems.MEMENTO_ITEM_03),
            Map.entry("Ink_Sky_", PDItems.MEMENTO_ITEM_04),
            Map.entry("ym574833017", PDItems.MEMENTO_ITEM_05),
            Map.entry("someoneice", PDItems.MEMENTO_ITEM_06),
            Map.entry("GQ2529", PDItems.MEMENTO_ITEM_07),
            Map.entry("Fallen_sky2", PDItems.MEMENTO_ITEM_08),
            Map.entry("Vittorio_Veneto", PDItems.MEMENTO_ITEM_10),
            Map.entry("AlireaRe", PDItems.MEMENTO_ITEM_11),
            Map.entry("housefish5312", PDItems.FORSAKENS_WING),
            Map.entry("BOTKanadeR", PDItems.ANGEL_WING)
            // AlireaRe 额外 cradle：见 dropMementosForPlayer 特判
    );

    /**
     * 玩家登录：全量同步 San 与融梦能量；公告 / hide_6 笔记 / 纪念物。
     *
     * @param event 登录事件
     */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) {
            return;
        }
        PDAttachments.syncSan(sp);
        PDAttachments.syncMeltDreamEnergy(sp);
        handleLoginAnnouncementAndStarter(sp);
    }

    /**
     * 原版 AnnouncementProcedure + MementoPlayerSpawnPr0：
     * 可选公告；若尚未 hide_6 则掉落纪念物、赠笔记 0 并授予 hide_6。
     *
     * @param player 登录的服务端玩家
     */
    private static void handleLoginAnnouncementAndStarter(ServerPlayer player) {
        if (Boolean.TRUE.equals(PDCommonConfig.MOD_ACCOUOCEMENT.get())) {
            player.displayClientMessage(Component.translatable("message.pasterdream.player_data.announcement_1"), false);
            player.displayClientMessage(Component.translatable("message.pasterdream.player_data.announcement_2"), false);
            player.displayClientMessage(Component.translatable("message.pasterdream.player_data.announcement_3"), false);
            player.displayClientMessage(Component.translatable("message.pasterdream.player_data.announcement_4"), false);
        }

        if (!PDAdvancements.has(player, PDAdvancements.HIDE_6)) {
            dropMementosForPlayer(player);
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(PDItemsDreamnotes.DREAMNOTES_0.get()));
            PDAdvancements.award(player, PDAdvancements.HIDE_6);
        }

        // 1.21 客户端只同步「可见」成就：未完成的根节点不会出现 Tab。
        // 登录时确保 achievement_start 完成，模组主线 Tab 才能出现；笔记 a_0 也依赖 start。
        PDAdvancements.award(player, PDAdvancements.START);
    }

    /**
     * 按显示名掉落对应纪念物（原版 MementoPlayerPr0 + Pr1）。
     * AlireaRe 同时获得 memento_item_11 与 cradle_in_ones_arms。
     *
     * @param player 目标玩家
     */
    private static void dropMementosForPlayer(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        String name = player.getDisplayName().getString();
        Supplier<? extends Item> supplier = MEMENTO_BY_NAME.get(name);
        if (supplier != null) {
            spawnItemEntity(level, player, new ItemStack(supplier.get()));
        }
        // 原版 Pr1：AlireaRe 额外 cradle（与 Pr0 的 memento_11 叠加）
        if ("AlireaRe".equals(name)) {
            spawnItemEntity(level, player, new ItemStack(PDItems.CRADLE_IN_ONES_ARMS.get()));
        }
    }

    private static void spawnItemEntity(ServerLevel level, ServerPlayer player, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), stack);
        entity.setPickUpDelay(10);
        level.addFreshEntity(entity);
    }

    /**
     * 玩家重生：全量同步（对应原版 playerRespawn → sync）
     *
     * @param event 重生事件
     */
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            PDAttachments.syncSan(sp);
            PDAttachments.syncMeltDreamEnergy(sp);
        }
    }

    /**
     * 玩家跨维度：全量同步（对应原版 playerChangeDimension → sync）
     *
     * @param event 跨维度事件
     */
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            PDAttachments.syncSan(sp);
            PDAttachments.syncMeltDreamEnergy(sp);
        }
    }

    /**
     * 玩家克隆（死亡重生 / 末地返回等）
     * <p>
     * 对照原版 clone 逻辑：
     * <ul>
     *   <li>死亡：San 重置为游戏规则 {@code pasterdreamStartSanOnRevive}（默认 90），
     *       开关取 {@code pasterdreamSanSystem}；融梦能量由 attachment 的 copyOnDeath 自动保留</li>
     *   <li>非死亡：两类数据均由 NeoForge attachment 机制自动复制（等价原版 reviveCaps + 拷贝）</li>
     * </ul>
     * 随后全量同步（与原版 playerClone 末尾的 sync 一致）。
     *
     * @param event 克隆事件
     */
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) {
            return;
        }
        if (event.isWasDeath()) {
            GameRules rules = sp.serverLevel().getGameRules();
            sp.setData(PDAttachments.PLAYER_SAN, new SanData(
                    rules.getInt(PDGameRules.START_SAN_ON_REVIVE),
                    rules.getBoolean(PDGameRules.SAN_CHECK_SYSTEM)));
        }
        PDAttachments.syncSan(sp);
        PDAttachments.syncMeltDreamEnergy(sp);
    }
}
