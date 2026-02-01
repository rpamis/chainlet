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
import com.rpamis.chainlet.core.definition.ChainStrategy;

import java.util.List;

/**
 * 责任链快速返回模式
 * 如果开启快速返回模式，则有一个成功就立即返回，否则执行完毕所有链上的handler
 * 有一个成功，则责任链最终结果为成功
 *
 * @author benym
 * @since 2023/3/8 16:50
 */
public class FastReturnStrategy<T> implements ChainStrategy<T> {

    private FullExecutionStrategy<T> fullExecutionStrategy;

    public void setFullExecutionStrategy(FullExecutionStrategy<T> fullExecutionStrategy) {
        this.fullExecutionStrategy = fullExecutionStrategy;
    }

    @Override
    public void doStrategy(ChainStrategyContext<T> chainStrategyContext) {
        ChainResult chainResult = chainStrategyContext.getChainResult();
        List<ChainResult> checkResults = chainStrategyContext.getCheckResults();
        if (chainResult.isProcessResult()) {
            checkResults.add(chainResult);
        } else {
            chainResult.setProcessResult(false);
            fullExecutionStrategy.doStrategy(chainStrategyContext);
        }
    }
}
