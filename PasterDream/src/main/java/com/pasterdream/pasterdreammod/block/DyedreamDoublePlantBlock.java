package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlockTags;
import com.pasterdream.pasterdreammod.registry.PDItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.core.BlockPos;

import java.util.Collections;
import java.util.List;

/**
 * 染梦双层植物通用方块
 * 用于所有双格高的花和草（如 flower_7, flower_10, grass_4, grass_10, grass_15 等）
 * 必须种在染梦土壤上；掉落以下半格结算，破坏上半格时按下半格补掉落
 */
public class DyedreamDoublePlantBlock extends DoublePlantBlock {
    /**
     * @param properties 方块属性
     */
    public DyedreamDoublePlantBlock(Properties properties) {
        super(properties);
    }

    /**
     * 使用默认属性的快捷构造
     */
    public DyedreamDoublePlantBlock() {
        this(BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .sound(SoundType.GRASS)
            .instabreak()
            .noCollission()
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .pushReaction(PushReaction.DESTROY));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(PDBlockTags.PLANTABLE_ON);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        // 只有下半部分参与掉落
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return Collections.emptyList();
        }
        // 仅使用剪刀/园艺钳等剪刀类工具破坏时掉落自身，空手或其他工具破坏不掉落
        ItemStack tool = params.getParameter(LootContextParams.TOOL);
        if (tool != null && !tool.isEmpty() && tool.is(PDItemTags.SHEARS)) {
            return List.of(new ItemStack(this));
        }
        return Collections.emptyList();
    }

    /**
     * 破坏上半格时按下半格结算掉落。
     * <p>
     * 原版 {@link DoublePlantBlock#playerWillDestroy} 按被破坏的半格结算，
     * 而 {@link #getDrops} 只对下半格返回掉落，导致从上半格采集时没有物品。
     * 此处在上半格被破坏时用下半格状态与玩家主手工具补一次掉落；
     * 基类随后对上半格结算为空，不会产生双倍掉落。
     *
     * @param level  世界
     * @param pos    被破坏的半格坐标
     * @param state  被破坏的半格状态
     * @param player 破坏方玩家
     * @return 更新后的方块状态
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !player.isCreative()
                && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos lowerPos = pos.below();
            BlockState lowerState = level.getBlockState(lowerPos);
            if (lowerState.is(this) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                dropResources(lowerState, level, lowerPos, null, player, player.getMainHandItem());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
