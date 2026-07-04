package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandSpawnBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * 亚伦柯斯之手生成激活方块
 * <p>
 * 使用 GeckoLib 渲染 3D 模型和动画的发光方块，
 * 对应方块实体 {@link AaroncosHandSpawnBlockEntity}。
 * 不可破坏，发光等级 12。
 * <p>
 * 右键点击可召唤亚伦柯斯左右手 BOSS，触发 BOSS 战。
 */
public class AaroncosHandSpawnBlock extends BaseEntityBlock {

    public static final MapCodec<AaroncosHandSpawnBlock> CODEC = simpleCodec(AaroncosHandSpawnBlock::new);

    /**
     * 构造亚伦柯斯之手生成激活方块
     *
     * @param properties 方块属性
     */
    public AaroncosHandSpawnBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AaroncosHandSpawnBlockEntity(pos, state);
    }

    // ==================== 右键交互 ====================

    /**
     * 右键交互 —— 召唤 BOSS 或离开竞技场
     * <p>
     * 不同阶段的行为：
     * <ul>
     *   <li>NOT_SUMMONED：召唤 BOSS</li>
     *   <li>SUMMONING：提示正在召唤</li>
     *   <li>FIGHTING：提示战斗中</li>
     *   <li>VICTORY：传送玩家离开竞技场</li>
     * </ul>
     *
     * @param state     方块状态
     * @param level     世界
     * @param pos       方块位置
     * @param player    玩家
     * @param hitResult 点击结果
     * @return 交互结果
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        // 客户端直接返回成功，防止重复交互
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // 旁观模式不能交互
        if (player.isSpectator()) {
            return InteractionResult.FAIL;
        }

        // 仅在亚伦柯斯竞技场维度生效
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        if (!serverLevel.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            player.displayClientMessage(Component.translatable("arena.pasterdream.summon_wrong_dimension"), true);
            return InteractionResult.FAIL;
        }

        // 获取当前战斗阶段
        PDArenaBossManager.BossFightPhase currentPhase = PDArenaBossManager.getPhase(serverLevel);

        // VICTORY 阶段：传送玩家离开竞技场
        if (currentPhase == PDArenaBossManager.BossFightPhase.VICTORY) {
            PDArenaBossManager.teleportPlayersToOverworld(serverLevel, player);
            player.displayClientMessage(Component.translatable("arena.pasterdream.leave_arena"), true);
            return InteractionResult.CONSUME;
        }

        // 其他阶段：尝试召唤 BOSS
        boolean success = PDArenaBossManager.triggerBossSummon(serverLevel);
        if (success) {
            player.displayClientMessage(Component.translatable("arena.pasterdream.summon_waking"), true);
            return InteractionResult.CONSUME;
        } else {
            String translationKey = switch (currentPhase) {
                case SUMMONING -> "arena.pasterdream.summon_summoning";
                case FIGHTING -> "arena.pasterdream.summon_fighting";
                default -> "arena.pasterdream.summon_failed";
            };
            player.displayClientMessage(Component.translatable(translationKey), true);
            return InteractionResult.FAIL;
        }
    }
}
