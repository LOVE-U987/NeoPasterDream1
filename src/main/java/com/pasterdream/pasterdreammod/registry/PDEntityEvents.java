package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

/**
 * 实体属性注册事件类
 * 负责将自定义实体的属性（如生命值、速度、伤害等）注册到游戏中
 * 同时处理实体的生成位置注册
 */
@EventBusSubscriber(modid = "pasterdream")
public class PDEntityEvents {

    /**
     * 在 EntityAttributeCreationEvent 事件中注册实体属性
     * 使用 {@link EntityAPI#registerAttributes(EntityAttributeCreationEvent, String)} 自动完成
     *
     * @param event 实体属性创建事件
     */
    @SubscribeEvent
    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        EntityAPI.registerAttributes(event, "shadow_golem");
        EntityAPI.registerAttributes(event, "pink_slime");
    }

    /**
     * 在 RegisterSpawnPlacementsEvent 事件中注册实体生成位置规则
     * 定义实体可以在地图的哪些位置自然生成（地面、水中、空中等）
     * 实际维度/群系限制由 biome_modifier JSON 控制
     *
     * @param event 生成位置注册事件
     */
    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(PDEntities.PINK_SLIME.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}