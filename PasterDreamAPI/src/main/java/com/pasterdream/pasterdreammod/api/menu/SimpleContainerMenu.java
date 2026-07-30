package com.pasterdream.pasterdreammod.api.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 通用容器菜单骨架 —— 抽取「容器格 + 玩家背包/快捷栏 + 双向 quickMove + BE stillValid」样板。
 * <p>
 * 主模具体菜单保留槽位坐标、业务槽过滤与特殊 stillValid；本类提供：
 * <ul>
 *   <li>{@link #addContainerGrid} — IItemHandler 矩形网格</li>
 *   <li>{@link #addPlayerInventory} — 标准 3×9 + 快捷栏</li>
 *   <li>{@link #quickMoveBetweenContainerAndPlayer} — 容器区 ↔ 玩家区 shift-click</li>
 *   <li>{@link #stillValidBlockEntity} — 方块实体距离校验</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * public class ShadowChestMenu extends SimpleContainerMenu {
 *     public ShadowChestMenu(int id, Inventory inv, BlockEntity be) {
 *         super(PDMenus.SHADOW_CHEST.get(), id, 15);
 *         IItemHandler h = ((ShadowChestBlockEntity) be).getItemHandler();
 *         addContainerGrid(h, 5, 3, 43, 15);
 *         addPlayerInventory(inv, 84);
 *         bindBlockEntity(be);
 *     }
 * }
 * }</pre>
 */
public abstract class SimpleContainerMenu extends AbstractContainerMenu {

    /** 容器区槽位数（不含玩家背包）；quickMove 分界 */
    protected final int containerSlotCount;

    @Nullable
    private BlockEntity boundBlockEntity;

    /**
     * @param type               MenuType
     * @param containerId        容器同步 ID
     * @param containerSlotCount 业务容器槽数量（玩家 36 格另计）
     */
    protected SimpleContainerMenu(@Nullable MenuType<?> type, int containerId, int containerSlotCount) {
        super(type, containerId);
        if (containerSlotCount < 0) {
            throw new IllegalArgumentException("containerSlotCount 不能为负: " + containerSlotCount);
        }
        this.containerSlotCount = containerSlotCount;
    }

    /**
     * 绑定方块实体，供默认 {@link #stillValid(Player)} 使用。
     */
    protected void bindBlockEntity(@Nullable BlockEntity blockEntity) {
        this.boundBlockEntity = blockEntity;
    }

    @Nullable
    protected BlockEntity getBoundBlockEntity() {
        return boundBlockEntity;
    }

    /**
     * 向菜单添加 IItemHandler 矩形槽位网格（行优先：index = row * columns + col）。
     *
     * @param handler  物品处理器
     * @param columns  列数
     * @param rows     行数
     * @param startX   左上角像素 X
     * @param startY   左上角像素 Y
     * @param slotSize 槽间距（通常 18）
     */
    protected void addContainerGrid(IItemHandler handler, int columns, int rows,
                                    int startX, int startY, int slotSize) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int index = col + row * columns;
                this.addSlot(new SlotItemHandler(handler, index,
                        startX + col * slotSize, startY + row * slotSize));
            }
        }
    }

    /**
     * 槽间距 18 的 {@link #addContainerGrid} 便捷重载。
     */
    protected void addContainerGrid(IItemHandler handler, int columns, int rows, int startX, int startY) {
        addContainerGrid(handler, columns, rows, startX, startY, 18);
    }

    /**
     * 添加标准玩家背包（3×9，索引 9–35）与快捷栏（索引 0–8）。
     * <p>
     * 背包起始 Y 为 {@code invY}；快捷栏 Y 默认为 {@code invY + 58}（与原版 84/142、100/158 等布局一致）。
     *
     * @param inv  玩家背包
     * @param invY 背包第一行像素 Y
     * @param invX 背包/快捷栏左缘像素 X（原版多为 8）
     */
    protected void addPlayerInventory(Inventory inv, int invY, int invX) {
        addPlayerInventory(inv, invY, invX, invY + 58);
    }

    /**
     * 添加玩家背包与快捷栏，快捷栏 Y 可独立指定。
     *
     * @param inv      玩家背包
     * @param invY     背包第一行 Y
     * @param invX     左缘 X
     * @param hotbarY  快捷栏 Y
     */
    protected void addPlayerInventory(Inventory inv, int invY, int invX, int hotbarY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        invX + col * 18, invY + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, invX + col * 18, hotbarY));
        }
    }

    /**
     * 左缘 X=8 的 {@link #addPlayerInventory(Inventory, int, int)} 便捷重载。
     */
    protected void addPlayerInventory(Inventory inv, int invY) {
        addPlayerInventory(inv, invY, 8);
    }

    /**
     * 容器区 [0, containerSlotCount) ↔ 玩家区 [containerSlotCount, slots) 的标准 quickMove。
     * 子类可直接在 {@link #quickMoveStack} 中委托本方法。
     */
    protected ItemStack quickMoveBetweenContainerAndPlayer(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return result;
        }
        ItemStack stack = slot.getItem();
        result = stack.copy();
        int containerEnd = containerSlotCount;
        int playerEnd = this.slots.size();

        if (index < containerEnd) {
            if (!this.moveItemStackTo(stack, containerEnd, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, containerEnd, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return result;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveBetweenContainerAndPlayer(player, index);
    }

    /**
     * 方块实体距离校验（与 vanilla {@link AbstractContainerMenu#stillValid} 一致）。
     */
    protected static boolean stillValidBlockEntity(Player player, @Nullable BlockEntity be) {
        if (be == null || be.getLevel() == null) {
            return false;
        }
        Block block = be.getBlockState().getBlock();
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(be.getLevel(), be.getBlockPos()),
                player, block);
    }

    /**
     * 默认：若已 {@link #bindBlockEntity}，则按 BE 校验；否则始终 true（物品菜单等可覆盖）。
     */
    @Override
    public boolean stillValid(Player player) {
        if (boundBlockEntity != null) {
            return stillValidBlockEntity(player, boundBlockEntity);
        }
        return true;
    }
}
