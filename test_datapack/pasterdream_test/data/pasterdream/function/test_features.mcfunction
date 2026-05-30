# ==============================
# 染梦世界 · 装饰物生成测试函数
# ==============================
# 使用方法：
#   1. 把 test_datapack/pasterdream_test 文件夹复制到 run/saves/<你的世界>/datapacks/
#   2. 进游戏执行 /reload
#   3. 在染梦群系(陆地或海洋)执行 /function pasterdream:test_features
#   4. 查看日志 run/logs/latest.log 中的 [Decor] / [findGroundY] 等标记
# ==============================

# === 冰门测试（biome_2 陆地 + biome_3 海洋都试试）===
say ===== [测试] 开始生成冰门 =====
place feature pasterdream:ice_gate ~ ~ ~
place feature pasterdream:ice_gate ~5 ~ ~5
place feature pasterdream:ice_gate ~-5 ~ ~-5
place feature pasterdream:ice_gate ~10 ~ ~5
place feature pasterdream:ice_gate ~-8 ~ ~8
place feature pasterdream:ice_gate ~15 ~ ~0
say ===== [测试] 冰门放置完成 =====

# === 冰柱测试 ===
say ===== [测试] 开始生成冰柱 =====
place feature pasterdream:ice_pillar ~ ~ ~
place feature pasterdream:ice_pillar ~6 ~ ~6
place feature pasterdream:ice_pillar ~-6 ~ ~-6
place feature pasterdream:ice_pillar ~12 ~ ~3
place feature pasterdream:ice_pillar ~-10 ~ ~-4
place feature pasterdream:ice_pillar ~0 ~ ~12
say ===== [测试] 冰柱放置完成 =====

# === 冰刺测试 ===
say ===== [测试] 开始生成冰刺 =====
place feature pasterdream:ice_spike ~ ~ ~
place feature pasterdream:ice_spike ~7 ~ ~7
place feature pasterdream:ice_spike ~-7 ~ ~-7
place feature pasterdream:ice_spike ~14 ~ ~2
place feature pasterdream:ice_spike ~-12 ~ ~-6
say ===== [测试] 冰刺放置完成 =====

# === 提示 ===
say ===== [测试] 全部测试完成！请查看日志中的 [Decor] 标记 =====
