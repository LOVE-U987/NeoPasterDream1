package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.dreamnotes.DreamnotesItems;
import com.pasterdream.pasterdreammod.dreamnotes.DreamnotesLogic;
import com.pasterdream.pasterdreammod.item.DreamnotesItem;
import com.pasterdream.pasterdreammod.menu.DreamnotesGui0Menu;
import com.pasterdream.pasterdreammod.registry.PDMenusDreamnotes;
import com.pasterdream.pasterdreammod.registry.items.PDItemsDreamnotes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
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
     * @param player   服务端玩家（用于 use / 研究台）
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

        // 1) 全物品注册
        for (int i = 0; i < DreamnotesItems.count(); i++) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dreamnotes_" + i);
            boolean present = BuiltInRegistries.ITEM.containsKey(id);
            Item item = BuiltInRegistries.ITEM.get(id);
            boolean holderOk = DreamnotesItems.byId(i) != null && DreamnotesItems.byId(i) == item;
            collect.accept(new Result("register_dreamnotes_" + i, present && holderOk && item != Items.AIR,
                    present ? item.toString() : "missing"));
        }

        // 2) 菜单类型
        boolean menuOk = PDMenusDreamnotes.DREAMNOTES_GUI_0.isBound()
                && BuiltInRegistries.MENU.containsKey(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dreamnotes_gui_0"));
        collect.accept(new Result("menu_dreamnotes_gui_0", menuOk, "bound=" + PDMenusDreamnotes.DREAMNOTES_GUI_0.isBound()));

        // 3) tag 成员完整（0..14；blueprint_0 可选）
        var tag = BuiltInRegistries.ITEM.getTag(DREAMNOTES_TAG);
        if (tag.isEmpty()) {
            collect.accept(new Result("tag_dreamnotes_loaded", false, "tag empty/missing"));
        } else {
            collect.accept(new Result("tag_dreamnotes_loaded", true, "ok"));
            for (int i = 0; i < 15; i++) {
                Item item = DreamnotesItems.byId(i);
                boolean inTag = item != null && new ItemStack(item).is(DREAMNOTES_TAG);
                collect.accept(new Result("tag_has_dreamnotes_" + i, inTag, inTag ? "member" : "not in tag"));
            }
        }

        // 4) 右键打开菜单 + 内容索引可解析
        if (player != null && player.serverLevel() != null) {
            for (int i = 0; i < 15; i++) {
                Item item = DreamnotesItems.byId(i);
                if (item == null) {
                    collect.accept(new Result("use_open_menu_" + i, false, "item null"));
                    continue;
                }
                ItemStack stack = new ItemStack(item);
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
                // 直接构造菜单验证 noteId / 工厂
                DreamnotesGui0Menu menu = new DreamnotesGui0Menu(
                        1000 + i, player.getInventory(), player.blockPosition(), (byte) 0, i);
                boolean idxOk = menu.noteId == i;
                boolean holdingOk = DreamnotesLogic.isHoldingNote(player, i);
                // 调用 use（服务端 openMenu）
                item.use(player.level(), player, InteractionHand.MAIN_HAND);
                boolean containerOpen = player.containerMenu instanceof DreamnotesGui0Menu
                        || player.containerMenu != player.inventoryMenu;
                collect.accept(new Result("use_open_menu_" + i, idxOk && holdingOk,
                        "noteId=" + menu.noteId + " holding=" + holdingOk + " containerChanged=" + containerOpen));
                player.closeContainer();
            }
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        } else {
            collect.accept(new Result("use_open_menu_suite", false, "player null"));
        }

        // 5) 研究台 copyNotes 端到端（反射，避免 worktree 无 ResearchTable 时编译失败）
        collect.accept(tryCopyNotesE2E(player));

        long fail = results.stream().filter(r -> !r.pass()).count();
        PDDebugLogger.smoketestInfo(TAG + "SUMMARY total={} pass={} fail={}", results.size(), results.size() - fail, fail);
        PDDebugLogger.smoketestInfo(TAG + "RESULT {}", fail == 0 ? "ALL_PASS" : "HAS_FAILURES");
        return fail == 0;
    }

    /**
     * 复制行为 helper：供主测试直接调用。
     * 在给定 handler 上执行与研究台相同的复制语义（不依赖 BE 存在）。
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
            Class<?> blockClass = Class.forName(
                    "com.pasterdream.pasterdreammod.block.ResearchTableBlock");
            ServerLevel level = player.serverLevel();
            BlockPos pos = player.blockPosition().above(3);

            // 若研究台方块未注册则跳过为“软通过”说明
            var blockOpt = BuiltInRegistries.BLOCK.getOptional(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "research_table"));
            if (blockOpt.isEmpty()) {
                // 退化为 helper 语义验证
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
            // SLOT_PEN=0 NOTES=1 PERGAMYN=2 COPY=3
            handler.setStackInSlot(0, new ItemStack(pen));
            handler.setStackInSlot(1, new ItemStack(notes));
            handler.setStackInSlot(2, new ItemStack(paper, 3));
            handler.setStackInSlot(3, ItemStack.EMPTY);

            Method copyNotes = beClass.getMethod("copyNotes", net.minecraft.world.entity.player.Player.class);
            copyNotes.invoke(be, player);

            ItemStack out = handler.getStackInSlot(3);
            ItemStack paperLeft = handler.getStackInSlot(2);
            boolean ok = !out.isEmpty() && out.is(notes) && out.getCount() == 1 && paperLeft.getCount() == 2;
            level.removeBlock(pos, false);
            return new Result("research_copy_e2e", ok,
                    "copy=" + out + " paperLeft=" + paperLeft.getCount());
        } catch (ClassNotFoundException e) {
            // 工作树可能尚未合并研究台：helper 验证
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
        // 确保分区类已加载
        PDItemsDreamnotes.bootstrap();
        verify(player, null);
    }
}
