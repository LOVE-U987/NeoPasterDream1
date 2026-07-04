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

        // 染梦世界生物 — 方案B（排除小石灵）
        EntityAPI.registerAttributes(event, "pink_chicken");
        EntityAPI.registerAttributes(event, "jellyfish");
        EntityAPI.registerAttributes(event, "friendly_ghost");
        EntityAPI.registerAttributes(event, "firefly");
        EntityAPI.registerAttributes(event, "golden_fox");
        EntityAPI.registerAttributes(event, "meltdream_crystal");
        // 恐怖尖喙变体系列
        EntityAPI.registerAttributes(event, "terrorbeak");
        EntityAPI.registerAttributes(event, "crazy_terrorbeak");
        EntityAPI.registerAttributes(event, "weakeness_terrorbeak");
        // 骨翼系列（飞行远程生物）
        EntityAPI.registerAttributes(event, "bone_wing");
        EntityAPI.registerAttributes(event, "ash_bone_wing");
        // 阴影系列（飞行敌对生物）
        EntityAPI.registerAttributes(event, "shadow_ghost");
        EntityAPI.registerAttributes(event, "shadow_squeal_ghost");
        EntityAPI.registerAttributes(event, "shadow_squeal_ghost_0");
        EntityAPI.registerAttributes(event, "shadow_hand");
        // 雷云系列（飞行敌对生物）
        EntityAPI.registerAttributes(event, "thundercloud");
        EntityAPI.registerAttributes(event, "highvoltage");
        // 其他敌对生物
        EntityAPI.registerAttributes(event, "wind_knight");
        EntityAPI.registerAttributes(event, "shaking_crystal");
        EntityAPI.registerAttributes(event, "shadow_tune_totem");
        EntityAPI.registerAttributes(event, "small_stone_spirit");
        EntityAPI.registerAttributes(event, "black_beetle");
        EntityAPI.registerAttributes(event, "black_beetle_mother");
        // 染梦新生物
        EntityAPI.registerAttributes(event, "basalt_snail");
        EntityAPI.registerAttributes(event, "fox_fire");
        EntityAPI.registerAttributes(event, "shadow_npc_0");
        EntityAPI.registerAttributes(event, "spore_entity");

        // BOSS 实体
        EntityAPI.registerAttributes(event, "aaroncos_lefthand_0");
        EntityAPI.registerAttributes(event, "aaroncos_righthand_0");
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

        // 粉色鸡：在地面生成（会飞，但初始生成在地面）
        event.register(PDEntities.PINK_CHICKEN.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 水母：在水中生成
        event.register(PDEntities.JELLYFISH.get(),
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 友好幽灵：在地面生成（飞行实体但初始在地面）
        event.register(PDEntities.FRIENDLY_GHOST.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 萤火虫：在地面生成（小型飞行生物）
        event.register(PDEntities.FIREFLY.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 金色狐狸：在地面生成（静止不动）
        event.register(PDEntities.GOLDEN_FOX.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 融梦水晶：在地面生成（静止漂浮，生成在靠近地面的位置）
        event.register(PDEntities.MELTDREAM_CRYSTAL.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // ==================== 阴影系列 ====================

        // 暗影幽灵：地面生成（飞行实体）
        event.register(PDEntities.SHADOW_GHOST.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 暗影尖啸幽灵：地面生成（飞行实体）
        event.register(PDEntities.SHADOW_SQUEAL_GHOST.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 暗影尖啸幽灵0：地面生成（飞行实体）
        event.register(PDEntities.SHADOW_SQUEAL_GHOST_0.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 暗影之手：地面生成（飞行实体）
        event.register(PDEntities.SHADOW_HAND.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // ==================== 雷云系列 ====================

        // 雷云：地面生成（飞行实体）
        event.register(PDEntities.THUNDERCLOUD.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 高压雷云：地面生成（飞行实体）
        event.register(PDEntities.HIGHVOLTAGE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // ==================== 其他敌对生物 ====================

        // 风之骑士：地面生成
        event.register(PDEntities.WIND_KNIGHT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 震动水晶：地面生成（静止）
        event.register(PDEntities.SHAKING_CRYSTAL.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 暗影调和图腾：地面生成（静止大型）
        event.register(PDEntities.SHADOW_TUNE_TOTEM.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 小石灵：地面生成
        event.register(PDEntities.SMALL_STONE_SPIRIT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 黑甲虫：地面生成
        event.register(PDEntities.BLACK_BEETLE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 黑甲虫母体：地面生成
        event.register(PDEntities.BLACK_BEETLE_MOTHER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // ==================== 染梦新生物 ====================

        // 玄武岩蜗牛：地面生成（中性）
        event.register(PDEntities.BASALT_SNAIL.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 狐火：地面生成（环境火焰精灵）
        event.register(PDEntities.FOX_FIRE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 暗影 ??? NPC：地面生成（被动NPC）
        event.register(PDEntities.SHADOW_NPC_0.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 孢子实体：地面生成（飞行实体，初始生成在地面）
        event.register(PDEntities.SPORE_ENTITY.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // ==================== BOSS 实体 ====================

        // 亚伦柯斯之触 - 左：地面生成（飞行 BOSS，初始生成在地面）
        event.register(PDEntities.AARONCOS_LEFTHAND_0.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 亚伦柯斯之触 - 右：地面生成（飞行 BOSS，初始生成在地面）
        event.register(PDEntities.AARONCOS_RIGHTHAND_0.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}