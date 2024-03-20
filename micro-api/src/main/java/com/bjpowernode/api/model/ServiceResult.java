package com.bjpowernode.api.model;

import java.io.Serializable;

//表示的服务之间调用结果
public class ServiceResult implements Serializable {

    private boolean result;
    private int code;
    private String msg;
    private Object data;

    public ServiceResult() {
        this.result = false;
    }

    public ServiceResult(boolean result) {
        this.result = result;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
