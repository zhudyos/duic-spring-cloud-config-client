package io.zhudy.duic.spring.cloud.config.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@ConfigurationProperties("duic.spring.cloud.config.watch")
@Data
public class ConfigWatchProperties {

    /**
     * 是否监视配置变化.
     */
    private boolean enabled;

    /**
     * 初始执行延迟时间.
     */
    private int initialDelay = 30 * 1000;

    /**
     * 固定执行延迟时间.
     */
    private int fixedDelay = 30 * 1000;

}
