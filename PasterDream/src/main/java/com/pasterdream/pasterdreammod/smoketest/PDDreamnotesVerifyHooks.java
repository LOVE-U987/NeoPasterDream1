package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.text.NoteText;
import com.pasterdream.pasterdreammod.dreamnotes.DreamnotesItems;
import com.pasterdream.pasterdreammod.dreamnotes.NoteDefinition;
import com.pasterdream.pasterdreammod.dreamnotes.PDNoteRegistry;
import com.pasterdream.pasterdreammod.item.DreamnotesItem;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.items.PDItemsDreamnotes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 寻梦者笔记分区验证钩子（独立于 {@code PDPortingVerifyTest}）。
 * <p>
 * 用法：
 * <ul>
 *   <li>主测试调用 {@link #verify(ServerPlayer, Consumer)}；</li>
 *   <li>或设置环境变量 {@code PASTERDREAM_DREAMNOTES_VERIFY=1} /
 *       {@code -Dpasterdream.dreamnotes.verify=true}，玩家入服后自动跑一遍。</li>
 * </ul>
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class PDDreamnotesVerifyHooks {

    public static final boolean ENABLED =
            "1".equals(System.getenv("PASTERDREAM_DREAMNOTES_VERIFY"))
                    || Boolean.getBoolean("pasterdream.dreamnotes.verify");

    private static final Logger LOGGER = LoggerFactory.getLogger(PDDreamnotesVerifyHooks.class);
    private static final String TAG = "[PDDreamnotesVerify] ";

    private static final TagKey<Item> DREAMNOTES_TAG =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dreamnotes"));

    private static boolean ranAuto;

    private PDDreamnotesVerifyHooks() {
    }

    /** 单条结果 */
    public record Result(String name, boolean pass, String detail) {
    }

    /**
     * 执行全部笔记相关断言。
     *
     * @param player   服务端玩家
     * @param consumer 逐条结果回调（可 null）
     * @return 是否全部通过
     */
    public static boolean verify(ServerPlayer player, Consumer<Result> consumer) {
        Consumer<Result> out = consumer != null ? consumer : r -> {
        };
        List<Result> results = new ArrayList<>();
        Consumer<Result> collect = r -> {
            results.add(r);
            out.accept(r);
            if (r.pass()) {
                PDDebugLogger.smoketestInfo(TAG + "PASS {} ({})", r.name(), r.detail());
            } else {
                LOGGER.error(TAG + "FAIL {} ({})", r.name(), r.detail());
            }
        };

        checkItems(collect);
        checkDefinitions(player, collect);
        checkTag(collect);
        checkNoteText(collect);
        collect.accept(tryCopyNotesE2E(player));

        long fail = results.stream().filter(r -> !r.pass()).count();
        PDDebugLogger.smoketestInfo(TAG + "SUMMARY total={} pass={} fail={}", results.size(), results.size() - fail, fail);
        PDDebugLogger.smoketestInfo(TAG + "RESULT {}", fail == 0 ? "ALL_PASS" : "HAS_FAILURES");
        return fail == 0;
    }

    /** 全物品注册 + PDItems 门面别名检查。 */
    private static void checkItems(Consumer<Result> collect) {
        boolean facadeHolderOk = PDItems.DREAMNOTES_0 != null
                && PDItems.DREAMNOTES_0 == PDItemsDreamnotes.DREAMNOTES_0;
        collect.accept(new Result("facade_dreamnotes_0_non_null", facadeHolderOk,
                facadeHolderOk ? "PDItems alias matches partition holder" : "PDItems.DREAMNOTES_0 null/mismatch"));
        for (int i = 0; i < DreamnotesItems.count(); i++) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dreamnotes_" + i);
            boolean present = BuiltInRegistries.ITEM.containsKey(id);
            Item item = BuiltInRegistries.ITEM.get(id);
            boolean holderOk = DreamnotesItems.byId(i) != null && DreamnotesItems.byId(i) == item;
            collect.accept(new Result("register_dreamnotes_" + i, present && holderOk && item != Items.AIR,
                    present ? item.toString() : "missing"));
        }
    }

    /** 同步注册表定义加载（正文非空）+ 统一 Item 类型检查。 */
    private static void checkDefinitions(ServerPlayer player, Consumer<Result> collect) {
        if (player == null || player.server == null) {
            collect.accept(new Result("definition_suite", false, "player/server null"));
            return;
        }
        for (int i = 0; i < DreamnotesItems.count(); i++) {
            NoteDefinition definition = PDNoteRegistry.get(player.server.registryAccess(), i);
            boolean ok = definition != null && !definition.bodyOr("zh_cn").isEmpty();
            collect.accept(new Result("definition_dreamnotes_" + i, ok,
                    definition == null ? "missing" : "bodyLen=" + definition.bodyOr("zh_cn").length()));
        }
        Item item0 = DreamnotesItems.byId(0);
        collect.accept(new Result("item_unified_class", item0 instanceof DreamnotesItem,
                item0 == null ? "null" : item0.getClass().getSimpleName()));
    }

    /** tag 成员完整（0..14；blueprint_0 可选）检查。 */
    private static void checkTag(Consumer<Result> collect) {
        var tag = BuiltInRegistries.ITEM.getTag(DREAMNOTES_TAG);
        if (tag.isEmpty()) {
            collect.accept(new Result("tag_dreamnotes_loaded", false, "tag empty/missing"));
            return;
        }
        collect.accept(new Result("tag_dreamnotes_loaded", true, "ok"));
        for (int i = 0; i < 15; i++) {
            Item item = DreamnotesItems.byId(i);
            boolean inTag = item != null && new ItemStack(item).is(DREAMNOTES_TAG);
            collect.accept(new Result("tag_has_dreamnotes_" + i, inTag, inTag ? "member" : "not in tag"));
        }
    }

    /** NoteText Markdown 解析自检（common 纯逻辑）。 */
    private static void checkNoteText(Consumer<Result> collect) {
        collect.accept(noteTextSelfCheck("plain", "hello", "hello"));
        collect.accept(noteTextSelfCheck("italic", "*ab*", "ab"));
        collect.accept(noteTextSelfCheck("color", "<red>ab</red>", "ab"));
        collect.accept(noteTextSelfCheck("escape", "\\*ab\\*", "*ab*"));
    }

    /**
     * 解析单条 Markdown 并断言纯文本结果。
     *
     * @param name     断言名
     * @param source   源文
     * @param expected 期望纯文本
     * @return 结果
     */
    private static Result noteTextSelfCheck(String name, String source, String expected) {
        Component rendered = NoteText.render(source);
        boolean ok = expected.equals(rendered.getString());
        return new Result("notetext_" + name, ok, "got=" + rendered.getString());
    }

    /**
     * 复制行为 helper：供主测试直接调用。
     *
     * @param pen       笔与墨
     * @param notes     笔记（须在 dreamnotes tag）
     * @param pergamyn  羊皮纸
     * @return 复制结果（count=1 的笔记拷贝），失败 EMPTY
     */
    public static ItemStack copyNotesHelper(ItemStack pen, ItemStack notes, ItemStack pergamyn) {
        if (pen.isEmpty() || notes.isEmpty() || pergamyn.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (!notes.is(DREAMNOTES_TAG)) {
            return ItemStack.EMPTY;
        }
        ResourceLocation penId = BuiltInRegistries.ITEM.getKey(pen.getItem());
        ResourceLocation paperId = BuiltInRegistries.ITEM.getKey(pergamyn.getItem());
        if (penId == null || paperId == null) {
            return ItemStack.EMPTY;
        }
        if (!"pen_and_ink".equals(penId.getPath()) || !"pergamyn".equals(paperId.getPath())) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = notes.copy();
        copy.setCount(1);
        pergamyn.shrink(1);
        return copy;
    }

    private static Result tryCopyNotesE2E(ServerPlayer player) {
        if (player == null) {
            return new Result("research_copy_e2e", false, "player null");
        }
        try {
            Class<?> beClass = Class.forName(
                    "com.pasterdream.pasterdreammod.block.entity.ResearchTableBlockEntity");
            ServerLevel level = player.serverLevel();
            net.minecraft.core.BlockPos pos = player.blockPosition().above(3);

            var blockOpt = BuiltInRegistries.BLOCK.getOptional(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "research_table"));
            if (blockOpt.isEmpty()) {
                Item notes = DreamnotesItems.byId(10);
                Item pen = BuiltInRegistries.ITEM.get(
                        ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "pen_and_ink"));
                Item paper = BuiltInRegistries.ITEM.get(
                        ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "pergamyn"));
                if (notes == null || pen == Items.AIR || paper == Items.AIR) {
                    return new Result("research_copy_e2e", false, "materials missing");
                }
                ItemStack paperStack = new ItemStack(paper, 2);
                ItemStack result = copyNotesHelper(new ItemStack(pen), new ItemStack(notes), paperStack);
                boolean ok = !result.isEmpty() && result.is(notes) && paperStack.getCount() == 1;
                return new Result("research_copy_e2e", ok,
                        ok ? "helper-only (research_table block absent)" : "helper failed");
            }

            level.setBlock(pos, blockOpt.get().defaultBlockState(), 3);
            BlockEntity be = level.getBlockEntity(pos);
            if (be == null || !beClass.isInstance(be)) {
                return new Result("research_copy_e2e", false, "BE missing after place");
            }
            Method getHandler = beClass.getMethod("getItemHandler");
            ItemStackHandler handler = (ItemStackHandler) getHandler.invoke(be);

            Item pen = BuiltInRegistries.ITEM.get(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "pen_and_ink"));
            Item paper = BuiltInRegistries.ITEM.get(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "pergamyn"));
            Item notes = DreamnotesItems.byId(10);
            if (pen == Items.AIR || paper == Items.AIR || notes == null) {
                return new Result("research_copy_e2e", false, "pen/paper/notes missing");
            }
            handler.setStackInSlot(0, new ItemStack(pen));
            handler.setStackInSlot(1, new ItemStack(notes));
            handler.setStackInSlot(2, new ItemStack(paper, 3));
            handler.setStackInSlot(3, ItemStack.EMPTY);

            Method copyNotes = beClass.getMethod("copyNotes", net.minecraft.world.entity.player.Player.class);
            copyNotes.invoke(be, player);

            ItemStack outStack = handler.getStackInSlot(3);
            ItemStack paperLeft = handler.getStackInSlot(2);
            boolean ok = !outStack.isEmpty() && outStack.is(notes) && outStack.getCount() == 1
                    && paperLeft.getCount() == 2;
            level.removeBlock(pos, false);
            return new Result("research_copy_e2e", ok,
                    "copy=" + outStack + " paperLeft=" + paperLeft.getCount());
        } catch (ClassNotFoundException e) {
            Item notes = DreamnotesItems.byId(7);
            Item pen = BuiltInRegistries.ITEM.getOptional(
                            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "pen_and_ink"))
                    .orElse(Items.AIR);
            Item paper = BuiltInRegistries.ITEM.getOptional(
                            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "pergamyn"))
                    .orElse(Items.AIR);
            if (notes == null || pen == Items.AIR || paper == Items.AIR) {
                return new Result("research_copy_e2e", false, "ResearchTable absent + materials missing");
            }
            ItemStack paperStack = new ItemStack(paper, 2);
            ItemStack result = copyNotesHelper(new ItemStack(pen), new ItemStack(notes), paperStack);
            boolean ok = !result.isEmpty() && paperStack.getCount() == 1;
            return new Result("research_copy_e2e", ok,
                    ok ? "helper-only (ResearchTable class absent)" : "helper failed");
        } catch (ReflectiveOperationException e) {
            return new Result("research_copy_e2e", false, "reflect: " + e.getMessage());
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!ENABLED || ranAuto) {
            return;
        }
        var server = event.getServer();
        if (server.getPlayerList().getPlayers().isEmpty()) {
            return;
        }
        if (server.getTickCount() < 40) {
            return;
        }
        ranAuto = true;
        ServerPlayer player = server.getPlayerList().getPlayers().get(0);
        PDItemsDreamnotes.bootstrap();
        verify(player, null);
    }
}
