package com.pasterdream.pasterdreammod.client.sky.skystatic;

import net.neoforged.fml.ModList;

/**
 * Iris / Oculus 运行时检测工具
 * <p>
 * 检测当前是否加载了 Iris（NeoForge）或 Oculus（Forge 移植版）
 * 光影加载器，用于决定是否启用 Static Sky 渲染模式。
 * <p>
 * 不使用编译期依赖（Iris/Oculus 为可选，非必需前置）。
 */
public final class IrisDetector {
    private static Boolean irisLoaded = null;

    private IrisDetector() {
    }

    /**
     * 检测 Iris 或 Oculus 是否已加载
     *
     * @return 已加载则 true
     */
    public static boolean isIrisLoaded() {
        if (irisLoaded == null) {
            ModList modList = ModList.get();
            irisLoaded = modList != null
                    && (modList.isLoaded("oculus") || modList.isLoaded("iris"));
        }
        return irisLoaded;
    }
}