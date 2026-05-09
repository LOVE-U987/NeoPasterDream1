package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.entity.mob.PinkSlimeEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowGolemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/**
 * 实体属性注册事件类
 * 负责将自定义实体的属性（如生命值、速度、伤害等）注册到游戏中
 */
@EventBusSubscriber(modid = "pasterdream", bus = EventBusSubscriber.Bus.MOD)
public class PDEntityEvents {

    /**
     * 在 EntityAttributeCreationEvent 事件中注册实体属性
     * 每个实体类型都需要调用其对应的 createAttributes() 静态方法
     *
     * @param event 实体属性创建事件
     */
    @SubscribeEvent
    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(PDEntities.SHADOW_GOLEM.get(), ShadowGolemEntity.createAttributes().build());
        event.put(PDEntities.PINK_SLIME.get(), PinkSlimeEntity.createAttributes().build());
    }
}