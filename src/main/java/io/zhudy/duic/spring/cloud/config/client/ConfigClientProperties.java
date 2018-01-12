package io.zhudy.duic.spring.cloud.config.client;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Duic 配置项.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@ConfigurationProperties(prefix = ConfigClientProperties.PREFIX)
@Data
public class ConfigClientProperties {

    public static final String PREFIX = "duic.spring.cloud.config";
    public static final String TOKEN_HEADER = "X-Config-Token";

    /**
     * 是否启用.
     */
    private boolean enabled = true;

    /**
     * Duic 服务器 uri.
     */
    private String uri;

    /**
     * 应用名称.
     */
    @Value("${spring.application.name:application}")
    private String name;

    /**
     * profile.
     */
    private String profile = "default";

    /**
     * 访问令牌.
     */
    private String token = "";

    /**
     * 超时时间.
     */
    private int timeout = 30 * 1000;

    public ConfigClientProperties override(org.springframework.core.env.Environment environment) {
        ConfigClientProperties override = new ConfigClientProperties();
        BeanUtils.copyProperties(this, override);
        override.setName(environment.resolvePlaceholders("${" + ConfigClientProperties.PREFIX
                + ".name:${spring.application.name:application}}"));
        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".profile")) {
            override.setProfile(environment.getProperty(ConfigClientProperties.PREFIX + ".profile"));
        }
        return override;
    }
}
