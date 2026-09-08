package com.pasterdream.pasterdreammod.mixin;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * StructureTemplate 访问器 Mixin —— 暴露内部调色板列表
 * <p>
 * 原版 {@link StructureTemplate#filterBlocks} 系列仅支持单一 {@code Block}
 * 精确过滤，且该过滤键会被 NeoForge 线程安全补丁（Fixes MC-271899）用作
 * {@code Palette.cache}（ConcurrentHashMap）的缓存键，传入 null 必然抛出
 * NullPointerException；而公开 API 又没有「无过滤取全部方块」的路径
 * （{@code palettes} 为 private 且无访问器）。
 * <p>
 * 结构树 Feature（DyedreamStructureTreeFeature）需要遍历模板全部方块做
 * 底部支撑检测，通过本 Accessor 拿到 palettes 后，调用
 * {@code StructurePlaceSettings#getRandomPalette(palettes, pos).blocks()}
 * 即可取得与 placeInWorld 完全一致的方块列表。
 */
@Mixin(StructureTemplate.class)
public interface StructureTemplateAccessor {

    /**
     * 获取模板的调色板列表（每个调色板持有模板全量方块信息）
     *
     * @return palettes 列表，普通结构 NBT 恒为单元素
     */
    @Accessor("palettes")
    List<StructureTemplate.Palette> getPalettes();
}
