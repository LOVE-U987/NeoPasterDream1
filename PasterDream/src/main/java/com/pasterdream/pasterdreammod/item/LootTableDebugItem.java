package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.MeltdreamChestBlock;
import com.pasterdream.pasterdreammod.block.entity.MeltdreamChestBlockEntity;
import com.pasterdream.pasterdreammod.menu.LootTableDebugMenu;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 战利品库调试器 (loot_table_debug_wand)。
 * <p>
 * 右键空气或潜行右键方块打开战利品库选择界面；普通右键方块在点击面外侧生成
 * 当前选择的原版箱子或融梦水晶箱，并把选中的战利品表写入方块实体。
 * 选择保存在物品的 {@link DataComponents#CUSTOM_DATA} 中。
 */
public class LootTableDebugItem extends Item {

    /** 当前战利品表 ID 的 NBT 键 */
    public static final String TAG_TABLE = "pasterdream:loot_table_debug_table";

    /** 箱体类型标记（1=融梦水晶箱，0=原版箱子）的 NBT 键 */
    public static final String TAG_MELTDREAM = "pasterdream:loot_table_debug_meltdream";

    /**
     * 构造战利品库调试器。
     */
    public LootTableDebugItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    }

    /**
     * 读取物品当前选择的战利品表。
     *
     * @param stack 调试器物品栈
     * @return 战利品表 ID；未选择或数据非法时返回 null
     */
    @Nullable
    public static ResourceLocation getSelectedTable(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(TAG_TABLE, Tag.TAG_STRING)) {
            return null;
        }
        return ResourceLocation.tryParse(tag.getString(TAG_TABLE));
    }

    /**
     * 读取物品当前选择的箱体类型。
     *
     * @param stack 调试器物品栈
     * @return true 为融梦水晶箱，false 为原版箱子
     */
    public static boolean isMeltdream(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(TAG_MELTDREAM);
    }

    /**
     * 写入选择，保留其他自定义数据键。
     *
     * @param stack     调试器物品栈
     * @param table     战利品表 ID；null 表示清除选择
     * @param meltdream 是否融梦水晶箱
     */
    public static void writeSelection(ItemStack stack, @Nullable ResourceLocation table, boolean meltdream) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (table == null) {
                tag.remove(TAG_TABLE);
            } else {
                tag.putString(TAG_TABLE, table.toString());
            }
            tag.putBoolean(TAG_MELTDREAM, meltdream);
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer, hand);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            if (!context.getLevel().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                openMenu(serverPlayer, context.getHand());
            }
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockPos target = context.getClickedPos().relative(context.getClickedFace());
        return placeChest((ServerLevel) context.getLevel(), target, player, context.getItemInHand());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ResourceLocation selected = getSelectedTable(stack);
        if (selected == null) {
            tooltip.add(Component.translatable("tooltip.pasterdream.loot_table_debug_wand.unselected"));
        } else {
            tooltip.add(Component.translatable(
                    "tooltip.pasterdream.loot_table_debug_wand.current", selected.toString()));
        }
        Component type = Component.translatable(isMeltdream(stack)
                ? "gui.pasterdream.loot_table_debug.type.meltdream"
                : "gui.pasterdream.loot_table_debug.type.vanilla");
        tooltip.add(Component.translatable("tooltip.pasterdream.loot_table_debug_wand.type", type));
        tooltip.add(Component.translatable("tooltip.pasterdream.loot_table_debug_wand.hint"));
    }

    /**
     * 打开选择界面；选择无效时回退到首项并写回物品。
     *
     * @param player 服务端玩家
     * @param hand   手持调试器的手
     */
    private static void openMenu(ServerPlayer player, InteractionHand hand) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        ItemStack stack = player.getItemInHand(hand);
        List<ResourceLocation> tables = LootTableDebugCatalog.scan(server);
        int index = resolveMenuIndex(player, stack, tables);
        boolean meltdream = isMeltdream(stack);
        boolean mainHand = hand == InteractionHand.MAIN_HAND;
        player.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new LootTableDebugMenu(id, inv, hand, tables, index, meltdream),
                        Component.translatable("gui.pasterdream.loot_table_debug.title")),
                buf -> writeMenuData(buf, tables, index, meltdream, mainHand));
    }

    /**
     * 计算界面初始下标；选择缺失或不在目录中时回退首项并同步手持栈。
     *
     * @param player 服务端玩家
     * @param stack  调试器物品栈
     * @param tables 当前目录
     * @return 合法下标；空目录返回 0
     */
    private static int resolveMenuIndex(ServerPlayer player, ItemStack stack, List<ResourceLocation> tables) {
        if (tables.isEmpty()) {
            return 0;
        }
        ResourceLocation selected = getSelectedTable(stack);
        int index = selected == null ? -1 : tables.indexOf(selected);
        if (index >= 0) {
            return index;
        }
        writeSelection(stack, tables.get(0), isMeltdream(stack));
        player.inventoryMenu.broadcastChanges();
        message(player, "message.pasterdream.loot_table_debug.fallback");
        return 0;
    }

    /**
     * 写入客户端打开界面所需的附加数据，顺序与菜单网络构造一致。
     *
     * @param buf       网络缓冲区
     * @param tables    战利品表目录
     * @param index     初始下标
     * @param meltdream 是否融梦水晶箱
     * @param mainHand  是否主手打开
     */
    private static void writeMenuData(FriendlyByteBuf buf, List<ResourceLocation> tables, int index,
                                      boolean meltdream, boolean mainHand) {
        buf.writeVarInt(tables.size());
        for (ResourceLocation table : tables) {
            buf.writeResourceLocation(table);
        }
        buf.writeVarInt(index);
        buf.writeBoolean(meltdream);
        buf.writeBoolean(mainHand);
    }

    /**
     * 在目标位置生成当前选择的箱子。
     *
     * @param level  服务端世界
     * @param target 目标位置
     * @param player 使用物品的玩家
     * @param stack  调试器物品栈
     * @return 交互结果
     */
    private static InteractionResult placeChest(ServerLevel level, BlockPos target, Player player, ItemStack stack) {
        List<ResourceLocation> tables = LootTableDebugCatalog.scan(level.getServer());
        ResourceLocation tableId = resolvePlacementTable(player, stack, tables);
        if (tableId == null) {
            message(player, "message.pasterdream.loot_table_debug.no_tables");
            return InteractionResult.FAIL;
        }
        if (!canPlaceAt(level, target, player)) {
            message(player, "message.pasterdream.loot_table_debug.invalid_pos");
            return InteractionResult.FAIL;
        }
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, tableId);
        LootTable table = level.getServer().reloadableRegistries().getLootTable(key);
        if (table == null || table == LootTable.EMPTY) {
            message(player, "message.pasterdream.loot_table_debug.table_missing");
            return InteractionResult.FAIL;
        }
        if (isMeltdream(stack)) {
            return placeMeltdreamChest(level, target, player, tableId);
        }
        return placeVanillaChest(level, target, player, key);
    }

    /**
     * 取得放置用战利品表；选择无效时回退首项并写回。
     *
     * @param player 使用物品的玩家
     * @param stack  调试器物品栈
     * @param tables 当前目录
     * @return 战利品表 ID；空目录返回 null
     */
    @Nullable
    private static ResourceLocation resolvePlacementTable(Player player, ItemStack stack, List<ResourceLocation> tables) {
        if (tables.isEmpty()) {
            return null;
        }
        ResourceLocation selected = getSelectedTable(stack);
        if (selected != null && tables.contains(selected)) {
            return selected;
        }
        ResourceLocation fallback = tables.get(0);
        writeSelection(stack, fallback, isMeltdream(stack));
        message(player, "message.pasterdream.loot_table_debug.fallback");
        return fallback;
    }

    /**
     * 校验目标位置是否可放置。
     *
     * @param level  服务端世界
     * @param target 目标位置
     * @param player 使用物品的玩家
     * @return true 可放置
     */
    private static boolean canPlaceAt(ServerLevel level, BlockPos target, Player player) {
        return level.mayInteract(player, target)
                && !level.isOutsideBuildHeight(target)
                && level.getBlockState(target).canBeReplaced();
    }

    /**
     * 生成单格原版箱子并写入战利品表。
     *
     * @param level  服务端世界
     * @param target 目标位置
     * @param player 使用物品的玩家
     * @param key    战利品表键
     * @return 交互结果
     */
    private static InteractionResult placeVanillaChest(ServerLevel level, BlockPos target, Player player,
                                                       ResourceKey<LootTable> key) {
        BlockState state = Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, player.getDirection().getOpposite())
                .setValue(ChestBlock.TYPE, ChestType.SINGLE);
        level.setBlockAndUpdate(target, state);
        if (!(level.getBlockEntity(target) instanceof RandomizableContainerBlockEntity container)) {
            level.removeBlock(target, false);
            PasterDreamMod.LOGGER.warn("[LootTableDebug] 原版箱子方块实体缺失，已移除：{}", target);
            message(player, "message.pasterdream.loot_table_debug.place_failed");
            return InteractionResult.FAIL;
        }
        container.setLootTable(key);
        container.setChanged();
        message(player, "message.pasterdream.loot_table_debug.placed",
                Component.translatable("message.pasterdream.loot_table_debug.type.vanilla"));
        return InteractionResult.SUCCESS;
    }

    /**
     * 生成融梦水晶箱并写入调试战利品表。
     *
     * @param level   服务端世界
     * @param target  目标位置
     * @param player  使用物品的玩家
     * @param tableId 战利品表 ID
     * @return 交互结果
     */
    private static InteractionResult placeMeltdreamChest(ServerLevel level, BlockPos target, Player player,
                                                         ResourceLocation tableId) {
        BlockState state = PDBlocks.MELTDREAM_CHEST.get().defaultBlockState()
                .setValue(MeltdreamChestBlock.FACING, player.getDirection().getOpposite());
        level.setBlockAndUpdate(target, state);
        if (!(level.getBlockEntity(target) instanceof MeltdreamChestBlockEntity chest)) {
            level.removeBlock(target, false);
            PasterDreamMod.LOGGER.warn("[LootTableDebug] 融梦水晶箱方块实体缺失，已移除：{}", target);
            message(player, "message.pasterdream.loot_table_debug.place_failed");
            return InteractionResult.FAIL;
        }
        chest.setDebugLootTable(tableId);
        message(player, "message.pasterdream.loot_table_debug.placed",
                Component.translatable("message.pasterdream.loot_table_debug.type.meltdream"));
        return InteractionResult.SUCCESS;
    }

    /**
     * 向玩家发送 actionbar 提示。
     *
     * @param player 玩家
     * @param key    语言键
     * @param args   翻译参数
     */
    private static void message(Player player, String key, Object... args) {
        player.displayClientMessage(Component.translatable(key, args), true);
    }
}
