package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

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

        // 技能投射物/剑气
        EntityAPI.registerAttributes(event, "terrasword_wave");
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

        // 悦灵：染梦群系刷怪表引用了 vanilla 悦灵，但 vanilla 未注册其生成规则
        // （原版仅经结构生成），缺失会在生成尝试时告警——补地面生成规则
        event.register(net.minecraft.world.entity.EntityType.ALLAY,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 骨翼/灰烬骨翼：地面生成（飞行远程生物，群系刷怪表已引用，缺放置规则会告警）
        event.register(PDEntities.BONE_WING.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PDEntities.ASH_BONE_WING.get(),
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
        // 原模组逻辑：方块下方为 ANIMALS_SPAWNABLE_ON + 亮度 > 8
        event.register(PDEntities.SPORE_ENTITY.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) ->
                        world.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON)
                                && world.getRawBrightness(pos, 0) > 8,
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

    /**
     * 限制原版发光鱿鱼（glow squid）在染梦维度中的自然生成
     * 原版 {@code GlowSquid#checkGlowSquidSpawnRules} 的判定为：
     * {@code pos.getY() <= seaLevel - 33}（深度上限）+ {@code getRawBrightness(pos, 0) == 0}
     * （生成点必须全黑）+ 生成位置为水方块。
     * 叠加 {@code dyedream_world} 噪声设置中 {@code aquifers_enabled: true}（地下含水层）后，
     * 海洋群系覆盖区的地下含水洞穴仍会成为发光鱿鱼的大量生成点，导致部分区域异常大量
     * 刷新，进而造成游戏卡顿。
     * 因此本规则在完整保留原版判定的基础上，额外要求生成点高度不低于海床高度图
     * （OCEAN_FLOOR_WG）：被顶板覆盖的含水层洞穴会被拒绝，露天海洋/湖泊的黑暗深水
     * 仍可正常生成；河流等浅水因原版深度上限天然被拒绝。
     *
     * @param event 生成位置注册事件
     */
    @SubscribeEvent
    public static void restrictGlowSquidSpawn(RegisterSpawnPlacementsEvent event) {
        event.register(EntityType.GLOW_SQUID,
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) ->
                        GlowSquid.checkGlowSquidSpawnRules(entityType, level, spawnType, pos, random)
                                && (!PDDimensions.isDyedreamWorld(level.getLevel())
                                    || pos.getY() >= level.getHeight(
                                            Heightmap.Types.OCEAN_FLOOR_WG,
                                            pos.getX(), pos.getZ())),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    /**
     * 发光鱿鱼刷新数量上限
     * 在染梦维度内，统计生成点周围（半径 = 服务器 simulation-distance）内所有非持久化
     * 发光鱿鱼；达到配置上限时拒绝本次自然生成。仅限制自然生成（NATURAL），不影响
     * 刷怪笼、刷怪蛋与区块生成。统计排除持久化个体，口径与游戏原版刷怪上限一致。
     *
     * @param event 生物生成位置检测事件
     */
    @SubscribeEvent
    public static void capGlowSquidSpawn(MobSpawnEvent.PositionCheck event) {
        if (event.getSpawnType() != MobSpawnType.NATURAL) {
            return;
        }
        if (!(event.getEntity() instanceof GlowSquid)) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!PDDimensions.isDyedreamWorld(level)) {
            return;
        }
        if (!PDCommonConfig.GLOW_SQUID_SPAWN_CAP_ENABLED.get()) {
            return;
        }
        int cap = PDCommonConfig.GLOW_SQUID_SPAWN_CAP.get();
        int radiusBlocks = level.getServer().getPlayerList().getSimulationDistance() * 16;
        BlockPos pos = event.getEntity().blockPosition();
        AABB area = new AABB(pos).inflate(radiusBlocks);
        int count = level.getEntitiesOfClass(GlowSquid.class, area,
                squid -> !squid.isPersistenceRequired() && !squid.requiresCustomPersistence()).size();
        if (count >= cap) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }
}
