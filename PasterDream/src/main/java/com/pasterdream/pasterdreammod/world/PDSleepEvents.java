package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.block.ShadowBedBlock;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import com.pasterdream.pasterdreammod.registry.items.PDItemsDreamnotes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/**
 * 入睡侧逻辑（原版 {@code SleepPr0Procedure}）。
 * <p>
 * NeoForge 1.21 无 {@code PlayerSleepInBedEvent}，改挂 {@link CanPlayerSleepEvent}
 * （由 {@code ServerPlayer#startSleepInBed} 发出）。仅在允许入睡
 * （{@code getProblem() == null}）且夜晚时执行，语义对齐原版：
 * <ul>
 *   <li>授予休憩 buff 3 分钟</li>
 *   <li>{@code start} 且未 {@code a_0}：约 110 tick 后发放 {@code dreamnotes_1} 与梦境叙事</li>
 *   <li>持有梦愿 buff：约 60 tick 后传送至染梦世界 (0.5, 108, 0.5)</li>
 *   <li>{@code hide_7} 且未 {@code hide_8}：约 110 tick 后发放 {@code dreamnotes_8}</li>
 *   <li>床上方 2 格为激活暮影之笼且已有 {@code hide_9}：约 95 tick 后进灯影世界
 *       （普通床路径；真·影之床自身 use 已另有即时传送）</li>
 * </ul>
 */
public final class PDSleepEvents {

    private PDSleepEvents() {
    }

