package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.item.LootTableDebugCatalog;
import com.pasterdream.pasterdreammod.item.LootTableDebugItem;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 战利品库调试器选择菜单（无槽位）。
 * <p>
 * 服务端持有当前战利品表目录、选中下标与箱体类型；客户端通过附带数据还原同一列表。
 * 五个按钮经 {@code clickMenuButton} 分派，每次点击恰好调用一次 {@link #commitSelection}
 * 把手持物品的选择写回。
 */
public class LootTableDebugMenu extends AbstractContainerMenu {

    /** 上一项 */
    public static final int BUTTON_UP = 0;
    /** 下一项 */
    public static final int BUTTON_DOWN = 1;
    /** 切换箱体类型 */
    public static final int BUTTON_TYPE = 2;
    /** 上一个分类首项 */
    public static final int BUTTON_CATEGORY_PREV = 3;
    /** 下一个分类首项 */
    public static final int BUTTON_CATEGORY_NEXT = 4;

    /** 单次网络解码允许的最大表数量 */
    private static final int MAX_TABLES = 4096;

    private final List<ResourceLocation> tables;
    private final InteractionHand hand;
    private final SimpleContainerData data = new SimpleContainerData(2);

    private int index;
    private boolean meltdream;

    /**
     * 网络构造：顺序与 {@link LootTableDebugItem} 的附加数据写入一致。
     *
     * @param id  容器 ID
     * @param inv 玩家库存
     * @param buf 附加数据
     */
    public LootTableDebugMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, decode(buf));
    }

    private LootTableDebugMenu(int id, Inventory inv, Decoded decoded) {
        this(id, inv, decoded.hand(), decoded.tables(), decoded.index(), decoded.meltdream());
    }

    /**
     * 构造选择菜单。
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param hand      打开界面的手
     * @param tables    战利品表目录
     * @param index     初始下标
     * @param meltdream 是否融梦水晶箱
     */
    public LootTableDebugMenu(int id, Inventory inv, InteractionHand hand, List<ResourceLocation> tables,
                              int index, boolean meltdream) {
        super(PDMenus.LOOT_TABLE_DEBUG.get(), id);
        this.hand = hand;
        this.tables = List.copyOf(tables);
        this.index = (this.tables.isEmpty() || index < 0 || index >= this.tables.size()) ? 0 : index;
        this.meltdream = meltdream;
        this.data.set(0, this.index);
        this.data.set(1, meltdream ? 1 : 0);
        this.addDataSlots(this.data);
    }

    /**
     * 从网络缓冲区读取目录与初始状态。
     *
     * @param buf 附加数据
     * @return 解码结果
     */
    private static Decoded decode(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        if (count < 0 || count > MAX_TABLES) {
            throw new DecoderException("loot table list too large: " + count);
        }
        List<ResourceLocation> tables = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            tables.add(buf.readResourceLocation());
        }
        int index = buf.readVarInt();
        boolean meltdream = buf.readBoolean();
        boolean mainHand = buf.readBoolean();
        if (count == 0) {
            index = 0;
        } else if (index < 0 || index >= count) {
            index = 0;
        }
        return new Decoded(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
                tables, index, meltdream);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        if (tables.isEmpty()) {
            return true;
        }
        switch (id) {
            case BUTTON_UP -> moveSelection(-1);
            case BUTTON_DOWN -> moveSelection(1);
            case BUTTON_TYPE -> toggleType();
            case BUTTON_CATEGORY_PREV -> jumpCategory(-1);
            case BUTTON_CATEGORY_NEXT -> jumpCategory(1);
            default -> {
                return false;
            }
        }
        commitSelection(serverPlayer);
        return true;
    }

    /**
     * 上下移动一个条目。
     *
     * @param delta 位移
     */
    private void moveSelection(int delta) {
        if (tables.isEmpty()) {
            return;
        }
        this.index = Math.floorMod(this.index + delta, this.tables.size());
    }

    /**
     * 跳转到相邻非空分类的首项。
     *
     * @param direction -1 上一个，1 下一个
     */
    private void jumpCategory(int direction) {
        if (tables.isEmpty()) {
            return;
        }
        this.index = LootTableDebugCatalog.jumpCategory(this.tables, this.index, direction);
    }

    /**
     * 翻转箱体类型。
     */
    private void toggleType() {
        this.meltdream = !this.meltdream;
    }

    /**
     * 把当前选择写回手持调试器并同步客户端。
     *
     * @param player 服务端玩家
     */
    private void commitSelection(ServerPlayer player) {
        this.data.set(0, this.index);
        this.data.set(1, this.meltdream ? 1 : 0);
        ItemStack stack = player.getItemInHand(this.hand);
        if (!(stack.getItem() instanceof LootTableDebugItem)) {
            return;
        }
        LootTableDebugItem.writeSelection(stack, this.tables.get(this.index), this.meltdream);
        player.inventoryMenu.broadcastChanges();
        this.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive()
                && player.getItemInHand(this.hand).is(PDItems.LOOT_TABLE_DEBUG_WAND.get());
    }

    /**
     * 获取当前目录。
     *
     * @return 不可变目录列表
     */
    public List<ResourceLocation> getTables() {
        return this.tables;
    }

    /**
     * 获取当前同步下标。
     *
     * @return 下标（空目录为 0）
     */
    public int getSyncedIndex() {
        return this.data.get(0);
    }

    /**
     * 获取当前同步的箱体类型。
     *
     * @return true 为融梦水晶箱
     */
    public boolean isMeltdreamSelected() {
        return this.data.get(1) != 0;
    }

    /**
     * 获取当前选中的战利品表。
     *
     * @return 战利品表 ID；空目录或下标越界时返回 null
     */
    @Nullable
    public ResourceLocation getCurrentTable() {
        int current = this.data.get(0);
        return current >= 0 && current < this.tables.size() ? this.tables.get(current) : null;
    }

    /**
     * 网络解码结果。
     *
     * @param hand      打开界面的手
     * @param tables    战利品表目录
     * @param index     初始下标
     * @param meltdream 是否融梦水晶箱
     */
    private record Decoded(InteractionHand hand, List<ResourceLocation> tables, int index, boolean meltdream) {
    }
}
