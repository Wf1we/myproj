package com.bjpowernode.consts;

//定义redis的key
public class YLBKey {

    //redis中存放的产品类型的值
    public static final String  DIC_PRODUCT_TYPE="DIC:PRODUCT:TYPE";
    //平台注册总用户数量
    public static final String PLAT_REGISTER_USER_COUNT = "PLAT:REGISER:USER:COUNTS";

    //注册时短信验证码
    public static final String SMS_REGIST_AUTH_CODE = "SMS:REGIST:CODE:";

    //实名认证错误的次数
    public static final String REALNAME_ERROR_TIMES = "RNTIMES:";

    //投资排行榜
    public static final String INVEST_TOP_LIST="IVNEST:TOP:LIST";

    //快钱充值的，自增序号
    public static final String RECHARGE_KQ_RECHARGENO_SEQ = "RECHARGE_KQ_SEQ" ;
    //充值订单号的集合
    public static final String RECHARGE_NO_LIST =  "RECHARGENO:LIST";
}
