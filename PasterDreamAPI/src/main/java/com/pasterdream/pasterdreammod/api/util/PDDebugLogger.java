package com.pasterdream.pasterdreammod.api.util;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * 帕斯特之梦模组统一调试日志工具类。
 * <p>
 * 通过配置文件中的 Debug 分类开关控制所有调试/诊断级别日志（debug、info）的输出。
 * 所有 warn/error 级别的日志不受此类控制，确保异常与错误始终可见。
 * <p>
 * 使用方式：
 * <ul>
 *   <li>API 模块代码调用 {@code PDDebugLogger.apiDebug(...)} / {@code apiInfo(...)}</li>
 *   <li>主模块代码调用 {@code PDDebugLogger.mainDebug(...)} / {@code mainInfo(...)}</li>
 *   <li>smoketest / 移植验证代码调用 {@code PDDebugLogger.smoketestDebug(...)} / {@code smoketestInfo(...)}</li>
 * </ul>
 *
 * @author PasterDream
 */
public final class PDDebugLogger {

    /** API 模块日志记录器，默认使用 {@link PasterDreamAPI#LOGGER} */
    private static Logger apiLogger = PasterDreamAPI.LOGGER;
    /** 主模块日志记录器，默认回退到 {@link PasterDreamAPI#LOGGER} */
    private static Logger mainLogger = PasterDreamAPI.LOGGER;

    /** 调试总开关 Supplier */
    private static Supplier<Boolean> masterEnabled = () -> false;
    /** API 模块调试开关 Supplier */
    private static Supplier<Boolean> apiEnabled = () -> false;
    /** 主模块调试开关 Supplier */
    private static Supplier<Boolean> mainEnabled = () -> false;
    /** Smoketest / 移植验证调试开关 Supplier */
    private static Supplier<Boolean> smoketestEnabled = () -> false;

    private PDDebugLogger() {
        throw new UnsupportedOperationException("PDDebugLogger 是工具类，不可实例化");
    }

    /**
     * 设置 API 模块使用的日志记录器。
     *
     * @param logger 日志记录器；为 null 时保持默认
     */
    public static void setApiLogger(Logger logger) {
        if (logger != null) {
            apiLogger = logger;
        }
    }

    /**
     * 设置主模块使用的日志记录器。
     *
     * @param logger 日志记录器；为 null 时保持默认
     */
    public static void setMainLogger(Logger logger) {
        if (logger != null) {
            mainLogger = logger;
        }
    }

    /**
     * 注入调试总开关。
     *
     * @param supplier 总开关 Supplier；为 null 时视为始终关闭
     */
    public static void setMasterEnabled(Supplier<Boolean> supplier) {
        masterEnabled = supplier != null ? supplier : () -> false;
    }

    /**
     * 注入 API 模块调试开关。
     *
     * @param supplier API 模块开关 Supplier；为 null 时视为始终关闭
     */
    public static void setApiEnabled(Supplier<Boolean> supplier) {
        apiEnabled = supplier != null ? supplier : () -> false;
    }

    /**
     * 注入主模块调试开关。
     *
     * @param supplier 主模块开关 Supplier；为 null 时视为始终关闭
     */
    public static void setMainEnabled(Supplier<Boolean> supplier) {
        mainEnabled = supplier != null ? supplier : () -> false;
    }

    /**
     * 注入 Smoketest / 移植验证调试开关。
     *
     * @param supplier Smoketest 开关 Supplier；为 null 时视为始终关闭
     */
    public static void setSmoketestEnabled(Supplier<Boolean> supplier) {
        smoketestEnabled = supplier != null ? supplier : () -> false;
    }

