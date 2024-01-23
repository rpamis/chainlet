package com.rpamis.chain.core.builder;

import com.rpamis.chain.core.fluent.Add;
import com.rpamis.chain.plugin.annotations.ChainBuilder;

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
