package com.pasterdream.pasterdreammod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 法杖武器公共工具 (Wand Support)
 * <p>
 * 还原原版 MCreator procedure 中反复出现的两段公共逻辑：
 * <ul>
 *   <li>“遍历全背包，为所有 pasterdream:magic / pasterdream:skill 标签物品统一上冷却”——
 *       原版通过 ITEM_HANDLER capability 遍历 41 格（主背包+盔甲+副手），
 *       1.21.1 改为直接遍历 {@link Inventory} 的各分区，键名与语义保持一致</li>
 *   <li>Curios 饰品佩戴检查（对应原版 CuriosApi.findEquippedCurio）</li>
 * </ul>
 */
public final class WandSupport {

    /** 法术物品标签 pasterdream:magic（统一施法冷却的作用范围） */
    public static final TagKey<Item> MAGIC_TAG = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("pasterdream", "magic"));

    /** 战技物品标签 pasterdream:skill（统一战技冷却的作用范围） */
    public static final TagKey<Item> SKILL_TAG = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("pasterdream", "skill"));

    private WandSupport() {
    }

    /**
     * 为玩家背包内所有带指定标签的物品统一设置使用冷却
     * <p>
     * 与原版一致：对每个匹配的物品都调用一次 addCooldown（重复物品会覆盖为相同值）。
     *
     * @param player 玩家
     * @param tag    物品标签（{@link #MAGIC_TAG} / {@link #SKILL_TAG}）
     * @param ticks  冷却 tick 数
     */
    public static void applyTaggedCooldown(Player player, TagKey<Item> tag, int ticks) {
        Inventory inventory = player.getInventory();
        int size = inventory.getContainerSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty() && stack.is(tag)) {
                player.getCooldowns().addCooldown(stack.getItem(), ticks);
            }
        }
    }

    /**
     * 判断生物是否佩戴了指定 Curios 饰品（对应原版 findEquippedCurio / findFirstCurio）
     *
     * @param entity 生物
     * @param item   饰品物品
     * @return true 表示已佩戴
     */
    public static boolean hasCurioEquipped(LivingEntity entity, Item item) {
        return CuriosApi.getCuriosInventory(entity)
                .map(inv -> inv.findFirstCurio(item).isPresent())
                .orElse(false);
    }
}