    /**
     * 玩家尝试入睡且原版校验通过时触发。
     *
     * @param event 入睡校验事件
     */
    public static void onCanPlayerSleep(CanPlayerSleepEvent event) {
        // 仅处理「允许入睡」；被拒绝（白天/太远等）不跑延迟任务
        if (event.getProblem() != null) {
            return;
        }
        ServerPlayer player = event.getEntity();
        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }
        // 原版：白天直接跳过全部分支
        if (level.isDay()) {
            return;
        }
        BlockPos bedPos = event.getPos();
        executeNightSleep(level, bedPos.getX(), bedPos.getY(), bedPos.getZ(), player);
    }

    /**
     * 夜晚入睡主体逻辑（原版 SleepPr0Procedure.execute）。
     *
     * @param world  当前世界
     * @param x      床 X
     * @param y      床 Y
     * @param z      床 Z
     * @param entity 入睡实体（应为玩家）
     */
    public static void executeNightSleep(Level world, double x, double y, double z, Entity entity) {
        if (entity == null || world.isClientSide()) {
            return;
        }

        if (entity instanceof ServerPlayer living) {
            living.addEffect(new MobEffectInstance(PDEffects.REST_BUFF, 3600, 0, false, false));
        }

        // 染梦裂隙笔记：start 已完成、a_0 未完成 → 梦中惊醒记录 dreamnotes_1
        if (entity instanceof ServerPlayer sp
                && PDAdvancements.has(sp, PDAdvancements.START)
                && !PDAdvancements.has(sp, PDAdvancements.A_0)) {
            ServerScheduler.schedule(110, () -> grantDreamnotes1(sp));
        }

        // 梦愿：入睡后短延迟送入染梦出生点
        if (entity instanceof ServerPlayer sp
                && sp.hasEffect(PDEffects.DREAMWISH_BUFF.holder())) {
            ServerScheduler.schedule(60, () -> teleportDreamwishToDyedream(sp));
        }

        // 灯影线笔记 8：hide_7 且未 hide_8
        if (entity instanceof ServerPlayer sp
                && PDAdvancements.has(sp, PDAdvancements.HIDE_7)
                && !PDAdvancements.has(sp, PDAdvancements.HIDE_8)) {
            ServerScheduler.schedule(110, () -> grantDreamnotes8(sp));
        }

        // 普通床 + 上方激活暮影之笼 + hide_9 → 进灯影（原版 95 tick）
        BlockPos lanternPos = BlockPos.containing(x, y + 2, z);
        if (world.getBlockState(lanternPos).is(PDBlocksFurniture.TWILIGHT_LANTERN.get())
                && W4DataBlockEntity.getBooleanAt(world, lanternPos, "key")
                && entity instanceof ServerPlayer sp
                && PDAdvancements.has(sp, PDAdvancements.HIDE_9)) {
            final BlockPos bedBlock = BlockPos.containing(x, y, z);
            ServerScheduler.schedule(95, () -> {
                if (!sp.isAlive() || sp.level().isClientSide()) {
                    return;
                }
                Level lvl = sp.level();
                // 保存原方块状态，防止传送后丢失（如阴影床等特殊床类型）
                BlockState originalState = lvl.getBlockState(bedBlock);
                lvl.setBlock(bedBlock, Blocks.BLACK_BED.defaultBlockState(), 3);
                com.pasterdream.pasterdreammod.attachment.PDAttachments.addPlayerSanWithCheck(sp, -10);
                ShadowBedBlock.teleportToLampShadowWorld(lvl, sp);
                // 传送完成后恢复原方块状态
                lvl.setBlock(bedBlock, originalState, 3);
            });
        }
    }

    /**
     * 发放 dreamnotes_1 与三段紫色梦境文案（原版 SleepPr0 110 tick 分支）。
     *
     * @param player 目标玩家
     */
    private static void grantDreamnotes1(ServerPlayer player) {
        if (!player.isAlive() || player.level().isClientSide()) {
            return;
        }
        // 延迟期间可能已读笔记解锁 a_0，或重复入睡叠加任务——仅未解锁时发放
        if (PDAdvancements.has(player, PDAdvancements.A_0)) {
            return;
        }
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(PDItemsDreamnotes.DREAMNOTES_1.get()));
        player.displayClientMessage(Component.literal(
                "§5你在睡梦中惊醒，背后冒出了些许冷汗，你回忆起自己梦见了往日探索中遇见的奇怪裂隙，在慢慢靠近并凝视着你。"), false);
        player.displayClientMessage(Component.literal(
                "§5你拿起附近的材料迅速地把这些梦境记录下来，在这之后你就失去了对这段梦境的记忆。"), false);
        player.displayClientMessage(Component.literal("§5我必须知道发生了什么..."), false);
    }

    /**
     * 发放 dreamnotes_8 与相同梦境叙事（原版 hide_7 分支）。
     *
     * @param player 目标玩家
     */
    private static void grantDreamnotes8(ServerPlayer player) {
        if (!player.isAlive() || player.level().isClientSide()) {
            return;
        }
        if (PDAdvancements.has(player, PDAdvancements.HIDE_8)) {
            return;
        }
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(PDItemsDreamnotes.DREAMNOTES_8.get()));
        player.displayClientMessage(Component.literal(
                "§5你在睡梦中惊醒，背后冒出了些许冷汗，你回忆起自己梦见了往日探索中遇见的奇怪裂隙，在慢慢靠近并凝视着你。"), false);
        player.displayClientMessage(Component.literal(
                "§5你拿起附近的材料迅速地把这些梦境记录下来，在这之后你就失去了对这段梦境的记忆。"), false);
        player.displayClientMessage(Component.literal("§5我必须知道发生了什么..."), false);
    }

    /**
     * 梦愿入睡：传送至染梦世界固定点 (0.5, 108, 0.5)。
     *
     * @param player 目标玩家
     */
    private static void teleportDreamwishToDyedream(ServerPlayer player) {
        if (!player.isAlive() || player.level().isClientSide()) {
            return;
        }
        if (player.level().dimension().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY)) {
            return;
        }
        if (!player.hasEffect(PDEffects.DREAMWISH_BUFF.holder())) {
            return;
        }
        ServerLevel target = player.server.getLevel(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY);
        if (target == null) {
            return;
        }
        DimensionTransition transition = new DimensionTransition(
                target,
                new Vec3(0.5, 108.0, 0.5),
                Vec3.ZERO,
                player.getYRot(),
                player.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND);
        player.changeDimension(transition);
        player.fallDistance = 0.0F;
    }
}
