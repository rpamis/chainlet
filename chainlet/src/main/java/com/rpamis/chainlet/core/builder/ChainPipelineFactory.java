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

import com.rpamis.chainlet.plugin.annotations.ChainFactory;
import com.rpamis.chainlet.core.support.ChainTypeReference;

/**
 * 责任链工厂
 *
 * @author benym
 * @since 2023/8/21 17:45
 */
@ChainFactory
public class ChainPipelineFactory {

    private ChainPipelineFactory() {
        throw new IllegalStateException("ChainPipelineFactory class prohibited instantiation");
    }

    /**
     * 创建责任链Builder实例
     *
     * @param chainTypeReference 责任链泛型包装类
     * @param <T>                责任链泛型
     * @return ChainPipelineDirector 责任链Builder实例
     */
    public static <T> ChainPipelineDirector<T> createChain(ChainTypeReference<T> chainTypeReference) {
        return new ChainPipelineDirectorImpl<>(chainTypeReference);
    }

    /**
     * 根据chainId获取串行责任链实例
     *
     * @param chainId            chainId
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return <T>
     */
    public static <T> SerialChainPipelineBuilder<T> getChain(String chainId, ChainTypeReference<T> chainTypeReference) {
        return ChainPipelineCache.getChain(chainId, chainTypeReference);
    }

    /**
     * 根据chainId获取并行责任链实例
     *
     * @param chainId            chainId
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return <T>
     */
    public static <T> ParallelChainPipelineBuilder<T> getParallelChain(String chainId, ChainTypeReference<T> chainTypeReference) {
        return ChainPipelineCache.getParallelChain(chainId, chainTypeReference);
    }

}
