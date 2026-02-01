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
package com.rpamis.chainlet.core.builder;

import com.rpamis.chainlet.core.ParallelChainPipelineImpl;
import com.rpamis.chainlet.core.SerialChainPipelineImpl;
import com.rpamis.chainlet.core.support.ChainTypeReference;
import com.rpamis.chainlet.plugin.annotations.ChainDirectorService;

/**
 * 责任链Director接口实现
 *
 * @author benym
 * @since 2023/8/21 17:10
 */
@ChainDirectorService
public class ChainPipelineDirectorImpl<T> implements ChainPipelineDirector<T> {

    private final ChainTypeReference<T> chainTypeReference;

    public ChainPipelineDirectorImpl(ChainTypeReference<T> chainTypeReference) {
        this.chainTypeReference = chainTypeReference;
    }

    @Override
    public SerialChainPipelineBuilder<T> chain() {
        return new SerialChainPipelineImpl<>(chainTypeReference);
    }

    @Override
    public SerialChainPipelineBuilder<T> chain(String chainId) {
        SerialChainPipelineImpl<T> serialChainPipeline = new SerialChainPipelineImpl<>(chainTypeReference);
        ChainPipelineCache.registerChain(serialChainPipeline, chainId);
        return serialChainPipeline;
    }

    @Override
    public ParallelChainPipelineBuilder<T> parallelChain() {
        return new ParallelChainPipelineImpl<>(chainTypeReference);
    }

    @Override
    public ParallelChainPipelineBuilder<T> parallelChain(String chainId) {
        ParallelChainPipelineImpl<T> parallelChainPipeline = new ParallelChainPipelineImpl<>(chainTypeReference);
        ChainPipelineCache.registerParallelChain(parallelChainPipeline, chainId);
        return parallelChainPipeline;
    }
}
