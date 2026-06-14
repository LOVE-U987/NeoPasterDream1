package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityResult;
import com.pasterdream.pasterdreammod.entity.mob.AshBoneWingEntity;
import com.pasterdream.pasterdreammod.entity.mob.BasaltSnailEntity;
import com.pasterdream.pasterdreammod.entity.mob.BlackBeetleEntity;
import com.pasterdream.pasterdreammod.entity.mob.BlackBeetleMotherEntity;
import com.pasterdream.pasterdreammod.entity.mob.BoneWingEntity;
import com.pasterdream.pasterdreammod.entity.mob.CrazyTerrorbeakEntity;
import com.pasterdream.pasterdreammod.entity.mob.FireflyEntity;
import com.pasterdream.pasterdreammod.entity.mob.FoxFireEntity;
import com.pasterdream.pasterdreammod.entity.mob.FriendlyGhostEntity;
import com.pasterdream.pasterdreammod.entity.mob.GoldenFoxEntity;
import com.pasterdream.pasterdreammod.entity.mob.HighvoltageThundercloudEntity;
import com.pasterdream.pasterdreammod.entity.mob.JellyfishEntity;
import com.pasterdream.pasterdreammod.entity.mob.MeltdreamCrystalEntity;
import com.pasterdream.pasterdreammod.entity.mob.PinkChickenEntity;
import com.pasterdream.pasterdreammod.entity.mob.PinkSlimeEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShakingCrystalEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowGhostEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowGolemEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowHandEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowNpc0Entity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowSquealGhost0Entity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowSquealGhostEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowTuneTotemEntity;
import com.pasterdream.pasterdreammod.entity.mob.SmallStoneSpiritEntity;
import com.pasterdream.pasterdreammod.entity.mob.SporeEntityEntity;
import com.pasterdream.pasterdreammod.entity.mob.TerrorbeakEntity;
import com.pasterdream.pasterdreammod.entity.mob.ThundercloudEntity;
import com.pasterdream.pasterdreammod.entity.mob.WeakenessTerrorbeakEntity;
import com.pasterdream.pasterdreammod.entity.mob.WindKnightEntity;
import com.pasterdream.pasterdreammod.entity.projectile.BoneWingFireBallProjectileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import java.util.function.Supplier;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 实体注册类
 * <p>
 * 使用 {@link EntityAPI} 的 Facade+Builder 模式注册所有实体，
 * 同时保持 {@link #SHADOW_GOLEM} / {@link #PINK_SLIME} 常量的向后兼容性。
 */
public class PDEntities {

    /**
     * 实体类型注册器（指向 {@link EntityAPI#REGISTRY}）
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = EntityAPI.REGISTRY;

    // ======================== EntityAPI 注册 ========================

    /**
     * 暗影魔像实体注册结果
     * 大型暗影主题怪物，使用 GeckoLib 动画
     * 尺寸: 2.2f x 3.5f
     */
    private static final EntityResult<ShadowGolemEntity> SHADOW_GOLEM_RESULT =
            EntityAPI.createEntity("shadow_golem")
                    .category(MobCategory.MONSTER)
                    .size(2.2f, 3.5f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(ShadowGolemEntity.class)
                    .attributes(ShadowGolemEntity::createAttributes)
                    .spawnEgg(0x191926, 0xA7A5B1)
                    .build();

    /**
     * 粉色史莱姆实体注册结果
     * 友好的粉色史莱姆生物，使用原版模型
     * 尺寸: 0.5f x 0.5f
     */
    private static final EntityResult<PinkSlimeEntity> PINK_SLIME_RESULT =
            EntityAPI.createEntity("pink_slime")
                    .category(MobCategory.CREATURE)
                    .size(0.5f, 0.5f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(PinkSlimeEntity.class)
                    .attributes(PinkSlimeEntity::createAttributes)
                    .spawnEgg(0xEFB0E4, 0xF27DC8)
                    .build();

    /**
     * 粉色鸡实体注册结果
     * 会飞的染梦世界小鸡，使用原版 ChickenModel 渲染
     * 尺寸: 0.4f x 0.7f
     */
    private static final EntityResult<PinkChickenEntity> PINK_CHICKEN_RESULT =
            EntityAPI.createEntity("pink_chicken")
                    .category(MobCategory.CREATURE)
                    .size(0.4f, 0.7f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(PinkChickenEntity.class)
                    .attributes(PinkChickenEntity::createAttributes)
                    .spawnEgg(0xFFC0CB, 0xFF69B4)
                    .build();

    /**
     * 水母实体注册结果
     * 染梦海洋中的发光 GeckoLib 水母
     * 尺寸: 0.8f x 0.8f
     */
    private static final EntityResult<JellyfishEntity> JELLYFISH_RESULT =
            EntityAPI.createEntity("jellyfish")
                    .category(MobCategory.WATER_CREATURE)
                    .size(0.8f, 0.8f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(JellyfishEntity.class)
                    .attributes(JellyfishEntity::createAttributes)
                    .spawnEgg(0x00CED1, 0xE0FFFF)
                    .build();

    /**
     * 怨魂实体注册结果
     * 敌对飞行幽灵，主动攻击玩家
     * 尺寸: 1.0f x 1.2f
     */
    private static final EntityResult<FriendlyGhostEntity> FRIENDLY_GHOST_RESULT =
            EntityAPI.createEntity("friendly_ghost")
                    .category(MobCategory.MONSTER)
                    .size(1.0f, 1.2f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(FriendlyGhostEntity.class)
                    .attributes(FriendlyGhostEntity::createAttributes)
                    .spawnEgg(0xF0F4F6, 0xBDDDEA)
                    .build();

    /**
     * 萤火虫实体注册结果
     * 染梦世界飞舞的发光 GeckoLib 小虫
     * 尺寸: 0.3f x 0.3f
     */
    private static final EntityResult<FireflyEntity> FIREFLY_RESULT =
            EntityAPI.createEntity("firefly")
                    .category(MobCategory.CREATURE)
                    .size(0.3f, 0.3f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(FireflyEntity.class)
                    .attributes(FireflyEntity::createAttributes)
                    .spawnEgg(0xFFFF00, 0x556B2F)
                    .build();

    /**
     * 金色狐狸实体注册结果
     * 静止不动的 GeckoLib 神秘金狐，接受祭品实现愿望
     * 尺寸: 0.6f x 0.6f
     */
    private static final EntityResult<GoldenFoxEntity> GOLDEN_FOX_RESULT =
            EntityAPI.createEntity("golden_fox")
                    .category(MobCategory.CREATURE)
                    .size(0.6f, 0.6f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(GoldenFoxEntity.class)
                    .attributes(GoldenFoxEntity::createAttributes)
                    .spawnEgg(0xFDF16A, 0xEAB518)
                    .build();

    /**
     * 融梦水晶实体注册结果
     * 漂浮的水晶装饰 GeckoLib 实体，右键消失掉落水晶碎片
     * 尺寸: 0.6f x 1.0f
     */
    private static final EntityResult<MeltdreamCrystalEntity> MELTDREAM_CRYSTAL_RESULT =
            EntityAPI.createEntity("meltdream_crystal")
                    .category(MobCategory.CREATURE)
                    .size(0.6f, 1.0f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(MeltdreamCrystalEntity.class)
                    .attributes(MeltdreamCrystalEntity::createAttributes)
                    .spawnEgg(0xFF69B4, 0xDB7093)
                    .build();

    /**
     * 恐怖尖喙实体注册结果
     * 大型地面敌对生物，40血，免疫火焰/仙人掌/凋零
     * 尺寸: 1.8f x 4.0f
     */
    private static final EntityResult<TerrorbeakEntity> TERRORBEAK_RESULT =
            EntityAPI.createEntity("terrorbeak")
                    .category(MobCategory.MONSTER)
                    .size(1.8f, 4.0f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(TerrorbeakEntity.class)
                    .attributes(TerrorbeakEntity::createAttributes)
                    .spawnEgg(0x000000, 0x323233)
                    .build();

    /**
     * 疯狂恐怖尖喙实体注册结果
     * 更强的大型地面敌对生物，60血，速度更快
     * 尺寸: 1.8f x 4.0f
     */
    private static final EntityResult<CrazyTerrorbeakEntity> CRAZY_TERRORBEAK_RESULT =
            EntityAPI.createEntity("crazy_terrorbeak")
                    .category(MobCategory.MONSTER)
                    .size(1.8f, 4.0f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(CrazyTerrorbeakEntity.class)
                    .attributes(CrazyTerrorbeakEntity::createAttributes)
                    .spawnEgg(0x000000, 0x540303)
                    .build();

    /**
     * 虚弱恐怖尖喙实体注册结果
     * 弱化版恐怖尖喙，30血，体型较小
     * 尺寸: 1.5f x 3.0f
     */
    private static final EntityResult<WeakenessTerrorbeakEntity> WEAKENESS_TERRORBEAK_RESULT =
            EntityAPI.createEntity("weakeness_terrorbeak")
                    .category(MobCategory.MONSTER)
                    .size(1.5f, 3.0f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(WeakenessTerrorbeakEntity.class)
                    .attributes(WeakenessTerrorbeakEntity::createAttributes)
                    .spawnEgg(0x000000, 0x333333)
                    .build();

    /**
     * 骨翼实体注册结果
     * 飞行远程敌对生物，使用火球攻击
     * 尺寸: 1.7f x 0.8f
     */
    private static final EntityResult<BoneWingEntity> BONE_WING_RESULT =
            EntityAPI.createEntity("bone_wing")
                    .category(MobCategory.MONSTER)
                    .size(1.7f, 0.8f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(BoneWingEntity.class)
                    .attributes(BoneWingEntity::createAttributes)
                    .spawnEgg(0xEDE4B4, 0xAF3016)
                    .build();

    /**
     * 灰烬骨翼实体注册结果
     * 更强的飞行远程敌对生物，32血3护甲
     * 尺寸: 1.7f x 0.8f
     */
    private static final EntityResult<AshBoneWingEntity> ASH_BONE_WING_RESULT =
            EntityAPI.createEntity("ash_bone_wing")
                    .category(MobCategory.MONSTER)
                    .size(1.7f, 0.8f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(AshBoneWingEntity.class)
                    .attributes(AshBoneWingEntity::createAttributes)
                    .spawnEgg(0x5F5E58, 0xAD4E16)
                    .build();

    /**
     * 暗影幽灵实体注册结果
     * 小型飞行敌对生物，10血，免疫火焰
     * 尺寸: 0.7f x 1.2f
     */
    private static final EntityResult<ShadowGhostEntity> SHADOW_GHOST_RESULT =
            EntityAPI.createEntity("shadow_ghost")
                    .category(MobCategory.MONSTER)
                    .size(0.7f, 1.2f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(ShadowGhostEntity.class)
                    .attributes(ShadowGhostEntity::createAttributes)
                    .spawnEgg(0xF6F9F9, 0x767DBB)
                    .build();

    /**
     * 暗影尖啸幽灵实体注册结果
     * 飞行射弹敌对生物，10血，免疫火焰
     * 尺寸: 0.7f x 1.2f
     */
    private static final EntityResult<ShadowSquealGhostEntity> SHADOW_SQUEAL_GHOST_RESULT =
            EntityAPI.createEntity("shadow_squeal_ghost")
                    .category(MobCategory.MONSTER)
                    .size(0.7f, 1.2f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(ShadowSquealGhostEntity.class)
                    .attributes(ShadowSquealGhostEntity::createAttributes)
                    .spawnEgg(0xF4F7F9, 0xAEDCF0)
                    .build();

    /**
     * 暗影尖啸幽灵0实体注册结果
     * 更强的飞行射弹敌对生物，20血，免疫火焰
     * 尺寸: 0.8f x 1.3f
     */
    private static final EntityResult<ShadowSquealGhost0Entity> SHADOW_SQUEAL_GHOST_0_RESULT =
            EntityAPI.createEntity("shadow_squeal_ghost_0")
                    .category(MobCategory.MONSTER)
                    .size(0.8f, 1.3f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(ShadowSquealGhost0Entity.class)
                    .attributes(ShadowSquealGhost0Entity::createAttributes)
                    .spawnEgg(0xF0DF77, 0x6A064C)
                    .build();

    /**
     * 暗影之手实体注册结果
     * 飞行近战敌对生物，12血，免疫火焰
     * 尺寸: 0.6f x 0.8f
     */
    private static final EntityResult<ShadowHandEntity> SHADOW_HAND_RESULT =
            EntityAPI.createEntity("shadow_hand")
                    .category(MobCategory.MONSTER)
                    .size(0.6f, 0.8f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(ShadowHandEntity.class)
                    .attributes(ShadowHandEntity::createAttributes)
                    .spawnEgg(0x3D3D3D, 0x070707)
                    .build();

    /**
     * 雷云实体注册结果
     * 飞行被动/中立生物，30血，免疫摔落/闪电
     * 尺寸: 1.0f x 0.8f
     */
    private static final EntityResult<ThundercloudEntity> THUNDERCLOUD_RESULT =
            EntityAPI.createEntity("thundercloud")
                    .category(MobCategory.MONSTER)
                    .size(1.0f, 0.8f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(ThundercloudEntity.class)
                    .attributes(ThundercloudEntity::createAttributes)
                    .spawnEgg(0x838D9A, 0x24489A)
                    .build();

    /**
     * 高压雷云实体注册结果
     * 飞行敌对生物，50血，免疫火焰/摔落/闪电
     * 尺寸: 1.4f x 1.1f
     */
    private static final EntityResult<HighvoltageThundercloudEntity> HIGHVOLTAGE_RESULT =
            EntityAPI.createEntity("highvoltage")
                    .category(MobCategory.MONSTER)
                    .size(1.4f, 1.1f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(HighvoltageThundercloudEntity.class)
                    .attributes(HighvoltageThundercloudEntity::createAttributes)
                    .spawnEgg(0x82BB9A, 0x2525A8)
                    .build();

    /**
     * 风之骑士实体注册结果
     * 大型地面BOSS级敌对生物，150血，免疫火焰
     * 尺寸: 1.0f x 2.0f
     */
    private static final EntityResult<WindKnightEntity> WIND_KNIGHT_RESULT =
            EntityAPI.createEntity("wind_knight")
                    .category(MobCategory.MONSTER)
                    .size(1.0f, 2.0f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(WindKnightEntity.class)
                    .attributes(WindKnightEntity::createAttributes)
                    .spawnEgg(0xE2E5E4, 0x6CDFB6)
                    .build();

    /**
     * 震动水晶实体注册结果
     * 静止敌对生物，50血，免疫火焰
     * 尺寸: 0.6f x 1.8f
     */
    private static final EntityResult<ShakingCrystalEntity> SHAKING_CRYSTAL_RESULT =
            EntityAPI.createEntity("shaking_crystal")
                    .category(MobCategory.MONSTER)
                    .size(0.6f, 1.8f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(ShakingCrystalEntity.class)
                    .attributes(ShakingCrystalEntity::createAttributes)
                    .spawnEgg(0x33A9B8, 0x4A48AE)
                    .build();

    /**
     * 暗影调和图腾实体注册结果
     * 静止大型敌对生物，40血，免疫火焰
     * 尺寸: 2.0f x 8.0f
     */
    private static final EntityResult<ShadowTuneTotemEntity> SHADOW_TUNE_TOTEM_RESULT =
            EntityAPI.createEntity("shadow_tune_totem")
                    .category(MobCategory.MONSTER)
                    .size(2.0f, 8.0f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(ShadowTuneTotemEntity.class)
                    .attributes(ShadowTuneTotemEntity::createAttributes)
                    .spawnEgg(0x2828D1, 0xBBBBBB)
                    .build();

    /**
     * 小石灵实体注册结果
     * 地面敌对生物，10血4甲
     * 尺寸: 0.7f x 1.0f
     */
    private static final EntityResult<SmallStoneSpiritEntity> SMALL_STONE_SPIRIT_RESULT =
            EntityAPI.createEntity("small_stone_spirit")
                    .category(MobCategory.MONSTER)
                    .size(0.7f, 1.0f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(SmallStoneSpiritEntity.class)
                    .attributes(SmallStoneSpiritEntity::createAttributes)
                    .spawnEgg(0xC3EAE8, 0x717A7A)
                    .build();

    /**
     * 黑甲虫实体注册结果
     * 地面近战敌对生物，12血5甲
     * 尺寸: 0.6f x 0.5f
     */
    private static final EntityResult<BlackBeetleEntity> BLACK_BEETLE_RESULT =
            EntityAPI.createEntity("black_beetle")
                    .category(MobCategory.MONSTER)
                    .size(0.6f, 0.5f)
                    .trackingRange(48)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(BlackBeetleEntity.class)
                    .attributes(BlackBeetleEntity::createAttributes)
                    .spawnEgg(0xD6D2A9, 0x53514E)
                    .build();

    /**
     * 黑甲虫母体实体注册结果
     * 大型地面BOSS级敌对生物，100血10甲
     * 尺寸: 2.0f x 1.0f
     */
    private static final EntityResult<BlackBeetleMotherEntity> BLACK_BEETLE_MOTHER_RESULT =
            EntityAPI.createEntity("black_beetle_mother")
                    .category(MobCategory.MONSTER)
                    .size(2.0f, 1.0f)
                    .trackingRange(48)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(BlackBeetleMotherEntity.class)
                    .attributes(BlackBeetleMotherEntity::createAttributes)
                    .spawnEgg(0xDAD6EE, 0x696252)
                    .build();

    /**
     * 玄武岩蜗牛实体注册结果
     * 染梦世界游荡的中性 GeckoLib 生物，受攻击时缩壳获得抗性
     * 尺寸: 0.8f x 0.6f
     */
    private static final EntityResult<BasaltSnailEntity> BASALT_SNAIL_RESULT =
            EntityAPI.createEntity("basalt_snail")
                    .category(MobCategory.CREATURE)
                    .size(0.8f, 0.6f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(BasaltSnailEntity.class)
                    .attributes(BasaltSnailEntity::createAttributes)
                    .spawnEgg(0x3D3D3D, 0x8B7355)
                    .build();

    /**
     * 狐火实体注册结果
     * 染梦世界中飘浮的火焰精灵，setNoAi(true) 纯环境效果
     * 尺寸: 0.6f x 0.6f
     */
    private static final EntityResult<FoxFireEntity> FOX_FIRE_RESULT =
            EntityAPI.createEntity("fox_fire")
                    .category(MobCategory.MONSTER)
                    .size(0.6f, 0.6f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(FoxFireEntity.class)
                    .attributes(FoxFireEntity::createAttributes)
                    .spawnEgg(0xFF4500, 0xFF6347)
                    .build();

    /**
     * 暗影 ??? NPC 实体注册结果
     * 暗影地牢中徘徊的神秘 GeckoLib NPC，右键交互对话
     * 尺寸: 0.6f x 1.8f
     */
    private static final EntityResult<ShadowNpc0Entity> SHADOW_NPC_0_RESULT =
            EntityAPI.createEntity("shadow_npc_0")
                    .category(MobCategory.CREATURE)
                    .size(0.6f, 1.8f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(ShadowNpc0Entity.class)
                    .attributes(ShadowNpc0Entity::createAttributes)
                    .spawnEgg(0x1A1A2E, 0x4A4A6A)
                    .build();

    /**
     * 孢子实体注册结果
     * 染梦世界飞行的丛林孢子，不使用 GeckoLib（SpiderModel）
     * 尺寸: 0.8f x 0.8f
     */
    private static final EntityResult<SporeEntityEntity> SPORE_ENTITY_RESULT =
            EntityAPI.createEntity("spore_entity")
                    .category(MobCategory.MONSTER)
                    .size(0.8f, 0.8f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(SporeEntityEntity.class)
                    .attributes(SporeEntityEntity::createAttributes)
                    .spawnEgg(0x2E8B57, 0x556B2F)
                    .build();

    // ======================== 向后兼容常量 ========================

    /**
     * 暗影魔像实体 (shadow_golem) — 向后兼容引用
     */
    public static final Supplier<EntityType<ShadowGolemEntity>> SHADOW_GOLEM =
            SHADOW_GOLEM_RESULT.entityTypeSupplier();

    /**
     * 粉色史莱姆实体 (pink_slime) — 向后兼容引用
     */
    public static final Supplier<EntityType<PinkSlimeEntity>> PINK_SLIME =
            PINK_SLIME_RESULT.entityTypeSupplier();

    /**
     * 粉色鸡实体 (pink_chicken) — 向后兼容引用
     */
    public static final Supplier<EntityType<PinkChickenEntity>> PINK_CHICKEN =
            PINK_CHICKEN_RESULT.entityTypeSupplier();

    /**
     * 水母实体 (jellyfish) — 向后兼容引用
     */
    public static final Supplier<EntityType<JellyfishEntity>> JELLYFISH =
            JELLYFISH_RESULT.entityTypeSupplier();

    /**
     * 友好幽灵实体 (friendly_ghost) — 向后兼容引用
     */
    public static final Supplier<EntityType<FriendlyGhostEntity>> FRIENDLY_GHOST =
            FRIENDLY_GHOST_RESULT.entityTypeSupplier();

    /**
     * 萤火虫实体 (firefly) — 向后兼容引用
     */
    public static final Supplier<EntityType<FireflyEntity>> FIREFLY =
            FIREFLY_RESULT.entityTypeSupplier();

    /**
     * 金色狐狸实体 (golden_fox) — 向后兼容引用
     */
    public static final Supplier<EntityType<GoldenFoxEntity>> GOLDEN_FOX =
            GOLDEN_FOX_RESULT.entityTypeSupplier();

    /**
     * 融梦水晶实体 (meltdream_crystal) — 向后兼容引用
     */
    public static final Supplier<EntityType<MeltdreamCrystalEntity>> MELTDREAM_CRYSTAL =
            MELTDREAM_CRYSTAL_RESULT.entityTypeSupplier();

    /**
     * 恐怖尖喙实体 (terrorbeak) — 向后兼容引用
     */
    public static final Supplier<EntityType<TerrorbeakEntity>> TERRORBEAK =
            TERRORBEAK_RESULT.entityTypeSupplier();

    /**
     * 疯狂恐怖尖喙实体 (crazy_terrorbeak) — 向后兼容引用
     */
    public static final Supplier<EntityType<CrazyTerrorbeakEntity>> CRAZY_TERRORBEAK =
            CRAZY_TERRORBEAK_RESULT.entityTypeSupplier();

    /**
     * 虚弱恐怖尖喙实体 (weakeness_terrorbeak) — 向后兼容引用
     */
    public static final Supplier<EntityType<WeakenessTerrorbeakEntity>> WEAKENESS_TERRORBEAK =
            WEAKENESS_TERRORBEAK_RESULT.entityTypeSupplier();

    /**
     * 骨翼实体 (bone_wing) — 向后兼容引用
     */
    public static final Supplier<EntityType<BoneWingEntity>> BONE_WING =
            BONE_WING_RESULT.entityTypeSupplier();

    /**
     * 灰烬骨翼实体 (ash_bone_wing) — 向后兼容引用
     */
    public static final Supplier<EntityType<AshBoneWingEntity>> ASH_BONE_WING =
            ASH_BONE_WING_RESULT.entityTypeSupplier();

    // ======================== 阴影系列 ========================

    /**
     * 暗影幽灵实体 (shadow_ghost) — 向后兼容引用
     */
    public static final Supplier<EntityType<ShadowGhostEntity>> SHADOW_GHOST =
            SHADOW_GHOST_RESULT.entityTypeSupplier();

    /**
     * 暗影尖啸幽灵实体 (shadow_squeal_ghost) — 向后兼容引用
     */
    public static final Supplier<EntityType<ShadowSquealGhostEntity>> SHADOW_SQUEAL_GHOST =
            SHADOW_SQUEAL_GHOST_RESULT.entityTypeSupplier();

    /**
     * 暗影尖啸幽灵0实体 (shadow_squeal_ghost_0) — 向后兼容引用
     */
    public static final Supplier<EntityType<ShadowSquealGhost0Entity>> SHADOW_SQUEAL_GHOST_0 =
            SHADOW_SQUEAL_GHOST_0_RESULT.entityTypeSupplier();

    /**
     * 暗影之手实体 (shadow_hand) — 向后兼容引用
     */
    public static final Supplier<EntityType<ShadowHandEntity>> SHADOW_HAND =
            SHADOW_HAND_RESULT.entityTypeSupplier();

    // ======================== 雷云系列 ========================

    /**
     * 雷云实体 (thundercloud) — 向后兼容引用
     */
    public static final Supplier<EntityType<ThundercloudEntity>> THUNDERCLOUD =
            THUNDERCLOUD_RESULT.entityTypeSupplier();

    /**
     * 高压雷云实体 (highvoltage) — 向后兼容引用
     */
    public static final Supplier<EntityType<HighvoltageThundercloudEntity>> HIGHVOLTAGE =
            HIGHVOLTAGE_RESULT.entityTypeSupplier();

    // ======================== 其他敌对生物 ========================

    /**
     * 风之骑士实体 (wind_knight) — 向后兼容引用
     */
    public static final Supplier<EntityType<WindKnightEntity>> WIND_KNIGHT =
            WIND_KNIGHT_RESULT.entityTypeSupplier();

    /**
     * 震动水晶实体 (shaking_crystal) — 向后兼容引用
     */
    public static final Supplier<EntityType<ShakingCrystalEntity>> SHAKING_CRYSTAL =
            SHAKING_CRYSTAL_RESULT.entityTypeSupplier();

    /**
     * 暗影调和图腾实体 (shadow_tune_totem) — 向后兼容引用
     */
    public static final Supplier<EntityType<ShadowTuneTotemEntity>> SHADOW_TUNE_TOTEM =
            SHADOW_TUNE_TOTEM_RESULT.entityTypeSupplier();

    /**
     * 小石灵实体 (small_stone_spirit) — 向后兼容引用
     */
    public static final Supplier<EntityType<SmallStoneSpiritEntity>> SMALL_STONE_SPIRIT =
            SMALL_STONE_SPIRIT_RESULT.entityTypeSupplier();

    /**
     * 黑甲虫实体 (black_beetle) — 向后兼容引用
     */
    public static final Supplier<EntityType<BlackBeetleEntity>> BLACK_BEETLE =
            BLACK_BEETLE_RESULT.entityTypeSupplier();

    /**
     * 黑甲虫母体实体 (black_beetle_mother) — 向后兼容引用
     */
    public static final Supplier<EntityType<BlackBeetleMotherEntity>> BLACK_BEETLE_MOTHER =
            BLACK_BEETLE_MOTHER_RESULT.entityTypeSupplier();

    // ==================== 染梦新生物 ====================

    /**
     * 玄武岩蜗牛实体 (basalt_snail) — 向后兼容引用
     */
    public static final Supplier<EntityType<BasaltSnailEntity>> BASALT_SNAIL =
            BASALT_SNAIL_RESULT.entityTypeSupplier();

    /**
     * 狐火实体 (fox_fire) — 向后兼容引用
     */
    public static final Supplier<EntityType<FoxFireEntity>> FOX_FIRE =
            FOX_FIRE_RESULT.entityTypeSupplier();

    /**
     * 暗影 ??? NPC 实体 (shadow_npc_0) — 向后兼容引用
     */
    public static final Supplier<EntityType<ShadowNpc0Entity>> SHADOW_NPC_0 =
            SHADOW_NPC_0_RESULT.entityTypeSupplier();

    /**
     * 孢子实体 (spore_entity) — 向后兼容引用
     */
    public static final Supplier<EntityType<SporeEntityEntity>> SPORE_ENTITY =
            SPORE_ENTITY_RESULT.entityTypeSupplier();

    // ==================== 弹射物实体注册 ====================

    /**
     * 骨翼火球弹射物 (bone_wing_fire_ball_projectile)
     * 继承 AbstractArrow，由骨翼/灰烬骨翼通过 performRangedAttack 发射。
     */
    public static final Supplier<EntityType<BoneWingFireBallProjectileEntity>> BONE_WING_FIRE_BALL_PROJECTILE =
            ENTITY_TYPES.register("bone_wing_fire_ball_projectile",
                    () -> EntityType.Builder.<BoneWingFireBallProjectileEntity>of(BoneWingFireBallProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("bone_wing_fire_ball_projectile"));
}
