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
package com.rpamis.chainlet.core.context;

import com.rpamis.chainlet.core.definition.ChainInnerPipeline;
import com.rpamis.chainlet.core.entities.ChainResult;

import java.io.Serializable;
import java.util.List;

/**
 * 责任链执行策略上下文
 *
 * @author benym
 * @since 2023/12/19 17:47
 */
public class ChainStrategyContext<T> implements Serializable {
    private static final long serialVersionUID = -2540886042216154901L;

    /**
     * 责任链处理的数据
     */
    private T handlerData;

    /**
     * 责任链
     */
    private ChainInnerPipeline<T> chain;

    /**
     * 单个责任链Handler执行结果
     */
    private ChainResult chainResult;

    /**
     * 责任链存储结果list
     */
    private List<ChainResult> checkResults;

    public ChainStrategyContext(T handlerData, ChainInnerPipeline<T> chain, ChainResult chainResult, List<ChainResult> checkResults) {
        this.handlerData = handlerData;
        this.chain = chain;
        this.chainResult = chainResult;
        this.checkResults = checkResults;
    }

    public T getHandlerData() {
        return handlerData;
    }

    public void setHandlerData(T handlerData) {
        this.handlerData = handlerData;
    }

    public ChainInnerPipeline<T> getChain() {
        return chain;
    }

    public void setChain(ChainInnerPipeline<T> chain) {
        this.chain = chain;
    }

    public ChainResult getChainResult() {
        return chainResult;
    }

    public void setChainResult(ChainResult chainResult) {
        this.chainResult = chainResult;
    }

    public List<ChainResult> getCheckResults() {
        return checkResults;
    }

    public void setCheckResults(List<ChainResult> checkResults) {
        this.checkResults = checkResults;
    }
}
