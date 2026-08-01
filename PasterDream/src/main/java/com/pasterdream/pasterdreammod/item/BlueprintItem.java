package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.menu.BlueprintGui0Menu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * 蓝图物品 (Blueprint)
 * <p>
 * 武器工坊群的多方块结构图纸（原版 Fix 版 BlueprintItem）：
 * <ul>
 *   <li>blueprint_0 —— 暗影高炉蓝图；blueprint_1 —— 精铸工坊蓝图
 *       （手持点击精铸工作台激活多方块结构校验）；</li>
 *   <li>悬浮提示：结构类型行 + 结构名称（青色）+ 展开说明 + 逐条构建描述；</li>
 *   <li>右键空气：打开 BlueprintGui0 结构阅览界面 + notes 音效
 *       （原版 NetworkHooks.openScreen + BlueprintPr0Procedure）；</li>
 *   <li>右键方块：提示「请对准空气使用」（原版 useOn 不打开 GUI）。</li>
 * </ul>
 */
public class BlueprintItem extends Item {

    /** 蓝图对应的多方块结构 ID（决定悬浮提示中的结构名称行） */
    private final ResourceLocation blueprintId;
    /** 逐条构建描述行数（item.pasterdream.&lt;id&gt;.describe.N） */
    private final int descriptionCount;

    /**
     * 构造蓝图物品
     *
     * @param blueprintId      结构 ID（如 pasterdream:weapon_workshop）
     * @param descriptionCount 构建描述行数
     */
    public BlueprintItem(String blueprintId, int descriptionCount) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
        this.blueprintId = ResourceLocation.parse(blueprintId);
        this.descriptionCount = descriptionCount;
    }

    /**
     * 构造无构建描述的蓝图物品
     *
     * @param blueprintId 结构 ID
     */
    public BlueprintItem(String blueprintId) {
        this(blueprintId, 0);
    }

    /**
     * 获取蓝图结构 ID
     *
     * @return 结构 ID
     */
    public ResourceLocation getBlueprintId() {
        return blueprintId;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.pasterdream.blueprint.0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("blueprint.pasterdream." + blueprintId.toLanguageKey())
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.pasterdream.blueprint.1").withStyle(ChatFormatting.GRAY));
        for (int i = 0; i < descriptionCount; i++) {
            tooltip.add(Component.translatable(this.getDescriptionId() + ".describe." + i)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            BlockPos pos = serverPlayer.blockPosition();
            boolean mainHand = hand == InteractionHand.MAIN_HAND;
            ResourceLocation id = this.blueprintId;
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("Blueprint " + id);
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player p) {
                    return new BlueprintGui0Menu(containerId, inventory, id, pos, hand);
                }
            }, buf -> {
                buf.writeResourceLocation(id);
                buf.writeBlockPos(pos);
                buf.writeBoolean(mainHand);
            });
        }
        playUseFeedback(level, player.getX(), player.getY(), player.getZ(), player);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        super.useOn(context);
        // 原版：对方块使用只走 BlueprintPr0Procedure（提示对准空气），不打开 GUI
        playUseFeedback(context.getLevel(),
                context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(),
                context.getPlayer());
        return InteractionResult.SUCCESS;
    }

    /**
     * 使用反馈（原版 BlueprintPr0Procedure）：
     * 目标位置为空气 → 播放 notes 翻页音效；否则提示对准空气使用。
     *
     * @param level  世界
     * @param x      目标 X
     * @param y      目标 Y
     * @param z      目标 Z
     * @param entity 使用者
     */
    private static void playUseFeedback(Level level, double x, double y, double z, Entity entity) {
        BlockPos pos = BlockPos.containing(x, y, z);
        if (level.getBlockState(pos).getBlock() == Blocks.AIR) {
            if (!level.isClientSide()) {
                // 原版 pasterdream:notes；本 worktree 若尚未挂 PDSounds.NOTES 则回退翻书音
                SoundEvent notes = BuiltInRegistries.SOUND_EVENT
                        .getOptional(ResourceLocation.parse("pasterdream:notes"))
                        .orElse(SoundEvents.BOOK_PAGE_TURN);
                level.playSound(null, pos, notes, SoundSource.NEUTRAL, 1.0f, 1.0f);
            }
        } else if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(Component.translatable("tooltip.pasterdream.blueprint.aim_air"), true);
        }
    }
}
