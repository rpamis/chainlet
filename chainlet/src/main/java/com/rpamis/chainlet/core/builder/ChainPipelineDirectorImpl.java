package com.rpamis.chainlet.core.builder;

import com.rpamis.chainlet.core.ParallelChainPipelineImpl;
import com.rpamis.chainlet.core.SerialChainPipelineImpl;
import com.rpamis.chainlet.core.support.ChainTypeReference;
import com.rpamis.chainlet.plugin.annotations.ChainDirectorService;

/**
 * 责任链Director接口实现
 *
 * @author benym
 * @date 2023/8/21 17:10
 */
@ChainDirectorService
public class ChainPipelineDirectorImpl<T> implements ChainPipelineDirector<T> {

    private final ChainTypeReference<T> chainTypeReference;

    public ChainPipelineDirectorImpl(ChainTypeReference<T> chainTypeReference) {
        this.chainTypeReference = chainTypeReference;
    }

    @Override
    public SerialChainPipelineBuilder<T> chain() {
        return new SerialChainPipelineImpl<>(chainTypeReference);
    }

    @Override
    public SerialChainPipelineBuilder<T> chain(String chainId) {
        SerialChainPipelineImpl<T> serialChainPipeline = new SerialChainPipelineImpl<>(chainTypeReference);
        ChainPipelineCache.registerChain(serialChainPipeline, chainId);
        return serialChainPipeline;
    }

    @Override
    public ParallelChainPipelineBuilder<T> parallelChain() {
        return new ParallelChainPipelineImpl<>(chainTypeReference);
    }

    @Override
    public ParallelChainPipelineBuilder<T> parallelChain(String chainId) {
        ParallelChainPipelineImpl<T> parallelChainPipeline = new ParallelChainPipelineImpl<>(chainTypeReference);
        ChainPipelineCache.registerParallelChain(parallelChainPipeline, chainId);
        return parallelChainPipeline;
    }
}
