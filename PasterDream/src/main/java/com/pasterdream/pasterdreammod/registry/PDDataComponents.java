package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 数据组件 (DataComponentType) 注册类
 *
 * 用于注册梦境炼药锅等系统使用的物品级数据组件。
 * 挂载方式：主类构造器中 {@code PDDataComponents.DATA_COMPONENTS.register(modEventBus)}。
 */
public class PDDataComponents {

    /**
     * 数据组件注册器
     */
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, PasterDreamMod.MOD_ID);

    /**
     * 梦之强化标记 (dream_enhanced)
     *
     * 由梦境炼药锅酿造的药水携带此组件，标识其经过梦之强化
     * （持续型效果时长增益、即时型效果等级增益，见 DreamPotionEnhancer）。
     * 值恒为 true，仅作为存在性标记使用。
     *
     * @see com.pasterdream.pasterdreammod.util.DreamPotionEnhancer
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DREAM_ENHANCED =
            DATA_COMPONENTS.register("dream_enhanced",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build());
}
