# 主模 → PasterDreamAPI · 勿上收清单

> **类别**：参考 · 架构边界  
> **原则**：API = 可复用框架 / Builder / 基类 / 横切工具；主模 = 内容数据、玩法数值、具体资产与维度 ID  
> **已上收能力与实现对照** → [`架构差异.md`](架构差异.md) §3.7–3.8  
> **功能是否对等** → [`功能状态.md`](功能状态.md)

---

## 不要上收（硬清单）

下列无论「看起来像工具」都 **留在主模**：

1. **玩法数值与玩家数据**：SAN / 融梦能量 Attachment、Payload、tick 环境修饰、游戏规则绑定  
2. **维度灵魂**：Dyedream ChunkGenerator / BiomeSource / Noises 与维度 JSON type 注册  
3. **内容注册表**：实体/方块/物品/装饰/遗迹/树的具体条目与美学参数  
4. **专用 Feature 与树形状实现**（除非未来抽成新的通用 DecorationType 且去掉 PD 方块硬编码）  
5. **Boss/技能时序与法术效果表**（`SpellEffects`、手写 skillTimer）  
6. **HUD / Boss 条 / 失智叠层**  
7. **配置文件键与默认平衡**  
8. **VERIFY / smoke hooks**  
9. **强绑定资产路径**：具体 geo 名、音乐名列表、结构模板名、出生点坐标  

### 补充（同样留主模）

| 类 | 说明 |
| :--- | :--- |
| **BGM 播放层** | `CrossfadeManager` / `VolumeSoundInstance` / `ModMusicManager`；API 仅有纯逻辑 `api/audio` |
| **网络业务** | `PDNetwork` 与全部 Payload handler（惯例可文档化，类不搬） |
| **命令体** | `PDCommands` 文案/传送；文件辅助可在 API，命令本身不搬 |
| **EntityImmunitySetup 数据表** | 免疫机制在 API；具体 mob 填表数据留主模 |
| **Attachment 生命周期 glue** | 登录/重生/克隆同步模式可写文档，类不搬 |
| **具体 Renderer / Model / Screen / Particle 实现** | 通用骨架可在 API；内容绑定类留主模 |

### 判定

新增主模「通用基类」时先问：是否 **零 `PD*` 注册表 / 零玩法数值 / 零具体资产路径** 依赖？

- **是** → 可考虑进 API  
- **否** → 内容，留主模  
