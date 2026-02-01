/*
 * Copyright 2023-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rpamis.chainlet.test;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * DemoUser
 *
 * @author benym
 * @since 2023/5/25 15:49
 */
@Data
@Builder
public class DemoUser implements Serializable {

    private static final long serialVersionUID = 6545355455196518822L;
    /**
     * name
     */
    private String name;
    /**
     * pwd
     */
    private String pwd;
    /**
     * role
     */
    private String role;
}
