package com.rpamis.pattern.chain.builder;

import com.rpamis.pattern.chain.SerialChainPipelineImpl;

/**
 * @author benym
 * @date 2023/8/21 17:10
 */
public class ChainPipelineBuilderImpl<T> implements ChainPipelineBuilder<T>{
    @Override
    public SerialChainPipelineBuilder<T> createSerialChain() {
        return new SerialChainPipelineImpl<>();
    }

    @Override
    public ParallelChainPipelineBuilder<T> createParallelChain() {
        return null;
    }
}
