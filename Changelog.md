## 1. 高层摘要（TL;DR）

*   **影响范围：** 高 - 涉及大规模国际化重构、音乐播放系统重构以及架构文档更新
*   **核心变更：**
    *   🌐 **国际化全面重构**：将 Java 代码和 Block 配置中的硬编码中文字符串替换为 `Component.translatable()` 翻译键
    *   🎵 **BGM 播放模式增强**：新增「完整播放+间隔」模式，支持曲目完整播放后再切换
    *   📚 **新增大量书籍内容**：添加了数十本书籍的翻译内容（中英文）
    *   🏗️ **API 架构文档更新**：完善多模块架构（PasterDreamAPI + PasterDream）的职责划分
    *   🔧 **配置项新增**：添加 BGM 播放模式相关的客户端配置选项

---

## 2. 视觉概览（代码与逻辑映射）

```mermaid
flowchart TD
    subgraph "国际化重构层"
        A1["Java 物品类<br/>Item/Block Classes"]
        A2["组件渲染逻辑<br/>Component Rendering"]
        A3["语言文件<br/>lang/zh_cn.json & en_us.json"]
    end

    subgraph "BGM 播放系统"
        B1["ModMusicManager<br/>音乐管理器"]
        B2["播放模式选择<br/>完整播放 vs 交叉淡化"]
        B3["配置系统<br/>PDClientConfig"]
        B4["群系音乐映射<br/>registerBiomeMusic()"]
    end

    subgraph "架构层"
        C1["API 文档<br/>SKILL.md"]
        C2["模块职责划分<br/>PasterDreamAPI vs PasterDream"]
    end

    A1 -->|Component.translatable()| A2
    A2 -->|翻译键查找| A3
    B3 -->|配置读取| B2
    B2 -->|控制播放逻辑| B1
    B1 -->|群系映射| B4
    C1 -->|架构指导| C2

    style A1 fill:#bbdefb,color:#0d47a1
    style A2 fill:#bbdefb,color:#0d47a1
    style A3 fill:#bbdefb,color:#0d47a1
    style B1 fill:#c8e6c9,color:#1a5e20
    style B2 fill:#c8e6c9,color:#1a5e20
    style B3 fill:#c8e6c9,color:#1a5e20
    style B4 fill:#c8e6c9,color:#1a5e20
    style C1 fill:#fff3e0,color:#e65100
    style C2 fill:#fff3e0,color:#e65100
```

---

## 3. 详细变更分析

### 🌐 3.1 国际化重构（i18n）

**变更内容：** 将硬编码的中文字符串替换为 `Component.translatable()` 翻译键，支持多语言切换。

#### 影响的文件示例

| 文件 | 变更类型 | 示例 |
|------|---------|------|
| `DreamWandItem.java` | 提示文本国际化 | `Component.literal("法杖数据已清空")` → `Component.translatable("tooltip.pasterdream.dream_wand.data_cleared")` |
| `PDStructureBlock.java` | 结构块提示国际化 | 所有 `SPECS` 中的 tooltip 从硬编码改为翻译键 |
| `AllkindsRingItem.java` | 物品描述国际化 | 三行硬编码文本改为翻译键 |
| `LifeCrystalBlock.java` | 消息国际化 | `Component.literal("你已经吸收过生命水晶了！")` → `Component.translatable(...)` |

#### 新增翻译键统计（部分）

| 类别 | 翻译键前缀 | 数量（估算） |
|------|-----------|-------------|
| 物品提示 | `tooltip.pasterdream.*` | 100+ |
| 结构块提示 | `tooltip.pasterdream.structure_block.*` | 24 |
| 系统消息 | `message.pasterdream.*` | 新增 2 条 |
| 书籍内容 | `book.pasterdream.*` | 新增 30+ 本书籍 |

#### 新增书籍内容

本次添加了大量书籍的完整翻译内容，包括：

*   《来往于梦》
*   《研究笔记-黑金属》
*   《气泡生态球》
*   《代达罗斯之翼与浮空岛》
*   《染梦教堂日记》（系列）
*   《染梦讲堂》
*   等数十本探索者笔记...

---

### 🎵 3.2 BGM 播放系统重构

**变更文件：**
*   `ModMusicManager.java`（核心逻辑）
*   `PDClientConfig.java`（配置项）
*   语言文件（新增配置翻译）

#### 核心变更：新增「完整播放+间隔」模式

| 模式 | 旧行为 | 新行为 |
|------|--------|--------|
| **交叉淡化模式**（默认） | 群系切换时立即交叉淡化切换到新音乐 | 保持不变 |
| **完整播放+间隔模式**（新增） | - | 群系切换不打断当前曲目，等完整播放完 + 间隔后再播放新曲目 |

#### 新增配置项

| 配置键 | 类型 | 默认值 | 范围 | 说明 |
|--------|------|--------|------|------|
| `bgm use song complete mode` | Boolean | `false` | - | 启用「完整播放+间隔」模式 |
| `bgm song interval seconds` | Integer | `45` | 30~60 | 完整播放后间隔秒数 |

#### 关键代码逻辑

