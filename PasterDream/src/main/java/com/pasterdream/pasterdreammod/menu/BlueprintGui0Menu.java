package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.data.BluePrintLoader;
import com.pasterdream.pasterdreammod.registry.PDMenusBlueprint;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 蓝图阅览容器菜单（还原原版 {@code BlueprintGui0Menu}）。
 * <p>
 * 布局：
 * <ul>
 *   <li>索引 0–24：只读 5×5 结构层展示槽（不可放入/取出）</li>
 *   <li>索引 25–51：玩家背包 3×9</li>
 *   <li>索引 52–60：快捷栏 1×9</li>
 * </ul>
 * 翻页通过 vanilla {@link #clickMenuButton}：按钮 ID 为 1-based 目标页码
 * （等价原版 {@code BlueprintGui0ButtonMessage.now_page}）。
 */
public class BlueprintGui0Menu extends AbstractContainerMenu {

    /** 原版 guistate 兼容字段（客户端按钮引用等） */
    public final HashMap<String, Object> guistate = new HashMap<>();

    public final Level world;
    public final Player entity;
    public final int x;
    public final int y;
    public final int z;

    private final ItemStackHandler internal = new ItemStackHandler(BluePrintLoader.PAGE_SIZE);
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    private final BluePrintLoader.BluePrint bluePrint;
    private final InteractionHand hand;
    private final ResourceLocation blueprintId;

    /**
     * 网络工厂构造（客户端 / IContainerFactory）
     *
     * @param id        容器 ID
     * @param inv       玩家背包
     * @param extraData blueprintId + BlockPos + isMainHand
     */
    public BlueprintGui0Menu(int id, Inventory inv, FriendlyByteBuf extraData) {
        // Defense: spectator's vanilla single-arg openMenu sends an empty buffer (readableBytes == 0),
        // readResourceLocation()/readBlockPos()/readBoolean() would throw IndexOutOfBoundsException → connection lost.
        this(id, inv,
                extraData != null && extraData.readableBytes() >= 2 ? extraData.readResourceLocation() : ResourceLocation.withDefaultNamespace("missing"),
                extraData != null && extraData.readableBytes() >= 8 ? extraData.readBlockPos() : inv.player.blockPosition(),
                extraData != null && extraData.readableBytes() >= 1
                        ? (extraData.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)
                        : InteractionHand.MAIN_HAND);
    }

    /**
     * 服务端/通用构造
     *
     * @param id          容器 ID
     * @param inv         玩家背包
     * @param blueprintId 蓝图 ID
     * @param pos         打开位置（音效）
     * @param hand        持有蓝图的手
     */
    public BlueprintGui0Menu(int id, Inventory inv, ResourceLocation blueprintId, BlockPos pos, InteractionHand hand) {
        super(PDMenusBlueprint.BLUEPRINT_GUI_0.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();
        this.blueprintId = blueprintId;
        this.bluePrint = BluePrintLoader.get(blueprintId);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.hand = hand;

        for (int i = 0; i < BluePrintLoader.PAGE_SIZE; i++) {
            int sx = i % 5;
            int sy = i / 5;
            // 原版坐标：53 + 18*x, 24 + 18*y
            this.customSlots.put(i, this.addSlot(new SlotItemHandler(internal, i, 53 + 18 * sx, 24 + 18 * sy) {
                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            }));
        }

        refreshSlot(entity, 0);

        // 玩家背包：原版偏移 10+8=18 水平，50+84=134 垂直
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                final int slotIndex = col + (row + 1) * 9;
                this.addSlot(new Slot(inv, slotIndex, 18 + col * 18, 134 + row * 18) {
                    @Override
                    public boolean mayPickup(Player player) {
                        return hand == InteractionHand.MAIN_HAND
                                ? this.getContainerSlot() != inv.selected
                                : super.mayPickup(player);
                    }

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return hand == InteractionHand.MAIN_HAND
                                ? this.getContainerSlot() != inv.selected
                                : super.mayPlace(stack);
                    }
                });
            }
        }

        // 快捷栏：原版 y = 50+142 = 192
        for (int col = 0; col < 9; col++) {
            final int hotbar = col;
            this.addSlot(new Slot(inv, hotbar, 18 + col * 18, 192) {
                @Override
                public boolean mayPickup(Player player) {
                    return hand == InteractionHand.MAIN_HAND
                            ? this.getContainerSlot() != inv.selected
                            : super.mayPickup(player);
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return hand == InteractionHand.MAIN_HAND
                            ? this.getContainerSlot() != inv.selected
                            : super.mayPlace(stack);
                }
            });
        }
    }

    /**
     * 当前蓝图数据（可能为 null，若数据包未加载）
     *
     * @return 蓝图
     */
    public BluePrintLoader.BluePrint getBluePrint() {
        return this.bluePrint;
    }

    /**
     * 蓝图 ID
     *
     * @return 资源 ID
     */
    public ResourceLocation getBlueprintId() {
        return blueprintId;
    }

    /**
     * 用指定页（0-based）刷新 5×5 展示槽
     *
     * @param player 玩家
     * @param index  0-based 页码
     */
    public void refreshSlot(Player player, int index) {
        if (bluePrint == null || bluePrint.isEmpty()) {
            for (int i = 0; i < BluePrintLoader.PAGE_SIZE; i++) {
                this.customSlots.get(i).set(ItemStack.EMPTY);
            }
            if (player.containerMenu == this) {
                this.broadcastChanges();
            }
            return;
        }
        if (index < 0 || index >= bluePrint.getMaxPage()) {
            return;
        }
        List<Item> itemList = bluePrint.get(index);
        for (int i = 0; i < BluePrintLoader.PAGE_SIZE; i++) {
            ItemStack setstack = new ItemStack(itemList.get(i));
            if (!setstack.isEmpty()) {
                setstack.setCount(1);
            }
            this.customSlots.get(i).set(setstack);
        }
        if (player.containerMenu == this) {
            this.broadcastChanges();
        }
    }

    /**
     * 翻页按钮：id 为 1-based 目标页（原版网络包 now_page）
     *
     * @param player 玩家
     * @param id     1-based 页码
     * @return 是否处理
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (bluePrint == null || bluePrint.isEmpty()) {
            return false;
        }
        int pageIndex = id - 1;
        if (pageIndex < 0 || pageIndex >= bluePrint.getMaxPage()) {
            return false;
        }
        Level level = player.level();
        BlockPos pos = BlockPos.containing(x, y, z);
        if (!level.hasChunkAt(pos)) {
            return false;
        }
        // 原版：服务端播放翻书音效
        if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.NEUTRAL, 1.0f, 1.0f);
        }
        refreshSlot(player, pageIndex);
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return entity.isAlive();
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        // 原版：副手打开时禁止 SWAP，避免把蓝图换走导致菜单失效
        if (!(hand == InteractionHand.OFF_HAND && clickType == ClickType.SWAP)) {
            super.clicked(slotId, button, clickType, player);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < BluePrintLoader.PAGE_SIZE) {
                // 展示槽不可 shift 取出（mayPickup=false），此处保底
                if (!this.moveItemStackTo(itemstack1, BluePrintLoader.PAGE_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (!this.moveItemStackTo(itemstack1, 0, BluePrintLoader.PAGE_SIZE, false)) {
                if (index < BluePrintLoader.PAGE_SIZE + 27) {
                    if (!this.moveItemStackTo(itemstack1, BluePrintLoader.PAGE_SIZE + 27, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemstack1, BluePrintLoader.PAGE_SIZE, BluePrintLoader.PAGE_SIZE + 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }
}
