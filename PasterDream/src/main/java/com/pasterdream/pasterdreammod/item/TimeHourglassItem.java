package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 时之沙（time_hourglass）。
 * <p>
 * 还原自原版 TimeHourglassItem + TimeHourglassPr0/Pr1Procedure：
 * <ul>
 *   <li>对空气使用：世界时间前进 12000 tick（昼夜互换），头顶落下纯视觉闪电并散布粒子，消耗 1 个；</li>
 *   <li>右击带方块实体的方块：该方块实体持久化数据 {@code time} 增加 1,000,000
 *       （用于瞬间完成计时类梦境方块的一个阶段 / 刷新地牢冷却），消耗 1 个。</li>
 * </ul>
 * 移植说明：原版存在 BAN_TIME_HOURGLASS 通用配置开关，新版尚无对应配置项，暂不移植该禁用逻辑。
 */
public class TimeHourglassItem extends Item {

    public TimeHourglassItem(Properties properties) {
        super(properties.stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("§7对空气使用以跳跃世界时间更替昼夜"));
        tooltip.add(Component.literal("§7潜行对部分计时梦境方块使用以瞬时完成一个阶段"));
        tooltip.add(Component.literal("§7可以刷新地牢冷却"));
        tooltip.add(Component.literal("§7潜行右击方块以使用"));
        tooltip.add(Component.literal("§7§o-- 沙漏将会记得 我们所遗忘的时光"));
    }

    /** 对空气使用：跳跃世界时间 12000 tick，并在使用者上方生成纯视觉闪电 */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();
            stack.shrink(1);
            serverLevel.setDayTime(serverLevel.dayTime() + 12000);
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (bolt != null) {
                bolt.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y + 4, z)));
                bolt.setVisualOnly(true);
                serverLevel.addFreshEntity(bolt);
            }
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 64, 1, 1, 1, 0.2);
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    x, y, z, 64, 1, 1, 1, 0.2);
        }
        return InteractionResultHolder.success(stack);
    }

    /**
     * 右击方块：若方块带有方块实体，则其持久化数据 {@code time} 增加 1,000,000。
     * <p>
     * 与原版一致：始终返回 SUCCESS，保证右击方块时不会串到对空气使用的时间跳跃逻辑。
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level instanceof ServerLevel serverLevel) {
            BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
            // 原版语义：仅方块实体存在时生效（无 time 标签按 0 处理）
            if (blockEntity != null && blockEntity.getPersistentData().getDouble("time") >= 0) {
                blockEntity.getPersistentData().putDouble("time",
                        blockEntity.getPersistentData().getDouble("time") + 1_000_000);
                BlockState state = serverLevel.getBlockState(pos);
                serverLevel.sendBlockUpdated(pos, state, state, 3);
                context.getItemInHand().shrink(1);
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 16, 0.5, 0.5, 0.5, 0.05);
                serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 24, 0.5, 0.5, 0.5, 0.1);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
