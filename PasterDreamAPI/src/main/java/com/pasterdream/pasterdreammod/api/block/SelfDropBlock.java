package com.pasterdream.pasterdreammod.api.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;

/**
 * 基础方块类 —— 所有通过 API 批量注册的方块均使用此类
 * <p>
 * 掉落逻辑采用混合策略：
 * <ol>
 *   <li><b>优先使用战利品表系统</b>（适用于已配置战利品表的矿石等方块）。
 *       只要方块配置了战利品表（key 非 {@link BuiltInLootTables#EMPTY} 且实际加载到
 *       非空 {@link LootTable}），就完全交给战利品表处理，保留"需要正确工具 /
 *       条件不满足无掉落"的语义，避免空结果被错误兜底成掉落本体。</li>
 *   <li><b>未配置战利品表时</b>（纯装饰方块）回退为掉落方块自身。</li>
 * </ol>
 */
public class SelfDropBlock extends Block {

    /**
     * @param properties 方块属性
     */
    public SelfDropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        // 关键修复：仅当方块「没有实际战利品表」时才兜底掉落自身。
        // 原先"战利品表结果为空就兜底"会在矿石被正确工具之外的逻辑拦截
        // （match_tool 不满足、requiresCorrectToolForDrops 空掉落等）时错误掉出本体。
        ResourceKey<LootTable> key = this.getLootTable();
        if (key != BuiltInLootTables.EMPTY) {
            ServerLevel level = params.getLevel();
            if (level != null && level.getServer() != null
                    && level.getServer().reloadableRegistries().getLootTable(key) != LootTable.EMPTY) {
                return super.getDrops(state, params);
            }
        }
        // 未配置战利品表（纯装饰方块）→ 回退掉落自身
        return List.of(new ItemStack(this));
    }
}
