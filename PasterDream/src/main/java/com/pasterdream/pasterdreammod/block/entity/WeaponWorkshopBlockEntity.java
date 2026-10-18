package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.WeaponWorkshopMenu;
import com.pasterdream.pasterdreammod.recipe.WeaponWorkshopRecipe;
import com.pasterdream.pasterdreammod.recipe.WeaponWorkshopRecipeInput;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.util.WeaponWorkshopVariables;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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
 * 精铸工坊方块实体 (Weapon Workshop Block Entity)
 * <p>
 * 武器工坊群的核心工作台：7 格库存（材料槽 0-4 + 强化石槽 5 + 产物槽 6），
 * 还原原版锻造流程（WeaponWorkshopGuiPr0 / Recipe0 / Inlay0 procedure）：
 * <ol>
 *   <li>点击"锻造"按钮（菜单按钮 0）→ 授予冒险成就 → 若产物槽为空则从 RecipeManager
 *       匹配 {@code pasterdream:weapon_forge} 数据驱动配方；</li>
 *   <li>配方命中：材料槽 0-4 各扣 1，产物写入产物槽 6，置位 inlay 标记；</li>
 *   <li>1 tick 后执行镶嵌结算（Inlay0）：若槽 5 为强化石Ⅰ → 攻击伤害 +1..5；
 *       强化石Ⅱ → 幸运 +1..2 且工序归零；均消耗强化石并把强化后的原胚写回槽 6；
 *       随后播放铁砧音效并清除 inlay 标记。</li>
 * </ol>
 * 与原版一致：改名"未完工原胚（待煅烧）"仅在强化石分支写回时才会保留。
 */
