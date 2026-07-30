package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.api.menu.SimpleContainerMenu;
import com.pasterdream.pasterdreammod.item.BookPlayerData;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 玩家书籍菜单（创造模式 — 玩家数据驱动）
 * <p>
 * 槽位布局与 {@link TheEndlessBookOfDreamSeekersMenu} 一致：
 * <ul>
 *   <li>索引 0：展示槽（115, 26）— 显示最后导入的书籍</li>
 *   <li>索引 1：导入槽（43, 26）— 放入待存储的书籍</li>
 *   <li>索引 2-28：玩家背包（3×9，起始 (8, 84)）</li>
 *   <li>索引 29-37：玩家快捷栏（1×9，起始 (8, 142)）</li>
 * </ul>
 * 点击"导入"按钮将该槽物品存入玩家 NBT 书库并更新展示槽。
 * <p>
 * 此菜单由玩家持有书卷物品右键触发，数据存储于玩家 NBT 而非方块实体。
 */
public class PlayerBookMenu extends SimpleContainerMenu {

    /** "导入"按钮 ID（与 {@link TheEndlessBookOfDreamSeekersMenu#BUTTON_IMPORT} 一致） */
    public static final int BUTTON_IMPORT = 0;

    private final BookPlayerData bookData;

    /**
     * 服务端构造 — 使用玩家书籍数据。
     *
     * @param id       容器 ID
     * @param inv      玩家背包
     * @param bookData 该玩家的书籍数据管理器
     */
    public PlayerBookMenu(int id, Inventory inv, BookPlayerData bookData) {
        super(PDMenus.PLAYER_BOOK.get(), id, 2);
        this.bookData = bookData;
        IItemHandler handler = bookData.getHandler();

        // 展示槽（115, 26）— 从书库第一个有效槽读取
        SlotItemHandler displaySlot = new SlotItemHandler(handler, 0, 115, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // 不可手动放入
            }
        };
        // 注意：这里展示槽绑定到 handler 槽 0，实际指向书库第一格
        // 导入时会同时更新展示槽显示
        this.addSlot(displaySlot);

        // 导入槽（43, 26）— 玩家放入待导入物品，使用独立 ItemStackHandler 暂存
        // 使用 BookPlayerData 的 handler 槽 0 作为临时导入暂存
        // 实际由 clickMenuButton 处理
        this.addSlot(new SlotItemHandler(handler, 1, 43, 26));

        addPlayerInventory(inv, 84);
    }

    /**
     * 客户端构造（网络反序列化）。
     *
     * @param id       容器 ID
     * @param inv      玩家背包
     * @param extraData 网络缓冲区
     */
    public PlayerBookMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        super(PDMenus.PLAYER_BOOK.get(), id, 2);
        // 客户端不需要 BookPlayerData，使用空 handler 暂存（槽数据由服务端同步）
        ItemStackHandler temp = new ItemStackHandler(2);
        this.bookData = null;

        this.addSlot(new SlotItemHandler(temp, 0, 115, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(temp, 1, 43, 26));
        addPlayerInventory(inv, 84);
    }

    /**
     * 获取玩家书籍数据（仅服务端可访问）。
     *
     * @return 书籍数据管理器，客户端返回 null
     */
    public BookPlayerData getBookData() {
        return bookData;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BUTTON_IMPORT && bookData != null) {
            if (player.level().isClientSide()) return true;
            // 从导入槽（handler 槽 1）读取物品
            net.neoforged.neoforge.items.ItemStackHandler handler = bookData.getHandler();
            ItemStack importStack = handler.getStackInSlot(1);
            if (!importStack.isEmpty()) {
                // 存入书库
                boolean added = bookData.addBook(importStack);
                if (added) {
                    // 更新展示槽 = 刚导入的物品
                    ItemStack displayCopy = importStack.copy();
                    displayCopy.setCount(1);
                    handler.setStackInSlot(0, displayCopy);
                    // 清空导入槽
                    handler.setStackInSlot(1, ItemStack.EMPTY);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return true; // 玩家数据菜单始终有效
    }
}
