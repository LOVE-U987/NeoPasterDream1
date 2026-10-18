package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.recipe.DreamBrewingRecipe;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDFluids;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import com.pasterdream.pasterdreammod.util.DreamPotionEnhancer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 梦境炼药锅方块实体 (Dream Cauldron Block Entity)
 * 类原版酿造台的双通道自动酿造：7 格库存 + 2000mB 融梦液体储罐。
 *
 * 槽位说明：
 * - 索引 0：通用材料槽（药水酿造材料 / 法术配方的引导药剂）
 * - 索引 1-3：药水瓶槽（放水瓶/任意药水；兼作法术配方材料位）
 * - 索引 4：融梦液体桶输入槽（放入后自动注入 1000mB 并退还空桶到槽 5）
 * - 索引 5：空桶回收槽
 * - 索引 6：成品槽（法术配方产物写入此处，仅可取出）
 *
 * 自动酿造流程（服务端 tick 状态机，无需按钮）：
 * 空闲时每 tick 判定，优先匹配法术配方，否则走通用药水酿造——
 * <ol>
 *   <li><b>法术酿造</b>（时长由数据包配方 craft_tick 决定，默认 60 tick，
 *       时序语义与原版 procedure 一致）：
 *       槽 0=引导药剂 + 槽 1-3 按序匹配 dream_brewing 数据包配方
 *       （{@link DreamBrewingRecipe}，支持数据包覆盖与 KubeJS 增删改），
 *       槽 6 必须为空。t=0 扣除 100mB 并触发炼制动画；t=20/40 气泡反馈；
 *       末前第 2 tick 消耗槽 0-3 各 1 个；末前第 1 tick 产物写入槽 6；
 *       末 tick 酿造音效+粒子并结束。</li>
 *   <li><b>药水酿造</b>（400 tick，与原版酿造台一致）：
 *       通过 {@code Level#potionBrewing()} 判定与执行——
 *       材料槽 {@code isIngredient} + 任一瓶槽 {@code hasMix}。
 *       该实例已被 NeoForge patch，自动涵盖原版全部配方与所有模组
 *       注册的酿造配方（IBrewingRecipe），无需逐一适配。
 *       完成时每瓶 {@code mix()} 产出并经 {@link DreamPotionEnhancer} 梦之强化
 *       （所有效果等级 +1、持续型效果额外时长 x1.5）后替换瓶内物品，
 *       材料消耗 1 个并处理余留物（与原版酿造台一致）。</li>
 *   <li><b>药水合并</b>（80 tick，优先于酿造，要求成品槽 6 为空）：瓶槽 ≥2 个
 *       有效药水即触发，<b>合并与容器转换一同完成</b>——槽 0 材料可与药水混合时
 *       先 {@code mix()} 转换（+火药→喷溅、+红石→长时效）再合并；相同药水每瓶
 *       等级 +1、时长 ×瓶数；不同药水效果并集（同效果取等级高/时长长者）；
 *       产物写入成品槽 6（参与瓶槽全部消耗），不再叠加梦之强化；
 *       效果操作基于 PotionContents 组件，模组药水自动兼容。</li>
 * </ol>
 *
 * 中断规则（与原版酿造台一致）：酿造中材料被拿走、被替换或不再满足
 * 可酿造条件时，酿造进度重置。药水酿造不持久化材料物品引用，
 * 存档重载后若材料未变化会继续酿造，否则自然中断。
 *
 * 动画说明（animations/block/dream_cauldron.animation.json）：
 * - "0"：空闲循环动画
 * - "1"：炼制动画（3 秒一次性，triggerAnim 触发）
 */
public class DreamCauldronBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    /** 库存槽位总数 */
    private static final int SLOT_COUNT = 7;
    /** 通用材料槽（酿造材料 / 法术配方引导药剂） */
    private static final int SLOT_MATERIAL = 0;
    /** 药水瓶槽起始索引（1-3） */
    private static final int SLOT_BOTTLE_FIRST = 1;
    /** 药水瓶槽结束索引（含） */
    private static final int SLOT_BOTTLE_LAST = 3;
    /** 液体桶输入槽 */
    private static final int SLOT_BUCKET_IN = 4;
    /** 空桶回收槽 */
    private static final int SLOT_BUCKET_OUT = 5;
    /** 成品槽（法术配方产物） */
    private static final int SLOT_RESULT = 6;

    /** 储罐容量（mB，与原版一致） */
    private static final int TANK_CAPACITY = 2000;
    /** 每桶注入量（mB） */
    private static final int BUCKET_FILL_AMOUNT = 1000;
    /** 每次酿造消耗液体量（mB，与原版一致） */
    private static final int CRAFT_FLUID_COST = 100;

    /**
     * 法术酿造默认总时长（tick，对应 3 秒炼制动画）。
     * 实际时长优先取配方 JSON 的 {@code craft_tick} 字段（DreamBrewingRecipe），
     * 本常量仅在配方暂未匹配（如旧存档恢复中）时作为回退值。
     */
    private static final int SPELL_CRAFT_DURATION = 60;

    /** 药水酿造总时长（tick，与原版酿造台 20 秒一致） */
    private static final int POTION_BREW_DURATION = 400;
    /** 药水合并酿造总时长（tick，轻量操作快于药水酿造） */
    private static final int MERGE_BREW_DURATION = 80;
    /** 酿造期间气泡反馈周期（tick） */
    private static final int POTION_BUBBLE_INTERVAL = 40;
    /**
     * 酿造动画重触发周期（tick，略短于 3 秒炼制动画以保证视觉连续）。
     * "1" 炼制动画为 3 秒一次性播放：法术酿造（60 tick）恰好覆盖，
     * 药水酿造（400 tick）与合并（80 tick）需周期性重触发，避免中途回落空闲动画。
     */
    private static final int BREW_ANIM_RETRIGGER_INTERVAL = 55;

    /**
     * 酿造通道类型
     */
    private enum BrewKind {
        /** 法术配方酿造（引导药剂 + 三材料） */
        SPELL,
        /** 通用药水酿造（原版 + 模组配方自动兼容） */
        POTION,
        /** 药水合并（相同药水叠加等级/时长，不同药水合并效果） */
        MERGE
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 7 格库存处理器（0 材料，1-3 瓶槽，4 桶入，5 桶出，6 成品） */
    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** 融梦液体储罐（2000mB，仅接受融梦液体，与原版一致） */
    private final FluidTank fluidTank = new FluidTank(TANK_CAPACITY,
            fs -> fs.getFluid() == PDFluids.MELTDREAM_LIQUID.get()) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    /** 酿造进度计数器：-1 = 空闲（空闲时每 tick 尝试自动启动酿造），0..时长 = 酿造中 */
    private int craftTicks = -1;

    /** 当前酿造通道（仅 craftTicks >= 0 时有效） */
    private BrewKind brewKind = BrewKind.SPELL;

    /** 法术酿造的产物（配方匹配时缓存，t=59 写入成品槽；药水酿造恒为空） */
    private ItemStack pendingResult = ItemStack.EMPTY;

    /** 法术酿造当前匹配的数据包配方（null = 未确定，重验证时自愈；不持久化） */
    @Nullable
    private RecipeHolder<DreamBrewingRecipe> activeRecipe;

    /** 药水酿造开始时的材料物品（用于检测材料被替换；不持久化，与原版酿造台一致） */
    @Nullable
    private Item activeIngredient;

    /**
     * 构造梦境炼药锅方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public DreamCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.DREAM_CAULDRON.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return ItemStackHandler 实例（7 格，槽位含义见类注释）
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * 获取储罐当前液体量
     *
     * @return 液体量（mB）
     */
    public int getFluidAmount() {
        return fluidTank.getFluidAmount();
    }

    /**
     * 是否处于酿造中
     *
     * @return 酿造中返回 true
     */
    public boolean isBrewing() {
        return craftTicks >= 0;
    }

    /**
     * 获取已酿造时长（GUI 进度条用）
     *
     * @return 已酿造 tick 数（空闲时为 0）
     */
    public int getBrewTimeElapsed() {
        return Math.max(craftTicks, 0);
    }

    /**
     * 获取当前酿造总时长（GUI 进度条用）
     *
     * @return 酿造总 tick 数（空闲时为 0）
     */
    public int getBrewDuration() {
        if (craftTicks < 0) {
            return 0;
        }
        if (brewKind == BrewKind.SPELL) {
            // 法术时长优先取配方 craft_tick（旧存档恢复中配方未匹配时用默认值）
            return activeRecipe != null ? activeRecipe.value().getCraftTick() : SPELL_CRAFT_DURATION;
        }
        if (brewKind == BrewKind.MERGE) {
            return MERGE_BREW_DURATION;
        }
        return POTION_BREW_DURATION;
    }

    // ==================== 炼制逻辑（服务端） ====================

    /**
     * 服务端每 tick 驱动：处理液体桶注入与自动酿造状态机
     */
    public void serverTick() {
        processBucketSlot();
        tickCrafting();
    }

    /**
     * 处理液体桶输入槽：融梦液体桶 → 注入 1000mB + 空桶移入回收槽（与原版一致）
     * 回收槽已满（空桶堆叠上限 16）时暂不处理，桶保留在输入槽。
     */
    private void processBucketSlot() {
        ItemStack bucketIn = itemHandler.getStackInSlot(SLOT_BUCKET_IN);
        if (bucketIn.isEmpty() || !bucketIn.is(PDItems.MELTDREAM_LIQUID_BUCKET.get())) {
            return;
        }
        // 储罐需能完整容纳一桶
        if (fluidTank.getFluidAmount() + BUCKET_FILL_AMOUNT > TANK_CAPACITY) {
            return;
        }
        // 空桶回收槽必须能再放入一个空桶
        ItemStack bucketOut = itemHandler.getStackInSlot(SLOT_BUCKET_OUT);
        if (!bucketOut.isEmpty()
                && (!bucketOut.is(Items.BUCKET) || bucketOut.getCount() >= bucketOut.getMaxStackSize())) {
            return;
        }

        fluidTank.fill(new FluidStack(PDFluids.MELTDREAM_LIQUID.get(), BUCKET_FILL_AMOUNT),
                IFluidHandler.FluidAction.EXECUTE);
        bucketIn.shrink(1);
        itemHandler.setStackInSlot(SLOT_BUCKET_IN, bucketIn);
        if (bucketOut.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_BUCKET_OUT, new ItemStack(Items.BUCKET));
        } else {
            bucketOut.grow(1);
            itemHandler.setStackInSlot(SLOT_BUCKET_OUT, bucketOut);
        }
        playSound(SoundEvents.BUCKET_FILL);
    }

    // ==================== 自动酿造判定与启动 ====================

    /**
     * 空闲时每 tick 尝试自动启动酿造（双通道，法术配方优先）：
     * 前提为液体量充足；法术配方需要成品槽为空（产物去处）。
     */
    private void tryStartAutoBrew() {
        if (fluidTank.getFluidAmount() < CRAFT_FLUID_COST) {
            return;
        }
        RecipeHolder<DreamBrewingRecipe> spellRecipe = matchSpellRecipe();
        if (spellRecipe != null) {
            if (!itemHandler.getStackInSlot(SLOT_RESULT).isEmpty()) {
                return;
            }
            startSpellCraft(spellRecipe);
            return;
        }
        // 合并通道（优先于酿造）：瓶槽 ≥2 药水即触发，完成时可顺带容器转换（材料在槽 0 时，
        // "合并+转换一同完成"）；产物写入成品槽 6，故要求槽 6 为空
        if (isMergeable() && itemHandler.getStackInSlot(SLOT_RESULT).isEmpty()) {
            startMergeBrew();
            return;
        }
        if (isPotionBrewable()) {
            startPotionBrew();
        }
    }

    /**
     * 匹配法术配方：从 RecipeManager 查询全部 dream_brewing 数据包配方
     * （支持数据包覆盖与 KubeJS 增删改），槽 0 = 引导药剂，槽 1-3 = 按序材料
     *
     * @return 匹配的配方 Holder，无匹配返回 null
     */
    @Nullable
    private RecipeHolder<DreamBrewingRecipe> matchSpellRecipe() {
        if (level == null) {
            return null;
        }
        DreamBrewingRecipe.DreamBrewingInput input = new DreamBrewingRecipe.DreamBrewingInput(
                itemHandler.getStackInSlot(SLOT_MATERIAL),
                itemHandler.getStackInSlot(1),
                itemHandler.getStackInSlot(2),
                itemHandler.getStackInSlot(3));
        for (RecipeHolder<DreamBrewingRecipe> holder
                : level.getRecipeManager().getAllRecipesFor(PDRecipeTypes.DREAM_BREWING.get())) {
            if (holder.value().matches(input, level)) {
                return holder;
            }
        }
        return null;
    }

    /**
     * 判定当前是否可进行通用药水酿造（原版 + 模组配方自动兼容）：
     * 材料槽物品是合法酿造材料，且任一瓶槽物品可被其混合。
     * 通过 {@code Level#potionBrewing()} 查询——该实例由 NeoForge patch，
     * 自动涵盖所有模组注册的酿造配方。
     *
     * @return 可酿造返回 true
     */
    private boolean isPotionBrewable() {
        if (level == null) {
            return false;
        }
        PotionBrewing brewing = level.potionBrewing();
        ItemStack material = itemHandler.getStackInSlot(SLOT_MATERIAL);
        if (material.isEmpty() || !brewing.isIngredient(material)) {
            return false;
        }
        for (int slot = SLOT_BOTTLE_FIRST; slot <= SLOT_BOTTLE_LAST; slot++) {
            ItemStack bottle = itemHandler.getStackInSlot(slot);
            if (!bottle.isEmpty() && brewing.hasMix(bottle, material)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 启动法术酿造：扣除液体、缓存产物、触发炼制动画与首轮反馈
     *
     * @param recipe 已匹配的数据包配方 Holder
     */
    private void startSpellCraft(RecipeHolder<DreamBrewingRecipe> recipe) {
        fluidTank.drain(CRAFT_FLUID_COST, IFluidHandler.FluidAction.EXECUTE);
        pendingResult = recipe.value().getResultItem(level.registryAccess()).copy();
        activeRecipe = recipe;
        brewKind = BrewKind.SPELL;
        craftTicks = 0;
        triggerAnim("controller", "craft");
        playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
        spawnCraftParticles(16, 12, false);
        setChanged();
    }

    /**
     * 启动药水酿造：扣除液体、记录材料物品、触发炼制动画与首轮反馈
     */
    private void startPotionBrew() {
        fluidTank.drain(CRAFT_FLUID_COST, IFluidHandler.FluidAction.EXECUTE);
        activeIngredient = itemHandler.getStackInSlot(SLOT_MATERIAL).getItem();
        activeRecipe = null;
        pendingResult = ItemStack.EMPTY;
        brewKind = BrewKind.POTION;
        craftTicks = 0;
        triggerAnim("controller", "craft");
        playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
        spawnCraftParticles(16, 12, false);
        setChanged();
    }

    // ==================== 药水合并 ====================

    /**
     * 判定瓶槽是否满足药水合并条件：≥2 个有效药水（携带非空效果）
     *
     * @return 可合并返回 true
     */
    private boolean isMergeable() {
        int count = 0;
        for (int slot = SLOT_BOTTLE_FIRST; slot <= SLOT_BOTTLE_LAST; slot++) {
            if (isMergeablePotion(itemHandler.getStackInSlot(slot))) {
                count++;
            }
        }
        return count >= 2;
    }

    /**
     * 判定物品是否为可参与合并的药水（携带 POTION_CONTENTS 且非空效果，模组药水自动兼容）
     *
     * @param stack 待判定物品
     * @return 可参与合并返回 true
     */
    private static boolean isMergeablePotion(ItemStack stack) {
        if (stack.isEmpty() || !stack.has(DataComponents.POTION_CONTENTS)) {
            return false;
        }
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.hasEffects();
    }

    /**
     * 启动药水合并酿造：扣除液体、触发炼制动画与首轮反馈
     */
    private void startMergeBrew() {
        fluidTank.drain(CRAFT_FLUID_COST, IFluidHandler.FluidAction.EXECUTE);
        activeIngredient = null;
        activeRecipe = null;
        pendingResult = ItemStack.EMPTY;
        brewKind = BrewKind.MERGE;
        craftTicks = 0;
        triggerAnim("controller", "craft");
        playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
        spawnCraftParticles(16, 12, false);
        setChanged();
    }

    /**
     * 药水合并时序推进：每 {@link #POTION_BUBBLE_INTERVAL} tick 气泡反馈；满时长执行合并并结束
     */
    private void tickMergeBrewing() {
        if (craftTicks < MERGE_BREW_DURATION && craftTicks % POTION_BUBBLE_INTERVAL == 0) {
            playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
            spawnCraftParticles(8, 6, false);
        }
        if (craftTicks >= MERGE_BREW_DURATION) {
            doMerge();
            playSound(SoundEvents.BREWING_STAND_BREW);
            spawnCraftParticles(24, 16, true);
            resetBrewing();
        }
    }

    /**
     * 执行药水合并（效果操作基于 PotionContents 组件，模组自定义药水自动兼容）：
     * <ul>
     *   <li><b>合并与容器转换一同完成</b>：若槽 0 材料可与某参与药水混合（hasMix），
     *       该药水先经 {@code mix()} 转换（如药水+火药→喷溅、+红石→长时效）再参与合并；
     *       发生转换时材料消耗 1 个并处理余留物（与酿造语义一致）；</li>
     *   <li>转换后全部相同（PotionContents 相等）：每瓶等级 +1、时长 ×瓶数；</li>
     *   <li>药水不同：效果并集（同效果取等级高者，相同等级取时长长者），
     *       容器与名称取首个参与药水；</li>
     *   <li>产物写入<b>成品槽 6</b>（GUI 下方输出位），参与瓶槽全部消耗 1 个；</li>
     *   <li>合并产物不再叠加梦之强化（合并本身即坩埚特色增益）。</li>
     * </ul>
     */
    private void doMerge() {
        PotionBrewing brewing = level == null ? null : level.potionBrewing();
        ItemStack material = itemHandler.getStackInSlot(SLOT_MATERIAL);
        boolean materialUsable = brewing != null && !material.isEmpty() && brewing.isIngredient(material);

        // 收集参与药水：槽 0 材料可混合时先做容器转换（合并+转换一同完成）
        List<ItemStack> convertedBottles = new ArrayList<>();
        boolean converted = false;
        for (int slot = SLOT_BOTTLE_FIRST; slot <= SLOT_BOTTLE_LAST; slot++) {
            ItemStack bottle = itemHandler.getStackInSlot(slot);
            if (!isMergeablePotion(bottle)) {
                continue;
            }
            ItemStack bottleOut = bottle;
            if (materialUsable && brewing.hasMix(bottle, material)) {
                bottleOut = brewing.mix(material, bottle);
                converted = true;
            }
            convertedBottles.add(bottleOut);
        }
        if (convertedBottles.size() < 2) {
            return;
        }

        // 材料消耗与余留物（发生转换时才消耗，与酿造语义一致）
        if (converted) {
            material.shrink(1);
            itemHandler.setStackInSlot(SLOT_MATERIAL, material);
            if (material.getItem().hasCraftingRemainingItem()) {
                ItemStack remaining = new ItemStack(material.getItem().getCraftingRemainingItem());
                if (material.isEmpty()) {
                    itemHandler.setStackInSlot(SLOT_MATERIAL, remaining);
                } else {
                    Containers.dropItemStack(level,
                            worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                            remaining);
                }
            }
        }

        ItemStack first = convertedBottles.get(0);
        PotionContents firstContents = first.get(DataComponents.POTION_CONTENTS);
        if (firstContents == null) {
            return;
        }
        // 在 potion 字段仍完好时记录原名称翻译键（保留合并前药水名）
        String nameKey = first.getItem().getDescriptionId(first);

        int count = convertedBottles.size();
        List<MobEffectInstance> merged = new ArrayList<>();
        if (convertedBottles.stream().allMatch(s -> Objects.equals(s.get(DataComponents.POTION_CONTENTS), firstContents))) {
            // 相同药水：每瓶等级 +1、时长 ×瓶数（无限时长保持原样）
            for (MobEffectInstance instance : firstContents.getAllEffects()) {
                int duration = instance.isInfiniteDuration() ? instance.getDuration() : instance.getDuration() * count;
                merged.add(new MobEffectInstance(instance.getEffect(), duration,
                        instance.getAmplifier() + (count - 1),
                        instance.isAmbient(), instance.isVisible(), instance.showIcon()));
            }
        } else {
            // 不同药水：效果并集（同效果取等级高者，相同等级取时长长者），按出现顺序保持稳定
            Map<Holder<MobEffect>, MobEffectInstance> byEffect = new LinkedHashMap<>();
            for (ItemStack bottle : convertedBottles) {
                for (MobEffectInstance instance : bottle.get(DataComponents.POTION_CONTENTS).getAllEffects()) {
                    MobEffectInstance existing = byEffect.get(instance.getEffect());
                    if (existing == null || instance.getAmplifier() > existing.getAmplifier()
                            || (instance.getAmplifier() == existing.getAmplifier()
                            && instance.getDuration() > existing.getDuration())) {
                        byEffect.put(instance.getEffect(), new MobEffectInstance(instance));
                    }
                }
            }
            merged.addAll(byEffect.values());
        }

        // 组装产物：清空 potion 字段防双重叠加，效果全量进 customEffects；保留容器与名称
        ItemStack result = first.copy();
        result.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Optional.empty(), firstContents.customColor(), merged));
        result.set(DataComponents.ITEM_NAME, Component.translatable(nameKey));

        // 产物写入成品槽 6，参与瓶槽全部消耗 1 个
        for (int slot = SLOT_BOTTLE_FIRST; slot <= SLOT_BOTTLE_LAST; slot++) {
            if (isMergeablePotion(itemHandler.getStackInSlot(slot))) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                stack.shrink(1);
                itemHandler.setStackInSlot(slot, stack);
            }
        }
        itemHandler.setStackInSlot(SLOT_RESULT, result);
    }

    // ==================== 酿造时序推进 ====================

    /**
     * 酿造状态机推进（每 tick）：
     * 空闲时尝试自动启动；酿造中先验证仍可酿造（材料被拿走/替换则中断），
     * 之后按通道分别推进时序。
     */
    private void tickCrafting() {
        if (craftTicks < 0) {
            tryStartAutoBrew();
            return;
        }
        craftTicks++;
        if (!validateBrewing()) {
            resetBrewing();
            return;
        }
        // 长时序通道（药水/合并）周期性重触发炼制动画，保持整个酿造期间动画连续
        // （法术酿造 60 tick 与动画时长一致，无需重触发）
        if (brewKind != BrewKind.SPELL && craftTicks % BREW_ANIM_RETRIGGER_INTERVAL == 0) {
            triggerAnim("controller", "craft");
        }
        if (brewKind == BrewKind.SPELL) {
            tickSpellCrafting();
        } else if (brewKind == BrewKind.MERGE) {
            tickMergeBrewing();
        } else {
            tickPotionBrewing();
        }
    }

    /**
     * 验证酿造是否仍应继续（与原版酿造台"材料变更即中断"规则一致）
     *
     * @return 继续酿造返回 true
     */
    private boolean validateBrewing() {
        if (brewKind == BrewKind.SPELL) {
            // 产物去处必须保持为空（被漏斗塞入则中断）
            if (!itemHandler.getStackInSlot(SLOT_RESULT).isEmpty()) {
                return false;
            }
            if (activeRecipe == null) {
                // 旧存档恢复：重新匹配以自愈
                RecipeHolder<DreamBrewingRecipe> matched = matchSpellRecipe();
                if (matched == null) {
                    return false;
                }
                activeRecipe = matched;
                return true;
            }
            // 槽位内容仍须匹配当前配方（按配方 ID 比较，与 RecipeManager 缓存实例解耦）
            RecipeHolder<DreamBrewingRecipe> matched = matchSpellRecipe();
            return matched != null && matched.id().equals(activeRecipe.id());
        }
        if (brewKind == BrewKind.MERGE) {
            // 合并中：产物去处被占用或瓶槽有效药水不足 2 瓶则中断
            return itemHandler.getStackInSlot(SLOT_RESULT).isEmpty() && isMergeable();
        }
        // 药水酿造：材料被拿走或被替换为其他物品则中断（activeIngredient 不持久化，
        // 存档重载后为 null，将在首个 tick 中断，与原版酿造台行为一致）
        ItemStack material = itemHandler.getStackInSlot(SLOT_MATERIAL);
        if (activeIngredient == null || material.isEmpty() || !material.is(activeIngredient)) {
            return false;
        }
        return isPotionBrewable();
    }

    /**
     * 中断酿造并重置状态
     */
    private void resetBrewing() {
        craftTicks = -1;
        pendingResult = ItemStack.EMPTY;
        activeRecipe = null;
        activeIngredient = null;
        setChanged();
    }

    /**
     * 法术酿造时序推进（时长由配方 {@code craft_tick} 决定）：
     * t=20/40 气泡反馈 → 末前第 2 tick 扣材料（槽 0-3 各 1 个，默认 60 时即 t=58，
     * 与原版 queueServerWork(58) 一致）→ 末前第 1 tick 产物入成品槽（默认 t=59）→
     * 末 tick 酿造音效+粒子并结束（默认 t=60）
     */
    private void tickSpellCrafting() {
        int duration = activeRecipe != null ? activeRecipe.value().getCraftTick() : SPELL_CRAFT_DURATION;
        if (craftTicks == 20 || craftTicks == 40) {
            playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
            spawnCraftParticles(16, 12, false);
        } else if (craftTicks == duration - 2) {
            // 槽 0-3 各消耗 1 个（与原版 queueServerWork(58) 一致）
            for (int slot = 0; slot <= SLOT_BOTTLE_LAST; slot++) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    stack.shrink(1);
                    itemHandler.setStackInSlot(slot, stack);
                }
            }
        } else if (craftTicks == duration - 1) {
            itemHandler.setStackInSlot(SLOT_RESULT, pendingResult.copy());
            pendingResult = ItemStack.EMPTY;
        } else if (craftTicks >= duration) {
            // 结束：酿造音效 + 强化粒子，产物保留在成品槽由玩家取走
            playSound(SoundEvents.BREWING_STAND_BREW);
            spawnCraftParticles(24, 16, true);
            resetBrewing();
        }
    }

    /**
     * 药水酿造时序推进：
     * 每 {@link #POTION_BUBBLE_INTERVAL} tick 气泡反馈；满时长时执行酿造并结束
     */
    private void tickPotionBrewing() {
        if (craftTicks < POTION_BREW_DURATION && craftTicks % POTION_BUBBLE_INTERVAL == 0) {
            playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
            spawnCraftParticles(8, 6, false);
        }
        if (craftTicks >= POTION_BREW_DURATION) {
            doPotionBrew();
            playSound(SoundEvents.BREWING_STAND_BREW);
            spawnCraftParticles(24, 16, true);
            resetBrewing();
        }
    }

    /**
     * 执行通用药水酿造（与原版 BrewingStandBlockEntity#doBrew 行为一致）：
     * 每个可混合的瓶槽经 {@code potionBrewing().mix()} 产出并应用梦之强化后
     * 替换瓶内物品；材料消耗 1 个，余留物（如带容器的材料）按原版规则
     * "材料耗尽 → 顶替材料槽 / 未耗尽 → 掉落"处理。
     */
    private void doPotionBrew() {
        if (level == null) {
            return;
        }
        PotionBrewing brewing = level.potionBrewing();
        ItemStack material = itemHandler.getStackInSlot(SLOT_MATERIAL);
        for (int slot = SLOT_BOTTLE_FIRST; slot <= SLOT_BOTTLE_LAST; slot++) {
            ItemStack bottle = itemHandler.getStackInSlot(slot);
            if (!bottle.isEmpty() && brewing.hasMix(bottle, material)) {
                ItemStack result = brewing.mix(material, bottle);
                // 梦之强化：持续型效果时长 x1.5，即时型效果等级 +1
                DreamPotionEnhancer.enhance(result);
                itemHandler.setStackInSlot(slot, result);
            }
        }
        // 材料消耗与余留物处理（与原版酿造台一致）
        material.shrink(1);
        itemHandler.setStackInSlot(SLOT_MATERIAL, material);
        if (material.getItem().hasCraftingRemainingItem()) {
            ItemStack remaining = new ItemStack(material.getItem().getCraftingRemainingItem());
            if (material.isEmpty()) {
                itemHandler.setStackInSlot(SLOT_MATERIAL, remaining);
            } else {
                Containers.dropItemStack(level,
                        worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                        remaining);
            }
        }
    }

    /**
     * 播放炼制音效（服务端广播，自动覆盖附近所有玩家）
     *
     * @param sound 音效事件
     */
    private void playSound(SoundEvent sound) {
        if (level != null && !level.isClientSide()) {
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    /**
     * 生成炼制粒子（融梦水晶 + 尘埃，收尾阶段额外加末地烛粒子）
     *
     * @param crystalCount 融梦水晶粒子数量
     * @param dustCount    尘埃粒子数量
     * @param finish       是否为收尾阶段（追加 END_ROD 粒子）
     */
    private void spawnCraftParticles(int crystalCount, int dustCount, boolean finish) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 1;
        double z = worldPosition.getZ() + 0.5;
        serverLevel.sendParticles(PDParticles.MELTDREAM_CRYSTAL_PARTICLE.holder().get(),
                x, y, z, crystalCount, 0.4, 0.4, 0.4, 0.01);
        serverLevel.sendParticles(PDParticles.DUST_0_PARTICLE.holder().get(),
                x, y, z, dustCount, 0.4, 0.4, 0.4, 0.01);
        if (finish) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 8, 0.4, 0.4, 0.4, 0.01);
        }
    }

    // ==================== GeckoLib 动画 ====================

    /**
     * 空闲动画控制器
     * 持续循环播放空闲动画 "0"（见 animations/block/dream_cauldron.animation.json），
     * 炼制动画 "1" 通过可触发动画 "craft" 播放，结束后自动回到空闲循环
     */
    private PlayState idlePredicate(AnimationState<DreamCauldronBlockEntity> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::idlePredicate)
                .triggerableAnim("craft", RawAnimation.begin().thenPlay("1")));
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
        fluidTank.writeToNBT(registries, tag);
        tag.putInt("CraftTicks", craftTicks);
        tag.putInt("BrewKind", brewKind == BrewKind.POTION ? 1 : brewKind == BrewKind.MERGE ? 2 : 0);
        tag.put("PendingResult", pendingResult.saveOptional(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        // 兼容旧版 4 槽存档：反序列化后强制恢复为 7 槽
        if (itemHandler.getSlots() != SLOT_COUNT) {
            ItemStackHandler upgraded = copyIntoSevenSlots();
            for (int i = 0; i < SLOT_COUNT; i++) {
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
            itemHandler.setSize(SLOT_COUNT);
            for (int i = 0; i < SLOT_COUNT; i++) {
                itemHandler.setStackInSlot(i, upgraded.getStackInSlot(i));
            }
        }
        fluidTank.readFromNBT(registries, tag);
        craftTicks = tag.contains("CraftTicks") ? tag.getInt("CraftTicks") : -1;
        if (tag.contains("BrewKind")) {
            int kind = tag.getInt("BrewKind");
            brewKind = kind == 1 ? BrewKind.POTION : kind == 2 ? BrewKind.MERGE : BrewKind.SPELL;
        } else {
            // 旧版存档无 BrewKind 字段：法术酿造中必有非空 PendingResult，据此推断
            ItemStack restored = tag.contains("PendingResult")
                    ? ItemStack.parseOptional(registries, tag.getCompound("PendingResult"))
                    : ItemStack.EMPTY;
            brewKind = restored.isEmpty() ? BrewKind.POTION : BrewKind.SPELL;
        }
        pendingResult = tag.contains("PendingResult")
                ? ItemStack.parseOptional(registries, tag.getCompound("PendingResult"))
                : ItemStack.EMPTY;
        // activeRecipe / activeIngredient 不持久化：
        // 由 validateBrewing 每 tick 自愈（法术重新匹配，药水因材料引用缺失自然中断）
        activeRecipe = null;
        activeIngredient = null;
    }

    /**
     * 旧版 4 槽存档迁移：0-2 材料平移到 1-3，3 号输出平移到 6
     *
     * @return 迁移后的 7 槽临时处理器
     */
    private ItemStackHandler copyIntoSevenSlots() {
        ItemStackHandler upgraded = new ItemStackHandler(SLOT_COUNT);
        int oldSlots = itemHandler.getSlots();
        for (int i = 0; i < Math.min(3, oldSlots); i++) {
            upgraded.setStackInSlot(i + 1, itemHandler.getStackInSlot(i));
        }
        if (oldSlots > 3) {
            upgraded.setStackInSlot(SLOT_RESULT, itemHandler.getStackInSlot(3));
        }
        return upgraded;
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
        return new com.pasterdream.pasterdreammod.menu.DreamCauldronMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.dream_cauldron");
    }
}
