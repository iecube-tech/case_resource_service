package com.iecube.community.model.auth;

public enum Auth {
    O("O"),      //online基本功能权限
    MD("MD"),     //修改markdown权限
    MDD("MDD"),    //删除markdown权限
    SC("SC"),     //修改学校权限
    SCD("SCD"),    //删除学校权限
    CO("CO"),     //修改学院权限
    COD("COD"),    //删除学院权限
    TE("TE"),     //修改教师权限
    TED("TED"),    //删除教师权限
    TEC("TEC"),    //教师课程授权权限
    UG("UG"),     //用户组权限
    OR("OR"),     //操作记录查看权限
    CR("CR"),     //创作中心权限
    CRD("CRD"),    //创作中心的删除权限
    CRE("CRE"),    //创作审核权限
    PR("PR"),     //产品分类管理权限
    LS("LS"),     // 理实映射
    AS("AS");     // 全部学生列表
    private final String auth;    // 查询所有学生

    Auth(String auth) {
        this.auth = auth;
    }

    public String getAuth() {
        return auth;
    }
}
