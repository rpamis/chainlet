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
package com.rpamis.chainlet.core.fluent;

import com.rpamis.chainlet.core.definition.ChainStrategy;
import com.rpamis.chainlet.core.fallback.GlobalChainFallBack;
import com.rpamis.chainlet.core.strategy.StrategyKey;

/**
 * With接口
 *
 * @author benym
 * @since 2023/8/21 15:09
 */
public interface With<T> extends Builder<T> {

    /**
     * 责任链执行策略
     *
     * @param strategyKey strategyKey
     * @return ChainPipeline
     */
    With<T> strategy(StrategyKey strategyKey);

    /**
     * 责任链执行策略
     *
     * @param chainStrategy chainStrategy
     * @return ChainPipeline
     */
    With<T> strategy(ChainStrategy<T> chainStrategy);

    /**
     * 责任链全局降级策略
     *
     * @param fallBack fallBack
     * @return ChainPipeline
     */
    With<T> globalFallback(GlobalChainFallBack<T> fallBack);
}
