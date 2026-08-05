package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.SkyLinkItem;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * 星空枕 (memento_item_08) 客户端交互事件 —— 左键移除连线星体
 * <p>
 * 右键创建星体由 {@link SkyLinkItem#use} 处理；移除改由左键触发：
 * <ul>
 *   <li>对准方块左键（{@link PlayerInteractEvent.LeftClickBlock}）：若视线方向
 *       对准了已创建的星体，则移除该星体并取消事件（不破坏方块）</li>
 *   <li>对准空气左键（{@link PlayerInteractEvent.LeftClickEmpty}）：同样尝试移除</li>
 * </ul>
 * 仅客户端订阅（星空枕数据纯客户端），白天移除会被 {@link SkyLinkItem#tryRemoveStarAt}
 * 拦截并提示"晚上再用"。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class SkyLinkItemEvents {

    private SkyLinkItemEvents() {
    }

    /**
     * 左键点击方块时：手持星空枕且对准星体 → 移除并取消破坏
     *
     * @param event 左键方块事件
     */
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!isHoldingSkyLink(event.getEntity())) {
            return;
        }
        if (SkyLinkItem.tryRemoveStarAt()) {
            event.setCanceled(true);
        }
    }

    /**
     * 左键点击空气时：手持星空枕 → 尝试移除对准的星体
     *
     * @param event 左键空气事件
     */
    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (!isHoldingSkyLink(event.getEntity())) {
            return;
        }
        SkyLinkItem.tryRemoveStarAt();
    }

    /**
     * 判断玩家主/副手是否持有星空枕
     *
     * @param player 玩家
     * @return 是否持有
     */
    private static boolean isHoldingSkyLink(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return main.is(PDItems.MEMENTO_ITEM_08) || off.is(PDItems.MEMENTO_ITEM_08);
    }
}