    /**
     * @return API 模块调试日志是否启用（总开关与 API 开关同时打开）
     * <p>
     * 注意：在配置尚未加载（如 RegisterEvent 阶段）时返回 false，避免抛 {@link IllegalStateException}。
     */
    public static boolean isApiDebugEnabled() {
        try {
            return Boolean.TRUE.equals(masterEnabled.get()) && Boolean.TRUE.equals(apiEnabled.get());
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * @return 主模块调试日志是否启用（总开关与主模块开关同时打开）
     * <p>
     * 注意：在配置尚未加载（如 RegisterEvent 阶段）时返回 false，避免抛 {@link IllegalStateException}。
     */
    public static boolean isMainDebugEnabled() {
        try {
            return Boolean.TRUE.equals(masterEnabled.get()) && Boolean.TRUE.equals(mainEnabled.get());
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * @return Smoketest 调试日志是否启用（总开关与 Smoketest 开关同时打开）
     * <p>
     * 注意：在配置尚未加载（如 RegisterEvent 阶段）时返回 false，避免抛 {@link IllegalStateException}。
     */
    public static boolean isSmoketestDebugEnabled() {
        try {
            return Boolean.TRUE.equals(masterEnabled.get()) && Boolean.TRUE.equals(smoketestEnabled.get());
        } catch (IllegalStateException e) {
            return false;
        }
    }

    // ==================== API 模块调试日志 ====================

    /**
     * 输出 API 模块 debug 日志。
     *
     * @param message 日志消息
     */
    public static void apiDebug(String message) {
        if (isApiDebugEnabled()) {
            apiLogger.debug(message);
        }
    }

    /**
     * 输出 API 模块 debug 日志（带一个参数）。
     *
     * @param format 消息模板
     * @param arg    模板参数
     */
    public static void apiDebug(String format, Object arg) {
        if (isApiDebugEnabled()) {
            apiLogger.debug(format, arg);
        }
    }

    /**
     * 输出 API 模块 debug 日志（带多个参数）。
     *
     * @param format 消息模板
     * @param args   模板参数列表
     */
    public static void apiDebug(String format, Object... args) {
        if (isApiDebugEnabled()) {
            apiLogger.debug(format, args);
        }
    }

    /**
     * 输出 API 模块 info 日志。
     *
     * @param message 日志消息
     */
    public static void apiInfo(String message) {
        if (isApiDebugEnabled()) {
            apiLogger.info(message);
        }
    }

    /**
     * 输出 API 模块 info 日志（带一个参数）。
     *
     * @param format 消息模板
     * @param arg    模板参数
     */
    public static void apiInfo(String format, Object arg) {
        if (isApiDebugEnabled()) {
            apiLogger.info(format, arg);
        }
    }

    /**
     * 输出 API 模块 info 日志（带多个参数）。
     *
     * @param format 消息模板
     * @param args   模板参数列表
     */
    public static void apiInfo(String format, Object... args) {
        if (isApiDebugEnabled()) {
            apiLogger.info(format, args);
        }
    }

    // ==================== 主模块调试日志 ====================

    /**
     * 输出主模块 debug 日志。
     *
     * @param message 日志消息
     */
    public static void mainDebug(String message) {
        if (isMainDebugEnabled()) {
            mainLogger.debug(message);
        }
    }

    /**
     * 输出主模块 debug 日志（带一个参数）。
     *
     * @param format 消息模板
     * @param arg    模板参数
     */
    public static void mainDebug(String format, Object arg) {
        if (isMainDebugEnabled()) {
            mainLogger.debug(format, arg);
        }
    }

    /**
     * 输出主模块 debug 日志（带多个参数）。
     *
     * @param format 消息模板
     * @param args   模板参数列表
     */
    public static void mainDebug(String format, Object... args) {
        if (isMainDebugEnabled()) {
            mainLogger.debug(format, args);
        }
    }

    /**
     * 输出主模块 info 日志。
     *
     * @param message 日志消息
     */
    public static void mainInfo(String message) {
        if (isMainDebugEnabled()) {
            mainLogger.info(message);
        }
    }

    /**
     * 输出主模块 info 日志（带一个参数）。
     *
     * @param format 消息模板
     * @param arg    模板参数
     */
    public static void mainInfo(String format, Object arg) {
        if (isMainDebugEnabled()) {
            mainLogger.info(format, arg);
        }
    }

    /**
     * 输出主模块 info 日志（带多个参数）。
     *
     * @param format 消息模板
     * @param args   模板参数列表
     */
    public static void mainInfo(String format, Object... args) {
        if (isMainDebugEnabled()) {
            mainLogger.info(format, args);
        }
    }

    // ==================== Smoketest / 移植验证调试日志 ====================

    /**
     * 输出 Smoketest debug 日志。
     *
     * @param message 日志消息
     */
    public static void smoketestDebug(String message) {
        if (isSmoketestDebugEnabled()) {
            mainLogger.debug(message);
        }
    }

    /**
     * 输出 Smoketest debug 日志（带一个参数）。
     *
     * @param format 消息模板
     * @param arg    模板参数
     */
    public static void smoketestDebug(String format, Object arg) {
        if (isSmoketestDebugEnabled()) {
            mainLogger.debug(format, arg);
        }
    }

    /**
     * 输出 Smoketest debug 日志（带多个参数）。
     *
     * @param format 消息模板
     * @param args   模板参数列表
     */
    public static void smoketestDebug(String format, Object... args) {
        if (isSmoketestDebugEnabled()) {
            mainLogger.debug(format, args);
        }
    }

    /**
     * 输出 Smoketest info 日志。
     *
     * @param message 日志消息
     */
    public static void smoketestInfo(String message) {
        if (isSmoketestDebugEnabled()) {
            mainLogger.info(message);
        }
    }

    /**
     * 输出 Smoketest info 日志（带一个参数）。
     *
     * @param format 消息模板
     * @param arg    模板参数
     */
    public static void smoketestInfo(String format, Object arg) {
        if (isSmoketestDebugEnabled()) {
            mainLogger.info(format, arg);
        }
    }

    /**
     * 输出 Smoketest info 日志（带多个参数）。
     *
     * @param format 消息模板
     * @param args   模板参数列表
     */
    public static void smoketestInfo(String format, Object... args) {
        if (isSmoketestDebugEnabled()) {
            mainLogger.info(format, args);
        }
    }
}
