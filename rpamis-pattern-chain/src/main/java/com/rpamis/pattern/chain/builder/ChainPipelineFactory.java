package com.rpamis.pattern.chain.builder;

import com.rpamis.pattern.chain.annotations.ChainFactory;
import com.rpamis.pattern.chain.support.ChainTypeReference;

/**
 * 责任链工厂
 *
 * @author benym
 * @date 2023/8/21 17:45
 */
@ChainFactory
public class ChainPipelineFactory {

    private ChainPipelineFactory() {
        throw new IllegalStateException("ChainPipelineFactory class prohibited instantiation");
    }

    /**
     * 创建责任链Builder实例
     *
     * @param chainTypeReference 责任链泛型包装类
     * @param <T>                责任链泛型
     * @return ChainPipelineDirector 责任链Builder实例
     */
    public static <T> ChainPipelineDirector<T> createChain(ChainTypeReference<T> chainTypeReference) {
        return new ChainPipelineDirectorImpl<>(chainTypeReference);
    }

    /**
     * 根据chainId获取串行责任链实例
     *
     * @param chainId            chainId
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return <T>
     */
    public static <T> SerialChainPipelineBuilder<T> getChain(String chainId, ChainTypeReference<T> chainTypeReference) {
        return ChainPipelineCache.getChain(chainId, chainTypeReference);
    }

    /**
     * 根据chainId获取并行责任链实例
     *
     * @param chainId            chainId
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return <T>
     */
    public static <T> ParallelChainPipelineBuilder<T> getParallelChain(String chainId, ChainTypeReference<T> chainTypeReference) {
        return ChainPipelineCache.getParallelChain(chainId, chainTypeReference);
    }

    /**
     * 根据chainId获取可变责任链实例
     *
     * @param chainId            chainId
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return <T>
     */
    public static <T> VariableChainPipelineBuilder<T> getVariableChain(String chainId, ChainTypeReference<T> chainTypeReference) {
        return ChainPipelineCache.getVariableChain(chainId, chainTypeReference);
    }


}
