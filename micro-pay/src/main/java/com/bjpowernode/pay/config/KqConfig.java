package com.bjpowernode.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kq.config")
public class KqConfig {

    private String merchant;
    private String bgUrl;

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }
}
