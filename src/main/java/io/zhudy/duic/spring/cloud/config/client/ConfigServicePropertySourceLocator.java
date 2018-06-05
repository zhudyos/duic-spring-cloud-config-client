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

import io.zhudy.duic.spring.cloud.config.environment.Environment;
import io.zhudy.duic.spring.cloud.config.environment.PropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.zhudy.duic.spring.cloud.config.client.ConfigClientProperties.TOKEN_HEADER;

/**
 * @author Dave Syer
 * @author Mathieu Ouellet
 */
@Order(0)
public class ConfigServicePropertySourceLocator implements PropertySourceLocator {

    private static Logger log = LoggerFactory.getLogger(ConfigServicePropertySourceLocator.class);

    private ConfigClientProperties defaultProperties;

    public ConfigServicePropertySourceLocator(ConfigClientProperties defaultProperties) {
        Assert.hasLength(defaultProperties.getUri(),
                "\"duic.spring.cloud.config.uri\" 不能为空，请在 bootstrap.yml 文件中配置");
        Assert.hasLength(defaultProperties.getName(),
                "\"duic.spring.cloud.config.name\" 不能为空，请在 bootstrap.yml 文件中配置");
        Assert.hasLength(defaultProperties.getProfile(),
                "\"duic.spring.cloud.config.profile\" 不能为空，请在 bootstrap.yml 文件中配置");

        this.defaultProperties = defaultProperties;
    }

    @Override
    public org.springframework.core.env.PropertySource<?> locate(org.springframework.core.env.Environment environment) {
        ConfigClientProperties properties = this.defaultProperties.override(environment);
        CompositePropertySource composite = new CompositePropertySource("configService");
        Exception error = null;
        String errorBody = null;

        try {
            Environment result = getRemoteEnvironment(properties);
            if (result != null) {
                log.info("Located environment: name={}, profiles={}, state={}",
                        result.getName(), Arrays.asList(result.getProfiles()), result.getState());

                if (result.getPropertySources() != null) { // result.getPropertySources() can be null if using xml
                    for (PropertySource source : result.getPropertySources()) {
                        Map<String, Object> map = (Map<String, Object>) source.getSource();
                        composite.addPropertySource(new MapPropertySource(source.getName(), map));
                    }
                }

                if (StringUtils.hasText(result.getState())) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("config.client.state", result.getState());
                    composite.addFirstPropertySource(new MapPropertySource("configClient", map));
                }
                return composite;
            }
        } catch (HttpServerErrorException e) {
            error = e;
            if (MediaType.APPLICATION_JSON.includes(e.getResponseHeaders().getContentType())) {
                errorBody = e.getResponseBodyAsString();
            }
        } catch (Exception e) {
            error = e;
        }

        log.warn("Could not locate PropertySource: {}", (errorBody == null ? error == null ? "no error message" : error.getMessage() : errorBody));
        return null;
    }

    private Environment getRemoteEnvironment(ConfigClientProperties properties) {
        String token = properties.getToken();
        String name = properties.getName();
        String profile = properties.getProfile();
        String url = properties.getUri() + "/ssc/" + name + "/" + profile;

        ResponseEntity<Environment> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            if (StringUtils.hasText(token)) {
                headers.add(TOKEN_HEADER, token);
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = RestTemplateUtils.getRestTemplate(properties);

            log.info("Fetching config from server at: {}", url);
            response = restTemplate.exchange(url, HttpMethod.GET, entity, Environment.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw e;
            }
        }

        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            return null;
        }
        Environment result = response.getBody();
        return result;
    }

}
