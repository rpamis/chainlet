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
package com.rpamis.chainlet.core.strategy;


import com.rpamis.chainlet.core.entities.ChainResult;
import com.rpamis.chainlet.core.context.ChainStrategyContext;
import com.rpamis.chainlet.core.definition.ChainInnerPipeline;
import com.rpamis.chainlet.core.definition.ChainStrategy;

import java.util.List;

/**
 * 责任链全执行模式
 * 无论成功失败，始终会交给链上下一个handler处理
 *
 * @author benym
 * @since 2023/3/8 16:55
 */
public class FullExecutionStrategy<T> implements ChainStrategy<T> {

    @Override
    public void doStrategy(ChainStrategyContext<T> chainStrategyContext) {
        ChainInnerPipeline<T> chain = chainStrategyContext.getChain();
        T handlerData = chainStrategyContext.getHandlerData();
        ChainResult chainResult = chainStrategyContext.getChainResult();
        List<ChainResult> checkResults = chainStrategyContext.getCheckResults();
        checkResults.add(chainResult);
        chain.doHandler(handlerData, checkResults);
    }
}
