package com.bjpowernode.api.model;

import com.bjpowernode.api.po.BidInfo;

public class InvestInfo extends BidInfo {
    private String phone;

    private String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
