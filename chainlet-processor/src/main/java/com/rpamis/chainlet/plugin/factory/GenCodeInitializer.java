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
package com.rpamis.chainlet.plugin.factory;

import com.rpamis.chainlet.plugin.template.*;

import java.util.HashSet;
import java.util.Set;

/**
 * 生成代码模版工厂初始化器
 *
 * @author benym
 * @since 2024/2/23 15:00
 */
public class GenCodeInitializer {

    /**
     * 模版集合
     */
    protected static Set<GenCodeTemplate> genCodeTemplates = new HashSet<>();

    static {
        init();
    }

    /**
     * 初始化需要处理的模版
     * 模版处理有顺序关系
     */
    public static void init() {
        genCodeTemplates.add(new ChainDirectorGenTemplate());
        genCodeTemplates.add(new ChainDirectorServiceGenTemplate());
        genCodeTemplates.add(new ChainCacheGenTemplate());
        genCodeTemplates.add(new ChainFactoryGenTemplate());
    }
}
