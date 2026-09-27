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

项目采用 `main` 稳定线 + 个人开发主分支 + 架构变动分支的模型:

| 分支 | 命名 | 角色 | 生命周期 |
|------|------|------|----------|
| `main` | 固定 | 稳定/发布线;禁止在 main 上直接开发提交 | 永久 |
| 个人开发主分支 | `<GitHub用户名>`(如 `momonyako`) | 个人日常开发 | 长期 |
| 架构变动分支 | `milestone/<name>` | 跨模块大改,与 main 并行 | 阶段性 |

### 命名规则

- 个人主分支:直接使用 GitHub 用户名,全部小写(如 `momonyako`、`phantomdaze`)。
- 架构变动分支:`milestone/<name>`,`name` 为简短主题,小写加连字符(如 `milestone/api-refactor`)。


### 分支流向

- 个人主分支:`<用户名>` → `main`(经 PR 或维护者本地 merge)
- 架构变动分支:`main` → `milestone/<name>` → `main`
- 禁止在 `main` 上直接开发提交。代码须先存在于个人分支、`milestone/*` 或贡献分支,再集成到 `main`。
- 维护者 bypass 仅用于集成「已在下游分支完成开发与验证」的代码,不得用于在 `main` 上直接开发。

### 同步与合并

- 个人分支落后 `main`:默认 `git rebase main`;若分支已被他人基于其开发(共享),改用 `git merge main`。
- 禁止对 `main` 与 `milestone/*` 强推;仅允许对个人分支使用 `git push --force-with-lease`。
- `--force-with-lease` 被拒绝时,禁止改用 `git push --force`;应先用 `git fetch` 检查远端是否有他人提交。
- 合入 `main`:PR + Squash,或维护者本地 merge 后推送,两者均可。

---

## 工作流程

### 1. 个人主分支开发

```bash
# 首次创建个人主分支(若尚不存在)
git checkout main
git pull origin main
git checkout -b <你的GitHub用户名>

# 日常开发
git add .
git commit -m "类型(范围): 你的提交信息"
git push origin <你的GitHub用户名>
```

### 2. 与 main 保持同步

```bash
git fetch origin

# 个人分支未被共享时默认 rebase
git rebase origin/main
git push --force-with-lease origin <你的GitHub用户名>

# 若分支已被他人基于其开发,改用 merge
git merge origin/main
git push origin <你的GitHub用户名>
```

### 3. 架构变动分支

```bash
git checkout main
git pull origin main
git checkout -b milestone/<name>
# 完成后合回 main
```

### 4. 创建 Pull Request

1. 推送分支(个人主分支或 `milestone/*`)
2. 访问 GitHub 仓库,点击 `Compare & pull request`,目标分支选 `main`
3. 填写 PR 描述,提交 PR

### 5. 代码审查

- 等待维护者审查
- 根据审查意见修改代码
- 提交修改

### 6. 合并

审查通过后,维护者会:

1. 使用 **Squash and Merge** 合并 PR
2. 确保 CI 通过
3. 合并后按需删除来源分支(个人主分支为长期分支,保留)

> 维护者也可在本地将「已完成开发与验证」的下游分支 merge 后推送到 `main`;此路径仅用于集成,不得用于在 `main` 上直接开发。

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

### Q: 如何同步 main 分支的更改?

个人分支落后 `main` 时,默认 rebase:

```bash
git checkout <你的GitHub用户名>
git fetch origin
git rebase origin/main
git push --force-with-lease origin <你的GitHub用户名>
```

若分支已被他人基于其开发,改用 merge:

```bash
git checkout <你的GitHub用户名>
git fetch origin
git merge origin/main
git push origin <你的GitHub用户名>
```

若 `--force-with-lease` 被拒绝,禁止改用 `--force`;应先 `git fetch` 检查远端是否有他人提交。

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
