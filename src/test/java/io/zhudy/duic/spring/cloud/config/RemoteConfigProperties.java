package io.zhudy.duic.spring.cloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@ConfigurationProperties
public class RemoteConfigProperties {

    private String a;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }
}
