package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.DreamCauldronBlockEntity;
import com.pasterdream.pasterdreammod.recipe.DreamBrewingRecipe;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 梦境炼药锅 GUI 容器菜单 (Dream Cauldron Menu)
 * 类原版酿造台的自动酿造容器：7 格炼药槽 + 玩家背包，
 * GUI 背景纹理为原版 196×196 素材，槽位坐标与纹理绘制位置一一对应。
 *
 * 槽位分布（相对于 GUI 纹理左上角）：
 * - 索引 0：通用材料槽（17, 50）——药水酿造材料 / 法术配方引导药剂
 * - 索引 1-3：药水瓶槽（61, 19）(89, 19)(117, 19)——放水瓶/药水，兼作法术配方材料位
 * - 索引 4：融梦液体桶输入槽（170, 23）——放入后自动注入 1000mB 并退还空桶
 * - 索引 5：空桶回收槽（170, 77）——仅可取出
 * - 索引 6：成品槽（89, 50)——仅可取出，法术配方产物写入此处
 * - 索引 7-33：玩家背包（3×9，起始 (18, 114)）
 * - 索引 34-42：玩家快捷栏（1×9，起始 (18, 172)）
 *
 * 同步数据（DataSlot）：
 * - 液体量（mB）：GUI 液体量文本显示
 * - 已酿造时长 / 酿造总时长：GUI 酿造进度条渲染
 *
 * 酿造为自动触发（无需按钮）：材料放入即由服务端 tick 判定启动。
 */
public class DreamCauldronMenu extends AbstractContainerMenu {

    private final DreamCauldronBlockEntity blockEntity;
    private final Level level;

    /** 客户端同步的液体量（mB），服务端每 broadcast 周期自动推送 */
    private final DataSlot fluidAmount = DataSlot.standalone();

    /** 客户端同步的已酿造时长（tick） */
    private final DataSlot brewElapsed = DataSlot.standalone();

    /** 客户端同步的酿造总时长（tick） */
    private final DataSlot brewDuration = DataSlot.standalone();

