package com.pasterdream.pasterdreammod.dreamnotes;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.worldgen.PDShadowDoorLocator;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.text.DecimalFormat;
import java.util.Optional;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 寻梦者笔记 procedure 语义（Pr0/Pr1、翻书音效、经验启发、成就发放）。
 * <p>
 * 对应原版 {@code Dreamnotes*Pr0Procedure} / {@code Dreamnotes8Pr1Procedure} /
 * {@code NotesExpupPr0Procedure} / {@code SoundnotesProcedure}。
 */
public final class DreamnotesLogic {

    private static final DecimalFormat INT_FMT = new DecimalFormat("####");

    private DreamnotesLogic() {
    }

    /** 打开/关闭笔记 GUI 时的翻书音效（原版 SoundnotesProcedure）。 */
    public static void playPageTurn(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof Level level) || level.isClientSide()) {
            return;
        }
        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    /** 笔记启发：经验提升 buff + 提示（原版 NotesExpupPr0Procedure）。 */
    public static void grantNotesExpup(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living.level().isClientSide()) {
            return;
        }
        living.addEffect(new MobEffectInstance(PDEffects.EXPUP_BUFF.holder(), 3600, 0));
        if (entity instanceof Player player) {
            player.displayClientMessage(Component.literal(
                    "你收到了笔记的启发，随§a时间流逝§f缓慢获得§e少量经验§f，并在§a获取经验时§f可以§e延长§f经验提升的效果时间"), false);
        }
    }

    /**
     * 右键笔记时的成就/坐标/掉落逻辑（按 noteId 分派）。
     *
     * @param noteId 0..14
     * @param stack  手持笔记
     */
    public static void onUse(int noteId, Level world, Entity entity, ItemStack stack) {
        if (entity == null || world.isClientSide()) {
            return;
        }
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        switch (noteId) {
            case 1 -> tryUnlock(world, x, y, z, entity, PDAdvancements.A_0, PDAdvancements.START,
                    "你习得了新的知识 关于§a[染梦裂隙]§f，新的进度已解锁", true);
            case 2 -> tryUnlock(world, x, y, z, entity, PDAdvancements.B_0, PDAdvancements.A_0,
                    "你习得了新的知识 关于§a[染梦世界]§f，新的进度已解锁", true);
            case 3 -> tryUnlock(world, x, y, z, entity, PDAdvancements.HIDE_0, PDAdvancements.START,
                    "你习得了新的知识 关于§a[粉红史莱姆]§f，新的进度已解锁", true);
            case 4 -> tryUnlock(world, x, y, z, entity, PDAdvancements.A_1, PDAdvancements.START,
                    "你习得了新的知识 关于§a[苍白雪莲]§f，新的进度已解锁", true);
            case 5 -> tryUnlock(world, x, y, z, entity, PDAdvancements.HIDE_3, PDAdvancements.A_1,
                    "你习得了新的知识 关于§a[苍白骨针]§f，新的进度已解锁", true);
            case 6 -> tryUnlock(world, x, y, z, entity, PDAdvancements.C_2, PDAdvancements.START,
                    "你习得了新的知识 关于§a[衍梦肥泥]§f，新的进度已解锁", true);
            case 7 -> tryUnlock(world, x, y, z, entity, PDAdvancements.C_3, PDAdvancements.B_0,
                    "你习得了新的知识 关于§a[蓄梦池]§f，新的进度已解锁", true);
            case 8 -> onUseNote8(world, x, y, z, entity, stack);
            case 9 -> onUseNote9(world, x, y, z, entity, stack);
            case 10 -> tryUnlock(world, x, y, z, entity, PDAdvancements.HIDE_11, PDAdvancements.SHADOW_START,
                    "你习得了新的知识 关于§a[沉淀阴影]§f，新的进度已解锁", true);
            case 11 -> tryUnlock(world, x, y, z, entity, PDAdvancements.HIDE_12, PDAdvancements.HIDE_11,
                    "你习得了新的知识 关于§a[阴影游记]§f，新的进度已解锁", true);
            case 12 -> {
                if (tryUnlock(world, x, y, z, entity, PDAdvancements.HIDE_14, PDAdvancements.HIDE_13,
                        "你习得了新的知识 关于§a[暗影地牢]§f，新的进度已解锁", true)) {
                    msg(entity, "你学会了修复暗影地牢的方法", false);
                }
            }
            case 13 -> {
                if (tryUnlock(world, x, y, z, entity, PDAdvancements.HIDE_15, PDAdvancements.HIDE_14,
                        "你习得了新的知识 关于§a[恐惧]§f，新的进度已解锁", true)) {
                    msg(entity, "黑暗双手的掌心向你敞开", false);
                }
            }
            case 14 -> {
                if (tryUnlock(world, x, y, z, entity, PDAdvancements.HIDE_16, PDAdvancements.B_0,
                        "你习得了新的知识 关于§a[无翼鸟也有展翅的梦]§f，新的进度已解锁", true)) {
                    msg(entity, "天空的云层将可以被你撕裂", false);
                }
            }
            default -> {
                // notes_0：仅打开 GUI，无成就
            }
        }
    }

    /** 手持选中时显示背面坐标（原版 Dreamnotes8Pr1，notes_8/9 共用）。 */
    public static void tickSelectedCoords(Entity entity, ItemStack stack) {
        if (entity == null || entity.level().isClientSide()) {
            return;
        }
        if (!DreamnotesData.getBoolean(stack, "switch")) {
            return;
        }
        if (entity instanceof Player player) {
            double cx = DreamnotesData.getDouble(stack, "x");
            double cz = DreamnotesData.getDouble(stack, "z");
            player.displayClientMessage(Component.literal(
                    "主世界 X:" + INT_FMT.format(cx) + " Z:" + INT_FMT.format(cz)), true);
        }
    }

    private static void onUseNote8(Level world, double x, double y, double z, Entity entity, ItemStack stack) {
        boolean hide8 = isDone(entity, PDAdvancements.HIDE_8);
        boolean hide7 = isDone(entity, PDAdvancements.HIDE_7);
        if (!hide8 && hide7) {
            if (awardAllCriteria(entity, PDAdvancements.HIDE_8)) {
                msg(entity, "你习得了新的知识 关于§a[阴影中的潜藏者]§f，新的进度已解锁", false);
                playChallenge(world, x, y, z);
                if (writeCoords(world, entity, stack)) {
                    msg(entity, "笔记的背面刻印这一个坐标", false);
                    msg(entity, "X:" + INT_FMT.format(DreamnotesData.getDouble(stack, "x"))
                            + "Z:" + INT_FMT.format(DreamnotesData.getDouble(stack, "z")), false);
                }
                grantNotesExpup(entity);
            }
        } else if (isDone(entity, PDAdvancements.HIDE_8)) {
            // 已解锁后再读：刷新为当前最近据点
            writeCoords(world, entity, stack);
        }
    }

    private static void onUseNote9(Level world, double x, double y, double z, Entity entity, ItemStack stack) {
        boolean hide10 = isDone(entity, PDAdvancements.HIDE_10);
        boolean b0 = isDone(entity, PDAdvancements.B_0);
        if (hide10 && b0) {
            if (writeCoords(world, entity, stack)) {
                msg(entity, "笔记的背面刻印这一个坐标", false);
            }
        }
        if (!hide10 && b0) {
            if (awardAllCriteria(entity, PDAdvancements.HIDE_10)) {
                msg(entity, "你习得了新的知识 关于§a[侵染教堂]§f，新的进度已解锁", false);
                playChallenge(world, x, y, z);
                DreamnotesData.putBoolean(stack, "switch", true);
                giveCalleCard0(entity);
                msg(entity, "你在对折的笔记里发现了一张卡片", false);
                if (writeCoords(world, entity, stack)) {
                    msg(entity, "笔记的背面刻印这一个坐标", false);
                    msg(entity, "X:" + INT_FMT.format(DreamnotesData.getDouble(stack, "x"))
                            + "Z:" + INT_FMT.format(DreamnotesData.getDouble(stack, "z")), false);
                }
                grantNotesExpup(entity);
            }
        }
        if (entity instanceof Player player && player.getAbilities().instabuild) {
            if (writeCoords(world, entity, stack)) {
                msg(entity, "笔记的背面刻印这一个坐标", false);
                msg(entity, "X:" + INT_FMT.format(DreamnotesData.getDouble(stack, "x"))
                        + "Z:" + INT_FMT.format(DreamnotesData.getDouble(stack, "z")), false);
            }
        }
    }

    /**
     * @return 本次是否成功新解锁
     */
    private static boolean tryUnlock(Level world, double x, double y, double z, Entity entity,
                                     ResourceLocation unlock, ResourceLocation prereq, String message, boolean expup) {
        if (isDone(entity, unlock) || !isDone(entity, prereq)) {
            return false;
        }
        if (!awardAllCriteria(entity, unlock)) {
            return false;
        }
        msg(entity, message, false);
        playChallenge(world, x, y, z);
        if (expup) {
            grantNotesExpup(entity);
        }
        return true;
    }

    /**
     * 写入最近暮影据点坐标（locate）。失败不写假 x/z。
     *
     * @return 是否成功写入
     */
    private static boolean writeCoords(Level world, Entity entity, ItemStack stack) {
        if (!(world instanceof ServerLevel server) || entity == null) {
            return false;
        }
        Optional<BlockPos> found = PDShadowDoorLocator.locate(server, entity.blockPosition());
        if (found.isEmpty()) {
            msg(entity, "尚未感应到暮影据点的方位", false);
            return false;
        }
        BlockPos pos = found.get();
        DreamnotesData.putBoolean(stack, "switch", true);
        DreamnotesData.putDouble(stack, "x", pos.getX());
        DreamnotesData.putDouble(stack, "z", pos.getZ());
        return true;
    }

    private static void giveCalleCard0(Entity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }
        Item item = BuiltInRegistries.ITEM.getOptional(
                        ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "calle_card_0"))
                .orElse(Items.AIR);
        if (item == Items.AIR) {
            PDDebugLogger.mainDebug("[Dreamnotes] calle_card_0 未注册，跳过 notes_9 卡片掉落");
            return;
        }
        ItemStack card = new ItemStack(item);
        if (!player.getInventory().add(card)) {
            player.drop(card, false);
        }
    }

    private static void playChallenge(Level world, double x, double y, double z) {
        if (world.isClientSide()) {
            return;
        }
        world.playSound(null, BlockPos.containing(x, y, z),
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }

    private static void msg(Entity entity, String text, boolean actionBar) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(Component.literal(text), actionBar);
        }
    }

    public static boolean isDone(Entity entity, ResourceLocation path) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }
        if (!PDAdvancements.isAdvancementLocked(player, path)) {
            return true;
        }
        AdvancementHolder holder = player.server.getAdvancements().get(path);
        if (holder == null) {
            return false;
        }
        return player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    private static boolean awardAllCriteria(Entity entity, ResourceLocation path) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }
        AdvancementHolder holder = player.server.getAdvancements().get(path);
        if (holder == null) {
            PDDebugLogger.mainDebug("[Dreamnotes] 成就 {} 未注册，跳过授予", path);
            return false;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (progress.isDone()) {
            return false;
        }
        for (String criteria : progress.getRemainingCriteria()) {
            player.getAdvancements().award(holder, criteria);
        }
        return true;
    }

    /** 判断玩家主手或副手是否持有指定 noteId 的笔记（GUI 页纹理选择）。 */
    public static boolean isHoldingNote(Entity entity, int noteId) {
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        Item target = DreamnotesItems.byId(noteId);
        if (target == null) {
            return false;
        }
        return living.getMainHandItem().is(target) || living.getOffhandItem().is(target);
    }
}
