package com.pasterdream.pasterdreammod.pasterdreamspells.event;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import com.pasterdream.pasterdreammod.pasterdreamspells.PasterDreamSpellsMod;
import com.pasterdream.pasterdreammod.pasterdreamspells.entity.mob.HealingSpellFieldEntity;
import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/**
 * PasterDreamSpells 实体事件处理。
 * <p>
 * 注册法术实体的属性创建（如治疗立场的属性）。
 *
 * @author PasterDream
 */
@EventBusSubscriber(modid = PasterDreamSpellsMod.MOD_ID)
public class PDSpellsEntityEvents {

    /**
     * 注册实体默认属性。
     *
     * @param event 实体属性创建事件
     */
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(PDSpellsEntities.HEALING_SPELL_ENTITY.get(), HealingSpellFieldEntity.createAttributes().build());
        PDDebugLogger.mainDebug("[PDSpellsEntityEvents] 治疗法术立场实体属性已注册");
    }
}
