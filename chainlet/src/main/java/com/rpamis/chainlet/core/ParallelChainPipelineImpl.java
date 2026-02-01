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

import com.rpamis.chainlet.core.context.ChainContext;
import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.entities.ChainException;
import com.rpamis.chainlet.core.entities.ChainResult;
import com.rpamis.chainlet.core.support.InstanceOfCache;
import com.rpamis.chainlet.plugin.annotations.ChainBuilderService;
import com.rpamis.chainlet.core.builder.ParallelChainPipelineBuilder;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.definition.ChainInnerPipeline;
import com.rpamis.chainlet.core.entities.UniqueList;
import com.rpamis.chainlet.core.fluent.Add;
import com.rpamis.chainlet.core.support.ChainTypeReference;
import com.rpamis.chainlet.core.strategy.FullExecutionStrategy;
import com.rpamis.chainlet.core.support.ParallelChainTask;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 并行责任链实现类
 *
 * @author benym
 * @since 2023/12/20 17:08
 */
@ChainBuilderService
public class ParallelChainPipelineImpl<T> extends AbstractChainPipeline<T> implements ParallelChainPipelineBuilder<T> {

    private ForkJoinPool forkJoinPool;

    private final ChainHandlerContext<T> handlerContext;

    private final AtomicBoolean inParallel = new AtomicBoolean(false);

    public ParallelChainPipelineImpl(ChainTypeReference<T> chainTypeReference) {
        super(chainTypeReference);
        this.forkJoinPool = new ForkJoinPool();
        this.handlerContext = new ChainHandlerContext<>();
    }

    @Override
    public void doHandler(T handlerData, List<ChainResult> checkResults) {
        if (!(InstanceOfCache.instanceofCheck(super.chainStrategy.getClass(), FullExecutionStrategy.class))) {
            throw new ChainException("Parallel chains can only be executed under the FullExecutionStrategy. Consider changing your execution strategy");
        }
        if (!inParallel.compareAndSet(false, true)) {
            return;
        }
        this.handlerContext.setHandlerData(handlerData);
        UniqueList<ChainHandler<T>> handlerList = super.handlerList;
        ParallelChainTask<T> parallelChainTask = new ParallelChainTask<>(handlerList, handlerData, checkResults, this);
        this.forkJoinPool.invoke(parallelChainTask);
        inParallel.set(false);
    }

    /**
     * 组装责任链上下文，并进入链式执行
     *
     * @param handlerData  handlerData
     * @param chain        chain
     * @param chainHandler chainHandler
     * @param checkResults checkResults
     */
    public void assembleAndExecute(T handlerData, ChainInnerPipeline<T> chain, ChainHandler<T> chainHandler, List<ChainResult> checkResults) {
        ChainContext<T> chainContext = new ChainContext<>(handlerData, chain,
                super.chainStrategy, chainHandler, checkResults);
        super.handlePipeline(chainContext, handlerContext);
    }

    @Override
    public Add<T> pool(ForkJoinPool forkJoinPool) {
        if (forkJoinPool != null) {
            this.forkJoinPool = forkJoinPool;
        }
        return this;
    }
}
