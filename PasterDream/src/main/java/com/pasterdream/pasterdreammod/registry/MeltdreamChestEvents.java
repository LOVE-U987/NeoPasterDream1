package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.block.MeltdreamChestBlock;
import com.pasterdream.pasterdreammod.block.MeltdreamChestOpenBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * 融梦水晶箱破坏保护事件
 * <p>
 * 对 {@code meltdream_chest} / {@code meltdream_chest_open} 施加破坏保护：
 * <ol>
 *   <li><b>开启动画中禁止破坏</b>：动画播放中（ANIMATION != 0）强制取消破坏，
 *       防止玩家在开箱途中挖掘导致状态机/物品弹出逻辑丢失；</li>
 *   <li><b>非潜行禁止破坏</b>：玩家必须潜行（Sneak）才能带走融梦水晶箱，
 *       非潜行时取消破坏并提示「每位玩家独立计时」机制说明。</li>
 * </ol>
 * 冷却时间在开箱瞬间写入 {@code MeltdreamChestBlockEntity}（NBT 持久化，从始至终同一个方块），
 * 服务器重启 / 区块重载不丢失；箱子被破坏（含 TNT）时冷却数据随掉落物携带，
 * 重放后冷却依然生效，无法通过"挖掘/TNT → 重放"绕过冷却。
 */
@EventBusSubscriber(modid = "pasterdream")
public final class MeltdreamChestEvents {

    private MeltdreamChestEvents() {
    }

    /**
     * 方块破坏事件拦截 —— 融梦水晶箱破坏保护
     *
     * @param event 方块破坏事件
     */
    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        boolean isChest = state.getBlock() instanceof MeltdreamChestBlock;
        boolean isOpenChest = state.getBlock() instanceof MeltdreamChestOpenBlock;
        // 仅拦截融梦水晶箱两个状态
        if (!isChest && !isOpenChest) {
            return;
        }

        Player player = event.getPlayer();

        // 1. 开启动画进行中禁止破坏（防止挖掘丢失状态机/物品弹出逻辑）
        if (isChest && state.getValue(MeltdreamChestBlock.ANIMATION) != 0) {
            event.setCanceled(true);
            player.displayClientMessage(
                    Component.translatable("message.pasterdream.meltdream_chest.break_animating"),
                    true);
            return;
        }

        // 2. 非潜行状态禁止破坏（每位玩家独立计时，潜行才能带走箱子）
        if (!player.isShiftKeyDown()) {
            event.setCanceled(true);
            player.displayClientMessage(
                    Component.translatable("message.pasterdream.meltdream_chest.break_require_sneak"),
                    true);
        }
    }
}
