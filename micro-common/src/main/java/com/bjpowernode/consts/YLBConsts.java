package com.bjpowernode.consts;

public class YLBConsts {

    //会话中的user的key
    public static final String SESSION_USER = "YLB_SESSION_USER" ;

    /***********默认的分页大小************************/
    public static final Integer DEFAULT_PAGE_SIZE = 6;

    /***********产品类型************************/
    //新手宝
    public static final Integer PRODUCT_TYPE_XINSHOUBAO = 0;
    //优选
    public static final Integer PRODUCT_TYPE_YOUXUAN = 1;
    //散标
    public static final Integer PRODUCT_TYPE_SANBIAO = 2;

    /***********状态字段值************************/
    //投资成功
    public static final Integer BID_STATUS_SUCCESS= 1;
    //投资失败
    public static final Integer BID_STATUS_FAIL= 2;

    //产品状态 未满标
    public static final Integer PRODUCT_STATUS_NOT_MANBIAO = 0;
    //产品状态 满标
    public static final Integer PRODUCT_STATUS_MANBIAO = 1;
    //满标生成收益计划
    public static final Integer PRODUCT_STATUS_MANBIAO_INCOME_PLAN = 2;

    //充值状态
    //充值中
    public static final Integer RECHARGE_PROCESSING = 0;
    //成功
    public static final Integer RECHARGE_SUCCESS = 1;
    //失败
    public static final Integer RECHARGE_FAILURE = 2;

    //收益状态 未返还
    public static final Integer INCOME_STATUS_PLAN = 0;
    //已返还
    public static final Integer INCOME_STATUS_BACK = 1;



    //错误码
    //资金账户不存在
    public static final Integer ERR_ACCOUNT_NOTEXISTS = 300010;
    //资金余额不足
    public static final Integer ERR_ACCOUNT_MONEY_NOT_ENOUGH = 300011;
    //产品不能售卖
    public static final Integer ERR_PRODUCT_NOT_INVEST = 300012;
    //投资金额不正确
    public static final Integer ERR_BIDMONEY_VALUE = 300013;
    //数据服务处理成功
    public static final Integer SERVICE_HANDLER_SUCCESS = 30000;



}
