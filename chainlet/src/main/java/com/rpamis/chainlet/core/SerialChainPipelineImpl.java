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
package com.rpamis.chainlet.core;

import com.rpamis.chainlet.plugin.annotations.ChainBuilderService;
import com.rpamis.chainlet.core.builder.SerialChainPipelineBuilder;
import com.rpamis.chainlet.core.support.ChainTypeReference;

/**
 * 串行责任链实现类
 *
 * @author benym
 * @since 2023/8/21 17:18
 */
@ChainBuilderService
public class SerialChainPipelineImpl<T> extends AbstractChainPipeline<T> implements SerialChainPipelineBuilder<T> {

    public SerialChainPipelineImpl(ChainTypeReference<T> chainTypeReference) {
        super(chainTypeReference);
    }
}
