package com.pasterdream.pasterdreammod.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * PasterDream 自定义 KubeJS 事件组
 */
public interface PasterDreamKubeJSEvents {

    EventGroup GROUP = EventGroup.of("PasterDreamEvents");

    EventHandler DOLL_REGISTRY = GROUP.startup("dollRegistry", () -> DollRegistryKubeJSEvent.class);
}
