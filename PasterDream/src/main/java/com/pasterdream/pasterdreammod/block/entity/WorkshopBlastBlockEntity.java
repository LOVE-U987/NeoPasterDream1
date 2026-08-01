package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.WorkshopBlastMenu;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import com.pasterdream.pasterdreammod.util.WeaponWorkshopVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 工坊锻炉方块实体 (Workshop Blast Block Entity)
 * <p>
 * 煅烧工序（原版 WorkshopBlastPr0/Pr1 + Recipe0 + Inlay0 procedure）：
 * <ul>
 *   <li>槽位：0 原胚输入 / 1 镶嵌材料（暗影碎片 tabitem_1）/ 2 岩浆桶输入 /
 *       3 空桶回收 / 4 产物输出；储罐 4000mB 仅收岩浆。</li>
 *   <li>岩浆桶：槽 2 放入岩浆桶且储罐可容纳 → 注入 1000mB，空桶叠入槽 3
 *       （原版由 GUI 槽位变更消息触发，新版在周期 tick 轮询，语义超集）。</li>
 *   <li>入炉（每 10 tick）：未煅烧中 + 储罐≥1000mB + 槽 0 为原胚 +
 *       全局暂存工序 &lt;1（原版跨机耦合门槛，保真保留）→ 吸入原胚、
 *       扣 1000mB、置位 switch、machine2 音效。</li>
 *   <li>煅烧中（每 10 tick）：number+1，烟/岩浆粒子 + 高炉噼啪声，
 *       number 达 5/10/15/20 追加 machine2 音效；number≥23（约 11.5 秒）出炉：
 *       工序置 1、改名"未完工原胚（待锤炼）"写入槽 4；若槽 1 有暗影碎片 →
 *       镶嵌 +0.5 攻击伤害并消耗；machine1 音效。</li>
 * </ul>
 * 新版增强：在制原胚持久化到 BE NBT（"PendingItem"），重启不丢失。
 */
public class WorkshopBlastBlockEntity extends BlockEntity implements MenuProvider {

    /** 原胚输入槽 */
    public static final int SLOT_INPUT = 0;
    /** 镶嵌材料槽（tabitem_1） */
    public static final int SLOT_INLAY = 1;
    /** 岩浆桶输入槽 */
    public static final int SLOT_BUCKET_IN = 2;
    /** 空桶回收槽 */
    public static final int SLOT_BUCKET_OUT = 3;
    /** 产物槽 */
    public static final int SLOT_RESULT = 4;

    /** 储罐容量（mB，与原版一致） */
    private static final int TANK_CAPACITY = 4000;
    /** 每次煅烧耗量 / 每桶注入量（mB） */
    private static final int LAVA_PER_OPERATION = 1000;
    /** 出炉所需 number 计数（每 10 tick +1） */
    private static final int FINISH_COUNT = 23;

