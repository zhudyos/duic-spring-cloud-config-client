package io.zhudy.duic.spring.cloud.config.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Configuration
@ConditionalOnProperty(value = "duic.spring.cloud.config.enabled", matchIfMissing = true)
public class ConfigServiceBootstrapConfiguration {

    @Autowired
    private ConfigurableEnvironment environment;

    @Bean
    public ConfigClientProperties configClientProperties() {
        return new ConfigClientProperties(environment);
    }

    @Bean
    @ConditionalOnMissingBean(ConfigServicePropertySourceLocator.class)
    public ConfigServicePropertySourceLocator configServicePropertySource(ConfigClientProperties properties) {
        return new ConfigServicePropertySourceLocator(properties);
    }
}
