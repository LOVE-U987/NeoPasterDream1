package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.WorkshopAnvilMenu;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import com.pasterdream.pasterdreammod.util.WeaponWorkshopVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 工坊铁砧方块实体 (Workshop Anvil Block Entity)
 * <p>
 * 锤炼小游戏（原版 WorkshopAnvilPr0/Pr1 + NbtNumberButton1-5 procedure）：
 * <ol>
 *   <li>槽 0 放入工序 1（待锤炼）的原胚，点击"开始锻造"→ 吸入原胚、
 *       抽取随机目标数字 1..5（switch 置位）；</li>
 *   <li>GUI 顶部亮起目标数字指示，玩家点击对应数字按钮：命中 +1 分（升级音效），
 *       按错按钮 1 扣 1 分、按钮 2-5 扣 2 分（破碎音效），随后重抽数字；</li>
 *   <li>方块每 10 tick 推进 game 计数，累计 16 次（约 8 秒）结算：
 *       积分封顶 20 → 攻击伤害/幸运数值 ×(积分×0.1) → 工序置 2、
 *       改名"未完成原胚（待淬火）"，写入产物槽 1，状态复位。</li>
 * </ol>
 * 新版增强：在制原胚持久化到 BE NBT（"PendingItem"），服务器重启不丢失
 * （原版存于全局静态变量，重启即失）；全局暂存变量仍按原版时机同步。
 */
public class WorkshopAnvilBlockEntity extends BlockEntity implements MenuProvider {

    /** 原胚输入槽 */
    public static final int SLOT_INPUT = 0;
    /** 产物槽 */
    public static final int SLOT_RESULT = 1;
    /** 结算所需 game 计数（每 10 tick +1，16 次后结算） */
    private static final int GAME_ROUNDS = 16;
    /** 积分上限 */
    private static final double SCORE_CAP = 20;

    /** 原胚物品标签 */
    private static final TagKey<Item> EMBRYO_ITEMS =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "embryo_items"));

    /** 2 格库存（0 输入 / 1 产出） */
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** 小游戏进行中标记（原版持久数据 "switch"） */
    private boolean switchOn;
    /** 当前目标数字 1..5（原版持久数据 "number"） */
    private int number;
    /** 当前积分（原版持久数据 "score"，可为负） */
    private double score;
    /** 结算计数（原版持久数据 "game"） */
    private double game;
    /** 在制原胚（原版存于全局静态，新版随 BE 持久化防重启丢失） */
    private ItemStack pendingItem = ItemStack.EMPTY;

    /**
     * 构造工坊铁砧方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public WorkshopAnvilBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.WORKSHOP_ANVIL.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return 2 格 ItemStackHandler
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * 获取当前目标数字（菜单 DataSlot 读取）
     *
     * @return 目标数字 1..5，未开始为 0
     */
    public int getNumber() {
        return number;
    }

    /**
     * 获取当前积分（菜单 DataSlot 读取）
     *
     * @return 积分（原版可为负）
     */
    public int getScore() {
        return (int) score;
    }

    /**
     * 小游戏是否进行中
     *
     * @return switch 状态
     */
    public boolean isSwitchOn() {
        return switchOn;
    }

    // ==================== 小游戏逻辑（服务端） ====================

    /**
     * "开始锻造"按钮（原版 WorkshopAnvilPr0Procedure）：
     * 未进行中 + 槽 0 为原胚 + 槽 1 空 + 工序==1 时启动小游戏
     */
    public void startGame() {
        if (level == null || level.isClientSide() || switchOn) {
            return;
        }
        ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);
        if (!input.is(EMBRYO_ITEMS)
                || !itemHandler.getStackInSlot(SLOT_RESULT).isEmpty()
                || PasterItemData.getDouble(input, "process") != 1) {
            return;
        }
        switchOn = true;
        pendingItem = input.copy();
        pendingItem.setCount(1);
        WeaponWorkshopVariables.weaponWorkshopItem = pendingItem;
        input.shrink(1);
        itemHandler.setStackInSlot(SLOT_INPUT, input);
        number = Mth.nextInt(level.getRandom(), 1, 5);
        setChanged();
        syncToClient();
    }

    /**
     * 数字按钮点击（原版 NbtNumberButton1-5Procedure）：
     * 命中目标数字 +1 分（升级音效），按错按钮 1 扣 1 分、按钮 2-5 扣 2 分（破碎音效），
     * 随后重抽目标数字
     *
     * @param button 按钮编号 1..5
     */
    public void pressNumber(int button) {
        if (level == null || level.isClientSide() || !switchOn || button < 1 || button > 5) {
            return;
        }
        if (number == button) {
            score += 1;
            level.playSound(null, worldPosition, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0f, 1.0f);
        } else {
            // 原版惩罚值：按钮 1 扣 1 分，按钮 2-5 扣 2 分
            score -= (button == 1 ? 1 : 2);
            level.playSound(null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        number = Mth.nextInt(level.getRandom(), 1, 5);
        setChanged();
        syncToClient();
    }

    /**
     * 周期推进（原版 WorkshopAnvilPr1Procedure，由方块每 10 tick 调度）：
     * switch 置位时 game+1；累计 16 次后结算强化并复位
     */
    public void tickGame() {
        if (level == null || level.isClientSide() || !switchOn) {
            return;
        }
        game += 1;
        if (game < GAME_ROUNDS) {
            setChanged();
            return;
        }
        // 结算：积分封顶 → 属性乘算 → 工序推进 → 产出写入槽 1
        game = 0;
        number = 0;
        if (score >= SCORE_CAP) {
            score = SCORE_CAP;
        }
        switchOn = false;
        ItemStack result = pendingItem.isEmpty() ? ItemStack.EMPTY : pendingItem.copy();
        if (!result.isEmpty()) {
            WeaponWorkshopVariables.weaponWorkshopItem = result;
            PasterItemData.putDouble(result, "paster_attack_damage_number",
                    PasterItemData.getDouble(result, "paster_attack_damage_number") * score * 0.1);
            PasterItemData.putDouble(result, "paster_luck_number",
                    PasterItemData.getDouble(result, "paster_luck_number") * score * 0.1);
            PasterItemData.putDouble(result, "process", 2);
            result.set(DataComponents.CUSTOM_NAME, Component.literal("未完成原胚（待淬火）"));
            result.setCount(1);
            itemHandler.setStackInSlot(SLOT_RESULT, result);
        }
        pendingItem = ItemStack.EMPTY;
        score = 0;
        setChanged();
        syncToClient();
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
        tag.putBoolean("switch", switchOn);
        tag.putDouble("number", number);
        tag.putDouble("score", score);
        tag.putDouble("game", game);
        tag.put("PendingItem", pendingItem.saveOptional(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        switchOn = tag.getBoolean("switch");
        number = (int) tag.getDouble("number");
        score = tag.getDouble("score");
        game = tag.getDouble("game");
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
        return new WorkshopAnvilMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.workshop_anvil");
    }
}
