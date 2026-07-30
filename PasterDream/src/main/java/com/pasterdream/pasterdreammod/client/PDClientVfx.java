package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.network.EvasionPosePayload;
import com.pasterdream.pasterdreammod.network.ItemActivationPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 客户端 VFX 网络包落地（仅客户端类加载路径引用本类）。
 * <p>
 * 由 {@link com.pasterdream.pasterdreammod.network.PDNetwork} 的 S2C 处理器转发；
 * 勿在公共/服务端类上直接写 {@code Minecraft} 引用，以免专用服类加载失败。
 */
@OnlyIn(Dist.CLIENT)
public final class PDClientVfx {

    private PDClientVfx() {
    }

    /**
     * 全屏物品展示（图腾式）。
     *
     * @param payload 物品激活包
     */
    public static void handleItemActivation(ItemActivationPayload payload) {
        ItemStack stack = payload.asStack();
        if (stack.isEmpty()) {
            return;
        }
        Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
    }

    /**
     * 启动本地闪避姿势。
     *
     * @param payload 空载荷（保留参数与 handler 签名一致）
     */
    public static void handleEvasionPose(EvasionPosePayload payload) {
        PDPlayerAnimation.startEvasionPose();
    }
}
