# -*- coding: utf-8 -*-
"""
为巨型针叶树补树冠段 —— 数据包 big1-0.nbt 只是树干/中下段（原设计 conifer_a + conifer_b 两段拼接，conifer_b 缺失），
顶部被平切。本脚本在结构顶部追加锥形针叶树冠（树叶逐层收尖 + 中心 log 延长），补成完整巨树。
用法: python bb_conifer_crown.py
"""
import os
import sys
from collections import defaultdict

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import bb_tree_import as bt

DST = os.path.join(bt.DST_BASE, "bb_conifer_big.nbt")


def label_of(state, pal):
    if state >= len(pal):
        return "?"
    n = pal[state].get("Name", "?")
    return "log" if "log" in n else ("leaf" if "leaves" in n else n)


def main():
    root, gz = bt.read_nbt_file(DST)
    size = root["size"]
    blocks = root["blocks"]
    pal = root["palette"]

    # palette 索引
    leaf_states = {i for i, x in enumerate(pal) if isinstance(x, dict) and "leaves" in x.get("Name", "")}
    log_states = {i for i, x in enumerate(pal) if isinstance(x, dict) and "log" in x.get("Name", "")}
    if not leaf_states or not log_states:
        print("palette 缺少树叶或原木，无法补树冠")
        sys.exit(1)
    leaf_state = next(iter(leaf_states))
    log_state = next(iter(log_states))

    top_y = max(b["pos"][1] for b in blocks)
    print("原始结构: size=%s 顶部 y=%d blocks=%d" % (size, top_y, len(blocks)))

    # 现有 blocks 转 map
    existing = defaultdict(dict)  # (x,z) -> {y: state}
    for b in blocks:
        x, y, z = b["pos"]
        existing[(x, z)][y] = b["state"]

    # 树干中心：取 y=0 log 的均值（四舍五入）
    base_logs = [b for b in blocks if b["pos"][1] == 0 and b["state"] in log_states]
    cx = round(sum(b["pos"][0] for b in base_logs) / len(base_logs))
    cz = round(sum(b["pos"][2] for b in base_logs) / len(base_logs))
    print("树干中心: (%d, %d)" % (cx, cz))

    # 追加树冠：从 top_y+1 开始，半径平滑收尖，总高约 18 层（y=22..39）
    new_blocks = []
    crown_base_radius = 8
    crown_height = 18
    for dy in range(1, crown_height + 1):
        y = top_y + dy
        # 半径平滑收窄（平方曲线，越往上越细）
        t = dy / crown_height
        r = max(0, round(crown_base_radius * (1 - t * t)))
        # log 内圈逐步收窄：3x3(树干) → 1x1 → 顶部无 log
        if dy <= 8:
            log_half = 1      # 3x3 树干保持
        elif dy <= 15:
            log_half = 0      # 1x1 收细
        else:
            log_half = -1     # 顶部无 log
        for dx in range(-r, r + 1):
            for dz in range(-r, r + 1):
                if dx * dx + dz * dz > r * r:
                    continue
                x = cx + dx
                z = cz + dz
                # 内圈放 log（树干延续，顶部收尖）
                if log_half >= 0 and abs(dx) <= log_half and abs(dz) <= log_half:
                    state = log_state
                else:
                    state = leaf_state
                new_blocks.append({"pos": [x, y, z], "state": state})

    # 合并到 blocks
    existing_set = {(b["pos"][0], b["pos"][1], b["pos"][2]) for b in blocks}
    added = 0
    for b in new_blocks:
        key = (b["pos"][0], b["pos"][1], b["pos"][2])
        if key not in existing_set:
            blocks.append(b)
            existing_set.add(key)
            added += 1

    # 更新 size 高度
    new_height = top_y + crown_height + 1
    root["size"] = [size[0], new_height, size[2]]

    bt.write_nbt_file(root, DST, gzipped=gz)
    print("补树冠完成: 新增 %d 方块, 高度 %d -> %d, 总 blocks=%d" % (added, top_y + 1, new_height, len(blocks)))

    # 校验
    root2, _ = bt.read_nbt_file(DST)
    blocks2 = root2["blocks"]
    top2 = max(b["pos"][1] for b in blocks2)
    pal2 = root2["palette"]
    # 顶层应该只有 1 个方块（尖顶）
    top_blocks = [b for b in blocks2 if b["pos"][1] == top2]
    print("新顶层 y=%d blocks=%d (期望≈1 尖顶)" % (top2, len(top_blocks)))
    print("新 size=%s" % root2["size"])


if __name__ == "__main__":
    main()
