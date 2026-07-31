package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.api.menu.SimpleContainerMenu;
import com.pasterdream.pasterdreammod.block.entity.TheEndlessBookOfDreamSeekersBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 寻梦者的永恒书卷 GUI 容器菜单 (The Endless Book of Dream Seekers Menu)
 * 布局对照原版 TheEndlessBookOfDreamSeekersGuiMenu：
 * <ul>
 *   <li>索引 0：展示槽（115, 26）——仅可取出；</li>
 *   <li>索引 1：导入槽（43, 26）；</li>
 *   <li>索引 2-28：玩家背包（3×9，起始 (8, 84)）；</li>
 *   <li>索引 29-37：玩家快捷栏（1×9，起始 (8, 142)）。</li>
 * </ul>
 * "导入"按钮经 vanilla {@code clickMenuButton} 触发
 * {@link TheEndlessBookOfDreamSeekersBlockEntity#importFromSlot}
 * （等价原版 GuiButtonMessage → Pr5）。
 */
public class TheEndlessBookOfDreamSeekersMenu extends SimpleContainerMenu {

    /** "导入"按钮 ID（原版 buttonID == 0 → Pr5） */
    public static final int BUTTON_IMPORT = 0;

    private final TheEndlessBookOfDreamSeekersBlockEntity blockEntity;

    /**
     * 构造寻梦者的永恒书卷菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public TheEndlessBookOfDreamSeekersMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 构造寻梦者的永恒书卷菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 书卷方块实体
     */
    public TheEndlessBookOfDreamSeekersMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), id, 2);
        // 防御：BE 缺失或类型不符时构造为空菜单，stillValid 返回 false 由服务端自动关闭
        this.blockEntity = blockEntity instanceof TheEndlessBookOfDreamSeekersBlockEntity eb ? eb : null;
        bindBlockEntity(this.blockEntity);

        if (this.blockEntity != null) {
            IItemHandler handler = this.blockEntity.getItemHandler();

            // 展示槽：原版 (115, 26)，仅可取出
            this.addSlot(new SlotItemHandler(handler, TheEndlessBookOfDreamSeekersBlockEntity.SLOT_DISPLAY, 115, 26) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
            // 导入槽：原版 (43, 26)
            this.addSlot(new SlotItemHandler(handler, TheEndlessBookOfDreamSeekersBlockEntity.SLOT_IMPORT, 43, 26));
        }

        addPlayerInventory(inv, 84);
    }

    /**
     * 获取方块实体
     *
     * @return 书卷方块实体
     */
    public TheEndlessBookOfDreamSeekersBlockEntity getBlockEntity() {
        return blockEntity;
    }

    /**
     * 处理 GUI 按钮点击（服务端调用）。
     * 执行方块实体导入（槽 1 → 槽 0），数据保存在方块实体的 NBT 中。
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        // 防御：BE 已失效（方块被拆除等）或距离过远时拒绝按钮操作
        if (this.blockEntity == null || !this.stillValid(player)) {
            return false;
        }
        if (id == BUTTON_IMPORT) {
            if (!player.level().isClientSide()) {
                this.blockEntity.importFromSlot();
            }
            return true;
        }
        return false;
    }

    /**
     * 校验玩家是否仍可操作该菜单
     *
     * @param player 玩家
     * @return BE 缺失/类型不符时菜单立即失效，由服务端关闭
     */
    @Override
    public boolean stillValid(Player player) {
        return this.blockEntity != null && super.stillValid(player);
    }
}
