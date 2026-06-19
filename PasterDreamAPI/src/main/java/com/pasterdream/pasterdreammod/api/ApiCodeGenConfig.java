package com.pasterdream.pasterdreammod.api;

import java.util.Objects;

/**
 * API 代码生成器的全局配置。
 * 主模组在启动时应调用 {@link #setDefaultBasePath(String)} 设置资源根目录。
 * 第三方使用者在使用生成器前也需设置此路径。
 */
public final class ApiCodeGenConfig {

    private ApiCodeGenConfig() {}

    private static String defaultBasePath;

    /** 代码生成器使用的注册类名 —— 物品 */
    private static String defaultItemRegistryClass = "PDItems";

    /** 代码生成器使用的注册类名 —— 方块 */
    private static String defaultBlockRegistryClass = "PDBlocks";

    /**
     * 设置生成器默认的资源根目录。
     * 对于主模组开发环境，通常为 {@code "src/main/resources"}。
     */
    public static void setDefaultBasePath(String path) {
        Objects.requireNonNull(path, "ApiCodeGenConfig: defaultBasePath cannot be null");
        defaultBasePath = path;
    }

    public static String getDefaultBasePath() {
        if (defaultBasePath == null) {
            throw new IllegalStateException(
                    "ApiCodeGenConfig: 未设置 defaultBasePath，请在模组启动时调用 ApiCodeGenConfig.setDefaultBasePath()");
        }
        return defaultBasePath;
    }

    public static void setDefaultItemRegistryClass(String className) {
        defaultItemRegistryClass = className;
    }

    public static String getDefaultItemRegistryClass() {
        return defaultItemRegistryClass;
    }

    public static void setDefaultBlockRegistryClass(String className) {
        defaultBlockRegistryClass = className;
    }

    public static String getDefaultBlockRegistryClass() {
        return defaultBlockRegistryClass;
    }
}
