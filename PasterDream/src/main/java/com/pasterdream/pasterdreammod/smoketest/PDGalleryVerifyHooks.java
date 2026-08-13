package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.DreamTrainStructureBlock;
import com.pasterdream.pasterdreammod.block.DyedreamSaplingBlock;
import com.pasterdream.pasterdreammod.block.PDStructureBlock;
import com.pasterdream.pasterdreammod.block.StarcallBlockBlock;
import com.pasterdream.pasterdreammod.block.StarcallCrackBlock;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 人工终验辅助：方块总览展台 + 结构目标维度一致性检查 + 夜视维持。
 * <p>
 * 展台分区：
 * <ul>
 *   <li><b>主展台</b>：全部可静物展示的模组方块（间隔 1），多方块（门/双层植物）上下半齐放，
 *       染梦植物下垫染梦草</li>
 *   <li><b>特展带</b>：结构触发块 / 唤星限时块 / 流体 —— 用物品展示框 + 告示牌标名
 *       （避免 onPlace 自毁、下射火球或流体漫延）</li>
 * </ul>
 * 玩家传送至起点上空；不清理、默认不退出。
 */
public final class PDGalleryVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    /** 方块间距（中心距 = 1 + gap） */
    private static final int BLOCK_GAP = 1;
    /** 每行方块数 */
    private static final int COLS = 32;
    /** 展台相对玩家出生点偏移 */
    private static final int ORIGIN_DX = 24;
    private static final int ORIGIN_DZ = 24;
    /** 主台与特展带之间的间隔行 */
    private static final int SPECIAL_GAP_ROWS = 2;

    private PDGalleryVerifyHooks() {
    }

    // ==================== 夜视 ====================

    /**
     * 施加长时夜视（可重复调用刷新）
     *
     * @param player 玩家
     */
    public static void ensureNightVision(ServerPlayer player) {
        if (player == null) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 60 * 60, 0, false, false, true));
        // 辅助：抗性 + 饱食，避免人工观察时干扰
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 60 * 60, 4, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20 * 60 * 60, 0, false, false, true));
        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        player.onUpdateAbilities();
    }

    // ==================== 结构维度一致性 ====================

    /**
     * 校验每个模组 STRUCTURE 的 biomes 是否落在预期维度集合内
     * （按 biome id / tag 关键字推断：dyedream / shadow / wind / overworld）。
     */
    public static void verifyStructureDimensions(MinecraftServer server, Consumer<Result> out) {
        var structureReg = server.registryAccess().registryOrThrow(Registries.STRUCTURE);
        var biomeReg = server.registryAccess().registryOrThrow(Registries.BIOME);

        Map<String, Set<Dim>> byStructure = new TreeMap<>();
        Map<Dim, Integer> dimCounts = new LinkedHashMap<>();
        for (Dim d : Dim.values()) {
            dimCounts.put(d, 0);
        }
        List<String> unknown = new ArrayList<>();
        List<String> multiDim = new ArrayList<>();

        int total = 0;
        for (var entry : structureReg.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            if (!PasterDreamMod.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            total++;
            Structure structure = entry.getValue();
            Set<Dim> dims = EnumSet.noneOf(Dim.class);
            HolderSet<Biome> biomes = structure.biomes();
            // 展开 HolderSet（直接 holder 或 tag）
            try {
                for (Holder<Biome> holder : biomes) {
                    dims.addAll(dimsOfBiomeHolder(holder, biomeReg));
                }
            } catch (Exception ex) {
                // tag 未绑定等：退回字符串解析
                dims.addAll(dimsFromBiomeSetString(String.valueOf(biomes)));
            }
            if (dims.isEmpty()) {
                unknown.add(id.getPath());
            } else {
                if (dims.size() > 1 && !(dims.size() == 2 && dims.contains(Dim.OVERWORLD) && dims.contains(Dim.UNKNOWN))) {
                    // 仅记录真正跨模组维度的结构
                    Set<Dim> meaningful = EnumSet.copyOf(dims);
                    meaningful.remove(Dim.UNKNOWN);
                    if (meaningful.size() > 1) {
                        multiDim.add(id.getPath() + meaningful);
                    }
                }
                for (Dim d : dims) {
                    dimCounts.merge(d, 1, Integer::sum);
                }
            }
            byStructure.put(id.getPath(), dims);
        }

        out.accept(detail(total >= 100,
                "STRUCTURE 维度扫描规模 " + total,
                "期望 ≥100"));

        // 各类维度至少应有代表结构（主世界/染梦/影灯/风旅）
        out.accept(detail(dimCounts.getOrDefault(Dim.OVERWORLD, 0) >= 1,
                "主世界相关结构 ≥1",
                "count=" + dimCounts.get(Dim.OVERWORLD)));
        out.accept(detail(dimCounts.getOrDefault(Dim.DYEDREAM, 0) >= 10,
                "染梦维度相关结构 ≥10",
                "count=" + dimCounts.get(Dim.DYEDREAM)));
        out.accept(detail(dimCounts.getOrDefault(Dim.SHADOW, 0) >= 5,
                "影灯维度相关结构 ≥5",
                "count=" + dimCounts.get(Dim.SHADOW)));
        out.accept(detail(dimCounts.getOrDefault(Dim.WIND, 0) >= 5,
                "风旅维度相关结构 ≥5",
                "count=" + dimCounts.get(Dim.WIND)));

        // 抽样关键结构维度
        expectDim(byStructure, "dream_train", Dim.DYEDREAM, out);
        expectDim(byStructure, "dyedream_worldtree_0", Dim.DYEDREAM, out);
        expectDim(byStructure, "dyedream_worldtree_1", Dim.DYEDREAM, out);
        expectDim(byStructure, "pinkagaric_house_0", Dim.DYEDREAM, out);
        expectDim(byStructure, "dream_church_0", Dim.DYEDREAM, out);
        expectDim(byStructure, "dream_church_8", Dim.DYEDREAM, out);
        expectDim(byStructure, "dream_church_10", Dim.DYEDREAM, out);
        expectDim(byStructure, "shadow_dungeon", Dim.SHADOW, out);
        expectDim(byStructure, "windmoor_tree_0", Dim.WIND, out);
        // 染梦裂隙受配置控制：关闭自然生成时 struct_dyedream_crack_1 不注册，跳过注册表断言
        if (PDCommonConfig.DYEDREAM_CRACK_GENERATE.get()) {
            expectDim(byStructure, "struct_dyedream_crack_1", Dim.OVERWORLD, out);
        } else {
            out.accept(detail(true, "结构 struct_dyedream_crack_1 已按配置禁用自然生成", "dyedream crack generate=false"));
        }
        expectDim(byStructure, "desert_cottage_0", Dim.OVERWORLD, out);

        out.accept(detail(unknown.size() < total * 0.15,
                "结构 biome→维度可解析 " + (total - unknown.size()) + "/" + total,
                unknown.isEmpty() ? "全部可解析" : "未知样例 " + unknown.subList(0, Math.min(8, unknown.size()))));

        out.accept(detail(true,
                "结构维度分布 " + dimCounts,
                multiDim.isEmpty() ? "无异常跨维" : "跨维样例 " + multiDim.subList(0, Math.min(6, multiDim.size()))));
    }

    private static void expectDim(Map<String, Set<Dim>> map, String path, Dim want, Consumer<Result> out) {
        Set<Dim> got = map.get(path);
        boolean ok = got != null && got.contains(want);
        out.accept(detail(ok,
                "结构 " + path + " → " + want.id,
                got == null ? "missing" : got.toString()));
    }

    private static Set<Dim> dimsOfBiomeHolder(Holder<Biome> holder,
                                              net.minecraft.core.Registry<Biome> biomeReg) {
        Set<Dim> dims = EnumSet.noneOf(Dim.class);
        holder.unwrapKey().ifPresent(key -> dims.add(dimFromBiomeId(key.location())));
        // 也看 tag
        for (var tag : holder.tags().toList()) {
            dims.add(dimFromBiomeId(tag.location()));
        }
        if (dims.isEmpty()) {
            // 尝试 registry key
            ResourceLocation rl = biomeReg.getKey(holder.value());
            if (rl != null) {
                dims.add(dimFromBiomeId(rl));
            }
        }
        return dims;
    }

    private static Set<Dim> dimsFromBiomeSetString(String s) {
        Set<Dim> dims = EnumSet.noneOf(Dim.class);
        String lower = s.toLowerCase(Locale.ROOT);
        if (lower.contains("dyedream") || lower.contains("is_dyedream")) {
            dims.add(Dim.DYEDREAM);
        }
        if (lower.contains("shadow") || lower.contains("lamp_shadow")) {
            dims.add(Dim.SHADOW);
        }
        if (lower.contains("wind_journey") || lower.contains("windmoor")) {
            dims.add(Dim.WIND);
        }
        if (lower.contains("overworld") || lower.contains("desert") || lower.contains("beach")
                || lower.contains("plains") || lower.contains("jungle") || lower.contains("is_overworld")) {
            dims.add(Dim.OVERWORLD);
        }
        if (lower.contains("aaroncos") || lower.contains("arena")) {
            // 竞技场入口可能在影/主世界；标记 UNKNOWN 由抽样断言收口
            dims.add(Dim.UNKNOWN);
        }
        return dims;
    }

    private static Dim dimFromBiomeId(ResourceLocation rl) {
        if (rl == null) {
            return Dim.UNKNOWN;
        }
        String p = (rl.getNamespace() + ":" + rl.getPath()).toLowerCase(Locale.ROOT);
        if (p.contains("dyedream") || p.contains("is_dyedream") || p.contains("pinkagaric")) {
            return Dim.DYEDREAM;
        }
        if (p.contains("shadow") || p.contains("lamp_shadow") || p.contains("nightmare")) {
            return Dim.SHADOW;
        }
        if (p.contains("wind_journey") || p.contains("windmoor") || p.contains("wind_")) {
            return Dim.WIND;
        }
        if (p.contains("aaroncos") || p.contains("arena")) {
            return Dim.UNKNOWN;
        }
        if ("minecraft".equals(rl.getNamespace())
                || p.contains("overworld") || p.contains("desert") || p.contains("beach")
                || p.contains("plains") || p.contains("jungle") || p.contains("ocean")
                || p.contains("forest") || p.contains("taiga") || p.contains("river")) {
            return Dim.OVERWORLD;
        }
        // 本模组未识别群系默认按路径猜
        if (PasterDreamMod.MOD_ID.equals(rl.getNamespace())) {
            return Dim.UNKNOWN;
        }
        return Dim.OVERWORLD;
    }

    private enum Dim {
        OVERWORLD("overworld"),
        DYEDREAM("dyedream_world"),
        SHADOW("lamp_shadow_world"),
        WIND("wind_journey_world"),
        UNKNOWN("unknown");

        final String id;

        Dim(String id) {
            this.id = id;
        }
    }

    // ==================== 方块总览展台 ====================

    /**
     * 在主世界平坦面铺设全部模组方块展台，并传送玩家到起点。
     *
     * @param player 玩家
     * @param out    断言输出
     * @return 展台原点（平台西北角）
     */
    public static BlockPos placeBlockGallery(ServerPlayer player, Consumer<Result> out) {
        if (player == null) {
            out.accept(new Result(false, "方块总览跳过", "player == null"));
            return BlockPos.ZERO;
        }
        ServerLevel level = player.serverLevel().getServer().getLevel(Level.OVERWORLD);
        if (level == null) {
            out.accept(new Result(false, "方块总览跳过", "overworld == null"));
            return BlockPos.ZERO;
        }

        List<Block> liveBlocks = new ArrayList<>();
        List<Block> specialBlocks = new ArrayList<>();
        collectGalleryBlocks(liveBlocks, specialBlocks);

        int stride = 1 + BLOCK_GAP;
        int liveRows = Math.max(1, (liveBlocks.size() + COLS - 1) / COLS);
        int specialRows = Math.max(1, (specialBlocks.size() + COLS - 1) / COLS);
        int totalRows = liveRows + SPECIAL_GAP_ROWS + specialRows;
        int platformW = COLS * stride + 4;
        int platformD = totalRows * stride + 6;

        BlockPos spawn = level.getSharedSpawnPos();
        int baseY = Math.max(level.getMinBuildHeight() + 8,
                level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                        spawn.getX() + ORIGIN_DX, spawn.getZ() + ORIGIN_DZ));
        baseY = Math.max(baseY, spawn.getY());
        BlockPos origin = new BlockPos(spawn.getX() + ORIGIN_DX, baseY, spawn.getZ() + ORIGIN_DZ);

        for (int x = 0; x < platformW; x += 16) {
            for (int z = 0; z < platformD; z += 16) {
                level.getChunk(origin.offset(x, 0, z));
            }
        }

        // 清空腔 + 石英平台（加高以容纳双层植物/门）
        BlockPos min = origin.offset(-2, -1, -2);
        BlockPos max = origin.offset(platformW, 8, platformD);
        for (BlockPos p : BlockPos.betweenClosed(min, max)) {
            int relY = p.getY() - origin.getY();
            if (relY == -1) {
                level.setBlock(p, Blocks.QUARTZ_BLOCK.defaultBlockState(), 2);
            } else {
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            }
        }

        // 边框灯
        for (int x = -1; x <= platformW - 1; x++) {
            level.setBlock(origin.offset(x, 0, -1), Blocks.SEA_LANTERN.defaultBlockState(), 2);
            level.setBlock(origin.offset(x, 0, platformD - 3), Blocks.SEA_LANTERN.defaultBlockState(), 2);
        }
        for (int z = -1; z <= platformD - 3; z++) {
            level.setBlock(origin.offset(-1, 0, z), Blocks.SEA_LANTERN.defaultBlockState(), 2);
            level.setBlock(origin.offset(platformW - 3, 0, z), Blocks.SEA_LANTERN.defaultBlockState(), 2);
        }

        int placed = 0;
        int failed = 0;
        List<String> failSamples = new ArrayList<>();
        for (int i = 0; i < liveBlocks.size(); i++) {
            int col = i % COLS;
            int row = i / COLS;
            BlockPos pos = origin.offset(col * stride, 0, row * stride);
            Block block = liveBlocks.get(i);
            try {
                if (placeLiveExhibit(level, pos, block)) {
                    placed++;
                } else {
                    failed++;
                    sampleFail(failSamples, block, null);
                }
            } catch (Exception ex) {
                failed++;
                sampleFail(failSamples, block, ex);
            }
        }

        // 主台 / 特展分界：橡木告示
        int specialBaseRow = liveRows + SPECIAL_GAP_ROWS;
        BlockPos divider = origin.offset(0, 0, liveRows * stride + stride);
        placeLabeledPedestal(level, divider, Items.OAK_SIGN,
                Component.literal("特展：结构/唤星/流体"),
                Component.literal("物品框展示（避免自毁）"),
                Component.empty(), Component.empty());

        int specialShown = 0;
        for (int i = 0; i < specialBlocks.size(); i++) {
            int col = i % COLS;
            int row = specialBaseRow + (i / COLS);
            BlockPos pos = origin.offset(col * stride, 0, row * stride);
            Block block = specialBlocks.get(i);
            try {
                if (placeSpecialExhibit(level, pos, block)) {
                    specialShown++;
                } else {
                    failed++;
                    sampleFail(failSamples, block, null);
                }
            } catch (Exception ex) {
                failed++;
                sampleFail(failSamples, block, ex);
            }
        }

        // 起点标记柱
        level.setBlock(origin.offset(-2, 0, -2), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
        level.setBlock(origin.offset(-2, 1, -2), Blocks.TORCH.defaultBlockState(), 3);

        double tpX = origin.getX() - 2.5;
        double tpY = origin.getY() + 3.0;
        double tpZ = origin.getZ() - 2.5;
        if (player.level() != level) {
            player.teleportTo(level, tpX, tpY, tpZ, 45f, 30f);
        } else {
            player.teleportTo(tpX, tpY, tpZ);
            player.setYRot(45f);
            player.setXRot(30f);
        }
        ensureNightVision(player);
        player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);

        int liveTotal = liveBlocks.size();
        double rate = liveTotal == 0 ? 0 : (100.0 * placed / liveTotal);
        out.accept(detail(placed > 0 && rate >= 90.0,
                "方块总览主台放置 " + placed + "/" + liveTotal + String.format(" (%.1f%%)", rate),
                "origin=" + origin.toShortString()
                        + " cols=" + COLS + " gap=" + BLOCK_GAP
                        + " special=" + specialShown + "/" + specialBlocks.size()
                        + (failSamples.isEmpty() ? "" : " failSamples=" + failSamples)));
        out.accept(detail(true,
                "玩家已传送至展台起点并开启夜视/飞行",
                "pos=" + player.blockPosition().toShortString()
                        + " dim=" + player.level().dimension().location()));
        out.accept(detail(failed < Math.max(1, (liveTotal + specialBlocks.size()) * 0.1),
                "方块放置失败 <10%（" + failed + "）",
                failSamples.isEmpty() ? "无失败" : failSamples.toString()));

        PDDebugLogger.smoketestInfo(
                "[PDGallery] gallery origin={} live={}/{} special={}/{} player@{}",
                origin.toShortString(), placed, liveTotal, specialShown, specialBlocks.size(),
                player.blockPosition().toShortString());
        return origin;
    }

    /**
     * 拆分静物主台与特展（结构/唤星/流体）。
     */
    private static void collectGalleryBlocks(List<Block> live, List<Block> special) {
        for (var entry : BuiltInRegistries.BLOCK.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            if (!PasterDreamMod.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            Block b = entry.getValue();
            if (b.defaultBlockState().isAir()) {
                continue;
            }
            if (isSpecialExhibit(b, id.getPath())) {
                special.add(b);
            } else {
                live.add(b);
            }
        }
        live.sort(PDGalleryVerifyHooks::compareBlockId);
        special.sort(PDGalleryVerifyHooks::compareBlockId);
    }

    private static int compareBlockId(Block a, Block b) {
        return String.valueOf(BuiltInRegistries.BLOCK.getKey(a))
                .compareTo(String.valueOf(BuiltInRegistries.BLOCK.getKey(b)));
    }

    /**
     * 结构触发 / 唤星限时 / 流体：不直接 setBlock 到展台格。
     */
    private static boolean isSpecialExhibit(Block b, String path) {
        if (b instanceof LiquidBlock) {
            return true;
        }
        if (b instanceof PDStructureBlock || b instanceof DreamTrainStructureBlock) {
            return true;
        }
        if (path.startsWith("structure_block_")) {
            return true;
        }
        if (b instanceof StarcallCrackBlock || b instanceof StarcallBlockBlock) {
            return true;
        }
        return "starcall_block".equals(path) || "starcall_crack".equals(path);
    }

    /**
     * 主台单格：基质 + 多方块上下半 + 安全状态。
     *
     * @return 主展示格非空气即为成功
     */
    private static boolean placeLiveExhibit(ServerLevel level, BlockPos pos, Block block) {
        BlockState state = safeState(block);

        // 需要染梦土壤的植物：垫一层染梦草
        if (needsDyedreamSoil(block)) {
            level.setBlock(pos.below(), PDBlocks.DYEDREAM_GRASS.get().defaultBlockState(), 2);
        }

        // 双层：门 / 双株植物
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                || block instanceof DoorBlock
                || block instanceof DoublePlantBlock) {
            BlockState lower = state;
            if (lower.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                lower = lower.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
            }
            BlockState upper = state;
            if (upper.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                upper = upper.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
            }
            level.setBlock(pos, lower, 2);
            level.setBlock(pos.above(), upper, 2);
            // 再以 3 刷新邻接
            level.setBlock(pos, lower, 3);
            level.setBlock(pos.above(), upper, 3);
            return !level.getBlockState(pos).isAir();
        }

        level.setBlock(pos, state, 3);
        return !level.getBlockState(pos).isAir();
    }

    /**
     * 特展：石英台柱 + 物品框（方块物品/桶）+ 简名告示，不触发 onPlace 行为。
     */
    private static boolean placeSpecialExhibit(ServerLevel level, BlockPos pos, Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        String path = id != null ? id.getPath() : "?";

        ItemStack display = exhibitStackFor(block);
        Component line0 = Component.literal(shortLabel(path));
        Component line1 = Component.literal(path.length() > 15 ? path.substring(0, 15) : path);
        Component line2 = Component.literal(specialKind(block, path));
        Component line3 = Component.empty();

        // 台座：平滑石 + 上方物品框朝南
        level.setBlock(pos, Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        placeItemFrame(level, pos.above(), display);
        // 台座北侧矮告示（朝向玩家从南看时可读：告示朝南）
        placeWallLabel(level, pos.north(), line0, line1, line2, line3);
        return true;
    }

    private static String specialKind(Block block, String path) {
        if (block instanceof LiquidBlock) {
            return "流体(框展)";
        }
        if (path.startsWith("structure_block_") || block instanceof PDStructureBlock
                || block instanceof DreamTrainStructureBlock) {
            return "结构触发";
        }
        if (path.startsWith("starcall")) {
            return "唤星限时";
        }
        return "特展";
    }

    private static String shortLabel(String path) {
        if (path.startsWith("structure_block_")) {
            return "结构#" + path.substring("structure_block_".length());
        }
        if ("starcall_crack".equals(path)) {
            return "唤星裂隙";
        }
        if ("starcall_block".equals(path)) {
            return "唤星照明";
        }
        if ("meltdream_liquid".equals(path)) {
            return "融梦液";
        }
        if ("shadow_liquid".equals(path)) {
            return "暗影液";
        }
        return path.length() > 12 ? path.substring(0, 12) : path;
    }

    private static ItemStack exhibitStackFor(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        String path = id != null ? id.getPath() : "";
        // 优先桶
        if ("meltdream_liquid".equals(path)) {
            Item bucket = PDItems.MELTDREAM_LIQUID_BUCKET.get();
            if (bucket != null && bucket != Items.AIR) {
                return new ItemStack(bucket);
            }
        }
        if ("shadow_liquid".equals(path)) {
            Item bucket = PDItems.SHADOW_LIQUID_BUCKET.get();
            if (bucket != null && bucket != Items.AIR) {
                return new ItemStack(bucket);
            }
        }
        Item item = block.asItem();
        if (item != null && item != Items.AIR) {
            return new ItemStack(item);
        }
        // 兜底：纸张写名
        ItemStack paper = new ItemStack(Items.PAPER);
        paper.set(DataComponents.CUSTOM_NAME, Component.literal(path));
        return paper;
    }

    private static void placeItemFrame(ServerLevel level, BlockPos pos, ItemStack stack) {
        // 先清实体位
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
        ItemFrame frame = new ItemFrame(level, pos, Direction.SOUTH);
        frame.setItem(stack.copy(), false);
        frame.setInvisible(false);
        // 1.21.1 无公开 setFixed；用 invulnerable + 无重力降低被清掉概率
        frame.setInvulnerable(true);
        frame.setNoGravity(true);
        level.addFreshEntity(frame);
    }

    private static void placeWallLabel(ServerLevel level, BlockPos pos,
                                       Component l0, Component l1, Component l2, Component l3) {
        level.setBlock(pos, Blocks.OAK_WALL_SIGN.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), 3);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SignBlockEntity sign) {
            SignText text = sign.getText(true)
                    .setMessage(0, l0)
                    .setMessage(1, l1)
                    .setMessage(2, l2)
                    .setMessage(3, l3);
            sign.setText(text, true);
            sign.setAllowedPlayerEditor(null);
            sign.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
    }

    private static void placeLabeledPedestal(ServerLevel level, BlockPos pos, Item icon,
                                             Component l0, Component l1, Component l2, Component l3) {
        level.setBlock(pos, Blocks.CHISELED_QUARTZ_BLOCK.defaultBlockState(), 3);
        placeItemFrame(level, pos.above(), new ItemStack(icon));
        placeWallLabel(level, pos.north(), l0, l1, l2, l3);
    }

    private static boolean needsDyedreamSoil(Block block) {
        if (block instanceof DyedreamSaplingBlock || block instanceof SaplingBlock) {
            return true;
        }
        // 染梦花/草/作物等 Bush/Flower 多数 mayPlaceOn 染梦土
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) {
            return false;
        }
        String p = id.getPath();
        if (p.startsWith("dyedream_") || p.startsWith("crop_") || p.startsWith("flower_")
                || p.startsWith("grass_") || p.contains("sapling") || p.contains("seagrass")
                || p.contains("lily") || p.contains("lotus") || p.contains("agaric")
                || p.contains("bud") || p.contains("vine")) {
            return block instanceof BushBlock || block instanceof FlowerBlock
                    || block instanceof DoublePlantBlock || block instanceof SaplingBlock;
        }
        return false;
    }

    /**
     * 尽量给出可放置的默认状态（含水关、门朝南、作物成熟视觉等）。
     */
    private static BlockState safeState(Block block) {
        BlockState state = block.defaultBlockState();
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            state = state.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        } else if (state.hasProperty(BlockStateProperties.FACING)) {
            state = state.setValue(BlockStateProperties.FACING, Direction.NORTH);
        }
        if (state.hasProperty(BlockStateProperties.OPEN)) {
            state = state.setValue(BlockStateProperties.OPEN, false);
        }
        if (state.hasProperty(BlockStateProperties.POWERED)) {
            state = state.setValue(BlockStateProperties.POWERED, false);
        }
        // 双层默认下半（placeLiveExhibit 会再写上半）
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            state = state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
        }
        return state;
    }

    private static void sampleFail(List<String> samples, Block block, Exception ex) {
        if (samples.size() >= 12) {
            return;
        }
        String id = String.valueOf(BuiltInRegistries.BLOCK.getKey(block));
        samples.add(ex == null ? id : id + ":" + ex.getClass().getSimpleName());
    }

    private static Result detail(boolean ok, String name, String d) {
        return new Result(ok, name, d);
    }
}
