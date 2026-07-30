package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.WeaponWorkshopMenu;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.util.WeaponWorkshopVariables;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
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

import java.util.List;
import java.util.function.Supplier;

/**
 * 精铸工坊方块实体 (Weapon Workshop Block Entity)
 * <p>
 * 武器工坊群的核心工作台：7 格库存（材料槽 0-4 + 强化石槽 5 + 产物槽 6），
 * 还原原版锻造流程（WeaponWorkshopGuiPr0 / Recipe0 / Inlay0 procedure）：
 * <ol>
 *   <li>点击"锻造"按钮（菜单按钮 0）→ 授予冒险成就 → 若产物槽为空则匹配 13 项锻造配方；</li>
 *   <li>配方命中：材料槽 0-4 各扣 1，原胚写入产物槽 6，置位 inlay 标记；</li>
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
     * 锻造配方（与原版 WeaponWorkshopRecipe0Procedure 完全一致）：
     * 槽 0-4 按序匹配 5 种材料，产出 1 个原胚/成品到槽 6。
     */
    private record ForgeRecipe(Supplier<Item> in0, Supplier<Item> in1, Supplier<Item> in2,
                               Supplier<Item> in3, Supplier<Item> in4, Supplier<Item> result) {
    }

    /** 全部 13 项原版锻造配方 */
    private static final List<ForgeRecipe> RECIPES = List.of(
            // 融梦水晶 + 钛锭 + 黑金属棒 + 染梦尘 + 染梦石英 → 聚梦法杖原胚
            new ForgeRecipe(() -> PDItems.MELTDREAM_CRYSTAL_0.get().asItem(), () -> PDItems.TITANIUM_INGOT.get().asItem(),
                    () -> PDItems.BLACKSTICK.get().asItem(), () -> PDItems.DYEDREAM_DUST.get().asItem(),
                    () -> PDItems.DYEDREAMQUARTZ.get().asItem(), () -> PDItems.DREAM_WAND_EMBRYO.get().asItem()),
            // 铁剑 + 黑金属锭 + 梦魇燃料 + 哭泣的黑曜石 + 黑金属棒 → 蚀影剑原胚
            new ForgeRecipe(() -> Items.IRON_SWORD, () -> PDItems.BLACKMETAL_INGOT.get().asItem(),
                    () -> PDItems.NIGHTMARE_FUEL.get().asItem(), () -> Items.CRYING_OBSIDIAN,
                    () -> PDItems.BLACKSTICK.get().asItem(), () -> PDItems.SHADOW_EROSION_SWORD_EMBRYO.get().asItem()),
            // 铁镐系列同理 → 蚀影镐原胚
            new ForgeRecipe(() -> Items.IRON_PICKAXE, () -> PDItems.BLACKMETAL_INGOT.get().asItem(),
                    () -> PDItems.NIGHTMARE_FUEL.get().asItem(), () -> Items.CRYING_OBSIDIAN,
                    () -> PDItems.BLACKSTICK.get().asItem(), () -> PDItems.SHADOW_EROSION_PICKAXE_EMBRYO.get().asItem()),
            // 铁斧 → 蚀影斧原胚
            new ForgeRecipe(() -> Items.IRON_AXE, () -> PDItems.BLACKMETAL_INGOT.get().asItem(),
                    () -> PDItems.NIGHTMARE_FUEL.get().asItem(), () -> Items.CRYING_OBSIDIAN,
                    () -> PDItems.BLACKSTICK.get().asItem(), () -> PDItems.SHADOW_EROSION_AXE_EMBRYO.get().asItem()),
            // 铁锹 → 蚀影锹原胚
            new ForgeRecipe(() -> Items.IRON_SHOVEL, () -> PDItems.BLACKMETAL_INGOT.get().asItem(),
                    () -> PDItems.NIGHTMARE_FUEL.get().asItem(), () -> Items.CRYING_OBSIDIAN,
                    () -> PDItems.BLACKSTICK.get().asItem(), () -> PDItems.SHADOW_EROSION_SHOVEL_EMBRYO.get().asItem()),
            // 铁锄 → 蚀影锄原胚
            new ForgeRecipe(() -> Items.IRON_HOE, () -> PDItems.BLACKMETAL_INGOT.get().asItem(),
                    () -> PDItems.NIGHTMARE_FUEL.get().asItem(), () -> Items.CRYING_OBSIDIAN,
                    () -> PDItems.BLACKSTICK.get().asItem(), () -> PDItems.SHADOW_EROSION_HOE_EMBRYO.get().asItem()),
            // 五真剑合一 → 泰拉之刃原胚
            new ForgeRecipe(() -> PDItems.TRUEST_MOLTENGOLD_SWORD.get().asItem(), () -> PDItems.TRUE_GRASS_SWORD.get().asItem(),
                    () -> PDItems.TRUE_TIDE_SWORD.get().asItem(), () -> PDItems.TRUE_DESERT_SWORD.get().asItem(),
                    () -> PDItems.BROKEN_HERO_SWORD.get().asItem(), () -> PDItems.TERRASWORD_EMBRYO.get().asItem()),
            // 钛剑 + 影灯 + 白水晶 + 下界之星 + 融梦水晶 → 白刃原胚
            new ForgeRecipe(() -> PDItems.TITANIUM_SWORD.get().asItem(), () -> PDBlocks.SHADOW_LIGHT_0.get().asItem(),
                    () -> PDItems.WHITE_CRYSTAL.get().asItem(), () -> Items.NETHER_STAR,
                    () -> PDItems.MELTDREAM_CRYSTAL_0.get().asItem(), () -> PDItems.WHITE_SWORD_EMBRYO.get().asItem()),
            // 蚀影剑 + 梦魇燃料 + 暗影剑柄 + 黑金属锭 + 羊皮纸 → 暗影剑原胚
            new ForgeRecipe(() -> PDItems.SHADOW_EROSION_SWORD.get().asItem(), () -> PDItems.NIGHTMARE_FUEL.get().asItem(),
                    () -> PDItems.SHADOW_HILT.get().asItem(), () -> PDItems.BLACKMETAL_INGOT.get().asItem(),
                    () -> PDItems.PERGAMYN.get().asItem(), () -> PDItems.SHADOW_SWORD_EMBRYO.get().asItem()),
            // 钓鱼竿 + 细雪桶 + 钻石 + 融梦水晶 + 雪誓头冠 → 占星者的祈愿原胚
            new ForgeRecipe(() -> Items.FISHING_ROD, () -> Items.POWDER_SNOW_BUCKET,
                    () -> Items.DIAMOND, () -> PDItems.MELTDREAM_CRYSTAL_0.get().asItem(),
                    () -> PDItems.SNOW_VOW_HEAD.get().asItem(), () -> PDItems.STAR_WISH_ROD_EMBRYO.get().asItem()),
            // 书 + 梦魇燃料 + 黑金属锭 + 纯粹恐惧 + 笔与墨 → 暗影旋涡（直接成品）
            new ForgeRecipe(() -> Items.BOOK, () -> PDItems.NIGHTMARE_FUEL.get().asItem(),
                    () -> PDItems.BLACKMETAL_INGOT.get().asItem(), () -> PDItems.PURE_HORROR.get().asItem(),
                    () -> PDItems.PEN_AND_INK.get().asItem(), () -> lookupItem("shadow_vortex_book")),
            // 钻石 + 蓝冰 + 黑金属锭 + 黑金属棒 + 纯粹恐惧 → 冰影锤原胚
            new ForgeRecipe(() -> Items.DIAMOND, () -> Items.BLUE_ICE,
                    () -> PDItems.BLACKMETAL_INGOT.get().asItem(), () -> PDItems.BLACKSTICK.get().asItem(),
                    () -> PDItems.PURE_HORROR.get().asItem(), () -> PDItems.ICESHADOW_HAMMER_EMBRYO.get().asItem()),
            // 银铃 + 染梦锭 + 线 + 染梦木板 + 白水晶 → 草莓之心（直接成品）
            new ForgeRecipe(() -> PDItems.SILVER_BELL.get().asItem(), () -> PDItems.DYEDREAM_INGOT.get().asItem(),
                    () -> Items.STRING, () -> PDBlocks.DYEDREAM_PLANKS.get().asItem(),
                    () -> PDItems.WHITE_CRYSTAL.get().asItem(), () -> PDItems.STRAWBERRY_HEART.get().asItem())
    );

    /**
     * 按注册名查找本模组物品（跨模块松耦合；未注册时返回 AIR，
     * 配方匹配阶段会跳过 AIR 产物的配方）
     *
     * @param path 注册路径
     * @return 物品（未注册返回 {@link Items#AIR}）
     */
    private static net.minecraft.world.item.Item lookupItem(String path) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path))
                .orElse(Items.AIR);
    }

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
     * 配方匹配与产出（原版 WeaponWorkshopRecipe0Procedure）：
     * 槽 0-4 按序匹配，命中后各扣 1 个材料并把产物写入槽 6、置位 inlay
     */
    private void matchAndCraft() {
        for (ForgeRecipe recipe : RECIPES) {
            if (recipe.result().get() == Items.AIR) {
                // 防御性：产物 Supplier 解析为 AIR 时跳过该配方
                continue;
            }
            if (itemHandler.getStackInSlot(0).is(recipe.in0().get())
                    && itemHandler.getStackInSlot(1).is(recipe.in1().get())
                    && itemHandler.getStackInSlot(2).is(recipe.in2().get())
                    && itemHandler.getStackInSlot(3).is(recipe.in3().get())
                    && itemHandler.getStackInSlot(4).is(recipe.in4().get())) {
                for (int slot = 0; slot <= 4; slot++) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    stack.shrink(1);
                    itemHandler.setStackInSlot(slot, stack);
                }
                itemHandler.setStackInSlot(SLOT_RESULT, new ItemStack(recipe.result().get()));
                this.inlay = true;
                setChanged();
                syncToClient();
                // 原版逐配方独立 if 判定，材料被扣除后后续配方不再可能命中，等价 break
                return;
            }
        }
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
                    Component.literal("未完工原胚（待煅烧）"));
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
            PasterDreamMod.LOGGER.debug("[WeaponWorkshop] 成就 {} 未注册，跳过授予", ADVENTURE_ADVANCEMENT);
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