    /**
     * 构造梦境炼药锅菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public DreamCauldronMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        // Defense: spectator's vanilla single-arg openMenu sends an empty buffer (readableBytes == 0),
        // readBlockPos() would throw IndexOutOfBoundsException → connection lost.
        this(id, inv, extraData != null && extraData.readableBytes() >= 8
                ? inv.player.level().getBlockEntity(extraData.readBlockPos()) : null);
    }

    /**
     * 构造梦境炼药锅菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 梦境炼药锅方块实体
     */
    public DreamCauldronMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.DREAM_CAULDRON.get(), id);
        // 防御：BE 缺失或类型不符时构造为空菜单，stillValid 返回 false 由服务端自动关闭
        this.blockEntity = blockEntity instanceof DreamCauldronBlockEntity dce ? dce : null;
        this.level = inv.player.level();

        IItemHandler handler = this.blockEntity != null
                ? this.blockEntity.getItemHandler()
                : new net.neoforged.neoforge.items.ItemStackHandler(7);

        // Slot 0: material slot - brewing ingredient / guiding drug (original coords 17, 50)
        // 槽位过滤：药水容器（含水瓶）不可放入材料槽
        this.addSlot(new SlotItemHandler(handler, 0, 17, 50) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !stack.has(DataComponents.POTION_CONTENTS);
            }
        });
        // Slots 1-3: potion bottle slots, also spell material slots (original coords 61/89/117, 19)
        // 槽位过滤：只收药水容器（含水瓶，dream_enhanced 酿造基底）或 dream_brewing 法术配方材料
        this.addSlot(new SlotItemHandler(handler, 1, 61, 19) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.has(DataComponents.POTION_CONTENTS) || isSpellMaterial(stack);
            }
        });
        this.addSlot(new SlotItemHandler(handler, 2, 89, 19) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.has(DataComponents.POTION_CONTENTS) || isSpellMaterial(stack);
            }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 117, 19) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.has(DataComponents.POTION_CONTENTS) || isSpellMaterial(stack);
            }
        });
        // Slot 4: fluid bucket input (original coords 170, 23)
        // 槽位过滤：只收融梦液体桶
        this.addSlot(new SlotItemHandler(handler, 4, 170, 23) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(PDItems.MELTDREAM_LIQUID_BUCKET.get());
            }
        });
        // Slot 5: empty bucket return, take only (original coords 170, 77)
        this.addSlot(new SlotItemHandler(handler, 5, 170, 77) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        // Slot 6: spell result, take only (original coords 89, 50)
        this.addSlot(new SlotItemHandler(handler, 6, 89, 50) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // 玩家背包：3×9 网格，起始 (18, 114)（原版偏移）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        18 + col * 18, 114 + row * 18));
            }
        }

        // 玩家快捷栏：1×9，起始 (18, 172)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 18 + col * 18, 172));
        }

        // 同步数据：液体量 + 酿造进度（服务端读方块实体，客户端读同步值）
        this.addDataSlot(this.fluidAmount);
        this.addDataSlot(this.brewElapsed);
        this.addDataSlot(this.brewDuration);
    }

    @Override
    public void broadcastChanges() {
        // 每次广播前刷新液体量与酿造进度（服务端侧）；BE 失效时跳过
        if (!this.level.isClientSide() && this.blockEntity != null) {
            this.fluidAmount.set(this.blockEntity.getFluidAmount());
            this.brewElapsed.set(this.blockEntity.getBrewTimeElapsed());
            this.brewDuration.set(this.blockEntity.getBrewDuration());
        }
        super.broadcastChanges();
    }

    /**
     * 获取当前储罐液体量（客户端为同步值）
     *
     * @return 液体量（mB）
     */
    public int getFluidAmount() {
        if (this.blockEntity == null) {
            return 0;
        }
        return this.level.isClientSide() ? this.fluidAmount.get() : this.blockEntity.getFluidAmount();
    }

    /**
     * 获取已酿造时长（客户端为同步值）
     *
     * @return 已酿造 tick 数（空闲时为 0）
     */
    public int getBrewTimeElapsed() {
        return this.brewElapsed.get();
    }

    /**
     * 获取酿造总时长（客户端为同步值）
     *
     * @return 酿造总 tick 数（空闲时为 0）
     */
    public int getBrewDuration() {
        return this.brewDuration.get();
    }

    /**
     * 获取方块实体
     *
     * @return 梦境炼药锅方块实体
     */
    public DreamCauldronBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 7) {
                // 从炼药锅槽位移到玩家背包（索引 7-42）
                if (!this.moveItemStackTo(stackInSlot, 7, 43, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到炼药锅可放入槽位（索引 0-4，5/6 由 mayPlace 拦截）；
                // 逐槽 mayPlace 校验插入——moveItemStackTo 本身不检查 mayPlace，
                // 直接对区间移动会绕过槽位过滤（如火药被 Shift 进瓶槽）
                if (!insertWithSlotFilter(stackInSlot)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    /**
     * 判定物品是否可作为法术配方材料放入瓶槽
     * （遍历 dream_brewing 数据包配方的 materials 列表；双端可用——配方已同步至客户端）
     *
     * @param stack 待判定物品
     * @return 是任一法术配方的材料返回 true
     */
    private boolean isSpellMaterial(ItemStack stack) {
        if (this.level == null) {
            return false;
        }
        for (RecipeHolder<DreamBrewingRecipe> holder
                : this.level.getRecipeManager().getAllRecipesFor(PDRecipeTypes.DREAM_BREWING.get())) {
            for (Ingredient material : holder.value().getMaterials()) {
                if (material.test(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将背包物品按槽位过滤规则插入坩埚槽 0-4：
     * 逐槽检查 mayPlace 后移动（moveItemStackTo 支持部分插入，剩余量继续尝试后续槽位）
     *
     * @param stack 待插入物品（就地消耗）
     * @return 是否全部插入
     */
    private boolean insertWithSlotFilter(ItemStack stack) {
        for (int i = 0; i < 5 && !stack.isEmpty(); i++) {
            if (this.slots.get(i).mayPlace(stack)) {
                this.moveItemStackTo(stack, i, i + 1, false);
            }
        }
        return stack.isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        // 防御：BE 缺失/类型不符时菜单立即失效，由服务端关闭
        if (this.blockEntity == null) {
            return false;
        }
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
}
