# PasterDream 装备、武器、配方、套装效果与方块移植 - 验证清单

## 盔甲注册检查
- [ ] Copper Armor 4个物品已注册（头盔、胸甲、护腿、靴子）
- [ ] Titanium Armor 4个物品已注册
- [ ] Sculk Armor 4个物品已注册
- [ ] Dyedream Armor 4个物品已注册
- [ ] Qym Armor 4个物品已注册
- [ ] 盔甲材料属性与原模组一致（耐久、防御、韧性等）

## 套装效果检查
- [ ] Dyedream Armor 套装效果：最大生命值+4
- [ ] Dyedream Armor 套装效果：随时间获得护盾
- [ ] Sculk Armor 套装效果正确实现
- [ ] 盔甲纹理路径正确配置

## 武器工具补全检查
- [ ] Meltdream 工具系列（axe, shovel, hoe）已注册
- [ ] Shadow Erosion 工具系列（axe, hoe, shovel）已注册
- [ ] Moltengold 工具系列（axe, shovel, hoe）已注册
- [ ] 法杖系列已注册

## 配方生成检查
- [ ] 盔甲合成配方已生成
- [ ] 工具合成配方已生成
- [ ] 熔炉/高炉配方已生成
- [ ] 配方文件位于正确目录 (data/pasterdream/recipe/)
- [ ] 配方格式符合 1.21 新规范（使用 "id" 而非 "item"）

## 方块移植检查
- [ ] 染梦维度剩余方块已注册
- [ ] 方块战利品表已生成
- [ ] 方块挖掘标签已生成
- [ ] 战利品表文件位于正确目录 (data/pasterdream/loot_table/blocks/)
- [ ] 挖掘标签文件位于正确目录 (data/minecraft/tags/block/)

## 遗迹战利品检查
- [ ] 遗迹结构战利品表已配置
- [ ] 战利品包含材料、装备、稀有物品

## 语言文件检查
- [ ] zh_cn.json 包含所有新注册物品的中文翻译
- [ ] zh_cn.json 包含所有新注册方块的中文翻译
- [ ] check_lang.py 脚本检查无缺失条目

## 编译验证检查
- [ ] `.\gradlew compileJava` 编译成功
- [ ] 无语法错误
- [ ] 无缺失依赖错误
