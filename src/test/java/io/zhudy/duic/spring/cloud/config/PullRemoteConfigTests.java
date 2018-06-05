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
package io.zhudy.duic.spring.cloud.config;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test-prc")
@SpringBootTest(classes = RemoteConfigProperties.class)
public class PullRemoteConfigTests {

    private static MockWebServer mockWebServer;

    @BeforeClass
    public static void beforeClass() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String path = request.getPath();
                MockResponse response = null;
                if (path.equals("/api/v1/apps/watches/samples/first")) {
                    response = new MockResponse();
                    response.addHeader("content-type", "application/json; utf-8");
                    response.setBody("{\"state\":\"1\"}");
                } else if (path.equals("/api/v1/ssc/samples/first")) {
                    response = new MockResponse();
                    response.addHeader("content-type", "application/json; utf-8");
                    response.setBody("{\"name\":\"samples\",\"profiles\":[\"first\"],\"state\":\"1\",\"propertySources\":[{\"name\":\"samples_first\",\"source\":{\"a\":\"b\"}}]}");
                }
                return response;
            }
        });

        mockWebServer.start();
        System.setProperty("mock.server.port", mockWebServer.getPort() + "");
    }

    @AfterClass
    public static void afterClass() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    //    @Autowired
//    private ConfigurationPropertiesBindingPostProcessor bindingPostProcessor;
    @Value("${a}")
    private String a;

    @Test
    public void execute1() {
        assertEquals(a, "b");

//        RemoteConfigProperties configProperties = new RemoteConfigProperties();
//        configProperties = (RemoteConfigProperties) bindingPostProcessor.postProcessBeforeInitialization(configProperties, "remoteConfigProperties");
//        assertEquals(a, configProperties.getA());
    }

}
