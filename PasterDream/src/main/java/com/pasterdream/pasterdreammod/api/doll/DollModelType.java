package com.pasterdream.pasterdreammod.api.doll;

/** 玩偶模型类型 */
public enum DollModelType {
    /** 新模型：双层皮肤，模型/纹理由 {@link DollConfig} 显式指定 */
    NEW,
    /** 旧模型：沿用 MemorialDollBlock 约定，按 <name>_holding.geo.json 切换抱物模型 */
    LEGACY
}
