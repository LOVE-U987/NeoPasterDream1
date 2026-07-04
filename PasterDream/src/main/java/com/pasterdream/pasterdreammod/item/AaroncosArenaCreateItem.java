package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;

import java.util.List;

/**
 * 亚伦柯斯竞技场创建器物品
 * <p>
 * 创造模式调试用物品，右键点击直接传送至亚伦柯斯 BOSS 竞技场。
 * 与竞技场传送门方块不同，该物品无需前置成就即可使用。
 */
public class AaroncosArenaCreateItem extends Item {

    /**
     * 构造函数 —— 配置物品属性
     */
    public AaroncosArenaCreateItem() {
        super(new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.COMMON));
    }

    /**
     * 添加物品提示信息
     *
     * @param itemstack 物品堆
     * @param context   提示上下文
     * @param list      提示信息列表
     * @param flag      提示标志
     */
    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("§4创造模式物品"));
    }

    /**
     * 右键使用物品 —— 传送至竞技场
     * <p>
     * 在服务器端执行传送逻辑：
     * 1. 检查是否已在目标维度（避免重复传送）
     * 2. 获取竞技场维度实例
     * 3. 传送到 (0, 70, 0) 位置
     * 4. 赋予缓降效果防止摔伤
     *
     * @param world  世界
     * @param player 玩家
     * @param hand   交互手
     * @return 交互结果
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> ar = super.use(world, player, hand);

        // 只在服务器端执行
        if (world.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return ar;
        }

        // 检查是否已在目标维度，避免重复传送
        if (world.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            serverPlayer.displayClientMessage(
                    Component.translatable("message.pasterdream.aaroncos_arena_create.already_in"),
                    true
            );
            return ar;
        }

        // 获取目标维度
        ServerLevel targetWorld = serverPlayer.getServer().getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        if (targetWorld == null) {
            return ar;
        }

        // 传送到竞技场维度 (0, 70, 0)
        DimensionTransition transition = new DimensionTransition(
                targetWorld,
                new net.minecraft.world.phys.Vec3(0.5, 70.0, 0.5),
                player.getDeltaMovement(),
                player.getYRot(),
                player.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND
        );

        serverPlayer.changeDimension(transition);

        // 赋予缓降效果 120 ticks
        if (player instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 120, 0));
        }

        return ar;
    }
}
