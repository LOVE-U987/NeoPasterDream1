package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.api.util.AddonDetector;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDFluids;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 梦境炼药锅方块实体 (Dream Cauldron Block Entity)
 * 完整还原原版 PasterDream 的炼药锅：7 格库存 + 2000mB 融梦液体储罐 + 炼制时序。
 *
 * 槽位说明（与原版一致）：
 * - 索引 0：引导药剂槽（所有配方的共同催化剂）
 * - 索引 1-3：炼药材料槽
 * - 索引 4：融梦液体桶输入槽（放入后自动注入 1000mB 并退还空桶到槽 5）
 * - 索引 5：空桶回收槽
 * - 索引 6：成品槽（炼制完成后自动向上弹出）
 *
 * 炼制流程（服务端 tick 状态机，时序与原版 procedure 一致）：
 * - t=0：扣除 100mB 液体，触发 "1" 炼制动画，关闭发起玩家的 GUI，气泡音效+粒子
 * - t=20 / t=40：气泡音效+粒子
 * - t=58：槽 0-3 各消耗 1 个材料
 * - t=59：成品写入槽 6
 * - t=60：酿造音效+粒子，槽 6 物品弹出为掉落物并清空，炼制结束
 *
 * 动画说明（animations/block/dream_cauldron.animation.json）：
 * - "0"：空闲循环动画
 * - "1"：炼制动画（3 秒一次性，triggerAnim 触发）
 */