```java
// ModMusicManager.java
private String pendingMusicName;  // 待播放曲目（完整播放模式用）

// 模式判断
public static boolean isSongCompleteMode() {
    return Boolean.TRUE.equals(PDClientConfig.BGM_USE_SONG_COMPLETE_MODE.get());
}

// 动态调整循环重播间隔
if (isSongCompleteMode()) {
    int intervalTicks = Math.max(1, PDClientConfig.BGM_SONG_INTERVAL_SECONDS.get() * 20);
    loopRestartManager.setIntervalRange(intervalTicks, intervalTicks, intervalTicks, intervalTicks);
} else {
    loopRestartManager.setIntervalRange(1200, 1800, 600, 1200);  // 旧默认值
}

// 群系切换处理（完整播放模式不打断）
if (isSongCompleteMode()) {
    this.pendingMusicName = musicName;  // 记录待播曲目
    loopRestartManager.markBiomeChanged();
    return;  // 不打断当前播放
}
```

#### 新增群系音乐映射

```java
// 新增海岸和河流群系音乐
registerBiomeMusic("biome_dyedream_shore", "sweetdream_music");
registerBiomeMusic("biome_dyedream_river", "dyedream_world");
```

---

### 🏗️ 3.3 API 架构文档更新

**变更文件：** `.trae/skills/api-split-multi-module/SKILL.md`

#### 模块职责划分优化

| 模块 | 包路径 | 应包含内容 | 新增内容 |
|------|--------|-----------|---------|
| **PasterDreamAPI** | `PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/` | BlockAPI, EntityAPI, ParticleAPI, DimensionAPI, RuinAPI, MobEffectAPI, ItemMigrationAPI | ✅ 新增: ItemAPI, MenuAPI, FluidAPI, CurioAPI, BlockEntityAPI, BgmAPI, SanAPI, MeltDreamEnergyAPI, SpellAPI |
| **PasterDream** | `PasterDream/src/main/java/com/pasterdream/pasterdreammod/` | 所有方块、物品、实体、流体、容器、渲染器、注册管理器 | ✅ 明确: 注册管理器路径为 `registry/PDBlocks`, `registry/PDItems` |

#### 注册事件总线方式更新

```diff
- 在 PasterDreamMod.java 构造器中 .register(modEventBus)
+ 在 PasterDreamAPI.registerAll(modEventBus) 中挂入 DeferredRegister
+ （主模组构造函数开头调用一次）
```

#### API Builder 模式统一化

| API | Builder 类 | 注册方法 |
|-----|-----------|---------|
| 方块 | `SimpleBlockBuilder` / `VariantSetBuilder` / `BatchBlockBuilder` | `.build()` |
| 物品 | `SimpleItemBuilder` / `FoodItemBuilder` / `ToolItemBuilder` / `CurioItemBuilder` | `.build()` |
| 粒子 | `ParticleBuilder` | `.build()` |
| 效果 | `MobEffectBuilder` | `.build()` |
| 维度 | `DimensionBuilder` | `.build()` |
| 遗迹 | `RuinBuilder` / `StructureSetBuilder` | `.build()` |
| 菜单 | `MenuBuilder` | `.build()` |

---

### 🔧 3.4 物品与配置清理

#### 移除的物品/内容

| 项目 | 说明 |
|------|------|
| `yinhul_cotton_candy` | 银狐棉花糖物品（从 PDItems.java 和语言文件移除） |
| `itemGroup.pasterdream.armor_tab` | 盔甲装备标签页（合并到武器工具） |

#### 物品标签页重命名

| 原名称 | 新名称 | 说明 |
|--------|--------|------|
| `武器工具` | `盔甲装备和武器工具` | 合并盔甲到武器工具标签页 |

---

## 4. 影响与风险评估

### ⚠️ 破坏性变更

| 变更类型 | 影响范围 | 风险等级 | 说明 |
|---------|---------|---------|------|
| 物品移除 | `yinhul_cotton_candy` | **高** | 现有世界中该物品可能失效或变为未知物品 |
| API 架构调整 | 模块依赖关系 | **中** | 需要确保 PasterDreamAPI 作为独立前置模组正确编译 |
| BGM 配置新增 | 客户端配置文件 | **低** | 向后兼容，新增配置使用默认值 |

### ✅ 测试建议

#### 必测场景

1.  **国际化验证**
    *   测试游戏语言切换（中文 ↔ 英文）时所有物品/方块提示是否正常显示
    *   验证结构块鼠标悬停提示翻译完整性

2.  **BGM 播放测试**
    *   在配置界面切换 BGM 播放模式
    *   在「完整播放+间隔」模式下验证群系切换不打断当前曲目
    *   验证间隔时间设置（30-60秒）是否生效
    *   测试新增的海岸和河流群系音乐

3.  **物品兼容性**
    *   检查现有世界中的 `yinhul_cotton_candy` 是否会导致错误
    *   验证物品标签页合并后 UI 显示是否正常

4.  **书籍内容**
    *   阅读新添加的书籍，验证文本格式和换行

---

## 5. 配置参数总结

### BGM 播放配置（新增）

| 配置键 | 类型 | 默认值 | 范围 | 文件位置 |
|--------|------|--------|------|---------|
| `bgm use song complete mode` | Boolean | `false` | - | `PDClientConfig.java` |
| `bgm song interval seconds` | Integer | `45` | 30~60 | `PDClientConfig.java` |

### 翻译键模式

所有新翻译键遵循统一命名规范：
*   物品提示: `tooltip.pasterdream.{item_name}.{key}`
*   系统消息: `message.pasterdream.{system}.{key}`
*   书籍内容: `book.pasterdream.{book_id}.title/author/page.{n}`
*   结构块提示: `tooltip.pasterdream.structure_block.{key}`

---

**总结：** 本次提交是一次大规模的代码质量提升，主要聚焦于国际化支持和用户体验优化。BGM 播放系统的增强为玩家提供了更多个性化选择，而架构文档的完善为多模块开发奠定了基础。