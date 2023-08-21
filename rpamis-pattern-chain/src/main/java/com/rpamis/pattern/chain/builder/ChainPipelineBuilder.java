package com.rpamis.pattern.chain.builder;

/**
 * @author benym
 * @date 2023/8/21 16:55
 */
public interface ChainPipelineBuilder<T> {

    /**
     * 构建串行责任链
     *
     * @return SerialChainPipelineBuilder
     */
    SerialChainPipelineBuilder<T> chain();

    /**
     * 构建并行责任链
     *
     * @return ParallelChainPipelineBuilder
     */
    ParallelChainPipelineBuilder<T> parallelChain();
}
