# -*- coding: utf-8 -*-
"""更新 wind_knight_spawnblock 合并相关的审计/迁移清单文件。"""
import json

BASE = r"PasterDream"

# ---------- 1) pd_porting_manifest.json：原版快照保留，添加 renames + excluded ----------
manifest_path = f"{BASE}/src/main/resources/pd_porting_manifest.json"
with open(manifest_path, encoding="utf-8") as f:
    manifest = json.load(f)

# renames：_0 -> 无后缀
manifest["renames"]["wind_knight_spawnblock_0"] = "wind_knight_spawnblock"

# excluded：_1.._4 刻意合并进主方块
merged = ["wind_knight_spawnblock_1", "wind_knight_spawnblock_2",
          "wind_knight_spawnblock_3", "wind_knight_spawnblock_4"]
excluded = manifest.setdefault("excluded", {})
if "blocks" not in excluded:
    excluded["blocks"] = []
for m in merged:
    if m not in excluded["blocks"]:
        excluded["blocks"].append(m)
if "items" not in excluded:
    excluded["items"] = []
for m in merged:
    if m not in excluded["items"]:
        excluded["items"].append(m)
excluded["_comment_merge"] = (
    "wind_knight_spawnblock_1..4 原版 4 个独立方块已合并进单一 "
    "wind_knight_spawnblock（样式由方块 STAGE 属性决定），故从期望中排除；"
    "_0 经 renames 映射到新名")

with open(manifest_path, "w", encoding="utf-8") as f:
    json.dump(manifest, f, ensure_ascii=False, indent=2)
    f.write("\n")
print("pd_porting_manifest.json updated")

# ---------- 2) tag_audit.json：新版侧清单，_0.._4 -> 单名 ----------
audit_path = f"{BASE}/tag_audit.json"
with open(audit_path, encoding="utf-8") as f:
    audit = json.load(f)

old_ids = [f"wind_knight_spawnblock_{i}" for i in range(5)]
for key in ("block_ids", "item_ids"):
    ids = audit[key]
    if all(o in ids for o in old_ids):
        ids = [o for o in ids if o not in old_ids]
        ids.append("wind_knight_spawnblock")
        audit[key] = ids
    else:
        print(f"warn: {key} 未完整包含 5 个旧 ID，跳过")

with open(audit_path, "w", encoding="utf-8") as f:
    json.dump(audit, f, ensure_ascii=False, indent=2)
    f.write("\n")
print("tag_audit.json updated")
