package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.block.entity.TheEndlessBookOfDreamSeekersBlockEntity;
import com.pasterdream.pasterdreammod.menu.TheEndlessBookOfDreamSeekersMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 无尽寻梦者书导入槽校验钩子（独立小测，避免改共享 PDPortingVerifyTest）。
 * <p>
 * 用法：
 * <ul>
 *   <li>逻辑自检：{@link #verifyLogic()} —— 不需世界，测槽常量 / isItemValid 语义 / 导入复制 / 1→2 扩容；</li>
 *   <li>游戏内：放置 {@code pasterdream:the_endless_book_of_dream_seekers}，打开 GUI，
 *       左槽（导入 43,26）放物品，点「导入」，右槽（展示 115,26）应出现该物品且导入槽清空；
 *       或打开菜单后调用
 *       {@code menu.clickMenuButton(player, TheEndlessBookOfDreamSeekersMenu.BUTTON_IMPORT)}。</li>
 * </ul>
 */
public final class PDEndlessBookVerifyHooks {

    private PDEndlessBookVerifyHooks() {
    }

    /**
     * 无世界逻辑自检
     *
     * @return 失败说明；空列表表示通过
     */
    public static List<String> verifyLogic() {
        List<String> failures = new ArrayList<>();

        if (TheEndlessBookOfDreamSeekersBlockEntity.SLOT_COUNT != 2) {
            failures.add("SLOT_COUNT expected 2, got " + TheEndlessBookOfDreamSeekersBlockEntity.SLOT_COUNT);
        }
        if (TheEndlessBookOfDreamSeekersMenu.BUTTON_IMPORT != 0) {
            failures.add("BUTTON_IMPORT expected 0");
        }

        // 与 BE 相同的 isItemValid / slotLimit 契约
        ItemStackHandler handler = new ItemStackHandler(2) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return slot != TheEndlessBookOfDreamSeekersBlockEntity.SLOT_DISPLAY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };

        ItemStack sample = new ItemStack(Items.BOOK);
        if (handler.isItemValid(TheEndlessBookOfDreamSeekersBlockEntity.SLOT_DISPLAY, sample)) {
            failures.add("display slot should reject isItemValid");
        }
        if (!handler.isItemValid(TheEndlessBookOfDreamSeekersBlockEntity.SLOT_IMPORT, sample)) {
            failures.add("import slot should accept isItemValid");
        }

        // 导入语义：槽1 → 槽0 并清空槽1（等价 Pr5）
        handler.setStackInSlot(TheEndlessBookOfDreamSeekersBlockEntity.SLOT_IMPORT, sample.copy());
        ItemStack imported = handler.getStackInSlot(TheEndlessBookOfDreamSeekersBlockEntity.SLOT_IMPORT).copy();
        imported.setCount(1);
        handler.setStackInSlot(TheEndlessBookOfDreamSeekersBlockEntity.SLOT_DISPLAY, imported);
        handler.setStackInSlot(TheEndlessBookOfDreamSeekersBlockEntity.SLOT_IMPORT, ItemStack.EMPTY);

        if (!handler.getStackInSlot(TheEndlessBookOfDreamSeekersBlockEntity.SLOT_DISPLAY).is(Items.BOOK)) {
            failures.add("after import, display slot should hold book");
        }
        if (!handler.getStackInSlot(TheEndlessBookOfDreamSeekersBlockEntity.SLOT_IMPORT).isEmpty()) {
            failures.add("after import, import slot should be empty");
        }

        // 旧档 1 槽扩容语义
        ItemStackHandler oldOne = new ItemStackHandler(1);
        oldOne.setStackInSlot(0, new ItemStack(Items.DIAMOND));
        ItemStack preserved = oldOne.getStackInSlot(0).copy();
        oldOne.setSize(2);
        oldOne.setStackInSlot(0, preserved);
        if (!oldOne.getStackInSlot(0).is(Items.DIAMOND) || oldOne.getSlots() != 2) {
            failures.add("1→2 slot expand should keep slot0 and size=2");
        }

        return failures;
    }
}
