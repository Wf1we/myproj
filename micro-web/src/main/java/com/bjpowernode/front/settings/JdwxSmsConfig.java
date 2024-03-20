package com.bjpowernode.front.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//短信服务配置类
@Component
@ConfigurationProperties(prefix = "jdwx.sms")
public class JdwxSmsConfig {
    private String url;
    private String content;
    private String appkey;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    @Override
    public String toString() {
        return "JdwxSmsConfig{" +
                "url='" + url + '\'' +
                ", content='" + content + '\'' +
                ", appkey='" + appkey + '\'' +
                '}';
    }
}
