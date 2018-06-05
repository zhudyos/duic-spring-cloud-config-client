package io.zhudy.duic.spring.cloud.config.client;

import io.zhudy.duic.spring.cloud.config.environment.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.zhudy.duic.spring.cloud.config.client.ConfigClientProperties.TOKEN_HEADER;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class ConfigWatchService implements Closeable {

    private static Logger log = LoggerFactory.getLogger(ConfigWatchService.class);
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final ContextRefresher refresher;
    private Environment environment;
    private ConfigurableListableBeanFactory beanFactory;
    private ConfigClientProperties properties;

    public ConfigWatchService(ContextRefresher refresher, Environment environment, ConfigurableListableBeanFactory beanFactory,
                              ConfigClientProperties clientProperties) {
        this.refresher = refresher;
        this.environment = environment;
        this.beanFactory = beanFactory;
        this.properties = clientProperties;
    }

    @PostConstruct
    public void start() {
        this.running.compareAndSet(false, true);

        Thread t = new Thread("duic-watch-config-state") {
            @Override
            public void run() {
                for (; ; ) {
                    try {
                        watch();
                    } catch (Exception e) {
                        log.error("监控配置状态异常", e);
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    private void watch() {
        if (this.running.get()) {
            String state = environment.getProperty("config.client.state");
            String remoteState = watchState(state);

            if (remoteState != null && !remoteState.equals(state)) {
                log.info("Reloading config: name={}, profiles={}, state={}, remoteState={}", properties.getName(),
                        properties.getProfile(), state, remoteState);
                refresher.refresh();

                // 刷新 @Value 配置注入
                AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
                bpp.setAutowiredAnnotationType(Value.class);
                bpp.setBeanFactory(beanFactory);
                for (String name : beanFactory.getBeanDefinitionNames()) {
                    bpp.processInjection(beanFactory.getBean(name));
                }
            }
        }
    }

    private String watchState(String state) {
        String token = properties.getToken();
        String name = properties.getName();
        String profile = properties.getProfile();
        String url = properties.getUri() + "/apps/watches/" + name + "/" + profile + "?state=" + state;

        ResponseEntity<State> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            if (StringUtils.hasText(token)) {
                headers.add(TOKEN_HEADER, token);
            }
            headers.add(HttpHeaders.CONNECTION, "keep-alive");

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = RestTemplateUtils.getRestTemplate(properties);

            log.debug("Checking config state from server at: {} {}", url);
            response = restTemplate.exchange(url, HttpMethod.GET, entity, State.class);
        } catch (Exception e) {
            log.warn("Checking config state failed: {}", e.getMessage());
        }

        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            return null;
        }
        State result = response.getBody();
        return result.getState();
    }

    @Override
    public void close() throws IOException {
        running.compareAndSet(true, false);
    }
}
