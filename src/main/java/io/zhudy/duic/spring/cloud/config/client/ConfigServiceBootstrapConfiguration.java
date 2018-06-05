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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Configuration
@ConditionalOnProperty(value = "duic.spring.cloud.config.enabled", matchIfMissing = true)
@EnableConfigurationProperties(ConfigClientProperties.class)
public class ConfigServiceBootstrapConfiguration {

    @Bean
    @ConditionalOnMissingBean(ConfigServicePropertySourceLocator.class)
    public ConfigServicePropertySourceLocator configServicePropertySource(ConfigClientProperties properties) {
        return new ConfigServicePropertySourceLocator(properties);
    }
}
