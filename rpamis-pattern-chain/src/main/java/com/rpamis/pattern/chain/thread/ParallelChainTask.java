package com.rpamis.pattern.chain.thread;

import com.rpamis.pattern.chain.ParallelChainPipelineImpl;
import com.rpamis.pattern.chain.definition.ChainHandler;
import com.rpamis.pattern.chain.entity.ChainResult;
import com.rpamis.pattern.chain.entity.UniqueList;

import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 * 并行责任链子任务
 *
 * @author benym
 * @date 2023/12/20 17:44
 */
public class ParallelChainTask<T> extends RecursiveAction {

    private static final long serialVersionUID = 6963339111247246802L;

    private final UniqueList<ChainHandler<T>> handlerList;

    private final T handlerData;

    private final List<ChainResult> checkResults;

    private final ParallelChainPipelineImpl<T> parallelChainPipeline;

    public ParallelChainTask(UniqueList<ChainHandler<T>> handlerList, T handlerData, List<ChainResult> checkResults, ParallelChainPipelineImpl<T> parallelChainPipeline) {
        this.handlerList = handlerList;
        this.handlerData = handlerData;
        this.checkResults = checkResults;
        this.parallelChainPipeline = parallelChainPipeline;
    }
    @Override
    protected void compute() {
        this.handlerList.parallelStream()
                .forEach(handler-> parallelChainPipeline.assembleAndExecute(handlerData, parallelChainPipeline, handler, checkResults));
    }
}
