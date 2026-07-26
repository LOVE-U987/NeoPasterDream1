package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 工坊磨石方块实体 (Workshop Grind Block Entity)
 * <p>
 * 打磨随机强化（原版 WorkshopGrindInlay0Procedure）：
 * 抽取随机数 1..2 存入 "number" →
 * 武器类原胚（paster_weapon）：1 → 攻击伤害 -5..10；2 → 攻击速度 -3..7；
 * 工具类原胚（paster_tool）：1 → 移动速度 -5..20；2 → 幸运 -2..5。
 * 原版 TileEntity 附带 9 格不可达库存，新版不再保留（无 GUI/掉落逻辑）。
 */
public class WorkshopGrindBlockEntity extends BlockEntity {

    /** 武器类原胚标签 */
    private static final TagKey<Item> PASTER_WEAPON =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "paster_weapon"));
    /** 工具类原胚标签 */
    private static final TagKey<Item> PASTER_TOOL =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "paster_tool"));

    /** 最近一次抽取的随机数（原版持久数据 "number"） */
    private int number;

    /**
     * 构造工坊磨石方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public WorkshopGrindBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.WORKSHOP_GRIND.get(), pos, state);
    }

    /**
     * 打磨随机强化（原版 WorkshopGrindInlay0Procedure）
     *
     * @param stash 待强化的原胚（直接原地修改）
     */
    public void applyGrindInlay(ItemStack stash) {
        if (level == null || level.isClientSide()) {
            return;
        }
        number = Mth.nextInt(level.getRandom(), 1, 2);
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        if (stash.is(PASTER_WEAPON)) {
            if (number == 1) {
                PasterItemData.putBoolean(stash, "paster_attack_damage", true);
                PasterItemData.putDouble(stash, "paster_attack_damage_number",
                        PasterItemData.getDouble(stash, "paster_attack_damage_number")
                                + Mth.nextInt(level.getRandom(), -5, 10));
            } else {
                PasterItemData.putBoolean(stash, "paster_attack_speed", true);
                PasterItemData.putDouble(stash, "paster_attack_speed_number",
                        PasterItemData.getDouble(stash, "paster_attack_speed_number")
                                + Mth.nextInt(level.getRandom(), -3, 7));
            }
        }
        if (stash.is(PASTER_TOOL)) {
            if (number == 1) {
                PasterItemData.putBoolean(stash, "paster_movement_speed", true);
                PasterItemData.putDouble(stash, "paster_movement_speed_number",
                        PasterItemData.getDouble(stash, "paster_movement_speed_number")
                                + Mth.nextInt(level.getRandom(), -5, 20));
            } else {
                PasterItemData.putBoolean(stash, "paster_luck", true);
                PasterItemData.putDouble(stash, "paster_luck_number",
                        PasterItemData.getDouble(stash, "paster_luck_number")
                                + Mth.nextInt(level.getRandom(), -2, 5));
            }
        }
    }

    // ==================== 持久化 ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putDouble("number", number);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        number = (int) tag.getDouble("number");
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
