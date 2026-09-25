package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.screen.SaveUpgradePromptScreen;
import com.pasterdream.pasterdreammod.network.SaveUpgradePromptPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * 旧存档兼容客户端落地（仅 CLIENT 发行版）。
 * <p>
 * 由 {@code PDNetwork} 经反射调用，避免公共侧静态链接客户端类。
 * <p>
 * 提示界面延迟到加载屏关闭后再打开（否则会被 {@code ReceivingLevelScreen} 顶掉）。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public final class PDSaveCompatClientEvents {

    /** 待打开的提示数据 */
    private static volatile SaveUpgradePromptPayload pending;

    private PDSaveCompatClientEvents() {
        throw new UnsupportedOperationException("PDSaveCompatClientEvents 是客户端事件落地类，不可实例化");
    }

    /**
     * 处理 S2C 旧存档提示包：登记待打开，并在界面就绪后弹出。
     *
     * @param payload 提示数据
     */
    public static void handleSaveUpgradePrompt(SaveUpgradePromptPayload payload) {
        pending = payload;
        tryOpen();
    }

    /**
     * 客户端 tick：界面就绪时打开提示。
     *
     * @param event 客户端 tick 事件
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (pending != null) {
            tryOpen();
        }
    }

    /**
     * 若无其他界面遮挡且已进入世界，则打开提示界面。
     */
    private static void tryOpen() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null || minecraft.level == null) {
            return;
        }
        SaveUpgradePromptPayload payload = pending;
        if (payload == null) {
            return;
        }
        pending = null;
        minecraft.setScreen(new SaveUpgradePromptScreen(payload));
    }
}
