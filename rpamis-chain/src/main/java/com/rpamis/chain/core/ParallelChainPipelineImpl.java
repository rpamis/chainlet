package com.rpamis.chain.core;

import com.rpamis.chain.core.entity.ChainContext;
import com.rpamis.chain.core.entity.ChainException;
import com.rpamis.chain.core.entity.ChainResult;
import com.rpamis.chain.plugin.annotations.ChainBuilderService;
import com.rpamis.chain.core.builder.ParallelChainPipelineBuilder;
import com.rpamis.chain.core.definition.ChainHandler;
import com.rpamis.chain.core.definition.ChainInnerPipeline;
import com.rpamis.chain.core.entity.UniqueList;
import com.rpamis.chain.core.fluent.Add;
import com.rpamis.chain.core.support.ChainTypeReference;
import com.rpamis.chain.core.strategy.FullExecutionStrategy;
import com.rpamis.chain.core.support.ParallelChainTask;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 并行责任链实现类
 *
 * @author benym
 * @date 2023/12/20 17:08
 */
@ChainBuilderService
public class ParallelChainPipelineImpl<T> extends AbstractChainPipeline<T> implements ParallelChainPipelineBuilder<T> {

    private ForkJoinPool forkJoinPool;

    private final AtomicBoolean inParallel = new AtomicBoolean(false);

    public ParallelChainPipelineImpl(ChainTypeReference<T> chainTypeReference) {
        super(chainTypeReference);
        this.forkJoinPool = new ForkJoinPool();
    }

    @Override
    public void doHandler(T handlerData, List<ChainResult> checkResults) {
        if (!(super.chainStrategy instanceof FullExecutionStrategy)) {
            throw new ChainException("Parallel chains can only be executed under the FullExecutionStrategy. Consider changing your execution strategy");
        }
        if (!inParallel.compareAndSet(false, true)) {
            return;
        }
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
        super.handlePipeline(chainContext);
    }

    @Override
    public Add<T> pool(ForkJoinPool forkJoinPool) {
        if (forkJoinPool != null) {
            setForkJoinPool(forkJoinPool);
        }
        return this;
    }

    public void setForkJoinPool(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }
}