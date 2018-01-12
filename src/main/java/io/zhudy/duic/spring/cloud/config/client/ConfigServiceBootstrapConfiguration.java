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
