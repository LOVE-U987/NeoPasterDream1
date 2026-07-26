package com.pasterdream.pasterdreammod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;

/**
 * 战利品生成工具（lootstable_create_0~9）。
 * <p>
 * 还原自原版 LootstableCreate0~9Item + LootstableCreateXPr0Procedure：
 * 潜行右击带随机容器能力的方块（箱子、木桶等）为其写入指定战利品表，
 * 下次开启时按该表生成战利品。原版通过 {@code data merge block} 命令写入
 * LootTable NBT，此处等价改为直接调用 {@link RandomizableContainerBlockEntity#setLootTable}。
 * 主要用于结构搭建与调试。
 */
public class LootstableCreateItem extends Item {

    /** 要写入容器的战利品表 */
    private final ResourceKey<LootTable> lootTable;

    /** 悬浮提示中的战利品表主题名（原版文案） */
    private final String themeLine;

    /**
     * 构造战利品生成工具
     *
     * @param tablePath 战利品表路径（如 {@code chests/loots_relic_0}，1 号为根级 {@code loots_relic_1}）
     * @param themeLine 悬浮提示主题行文案（含颜色码）
     */
    public LootstableCreateItem(String tablePath, String themeLine) {
        super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
        this.lootTable = ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath("pasterdream", tablePath));
        this.themeLine = themeLine;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("§7潜行右击容器方块以使其增加战利品表"));
        tooltip.add(Component.literal("§7战利品表："));
        tooltip.add(Component.literal(themeLine));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (level instanceof ServerLevel serverLevel) {
            BlockEntity blockEntity = serverLevel.getBlockEntity(context.getClickedPos());
            if (blockEntity instanceof RandomizableContainerBlockEntity container) {
                container.setLootTable(lootTable);
                container.setChanged();
                player.displayClientMessage(Component.literal("战利品已生成"), true);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
