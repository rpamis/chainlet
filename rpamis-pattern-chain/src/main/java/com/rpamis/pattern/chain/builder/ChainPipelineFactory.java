package com.rpamis.pattern.chain.builder;

/**
 * @author benym
 * @date 2023/8/21 17:45
 */
public class ChainPipelineFactory {

    public static <T> SerialChainPipelineBuilder<T> chain(Class<T> type) {
        ChainPipelineBuilder<T> chain = createChain();
        return chain.createSerialChain();
    }

    public static <T> ParallelChainPipelineBuilder<T> parallelChain(Class<T> type){
        ChainPipelineBuilder<T> chain = createChain();
        return chain.createParallelChain();
    }

    private static <T> ChainPipelineBuilder<T> createChain(){
        return new ChainPipelineBuilderImpl<>();
    }
}
