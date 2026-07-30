package com.pasterdream.pasterdreammod.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.entity.tag.EntityTag;
import com.pasterdream.pasterdreammod.api.entity.tag.EntityTagRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 实体标签内置行为事件处理类
 * <p>
 * 订阅游戏总线事件，根据 {@link EntityTagRegistry} 中绑定的标签自动执行相应逻辑：
 * <ul>
 *   <li>{@link EntityTag#SPELL_INVINCIBLE}：实体加入世界时自动设置为无敌</li>
 *   <li>{@link EntityTag#LAMP_SHADOW_MONSTER}：同标签怪物之间互相攻击时伤害归零，避免友伤</li>
 * </ul>
 *
 * @see EntityTagSetup
 * @see EntityTag
 * @see EntityTagRegistry
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class EntityTagEvents {

    private EntityTagEvents() {
        throw new UnsupportedOperationException("EntityTagEvents 是事件订阅类，不可实例化");
    }

    /**
     * 处理法术无敌标签
     * <p>
     * 当带有 {@link EntityTag#SPELL_INVINCIBLE} 标签的实体加入世界时，
     * 调用 {@link Entity#setInvulnerable(boolean)} 使其免疫所有伤害。
     * 该标签通常用于治疗立场、狂暴立场等展示性法术实体。
     *
     * @param event 实体加入世界事件
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) {
            return;
        }
        if (EntityTagRegistry.hasTag(entity, EntityTag.SPELL_INVINCIBLE)) {
            entity.setInvulnerable(true);
            PDDebugLogger.mainDebug("[EntityTagEvents] 实体 [{}] 已设置为无敌（spell_invincible）",
                    EntityType.getKey(entity.getType()));
        }
    }

    /**
     * 处理灯影怪物友伤免疫
     * <p>
     * 当伤害来源实体与受伤实体均带有 {@link EntityTag#LAMP_SHADOW_MONSTER} 标签时，
     * 将本次伤害归零，防止暗影系列怪物互相内耗。
     * 使用 {@link LivingDamageEvent.Pre} 可在护甲/效果结算后、最终扣血前改写伤害。
     *
     * @param event 生物伤害前置事件
     */
    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        Entity attacker = event.getSource().getEntity();
        Entity target = event.getEntity();
        if (attacker == null || target == null) {
            return;
        }
        if (EntityTagRegistry.hasTag(attacker, EntityTag.LAMP_SHADOW_MONSTER)
                && EntityTagRegistry.hasTag(target, EntityTag.LAMP_SHADOW_MONSTER)) {
            event.setNewDamage(0.0F);
            PDDebugLogger.mainDebug("[EntityTagEvents] 灯影怪物友伤已抵消: {} -> {}",
                    EntityType.getKey(attacker.getType()), EntityType.getKey(target.getType()));
        }
    }
}
