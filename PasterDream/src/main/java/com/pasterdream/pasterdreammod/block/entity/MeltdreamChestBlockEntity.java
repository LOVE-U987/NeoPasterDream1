package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.block.MeltdreamChestBlock;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 融梦水晶箱方块实体 (Meltdream Chest Block Entity)
 * <p>
 * 集成 GeckoLib 动画 + 9 格库存系统 + 玩家冷却 + 物品弹出状态机
 * <p>
 * 动画说明：
 * - animation = 0：空闲循环动画
 * - animation = 1：普通品质开启动画
 * - animation = 2：稀有品质开启动画
 * - animation = 3：传说品质开启动画
 * <p>
 * 物品弹出机制：动画播放完毕后，逐 tick 将容器内物品弹出为 ItemEntity，
 * 最后一个槽位（下标 8）若为 meltdream_crystal_0 则生成水晶实体而非弹出。
 */
public class MeltdreamChestBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final String ANIM_PROPERTY = "animation";

    /** 缓存动画属性值，避免每帧查询 block state */
    private int cachedAnimation = 0;

    /** 9 格库存处理器 */
    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    // ==================== 状态机字段 ====================

    /** 最后一次打开该宝箱的玩家 UUID */
    @Nullable
    private UUID openingPlayerUUID = null;

    /** 已弹出物品数量（0~9） */
    private int popProgress = 0;

    /** 战利品是否已填充到容器中 */
    private boolean itemsSet = false;

    /** 自打开以来经过的刻数 */
    private int openingTick = 0;

    /** 品质等级（1=普通, 2=稀有, 3=传说） */
    private int quality = 0;

    /** 弹出阶段是否已完成 */
    private boolean popComplete = false;

    /** 各品质对应的弹出总时长（tick），用于 tick 阶段判定 */
    private static final int[] POP_DURATIONS = {0, 46, 62, 83};

    // ==================== 冷却系统 ====================

    /** 玩家冷却映射表（UUID → 游戏刻到期时间） */
    private final Map<UUID, Long> playerCooldowns = new HashMap<>();

    /** 冷却时长（游戏刻）：1 分钟 = 1200 ticks */
    private static final long COOLDOWN_DURATION = 1200;

    // ==================== NBT 键 ====================

    private static final String TAG_POP_PROGRESS = "popProgress";
    private static final String TAG_ITEMS_SET = "itemsSet";
    private static final String TAG_OPENING_TICK = "openingTick";
    private static final String TAG_QUALITY = "quality";
    private static final String TAG_POP_COMPLETE = "popComplete";
    private static final String TAG_COOLDOWNS = "playerCooldowns";
    private static final String TAG_COOLDOWN_UUID = "uuid";
    private static final String TAG_COOLDOWN_TIME = "time";

    /**
     * 构造融梦水晶箱方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public MeltdreamChestBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.MELTDREAM_CHEST.get(), pos, state);
        updateCachedAnimation(state);
    }

    /**
     * 获取库存处理器
     *
     * @return ItemStackHandler 实例（9 格）
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // ==================== 冷却 API ====================

    /**
     * 检查玩家是否可以打开此宝箱（冷却是否已过）
     *
     * @param player 玩家
     * @return true 可打开，false 冷却中
     */
    public boolean canOpen(Player player) {
        Long cooldownEnd = playerCooldowns.get(player.getUUID());
        if (cooldownEnd == null) return true;
        if (level == null) return true;
        return level.getGameTime() >= cooldownEnd;
    }

    /**
     * 设置玩家冷却（当前游戏刻 + 冷却时长）
     *
     * @param player 玩家
     */
    public void setCooldown(Player player) {
        if (level == null) return;
        playerCooldowns.put(player.getUUID(), level.getGameTime() + COOLDOWN_DURATION);
        setChanged();
    }

    // ==================== 状态机 API ====================

    /**
     * 获取自打开以来经过的刻数
     *
     * @return 刻数
     */
    public int getOpeningTick() {
        return openingTick;
    }

    /**
     * 获取当前品质
     *
     * @return 品质（1-3）
     */
    public int getQuality() {
        return quality;
    }

    /**
     * 重置状态机，准备下一次打开
     */
    public void resetOpeningState() {
        this.openingPlayerUUID = null;
        this.popProgress = 0;
        this.itemsSet = false;
        this.openingTick = 0;
        this.quality = 0;
        this.popComplete = false;
        setChanged();
    }

    /**
     * 初始化打开状态
     *
     * @param player  打开的玩家
     * @param quality 品质（1-3）
     */
    public void initOpening(Player player, int quality) {
        this.openingPlayerUUID = player.getUUID();
        this.quality = quality;
        this.openingTick = 0;
        this.popProgress = 0;
        this.itemsSet = false;
        this.popComplete = false;
        setChanged();
    }

    /**
     * 标记战利品已填充
     */
    public void markItemsSet() {
        this.itemsSet = true;
        setChanged();
    }

    /**
     * 弹出下一个物品
     *
     * @param level 服务端世界
     * @param pos   方块位置
     * @return true 弹出成功，false 已全部弹出
     */
    public boolean popNextItem(ServerLevel level, BlockPos pos) {
        if (popProgress >= 9 || popComplete) return false;

        int slot = popProgress;
        popProgress++;

        ItemStack stack = itemHandler.getStackInSlot(slot);
        if (stack.isEmpty()) {
            // 空槽位跳过，但如果这是最后一格（下标 8），需要标记弹出完成
            if (popProgress >= 9) {
                popComplete = true;
                setChanged();
            }
            return true;
        }

        if (slot == 8 && stack.is(PDItems.MELTDREAM_CRYSTAL_0.get())) {
            // 第 9 格（下标 8）是融梦水晶 → 生成水晶实体
            if (level.getServer() != null) {
                // 暂时回退为掉落物（水晶实体未移植）
                spawnItemEntity(level, pos, stack);
            }
        } else {
            spawnItemEntity(level, pos, stack);
        }

        itemHandler.setStackInSlot(slot, ItemStack.EMPTY);

        if (popProgress >= 9) {
            popComplete = true;
        }
        setChanged();
        return true;
    }

    /**
     * 生成物品实体（带弹跳效果）
     *
     * @param level 服务端世界
     * @param pos   方块位置
     * @param stack 物品
     */
    private void spawnItemEntity(ServerLevel level, BlockPos pos, ItemStack stack) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.8;
        double z = pos.getZ() + 0.5;
        ItemEntity entity = new ItemEntity(level, x, y, z, stack.copy());
        entity.setPickUpDelay(20);
        level.addFreshEntity(entity);
    }

    /**
     * 弹出阶段是否已完成
     *
     * @return true 已完成
     */
    public boolean isPopComplete() {
        return popComplete;
    }

    /**
     * 战利品是否已填充
     *
     * @return true 已填充
     */
    public boolean isItemsSet() {
        return itemsSet;
    }

    /**
     * 弹出进度
     *
     * @return 已弹出数量
     */
    public int getPopProgress() {
        return popProgress;
    }

    /**
     * 递增打开刻数
     */
    public void incrementOpeningTick() {
        this.openingTick++;
    }

    // ==================== 服务端 Tick ====================

    /**
     * 服务端刻更新 —— 状态机驱动核心
     * <p>
     * 当 animation != 0（箱盖打开中）时运行：
     * <ol>
     *   <li>递增 openingTick</li>
     *   <li>每 5 tick 发射一次融梦水晶粒子</li>
     *   <li>动画播放到 60% 时开始每 6 tick 弹出一个物品（边播边喷）</li>
     *   <li>全部弹出后重置 animation=0 回到闲置态</li>
     * </ol>
     *
     * @param level 服务端世界
     * @param pos   方块位置
     * @param state 方块状态
     * @param be    方块实体
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, MeltdreamChestBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        int anim = state.getValue(MeltdreamChestBlock.ANIMATION);
        if (anim == 0) return;

        be.openingTick++;

        // ======== 粒子效果 ========
        if (be.openingTick % 5 == 0) {
            serverLevel.sendParticles(
                    (SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType(),
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    3,                           // 数量
                    0.3, 0.1, 0.3,               // 扩散范围
                    0.01                          // 速度
            );
        }

        // ======== 弹出阶段 ========
        // 在动画播放到 60% 时就开始弹出物品，而非等动画播完
        int animDuration = MeltdreamChestBlock.ANIMATION_DURATIONS[anim];
        int popStartTick = animDuration * 3 / 5; // 60% 处开始喷物品
        if (be.openingTick >= popStartTick) {
            int popTick = be.openingTick - popStartTick;

            // 每 6 tick 弹出一个
            if (popTick % 6 == 0 && !be.popComplete) {
                be.popNextItem(serverLevel, pos);
            }

            // 全部弹出完毕 → 重置为闲置态
            if (be.popComplete) {
                level.setBlock(pos, state.setValue(MeltdreamChestBlock.ANIMATION, 0), 3);
            }
        }
    }

    // ==================== GeckoLib 动画 ====================

    /**
     * 空闲动画控制器：animation = 0 时持续循环
     */
    private PlayState idlePredicate(AnimationState<MeltdreamChestBlockEntity> state) {
        int anim = getAnimationProperty();
        if (anim == 0) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("0"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    /**
     * 触发式动画控制器：animation = 1/2/3 时播放对应品质的开启动画
     */
    private PlayState openPredicate(AnimationState<MeltdreamChestBlockEntity> state) {
        int anim = getAnimationProperty();
        if (anim != 0) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay(String.valueOf(anim)));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle_controller", 0, this::idlePredicate));
        controllers.add(new AnimationController<>(this, "open_controller", 0, this::openPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * 更新缓存的动画属性值
     *
     * @param state 方块状态
     */
    public void updateCachedAnimation(BlockState state) {
        if (state.getBlock().getStateDefinition().getProperty(ANIM_PROPERTY) instanceof IntegerProperty prop) {
            cachedAnimation = state.getValue(prop);
        } else {
            cachedAnimation = 0;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null) {
            updateCachedAnimation(getBlockState());
        }
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        updateCachedAnimation(state);
    }

    /**
     * 获取当前动画属性值（使用缓存）
     *
     * @return animation 属性值（0-3）
     */
    public int getAnimationProperty() {
        return cachedAnimation;
    }

    // ==================== NBT 持久化 ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt(TAG_POP_PROGRESS, popProgress);
        tag.putBoolean(TAG_ITEMS_SET, itemsSet);
        tag.putInt(TAG_OPENING_TICK, openingTick);
        tag.putInt(TAG_QUALITY, quality);
        tag.putBoolean(TAG_POP_COMPLETE, popComplete);

        // 持久化冷却映射
        ListTag cooldownList = new ListTag();
        for (Map.Entry<UUID, Long> entry : playerCooldowns.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString(TAG_COOLDOWN_UUID, entry.getKey().toString());
            entryTag.putLong(TAG_COOLDOWN_TIME, entry.getValue());
            cooldownList.add(entryTag);
        }
        tag.put(TAG_COOLDOWNS, cooldownList);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        popProgress = tag.getInt(TAG_POP_PROGRESS);
        itemsSet = tag.getBoolean(TAG_ITEMS_SET);
        openingTick = tag.getInt(TAG_OPENING_TICK);
        quality = tag.getInt(TAG_QUALITY);
        popComplete = tag.getBoolean(TAG_POP_COMPLETE);

        // 读取冷却映射
        playerCooldowns.clear();
        if (tag.contains(TAG_COOLDOWNS, Tag.TAG_LIST)) {
            ListTag cooldownList = tag.getList(TAG_COOLDOWNS, Tag.TAG_COMPOUND);
            for (int i = 0; i < cooldownList.size(); i++) {
                CompoundTag entryTag = cooldownList.getCompound(i);
                UUID uuid = UUID.fromString(entryTag.getString(TAG_COOLDOWN_UUID));
                long time = entryTag.getLong(TAG_COOLDOWN_TIME);
                playerCooldowns.put(uuid, time);
            }
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
}