public class WeaponWorkshopBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    /** 库存槽位总数（材料 0-4 + 强化石 5 + 产物 6） */
    public static final int SLOT_COUNT = 7;
    /** 强化石槽 */
    public static final int SLOT_ENHANCE = 5;
    /** 产物槽 */
    public static final int SLOT_RESULT = 6;

    /** 原胚物品标签（pasterdream:embryo_items） */
    private static final TagKey<Item> EMBRYO_ITEMS =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "embryo_items"));

    /** 锻造按钮点击时授予的冒险成就（原版 achievement_adventure_0） */
    private static final ResourceLocation ADVENTURE_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "achievement_adventure_0");

    /**
     * 锻造配方已迁移至数据包 {@code data/pasterdream/recipe/weapon_forge/}
     * （{@link WeaponWorkshopRecipe}，{@code pasterdream:weapon_forge} 类型），
     * 支持数据包覆盖与 KubeJS 增删改；本类仅保留锻造通用行为（附魔合并 / 镶嵌结算）。
     */

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 7 格库存处理器 */
    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** 镶嵌待结算标记（原版 BE 持久数据 "inlay"） */
    private boolean inlay;

    /**
     * 构造精铸工坊方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public WeaponWorkshopBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.WEAPON_WORKSHOP.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return 7 格 ItemStackHandler
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // ==================== 锻造流程（服务端） ====================

    /**
     * "锻造"按钮逻辑（原版 WeaponWorkshopGuiPr0Procedure）：
     * 授予冒险成就 → 产物槽为空时执行配方匹配，并在 1 tick 后结算镶嵌
     *
     * @param player 点击按钮的玩家
     */
    public void tryForge(Player player) {
        if (level == null || level.isClientSide()) {
            return;
        }
        grantAdventureAdvancement(player);
        if (itemHandler.getStackInSlot(SLOT_RESULT).isEmpty()) {
            matchAndCraft();
            ServerScheduler.schedule(1, this::processInlay);
        }
    }

    /**
     * 配方匹配与产出（原版 WeaponWorkshopRecipe0Procedure 的数据驱动重构）：
     * 以材料槽 0-4 构建输入视图，从 RecipeManager 查询 {@code pasterdream:weapon_forge}
     * 配方；命中后各扣 1 个材料并把产物写入槽 6、置位 inlay
     */
    private void matchAndCraft() {
        if (this.level == null) {
            return;
        }
        WeaponWorkshopRecipeInput input = WeaponWorkshopRecipeInput.of(itemHandler);
        this.level.getRecipeManager()
                .getRecipeFor(PDRecipeTypes.WEAPON_FORGE.get(), input, this.level)
                .ifPresent(holder -> craft(holder.value()));
    }

    /**
     * 执行锻造产出（配方命中后的通用行为）：
     * 先于扣减合并输入附魔（同名取最高等级）到产物，再扣料、写产物槽 6、置位 inlay
     *
     * @param recipe 命中的锻造配方
     */
    private void craft(WeaponWorkshopRecipe recipe) {
        // 调用链保证 level 非空（matchAndCraft 已检查）
        ItemStack result = recipe.assemble(
                WeaponWorkshopRecipeInput.of(itemHandler),
                this.level.registryAccess());
        if (result.isEmpty()) {
            // 防御性：产物为空的配方不产出（与原版跳过 AIR 产物配方等价）
            return;
        }
        // 继承主材料槽（槽 0）全部物品组件——耐久损耗/自定义数据/其他模组词条等，
        // 升级语义下数据不丢失（1.21.1 模组词条普遍走 DataComponents，组件继承即通用兼容）；
        // 附魔随后由下方合并循环按全槽最高等级覆盖，不依赖此处
        result.applyComponents(itemHandler.getStackInSlot(0).getComponentsPatch());
        // 合并输入附魔到产物（同名取最高等级）
        ItemEnchantments.Mutable merged = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (int slot = 0; slot <= 4; slot++) {
            ItemEnchantments ench =
                    EnchantmentHelper.getEnchantmentsForCrafting(itemHandler.getStackInSlot(slot));
            for (var entry : ench.entrySet()) {
                Holder<Enchantment> holder = entry.getKey();
                merged.set(holder, Math.max(merged.getLevel(holder), entry.getIntValue()));
            }
        }
        ItemEnchantments mergedEnchantments = merged.toImmutable();
        if (!mergedEnchantments.isEmpty()) {
            EnchantmentHelper.setEnchantments(result, mergedEnchantments);
        }
        for (int slot = 0; slot <= 4; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            stack.shrink(1);
            itemHandler.setStackInSlot(slot, stack);
        }
        itemHandler.setStackInSlot(SLOT_RESULT, result);
        this.inlay = true;
        setChanged();
        syncToClient();
    }

    /**
     * 镶嵌结算（原版 WeaponWorkshopInlay0Procedure）：
     * inlay 置位时读取产物槽副本 → 原胚改名 → 按槽 5 强化石施加属性并写回 →
     * 播放铁砧音效 → 清除 inlay
     */
    public void processInlay() {
        if (level == null || level.isClientSide() || !this.inlay) {
            return;
        }
        ItemStack stash = itemHandler.getStackInSlot(SLOT_RESULT).copy();
        WeaponWorkshopVariables.weaponWorkshopItem = stash;
        if (stash.is(EMBRYO_ITEMS)) {
            stash.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                    Component.translatable("message.pasterdream.embryo.pending_calcine"));
        }
        ItemStack enhanceStone = itemHandler.getStackInSlot(SLOT_ENHANCE);
        if (enhanceStone.is(PDItems.ENHANCE_STONE_0.get().asItem())) {
            // 强化石Ⅰ：攻击伤害 +1..5
            PasterItemData.putBoolean(stash, "paster_attack_damage", true);
            PasterItemData.putDouble(stash, "paster_attack_damage_number",
                    PasterItemData.getDouble(stash, "paster_attack_damage_number")
                            + net.minecraft.util.Mth.nextInt(level.getRandom(), 1, 5));
            consumeEnhanceStoneAndStore(stash);
        } else if (enhanceStone.is(PDItems.ENHANCE_STONE_1.get().asItem())) {
            // 强化石Ⅱ：幸运 +1..2，工序归零
            PasterItemData.putBoolean(stash, "paster_luck", true);
            PasterItemData.putDouble(stash, "paster_luck_number",
                    PasterItemData.getDouble(stash, "paster_luck_number")
                            + net.minecraft.util.Mth.nextInt(level.getRandom(), 1, 2));
            consumeEnhanceStoneAndStore(stash);
            PasterItemData.putDouble(stash, "process", 0);
        }
        level.playSound(null, worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.1f, 1.0f);
        this.inlay = false;
        setChanged();
        syncToClient();
    }

    /**
     * 消耗 1 个强化石并把强化后的原胚写回产物槽（原版逐槽写回逻辑）
     *
     * @param stash 强化后的原胚
     */
    private void consumeEnhanceStoneAndStore(ItemStack stash) {
        ItemStack stone = itemHandler.getStackInSlot(SLOT_ENHANCE);
        stone.shrink(1);
        itemHandler.setStackInSlot(SLOT_ENHANCE, stone);
        ItemStack result = stash.copy();
        result.setCount(1);
        itemHandler.setStackInSlot(SLOT_RESULT, result);
    }

    /**
     * 授予冒险成就 achievement_adventure_0（原版按钮附带逻辑）；
     * 防御性：holder 缺失时输出调试日志并跳过
     *
     * @param player 玩家
     */
    private void grantAdventureAdvancement(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        AdvancementHolder holder = serverPlayer.server.getAdvancements().get(ADVENTURE_ADVANCEMENT);
        if (holder == null) {
            PDDebugLogger.mainDebug("[WeaponWorkshop] 成就 {} 未注册，跳过授予", ADVENTURE_ADVANCEMENT);
            return;
        }
        AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(holder);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                serverPlayer.getAdvancements().award(holder, criterion);
            }
        }
    }

    /** 同步方块实体数据到客户端（等价原版 sendBlockUpdated） */
    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
    private PlayState predicate(AnimationState<WeaponWorkshopBlockEntity> state) {
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
        tag.putBoolean("inlay", inlay);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        inlay = tag.getBoolean("inlay");
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
        return new WeaponWorkshopMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.weapon_workshop");
    }
}
