package com.rpamis.pattern.chain.builder;

import com.rpamis.pattern.chain.entity.ChainException;

import java.util.HashMap;
import java.util.Map;

/**
 * 责任链实例缓存
 *
 * @author benym
 * @date 2023/12/21 15:21
 */
public class ChainPipelineCache {

    private static final Map<String, SerialChainPipelineBuilder<?>> CHAIN_MAP = new HashMap<>();

    private static final Map<String, ParallelChainPipelineBuilder<?>> PARALLEL_CHAIN_MAP = new HashMap<>();

    /**
     * 注册一个串行责任链
     *
     * @param chain   要注册的串行责任链
     * @param chainId 唯一标识
     * @param <T>     责任链数据类型
     */
    public static <T> void registerChain(SerialChainPipelineBuilder<T> chain, String chainId) {
        if (CHAIN_MAP.containsKey(chainId)) {
            throw new ChainException("There is already a serial chain with chainId [" + chainId + "], please change your chainId, " +
                    "or use com.rpamis.pattern.chain.builder.ChainPipelineFactory.getChain method to obtain the existing chain");
        }
        CHAIN_MAP.put(chainId, chain);
    }

    /**
     * 注册一个并行责任链
     *
     * @param chain   要注册的并行责任链
     * @param chainId 唯一标识
     * @param <T>     责任链数据类型
     */
    public static <T> void registerParallelChain(ParallelChainPipelineBuilder<T> chain, String chainId) {
        if (PARALLEL_CHAIN_MAP.containsKey(chainId)) {
            throw new ChainException("There is already a parallel chain with chainId [" + chainId + "], please change your chainId, " +
                    "or use com.rpamis.pattern.chain.builder.ChainPipelineFactory.getParallelChain method to obtain the existing chain");
        }
        PARALLEL_CHAIN_MAP.put(chainId, chain);
    }

    /**
     * 根据chainId获取串行责任链
     *
     * @param chainId 唯一标识
     * @param <T>     <T>
     * @return <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> SerialChainPipelineBuilder<T> getChain(String chainId) {
        SerialChainPipelineBuilder<?> chain = CHAIN_MAP.get(chainId);
        if (chain == null) {
            throw new ChainException("There is no chain instance for " + chainId + ", please create chain with chainId");
        }
        return (SerialChainPipelineBuilder<T>) chain;
    }

    /**
     * 根据chainId获取并行责任链
     *
     * @param chainId 唯一标识
     * @param <T>     <T>
     * @return <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> ParallelChainPipelineBuilder<T> getParallelChain(String chainId) {
        ParallelChainPipelineBuilder<?> chain = PARALLEL_CHAIN_MAP.get(chainId);
        if (chain == null) {
            throw new ChainException("There is no chain instance for " + chainId + ", please create chain with chainId");
        }
        return (ParallelChainPipelineBuilder<T>) chain;
    }
}
