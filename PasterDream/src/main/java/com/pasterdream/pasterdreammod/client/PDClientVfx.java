package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.api.client.effect.atmosphere.AtmosphereHandler;
import com.pasterdream.pasterdreammod.api.client.effect.cutscene.CutsceneCameraHandler;
import com.pasterdream.pasterdreammod.api.client.effect.ghost.GhostHandler;
import com.pasterdream.pasterdreammod.api.client.effect.impact.ImpactFramesHandler;
import com.pasterdream.pasterdreammod.api.client.effect.particle.ParticleEmitterHandler;
import com.pasterdream.pasterdreammod.api.client.effect.screen.ScreenEffect;
import com.pasterdream.pasterdreammod.api.client.effect.screen.ScreenEffectFactoryRegistry;
import com.pasterdream.pasterdreammod.api.client.effect.screen.ScreenEffectOverlay;
import com.pasterdream.pasterdreammod.api.client.effect.shake.ScreenShakeHandler;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneData;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectFactory;
import com.pasterdream.pasterdreammod.api.network.AtmospherePayload;
import com.pasterdream.pasterdreammod.api.network.GhostPayload;
import com.pasterdream.pasterdreammod.api.network.ImpactFramesPayload;
import com.pasterdream.pasterdreammod.api.network.ScreenShakePayload;
import com.pasterdream.pasterdreammod.api.network.ParticleEmitterPayload;
import com.pasterdream.pasterdreammod.api.network.ScreenEffectPayload;
import com.pasterdream.pasterdreammod.api.network.StartCutscenePayload;
import com.pasterdream.pasterdreammod.api.network.StopCutscenePayload;
import com.pasterdream.pasterdreammod.network.EvasionPosePayload;
import com.pasterdream.pasterdreammod.network.ItemActivationPayload;
import com.pasterdream.pasterdreammod.network.TwilightLanternMusicPayload;
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

    /**
     * 更新暮影之笼事件 BGM 状态（静音/恢复原版背景音乐）。
     *
     * @param payload 暮影之笼音乐状态包
     */
    public static void handleTwilightLanternMusic(TwilightLanternMusicPayload payload) {
        com.pasterdream.pasterdreammod.client.audio.TwilightLanternMusicHandler.setActive(payload.active());
    }

    /**
     * 落地一次粒子发射事件。
     *
     * @param payload 粒子发射器网络包
     */
    public static void handleParticleEmitter(ParticleEmitterPayload payload) {
        ParticleEmitterHandler.add(payload.data());
    }

    /**
     * 落地一次打击帧序列。
     *
     * @param payload 打击帧网络包
     */
    public static void handleImpactFrames(ImpactFramesPayload payload) {
        ImpactFramesHandler.addAll(payload.frames());
    }

    /**
     * 落地一次残影特效（为指定实体开启残影拖尾）。
     *
     * @param payload 残影网络包
     */
    public static void handleGhost(GhostPayload payload) {
        GhostHandler.start(payload.entityId(), payload.duration(), payload.alpha());
    }

    /**
     * 落地一次雾色/暗化氛围。
     *
     * @param payload 氛围网络包
     */
    public static void handleAtmosphere(AtmospherePayload payload) {
        AtmosphereHandler.start(payload);
    }

    /**
     * 落地一次屏幕晃动。
     *
     * @param payload 屏幕晃动网络包
     */
    public static void handleScreenShake(ScreenShakePayload payload) {
        ScreenShakeHandler.add(payload.data());
    }

    /**
     * 落地一次屏幕特效。
     * <p>
     * 经 payload 携带的特效类型 id 反查客户端工厂创建特效实例并加入叠加层。
     * 未知类型/工厂或解码失败时静默跳过（不崩溃）。
     *
     * @param payload 屏幕特效网络包
     */
    public static void handleScreenEffect(ScreenEffectPayload payload) {
        if (payload.data() == null) {
            return;
        }
        ScreenEffectFactory<?> factory = ScreenEffectFactoryRegistry.get(payload.typeId());
        if (factory == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        ScreenEffect<?> effect = (ScreenEffect<?>) factory.create(
                payload.data(), payload.inTime(), payload.stayTime(), payload.outTime());
        if (effect != null) {
            ScreenEffectOverlay.add(effect);
        }
    }

    /**
     * 落地一次过场动画开始。
     *
     * @param payload 过场开始网络包
     */
    public static void handleStartCutscene(StartCutscenePayload payload) {
        CutsceneCameraHandler.start(CutsceneData.fromTag(payload.dataNbt()));
    }

    /**
     * 落地一次过场动画停止。
     *
     * @param payload 过场停止网络包
     */
    public static void handleStopCutscene(StopCutscenePayload payload) {
        CutsceneCameraHandler.stop();
    }
}
