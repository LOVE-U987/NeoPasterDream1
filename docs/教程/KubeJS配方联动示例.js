// ============================================================
// PasterDream KubeJS 配方联动示例（随仓库分发，可直接拷入
//   kubejs/server_scripts/ 使用；本地实测版位于 run/kubejs/，
//   被 .gitignore 忽略，VERIFY 套件 kubejs-recipes 检测的即其内容）
// 覆盖：暗影高炉 shadow_blasting / 精铸工坊 weapon_forge /
//       梦境炼药锅 dream_brewing 三种自定义配方类型
// ============================================================
// 测试配方显式使用 kubejs 命名空间，供 kubejs-recipes 套件识别。

ServerEvents.recipes(event => {
    // --------------------------------------------------------
    // 暗影高炉测试配方 1：煤炭块 → 钻石（附副产物与缩短时长）
    // --------------------------------------------------------
    event.recipes.pasterdream.shadow_blasting('minecraft:diamond', 'minecraft:coal_block')
        .byResult('minecraft:coal')
        .byResultProbability(0.5)
        .blastingTick(50)
        .spendFluidFuel(100)
        .spendFuel(0).id('kubejs:pd_verify_1');

    // --------------------------------------------------------
    // 暗影高炉测试配方 2：染梦尘 → 染梦石英（最简形态，全部默认参数）
    // --------------------------------------------------------
    event.recipes.pasterdream.shadow_blasting('pasterdream:dyedreamquartz', 'pasterdream:dyedream_dust').id('kubejs:pd_verify_2');

    // --------------------------------------------------------
    // 精铸工坊测试配方 1：五色锭 → 下界合金碎片（带镶嵌槽展示）
    // --------------------------------------------------------
    event.recipes.pasterdream.weapon_forge(
        'minecraft:netherite_scrap',
        ['minecraft:iron_ingot', 'minecraft:gold_ingot', 'minecraft:copper_ingot',
         'minecraft:diamond', 'minecraft:emerald']
    ).inlay(true).id('kubejs:pd_verify_3');

    // --------------------------------------------------------
    // 精铸工坊测试配方 2：铁剑 → 金剑（最小 2 参数形态 + 成品预览）
    // --------------------------------------------------------
    event.recipes.pasterdream.weapon_forge(
        'minecraft:golden_sword',
        ['minecraft:iron_sword', 'minecraft:gold_ingot', 'minecraft:stick',
         'minecraft:gold_ingot', 'minecraft:iron_ingot']
    ).finalResult('minecraft:diamond_sword').id('kubejs:pd_verify_4');

    // --------------------------------------------------------
    // 梦境炼药锅测试配方 1：苹果+金锭+荧石粉 → 金苹果（自定义引导药剂缺省 = 药引瓶）
    // --------------------------------------------------------
    event.recipes.pasterdream.dream_brewing(
        'minecraft:golden_apple',
        ['minecraft:apple', 'minecraft:gold_ingot', 'minecraft:glowstone_dust']
    ).craftTick(40).id('kubejs:pd_verify_5');

    // --------------------------------------------------------
    // 梦境炼药锅测试配方 2：面包+小麦+糖 → 蛋糕（指定引导药剂 + 最短时长）
    // --------------------------------------------------------
    event.recipes.pasterdream.dream_brewing(
        'minecraft:cake',
        ['minecraft:bread', 'minecraft:wheat', 'minecraft:sugar']
    ).guidingDrug('minecraft:milk_bucket').craftTick(20).id('kubejs:pd_verify_6');

    console.log('[PasterDream-KubeJsTest] 已注册 2 条暗影高炉 + 2 条精铸工坊 + 2 条梦境炼药锅测试配方');
});
