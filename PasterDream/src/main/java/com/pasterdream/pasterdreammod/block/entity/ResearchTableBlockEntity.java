package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.ResearchTableMenu;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 研究台方块实体 (Research Table Block Entity)
 * <p>
 * 6 格库存（0 笔与墨 / 1 寻梦者笔记 / 2 羊皮纸 / 3 复制产物 /
 * 4 未知笔记 / 5 研究产物）+ GeckoLib 动画 + GUI 菜单。
 * <p>
 * 两个按钮的服务端语义（经 vanilla clickMenuButton 通道触发）：
 * <ul>
 *   <li>{@link #copyNotes 复制}（原版 ResearchTablePr1）：槽 0 需笔与墨、槽 3 为空、
 *       槽 1 为 pasterdream:dreamnotes 标签物品且槽 2 为羊皮纸 →
 *       笔记复制到槽 3、消耗 1 羊皮纸、笔与墨损耗 1 点耐久；</li>
 *   <li>{@link #studyNotes 研究}（原版 ResearchTablePr0）：槽 0 需笔与墨、槽 4 为
 *       unknownnotes_0 且槽 5 为空 → 校验 achievement_shadow_start 前置成就后，
 *       按 achievement_hide_11..15 成就梯度产出下一级寻梦者笔记
 *       （dreamnotes_10..13 / blueprint_0），并播放翻书音效。</li>
 * </ul>
 * 笔记产物按名动态查找 dreamnotes_10..13 / blueprint_0；
 * 成就 achievement_shadow_start / hide_11..15 未完成时提示缺少前置知识。
 */
public class ResearchTableBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    /** 笔与墨槽 */
    public static final int SLOT_PEN = 0;
    /** 寻梦者笔记槽（复制原本） */
    public static final int SLOT_NOTES = 1;
    /** 羊皮纸槽 */
    public static final int SLOT_PERGAMYN = 2;
    /** 复制产物槽 */
    public static final int SLOT_COPY_RESULT = 3;
    /** 未知笔记槽 */
    public static final int SLOT_UNKNOWN = 4;
    /** 研究产物槽 */
    public static final int SLOT_STUDY_RESULT = 5;
    /** 槽位总数 */
    public static final int SLOT_COUNT = 6;

    /** 寻梦者笔记物品标签（pasterdream:dreamnotes） */
    public static final TagKey<Item> DREAMNOTES_TAG =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dreamnotes"));

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 6 格库存 */
    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /**
     * 构造研究台方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public ResearchTableBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.RESEARCH_TABLE.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return 6 格 ItemStackHandler
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // ==================== 按钮语义（服务端） ====================

    /**
     * "复制"按钮（原版 ResearchTablePr1Procedure）：
     * 把槽 1 的寻梦者笔记复制到槽 3，消耗羊皮纸并损耗笔与墨
     *
     * @param player 点击按钮的玩家
     */
    public void copyNotes(Player player) {
        if (level == null || level.isClientSide()) {
            return;
        }
        if (!itemHandler.getStackInSlot(SLOT_PEN).is(PDItems.PEN_AND_INK.get().asItem())) {
            sendMessage(player, "缺少笔与墨", false);
            return;
        }
        if (!itemHandler.getStackInSlot(SLOT_COPY_RESULT).isEmpty()) {
            return;
        }
        ItemStack notes = itemHandler.getStackInSlot(SLOT_NOTES);
        if (notes.is(DREAMNOTES_TAG) && itemHandler.getStackInSlot(SLOT_PERGAMYN).is(PDItems.PERGAMYN.get().asItem())) {
            ItemStack copy = notes.copy();
            copy.setCount(1);
            itemHandler.setStackInSlot(SLOT_COPY_RESULT, copy);
            ItemStack pergamyn = itemHandler.getStackInSlot(SLOT_PERGAMYN);
            pergamyn.shrink(1);
            itemHandler.setStackInSlot(SLOT_PERGAMYN, pergamyn);
            damagePen();
            sendMessage(player, "笔记已成功复制", false);
        }
    }

    /**
     * "研究"按钮（原版 ResearchTablePr0Procedure）：
     * 消耗未知笔记，按成就梯度产出下一级寻梦者笔记
     *
     * @param player 点击按钮的玩家
     */
    public void studyNotes(Player player) {
        if (level == null || level.isClientSide()) {
            return;
        }
        if (!itemHandler.getStackInSlot(SLOT_PEN).is(PDItems.PEN_AND_INK.get().asItem())) {
            sendMessage(player, "缺少笔与墨", false);
            return;
        }
        if (!itemHandler.getStackInSlot(SLOT_UNKNOWN).is(PDItems.UNKNOWNNOTES_0.get().asItem())
                || !itemHandler.getStackInSlot(SLOT_STUDY_RESULT).isEmpty()) {
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!isAdvancementDone(serverPlayer, "achievement_shadow_start")) {
            // 缺少前置知识：与原版一致仅提示 + 合书音效
            sendMessage(player, "缺少前置知识 你还无法解读笔记的内容", true);
            level.playSound(null, worldPosition, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1, 1);
            return;
        }
        // 消耗：笔与墨损耗 1 点耐久，未知笔记 -1
        damagePen();
        ItemStack unknown = itemHandler.getStackInSlot(SLOT_UNKNOWN);
        unknown.shrink(1);
        itemHandler.setStackInSlot(SLOT_UNKNOWN, unknown);
        level.playSound(null, worldPosition, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.2f, 1);

        boolean hide11 = isAdvancementDone(serverPlayer, "achievement_hide_11");
        boolean hide12 = isAdvancementDone(serverPlayer, "achievement_hide_12");
        boolean hide13 = isAdvancementDone(serverPlayer, "achievement_hide_13");
        boolean hide14 = isAdvancementDone(serverPlayer, "achievement_hide_14");
        boolean hide15 = isAdvancementDone(serverPlayer, "achievement_hide_15");

        if (hide14 && hide15) {
            // 全部研究完成：关闭界面并补偿经验
            player.closeContainer();
            sendMessage(player, "笔记上的研究内容你都已经了解，但还是能给你带来些许启发", false);
            sendMessage(player, "经验值+50", false);
            player.giveExperiencePoints(50);
        } else if (hide14) {
            grantStudyResult(player, "dreamnotes_13");
        } else if (hide13) {
            grantStudyResult(player, "dreamnotes_12");
        } else if (hide12) {
            grantStudyResult(player, "blueprint_0");
        } else if (hide11) {
            grantStudyResult(player, "dreamnotes_11");
        } else {
            grantStudyResult(player, "dreamnotes_10");
        }
    }

    /**
     * 把研究产物写入槽 5 并提示（原版逐梯度的 setStackInSlot + 双行提示）；
     * 目标物品未注册（并行笔记模块未落地）时跳过并输出调试日志
     *
     * @param player   玩家
     * @param itemName 产物注册名
     */
    private void grantStudyResult(Player player, String itemName) {
        Item item = BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, itemName))
                .orElse(Items.AIR);
        if (item == Items.AIR) {
            PDDebugLogger.mainDebug("[ResearchTable] 研究产物 {} 未注册，跳过发放", itemName);
            return;
        }
        itemHandler.setStackInSlot(SLOT_STUDY_RESULT, new ItemStack(item));
        sendMessage(player, "残破的笔记被你重新排列，模糊的文字和图案被重新勾勒", false);
        sendMessage(player, "已获得新的寻梦者笔记", false);
    }

    /**
     * 笔与墨损耗 1 点耐久，用尽则消耗（原版 _stk.hurt 语义；
     * 当前笔与墨为无耐久注册时等效不损耗）
     */
    private void damagePen() {
        ItemStack pen = itemHandler.getStackInSlot(SLOT_PEN);
        if (!pen.isDamageableItem()) {
            return;
        }
        pen.setDamageValue(pen.getDamageValue() + 1);
        if (pen.getDamageValue() >= pen.getMaxDamage()) {
            pen.shrink(1);
            pen.setDamageValue(0);
        }
        itemHandler.setStackInSlot(SLOT_PEN, pen);
    }

    /**
     * 判断玩家是否已完成指定成就；防御性：holder 为 null 时视为未完成
     *
     * @param player 服务端玩家
     * @param path   成就注册路径
     * @return 是否已完成
     */
    private boolean isAdvancementDone(ServerPlayer player, String path) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        if (holder == null) {
            PDDebugLogger.mainDebug("[ResearchTable] 成就 {} 未注册，按未完成处理", path);
            return false;
        }
        return player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    /** 向玩家发送服务端消息（原版 displayClientMessage 语义） */
    private static void sendMessage(Player player, String message, boolean actionBar) {
        if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.literal(message), actionBar);
        }
    }

    // ==================== GeckoLib 动画 ====================

    /**
     * 动画谓词（还原原版双控制器语义）：
     * ANIMATION 方块状态为 0 时循环播放空闲动画 "0"，非 0 播放对应一次性动画
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState predicate(AnimationState<ResearchTableBlockEntity> state) {
        int anim = 0;
        if (getBlockState().getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty prop) {
            anim = getBlockState().getValue(prop);
        }
        if (anim == 0) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
        }
        return state.setAndContinue(RawAnimation.begin().thenPlay(String.valueOf(anim)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // ==================== 持久化 ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
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
        return new ResearchTableMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.research_table");
    }
}
