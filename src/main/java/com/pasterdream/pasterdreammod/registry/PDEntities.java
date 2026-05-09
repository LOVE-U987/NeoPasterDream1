package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.PinkSlimeEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowGolemEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 实体注册类
 * 使用 DeferredRegister 模式注册所有实体
 */
public class PDEntities {

    /**
     * 实体类型注册器
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            BuiltInRegistries.ENTITY_TYPE, PasterDreamMod.MOD_ID);

    /**
     * 暗影魔像实体 (shadow_golem)
     * 大型暗影主题怪物，使用 GeckoLib 动画
     * 尺寸: 2.2f x 3.5f
     */
    public static final DeferredHolder<EntityType<?>, EntityType<ShadowGolemEntity>> SHADOW_GOLEM =
            ENTITY_TYPES.register("shadow_golem",
                    () -> EntityType.Builder.<ShadowGolemEntity>of(ShadowGolemEntity::new, MobCategory.MONSTER)
                            .sized(2.2f, 3.5f)
                            .setTrackingRange(64)
                            .setUpdateInterval(3)
                            .setShouldReceiveVelocityUpdates(true)
                            .build("shadow_golem"));

    /**
     * 粉色史莱姆实体 (pink_slime)
     * 友好的粉色史莱姆生物，使用原版模型
     * 尺寸: 0.5f x 0.5f
     */
    public static final DeferredHolder<EntityType<?>, EntityType<PinkSlimeEntity>> PINK_SLIME =
            ENTITY_TYPES.register("pink_slime",
                    () -> EntityType.Builder.<PinkSlimeEntity>of(PinkSlimeEntity::new, MobCategory.CREATURE)
                            .sized(0.5f, 0.5f)
                            .setTrackingRange(64)
                            .setUpdateInterval(3)
                            .setShouldReceiveVelocityUpdates(true)
                            .build("pink_slime"));
}
