package com.rpamis.pattern.chain.builder;

import com.rpamis.pattern.chain.ParallelChainPipelineImpl;
import com.rpamis.pattern.chain.SerialChainPipelineImpl;
import com.rpamis.pattern.chain.VariableChainPipelineImpl;
import com.rpamis.pattern.chain.generic.ChainTypeReference;
import com.rpamis.pattern.chain.plugin.ChainDirectorService;

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

    @Override
    public VariableChainPipelineBuilder<T> variableChain() {
        return new VariableChainPipelineImpl<>(chainTypeReference);
    }

    @Override
    public VariableChainPipelineBuilder<T> variableChain(String chainId) {
        VariableChainPipelineImpl<T> variableChainPipeline = new VariableChainPipelineImpl<>(chainTypeReference);
        ChainPipelineCache.registerVariableChain(variableChainPipeline, chainId);
        return variableChainPipeline;
    }
}
