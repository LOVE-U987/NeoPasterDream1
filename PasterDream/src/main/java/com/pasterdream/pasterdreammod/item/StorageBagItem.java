package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.menu.StorageBagMenu;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

/**
 * 储物袋物品族（原版 StorageBag / StorageBag0）。
 * <p>
 * 1.21 用 {@link DataComponents#CONTAINER} 替代原版 ITEM_HANDLER Capability；
 * 打开 GUI 时把容器内容装入菜单，关闭时写回物品。
 */
public class StorageBagItem extends Item {

    /** 小储物袋 3×3 */
    public static final int SIZE_SMALL = 9;
    /** 高级储物袋 5×5 */
    public static final int SIZE_LARGE = 25;

    private final int slots;
    private final boolean advanced;

    /**
     * @param advanced true=storage_bag_0（25 格），false=storage_bag（9 格）
     */
    public StorageBagItem(boolean advanced) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)
                .component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
        this.advanced = advanced;
        this.slots = advanced ? SIZE_LARGE : SIZE_SMALL;
    }

    public int getSlotCount() {
        return slots;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            boolean mainHand = hand == InteractionHand.MAIN_HAND;
            boolean advancedBag = this.advanced;
            int slotCount = this.slots;
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable(advancedBag
                            ? "container.pasterdream.storage_bag_0"
                            : "container.pasterdream.storage_bag");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                    return new StorageBagMenu(id, inv, stack, slotCount, advancedBag, mainHand);
                }
            }, buf -> {
                buf.writeVarInt(slotCount);
                buf.writeBoolean(advancedBag);
                buf.writeBoolean(mainHand);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /**
     * 读取物品容器内容为固定长度列表（不足补 EMPTY，超出截断）
     */
    public static java.util.List<ItemStack> readContents(ItemStack bag, int size) {
        ItemContainerContents contents = bag.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        java.util.List<ItemStack> list = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(i < contents.getSlots() ? contents.getStackInSlot(i).copy() : ItemStack.EMPTY);
        }
        return list;
    }

    /**
     * 把槽位内容写回物品的 CONTAINER 组件
     */
    public static void writeContents(ItemStack bag, java.util.List<ItemStack> stacks) {
        bag.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(stacks));
    }
}
