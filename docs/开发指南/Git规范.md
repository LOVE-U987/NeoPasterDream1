# Git 规范

> 本文档定义 PasterDream 项目的 Git 提交信息格式、分支策略和 PR 流程。

---

## 提交信息格式

### 格式

```
类型(范围): 内容
```

### 类型

| 类型 | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(api): 添加 BiomeShading API` |
| `fix` | Bug 修复 | `fix(item): 修正染梦芽孢碎块掉落` |
| `docs` | 文档更新 | `docs(code & docs): 更新 AGENTS.md` |
| `style` | 代码格式 (不影响运行) | `style(registry): 修正 PDBlocks 缩进` |
| `refactor` | 重构 | `refactor(entity): 提取 TeleportationService` |
| `test` | 增加测试 | `test(api): 添加 BlockAPI 单元测试` |
| `chore` | 构建/辅助工具变动 | `chore(tools): 添加 verify_resource_closure.py` |

### 范围 (可选)

| 范围 | 说明 |
|------|------|
| `api` | API 模块 |
| `block` | 方块相关 |
| `entity` | 实体相关 |
| `item` | 物品相关 |
| `model` | 模型相关 |
| `render` | 渲染相关 |
| `registry` | 注册系统 |
| `client` | 客户端代码 |
| `server` | 服务端代码 |
| `worldgen` | 世界生成 |
| `config` | 配置相关 |
| `network` | 网络相关 |
| `code & docs` | 代码与文档 |

### 示例

```bash
# 新功能
git commit -m "feat(api): 添加 BiomeShading API 以支持数据驱动的生物群系雾颜色"

# Bug 修复
git commit -m "fix(item): 使染梦芽孢碎块掉落随体积与时运缩放"

# 文档更新
git commit -m "docs(code & docs): 关闭云瀑的 fillHang 并更新 Issue-#11 追踪"

# 重构
git commit -m "refactor(worldgen): 重写芽孢生成逻辑"

# 多范围
git commit -m "fix & (worldgen): 调整染梦河的生成逻辑与视觉效果"
```

---

## 分支策略

### 分支命名

使用 `类型/负责人/主题` 格式:

```bash
# 格式
类型/GitHub用户名/简短描述

# 示例
feature/momonyako/dream-meter
fix/phantomdaze/loot-table
refactor/username/cleanup-api
docs/username/update-readme
```

### 分支类型

| 类型 | 用途 | 示例 |
|------|------|------|
| `feature` | 新功能 | `feature/momonyako/dream-meter` |
| `fix` | Bug 修复 | `fix/phantomdaze/loot-table` |
| `refactor` | 代码重构 | `refactor/username/cleanup-api` |
| `docs` | 文档更新 | `docs/username/update-readme` |
| `test` | 测试相关 | `test/username/add-unit-tests` |

### 分支命名规范

- 使用小写字母
- 使用连字符 `-` 分隔单词
- 主题简短描述 (不超过 3-4 个单词)

```bash
# 正确 ✅
feature/momonyako/dream-meter
fix/phantomdaze/loot-table

# 错误 ❌
feature/Momonyako/DreamMeter
fix/phantomdaze/Loot_Table
```

---

## 工作流程

### 1. 从 main 分支创建功能分支

```bash
# 确保 main 分支是最新的
git checkout main
git pull origin main

# 创建功能分支
git checkout -b feature/your-username/my-feature
```

### 2. 在功能分支上进行开发

```bash
# 进行修改
# ...

# 提交更改
git add .
git commit -m "feat(scope): 你的提交信息"
```

### 3. 推送到远程仓库

```bash
git push origin feature/your-username/my-feature
```

### 4. 创建 Pull Request

1. 访问 GitHub 仓库
2. 点击 `Compare & pull request`
3. 填写 PR 描述
4. 提交 PR

### 5. 代码审查

- 等待维护者审查
- 根据审查意见修改代码
- 提交修改

### 6. 合并

审查通过后,维护者会:

1. 使用 **Squash and Merge** 合并 PR
2. 确保 CI/CD 通过
3. 合并后删除功能分支

---

## PR 描述模板

```markdown
## 变更内容
- 简要描述你做了什么

## 变更原因
- 为什么需要这个更改

## 测试情况
- [ ] 编译通过
- [ ] 客户端测试通过
- [ ] 无新增警告

## 关联 Issue
- Closes #123
```

---

## 提交信息规范

### 基本要求

1. **语言**: 使用中文（类型与范围 token 保持英文小写）
2. **长度**: 简洁明了,不超过 72 字符
3. **格式**: `类型(范围): 内容`
4. **内容**: 描述变更内容,不包含代码

### 好的提交信息

```bash
# 好 ✅
feat(api): 添加 BiomeShading API 以支持数据驱动的生物群系雾颜色
fix(item): 使染梦芽孢碎块掉落随体积与时运缩放
docs(code & docs): 更新 AGENTS.md 中的新约定

# 坏 ❌
更新代码
修复 bug
添加新功能
```

---

## 常见问题

### Q: 如何修改最近的提交?

```bash
# 修改提交信息
git commit --amend -m "new commit message"

# 修改文件
git add .
git commit --amend --no-edit
```

### Q: 如何撤销提交?

```bash
# 撤销最后一次提交,保留更改
git reset --soft HEAD~1

# 撤销最后一次提交,丢弃更改
git reset --hard HEAD~1
```

### Q: 如何合并 main 分支的更改?

```bash
# 切换到功能分支
git checkout feature/your-username/my-feature

# 合并 main 分支
git merge main

# 推送到远程
git push origin feature/your-username/my-feature
```

### Q: 如何解决合并冲突?

1. 打开冲突文件
2. 找到冲突标记 (`<<<<<<<`, `=======`, `>>>>>>>`)
3. 手动解决冲突
4. 删除冲突标记
5. 提交更改

```bash
# 解决冲突后
git add .
git commit -m "fix: 解决合并冲突"
```

---

## 下一步

- [代码规范](代码规范.md) — 编码规范
- [注册指南](注册指南.md) — 注册系统详解
- [第一次贡献](../入门/第一次贡献.md) — 完整贡献流程
