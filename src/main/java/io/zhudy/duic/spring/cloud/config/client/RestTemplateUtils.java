package io.zhudy.duic.spring.cloud.config.client;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
class RestTemplateUtils {

    static RestTemplate getRestTemplate(ConfigClientProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getTimeout());
        requestFactory.setReadTimeout(properties.getTimeout());
        return new RestTemplate(requestFactory);
    }
}
