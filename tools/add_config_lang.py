import json
from pathlib import Path

LANG_FILE = Path(r'c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\src\main\resources\assets\pasterdream\lang\zh_cn.json')

new_entries = {
    # 界面通用
    "gui.pasterdream.config.title": "帕斯特之梦 · 模组配置",
    "gui.pasterdream.config.nav": "配置分类",
    "gui.pasterdream.config.save": "保存配置",
    "gui.pasterdream.config.save.success": "配置已保存",
    "gui.pasterdream.config.reset": "恢复默认",
    "gui.pasterdream.config.reset.success": "已恢复默认配置",
    "gui.pasterdream.config.value.on": "开启",
    "gui.pasterdream.config.value.off": "关闭",

    # 分类
    "gui.pasterdream.config.category.hud": "界面显示",
    "gui.pasterdream.config.category.basic": "基础机制",
    "gui.pasterdream.config.category.property": "性能属性",
    "gui.pasterdream.config.category.ban": "功能禁用",

    # HUD
    "gui.pasterdream.config.stealth_display_attribute_hud": "潜行显示属性 HUD",
    "gui.pasterdream.config.stealth_display_attribute_hud.tooltip": "仅在潜行时显示融梦能量条和精神值的 HUD 图标",
    "gui.pasterdream.config.loading_gui_tips": "加载界面 Tips",
    "gui.pasterdream.config.loading_gui_tips.tooltip": "在加载界面时显示帕斯特之梦的提示文本",
    "gui.pasterdream.config.paster_health_hud": "主题生命值条",
    "gui.pasterdream.config.paster_health_hud.tooltip": "启用帕斯特之梦风格的主题生命值条",
    "gui.pasterdream.config.meltdreamenergy_tank_xbase": "融梦能量条 X 偏移",
    "gui.pasterdream.config.meltdreamenergy_tank_xbase.tooltip": "融梦能量条在屏幕上的水平基准位置",
    "gui.pasterdream.config.meltdreamenergy_tank_ybase": "融梦能量条 Y 偏移",
    "gui.pasterdream.config.meltdreamenergy_tank_ybase.tooltip": "融梦能量条在屏幕上的垂直基准位置",
    "gui.pasterdream.config.san_tank_xbase": "精神值条 X 偏移",
    "gui.pasterdream.config.san_tank_xbase.tooltip": "精神值量条在屏幕上的水平基准位置",
    "gui.pasterdream.config.san_tank_ybase": "精神值条 Y 偏移",
    "gui.pasterdream.config.san_tank_ybase.tooltip": "精神值量条在屏幕上的垂直基准位置",

    # Basic
    "gui.pasterdream.config.overworld_night_lowers_san": "主世界夜晚降 San",
    "gui.pasterdream.config.overworld_night_lowers_san.tooltip": "玩家在主世界的夜晚会降低精神值",
    "gui.pasterdream.config.dyedream_crack_generate": "染梦裂隙自然生成",
    "gui.pasterdream.config.dyedream_crack_generate.tooltip": "允许染梦裂隙在世界中自然生成（关闭可能影响正常流程）",
    "gui.pasterdream.config.low_san_debuff": "低 San 负面效果",
    "gui.pasterdream.config.low_san_debuff.tooltip": "精神值过低时给予玩家负面 buff 效果",
    "gui.pasterdream.config.cheerup_buff_threshold_value": "振奋效果阈值",
    "gui.pasterdream.config.cheerup_buff_threshold_value.tooltip": "精神值大于等于该数值时给予振奋效果",
    "gui.pasterdream.config.meltdream_chest_legend_multiplier": "传说宝藏倍率",
    "gui.pasterdream.config.meltdream_chest_legend_multiplier.tooltip": "融梦水晶箱触发传说宝藏的额外倍率",
    "gui.pasterdream.config.meltdream_chest_rare_multiplier": "稀有宝藏倍率",
    "gui.pasterdream.config.meltdream_chest_rare_multiplier.tooltip": "融梦水晶箱触发稀有宝藏的额外倍率",
    "gui.pasterdream.config.sleep_san_recovery_amount": "睡眠 San 回复量",
    "gui.pasterdream.config.sleep_san_recovery_amount.tooltip": "玩家在完成睡眠时回复的精神值数量",
    "gui.pasterdream.config.low_san_picture_jitter": "低 San 画面抖动",
    "gui.pasterdream.config.low_san_picture_jitter.tooltip": "精神值过低时产生画面抖动效果",
    "gui.pasterdream.config.the_origin_of_the_world_initially_generated_dyedream_crack": "原点生成染梦裂隙",
    "gui.pasterdream.config.the_origin_of_the_world_initially_generated_dyedream_crack.tooltip": "初始生成世界时在 0,0 原点生成染梦裂隙",
    "gui.pasterdream.config.mod_accouocement": "进游公告",
    "gui.pasterdream.config.mod_accouocement.tooltip": "进入游戏时在聊天栏显示模组公告",
    "gui.pasterdream.config.in_lamp_shadow_give_pale_boneneedle": "灯影赠针",
    "gui.pasterdream.config.in_lamp_shadow_give_pale_boneneedle.tooltip": "进入主题梦境《灯影之下》时是否给予苍白骨针",
    "gui.pasterdream.config.no_return_dyedream_crack": "禁止返程传送",
    "gui.pasterdream.config.no_return_dyedream_crack.tooltip": "禁止通过染梦世界的染梦裂隙返回主世界",
    "gui.pasterdream.config.dyedream_origin_spawnpoint": "染梦出生点岛屿",
    "gui.pasterdream.config.dyedream_origin_spawnpoint.tooltip": "在染梦世界生成初始出生点岛屿",
    "gui.pasterdream.config.shadow_npc_third_dialogue_after_tp_player_back_to_overworld": "三次对话强制返回",
    "gui.pasterdream.config.shadow_npc_third_dialogue_after_tp_player_back_to_overworld.tooltip": "与无名第三次对话后强制将玩家传送回主世界",

    # Property
    "gui.pasterdream.config.player_total_tick_update": "玩家刻更新频率",
    "gui.pasterdream.config.player_total_tick_update.tooltip": "玩家刻功能程序更新频率（tick），推荐范围 2~20",

    # Ban
    "gui.pasterdream.config.ban_all_the_wings": "禁用所有翅膀",
    "gui.pasterdream.config.ban_all_the_wings.tooltip": "关闭并禁止所有翅膀的功能",
    "gui.pasterdream.config.ban_terra_sword": "禁用大地之刃",
    "gui.pasterdream.config.ban_terra_sword.tooltip": "关闭并禁止大地之刃的功能",
    "gui.pasterdream.config.ban_fire_necklace": "禁用业火项链",
    "gui.pasterdream.config.ban_fire_necklace.tooltip": "关闭并禁止业火项链的功能",
    "gui.pasterdream.config.ban_time_hourglass": "禁用时之沙",
    "gui.pasterdream.config.ban_time_hourglass.tooltip": "关闭并禁止时之沙的功能",
}

with open(LANG_FILE, 'r', encoding='utf-8') as f:
    data = json.load(f)

print(f"原语言条目数: {len(data)}")
for k, v in new_entries.items():
    if k in data:
        print(f"[覆盖] {k}")
    data[k] = v
print(f"新增/更新后条目数: {len(data)}")

with open(LANG_FILE, 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)
    f.write('\n')

print("语言文件已保存")
