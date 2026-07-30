package com.pasterdream.pasterdreammod.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 寻梦者的永恒书卷 — 玩家书籍数据管理器
 * <p>
 * 在玩家持久 NBT 中维护一个书籍物品槽位列表。
 * 创造模式右键将书籍"存入"此处，非创造模式右键从此"取出"。
 * 使用 {@link ItemStackHandler} 存储，支持序列化/反序列化。
 * <p>
 * 每个玩家最多可存储 {@link #MAX_BOOKS} 本书籍。
 */
public class BookPlayerData {

    /** NBT 根键名 */
    private static final String PLAYER_TAG_ROOT = "pasterdream";
    /** 书籍列表的 NBT 子键 */
    private static final String TAG_BOOKS = "book_of_dream_seekers_items";
    /** 最大存储数量 */
    public static final int MAX_BOOKS = 9;
    /** 默认槽位大小 */
    private static final int SLOT_STACK_LIMIT = 1;

    private final ItemStackHandler handler;
    private final Player player;

    /**
     * @param player 目标玩家
     */
    public BookPlayerData(Player player) {
        this.player = player;
        this.handler = new ItemStackHandler(MAX_BOOKS) {
            @Override
            public int getSlotLimit(int slot) {
                return SLOT_STACK_LIMIT;
            }

            @Override
            protected void onContentsChanged(int slot) {
                saveToPlayer();
            }
        };
        loadFromPlayer();
    }

    /**
     * 获取底层 {@link ItemStackHandler}，供菜单直接绑定。
     *
     * @return ItemStackHandler（9 槽，每槽叠加上限 1）
     */
    public ItemStackHandler getHandler() {
        return handler;
    }

    /**
     * 存储一本书到玩家数据。
     *
     * @param stack 要存储的物品（数量会被设为 1）
     * @return 是否成功存入（false 表示已满或物品为空）
     */
    public boolean addBook(ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack existing = handler.getStackInSlot(i);
            if (existing.isEmpty()) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                handler.setStackInSlot(i, copy);
                return true;
            }
        }
        return false; // 已满
    }

    /**
     * 取出并移除第一本书（FIFO 顺序）。
     *
     * @return 取出的 ItemStack，无书时返回 {@link ItemStack#EMPTY}
     */
    public ItemStack takeFirstBook() {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack existing = handler.getStackInSlot(i);
            if (!existing.isEmpty()) {
                ItemStack result = existing.copy();
                handler.setStackInSlot(i, ItemStack.EMPTY);
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * 检查是否还有存储的书籍。
     *
     * @return 至少有一本书时为 true
     */
    public boolean hasBooks() {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    /**
     * 获取所有存储书籍的列表（不可变快照）。
     *
     * @return 书籍列表
     */
    public List<ItemStack> getBooks() {
        List<ItemStack> books = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                books.add(stack.copy());
            }
        }
        return books;
    }

    /**
     * 从玩家 NBT 加载数据。
     */
    private void loadFromPlayer() {
        CompoundTag root = player.getPersistentData().getCompound(PLAYER_TAG_ROOT);
        if (root.contains(TAG_BOOKS, Tag.TAG_LIST)) {
            ListTag list = root.getList(TAG_BOOKS, Tag.TAG_COMPOUND);
            // 先将 handler 初始化为空
            for (int i = 0; i < handler.getSlots(); i++) {
                handler.setStackInSlot(i, ItemStack.EMPTY);
            }
            // 用 ListTag 反序列化（ItemStackHandler 的 deserializeNBT 需要完整 CompoundTag）
            CompoundTag wrapper = new CompoundTag();
            wrapper.put("items", list);
            handler.deserializeNBT(player.registryAccess(), wrapper);
        }
    }

    /**
     * 保存数据到玩家 NBT。
     */
    public void saveToPlayer() {
        CompoundTag root = player.getPersistentData().getCompound(PLAYER_TAG_ROOT);
        CompoundTag serialized = handler.serializeNBT(player.registryAccess());
        ListTag list = serialized.getList("items", Tag.TAG_COMPOUND);
        root.put(TAG_BOOKS, list);
        player.getPersistentData().put(PLAYER_TAG_ROOT, root);
    }
}