public class DreamCauldronBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    /** 库存槽位总数 */
    private static final int SLOT_COUNT = 7;
    /** 引导药剂槽 */
    private static final int SLOT_GUIDING_DRUG = 0;
    /** 液体桶输入槽 */
    private static final int SLOT_BUCKET_IN = 4;
    /** 空桶回收槽 */
    private static final int SLOT_BUCKET_OUT = 5;
    /** 成品槽 */
    private static final int SLOT_RESULT = 6;

    /** 储罐容量（mB，与原版一致） */
    private static final int TANK_CAPACITY = 2000;
    /** 每桶注入量（mB） */
    private static final int BUCKET_FILL_AMOUNT = 1000;
    /** 每次炼制消耗液体量（mB，与原版一致） */
    private static final int CRAFT_FLUID_COST = 100;

    /** 炼制总时长（tick，对应 3 秒炼制动画） */
    private static final int CRAFT_DURATION = 60;
    /** 材料消耗时刻（与原版 queueServerWork(58) 一致） */
    private static final int CRAFT_CONSUME_TICK = 58;
    /** 成品写入时刻（与原版 queueServerWork(59) 一致） */
    private static final int CRAFT_RESULT_TICK = 59;

    /** PasterDreamSpells 法术物品的命名空间 */
    private static final String SPELLS_MOD_ID = "pasterdreamspells";

    /**
     * 炼药配方（与原版 DreamCauldronRecipePr0Procedure 一致）：
     * 槽 0 = 引导药剂（公共前提），槽 1-3 = 按序匹配的三种材料，产出一种法术物品。
     * result 使用 Optional，未安装 PasterDreamSpells 时配方结果为空，炼制不会启动。
     */
    private record CauldronRecipe(Supplier<Item> input1, Supplier<Item> input2,
                                  Supplier<Item> input3, Optional<Item> result) {
    }

    /**
     * 动态构建 5 个原版配方。
     * 当 PasterDreamSpells 未加载时返回空列表，避免引用不存在的法术物品。
     */
    private static List<CauldronRecipe> buildRecipes() {
        if (!AddonDetector.isSpellsLoaded()) {
            return List.of();
        }
        return List.of(
                // 矢车菊 + 红石 + 阴暗云 → 闪电法术
                new CauldronRecipe(() -> Items.CORNFLOWER, () -> Items.REDSTONE,
                        () -> PDItems.DARK_CLOUD.get(), lookupSpellItem("lightning_spell")),
                // 花卉2 + 蜘蛛眼 + 毒马铃薯 → 剧毒法术
                new CauldronRecipe(() -> PDItems.FLOWER_2.get(), () -> Items.SPIDER_EYE,
                        () -> Items.POISONOUS_POTATO, lookupSpellItem("poison_spell")),
                // 金苹果 + 闪烁的西瓜片 + 向日葵 → 治疗法术
                new CauldronRecipe(() -> Items.GOLDEN_APPLE, () -> Items.GLISTERING_MELON_SLICE,
                        () -> Items.SUNFLOWER, lookupSpellItem("healing_spell")),
                // 绒球葱 + 龙息 + 紫水晶碎片 → 狂暴法术
                new CauldronRecipe(() -> Items.ALLIUM, () -> Items.DRAGON_BREATH,
                        () -> Items.AMETHYST_SHARD, lookupSpellItem("fury_spell")),
                // 兰花 + 雪球 + 冰芽 → 冰冻法术
                new CauldronRecipe(() -> Items.BLUE_ORCHID, () -> Items.SNOWBALL,
                        () -> PDItems.ICE_BUD_0.get(), lookupSpellItem("ice_spell"))
        );
    }

    /**
     * 通过 {@link BuiltInRegistries#ITEM} 动态查找 PasterDreamSpells 的法术物品。
     *
     * @param path 法术物品注册名（如 "lightning_spell"）
     * @return 对应物品的 Optional
     */
    private static Optional<Item> lookupSpellItem(String path) {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(SPELLS_MOD_ID, path));
    }

    /** 全部 5 个原版配方（运行时动态构建） */
    private static final List<CauldronRecipe> RECIPES = buildRecipes();

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 7 格库存处理器（0 引导药剂，1-3 材料，4 桶入，5 桶出，6 成品） */
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

    /** 炼制进度计数器：-1 = 空闲，0..CRAFT_DURATION = 炼制中 */
    private int craftTicks = -1;

    /** 本次炼制的产物（配方匹配时缓存，t=59 写入成品槽） */
    private ItemStack pendingResult = ItemStack.EMPTY;

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

    // ==================== 炼制逻辑（服务端） ====================

    /**
     * 服务端每 tick 驱动：处理液体桶注入与炼制时序
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

    /**
     * 尝试启动炼制（由 GUI 合成按钮经菜单在服务端调用）
     * 校验：未在炼制中 + 槽 0 为引导药剂 + 槽 1-3 按序匹配某配方 + 液体 ≥ 100mB
     *
     * @param player 点击按钮的玩家（炼制启动后为其关闭 GUI，与原版一致）
     */
    public void tryStartCraft(Player player) {
        if (level == null || level.isClientSide() || craftTicks >= 0) {
            return;
        }
        if (!itemHandler.getStackInSlot(SLOT_GUIDING_DRUG).is(PDItems.GUIDING_DRUG.get())) {
            return;
        }
        CauldronRecipe matched = null;
        for (CauldronRecipe recipe : RECIPES) {
            if (recipe.result().isEmpty()) {
                continue;
            }
            if (itemHandler.getStackInSlot(1).is(recipe.input1().get())
                    && itemHandler.getStackInSlot(2).is(recipe.input2().get())
                    && itemHandler.getStackInSlot(3).is(recipe.input3().get())) {
                matched = recipe;
                break;
            }
        }
        if (matched == null || fluidTank.getFluidAmount() < CRAFT_FLUID_COST) {
            return;
        }

        // t=0：扣液体、启动状态机、触发炼制动画、关闭 GUI、首轮音效粒子（与原版时序一致）
        fluidTank.drain(CRAFT_FLUID_COST, IFluidHandler.FluidAction.EXECUTE);
        pendingResult = new ItemStack(matched.result().get());
        craftTicks = 0;
        triggerAnim("controller", "craft");
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.closeContainer();
        }
        playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
        spawnCraftParticles(16, 12, false);
        setChanged();
    }

    /**
     * 炼制时序推进（每 tick）：
     * t=20/40 气泡反馈 → t=58 扣材料 → t=59 出成品 → t=60 弹出并结束
     */
    private void tickCrafting() {
        if (craftTicks < 0) {
            return;
        }
        craftTicks++;

        if (craftTicks == 20 || craftTicks == 40) {
            playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP);
            spawnCraftParticles(16, 12, false);
        } else if (craftTicks == CRAFT_CONSUME_TICK) {
            // 槽 0-3 各消耗 1 个（与原版 queueServerWork(58) 一致）
            for (int slot = 0; slot <= 3; slot++) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    stack.shrink(1);
                    itemHandler.setStackInSlot(slot, stack);
                }
            }
        } else if (craftTicks == CRAFT_RESULT_TICK) {
            itemHandler.setStackInSlot(SLOT_RESULT, pendingResult.copy());
            pendingResult = ItemStack.EMPTY;
        } else if (craftTicks >= CRAFT_DURATION) {
            // t=60：酿造音效 + 强化粒子 + 弹出成品
            playSound(SoundEvents.BREWING_STAND_BREW);
            spawnCraftParticles(24, 16, true);
            ejectResult();
            craftTicks = -1;
            setChanged();
        }
    }

    /**
     * 将成品槽物品弹出为掉落物（位置/拾取延迟与原版一致）并清空成品槽
     */
    private void ejectResult() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack result = itemHandler.getStackInSlot(SLOT_RESULT);
        if (result.isEmpty()) {
            return;
        }
        ItemEntity itemEntity = new ItemEntity(serverLevel,
                worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5,
                result.copy());
        itemEntity.setPickUpDelay(10);
        serverLevel.addFreshEntity(itemEntity);
        itemHandler.setStackInSlot(SLOT_RESULT, ItemStack.EMPTY);
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
     * 生成炼制粒子（融梦水晶 + 尘埃，t=60 时额外加末地烛粒子，数量与原版一致）
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
        pendingResult = tag.contains("PendingResult")
                ? ItemStack.parseOptional(registries, tag.getCompound("PendingResult"))
                : ItemStack.EMPTY;
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
