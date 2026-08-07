# -*- coding: utf-8 -*-
"""
读取 Minecraft 1.21.1 区块文件（region/mca + NBT），分析染梦维度河流群系区域
的方块分布，用于验证「河流有水 + 河床染梦沙」。

用法: python analyze_dyedream_river_chunk.py
纯 Python 标准库实现（struct/zlib），无需第三方依赖。
"""
import struct
import zlib
import os
import sys

# ---------- NBT 解析 ----------
TAG_END = 0
TAG_BYTE = 1
TAG_SHORT = 2
TAG_INT = 3
TAG_LONG = 4
TAG_FLOAT = 5
TAG_DOUBLE = 6
TAG_BYTE_ARRAY = 7
TAG_STRING = 8
TAG_LIST = 9
TAG_COMPOUND = 10
TAG_INT_ARRAY = 11
TAG_LONG_ARRAY = 12

class NBTReader:
    def __init__(self, data):
        self.data = data
        self.pos = 0

    def read(self, n):
        out = self.data[self.pos:self.pos + n]
        self.pos += n
        if len(out) < n:
            raise EOFError("NBT truncated")
        return out

    def u8(self):
        return self.read(1)[0]

    def i8(self):
        return struct.unpack('>b', self.read(1))[0]

    def i16(self):
        return struct.unpack('>h', self.read(2))[0]

    def u16(self):
        return struct.unpack('>H', self.read(2))[0]

    def i32(self):
        return struct.unpack('>i', self.read(4))[0]

    def i64(self):
        return struct.unpack('>q', self.read(8))[0]

    def f32(self):
        return struct.unpack('>f', self.read(4))[0]

    def f64(self):
        return struct.unpack('>d', self.read(8))[0]

    def string(self):
        n = self.u16()
        return self.read(n).decode('utf-8', errors='replace')

    def tag(self):
        t = self.u8()
        return self._value(t)

    def _value(self, t):
        if t == TAG_END:
            return None
        if t == TAG_BYTE:
            return self.i8()
        if t == TAG_SHORT:
            return self.i16()
        if t == TAG_INT:
            return self.i32()
        if t == TAG_LONG:
            return self.i64()
        if t == TAG_FLOAT:
            return self.f32()
        if t == TAG_DOUBLE:
            return self.f64()
        if t == TAG_BYTE_ARRAY:
            n = self.i32()
            return list(self.read(n))
        if t == TAG_STRING:
            return self.string()
        if t == TAG_LIST:
            elem_type = self.u8()
            n = self.i32()
            return [self._value(elem_type) for _ in range(n)]
        if t == TAG_COMPOUND:
            d = {}
            while True:
                tt = self.u8()
                if tt == TAG_END:
                    break
                name = self.string()
                d[name] = self._value(tt)
            return d
        if t == TAG_INT_ARRAY:
            n = self.i32()
            return [self.i32() for _ in range(n)]
        if t == TAG_LONG_ARRAY:
            n = self.i32()
            return [self.i64() for _ in range(n)]
        raise ValueError(f"unknown tag {t}")

    def root_compound(self):
        """读取根复合标签（首个 TAG_Compound 或带名字的 tag）"""
        t = self.u8()
        assert t == TAG_COMPOUND, f"root not compound: {t}"
        name = self.string()
        return self._value(TAG_COMPOUND)


def read_region_chunks(mca_path):
    """读取 region 文件，返回 { (chunkX, chunkZ): CompoundTag }"""
    with open(mca_path, 'rb') as f:
        header = f.read(4096)
    chunks = {}
    for i in range(1024):
        off = struct.unpack('>I', header[i * 4:i * 4 + 4])[0]
        if off == 0:
            continue
        sector = off >> 8
        count = off & 0xFF
        with open(mca_path, 'rb') as f:
            f.seek(sector * 4096)
            length = struct.unpack('>I', f.read(4))[0]
            compression = f.read(1)[0]
            payload = f.read(length - 1)
        try:
            if compression == 1:  # gzip
                raw = zlib.decompress(payload, 16 + zlib.MAX_WBITS)
            elif compression == 2:  # zlib
                raw = zlib.decompress(payload)
            elif compression == 3:  # 无压缩
                raw = payload
            else:
                continue
        except Exception:
            continue
        try:
            nbt = NBTReader(raw).root_compound()
            cx = nbt.get('xPos')
            cz = nbt.get('zPos')
            if cx is not None and cz is not None:
                chunks[(cx, cz)] = nbt
        except Exception:
            continue
    return chunks


