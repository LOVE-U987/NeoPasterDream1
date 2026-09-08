package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.worldgen.structure.placement.DyedreamCrackPlacement;
import com.pasterdream.pasterdreammod.worldgen.structure.placement.OriginFixedPlacement;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 结构放置策略类型注册类 —— 注册自定义 {@link StructurePlacementType}。
 * <p>
 * 提供两种策略：
 * <ul>
 *   <li>{@code dyedream_crack_spread} —— 染梦裂隙随机放置（受 {@code DYEDREAM_CRACK_GENERATE} 控制）</li>
 *   <li>{@code origin_fixed} —— 原点固定放置（不受配置控制，保证指定区块必定生成）</li>
 * </ul>
 * 必须在 {@link com.pasterdream.pasterdreammod.PasterDreamMod} 构造器中调用
 * {@link DeferredRegister#register} 完成注册。
 */
public final class PDStructurePlacements {

    /** StructurePlacementType 的 DeferredRegister */
    public static final DeferredRegister<StructurePlacementType<?>> PLACEMENT_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, PasterDreamMod.MOD_ID);

    /** 染梦裂隙放置策略（受配置 DYEDREAM_CRACK_GENERATE 控制） */
    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<DyedreamCrackPlacement>> DYEDREAM_CRACK_SPREAD =
            PLACEMENT_TYPES.register("dyedream_crack_spread", () -> () -> DyedreamCrackPlacement.CODEC);

    /** 原点固定放置策略（保证染梦维度 (0,0) 必定生成裂隙结构） */
    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<OriginFixedPlacement>> ORIGIN_FIXED =
            PLACEMENT_TYPES.register("origin_fixed", () -> () -> OriginFixedPlacement.CODEC);

    private PDStructurePlacements() {
    }
}
