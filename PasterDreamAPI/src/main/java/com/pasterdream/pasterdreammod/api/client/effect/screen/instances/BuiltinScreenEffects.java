package com.pasterdream.pasterdreammod.api.client.effect.screen.instances;

import com.pasterdream.pasterdreammod.api.client.effect.screen.ScreenEffectFactoryRegistry;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectRegistry;
import com.pasterdream.pasterdreammod.api.effect.screen.instances.ScreenColorData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 内置屏幕特效注册入口（客户端）
 * <p>
 * 静态初始化时：
 * <ul>
 *   <li>把内置特效的<b>数据编解码类型</b>注册进服务端安全的
 *       {@link ScreenEffectRegistry}（也可由静态块预先注册）；</li>
 *   <li>把内置特效的<b>客户端工厂</b>注册进 {@link ScreenEffectFactoryRegistry}，
 *       供客户端反查创建特效实例。</li>
 * </ul>
 * 主模客户端在初始化时显式引用本类触发注册（如 FMLClientSetupEvent）。
 */
@OnlyIn(Dist.CLIENT)
public final class BuiltinScreenEffects {

    private BuiltinScreenEffects() {
        throw new UnsupportedOperationException("BuiltinScreenEffects 是纯静态注册入口，不可实例化");
    }

    /** 注册全部内置屏幕特效（数据编解码 + 客户端工厂） */
    public static void registerAll() {
        // 数据编解码类型（服务端安全，静态块已注册 ScreenColorData.TYPE，此处幂等补充）
        ScreenEffectRegistry.register(ScreenColorData.TYPE);
        // 客户端工厂
        ScreenEffectFactoryRegistry.register(ScreenColorData.TYPE_ID,
                (data, in, stay, out) -> new ScreenColorEffect((ScreenColorData) data, in, stay, out));
    }
}
