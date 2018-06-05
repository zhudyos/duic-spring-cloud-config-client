/**
 * Copyright 2017-2018 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    public static final String TOKEN_HEADER = "x-config-token";

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
    private int timeout = 60 * 1000;

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
