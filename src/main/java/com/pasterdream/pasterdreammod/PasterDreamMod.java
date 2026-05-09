package com.pasterdream.pasterdreammod;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDEntityEvents;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDStructures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PasterDream 模组主类
 * 负责模组的初始化和事件总线管理
 */
@Mod(PasterDreamMod.MOD_ID)
public class PasterDreamMod {

    /**
     * 模组 ID 常量
     */
    public static final String MOD_ID = "pasterdream";

    /**
     * 模组日志记录器
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(PasterDreamMod.class);

    /**
     * 构造函数
     *
     * @param modEventBus NeoForge 事件总线
     * @param modContainer NeoForge 模组容器
     */
    public PasterDreamMod(IEventBus modEventBus, ModContainer modContainer) {
        // 注册方块
        PDBlocks.BLOCKS.register(modEventBus);

        // 注册物品
        PDItems.ITEMS.register(modEventBus);

        // 注册方块实体
        PDBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        // 注册实体类型
        PDEntities.ENTITY_TYPES.register(modEventBus);

        // 注册创造模式物品栏
        PDCreativeTabs.TABS.register(modEventBus);

        // 注册状态效果（BUFF/DEBUFF）
        PDEffects.MOB_EFFECTS.register(modEventBus);

        // 注册维度类型
        PDDimensions.DIMENSION_TYPES.register(modEventBus);

        // 注册维度实例
        PDDimensions.LEVEL_STEMS.register(modEventBus);

        // 注册结构类型
        PDStructures.STRUCTURE_TYPES.register(modEventBus);

        // 注册菜单类型
        PDMenus.MENUS.register(modEventBus);

        // 注册粒子类型
        PDParticles.PARTICLE_TYPES.register(modEventBus);

        // 监听通用设置事件
        modEventBus.addListener(this::commonSetup);
    }

    /**
     * 通用设置阶段初始化
     *
     * @param event FML 通用设置事件
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        // 初始化逻辑放在这里
    }
}
