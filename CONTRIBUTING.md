# Contributing to PasterDream

感谢您对 PasterDream 项目的贡献！本文档将指导您如何参与项目开发。

## 项目概述

PasterDream 是一个 NeoForge 1.21.1 模组，是原 PasterDream 模组的精神续作。我们的核心理念是：

- 原模组（`libs/FixPasterDream-main/`）仅作为**参考**，部分开发方向已偏离原模组设计
- 原模组是 MCreator 生成，代码不可移植，必须基于 NeoForge 1.21.1 API 重新实现
- 不直接复制或修改原代码，相同效果用不同技术方案实现
- 在原有的项目上，加入我们自己的，独特内容

### 项目结构

本项目采用多模块架构：

| 模块 | 说明 |
|------|------|
| `PasterDreamAPI` | API 模块（Builder/Facade/Result/Config） |
| `PasterDream` | 主模块（方块/物品/实体/渲染/注册） |
| `PasterDreamSpells` | 附属模块：法术系统（thin 发行） |
| `PasterDreamSanity` | 附属模块：理智系统（thin 发行） |
| `PasterDreamMeltDream` | 附属模块：融梦能量系统（thin 发行） |

> 附属模块均为 thin 发行模式，不内嵌 PasterDreamAPI（由主模组打包提供），运行时需主模组作为前置。

## 开发环境设置

### 环境要求

- Java 21
- Gradle 8.x
- Git
- IntelliJ IDEA（推荐）或 Eclipse

### 克隆项目

```bash
git clone https://github.com/LOVE-U987/NeoPasterDream1.git
cd NeoPasterDream1
```

### 构建项目

```bash
# 编译所有模块
.\gradlew compileJava

# 编译单个模块（示例）
.\gradlew :PasterDreamSpells:compileJava
.\gradlew :PasterDreamSanity:compileJava
.\gradlew :PasterDreamMeltDream:compileJava

# 运行数据生成器
.\gradlew runData

# 启动游戏客户端
.\gradlew runClient

# 启动游戏服务器
.\gradlew runServer
```

## 分支策略

### 分支命名规范

本项目允许存在个人开发主分支，在合并前请先在个人分支完成测试

使用 `类型/负责人/主题` 格式：

- **类型**（包括但不限于）：
  - `feature` - 新功能开发
  - `fix` - Bug 修复
  - `refactor` - 代码重构
  - `docs` - 文档更新
  - `test` - 测试相关
- **负责人**：GitHub 用户名
- **主题**：简短描述，使用小写字母和连字符

### 示例

```
feature/momonyako/dream-meter
fix/phantomdaze/loot-table
refactor/username/cleanup-api
docs/username/update-readme
```

### 工作流程

1. 从 `main` 分支或基于主分支变基的个人分支创建功能分支
2. 在功能分支上进行开发
3. 完成开发后，创建 Pull Request，交由核心开发者审查
4. 经过代码审查后合并到 `main`
5. 合并后及时删除功能分支

## 提交信息规范

### 格式

```
类型(范围): 内容
```

### 类型

- `feat` - 新功能
- `fix` - Bug 修复
- `docs` - 文档更新
- `style` - 代码格式（不影响代码运行的变动）
- `refactor` - 重构（既不是新增功能，也不是修改 bug 的代码变动）
- `test` - 增加测试
- `chore` - 构建过程或辅助工具的变动

### 范围

可选，表示影响范围：

- `api` - API 模块
- `block` - 方块相关
- `entity` - 实体相关
- `item` - 物品相关
- `model` - 模型相关
- `render` - 渲染相关
- `registry` - 注册系统
- `client` - 客户端代码
- `server` - 服务端代码
- `refactor` - 重构
- `code & docs` - 代码与文档

### 示例

```
fix(model): correct dyedream_hanging_vine item and drop form
fix(code & docs): disable fillHang for cloud fall and update Issue-#11 tracker
fix(refactor): reduce the formation of ice_crystal_spike
```

## 代码风格规范

### 编码规范

- **非 MD 文件**：标准 ASCII 字符 + UTF-8 编码，禁止使用 Emoji
- **MD 文件**：UTF-8 编码，允许 Unicode 和 Emoji
- **代码注释**：允许使用 UTF-8 字符（如中文）

### 格式规范

- **缩进**：4 空格，禁止制表符
- **大括号**：K&R 风格（右花括号在同一行）
- **换行符**：LF（Unix 格式）
- **行长度**：推荐 120 字符，最大 150 字符
- **空格**：运算符周围、逗号后、冒号后

### 命名约定

- **类**：PascalCase（如 `ShadowGolemEntity`）
- **方法**：camelCase（如 `createAttributes`）
- **常量**：UPPER_SNAKE_CASE（如 `MOD_ID`）
- **注册名**：snake_case（如 `shadow_golem`）

### 注释规范

- 使用中文注释
- 统一采用多行注释
- Javadoc 使用 `@param`、`@return` 等标签
- 每个类和公共方法都需要注释

### 导入顺序

1. 项目内部导入
2. 第三方库导入
3. Java 标准库导入

## 测试要求

### 编译测试

每次提交前，确保代码能够成功编译：

```bash
# 全量编译（推荐）
.\gradlew compileJava

# 或仅编译变更涉及的模块
.\gradlew :PasterDream:compileJava
.\gradlew :PasterDreamSpells:compileJava
```

### 运行时测试

确保游戏能够正常启动和运行：

```bash
.\gradlew runClient
```

### 代码审查

所有代码都需要经过审查才能合并到 `main` 分支。

## PR 流程

### 1. 创建 Pull Request

- 标题：简洁描述更改内容
- 描述：详细说明更改内容、原因和影响
- 关联 Issue：如果有相关 Issue，请关联

### 2. 代码审查

- 至少需要一名核心维护者审查
- 审查内容包括：
  - 代码质量
  - 项目架构一致性
  - 测试覆盖
  - 文档更新

### 3. 合并

- 审查通过后，使用 Squash and Merge
- 确保 CI/CD 通过
- 合并后删除功能分支

## 审查流程

### 审查标准

- **代码质量**：是否符合项目代码风格
- **架构一致性**：是否符合项目架构设计
- **功能完整性**：是否实现了预期功能
- **测试覆盖**：是否有足够的测试
- **文档更新**：是否更新了相关文档

### 反馈机制

- 使用 GitHub Review 功能
- 提供建设性的反馈
- 解决所有审查意见后再合并

## 发布流程

### 版本管理

使用语义化版本：

- **主版本号**：不兼容的 API 更改
- **次版本号**：向下兼容的功能性新增
- **修订号**：向下兼容的问题修正

### 发布步骤

1. 更新版本号
2. 更新 CHANGELOG
3. 创建 Release Tag
4. 发布到 CurseForge

## 常见问题

### Q: 如何开始贡献？

1. Fork 项目
2. 创建功能分支
3. 进行开发
4. 提交 Pull Request

### Q: 遇到问题怎么办？

1. 查看 Issue 是否已有相关讨论
2. 创建新的 Issue 描述问题
3. 等待维护者回复

### Q: 如何联系维护者？

- 通过 GitHub Issue
- 通过项目官方QQ群：710290194

## 行为准则

- 尊重所有贡献者
- 接受建设性的批评
- 专注于对社区最有利的事情
- 对其他社区成员表示同理心

感谢您的贡献！
