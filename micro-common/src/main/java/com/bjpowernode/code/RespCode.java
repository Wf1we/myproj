package com.bjpowernode.code;

//请求的，应答码
public enum RespCode {

    UN_KNOWN(0,"未知错误"),
    RESP_SUCCESS(1000,"请求成功"),
    PARAMTER_NONE(1001,"请求参数为空"),
    PHONE_ERR(1002,"手机号格式不正确"),
    PHONE_EXISTS(1003,"手机号已经注册过"),
    PASSWORD_ERR(1004,"请求数据内容有误"),
    SMS_CODE_INVALIDATE(1005,"短信验证码无效"),
    REAL_NAME_PHONE(1006,"实名认证手机号不符合要求"),
    REAL_NAME_TIMES_ERR(1007,"实名认证次数超出范围了，请明天认证"),
    REAL_NAME_TIP(1008,"请重新认证"),
    LOGIN_IP_ERR(1009,"登录存在风险，进行身份认证"),
    LOGIN_DATA_ERR(1010,"登录失败，手机号或者密码有误"),
    LOGIN_REQUIRED(20000,"需要登录"),
    ;

    private int code;
    private String msg;

    RespCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
