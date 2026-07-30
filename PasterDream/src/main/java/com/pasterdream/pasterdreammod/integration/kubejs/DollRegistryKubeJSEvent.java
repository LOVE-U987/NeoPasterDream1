package com.pasterdream.pasterdreammod.integration.kubejs;

import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import com.pasterdream.pasterdreammod.api.doll.DollBuilder;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.ResourceLocation;

/**
 * KubeJS 脚本中 {@code PasterDreamEvents.dollRegistry} 事件的实现
 */
public class DollRegistryKubeJSEvent implements KubeEvent {

    /**
     * 创建一个 JS 侧玩偶 Builder
     *
     * @param name 玩偶注册名
     * @return JS Builder
     */
    public DollJSBuilder create(String name) {
        return new DollJSBuilder(name);
    }

    /**
     * JS 侧玩偶 Builder 包装
     */
    public static class DollJSBuilder {

        private final DollBuilder builder;

        private DollJSBuilder(String name) {
            this.builder = DollAPI.create(name);
        }

        public DollJSBuilder model(String model) {
            builder.model(ResourceLocation.parse(model));
            return this;
        }

        public DollJSBuilder texture(String texture) {
            builder.texture(ResourceLocation.parse(texture));
            return this;
        }

        public DollJSBuilder canHoldItems(boolean canHoldItems) {
            builder.canHoldItems(canHoldItems);
            return this;
        }

        public DollJSBuilder holdingModel(String holdingModel) {
            builder.holdingModel(ResourceLocation.parse(holdingModel));
            return this;
        }

        /**
         * 执行注册（使用 {@link com.pasterdream.pasterdreammod.api.doll.DollBuilder#registerDirect()}，
         * 绕过 DeferredRegister，因为 KubeJS 启动脚本在注册阶段之后执行）。
         */
        public void register() {
            builder.registerDirect();
        }
    }
}
