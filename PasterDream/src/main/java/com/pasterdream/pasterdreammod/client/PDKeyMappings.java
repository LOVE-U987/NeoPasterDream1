package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.network.CloakActivatePayload;
import com.pasterdream.pasterdreammod.network.TeleportationPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/**
 * 按键绑定注册类（仅客户端）
 * <p>
 * 移植自原版 {@code init/PasterdreamModKeyMappings.java}，翻译键、默认键位、分类与原版一致：
 * <ul>
 *   <li>{@link #TELEPORTATION} — 瞬身术（默认 C，分类 key.categories.pasterdream）</li>
 *   <li>{@link #CLOAK_ACTIVATE} — 斗篷激活（默认 Z，分类 key.categories.misc）</li>
 * </ul>
 * 触发方式与原版一致：重写 {@code setDown} 在按下沿发送 C2S 包
 * （原版同时在客户端本地调用 pressAction 做预执行；新版为服务端权威，客户端只发包，
 * 瞬身冲刺速度由服务端经 hurtMarked 运动包回传）；
 * 每客户端刻在无界面打开时 {@code consumeClick} 清空点击计数。
 * <p>
 * 服务端功能实现见 {@code network/PDNetwork} 的
 * executeTeleportation / executeCloakActivate。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDKeyMappings {

    /** 瞬身术按键（默认 C） */
    public static final KeyMapping TELEPORTATION = new KeyMapping(
            "key.pasterdream.teleportation", GLFW.GLFW_KEY_C, "key.categories.pasterdream") {
        private boolean isDownOld = false;

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (isDownOld != isDown && isDown && Minecraft.getInstance().getConnection() != null) {
                PacketDistributor.sendToServer(new TeleportationPayload(0, 0));
            }
            isDownOld = isDown;
        }
    };

    /** 斗篷激活按键（默认 Z，原版归在 misc 分类） */
    public static final KeyMapping CLOAK_ACTIVATE = new KeyMapping(
            "key.pasterdream.cloak_activate", GLFW.GLFW_KEY_Z, "key.categories.misc") {
        private boolean isDownOld = false;

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (isDownOld != isDown && isDown && Minecraft.getInstance().getConnection() != null) {
                PacketDistributor.sendToServer(new CloakActivatePayload(0, 0));
            }
            isDownOld = isDown;
        }
    };

    /**
     * 注册按键绑定（MOD 总线事件）
     *
     * @param event 按键注册事件
     */
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TELEPORTATION);
        event.register(CLOAK_ACTIVATE);
    }

    /**
     * 客户端刻末清空按键点击计数（对应原版 KeyEventListener#onClientTick）
     *
     * @param event 客户端刻事件
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().screen == null) {
            TELEPORTATION.consumeClick();
            CLOAK_ACTIVATE.consumeClick();
        }
    }
}
