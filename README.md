# 新帕斯特之梦 · NeoPasterDream

简体中文 | [English](README_EN.md)

**面向 Minecraft 1.21.1 / NeoForge 的《帕斯特之梦》经原作者正式授权的移植。**

在染梦、灯影与风之旅途之间探索，收集梦境中的知识，使用工坊、法术与饰品推进冒险。项目以还原原模组的核心内容与玩法体验为目标，并针对新版本进行适配、修复和改进。

[下载](https://www.curseforge.com/minecraft/mc-mods/neopasterdream) · [问题反馈](https://github.com/LOVE-U987/NeoPasterDream1/issues) · [开发文档](docs/README.md) · [参与贡献](CONTRIBUTING.md)

## 项目状态

项目仍在开发中。各系统的还原状态、有意差异与开放项见[开发进度](docs/设计/开发进度.md)和[设计方向](docs/设计/设计方向.md)。

本项目**已获得原作者异星之尘（Aerolite_Dust）的正式授权**，由 NPD 团队独立开发和维护。相关记录见根目录[授权截图](授权截图.jpg)，版权归属及具体使用范围见[版权声明与授权状态](PERMISSIONS.md)。

## 内容概览

| 内容 | 介绍 |
|------|------|
| 梦境探索 | 染梦、灯影、风之旅途与亚伦柯斯竞技场，以及连接冒险流程的入口、遗迹和事件 |
| 战斗与能力 | 法杖、法术投射物、瞬身术、斗篷能力与 Curios 饰品 |
| 工艺与研究 | 武器工坊、暗影高炉、梦境炼药锅、研究台与蓝图 |
| 冒险记录 | 梦笔记、卡牌、纪念物与成就；安装 Patchouli 后可使用帕斯特指南 |
| 独立附属 | 法术、理智与融梦能量分别由附属模组提供 |
| 集成与表现 | JEI 配方查看、KubeJS 集成、GeckoLib 动画、维度音乐与视觉效果 |

以上为当前开发内容概览，具体验收范围以开发文档为准。

## 玩家安装

1. 准备 **Minecraft 1.21.1、Java 21 和 NeoForge 21.1.219 或以上的兼容 1.21.1 版本**。
2. 从[下载页面](https://www.curseforge.com/minecraft/mc-mods/neopasterdream)取得对应版本，将主模组和必需前置放入实例的 `mods/` 目录。
3. 如需法术、理智和融梦能量系统，将同一发行版本的三个附属 Jar 一并安装。主模组已内嵌 PasterDreamAPI。
4. 根据需要安装下表中的可选集成，然后启动游戏。

### 第三方依赖

下表中的版本是仓库当前开发依赖；玩家安装时选择适配 Minecraft 1.21.1 / NeoForge 的文件。

| 依赖 | 类型 | 当前开发版本 / 用途 |
|------|------|---------------------|
| GeckoLib | 必需 | 4.8.4，模型与动画 |
| Curios | 必需 | 9.5.1+1.21.1，饰品系统 |
| Player Animator | 可选 | 2.0.4+1.21.1，玩家动作表现 |
| JEI | 可选 | 19.39.0.372，配方查看 |
| Patchouli | 可选 | 1.21.1-93-NEOFORGE，帕斯特指南 |
| KubeJS / Rhino | 可选 | 脚本集成及其运行依赖，版本选择见构建配置 |

准确的构建版本见 [gradle.properties](gradle.properties)，主模组声明的加载要求见 [neoforge.mods.toml](PasterDream/src/main/templates/META-INF/neoforge.mods.toml)。第三方依赖不内嵌在本项目发行 Jar 中。

## 从源码开发

### 环境

- JDK 21。
- 仓库自带 Gradle Wrapper；当前为 9.2.1，以 [Wrapper 配置](gradle/wrapper/gradle-wrapper.properties)为准。
- Git；IDE 可使用 IntelliJ IDEA，项目编码设为 UTF-8。
- 工具脚本与发行归档任务使用 Python 3，需能通过 `python` 命令调用。

### 常用命令

在项目根目录使用 Windows PowerShell：

```powershell
# 编译所有模块
.\gradlew compileJava

# 生成数据
.\gradlew runData

# 启动开发客户端
.\gradlew runClient

# 打包主模组与三个附属
.\gradlew packageMod
```

macOS / Linux 使用 `./gradlew`。`packageMod` 将四个发行 Jar 收集到 `build/dist/`，随后自动归档到根目录的 `打包产物/`。完整环境与运行配置见[环境搭建](docs/入门/环境搭建.md)。

### 测试驱动开发

先为目标行为建立失败基线，再实现、验证与重构。提交前完成全量编译、相关 VERIFY 套件和游戏实测；涉及数据时运行 DataGen。默认 VERIFY `all` 不包含部分专项套件，需按改动范围显式选择。

测试命令、覆盖范围与验收要求见[测试指南](docs/开发指南/测试指南.md)。

## 模块与文档

| 模块 | 职责 |
|------|------|
| `PasterDream` | 主模组：游戏内容、客户端表现与集成，发行 Jar 内嵌 API |
| `PasterDreamAPI` | 可复用 API、注册门面与 Builder |
| `PasterDreamSpells` | 法术附属 |
| `PasterDreamSanity` | 理智附属 |
| `PasterDreamMeltDream` | 融梦能量附属 |

三个附属独立发行，运行时依赖主模组及其内嵌 API。

- [开发文档入口](docs/README.md)：入门、架构、开发指南、参考与教程。
- [贡献指南](CONTRIBUTING.md)：任务沟通、开发流程与 PR 验收。
- [架构总览](docs/架构/架构总览.md)：设计理念与模块关系。
- [更新记录](Changelog.md)：版本变更。
- [Agent 规则](AGENTS.md)：AI 辅助开发的项目约束。

## 许可与致谢

- **NPD 原创内容**：原创代码、美术及其他内容的版权归 NPD 项目所有，具体权利归属与贡献者保留权利按实际归属及贡献约定确认。
- **引用内容**：使用或改编自原模组、PDR 的代码、美术等，其原有部分版权归原创作者或相应权利人所有，保留署名并遵守各自的使用协议与授权范围。
- **使用许可**：NPD 原创代码沿用现有 [MIT License](LICENSE)；非代码内容及第三方内容依其各自许可或明确授权使用。

完整[版权声明与授权状态](PERMISSIONS.md)记录正式授权状态、版权归属与适用许可；具体使用范围以实际授权记录及各内容的协议为准。

感谢原作者**异星之尘（Aerolite_Dust）**及原模组贡献者创造了帕斯特之梦的内容与资源，感谢其对本项目的正式授权与支持。感谢本项目的所有[贡献者](https://github.com/LOVE-U987/NeoPasterDream1/graphs/contributors)，以及 [MomoNyako](https://www.mcmod.cn/author/40210.html)、[PhantomDaze](https://www.mcmod.cn/author/41210.html)。

## 联系与反馈

请通过 [GitHub Issues](https://github.com/LOVE-U987/NeoPasterDream1/issues)提交问题，附上模组版本、加载器版本、复现步骤及相关日志。

项目 QQ 群：**710290194**。
