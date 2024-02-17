package com.rpamis.chain.core.builder;

import com.rpamis.chain.core.ParallelChainPipelineImpl;
import com.rpamis.chain.core.SerialChainPipelineImpl;
import com.rpamis.chain.core.VariableChainPipelineImpl;
import com.rpamis.chain.core.entities.ChainException;
import com.rpamis.chain.core.entities.UniqueList;
import com.rpamis.chain.plugin.annotations.ChainCache;
import com.rpamis.chain.core.definition.ChainFallBack;
import com.rpamis.chain.core.definition.ChainHandler;
import com.rpamis.chain.core.definition.ChainStrategy;
import com.rpamis.chain.core.fallback.GlobalChainFallBack;
import com.rpamis.chain.core.support.ChainTypeReference;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 责任链实例缓存
 *
 * @author benym
 * @date 2023/12/21 15:21
 */
@ChainCache
public class ChainPipelineCache {

    private ChainPipelineCache() {
        throw new IllegalStateException("ChainPipelineFactory class prohibited instantiation");
    }

    private static final Map<String, SerialChainPipelineImpl<?>> CHAIN_MAP = new ConcurrentHashMap<>();

    private static final Map<String, ParallelChainPipelineImpl<?>> PARALLEL_CHAIN_MAP = new ConcurrentHashMap<>();

    private static final Map<String, VariableChainPipelineImpl<?>> VARIABLE_CHAIN_MAP = new ConcurrentHashMap<>();

