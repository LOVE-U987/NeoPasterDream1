package com.pasterdream.pasterdreammod.dreamnotes;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.lang.reflect.Method;

/**
 * 当前客户端语言桥（common 侧）。
 * <p>
 * 供 common 物品的 tooltip 按语言取文本；经反射调用客户端
 * {@code PDClientNoteScreens.currentLanguage()}，避免 common 类静态链接客户端类
 * （沿用 {@code PDNetwork -> PDClientNoteScreens} 反射模式，专用服安全）。
 * 非客户端环境或反射失败时回退 {@code en_us}。
 */
public final class NoteLangBridge {

    /** 客户端笔记语言 helper 全限定名。 */
    private static final String CLIENT_CLASS = "com.pasterdream.pasterdreammod.client.PDClientNoteScreens";

    private static Method cachedMethod;
    private static boolean resolved;

    private NoteLangBridge() {
    }

    /**
     * 返回当前客户端语言键（{@code zh_cn} / {@code en_us}）。
     *
     * @return 语言键；非客户端或不可用时返回 {@code en_us}
     */
    public static String current() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return "en_us";
        }
        Method method = resolve();
        if (method == null) {
            return "en_us";
        }
        try {
            Object result = method.invoke(null);
            return result instanceof String value ? value : "en_us";
        } catch (ReflectiveOperationException exception) {
            return "en_us";
        }
    }

    /** 解析并缓存客户端方法（仅一次）。 */
    private static Method resolve() {
        if (!resolved) {
            resolved = true;
            try {
                cachedMethod = Class.forName(CLIENT_CLASS).getMethod("currentLanguage");
            } catch (ReflectiveOperationException exception) {
                cachedMethod = null;
                PasterDreamMod.LOGGER.warn(
                        "[NoteLangBridge] 无法解析 {}.currentLanguage()，tooltip 语言将回退 en_us；"
                                + "若该方法被改名，请同步本桥的类名与方法名",
                        CLIENT_CLASS);
            }
        }
        return cachedMethod;
    }
}
