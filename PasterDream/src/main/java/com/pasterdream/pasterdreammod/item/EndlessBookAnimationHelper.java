package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.api.util.BookLocalization;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 寻梦者的永恒书卷 — 非创造模式动画时序辅助类
 * <p>
 * 还原原版 Pr1Procedure 的 40-tick 粒子+音效时序（以方块位置为中心），
 * 并控制书籍给予逻辑（玩家 NBT → 战利品表）。
 */
public class EndlessBookAnimationHelper {

    /** 模组战利品表来源 */
    private static final List<ResourceKey<LootTable>> BOOK_LOOT_TABLES = List.of(
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "gameplay/achievement_hide_1")),
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "gameplay/achievement_hide_2")),
            ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "gameplay/achievement_hide_4"))
    );

    /** 1 tick = 50ms */
    private static final long TICK_MS = 50L;

    /**
     * 以方块位置为中心播放寻梦者书卷动画并给予书籍。
     * <p>
     * 时序还原原版 {@code Pr1Procedure}：每 10 tick 一轮粒子+音效。
     *
     * @param serverLevel   服务端世界
     * @param pos           方块位置
     * @param player        交互玩家
     * @param overrideBook  优先书籍（方块实体展示槽副本），非空则优先给予
     */
    public static void playAnimationAndGiveBook(ServerLevel serverLevel, BlockPos pos,
                                                ServerPlayer player,
                                                @Nullable ItemStack overrideBook) {
        final double x = pos.getX() + 0.5;
        final double y = pos.getY() + 0.4;
        final double z = pos.getZ() + 0.5;

        // —— 阶段 0（t=0）：翻页音效 + 粒子 ——
        serverLevel.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN,
                SoundSource.PLAYERS, 1.0f, 1.0f);
        spawnParticleBurst(serverLevel, x, y, z);

        // —— 剩余阶段后台线程调度 ——
        final ServerLevel sl = serverLevel;
        final ServerPlayer sp = player;
        final ItemStack finalOverride = overrideBook != null ? overrideBook.copy() : null;
        Thread scheduler = new Thread(() -> {
            try {
                // t=10：附魔台音效
                Thread.sleep(10 * TICK_MS);
                schedule(sl, sp, () -> {
                    sl.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE,
                            SoundSource.PLAYERS, 0.8f, 1.0f);
                    spawnParticleBurst(sl, x, y, z);
                });

                // t=20：粒子
                Thread.sleep(10 * TICK_MS);
                schedule(sl, sp, () -> spawnParticleBurst(sl, x, y, z));

                // t=30：放置音效 + 粒子 + 给予书籍
                Thread.sleep(10 * TICK_MS);
                schedule(sl, sp, () -> {
                    sl.playSound(null, pos, SoundEvents.BOOK_PUT,
                            SoundSource.PLAYERS, 1.0f, 1.0f);
                    spawnParticleBurst(sl, x, y, z);
                    giveBookToPlayer(sl, sp, finalOverride);
                });

                // t=40：收尾粒子
                Thread.sleep(10 * TICK_MS);
                schedule(sl, sp, () -> spawnParticleBurst(sl, x, y, z));

            } catch (InterruptedException ignored) {}
        }, "EndlessBookAnimScheduler");
        scheduler.setDaemon(true);
        scheduler.start();
    }

    /**
     * 将任务调度到服务端主线程，忽略离线玩家。
     */
    private static void schedule(ServerLevel level, ServerPlayer player, Runnable task) {
        level.getServer().execute(() -> {
            if (player.isAlive() && player.level() == level) {
                task.run();
            }
        });
    }

    /**
     * 给予玩家一本书：优先 overrideBook，无则战利品表随机。
     *
     * @param overrideBook 方块实体展示槽书籍副本（不清除源槽），可为空
     */
    private static void giveBookToPlayer(ServerLevel level, ServerPlayer player,
                                          @Nullable ItemStack overrideBook) {
        ItemStack book;

        // 第一优先级：方块实体展示槽书籍
        if (overrideBook != null && !overrideBook.isEmpty()) {
            book = overrideBook.copy();
        } else {
            // 第二优先级：战利品表
            book = rollBookFromLoot(level, player);
        }

        if (!book.isEmpty()) {
            // 结构成书本地化保险：无论书来自展示槽还是战利品表，都确保书页随语言切换
            BookLocalization.localize(book);
            if (!player.getInventory().add(book)) {
                ItemEntity drop = new ItemEntity(level,
                        player.getX(), player.getY(), player.getZ(), book);
                drop.setPickUpDelay(5);
                level.addFreshEntity(drop);
            }
        }
    }

    /**
     * 从战利品表中随机获取一本书。
     */
    private static ItemStack rollBookFromLoot(ServerLevel level, ServerPlayer player) {
        if (BOOK_LOOT_TABLES.isEmpty()) return ItemStack.EMPTY;
        int idx = level.getRandom().nextInt(BOOK_LOOT_TABLES.size());
        LootTable table = level.getServer().reloadableRegistries()
                .getLootTable(BOOK_LOOT_TABLES.get(idx));
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .create(LootContextParamSets.CHEST);
        List<ItemStack> items = table.getRandomItems(params);
        return items.isEmpty() ? ItemStack.EMPTY : items.getFirst().copy();
    }

    /**
     * 在方块中心生成一轮 ENCHANT + 尘埃粒子（各 16 个）。
     */
    private static void spawnParticleBurst(ServerLevel level, double x, double y, double z) {
        level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 16, 1, 1, 1, 0.5);
        level.sendParticles(
                (SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                x, y, z, 16, 1, 1, 1, 0.1);
    }
}
