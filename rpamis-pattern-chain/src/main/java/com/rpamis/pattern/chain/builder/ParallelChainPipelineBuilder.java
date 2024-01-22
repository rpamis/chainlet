package com.rpamis.pattern.chain.builder;

import com.rpamis.pattern.chain.annotations.ChainBuilder;
import com.rpamis.pattern.chain.fluent.Add;

import java.util.concurrent.ForkJoinPool;

/**
 * 并行责任链Builder
 *
 * @author benym
 * @date 2023/8/21 17:09
 */
@ChainBuilder
public interface ParallelChainPipelineBuilder<T> extends ChainPipelineBuilder<T> {

    /**
     * 替换并行责任链的ForkJoinPool
     *
     * @param forkJoinPool forkJoinPool
     * @return Add<T>
     */
    Add<T> pool(ForkJoinPool forkJoinPool);
}
