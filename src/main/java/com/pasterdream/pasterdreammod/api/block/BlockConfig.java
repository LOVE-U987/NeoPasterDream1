package com.pasterdream.pasterdreammod.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
    String model;

    @Nullable
    Map<String, String> textures;

    @Nullable
    InteractionHandler interaction;

    @Nullable
    String animationFile;

    BlockConfig() {
    }

    public static BlockConfig of() {
        return new BlockConfig();
    }

    /** @return 挖掘工具类型，如 "axe"/"pickaxe"/"shovel"/"hoe" */
    @Nullable
    public String getMineable() { return mineable; }

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
}