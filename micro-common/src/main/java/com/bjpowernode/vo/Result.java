package com.bjpowernode.vo;

import com.bjpowernode.code.RespCode;

public class Result<T> {

    //true：成功处理请求，false：失败
    private boolean result;
    //错误代码
    private int code;
    //消息文本
    private String msg;
    //数据
    private T data;

    public Result() {
    }

    public Result(boolean result, int code, String msg, T data) {
        this.result = result;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    //表示默认错误的Result
    public static Result fail(){
        Result<String> result = new Result();
        result.setResult(false);
        result.setEnum(RespCode.UN_KNOWN);
        result.setData("");
        return result;
    }

    //表示成功的Result
    public static Result ok(){
        Result<String> result = new Result<>();
        result.setResult(true);
        result.setData("");
        result.setEnum(RespCode.RESP_SUCCESS);
        return result;
    }

    public void setEnum(RespCode respCode){
        this.setCode(respCode.getCode());
        this.setMsg(respCode.getMsg());
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "result=" + result +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
