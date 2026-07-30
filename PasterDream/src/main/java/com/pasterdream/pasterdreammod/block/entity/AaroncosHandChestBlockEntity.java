package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 亚伦柯斯之触战利品箱方块实体
 * <p>
 * GeckoLib 渲染；右键触发开启（对齐原版 {@code AaroncosHandChestPr0}）：
 * 音效 + 开启动画 → 40t 地面掉落 talent 分支战利品 → 41t 拆除箱体。
 * 开箱时 {@link PDArenaBossManager#cancelForceLeaveOnChestOpen} 取消胜利强制离场，
 * 玩家捡完后自行右键之眼离开。{@link #claimed} 保证只开一次。
 */
public class AaroncosHandChestBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final String TAG_CLAIMED = "Claimed";

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 是否已开启（对应原版 BE persistentData {@code switch}） */
    private boolean claimed;

    public AaroncosHandChestBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.AARONCOS_HAND_CHEST.get(), pos, state);
    }

    public boolean isClaimed() {
        return claimed;
    }

    /**
     * 右键开启：仅服务端、未 claimed 时生效。
     * <p>
     * 对齐原版 {@code AaroncosHandChestPr0}：
     * switch→true、shadow_door、animation=1 → 40t 粒子 + 按<strong>开启者</strong> talent 掉落
     * （light→白花体/白晶；shadow→堕落体/影柄；必掉 pure_horror，pickupDelay=10）→ 41t 拆箱。
     * talent 在 40t 时再判（原版 queue 内读 entity 成就），非开箱瞬间快照。
     *
     * @param player 开启者（对应 Pr0 的 entity）
     * @return true 若本次成功启动开启序列
     */
    public boolean tryOpen(Player player) {
        if (level == null || level.isClientSide || claimed || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        if (!(player instanceof ServerPlayer opener)) {
            return false;
        }
        claimed = true;
        setChanged();
        serverLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

        // 开箱即取消胜利强制离场：只保留开箱前那一条倒计时，不再另计 10 秒传出
        PDArenaBossManager.cancelForceLeaveOnChestOpen(serverLevel);

        serverLevel.playSound(null, worldPosition,
                PDSounds.SHADOW_DOOR.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        triggerAnim("main", "open");

        final BlockPos pos = worldPosition.immutable();
        final java.util.UUID openerId = opener.getUUID();

        ServerScheduler.schedule(40, () -> {
            if (!(level instanceof ServerLevel sl) || sl.isClientSide) {
                return;
            }
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            // 粒子四连：shadow_stone / end_rod / smoke / dust_0（与 Pr0 数量一致）
            sl.sendParticles((SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(),
                    x, y, z, 64, 1, 1, 1, 0.2);
            sl.sendParticles(ParticleTypes.END_ROD, x, y, z, 16, 1, 1, 1, 0.2);
            sl.sendParticles(ParticleTypes.SMOKE, x, y, z, 24, 1, 1, 1, 0.2);
            sl.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    x, y, z, 32, 1, 1, 1, 0.2);

            // talent 在掉落时判定（对齐 Pr0 在 queueServerWork(40) 内读 entity 成就）
            Player dropPlayer = sl.getPlayerByUUID(openerId);
            List<ItemStack> drops = buildLootFor(dropPlayer != null ? dropPlayer : opener);
            for (ItemStack stack : drops) {
                spawnItem(sl, stack, x, y, z);
            }
            PDDebugLogger.mainDebug("[AaroncosHandChest] 🎁 战利品已掉落于 ({}, {}, {}) count={}",
                    x, y, z, drops.size());
        });

        ServerScheduler.schedule(41, () -> {
            if (level instanceof ServerLevel sl && sl.getBlockEntity(pos) == this) {
                sl.destroyBlock(pos, false);
            }
        });
        return true;
    }

    /**
     * 强制离场批量发放后标记已领，避免重复。
     */
    public void markClaimedWithoutDrop() {
        if (claimed) {
            return;
        }
        claimed = true;
        setChanged();
    }

    /**
     * 单人强制离场等场景：若尚未开启，把本应掉落的战利品直接塞进玩家背包。
     */
    public void grantUnclaimedTo(ServerPlayer player) {
        if (claimed || player == null) {
            return;
        }
        claimed = true;
        setChanged();
        for (ItemStack stack : buildLootFor(player)) {
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
        PDDebugLogger.mainInfo("[AaroncosHandChest] 📦 未开启箱：已将战利品给予 {}",
                player.getName().getString());
    }

    /**
     * 按玩家 talent 成就构建掉落列表（严格对齐 Pr0 顺序与物品）：
     * <ol>
     *   <li>{@code talent_light} → {@code white_flower_body} + {@code white_crystal}</li>
     *   <li>{@code talent_shadow} → {@code degenerate_bodys} + {@code shadow_hilt}</li>
     *   <li>必掉 {@code pure_horror}（无 talent 也掉）</li>
     * </ol>
     * 两 talent 可并存（原版两个 if，非 else-if）。
     */
    public static List<ItemStack> buildLootFor(Player player) {
        List<ItemStack> out = new ArrayList<>();
        if (player instanceof ServerPlayer sp) {
            if (hasAdvancement(sp, "achievement_talent_light")) {
                // 白花胸针已拆分到 PasterDreamSanity；未安装时不加入战利品
                BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("pasterdreamsanity", "white_flower_body"))
                        .ifPresent(item -> out.add(new ItemStack(item)));
                out.add(new ItemStack(PDItems.WHITE_CRYSTAL.get()));
            }
            if (hasAdvancement(sp, "achievement_talent_shadow")) {
                out.add(new ItemStack(PDItems.DEGENERATE_BODYS.get()));
                out.add(new ItemStack(PDItems.SHADOW_HILT.get()));
            }
        }
        out.add(new ItemStack(PDItems.PURE_HORROR.get()));
        return out;
    }

    private static boolean hasAdvancement(ServerPlayer player, String path) {
        AdvancementHolder h = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        return h != null && player.getAdvancements().getOrStartProgress(h).isDone();
    }

    private static void spawnItem(ServerLevel level, ItemStack stack, double x, double y, double z) {
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setPickUpDelay(10);
        level.addFreshEntity(entity);
    }

    private PlayState idlePredicate(software.bernie.geckolib.animation.AnimationState<AaroncosHandChestBlockEntity> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0, this::idlePredicate)
                .triggerableAnim("open", RawAnimation.begin().thenPlay("1")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean(TAG_CLAIMED, claimed);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        claimed = tag.getBoolean(TAG_CLAIMED);
    }

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
