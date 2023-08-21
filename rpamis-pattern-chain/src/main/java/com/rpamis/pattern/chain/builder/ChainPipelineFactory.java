package com.rpamis.pattern.chain.builder;

/**
 * @author benym
 * @date 2023/8/21 17:45
 */
public class ChainPipelineFactory {

    public static <T> ChainPipelineBuilder<T> createChain(){
        return new ChainPipelineBuilderImpl<>();
    }
}
