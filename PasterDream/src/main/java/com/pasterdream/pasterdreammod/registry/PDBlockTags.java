package com.pasterdream.pasterdreammod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * PasterDream 方块标签常量。
 * <p>
 * 所有与装饰物、地形相关的自定义方块标签统一在此声明，
 * 避免各业务类中重复定义 TagKey 常量。
 */
public final class PDBlockTags {

    private PDBlockTags() {}

    /**
     * 可种植地面标签 —— 染梦草、染梦泥土、染梦沙、染梦合金块、苔苍青岩等
     * 模组内的花草/树苗/作物统一检查此标签，而非硬编码具体方块。
     */
    public static final TagKey<Block> PLANTABLE_ON = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("pasterdream", "plantable_on")
    );

    /**
     * 灯笼标签 —— 社区约定标签 {@code c:lanterns}（与 Fabric Convention Tags 兼容），
     * 供其他模组识别灯笼类方块（如挂载、照明联动等逻辑）。
     * 包含染梦灯笼、染梦水晶灯及原版灯笼。
     */
    public static final TagKey<Block> LANTERNS = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("c", "lanterns")
    );
}
