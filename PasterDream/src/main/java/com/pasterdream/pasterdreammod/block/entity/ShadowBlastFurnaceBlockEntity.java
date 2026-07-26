package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.block.ShadowBlastFurnaceBlock;
import com.pasterdream.pasterdreammod.menu.ShadowBlastFurnaceMenu;
import com.pasterdream.pasterdreammod.recipe.ShadowBlastFurnaceRecipe;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDFluids;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
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

import java.util.Optional;

/**
 * 暗影高炉方块实体 (Shadow Blast Furnace Block Entity)
 * <p>
 * 还原原版 {@code ShadowBlastFurnaceTileEntity} 的槽位与冶炼状态机：
 * <ul>
 *   <li>槽位：0 冶炼输入 / 1 梦魇燃料 / 2 主产物 / 3 副产物 /
 *       4 暗影液体桶输入 / 5 空桶回收；</li>
 *   <li>储罐 9000mB，仅接受暗影液体（shadow_liquid）；</li>
 *   <li>{@link #blastingTick} 每 tick 依次执行：液体桶注入 → 冶炼推进 →
 *       新配方匹配（经 {@code pasterdream:shadow_blasting} 数据包配方，
 *       {@code RecipeManager.getRecipeFor} + {@link SingleRecipeInput}）→
 *       WORKING 方块状态同步；</li>
 *   <li>{@link #animationTick} 客户端逐 tick 播放工作烟雾与高炉噼啪声
 *       （与原版 animationTick 相同的粒子分布参数）。</li>
 * </ul>
 * 与原版差异（保真说明）：原版经 Forge 物品流体能力抽取任意容器中的暗影液体，
 * 本版按项目工坊锻炉先例改为显式识别暗影液体桶（游戏内唯一可获取的容器）。
 */
public class ShadowBlastFurnaceBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    /** 冶炼输入槽 */
    public static final int SLOT_INPUT = 0;
    /** 梦魇燃料槽 */
    public static final int SLOT_FUEL = 1;
    /** 主产物槽 */
    public static final int SLOT_RESULT = 2;
    /** 副产物槽 */
    public static final int SLOT_BY_RESULT = 3;
    /** 液体燃料桶输入槽 */
    public static final int SLOT_BUCKET_IN = 4;
    /** 空桶回收槽 */
    public static final int SLOT_BUCKET_OUT = 5;
    /** 槽位总数 */
    public static final int SLOT_COUNT = 6;

    /** 储罐容量（mB，与原版一致） */
    public static final int TANK_CAPACITY = 9000;
    /** 一桶暗影液体的注入量（mB） */
    private static final int BUCKET_VOLUME = 1000;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 6 格库存 */
    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** 暗影液体储罐（仅接受 shadow_liquid） */
    private final FluidTank fluidTank = new FluidTank(TANK_CAPACITY,
            fs -> fs.getFluid().isSame(PDFluids.SHADOW_LIQUID.get())) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            syncToClient();
        }
    };

    /** 当前冶炼配方 ID（原版 workRecipeID，空表示未在冶炼） */
    @Nullable
    private ResourceLocation workRecipeId;
    /** 当前冶炼已进行的 tick 数 */
    private int blastingTime;

    /**
     * 构造暗影高炉方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public ShadowBlastFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.SHADOW_BLAST_FURNACE.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return 6 格 ItemStackHandler
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * 获取当前冶炼配方 ID
     *
     * @return 配方 ID（未在冶炼时为 null）
     */
    @Nullable
    public ResourceLocation getWorkRecipeId() {
        return workRecipeId;
    }

    /**
     * 获取当前冶炼进度（tick）
     *
     * @return 已冶炼 tick 数
     */
    public int getBlastingTime() {
        return blastingTime;
    }

    /**
     * 获取当前配方所需的总冶炼时长（原版 getNeedBlastingTime）
     *
     * @return 总时长（tick），未在冶炼或配方丢失时为 0
     */
    public int getNeedBlastingTime() {
        if (workRecipeId == null || level == null) {
            return 0;
        }
        return level.getRecipeManager().byKey(workRecipeId)
                .map(holder -> holder.value() instanceof ShadowBlastFurnaceRecipe recipe ? recipe.getBlastingTick() : 0)
                .orElse(0);
    }

    /**
     * 获取储罐中的暗影液体量（供菜单 DataSlot 同步）
     *
     * @return 液体量（mB）
     */
    public int getFluidAmount() {
        return fluidTank.getFluidAmount();
    }

    // ==================== 客户端动画 tick（原版 animationTick） ====================

    /**
     * 客户端逐 tick 工作特效：WORKING 时播放大烟雾/营火烟粒子与高炉噼啪声
     *
     * @param level   世界
     * @param pos     方块位置
     * @param state   方块状态
     * @param furnace 高炉方块实体
     */
    public static void animationTick(Level level, BlockPos pos, BlockState state, ShadowBlastFurnaceBlockEntity furnace) {
        if (state.getValue(ShadowBlastFurnaceBlock.WORKING)) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            addParticles(ParticleTypes.LARGE_SMOKE, level, x + 0.5, y + 2, z + 0.5, 7, 0, 3, 0, 0.15);
            addParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, level, x + 0.5, y + 2, z + 0.5, 4, 0.05, 3, 0.05, 0.15);
            level.playLocalSound(x, y, z, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 2, 1, false);
        }
    }

    /**
     * 按高斯分布散布粒子（与原版 addParticles 相同的参数含义）
     */
    private static <T extends ParticleOptions> void addParticles(T particleType, Level level,
                                                                 double x, double y, double z, int count,
                                                                 double xDist, double yDist, double zDist, double maxSpeed) {
        RandomSource random = level.random;
        for (int i = 0; i < count; i++) {
            double fx = random.nextGaussian() * xDist;
            double fy = random.nextGaussian() * yDist;
            double fz = random.nextGaussian() * zDist;
            double vx = random.nextGaussian() * maxSpeed;
            double vy = random.nextGaussian() * maxSpeed;
            double vz = random.nextGaussian() * maxSpeed;
            level.addParticle(particleType, x + fx, y + fy, z + fz, vx, vy, vz);
        }
    }

    // ==================== 服务端冶炼 tick（原版 blastingTick） ====================

    /**
     * 服务端逐 tick 冶炼状态机：液体桶注入 → 冶炼推进/完成 → 新配方匹配 → WORKING 同步
     *
     * @param level   世界
     * @param pos     方块位置
     * @param state   方块状态
     * @param furnace 高炉方块实体
     */
    public static void blastingTick(Level level, BlockPos pos, BlockState state, ShadowBlastFurnaceBlockEntity furnace) {
        boolean updateInventory = false;

        // 液体燃料注入：槽 4 暗影液体桶 → 储罐 +1000mB，空桶叠入槽 5
        updateInventory |= furnace.processFluidBucket();

        // 冶炼推进
        if (furnace.workRecipeId != null) {
            Optional<RecipeHolder<?>> holder = level.getRecipeManager().byKey(furnace.workRecipeId);
            if (holder.isPresent() && holder.get().value() instanceof ShadowBlastFurnaceRecipe recipe) {
                furnace.blastingTime = Math.min(recipe.getBlastingTick(), furnace.blastingTime + 1);
                updateInventory = true;
                // 冶炼完成：主/副产物槽均可容纳时出料，否则卡住等待
                if (furnace.blastingTime == recipe.getBlastingTick()) {
                    ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
                    ItemStack byResult = recipe.getByResultItem().copy();
                    ItemStack output = furnace.itemHandler.getStackInSlot(SLOT_RESULT);
                    ItemStack byOutput = furnace.itemHandler.getStackInSlot(SLOT_BY_RESULT);
                    boolean canOutput = result.isEmpty() || output.isEmpty()
                            || (ItemStack.isSameItem(result, output)
                            && output.getCount() + result.getCount() <= result.getMaxStackSize());
                    boolean canByOutput = byResult.isEmpty() || byOutput.isEmpty()
                            || (ItemStack.isSameItem(byResult, byOutput)
                            && byOutput.getCount() + byResult.getCount() <= byResult.getMaxStackSize());
                    if (canOutput && canByOutput) {
                        furnace.workRecipeId = null;
                        if (!result.isEmpty()) {
                            if (output.isEmpty()) {
                                furnace.itemHandler.setStackInSlot(SLOT_RESULT, result);
                            } else {
                                output.grow(result.getCount());
                            }
                        }
                        if (!byResult.isEmpty() && level.random.nextDouble() <= recipe.getByOutputProbability()) {
                            if (byOutput.isEmpty()) {
                                furnace.itemHandler.setStackInSlot(SLOT_BY_RESULT, byResult);
                            } else {
                                byOutput.grow(byResult.getCount());
                            }
                        }
                    }
                }
            } else {
                // 原配方已随数据包消失
                furnace.workRecipeId = null;
            }
        }

        // 匹配新配方（不支持空烧）
        if (furnace.workRecipeId == null) {
            furnace.blastingTime = 0;
            ItemStack input = furnace.itemHandler.getStackInSlot(SLOT_INPUT);
            if (!input.isEmpty()) {
                Optional<RecipeHolder<ShadowBlastFurnaceRecipe>> match = level.getRecipeManager()
                        .getRecipeFor(PDRecipeTypes.SHADOW_BLASTING.get(), new SingleRecipeInput(input), level);
                if (match.isPresent()) {
                    ShadowBlastFurnaceRecipe recipe = match.get().value();
                    ItemStack fuel = furnace.itemHandler.getStackInSlot(SLOT_FUEL);
                    // 与原版一致：燃料槽必须是梦魇燃料且数量足够（spend_fuel 为 0 时也要求放入燃料）
                    boolean hasFuel = fuel.is(PDItems.NIGHTMARE_FUEL.get().asItem())
                            && fuel.getCount() >= recipe.getSpendFuel();
                    boolean hasFluid = furnace.fluidTank.getFluidAmount() >= recipe.getSpendFluidFuel();
                    if (hasFuel && hasFluid) {
                        updateInventory = true;
                        fuel.shrink(recipe.getSpendFuel());
                        furnace.itemHandler.setStackInSlot(SLOT_FUEL, fuel);
                        input.shrink(1);
                        furnace.itemHandler.setStackInSlot(SLOT_INPUT, input);
                        furnace.fluidTank.drain(recipe.getSpendFluidFuel(), IFluidHandler.FluidAction.EXECUTE);
                        furnace.workRecipeId = match.get().id();
                    }
                }
            }
        }

        // WORKING 方块状态同步
        if ((furnace.workRecipeId == null) == state.getValue(ShadowBlastFurnaceBlock.WORKING)) {
            updateInventory = true;
            BlockState newState = state.setValue(ShadowBlastFurnaceBlock.WORKING, furnace.workRecipeId != null);
            level.setBlock(pos, newState, Block.UPDATE_ALL);
        }

        if (updateInventory) {
            furnace.setChanged();
            furnace.syncToClient();
        }
    }

    /**
     * 液体燃料桶注入（原版经物品流体能力抽取，此处按工坊锻炉先例显式识别暗影液体桶）
     *
     * @return 是否发生了注入
     */
    private boolean processFluidBucket() {
        if (fluidTank.getSpace() < BUCKET_VOLUME) {
            return false;
        }
        ItemStack bucket = itemHandler.getStackInSlot(SLOT_BUCKET_IN);
        if (!bucket.is(PDItems.SHADOW_LIQUID_BUCKET.get().asItem())) {
            return false;
        }
        ItemStack out = itemHandler.getStackInSlot(SLOT_BUCKET_OUT);
        // 回收槽必须为空或已是未满的空桶堆
        boolean canReturn = out.isEmpty()
                || (out.is(Items.BUCKET) && out.getCount() + 1 <= out.getMaxStackSize());
        if (!canReturn) {
            return false;
        }
        fluidTank.fill(new FluidStack(PDFluids.SHADOW_LIQUID.get(), BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
        bucket.shrink(1);
        itemHandler.setStackInSlot(SLOT_BUCKET_IN, bucket);
        if (out.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_BUCKET_OUT, new ItemStack(Items.BUCKET));
        } else {
            out.grow(1);
            itemHandler.setStackInSlot(SLOT_BUCKET_OUT, out);
        }
        return true;
    }

    /** 同步方块实体数据到客户端（等价原版 inventoryChanged 的 sendBlockUpdated） */
    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
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
    private PlayState predicate(AnimationState<ShadowBlastFurnaceBlockEntity> state) {
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
        CompoundTag tankTag = new CompoundTag();
        fluidTank.writeToNBT(registries, tankTag);
        tag.put("fluidTank", tankTag);
        tag.putString("workingRecipe", workRecipeId == null ? "" : workRecipeId.toString());
        tag.putInt("blastingTime", blastingTime);
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
        String recipeId = tag.getString("workingRecipe");
        this.workRecipeId = recipeId.isEmpty() ? null : ResourceLocation.parse(recipeId);
        this.blastingTime = tag.getInt("blastingTime");
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
        return new ShadowBlastFurnaceMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.shadow_blast_furnace");
    }
}
