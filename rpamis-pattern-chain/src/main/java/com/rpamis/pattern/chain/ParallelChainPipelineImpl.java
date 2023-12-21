package com.rpamis.pattern.chain;

import com.rpamis.pattern.chain.builder.ParallelChainPipelineBuilder;
import com.rpamis.pattern.chain.definition.ChainHandler;
import com.rpamis.pattern.chain.definition.ChainPipeline;
import com.rpamis.pattern.chain.entity.ChainContext;
import com.rpamis.pattern.chain.entity.ChainException;
import com.rpamis.pattern.chain.entity.ChainResult;
import com.rpamis.pattern.chain.entity.UniqueList;
import com.rpamis.pattern.chain.fluent.Add;
import com.rpamis.pattern.chain.generic.ChainTypeReference;
import com.rpamis.pattern.chain.strategy.FullExecutionStrategy;
import com.rpamis.pattern.chain.thread.ParallelChainTask;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 并行责任链实现类
 *
 * @author benym
 * @date 2023/12/20 17:08
 */
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
    public void assembleAndExecute(T handlerData, ChainPipeline<T> chain, ChainHandler<T> chainHandler, List<ChainResult> checkResults) {
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
