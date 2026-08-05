package com.pasterdream.pasterdreammod.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 方块配置 —— 在 BlockAPI Builder 中链式设置方块的纹理、模型、挖掘标签、交互等属性
 * <p>
 * 使用示例：
 * <pre>{@code
 * BlockAPI.registerSimpleBlocks()
 *     .add("dyedream_log", Blocks.OAK_LOG, BlockConfig.of()
 *         .mineable("axe")
 *         .model("cube_column")
 *         .tex("end", "pasterdream:block/dyedream_log_top")
 *         .tex("side", "pasterdream:block/dyedream_log_side")
 *     )
 *     .build();
 * }</pre>
 */
public class BlockConfig {

    @Nullable
    String mineable;

    @Nullable
    String renderType;

    @Nullable
    String model;

    @Nullable
    Map<String, String> textures;

    @Nullable
    InteractionHandler interaction;

    @Nullable
    String animationFile;

    @Nullable
    BlockFactory blockFactory;

    /** 是否可种植标记 —— 为 true 时自动加入 pasterdream:plantable_on 标签 */
    boolean plantableOn;

    /** 方块染色类型（客户端 tint 渲染），默认 NONE 不染色 */
    TintType tint = TintType.NONE;

    /** 固定染色值（ARGB，含 alpha 位），仅当 tint == FIXED 时使用 */
    int fixedTint = 0xFF92557F;

    BlockConfig() {
    }

    public static BlockConfig of() {
        return new BlockConfig();
    }

    /** @return 挖掘工具类型，如 "axe"/"pickaxe"/"shovel"/"hoe" */
    @Nullable
    public String getMineable() { return mineable; }

    /** @return 方块染色类型 */
    public TintType getTint() { return tint; }

    /** @return 固定染色值（ARGB），仅当 tint == FIXED 时有效 */
    public int getFixedTint() { return fixedTint; }

    /** @return 是否可种植（自动加入 plantable_on 标签） */
    public boolean isPlantable() { return plantableOn; }

    /** @return 渲染类型，如 "translucent"/"cutout"，可为 null（默认 solid） */
    @Nullable
    public String getRenderType() { return renderType; }

    /** @return 模型标识，如 "cube_all"/"cube_column" */
    @Nullable
    public String getModel() { return model; }

    /** @return 纹理层映射（层名 → 纹理路径） */
    @Nullable
    public Map<String, String> getTextures() { return textures; }

    /** @return 右键交互回调，可为 null */
    @Nullable
    public InteractionHandler getInteraction() { return interaction; }

    /** @return GeckoLib 动画文件路径，可为 null */
    @Nullable
    public String getAnimationFile() { return animationFile; }

    /**
     * @return 自定义 Block 构造工厂，为 null 时默认使用 {@link
     *         com.pasterdream.pasterdreammod.block.SelfDropBlock}
     */
    @Nullable
    public BlockFactory getBlockFactory() { return blockFactory; }

    /**
     * 设置挖掘工具类型
     *
     * @param tool 工具类型（"axe"/"pickaxe"/"shovel"/"hoe"）
     * @return 当前配置实例
     */
    public BlockConfig mineable(String tool) {
        this.mineable = tool;
        return this;
    }

    /**
     * 设置渲染类型（用于玻璃、冰等需要透明渲染的方块）
     *
     * @param type 渲染类型，如 "translucent"/"cutout"/"cutout_mipped"
     * @return 当前配置实例
     */
    public BlockConfig renderType(String type) {
        this.renderType = type;
        return this;
    }

    /**
     * 设置模型类型
     *
     * @param modelId 模型标识（如 "cube_all"、"cube_column" 等）
     * @return 当前配置实例
     */
    public BlockConfig model(String modelId) {
        this.model = modelId;
        return this;
    }

    /**
     * 设置纹理层
     *
     * @param layer 纹理层名（如 "all"、"top"、"side"、"end" 等）
     * @param path  纹理路径（如 "pasterdream:block/dyedream_log_top"）
     * @return 当前配置实例
     */
    public BlockConfig tex(String layer, String path) {
        if (this.textures == null) {
            this.textures = new HashMap<>();
        }
        this.textures.put(layer, path);
        return this;
    }

    /**
     * 设置右键交互回调
     *
     * @param handler 交互处理函数
     * @return 当前配置实例
     */
    public BlockConfig interact(InteractionHandler handler) {
        this.interaction = handler;
        return this;
    }

    /**
     * 设置 GeckoLib 动画文件
     *
     * @param geoFile 动画 geo 文件路径（如 "geo/magic_block.geo.json"）
     * @return 当前配置实例
     */
    public BlockConfig animated(String geoFile) {
        this.animationFile = geoFile;
        return this;
    }

    /**
     * 设置自定义 Block 构造工厂（覆盖默认的 {@code SelfDropBlock::new}）
     * <p>
     * 适用于需要特殊行为（如 {@link net.minecraft.world.level.block.GlassBlock}）的方块。
     *
     * @param factory 接收 {@link BlockBehaviour.Properties} 返回 {@link Block} 的函数
     * @return 当前配置实例
     */
    public BlockConfig blockFactory(BlockFactory factory) {
        this.blockFactory = factory;
        return this;
    }

    /**
     * 标记该方块为可种植地面，自动加入 {@code pasterdream:plantable_on} 标签。
     * <p>
     * 花草/树苗/作物将能种植在此方块上。
     *
     * @return 当前配置实例
     */
    public BlockConfig plantable() {
        this.plantableOn = true;
        return this;
    }

    /**
     * 设置方块染色类型为「跟随群系 foliage 颜色」（原版树叶/藤蔓机制）。
     * <p>
     * 客户端渲染时通过 {@link net.minecraft.client.renderer.BiomeColors#getAverageFoliageColor}
     * 取群系 {@code foliage_color}（数据驱动，兼容 Sodium/Iris）。
     * 需配合模型 element 的 {@code tintindex}（如 parent 使用 {@code minecraft:block/leaves}）。
     *
     * @return 当前配置实例
     */
    public BlockConfig tintFoliage() {
        this.tint = TintType.FOLIAGE;
        return this;
    }

    /**
     * 设置方块染色类型为「跟随群系草地颜色」。
     *
     * @return 当前配置实例
     */
    public BlockConfig tintGrass() {
        this.tint = TintType.GRASS;
        return this;
    }

    /**
     * 设置方块染色类型为「固定颜色」（不随群系变化）。
     *
     * @param argb 固定染色值（ARGB 格式，须含 alpha 位，如 0xFF92557F）
     * @return 当前配置实例
     */
    public BlockConfig tintFixed(int argb) {
        this.tint = TintType.FIXED;
        this.fixedTint = argb;
        return this;
    }

    /**
     * 方块染色类型枚举
     */
    public enum TintType {
        /** 不染色（默认） */
        NONE,
        /** 跟随群系 foliage 颜色（树叶/藤蔓） */
        FOLIAGE,
        /** 跟随群系草地颜色 */
        GRASS,
        /** 固定颜色 */
        FIXED
    }

    /**
     * 右键交互回调接口
     */
    @FunctionalInterface
    public interface InteractionHandler {
        /**
         * 处理方块右键交互
         *
         * @param level  世界
         * @param pos    方块位置
         * @param player 交互的玩家
         * @param hand   交互的手
         */
        void interact(Level level, BlockPos pos, Player player, InteractionHand hand);
    }

    /**
     * Block 构造工厂 —— {@code (BlockBehaviour.Properties) -> Block}
     */
    @FunctionalInterface
    public interface BlockFactory {
        Block create(BlockBehaviour.Properties properties);
    }
}