package com.pasterdream.pasterdreammod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 深海秘宝 / 染梦深海秘宝（deep_treasure_0 / deep_treasure_1）。
 * <p>
 * 还原自原版 DeepTreasure0Item / DeepTreasure1Item + DeepTreasurePr0Procedure：
 * 右键使用后播放箱子开启音效，按对应战利品表掉落随机战利品并消耗 1 个。
 * 若物品自定义数据中带有 {@code deep_treasure_super=true} 标记，
 * 则物品呈附魔光效并改用超级战利品表（*_super）。
 */
public class DeepTreasureItem extends Item {

    /** 超级秘宝的自定义数据标记键（与原版 NBT 键一致） */
    private static final String SUPER_TAG = "deep_treasure_super";

    /** 普通战利品表 */
    private final ResourceKey<LootTable> normalTable;

    /** 超级战利品表（deep_treasure_super 标记时使用） */
    private final ResourceKey<LootTable> superTable;

    /**
     * 构造深海秘宝
     *
     * @param tablePath 普通战利品表路径（如 {@code chests/loots_deep_treasure_0}），
     *                  超级表自动追加 {@code _super} 后缀
     */
    public DeepTreasureItem(String tablePath) {
        super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
        this.normalTable = ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath("pasterdream", tablePath));
        this.superTable = ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath("pasterdream", tablePath + "_super"));
    }

    /** 带有 deep_treasure_super 标记时呈附魔光效（对应原版 DeepTreasureIfProcedure） */
    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getBoolean(SUPER_TAG);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.pasterdream.deep_treasure.lore"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        // 开箱音效
        serverLevel.playSound(null, BlockPos.containing(x, y, z),
                SoundEvents.CHEST_OPEN, SoundSource.PLAYERS, 1.0f, 1.0f);

        // 按标记选择战利品表并掉落随机战利品
        ResourceKey<LootTable> tableKey = isFoil(stack) ? superTable : normalTable;
        LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(tableKey);
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, new Vec3(x, y, z))
                .create(LootContextParamSets.CHEST);
        for (ItemStack loot : table.getRandomItems(params)) {
            ItemEntity drop = new ItemEntity(serverLevel, x, y, z, loot);
            drop.setPickUpDelay(5);
            serverLevel.addFreshEntity(drop);
        }
        stack.shrink(1);
        return InteractionResultHolder.success(stack);
    }
}