    /** 原胚物品标签 */
    private static final TagKey<Item> EMBRYO_ITEMS =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "embryo_items"));

    /** 5 格库存 */
    private final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** 岩浆储罐（4000mB，仅接受岩浆，与原版一致） */
    private final FluidTank fluidTank = new FluidTank(TANK_CAPACITY, fs -> fs.getFluid() == Fluids.LAVA) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
            }
        }
    };

    /** 煅烧进行中标记（原版持久数据 "switch"） */
    private boolean switchOn;
    /** 煅烧计数（原版持久数据 "number"） */
    private double number;
    /** 镶嵌待结算标记（原版持久数据 "inlay"） */
    private boolean inlay;
    /** 在制原胚（新版持久化，防重启丢失） */
    private ItemStack pendingItem = ItemStack.EMPTY;

    /**
     * 构造工坊锻炉方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public WorkshopBlastBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.WORKSHOP_BLAST.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return 5 格 ItemStackHandler
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * 获取储罐岩浆量（菜单 DataSlot 读取，GUI 满罐贴图判断 ≥1000）
     *
     * @return 岩浆量（mB）
     */
    public int getFluidAmount() {
        return fluidTank.getFluidAmount();
    }

    // ==================== 煅烧流程（服务端，每 10 tick 由方块调度） ====================

    /**
     * 周期处理（原版 WorkshopBlastPr1Procedure）：
     * 岩浆桶注入 → 入炉/出炉判定 → 煅烧计数与音效粒子
     */
    public void tickBlast() {
        if (level == null || level.isClientSide()) {
            return;
        }
        processLavaBucket();
        processRecipe();
        if (!switchOn) {
            return;
        }
        number += 1;
        if (level instanceof ServerLevel serverLevel) {
            double x = worldPosition.getX();
            double y = worldPosition.getY();
            double z = worldPosition.getZ();
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x + 0.5, y + 2, z + 0.5, 7, 0, 3, 0, 0.15);
            serverLevel.sendParticles(ParticleTypes.LAVA, x + 0.5, y + 2, z + 0.5, 4, 0.05, 3, 0.05, 0.15);
        }
        level.playSound(null, worldPosition, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 2.0f, 1.0f);
        if (number == 5 || number == 10 || number == 15 || number == 20) {
            level.playSound(null, worldPosition, PDSounds.MACHINE2.get(), SoundSource.BLOCKS, 1.2f, 1.0f);
        }
        setChanged();
        syncToClient();
    }

    /**
     * 岩浆桶注入（原版 WorkshopBlastPr0Procedure）：
     * 槽 2 为岩浆桶且储罐可再容纳 1000mB → 注入并把空桶叠入槽 3
     */
    private void processLavaBucket() {
        ItemStack bucket = itemHandler.getStackInSlot(SLOT_BUCKET_IN);
        if (!bucket.is(Items.LAVA_BUCKET) || fluidTank.getFluidAmount() + LAVA_PER_OPERATION > TANK_CAPACITY) {
            return;
        }
        fluidTank.fill(new FluidStack(Fluids.LAVA, LAVA_PER_OPERATION), IFluidHandler.FluidAction.EXECUTE);
        itemHandler.setStackInSlot(SLOT_BUCKET_IN, ItemStack.EMPTY);
        ItemStack out = itemHandler.getStackInSlot(SLOT_BUCKET_OUT);
        ItemStack emptyBuckets = new ItemStack(Items.BUCKET, out.getCount() + 1);
        itemHandler.setStackInSlot(SLOT_BUCKET_OUT, emptyBuckets);
    }

    /**
     * 入炉与出炉判定（原版 WorkshopBlastRecipe0Procedure）
     */
    private void processRecipe() {
        if (!switchOn) {
            // 入炉：储罐≥1000mB + 槽 0 原胚 + 全局暂存工序 <1（原版跨机耦合门槛）
            ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
            if (fluidTank.getFluidAmount() >= LAVA_PER_OPERATION
                    && input.is(EMBRYO_ITEMS)
                    && PasterItemData.getDouble(WeaponWorkshopVariables.weaponWorkshopItem, "process") < 1) {
                pendingItem = input.copy();
                pendingItem.setCount(1);
                WeaponWorkshopVariables.weaponWorkshopItem = pendingItem;
                input.shrink(1);
                itemHandler.setStackInSlot(SLOT_INPUT, input);
                fluidTank.drain(LAVA_PER_OPERATION, IFluidHandler.FluidAction.EXECUTE);
                switchOn = true;
                level.playSound(null, worldPosition, PDSounds.MACHINE2.get(), SoundSource.BLOCKS, 1.2f, 1.0f);
                setChanged();
                syncToClient();
            }
            return;
        }
        if (number < FINISH_COUNT) {
            return;
        }
        // 出炉：工序置 1、改名写入槽 4；槽 1 暗影碎片 → 镶嵌结算；machine1 音效
        switchOn = false;
        number = 0;
        ItemStack result = pendingItem.isEmpty() ? ItemStack.EMPTY : pendingItem.copy();
        if (!result.isEmpty()) {
            WeaponWorkshopVariables.weaponWorkshopItem = result;
            PasterItemData.putDouble(result, "process", 1);
            result.set(DataComponents.CUSTOM_NAME, Component.translatable("message.pasterdream.embryo.pending_forge"));
            result.setCount(1);
            itemHandler.setStackInSlot(SLOT_RESULT, result);
        }
        pendingItem = ItemStack.EMPTY;
        if (itemHandler.getStackInSlot(SLOT_INLAY).is(PDItems.TABITEM_1.get().asItem())) {
            this.inlay = true;
            processInlay();
        }
        level.playSound(null, worldPosition, PDSounds.MACHINE1.get(), SoundSource.NEUTRAL, 1.2f, 1.0f);
        setChanged();
        syncToClient();
    }

    /**
     * 镶嵌结算（原版 WorkshopBlastInlay0Procedure）：
     * inlay 置位时读取槽 4 产物 → 槽 1 为暗影碎片则 +0.5 攻击伤害、
     * 消耗碎片并写回槽 4 → 清除 inlay
     */
    private void processInlay() {
        if (!this.inlay) {
            return;
        }
        ItemStack stash = itemHandler.getStackInSlot(SLOT_RESULT).copy();
        WeaponWorkshopVariables.weaponWorkshopItem = stash;
        if (itemHandler.getStackInSlot(SLOT_INLAY).is(PDItems.TABITEM_1.get().asItem())) {
            PasterItemData.putBoolean(stash, "paster_attack_damage", true);
            PasterItemData.putDouble(stash, "paster_attack_damage_number",
                    PasterItemData.getDouble(stash, "paster_attack_damage_number") + 0.5);
            ItemStack inlayStack = itemHandler.getStackInSlot(SLOT_INLAY);
            inlayStack.shrink(1);
            itemHandler.setStackInSlot(SLOT_INLAY, inlayStack);
            stash.setCount(1);
            itemHandler.setStackInSlot(SLOT_RESULT, stash);
        }
        this.inlay = false;
    }

    /** 同步方块实体数据到客户端 */
    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // ==================== 持久化 ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        CompoundTag tankTag = new CompoundTag();
        fluidTank.writeToNBT(registries, tankTag);
        tag.put("fluidTank", tankTag);
        tag.putBoolean("switch", switchOn);
        tag.putDouble("number", number);
        tag.putBoolean("inlay", inlay);
        tag.put("PendingItem", pendingItem.saveOptional(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        if (tag.get("fluidTank") instanceof CompoundTag tankTag) {
            fluidTank.readFromNBT(registries, tankTag);
        }
        switchOn = tag.getBoolean("switch");
        number = tag.getDouble("number");
        inlay = tag.getBoolean("inlay");
        pendingItem = tag.contains("PendingItem")
                ? ItemStack.parseOptional(registries, tag.getCompound("PendingItem"))
                : ItemStack.EMPTY;
    }

    // ==================== 客户端同步 ====================

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ==================== GUI 菜单提供者 ====================

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new WorkshopBlastMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.workshop_blast");
    }
}
