package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 梦境列车结构方块 (Dream Train Structure)
 * 装饰性方块，右键点击时发送列车到站提示消息
 */
public class DreamTrainStructureBlock extends Block {

    /**
     * 构造梦境列车结构方块
     *
     * @param properties 方块属性
     */
    public DreamTrainStructureBlock(Properties properties) {
        super(properties);
    }

    /**
     * 右键点击时发送列车到站提示消息
     *
     * @param state      方块状态
     * @param level      世界实例
     * @param pos        方块位置
     * @param player     交互玩家
     * @param hitResult  点击结果
     * @return InteractionResult 交互结果
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.sendSystemMessage(Component.literal("§6列车即将到站，请做好准备..."));
        }
        return InteractionResult.SUCCESS;
    }
}
