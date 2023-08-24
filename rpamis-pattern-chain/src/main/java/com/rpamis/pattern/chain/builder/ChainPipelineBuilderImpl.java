package com.rpamis.pattern.chain.builder;

import com.rpamis.pattern.chain.core.SerialChainPipelineImpl;
import com.rpamis.pattern.chain.generic.ChainTypeReference;

/**
 * 责任链Builder接口实现
 *
 * @author benym
 * @date 2023/8/21 17:10
 */
public class ChainPipelineBuilderImpl<T> implements ChainPipelineBuilder<T> {

    private final ChainTypeReference<T> chainTypeReference;

    public ChainPipelineBuilderImpl(ChainTypeReference<T> chainTypeReference) {
        this.chainTypeReference = chainTypeReference;
    }

    @Override
    public SerialChainPipelineBuilder<T> chain() {
        return new SerialChainPipelineImpl<>(chainTypeReference);
    }

    @Override
    public ParallelChainPipelineBuilder<T> parallelChain() {
        return null;
    }
}