# ---------- 区块方块读取 ----------
def block_states_to_palette_and_data(chunk):
    """提取区块 sections 的方块状态与全局调色板"""
    # 1.21: chunk['sections'] = [ { 'Y': int, 'block_states': {...}, 'biomes': {...} } ]
    sections = chunk.get('sections', [])
    result = {}
    for sec in sections:
        y = sec.get('Y')
        bs = sec.get('block_states', {})
        result[y] = {
            'palette': bs.get('palette', []),
            'data': bs.get('data', []),
        }
    return result


def get_block_at(chunk, x, y, z):
    """获取全局坐标的方块名（块级 x/z 0-15）"""
    sec_y = y >> 4
    sections = chunk.get('sections', [])
    sec = None
    for s in sections:
        if s.get('Y') == sec_y:
            sec = s
            break
    if sec is None:
        return None
    bs = sec.get('block_states', {})
    palette = bs.get('palette', [])
    if not palette:
        return None
    # 单一方块优化：无 data 字段
    if 'data' not in bs:
        return palette[0].get('Name', '?')
    data = bs['data']
    # 由 palette 大小决定 bits
    n = len(palette)
    if n <= 16:
        bits = 4
    elif n <= 256:
        bits = 8
    else:
        bits = 15
    # 定位位
    lx, lz = x & 15, z & 15
    ly = y & 15
    idx = (ly << 8) | (lz << 4) | lx
    if bits == 15:
        # 64 位字，每 15 位一个索引
        start_bit = idx * 15
        word_idx = start_bit // 64
        bit_off = start_bit % 64
        if word_idx + 1 >= len(data):
            return None
        v = (data[word_idx] >> bit_off) | (data[word_idx + 1] << (64 - bit_off))
        pal = v & ((1 << 15) - 1)
        if pal < n:
            return palette[pal].get('Name', '?')
        return None
    else:
        per_word = 64 // bits
        word_idx = idx // per_word
        bit_off = (idx % per_word) * bits
        if word_idx >= len(data):
            return None
        pal = (data[word_idx] >> bit_off) & ((1 << bits) - 1)
        if pal < n:
            return palette[pal].get('Name', '?')
        return None


