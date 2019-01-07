/**
 * Copyright 2017-2018 the original author or authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zhudy.duic.spring.cloud.config.environment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Simple plain text serializable encapsulation of a list of property sources. Basically a
 * DTO for {@link org.springframework.core.env.Environment}, but also applicable outside
 * the domain of a Spring application.
 *
 * @author Dave Syer
 * @author Spencer Gibb
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Data
public class Environment {

    private String name;
    private String[] profiles;
    private String state;

    @JsonCreator
    public Environment(@JsonProperty("name") String name,
                       @JsonProperty("profiles") String[] profiles,
                       @JsonProperty("state") String state) {
        super();
        this.name = name;
        this.profiles = profiles;
        this.state = state;
    }

}
