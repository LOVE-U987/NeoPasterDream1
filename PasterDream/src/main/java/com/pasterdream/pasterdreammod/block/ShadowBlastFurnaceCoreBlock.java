package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

/**
 * 暗影高炉核心方块 (Shadow Blast Furnace Core)
 * <p>
 * 多方块结构核心：完成 3×3×3 结构（雕纹暗影石砖基座 + 暗影石瓦四角 +
 * 暗影石砖台阶十字 + 暗影石瓦墙 / 锈蚀黑金属墙立柱 + 顶部锈蚀黑金属块）后，
 * 手持蓝图 blueprint_0 右键核心 → 整组结构消融并在核心下方生成暗影高炉
 * （原版 ShadowBlastFurnaceCorePr0Procedure）。
 * 悬浮提示与原版一致，提示搭建方式。
 */
public class ShadowBlastFurnaceCoreBlock extends Block {

    /**
     * 相对核心的结构校验表：{dx, dy, dz} → 对应方块的注册引用下标
     * （0 雕纹暗影石砖 / 1 暗影石瓦 / 2 暗影石砖台阶 / 3 暗影石瓦墙 /
     * 4 锈蚀黑金属块墙 / 5 锈蚀黑金属块）
     */
    private static final int[][] STRUCTURE = {
            {0, -1, 0, 0},
            {1, -1, 1, 1}, {1, -1, -1, 1}, {-1, -1, -1, 1}, {-1, -1, 1, 1},
            {1, -1, 0, 2}, {-1, -1, 0, 2}, {0, -1, 1, 2}, {0, -1, -1, 2},
            {1, 0, 1, 3}, {1, 0, -1, 3}, {-1, 0, -1, 3}, {-1, 0, 1, 3},
            {1, 1, 1, 4}, {1, 1, -1, 4}, {-1, 1, -1, 4}, {-1, 1, 1, 4},
            {0, 1, 0, 5}
    };

    /**
     * 构造暗影高炉核心方块
     *
     * @param properties 方块属性
     */
    public ShadowBlastFurnaceCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("在完成多方块结构时"));
        tooltip.add(Component.literal("使用对应蓝图右击此核心以进行搭建"));
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return List.of(new ItemStack(this));
    }

    // ==================== 多方块搭建（原版 ShadowBlastFurnaceCorePr0） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        tryAssemble(level, pos, player);
        return InteractionResult.CONSUME;
    }

    /**
     * 尝试搭建多方块结构：校验蓝图与 3×3×3 结构，
     * 成功则消融全部结构方块并在核心下方生成暗影高炉
     *
     * @param level  世界（服务端）
     * @param pos    核心位置
     * @param player 交互玩家
     */
    private void tryAssemble(Level level, BlockPos pos, Player player) {
        if (!player.getMainHandItem().is(PDItems.BLUEPRINT_0.get().asItem())) {
            player.displayClientMessage(Component.literal("缺少蓝图 请手持蓝图点击核心"), true);
            return;
        }
        Block[] parts = {
                PDBlocks.CHISELED_SHADOW_STONE_BRICK.get(),
                PDBlocks.SHADOW_STONE_TILES.get(),
                PDBlocks.SHADOW_STONE_BRICK_SLAB.get(),
                PDBlocks.SHADOW_STONE_TILES_WALL.get(),
                PDBlocks.RUST_BLACK_METAL_BLOCK_WALL.get(),
                PDBlocks.RUST_BLACK_METAL_BLOCK.get()
        };
        for (int[] cell : STRUCTURE) {
            BlockPos target = pos.offset(cell[0], cell[1], cell[2]);
            if (level.getBlockState(target).getBlock() != parts[cell[3]]) {
                player.displayClientMessage(Component.literal("多方块结构不完整"), true);
                return;
            }
        }
        // 结构完整：消融全部结构方块与核心本体
        for (int[] cell : STRUCTURE) {
            level.destroyBlock(pos.offset(cell[0], cell[1], cell[2]), false);
        }
        level.destroyBlock(pos, false);
        // 在核心下方生成暗影高炉并播放搭建特效（machine0 + 动画 3）
        BlockPos furnacePos = pos.below();
        level.setBlock(furnacePos, PDBlocks.SHADOW_BLAST_FURNACE.get().defaultBlockState(), 3);
        ShadowBlastFurnaceBlock.playAssembleEffect(level, furnacePos);
    }
}
