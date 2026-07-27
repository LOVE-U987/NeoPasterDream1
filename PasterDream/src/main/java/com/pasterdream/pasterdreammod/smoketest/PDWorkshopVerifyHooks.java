package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.WeaponWorkshopBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WorkshopAnvilBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WorkshopBlastBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WorkshopCauldeonBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.WorkshopGrindBlockEntity;
import com.pasterdream.pasterdreammod.menu.WeaponWorkshopMenu;
import com.pasterdream.pasterdreammod.menu.WorkshopAnvilMenu;
import com.pasterdream.pasterdreammod.menu.WorkshopBlastMenu;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import com.pasterdream.pasterdreammod.util.WeaponWorkshopVariables;
import com.pasterdream.pasterdreammod.util.WorkshopMultiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 武器工坊群运行时校验钩子（精铸工坊 / 锻炉 / 铁砧 / 冷却盆 / 磨盘 / 展示桌）。
 * <p>
 * 覆盖：注册完备、锻造按钮 E2E、强化石镶嵌、锻炉岩浆入炉出炉、铁砧锤炼小游戏、
 * 淬火/打磨强化 API、多结构辅助类在场。供 {@link PDPortingVerifyTest} 调用。
 */
public final class PDWorkshopVerifyHooks {

    /** 单条断言结果 */
    public record Result(boolean pass, String name, String detail) {
    }