    /**
     * 注册一个串行责任链
     *
     * @param chain   要注册的串行责任链
     * @param chainId 唯一标识
     * @param <T>     责任链数据类型
     */
    public static <T> void registerChain(SerialChainPipelineImpl<T> chain, String chainId) {
        if (CHAIN_MAP.containsKey(chainId)) {
            throw new ChainException("There is already a serial chain with chainId [" + chainId + "], please change your chainId, " +
                    "or use com.rpamis.chain.core.builder.ChainPipelineFactory.getChain method to obtain the existing chain");
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
    public static <T> void registerParallelChain(ParallelChainPipelineImpl<T> chain, String chainId) {
        if (PARALLEL_CHAIN_MAP.containsKey(chainId)) {
            throw new ChainException("There is already a parallel chain with chainId [" + chainId + "], please change your chainId, " +
                    "or use com.rpamis.chain.core.builder.ChainPipelineFactory.getParallelChain method to obtain the existing chain");
        }
        PARALLEL_CHAIN_MAP.put(chainId, chain);
    }

    /**
     * 注册一个可变责任链
     *
     * @param chain   要注册的可变责任链
     * @param chainId 唯一标识
     * @param <T>     责任链数据类型
     */
    public static <T> void registerVariableChain(VariableChainPipelineImpl<T> chain, String chainId) {
        if (VARIABLE_CHAIN_MAP.containsKey(chainId)) {
            throw new ChainException("There is already a variable chain with chainId [" + chainId + "], please change your chainId, " +
                    "or use com.rpamis.chain.core.builder.ChainPipelineFactory.getVariableChain method to obtain the existing chain");
        }
        VARIABLE_CHAIN_MAP.put(chainId, chain);
    }

    /**
     * 根据chainId获取串行责任链
     *
     * @param chainId            唯一标识
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return <T>
     */
    public static <T> SerialChainPipelineBuilder<T> getChain(String chainId, ChainTypeReference<T> chainTypeReference) {
        SerialChainPipelineImpl<?> chain = CHAIN_MAP.get(chainId);
        if (chain == null) {
            throw new ChainException("There is no chain instance for " + chainId + ", please create chain with chainId");
        }
        return copyChain(chainId, chain, chainTypeReference);
    }

    /**
     * 根据chainId获取并行责任链
     *
     * @param chainId            唯一标识
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return <T>
     */
    public static <T> ParallelChainPipelineBuilder<T> getParallelChain(String chainId, ChainTypeReference<T> chainTypeReference) {
        ParallelChainPipelineImpl<?> chain = PARALLEL_CHAIN_MAP.get(chainId);
        if (chain == null) {
            throw new ChainException("There is no chain instance for " + chainId + ", please create chain with chainId");
        }
        return copyChain(chainId, chain, chainTypeReference);
    }

    /**
     * 根据chainId获取可变责任链
     *
     * @param chainId            唯一标识
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return <T>
     */
    public static <T> VariableChainPipelineBuilder<T> getVariableChain(String chainId, ChainTypeReference<T> chainTypeReference) {
        VariableChainPipelineImpl<?> chain = VARIABLE_CHAIN_MAP.get(chainId);
        if (chain == null) {
            throw new ChainException("There is no chain instance for " + chainId + ", please create chain with chainId");
        }
        return copyChain(chainId, chain, chainTypeReference);
    }

    /**
     * 复制串行责任链属性到新串行责任链
     *
     * @param chainId            chainId
     * @param chain              chain
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return SerialChainPipelineBuilder<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> SerialChainPipelineBuilder<T> copyChain(String chainId, SerialChainPipelineImpl<?> chain, ChainTypeReference<T> chainTypeReference) {
        verifyChainTypeReference(chainId, chain.getChainTypeReference(), chainTypeReference);
        ChainStrategy<?> chainStrategy = chain.getChainStrategy();
        ChainFallBack<?> chainFallBack = chain.getChainFallBack();
        UniqueList<? extends ChainHandler<?>> handlerList = chain.getHandlerList();
        SerialChainPipelineBuilder<T> newChain = ChainPipelineFactory.createChain(chainTypeReference).chain();
        newChain.addHandler((ChainHandler<T>) handlerList);
        newChain.globalFallback((GlobalChainFallBack<T>) chainFallBack);
        newChain.strategy((ChainStrategy<T>) chainStrategy);
        return newChain;
    }

    /**
     * 复制并行责任链属性到新并行责任链
     *
     * @param chainId            chainId
     * @param chain              chain
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return ParallelChainPipelineBuilder<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> ParallelChainPipelineBuilder<T> copyChain(String chainId, ParallelChainPipelineImpl<?> chain, ChainTypeReference<T> chainTypeReference) {
        verifyChainTypeReference(chainId, chain.getChainTypeReference(), chainTypeReference);
        ChainStrategy<?> chainStrategy = chain.getChainStrategy();
        ChainFallBack<?> chainFallBack = chain.getChainFallBack();
        UniqueList<? extends ChainHandler<?>> handlerList = chain.getHandlerList();
        ParallelChainPipelineBuilder<T> newChain = ChainPipelineFactory.createChain(chainTypeReference).parallelChain();
        newChain.addHandler((ChainHandler<T>) handlerList);
        newChain.globalFallback((GlobalChainFallBack<T>) chainFallBack);
        newChain.strategy((ChainStrategy<T>) chainStrategy);
        return newChain;
    }

    /**
     * 复制可变责任链属性到新可变责任链
     *
     * @param chainId            chainId
     * @param chain              chain
     * @param chainTypeReference chainTypeReference
     * @param <T>                <T>
     * @return VariableChainPipelineBuilder<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> VariableChainPipelineBuilder<T> copyChain(String chainId, VariableChainPipelineImpl<?> chain, ChainTypeReference<T> chainTypeReference) {
        verifyChainTypeReference(chainId, chain.getChainTypeReference(), chainTypeReference);
        ChainStrategy<?> chainStrategy = chain.getChainStrategy();
        ChainFallBack<?> chainFallBack = chain.getChainFallBack();
        UniqueList<? extends ChainHandler<?>> handlerList = chain.getHandlerList();
        VariableChainPipelineBuilder<T> newChain = ChainPipelineFactory.createChain(chainTypeReference).variableChain();
        newChain.addHandler((ChainHandler<T>) handlerList);
        newChain.globalFallback((GlobalChainFallBack<T>) chainFallBack);
        newChain.strategy((ChainStrategy<T>) chainStrategy);
        return newChain;
    }

    /**
     * 验证提供的chainTypeReference是否与原chainTypeReference匹配
     *
     * @param chainId    唯一标识
     * @param sourceType 原chainTypeReference
     * @param targetType 提供的chainTypeReference
     * @param <T>        <T>
     */
    private static <T> void verifyChainTypeReference(String chainId, ChainTypeReference<?> sourceType, ChainTypeReference<T> targetType) {
        if (!sourceType.getGenericClass().equals(targetType.getGenericClass())) {
            throw new ChainException("The chainTypeReference provided does not match the original chainTypeReference " +
                    "whose chainId is [" + chainId + "]. Please provide the correct parameters");
        }
    }
}
