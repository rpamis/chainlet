package com.rpamis.pattern.chain.builder;

/**
 * @author benym
 * @date 2023/8/21 17:45
 */
public class ChainPipelineFactory {

    public static <T> SerialChainPipelineBuilder<T> chain(Class<T> type) {
        ChainPipelineBuilder<T> chain = createChain(type);
        return chain.createSerialChain();
    }

    public static <T> ParallelChainPipelineBuilder<T> parallelChain(Class<T> type){
        ChainPipelineBuilder<T> chain = createChain(type);
        return chain.createParallelChain();
    }

    private static <T> ChainPipelineBuilder<T> createChain(Class<T> type){
        return new ChainPipelineBuilderImpl<>(type);
    }
}
