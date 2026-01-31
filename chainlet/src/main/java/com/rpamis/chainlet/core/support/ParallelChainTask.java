package com.rpamis.chainlet.core.support;

import com.rpamis.chainlet.core.ParallelChainPipelineImpl;
import com.rpamis.chainlet.core.entities.ChainResult;
import com.rpamis.chainlet.core.definition.ChainHandler;

import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 * 并行责任链子任务
 *
 * @author benym
 * @since 2023/12/20 17:44
 */
public class ParallelChainTask<T> extends RecursiveAction {

    private static final long serialVersionUID = 6963339111247246802L;

    private static final int THRESHOLD = 1;

    private final List<ChainHandler<T>> handlerList;

    private final T handlerData;

    private final List<ChainResult> checkResults;

    private final ParallelChainPipelineImpl<T> parallelChainPipeline;

    public ParallelChainTask(List<ChainHandler<T>> handlerList, T handlerData, List<ChainResult> checkResults, ParallelChainPipelineImpl<T> parallelChainPipeline) {
        this.handlerList = handlerList;
        this.handlerData = handlerData;
        this.checkResults = checkResults;
        this.parallelChainPipeline = parallelChainPipeline;
    }

    @Override
    protected void compute() {
        if (handlerList.isEmpty()) {
            return;
        }
        if (handlerList.size() == THRESHOLD) {
            parallelChainPipeline.assembleAndExecute(handlerData, parallelChainPipeline, handlerList.get(0), checkResults);
            return;
        }
        int mid = handlerList.size() / 2;
        List<ChainHandler<T>> leftHandlerList = handlerList.subList(0, mid);
        List<ChainHandler<T>> rightHandlerList = handlerList.subList(mid, handlerList.size());
        ParallelChainTask<T> leftTask = new ParallelChainTask<>(leftHandlerList, handlerData, checkResults, parallelChainPipeline);
        ParallelChainTask<T> rightTask = new ParallelChainTask<>(rightHandlerList, handlerData, checkResults, parallelChainPipeline);
        invokeAll(leftTask, rightTask);
    }
}
