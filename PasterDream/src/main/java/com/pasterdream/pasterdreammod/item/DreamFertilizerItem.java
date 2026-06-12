package com.pasterdream.pasterdreammod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Supplier;

/**
 * 衍梦肥泥 (Dream Fertilizer)
 * 对作物使用时生效，类似骨粉效果
 * 额外生成衍梦肥泥粒子效果并播放挥手动画
 */
public class DreamFertilizerItem extends Item {

    private final Supplier<SimpleParticleType> fertilizerParticle;

    /**
     * @param properties         物品属性
     * @param fertilizerParticle 粒子 Supplier（延迟加载，避免注册时序问题）
     */
    public DreamFertilizerItem(Item.Properties properties, Supplier<SimpleParticleType> fertilizerParticle) {
        super(properties.stacksTo(64));
        this.fertilizerParticle = fertilizerParticle;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        if (level instanceof Level _level) {
            BlockPos _bp = clickedPos;
            boolean grew = BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), _level, _bp)
                    || BoneMealItem.growWaterPlant(new ItemStack(Items.BONE_MEAL), _level, _bp, null);
            if (grew) {
                if (!_level.isClientSide()) {
                    _level.levelEvent(2005, _bp, 0);
                }
            }
        }

        if (player instanceof LivingEntity livingEntity) {
            livingEntity.swing(InteractionHand.MAIN_HAND, true);
        }

        if (!player.isCreative()) {
            itemStack.shrink(1);
        }

        if (level instanceof ServerLevel serverLevel) {
            SimpleParticleType particle = fertilizerParticle.get();
            if (particle != null) {
                serverLevel.sendParticles(
                        particle,
                        clickedPos.getX() + 0.5,
                        clickedPos.getY() + 0.7,
                        clickedPos.getZ() + 0.5,
                        16, 0.6, 0.5, 0.6, 0.25
                );
            }
        }

        return InteractionResult.SUCCESS;
    }
}