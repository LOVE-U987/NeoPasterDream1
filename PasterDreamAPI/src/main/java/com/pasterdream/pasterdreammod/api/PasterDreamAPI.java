package com.pasterdream.pasterdreammod.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PasterDreamAPI 前置模组常量定义。
 * 提供 MOD_ID 和 LOGGER 供所有 API 子模块使用。
 * 主模组应引用此常量确保命名空间一致。
 */
public final class PasterDreamAPI {

    /** 模组命名空间，须与主模组 {@code PasterDreamAPI.MOD_ID} 一致 */
    public static final String MOD_ID = "pasterdream";

    /** API 模块日志记录器 */
    public static final Logger LOGGER = LoggerFactory.getLogger("PasterDreamAPI");

    private PasterDreamAPI() {
        throw new UnsupportedOperationException("PasterDreamAPI 是常量类，不可实例化");
    }
}
