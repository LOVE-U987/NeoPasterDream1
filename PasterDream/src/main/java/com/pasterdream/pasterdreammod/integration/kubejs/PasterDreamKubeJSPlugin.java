package com.pasterdream.pasterdreammod.integration.kubejs;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;

/**
 * PasterDream 的 KubeJS 插件入口
 * <p>
 * 由 {@code kubejs.plugins.txt} 声明，KubeJS 加载时自动实例化。
 */
public class PasterDreamKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void init() {
        PasterDreamMod.LOGGER.info("[PasterDream] KubeJS 插件已初始化");
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(PasterDreamKubeJSEvents.GROUP);
    }
}