    private static final TagKey<Item> EMBRYO_ITEMS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "embryo_items"));
    private static final TagKey<Item> PASTER_WEAPON = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "paster_weapon"));

    private static final String[] BLOCKS = {
            "weapon_table", "weapon_workshop", "workshop_anvil",
            "workshop_blast", "workshop_cauldeon", "workshop_grind"
    };
    private static final String[] MENUS = {
            "weapon_workshop", "workshop_anvil", "workshop_blast"
    };
    private static final String[] BLOCK_ENTITIES = {
            "weapon_table", "weapon_workshop", "workshop_anvil",
            "workshop_blast", "workshop_cauldeon", "workshop_grind"
    };

    private PDWorkshopVerifyHooks() {
    }

    /**
     * 运行工坊群全部校验
     *
     * @param player 服务端玩家（放置方块 / 打开菜单）
     * @param out    断言输出
     */
    public static void verify(ServerPlayer player, Consumer<Result> out) {
        verifyRegistry(out);
        verifyTags(out);
        verifyHelpers(out);
        if (player == null) {
            out.accept(new Result(false, "工坊行为测跳过", "player == null"));
            return;
        }
        ServerLevel level = player.serverLevel();
        // stillValid 距离上限约 8 格：全部落在玩家旁
        BlockPos base = anchor(level, player, 3, 2);

        try {
            verifyWeaponWorkshopForge(level, player, base.offset(0, 0, 0), out);
            verifyWorkshopBlast(level, player, base.offset(2, 0, 0), out);
            verifyWorkshopAnvil(level, player, base.offset(0, 0, 2), out);
            verifyQuenchAndGrind(level, player, base.offset(2, 0, 2), out);
        } finally {
            // 避免跨测例污染全局原胚暂存
            WeaponWorkshopVariables.weaponWorkshopItem = ItemStack.EMPTY;
        }
    }

    // ==================== 注册 / 标签 ====================

    private static void verifyRegistry(Consumer<Result> out) {
        List<String> missing = new ArrayList<>();
        for (String path : BLOCKS) {
            if (!BuiltInRegistries.BLOCK.containsKey(rl(path))) {
                missing.add("block:" + path);
            }
            if (!BuiltInRegistries.ITEM.containsKey(rl(path))) {
                missing.add("item:" + path);
            }
        }
        for (String path : MENUS) {
            if (!BuiltInRegistries.MENU.containsKey(rl(path))) {
                missing.add("menu:" + path);
            }
        }
        for (String path : BLOCK_ENTITIES) {
            if (!BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(rl(path))) {
                missing.add("be:" + path);
            }
        }
        // Deferred 入口非空
        try {
            if (PDMenus.WEAPON_WORKSHOP.get() == null) {
                missing.add("PDMenus.WEAPON_WORKSHOP");
            }
            if (PDMenus.WORKSHOP_ANVIL.get() == null) {
                missing.add("PDMenus.WORKSHOP_ANVIL");
            }
            if (PDMenus.WORKSHOP_BLAST.get() == null) {
                missing.add("PDMenus.WORKSHOP_BLAST");
            }
            if (PDBlocks.WEAPON_WORKSHOP.get() == null) {
                missing.add("PDBlocks.WEAPON_WORKSHOP");
            }
        } catch (Exception e) {
            missing.add("deferred:" + e.getClass().getSimpleName());
        }
        out.accept(detail(missing.isEmpty(),
                "工坊群方块/物品/菜单/BE 注册齐全",
                missing.isEmpty() ? "6 方块 + 3 菜单 + 6 BE" : "缺失 " + missing));
    }

    private static void verifyTags(Consumer<Result> out) {
        Item embryo = item("dream_wand_embryo");
        boolean embryoTagged = embryo != Items.AIR && new ItemStack(embryo).is(EMBRYO_ITEMS);
        out.accept(detail(embryoTagged,
                "dream_wand_embryo ∈ #pasterdream:embryo_items",
                embryo == Items.AIR ? "物品未注册" : "tagged=" + embryoTagged));

        // 配方关键材料
        String[] mats = {
                "meltdream_crystal_0", "titanium_ingot", "blackstick",
                "dyedream_dust", "dyedreamquartz", "dream_wand_embryo",
                "enhance_stone_0", "enhance_stone_1", "tabitem_1"
        };
        List<String> miss = new ArrayList<>();
        for (String m : mats) {
            if (item(m) == Items.AIR) {
                miss.add(m);
            }
        }
        out.accept(detail(miss.isEmpty(),
                "工坊关键材料物品在场 " + (mats.length - miss.size()) + "/" + mats.length,
                miss.isEmpty() ? "全部在场" : "缺失 " + miss));
    }

    private static void verifyHelpers(Consumer<Result> out) {
        // 类加载即视为在场（多结构放置工具 + 全局原胚暂存）
        boolean multi = WorkshopMultiBlock.class.getName().contains("WorkshopMultiBlock");
        boolean vars = WeaponWorkshopVariables.class.getName().contains("WeaponWorkshopVariables");
        out.accept(detail(multi && vars,
                "WorkshopMultiBlock / WeaponWorkshopVariables 在场",
                "multi=" + multi + " vars=" + vars));
    }

    // ==================== 精铸工坊锻造 E2E ====================

    /**
     * 聚梦法杖原胚配方：融梦水晶+钛锭+黑金属棒+染梦尘+染梦石英 → dream_wand_embryo；
     * 同步点「锻造」按钮并立即结算镶嵌（强化石Ⅰ）。
     */
    private static void verifyWeaponWorkshopForge(ServerLevel level, ServerPlayer player,
                                                   BlockPos pos, Consumer<Result> out) {
        pos = surface(level, pos);
        level.setBlock(pos, PDBlocks.WEAPON_WORKSHOP.get().defaultBlockState(), 3);
        BlockEntity raw = level.getBlockEntity(pos);
        if (!(raw instanceof WeaponWorkshopBlockEntity be)) {
            out.accept(new Result(false, "精铸工坊 BE 已创建", "got=" + raw));
            level.removeBlock(pos, false);
            return;
        }
        out.accept(new Result(true, "精铸工坊 BE 已创建", "pos=" + pos.toShortString()));

        var handler = be.getItemHandler();
        out.accept(detail(handler.getSlots() == WeaponWorkshopBlockEntity.SLOT_COUNT,
                "精铸工坊库存 " + handler.getSlots() + " 槽",
                "期望 " + WeaponWorkshopBlockEntity.SLOT_COUNT));

        // 填充聚梦法杖原胚配方
        handler.setStackInSlot(0, new ItemStack(item("meltdream_crystal_0")));
        handler.setStackInSlot(1, new ItemStack(item("titanium_ingot")));
        handler.setStackInSlot(2, new ItemStack(item("blackstick")));
        handler.setStackInSlot(3, new ItemStack(item("dyedream_dust")));
        handler.setStackInSlot(4, new ItemStack(item("dyedreamquartz")));
        handler.setStackInSlot(WeaponWorkshopBlockEntity.SLOT_ENHANCE,
                new ItemStack(item("enhance_stone_0")));
        handler.setStackInSlot(WeaponWorkshopBlockEntity.SLOT_RESULT, ItemStack.EMPTY);

        // 菜单 + 锻造按钮
        WeaponWorkshopMenu menu = new WeaponWorkshopMenu(9001, player.getInventory(), be);
        boolean button = menu.clickMenuButton(player, WeaponWorkshopMenu.BUTTON_FORGE);
        out.accept(detail(button, "精铸工坊 clickMenuButton(FORGE) 受理", "button=" + button));

        ItemStack crafted = handler.getStackInSlot(WeaponWorkshopBlockEntity.SLOT_RESULT);
        boolean isEmbryo = crafted.is(item("dream_wand_embryo"));
        out.accept(detail(isEmbryo,
                "精铸工坊产出 dream_wand_embryo",
                "result=" + summarize(crafted)));

        // 材料应被各扣 1
        boolean matsConsumed = handler.getStackInSlot(0).isEmpty()
                && handler.getStackInSlot(1).isEmpty()
                && handler.getStackInSlot(2).isEmpty()
                && handler.getStackInSlot(3).isEmpty()
                && handler.getStackInSlot(4).isEmpty();
        out.accept(detail(matsConsumed, "精铸工坊材料槽 0-4 已消耗", "ok"));

        // 立即镶嵌结算（测试中不等 ServerScheduler 1 tick）
        be.processInlay();
        ItemStack enhanced = handler.getStackInSlot(WeaponWorkshopBlockEntity.SLOT_RESULT);
        boolean dmgFlag = PasterItemData.getBoolean(enhanced, "paster_attack_damage");
        double dmg = PasterItemData.getDouble(enhanced, "paster_attack_damage_number");
        boolean stoneConsumed = handler.getStackInSlot(WeaponWorkshopBlockEntity.SLOT_ENHANCE).isEmpty();
        out.accept(detail(dmgFlag && dmg >= 1 && dmg <= 5 && stoneConsumed,
                "强化石Ⅰ镶嵌：攻击伤害 +1..5 且消耗强化石",
                "flag=" + dmgFlag + " dmg=" + dmg + " stoneEmpty=" + stoneConsumed));

        // 菜单 stillValid
        out.accept(detail(menu.stillValid(player), "精铸工坊菜单 stillValid", "ok"));
        menu.removed(player);

        level.removeBlock(pos, false);
    }

    // ==================== 工坊锻炉：岩浆桶 + 煅烧 ====================

    private static void verifyWorkshopBlast(ServerLevel level, ServerPlayer player,
                                             BlockPos pos, Consumer<Result> out) {
        pos = surface(level, pos);
        level.setBlock(pos, PDBlocks.WORKSHOP_BLAST.get().defaultBlockState(), 3);
        BlockEntity raw = level.getBlockEntity(pos);
        if (!(raw instanceof WorkshopBlastBlockEntity be)) {
            out.accept(new Result(false, "工坊锻炉 BE 已创建", "got=" + raw));
            level.removeBlock(pos, false);
            return;
        }
        out.accept(new Result(true, "工坊锻炉 BE 已创建", "ok"));

        var handler = be.getItemHandler();
        out.accept(detail(handler.getSlots() == 5, "工坊锻炉库存 5 槽", "slots=" + handler.getSlots()));

        // 准备：原胚 process=0 + 岩浆桶；清空全局暂存 process
        ItemStack embryo = new ItemStack(item("dream_wand_embryo"));
        PasterItemData.putDouble(embryo, "process", 0);
        WeaponWorkshopVariables.weaponWorkshopItem = embryo.copy();
        handler.setStackInSlot(WorkshopBlastBlockEntity.SLOT_INPUT, embryo);
        handler.setStackInSlot(WorkshopBlastBlockEntity.SLOT_BUCKET_IN, new ItemStack(Items.LAVA_BUCKET));
        handler.setStackInSlot(WorkshopBlastBlockEntity.SLOT_RESULT, ItemStack.EMPTY);

        // 第一次 tick：吸岩浆桶
        be.tickBlast();
        boolean bucketOut = handler.getStackInSlot(WorkshopBlastBlockEntity.SLOT_BUCKET_OUT).is(Items.BUCKET);
        boolean fluidOk = be.getFluidAmount() >= 1000 || bucketOut;
        // 入炉可能同 tick 发生（吸岩浆后 processRecipe）
        out.accept(detail(bucketOut || be.getFluidAmount() > 0,
                "工坊锻炉吸入岩浆桶",
                "bucketOut=" + bucketOut + " fluid=" + be.getFluidAmount()));

        // 若尚未入炉，再 tick 一次
        if (handler.getStackInSlot(WorkshopBlastBlockEntity.SLOT_INPUT).is(EMBRYO_ITEMS)) {
            be.tickBlast();
        }

        // 推进煅烧至出炉（FINISH_COUNT=23；每次 tickBlast 在 switchOn 时 +1）
        for (int i = 0; i < 30; i++) {
            be.tickBlast();
            if (!handler.getStackInSlot(WorkshopBlastBlockEntity.SLOT_RESULT).isEmpty()) {
                break;
            }
        }

        ItemStack result = handler.getStackInSlot(WorkshopBlastBlockEntity.SLOT_RESULT);
        double process = PasterItemData.getDouble(result, "process");
        out.accept(detail(!result.isEmpty() && process >= 1,
                "工坊锻炉出炉：原胚 process≥1",
                "result=" + summarize(result) + " process=" + process));

        WorkshopBlastMenu menu = new WorkshopBlastMenu(9002, player.getInventory(), be);
        out.accept(detail(menu.stillValid(player), "工坊锻炉菜单 stillValid", "ok"));
        out.accept(detail(menu.getFluidAmount() >= 0, "工坊锻炉菜单可读流体量",
                "fluid=" + menu.getFluidAmount()));
        menu.removed(player);

        level.removeBlock(pos, false);
    }

    // ==================== 工坊铁砧：锤炼小游戏 ====================

    private static void verifyWorkshopAnvil(ServerLevel level, ServerPlayer player,
                                            BlockPos pos, Consumer<Result> out) {
        pos = surface(level, pos);
        level.setBlock(pos, PDBlocks.WORKSHOP_ANVIL.get().defaultBlockState(), 3);
        BlockEntity raw = level.getBlockEntity(pos);
        if (!(raw instanceof WorkshopAnvilBlockEntity be)) {
            out.accept(new Result(false, "工坊铁砧 BE 已创建", "got=" + raw));
            level.removeBlock(pos, false);
            return;
        }
        out.accept(new Result(true, "工坊铁砧 BE 已创建", "ok"));

        var handler = be.getItemHandler();
        ItemStack embryo = new ItemStack(item("dream_wand_embryo"));
        PasterItemData.putDouble(embryo, "process", 1);
        PasterItemData.putBoolean(embryo, "paster_attack_damage", true);
        PasterItemData.putDouble(embryo, "paster_attack_damage_number", 10);
        handler.setStackInSlot(WorkshopAnvilBlockEntity.SLOT_INPUT, embryo);
        handler.setStackInSlot(WorkshopAnvilBlockEntity.SLOT_RESULT, ItemStack.EMPTY);

        WorkshopAnvilMenu menu = new WorkshopAnvilMenu(9003, player.getInventory(), be);
        boolean start = menu.clickMenuButton(player, WorkshopAnvilMenu.BUTTON_START);
        out.accept(detail(start && be.isSwitchOn(),
                "工坊铁砧开始锤炼小游戏",
                "startBtn=" + start + " switchOn=" + be.isSwitchOn() + " target=" + be.getNumber()));

        // 连点正确数字攒分（每次 press 后目标重抽，读 getNumber 再点）
        for (int i = 0; i < 12 && be.isSwitchOn(); i++) {
            int target = be.getNumber();
            if (target >= 1 && target <= 5) {
                be.pressNumber(target);
            }
        }
        out.accept(detail(be.getScore() > 0,
                "工坊铁砧命中得分 score=" + be.getScore(),
                "score=" + be.getScore()));

        // 推进 16 回合结算
        for (int i = 0; i < 20 && be.isSwitchOn(); i++) {
            be.tickGame();
        }
        ItemStack result = handler.getStackInSlot(WorkshopAnvilBlockEntity.SLOT_RESULT);
        double process = PasterItemData.getDouble(result, "process");
        out.accept(detail(!result.isEmpty() && process >= 2 && !be.isSwitchOn(),
                "工坊铁砧结算：process≥2 且小游戏结束",
                "result=" + summarize(result) + " process=" + process + " switch=" + be.isSwitchOn()));

        out.accept(detail(menu.stillValid(player), "工坊铁砧菜单 stillValid", "ok"));
        menu.removed(player);
        level.removeBlock(pos, false);
    }

    // ==================== 冷却盆 / 磨盘强化 API ====================

    private static void verifyQuenchAndGrind(ServerLevel level, ServerPlayer player,
                                              BlockPos pos, Consumer<Result> out) {
        BlockPos cauldronPos = surface(level, pos);
        BlockPos grindPos = surface(level, pos.offset(2, 0, 0));
        BlockPos tablePos = surface(level, pos.offset(4, 0, 0));

        level.setBlock(cauldronPos, PDBlocks.WORKSHOP_CAULDEON.get().defaultBlockState(), 3);
        level.setBlock(grindPos, PDBlocks.WORKSHOP_GRIND.get().defaultBlockState(), 3);
        level.setBlock(tablePos, PDBlocks.WEAPON_TABLE.get().defaultBlockState(), 3);

        BlockEntity cRaw = level.getBlockEntity(cauldronPos);
        BlockEntity gRaw = level.getBlockEntity(grindPos);
        BlockEntity tRaw = level.getBlockEntity(tablePos);

        out.accept(detail(cRaw instanceof WorkshopCauldeonBlockEntity,
                "冷却盆 BE 已创建", "got=" + typeName(cRaw)));
        out.accept(detail(gRaw instanceof WorkshopGrindBlockEntity,
                "磨盘 BE 已创建", "got=" + typeName(gRaw)));
        out.accept(detail(tRaw != null, "武器展示桌 BE 已创建", "got=" + typeName(tRaw)));

        // shadow_erosion_sword_embryo ∈ embryo_items ∩ paster_weapon
        Item weaponEmbryo = item("shadow_erosion_sword_embryo");
        out.accept(detail(weaponEmbryo != Items.AIR && new ItemStack(weaponEmbryo).is(PASTER_WEAPON),
                "shadow_erosion_sword_embryo ∈ #pasterdream:paster_weapon",
                weaponEmbryo == Items.AIR ? "未注册" : "ok"));

        if (cRaw instanceof WorkshopCauldeonBlockEntity cauldron) {
            ItemStack stash = new ItemStack(weaponEmbryo);
            cauldron.applyQuenchInlay(stash);
            boolean touched = PasterItemData.getBoolean(stash, "paster_attack_damage")
                    || PasterItemData.getBoolean(stash, "paster_attack_speed")
                    || PasterItemData.getBoolean(stash, "paster_movement_speed")
                    || PasterItemData.getBoolean(stash, "paster_luck");
            out.accept(detail(touched,
                    "冷却盆对武器类原胚写入属性标记",
                    "modified=" + touched + " dmg=" + PasterItemData.getBoolean(stash, "paster_attack_damage")
                            + " spd=" + PasterItemData.getBoolean(stash, "paster_attack_speed")));
        }

        if (gRaw instanceof WorkshopGrindBlockEntity grind) {
            ItemStack stash = new ItemStack(weaponEmbryo);
            grind.applyGrindInlay(stash);
            boolean touched = PasterItemData.getBoolean(stash, "paster_attack_damage")
                    || PasterItemData.getBoolean(stash, "paster_attack_speed")
                    || PasterItemData.getBoolean(stash, "paster_movement_speed")
                    || PasterItemData.getBoolean(stash, "paster_luck");
            out.accept(detail(touched,
                    "磨盘对武器类原胚写入属性标记",
                    "modified=" + touched + " dmg=" + PasterItemData.getBoolean(stash, "paster_attack_damage")
                            + " spd=" + PasterItemData.getBoolean(stash, "paster_attack_speed")));
        }

        level.removeBlock(cauldronPos, false);
        level.removeBlock(grindPos, false);
        level.removeBlock(tablePos, false);
    }

    // ==================== 工具 ====================

    private static BlockPos anchor(ServerLevel level, ServerPlayer player, int dx, int dz) {
        BlockPos p = player.blockPosition().offset(dx, 0, dz);
        level.getChunk(p);
        return p;
    }

    private static BlockPos surface(ServerLevel level, BlockPos pos) {
        // 与玩家同高度附近放置，避免 heightmap 地表与玩家纵向距离超出 stillValid(8 格)
        // 仅保证区块已加载；若该处非空气则上抬到最近空气
        BlockPos p = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        level.getChunk(p);
        if (level.getBlockState(p).isAir()) {
            return p;
        }
        for (int dy = 1; dy <= 4; dy++) {
            BlockPos up = p.above(dy);
            if (level.getBlockState(up).isAir()) {
                return up;
            }
        }
        // 强制清空后使用
        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
        return p;
    }

    private static Item item(String path) {
        return BuiltInRegistries.ITEM.getOptional(rl(path)).orElse(Items.AIR);
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path);
    }

    private static String summarize(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "empty";
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id + "×" + stack.getCount();
    }

    private static String typeName(Object o) {
        return o == null ? "null" : o.getClass().getSimpleName();
    }

    private static Result detail(boolean ok, String name, String detail) {
        return new Result(ok, name, detail);
    }
}
