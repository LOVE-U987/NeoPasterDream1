package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.network.EvasionPosePayload;
import com.pasterdream.pasterdreammod.network.ItemActivationPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;

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
     * <p>playeranimator 未安装时 no-op，且不触碰 {@link PDPlayerAnimation} 类加载。</p>
     *
     * @param payload 空载荷（保留参数与 handler 签名一致）
     */
    public static void handleEvasionPose(EvasionPosePayload payload) {
        // 类加载守卫：playeranimator 未安装时绝不能加载 PDPlayerAnimation（其方法体含 playerAnim 硬符号，加载即 NoClassDefFoundError）。
        // 故必须用字面量 modId 判断（不可写 PDPlayerAnimation.XXX，否则 getstatic 会先加载该类）。
        // 此检查与 startEvasionPose() 内部 isAvailable() 并非冗余：内层仅在类已加载后防 API 误用，外层才是真正的加载门禁。
        if (!ModList.get().isLoaded("playeranimator")) {
            return;
        }
        PDPlayerAnimation.startEvasionPose();
    }
}