def main():
    root = r"C:\Users\97128\Documents\GitHub\NeoPasterDream1"
    region_dir = os.path.join(root, "PasterDream", "run", "world", "dimensions", "pasterdream", "dyedream_world", "region")
    if not os.path.isdir(region_dir):
        print("region 目录不存在:", region_dir)
        sys.exit(1)

    all_chunks = {}
    for fn in sorted(os.listdir(region_dir)):
        if fn.endswith('.mca'):
            path = os.path.join(region_dir, fn)
            chunks = read_region_chunks(path)
            print(f"{fn}: {len(chunks)} chunks")
            all_chunks.update(chunks)

    print(f"总计 {len(all_chunks)} 个区块")
    if not all_chunks:
        sys.exit(1)

    # ============ 地表高度统计 ============
    print("\n=== 地表高度统计 (每列最高非空气方块) ===")
    surface_heights = []  # (y, count)
    height_hist = {}
    anomaly = []  # y > 250 的异常列
    sampled = 0
    for (cx, cz), chunk in all_chunks.items():
        # 采样每区块中心 8x8 列
        for x in range(4, 12, 2):
            for z in range(4, 12, 2):
                wx, wz = cx * 16 + x, cz * 16 + z
                # 从高到低找最高非空气方块
                top = None
                for y in range(318, 30, -1):
                    name = get_block_at(chunk, wx, y, wz)
                    if name is None:
                        continue
                    if not name.endswith(':air'):
                        top = y
                        break
                if top is not None:
                    surface_heights.append(top)
                    height_hist[top] = height_hist.get(top, 0) + 1
                    sampled += 1
                    if top > 250:
                        anomaly.append((wx, wz, top))
    if surface_heights:
        avg = sum(surface_heights) / len(surface_heights)
        print(f"采样列数: {sampled}, 平均地表高度: {avg:.1f}")
        print(f"最低: {min(surface_heights)}, 最高: {max(surface_heights)}")
        # 直方图（按 20 格分桶）
        buckets = {}
        for h in surface_heights:
            b = (h // 20) * 20
            buckets[b] = buckets.get(b, 0) + 1
        print("高度分布 (每20格):")
        for b in sorted(buckets):
            bar = '#' * min(buckets[b] * 2, 60)
            print(f"  y {b:>3}-{b+19:>3}: {buckets[b]:>4} {bar}")
        if anomaly:
            print(f"\n⚠️ 异常抬升列 (y>250) {len(anomaly)} 个: {anomaly[:10]}")
        else:
            print("\n✅ 无异常抬升 (所有采样列地表 < 250)")

    # ============ 方块统计 ============
    stats = {}
    for (cx, cz), chunk in all_chunks.items():
        s = {'water': 0, 'sand': 0, 'grass': 0, 'calcite': 0, 'air': 0, 'other': {}}
        sections = chunk.get('sections', [])
        for sec in sections:
            sec_y = sec.get('Y')
            base_y = sec_y * 16
            # 只需分析海平面附近 y=32..96 的区域
            if base_y > 96 or base_y + 15 < 32:
                continue
            bs = sec.get('block_states', {})
            palette = bs.get('palette', [])
            # 用调色板统计更高效
            for x in range(16):
                for z in range(16):
                    for y in range(16):
                        wy = base_y + y
                        if 32 <= wy <= 96:
                            name = get_block_at(chunk, cx * 16 + x, wy, cz * 16 + z)
                            if name is None:
                                continue
                            if name.endswith(':water'):
                                s['water'] += 1
                            elif name.endswith(':dyedream_sand'):
                                s['sand'] += 1
                            elif name.endswith(':dyedream_grass'):
                                s['grass'] += 1
                            elif name.endswith(':calcite'):
                                s['calcite'] += 1
                            elif name.endswith(':air'):
                                s['air'] += 1
                            else:
                                s['other'][name] = s['other'].get(name, 0) + 1
        stats[(cx, cz)] = s

    # 输出每个区块的统计
    print("\n=== 区块方块统计 (y32..96) ===")
    print(f"{'chunk':>10} {'water':>6} {'sand':>6} {'grass':>6} {'calcite':>8} {'air':>6}  其他")
    for (cx, cz) in sorted(stats.keys()):
        s = stats[(cx, cz)]
        other_str = ", ".join(f"{k}:{v}" for k, v in sorted(s['other'].items())[:6])
        print(f"({cx:>3},{cz:>3}) {s['water']:>6} {s['sand']:>6} {s['grass']:>6} {s['calcite']:>8} {s['air']:>6}  {other_str}")

    # 找有水且有沙的区块（河流特征）
    print("\n=== 潜在河流区块（water>0 且 sand>0）===")
    found = False
    for (cx, cz), s in stats.items():
        if s['water'] > 0 and s['sand'] > 0:
            print(f"chunk ({cx},{cz}): water={s['water']}, sand={s['sand']}")
            found = True
    if not found:
        print("（无）")
        print("\n=== 有水但无沙的区块 ===")
        for (cx, cz), s in stats.items():
            if s['water'] > 0 and s['sand'] == 0:
                print(f"chunk ({cx},{cz}): water={s['water']}")


if __name__ == '__main__':
    main()
