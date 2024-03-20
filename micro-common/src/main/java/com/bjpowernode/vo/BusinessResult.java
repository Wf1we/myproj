package com.bjpowernode.vo;

import org.springframework.stereotype.Service;

import java.io.Serializable;

public class BusinessResult implements Serializable {

    private static final long serialVersionUID = -8907133740313681488L;
    private boolean result;
    private int code;
    private String msg;
    private Object data;


    public BusinessResult() {
    }

    public BusinessResult(boolean result, int code, String msg) {
        this.result = result;
        this.code = code;
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean getResult() {
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
}
