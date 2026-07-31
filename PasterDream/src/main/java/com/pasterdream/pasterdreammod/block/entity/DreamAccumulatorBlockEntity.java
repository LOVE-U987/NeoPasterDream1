package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.menu.DreamAccumulatorMenu;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 蓄梦池方块实体 (Dream Accumulator Block Entity)
 * 使用 GeckoLib 实现动画效果
 * <p>
 * 动画说明：
 * - move 骨骼：上下浮动动画（4秒循环）
 * - bone2 骨骼：360度旋转动画（4秒一圈）
 * <p>
 * 本波次补全原版 TileEntity 的功能语义：2 格库存（0 染梦尘埃碎片产物 /
 * 1 吸附剂加速）+ 蓄梦计时（原版 PersistentData "time"）+ GUI 菜单。
 * 蓄梦逻辑（原版 DreamAccumulatorPr0Procedure，每 40 tick 由方块调度）：
 * <ul>
 *   <li>仅在染梦世界（pasterdream:dyedream_world）内积累：time+1，槽 1 有吸附剂
 *       时额外 +2 并损耗吸附剂耐久；伴随尘埃粒子与低音量 dream2 音效；</li>
 *   <li>time ≥ 600：向槽 0 产出 1 个染梦尘埃碎片（上限 64），time 归零，
 *       伴随染梦粒子与 dream2 音效。</li>
 * </ul>
 */
public class DreamAccumulatorBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    /** 产物槽（染梦尘埃碎片） */
    public static final int SLOT_OUTPUT = 0;
    /** 吸附剂槽 */
    public static final int SLOT_SORBENT = 1;

    /** 产出一次所需的蓄梦计时 */
    private static final double TIME_FULL = 600;

    /**
     * GeckoLib 动画实例缓存
     */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 2 格库存（0 产物 / 1 吸附剂） */
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** 蓄梦计时（原版 PersistentData "time"） */
    private double time;

    /**
     * 构造蓄梦池方块实体
     *
     * @param pos 方块位置
     * @param state 方块状态
     */
    public DreamAccumulatorBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.DREAM_ACCUMULATOR.get(), pos, state);
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
     * 蓄梦计时归零并播放 dream1 音效（原版 DreamAccumulatorPr1Procedure，
     * 放置时与取走产物时触发）
     */
    public void resetTime() {
        this.time = 0;
        setChanged();
        syncToClient();
        if (level != null && !level.isClientSide()) {
            level.playSound(null, worldPosition, PDSounds.DREAM1.get(), SoundSource.NEUTRAL, 0.8f, 1);
        }
    }

    /**
     * 蓄梦周期逻辑（原版 DreamAccumulatorPr0Procedure，每 40 tick 由方块调度）
     */
    public void accumulateTick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        double x = worldPosition.getX();
        double y = worldPosition.getY();
        double z = worldPosition.getZ();
        if (time < TIME_FULL) {
            // 仅在染梦世界内积累
            if (serverLevel.dimension() == PDDimensions.DYEDREAM_WORLD_LEVEL_KEY) {
                time += 1;
                ItemStack sorbent = itemHandler.getStackInSlot(SLOT_SORBENT);
                if (sorbent.is(PDItems.SORBENT.get().asItem())) {
                    damageSorbent(sorbent);
                    time += 2;
                }
                setChanged();
                syncToClient();
                serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                        x + 0.5, y + 0.5, z + 0.5, 16, 0.1, 0.2, 0.1, 0.02);
                serverLevel.playSound(null, worldPosition, PDSounds.DREAM2.get(), SoundSource.NEUTRAL, 0.1f, 1);
            }
            return;
        }
        // 蓄满：产出染梦尘埃碎片
        ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);
        boolean produced = false;
        if (output.is(PDItems.DYEDREAM_DUST_PIECE.get().asItem()) && output.getCount() <= 63) {
            output.grow(1);
            itemHandler.setStackInSlot(SLOT_OUTPUT, output);
            produced = true;
        } else if (output.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_OUTPUT, new ItemStack(PDItems.DYEDREAM_DUST_PIECE.get().asItem()));
            produced = true;
        }
        if (produced) {
            time = 0;
            setChanged();
            syncToClient();
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DYEDREAM_0_PARTICLE.particleType(),
                    x + 0.5, y + 0.5, z + 0.5, 16, 0.1, 0.2, 0.1, 0.02);
            serverLevel.playSound(null, worldPosition, PDSounds.DREAM2.get(), SoundSource.NEUTRAL, 1, 1);
        }
    }

    /**
     * 打开 GUI 时的笔记馈赠（原版 DreamAccumulatorGuiPr0Procedure）：
     * 未完成 achievement_c_3 且背包中没有 dreamnotes_7 时，
     * 关闭界面并赠送一张寻梦者笔记；笔记物品未注册时跳过
     *
     * @param player 打开界面的玩家
     */
    public void giveIntroNotes(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        Item notes = BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dreamnotes_7"))
                .orElse(Items.AIR);
        if (notes == Items.AIR) {
            PDDebugLogger.mainDebug("[DreamAccumulator] dreamnotes_7 未注册，跳过初见馈赠");
            return;
        }
        boolean done;
        if (!PDAdvancements.isAdvancementLocked(serverPlayer,
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "achievement_c_3"))) {
            done = true;
        } else {
            AdvancementHolder holder = serverPlayer.server.getAdvancements()
                    .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "achievement_c_3"));
            done = holder != null && serverPlayer.getAdvancements().getOrStartProgress(holder).isDone();
        }
        if (!done && !serverPlayer.getInventory().contains(new ItemStack(notes))) {
            serverPlayer.closeContainer();
            ItemStack gift = new ItemStack(notes);
            if (!serverPlayer.getInventory().add(gift)) {
                serverPlayer.drop(gift, false);
            }
            serverPlayer.displayClientMessage(
                    Component.literal("你发现了一张寻梦者笔记，并收进了你的背包"), false);
        }
    }

    /**
     * 吸附剂损耗 1 点耐久，用尽则消耗（原版 _stk.hurt 语义；
     * 吸附剂当前为无耐久注册时等效不损耗）
     *
     * @param sorbent 吸附剂物品栈
     */
    private void damageSorbent(ItemStack sorbent) {
        if (!sorbent.isDamageableItem()) {
            return;
        }
        sorbent.setDamageValue(sorbent.getDamageValue() + 1);
        if (sorbent.getDamageValue() >= sorbent.getMaxDamage()) {
            sorbent.shrink(1);
            sorbent.setDamageValue(0);
        }
        itemHandler.setStackInSlot(SLOT_SORBENT, sorbent);
    }

    /** 同步方块实体数据到客户端 */
    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    /**
     * 注册动画控制器
     * 定义动画播放逻辑
     *
     * @param controllers 动画控制器注册器
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // 循环播放动画 "0"（对应动画文件中的 "0" 动画）
            state.setAnimation(RawAnimation.begin().thenLoop("0"));
            return PlayState.CONTINUE;
        }));
    }

    /**
     * 获取动画实例缓存
     *
     * @return AnimatableInstanceCache 实例
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // ==================== 持久化（NBT 键与原版 PersistentData 一致） ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putDouble("time", time);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        time = tag.getDouble("time");
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
        return new DreamAccumulatorMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.dream_accumulator");
    }
}
