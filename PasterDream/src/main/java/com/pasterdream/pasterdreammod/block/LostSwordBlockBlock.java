package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 遗落之剑方块（lost_sword_block）
 * <p>
 * 忠实还原原版 {@code LostSwordBlockBlock + LostSwordBlockPr0/Pr1Procedure}：
 * <ul>
 *   <li>20 tick 循环释放尘埃粒子；</li>
 *   <li>右键：需要力量 II 及以上效果才能撬动；累计 5 次（BE 数据 number）后
 *       破坏方块、掉出剑胚（sword_embryo_0）并播放 skill0 音效，
 *       每次撬动伴随尘埃粒子与深板岩破裂声。</li>
 * </ul>
 * 不可破坏、noOcclusion，形状为底座 + 剑身两段。
 */
public class LostSwordBlockBlock extends Block implements EntityBlock {

    /**
     * 构造遗落之剑方块
     *
     * @param properties 方块属性
     */
    public LostSwordBlockBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.or(box(0, 0, 0, 16, 2, 16), box(4, 2, 4, 12, 9, 12));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return Collections.singletonList(new ItemStack(this));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        level.sendParticles(PDParticles.DUST_0_PARTICLE.holder().get(),
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8, 0.5, 0.6, 0.5, 0.02);
        level.scheduleTick(pos, this, 20);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        // 原 LostSwordBlockPr1Procedure
        int strengthAmp = player.hasEffect(MobEffects.DAMAGE_BOOST)
                ? player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() : 0;
        if (strengthAmp >= 1) {
            if (W4DataBlockEntity.getDoubleAt(level, pos, "number") >= 5) {
                level.destroyBlock(pos, false);
                if (level instanceof ServerLevel serverLevel) {
                    ItemEntity drop = new ItemEntity(serverLevel,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            new ItemStack(PDItemsMaterials.SWORD_EMBRYO_0.get()));
                    drop.setPickUpDelay(5);
                    serverLevel.addFreshEntity(drop);
                }
                if (!level.isClientSide()) {
                    level.playSound(null, pos, PDSounds.SKILL0.get(), SoundSource.NEUTRAL, 1, 1);
                }
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(PDParticles.DUST_0_PARTICLE.holder().get(),
                            pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 16, 0.3, 0.2, 0.3, 0.5);
                }
            }
            W4DataBlockEntity.putDoubleAt(level, pos, "number",
                    W4DataBlockEntity.getDoubleAt(level, pos, "number") + 1);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(PDParticles.DUST_0_PARTICLE.holder().get(),
                        pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 16, 0, 0.2, 0, 0.4);
            }
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.DEEPSLATE_BREAK, SoundSource.NEUTRAL, 1, 1);
            }
        } else if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.translatable("message.pasterdream.lost_sword.not_strong_enough"), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4DataBlockEntity(PDBlockEntitiesFurniture.LOST_SWORD_BLOCK.get(), pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventId, int eventParam) {
        super.triggerEvent(state, level, pos, eventId, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventId, eventParam);
    }
}
