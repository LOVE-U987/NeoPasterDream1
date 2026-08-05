# -*- coding: utf-8 -*-
"""将结构 NBT 中的旧方块 ID wind_knight_spawnblock_0 改写为合并后的新 ID。"""
import gzip

SRC = r"PasterDream\src\main\resources\data\pasterdream\structure\lost_windknight_ruins.nbt"

with gzip.open(SRC, "rb") as f:
    data = bytearray(f.read())

OLD = "pasterdream:wind_knight_spawnblock_0"
NEW = "pasterdream:wind_knight_spawnblock"
assert len(OLD) == 36 and len(NEW) == 34, (len(OLD), len(NEW))

count = 0
idx = data.find(OLD.encode())
while idx != -1:
    # NBT TAG_String: 0x08 + 2-byte big-endian length + utf8
    # 长度前缀位于字符串开始前 2 字节
    len_hi = data[idx - 2]
    len_lo = data[idx - 1]
    old_len = (len_hi << 8) | len_lo
    assert old_len == len(OLD), f"长度前缀不匹配 {old_len} at {idx}"
    data[idx - 2] = (len(NEW) >> 8) & 0xFF
    data[idx - 1] = len(NEW) & 0xFF
    data[idx:idx + len(OLD)] = NEW.encode()
    count += 1
    idx = data.find(OLD.encode(), idx + len(NEW))

print(f"替换 {count} 处")

with gzip.open(SRC, "wb") as f:
    f.write(bytes(data))
print("已写回", SRC)
