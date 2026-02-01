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

import com.rpamis.chainlet.plugin.GenContext;
import com.rpamis.chainlet.plugin.ProcessorContext;
import com.rpamis.chainlet.plugin.template.GenCodeTemplate;

/**
 * 生成代码模版工厂
 *
 * @author benym
 * @since 2024/2/23 15:03
 */
public class GenCodeTemplateFactory extends GenCodeInitializer {

    /**
     * 依据模版生成代码
     *
     * @param genContext       genContext
     * @param processorContext processorContext
     */
    public void genCodeWithTemplate(GenContext genContext, ProcessorContext processorContext) {
        for (GenCodeTemplate genCodeTemplate : genCodeTemplates) {
            genCodeTemplate.execute(genContext, processorContext);
        }
    }
}
