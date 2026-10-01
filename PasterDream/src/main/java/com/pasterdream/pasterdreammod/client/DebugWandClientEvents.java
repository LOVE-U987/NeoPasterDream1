package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.DebugVariantWandItem;
import com.pasterdream.pasterdreammod.network.DebugWandVariantPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 调试变体水晶的客户端交互事件。
 * <p>
 * 处理「潜行 + 滚轮」切换变体：命中时本地先改物品组件让 tooltip 立即刷新，
 * 再发 C2S 包把同一个变体序号同步给服务端（放置判定在服务端，必须两端一致）。
 * 仅在客户端加载，专用服务端安全。
 *
 * @author PasterDream Team
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class DebugWandClientEvents {

    /**
     * 滚轮事件 —— 潜行手持变体水晶时切换变体并取消原版物品栏切换
     *
     * @param event 鼠标滚轮事件
     */
    @SubscribeEvent
    public static void onMouseScrolling(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        // 仅在游戏内、无界面打开、玩家潜行时生效
        if (player == null || minecraft.screen != null || !player.isShiftKeyDown()) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof DebugVariantWandItem wand)) {
            return;
        }

        int count = wand.targetCount();
        if (count <= 1) {
            return;
        }

        double delta = event.getScrollDeltaY();
        if (delta == 0) {
            return;
        }

        int next = Math.floorMod(DebugVariantWandItem.getVariant(stack) + (delta > 0 ? 1 : -1), count);
        DebugVariantWandItem.setVariant(stack, next);
        PacketDistributor.sendToServer(new DebugWandVariantPayload(player.getInventory().selected, next));

        // 取消事件，避免同时切换快捷栏选中槽
        event.setCanceled(true);
    }
}
