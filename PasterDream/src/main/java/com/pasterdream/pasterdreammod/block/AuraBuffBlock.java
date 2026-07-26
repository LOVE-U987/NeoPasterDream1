package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * 范围增益方块（guard_block 守护者方块 / restrainmove_block 行动抑制方块）
 * <p>
 * 忠实还原原版 {@code GuardBlockBlock / RestrainmoveBlockBlock} 与
 * {@code GuardBlockPr0/Pr1、RestrainmoveBlockPr0/Pr1}：
 * <ul>
 *   <li>onPlace：首次初始化 BE 数据 range（守护 16 / 抑制 0）与 switch=true，
 *       并进入 20 tick 调度循环；</li>
 *   <li>tick：当规则 pasterdreamDebugmode 关闭时，向以自身为中心、
 *       边长 range 的立方体范围内所有玩家施加对应 buff（60 tick，隐藏粒子）。</li>
 * </ul>
 * 不可破坏石块（无 noOcclusion，完整实体方块）。
 */
public class AuraBuffBlock extends Block implements EntityBlock {

    private final Supplier<Holder<MobEffect>> effect;
    private final double defaultRange;
    private final Supplier<BlockEntityType<?>> beType;
    private final List<String> tooltip;

    /**
     * 构造范围增益方块
     *
     * @param effect       施加的效果
     * @param defaultRange 初始 range 数据值
     * @param beType       方块实体类型
     * @param tooltip      悬浮提示行
     * @param properties   方块属性
     */
    public AuraBuffBlock(Supplier<Holder<MobEffect>> effect, double defaultRange,
                         Supplier<BlockEntityType<?>> beType, List<String> tooltip, Properties properties) {
        super(properties);
        this.effect = effect;
        this.defaultRange = defaultRange;
        this.beType = beType;
        this.tooltip = tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, context, list, flag);
        for (String line : tooltip) {
            list.add(Component.literal(line));
        }
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
        // 原 Pr1：首次初始化 range/switch
        if (!level.isClientSide() && !W4DataBlockEntity.getBooleanAt(level, pos, "switch")) {
            W4DataBlockEntity.putDoubleAt(level, pos, "range", defaultRange);
            W4DataBlockEntity.putBooleanAt(level, pos, "switch", true);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        // 原 Pr0：debug 规则关闭时对范围内玩家施加 buff
        if (!level.getGameRules().getBoolean(PDGameRules.PASTERDREAM_DEBUG_MODE)) {
            double range = W4DataBlockEntity.getDoubleAt(level, pos, "range");
            Vec3 center = new Vec3(pos.getX(), pos.getY(), pos.getZ());
            List<Entity> found = level.getEntitiesOfClass(Entity.class,
                    new AABB(center, center).inflate(range / 2d), e -> true);
            for (Entity entity : found) {
                if (entity instanceof Player && entity instanceof LivingEntity living
                        && !living.level().isClientSide()) {
                    living.addEffect(new MobEffectInstance(effect.get(), 60, 0, false, false));
                }
            }
        }
        level.scheduleTick(pos, this, 20);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4DataBlockEntity(beType.get(), pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventId, int eventParam) {
        super.triggerEvent(state, level, pos, eventId, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventId, eventParam);
    }
}
