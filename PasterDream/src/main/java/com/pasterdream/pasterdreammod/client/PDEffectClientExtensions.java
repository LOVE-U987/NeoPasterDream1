package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 客户端状态效果扩展注册类 (Effect Client Extensions)
 * <p>
 * 还原原版 5 个"隐藏图标"效果的 {@code MobEffect#initializeClient} 客户端扩展——
 * 1.21.1 中该钩子已由 NeoForge {@link RegisterClientExtensionsEvent} 统一接管：
 * <ul>
 *   <li><b>HUD 与背包均隐藏</b>（原版覆写 isVisibleInGui + isVisibleInInventory）：
 *       云雾 cloudmist_buff、机翼 machine_wing_effect</li>
 *   <li><b>仅隐藏 HUD 图标</b>（原版仅覆写 isVisibleInGui）：
 *       波波脊 boboji_buff、顺风 tailwind_buff、死风 deadwind_buff</li>
 * </ul>
 * 这些效果均为内部计时器/状态标记（如剑气寿命、飞行状态），原版即不向玩家展示图标。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDEffectClientExtensions {

    /** 仅隐藏 HUD 叠加层图标（背包内仍可见，与原版一致） */
    private static final IClientMobEffectExtensions HIDE_GUI_ONLY = new IClientMobEffectExtensions() {
        @Override
        public boolean isVisibleInGui(MobEffectInstance instance) {
            return false;
        }
    };

    /** HUD 与背包界面全部隐藏（与原版一致） */
    private static final IClientMobEffectExtensions HIDE_EVERYWHERE = new IClientMobEffectExtensions() {
        @Override
        public boolean isVisibleInGui(MobEffectInstance instance) {
            return false;
        }

        @Override
        public boolean isVisibleInInventory(MobEffectInstance instance) {
            return false;
        }
    };

    /**
     * 注册客户端效果扩展（5 个隐藏图标效果）
     *
     * @param event 客户端扩展注册事件
     */
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        // HUD 与背包均隐藏：云雾（剑气/浮空计时器）、机翼（机械之翼飞行状态）
        event.registerMobEffect(HIDE_EVERYWHERE,
                PDEffects.CLOUDMIST_BUFF.effect(),
                PDEffects.MACHINE_WING_EFFECT.effect());

        // 仅隐藏 HUD 图标：波波脊、顺风、死风
        event.registerMobEffect(HIDE_GUI_ONLY,
                PDEffects.BOBOJI_BUFF.effect(),
                PDEffects.TAILWIND_BUFF.effect(),
                PDEffects.DEADWIND_BUFF.effect());

        PDDebugLogger.mainDebug("[PDEffectClientExtensions] 注册效果图标隐藏扩展: 5 个（boboji/cloudmist/machine_wing/tailwind/deadwind）");
    }
}
