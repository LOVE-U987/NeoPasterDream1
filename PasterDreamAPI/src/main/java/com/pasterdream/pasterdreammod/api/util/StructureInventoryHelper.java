package com.pasterdream.pasterdreammod.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 结构 NBT / 世界存档 双格式库存读写。
 * <p>
 * 原版 1.20 结构与 MCreator BE 使用原版键 {@code Items}（+ 可选 {@code LootTable}）；
 * 重写版 BE 持久化使用 NeoForge {@code inventory}（{@link ItemStackHandler#serializeNBT}）。
 * 结构放置走 {@code loadWithComponents} → {@code loadAdditional}，若不识别 {@code Items}
 * 则书桌/箱子内的成书等会静默丢失。
 * <p>
 * 物品解析：优先 {@link ItemStack#parse}（DFU 后 components）；失败时对
 * {@code written_book} 的旧 {@code tag{pages,title,author}} 做手工迁移。
 *
 * <p>用途：结构容器双格式 NBT 读写辅助（支持原版结构与本模组持久化格式互操作）。
 */
public final class StructureInventoryHelper {

    public static final String TAG_INVENTORY = "inventory";
    public static final String TAG_ITEMS = "Items";
    public static final String TAG_LOOT_TABLE = "LootTable";
    public static final String TAG_LOOT_TABLE_SEED = "LootTableSeed";

    private StructureInventoryHelper() {
    }

    /**
     * 将 BE 标签写入 handler：优先 {@code inventory}（本模组存档），否则 {@code Items}（结构/原版）。
     *
     * @return 是否从任一格式载入了数据（含空列表）
     */
    public static boolean loadItemHandler(ItemStackHandler handler, CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains(TAG_INVENTORY, Tag.TAG_COMPOUND)) {
            handler.deserializeNBT(registries, tag.getCompound(TAG_INVENTORY));
            return true;
        }
        if (tag.contains(TAG_ITEMS, Tag.TAG_LIST)) {
            loadVanillaItems(handler, tag.getList(TAG_ITEMS, Tag.TAG_COMPOUND), registries);
            return true;
        }
        return false;
    }

    /**
     * 解析原版 {@code Items} 列表写入 handler（按 Slot；无 Slot 时按列表下标）。
     */
    public static void loadVanillaItems(ItemStackHandler handler, ListTag items, HolderLookup.Provider registries) {
        for (int i = 0; i < items.size(); i++) {
            CompoundTag entry = items.getCompound(i).copy();
            int slot = entry.contains("Slot", Tag.TAG_ANY_NUMERIC)
                    ? entry.getInt("Slot") & 0xFF
                    : i;
            entry.remove("Slot");
            if (slot < 0 || slot >= handler.getSlots()) {
                continue;
            }
            ItemStack stack = parseItemStack(entry, registries);
            if (!stack.isEmpty()) {
                handler.setStackInSlot(slot, stack);
            }
        }
    }

    /**
     * 从结构/旧版物品标签构建 ItemStack。
     */
    public static ItemStack parseItemStack(CompoundTag entry, HolderLookup.Provider registries) {
        CompoundTag working = entry.copy();
        normalizeLegacyItemKeys(working);

        // 旧成书：先走手工迁移。codec 可能只认 id/count 而丢掉 tag.pages，得到空白书
        if (isLegacyWrittenBook(working)) {
            ItemStack legacyBook = tryLegacyWrittenBook(working, registries);
            if (!legacyBook.isEmpty()) {
                return legacyBook;
            }
        }

        Optional<ItemStack> parsed = ItemStack.parse(registries, working);
        if (parsed.isPresent() && !parsed.get().isEmpty()) {
            ItemStack stack = parsed.get();
            // 解析结果是空白成书但源 NBT 仍有 pages 时，强制回退
            if (stack.is(Items.WRITTEN_BOOK)
                    && !stack.has(DataComponents.WRITTEN_BOOK_CONTENT)
                    && working.contains("tag", Tag.TAG_COMPOUND)) {
                ItemStack legacyBook = tryLegacyWrittenBook(working, registries);
                if (!legacyBook.isEmpty()) {
                    return legacyBook;
                }
            }
            return stack;
        }

        // 最小 id+count 回退（无组件物品仍可进格）
        return tryBareItem(working);
    }

    private static boolean isLegacyWrittenBook(CompoundTag item) {
        return "minecraft:written_book".equals(item.getString("id"))
                && item.contains("tag", Tag.TAG_COMPOUND)
                && !item.contains("components", Tag.TAG_COMPOUND);
    }

    /**
     * 旧版物品标签：{@code Count} → {@code count}(int)。
     */
    public static void normalizeLegacyItemKeys(CompoundTag item) {
        if (!item.contains("count") && item.contains("Count")) {
            int count;
            if (item.contains("Count", Tag.TAG_BYTE)) {
                count = item.getByte("Count") & 0xFF;
            } else if (item.contains("Count", Tag.TAG_SHORT)) {
                count = item.getShort("Count");
            } else if (item.contains("Count", Tag.TAG_INT)) {
                count = item.getInt("Count");
            } else {
                count = 1;
            }
            item.putInt("count", Math.max(1, count));
            item.remove("Count");
        }
    }

    private static ItemStack tryLegacyWrittenBook(CompoundTag item, HolderLookup.Provider registries) {
        String id = item.getString("id");
        if (!"minecraft:written_book".equals(id) || !item.contains("tag", Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }
        CompoundTag tag = item.getCompound("tag");
        String title = tag.contains("title", Tag.TAG_STRING) ? tag.getString("title") : "";
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        String author = tag.contains("author", Tag.TAG_STRING) ? tag.getString("author") : "";
        int generation = tag.contains("generation", Tag.TAG_ANY_NUMERIC) ? tag.getInt("generation") : 0;
        generation = Math.max(0, Math.min(3, generation));
        boolean resolved = false;
        if (tag.contains("resolved", Tag.TAG_BYTE) || tag.contains("resolved", Tag.TAG_ANY_NUMERIC)) {
            resolved = tag.getBoolean("resolved") || tag.getByte("resolved") != 0;
        }

        List<Filterable<Component>> pages = new ArrayList<>();
        if (tag.contains("pages", Tag.TAG_LIST)) {
            ListTag pageList = tag.getList("pages", Tag.TAG_STRING);
            for (int i = 0; i < pageList.size(); i++) {
                String raw = pageList.getString(i);
                Component page = null;
                try {
                    page = Component.Serializer.fromJson(raw, registries);
                } catch (Exception ignored) {
                    // fall through
                }
                if (page == null) {
                    try {
                        page = Component.Serializer.fromJsonLenient(raw, registries);
                    } catch (Exception ignored) {
                        page = Component.literal(raw);
                    }
                }
                pages.add(Filterable.passThrough(page));
            }
        }

        int count = item.contains("count", Tag.TAG_ANY_NUMERIC) ? Math.max(1, item.getInt("count")) : 1;
        ItemStack stack = new ItemStack(Items.WRITTEN_BOOK, count);
        stack.set(DataComponents.WRITTEN_BOOK_CONTENT,
                new WrittenBookContent(Filterable.passThrough(title), author, generation, List.copyOf(pages), resolved));
        return stack;
    }

    private static ItemStack tryBareItem(CompoundTag item) {
        if (!item.contains("id", Tag.TAG_STRING)) {
            return ItemStack.EMPTY;
        }
        ResourceLocation rl;
        try {
            rl = ResourceLocation.parse(item.getString("id"));
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
        Item type = BuiltInRegistries.ITEM.get(rl);
        if (type == Items.AIR) {
            return ItemStack.EMPTY;
        }
        int count = item.contains("count", Tag.TAG_ANY_NUMERIC) ? Math.max(1, item.getInt("count")) : 1;
        return new ItemStack(type, count);
    }

    /**
     * 从标签读取 LootTable / LootTableSeed。
     *
     * @return 非 null 表示结构挂了战利品表（此时通常无 Items）
     */
    @Nullable
    public static ResourceKey<LootTable> readLootTable(CompoundTag tag) {
        if (!tag.contains(TAG_LOOT_TABLE, Tag.TAG_STRING)) {
            return null;
        }
        String raw = tag.getString(TAG_LOOT_TABLE);
        if (raw.isEmpty()) {
            return null;
        }
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(raw));
    }

    public static long readLootTableSeed(CompoundTag tag) {
        return tag.contains(TAG_LOOT_TABLE_SEED, Tag.TAG_LONG) ? tag.getLong(TAG_LOOT_TABLE_SEED) : 0L;
    }

    public static void writeLootTable(CompoundTag tag, @Nullable ResourceKey<LootTable> key, long seed) {
        if (key == null) {
            return;
        }
        tag.putString(TAG_LOOT_TABLE, key.location().toString());
        if (seed != 0L) {
            tag.putLong(TAG_LOOT_TABLE_SEED, seed);
        }
    }

    /**
     * 首次打开时把战利品表填入 handler，并清空 key（与原版 RandomizableContainer 一致）。
     *
     * @return 是否实际解包
     */
    public static boolean unpackLootTable(
            ItemStackHandler handler,
            @Nullable ResourceKey<LootTable> lootTable,
            long lootTableSeed,
            ServerLevel level,
            BlockPos pos,
            @Nullable Player player,
            Runnable clearLootTable
    ) {
        if (lootTable == null) {
            return false;
        }
        LootTable table = level.getServer().reloadableRegistries().getLootTable(lootTable);
        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos));
        if (player != null) {
            builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
        }
        SimpleContainer buffer = new SimpleContainer(handler.getSlots());
        table.fill(buffer, builder.create(LootContextParamSets.CHEST), lootTableSeed);
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, buffer.getItem(i));
        }
        clearLootTable.run();
        if (player instanceof ServerPlayer serverPlayer) {
            net.minecraft.advancements.CriteriaTriggers.GENERATE_LOOT.trigger(serverPlayer, lootTable);
        }
        return true;
    }
}
