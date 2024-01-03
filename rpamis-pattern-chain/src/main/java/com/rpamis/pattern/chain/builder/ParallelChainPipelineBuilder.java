package com.rpamis.pattern.chain.builder;

import com.rpamis.pattern.chain.fluent.Add;
import com.rpamis.pattern.chain.plugin.ChainBuilder;

import java.util.concurrent.ForkJoinPool;

/**
 * 并行责任链Builder
 *
 * @author benym
 * @date 2023/8/21 17:09
 */
@ChainBuilder(value = "ParallelChain")
public interface ParallelChainPipelineBuilder<T> extends Add<T> {

    /**
     * 替换并行责任链的ForkJoinPool
     *
     * @param forkJoinPool forkJoinPool
     * @return Add<T>
     */
    Add<T> pool(ForkJoinPool forkJoinPool);
}
