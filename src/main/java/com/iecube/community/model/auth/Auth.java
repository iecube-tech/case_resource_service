package com.iecube.community.model.auth;

public enum Auth {
    O,      //online基本功能权限
    MD,     //修改markdown权限
    MDD,    //删除markdown权限
    SC,     //修改学校权限
    SCD,    //删除学校权限
    CO,     //修改学院权限
    COD,    //删除学院权限
    TE,     //修改教师权限
    TED,    //删除教师权限
    TEC,    //教师课程授权权限
    UG,     //用户组权限
    OR,     //操作记录查看权限
    CR,     //创作中心权限
    CRD,    //创作中心的删除权限
    CRE,    //创作审核权限
    PR,     //产品分类管理权限
    LS,     // 理实映射
}
