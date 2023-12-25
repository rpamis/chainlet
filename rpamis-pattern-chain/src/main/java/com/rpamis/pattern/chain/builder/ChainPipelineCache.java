package com.rpamis.pattern.chain.builder;

import com.rpamis.pattern.chain.ParallelChainPipelineImpl;
import com.rpamis.pattern.chain.SerialChainPipelineImpl;
import com.rpamis.pattern.chain.VariableChainPipelineImpl;
import com.rpamis.pattern.chain.definition.ChainFallBack;
import com.rpamis.pattern.chain.definition.ChainHandler;
import com.rpamis.pattern.chain.definition.ChainStrategy;
import com.rpamis.pattern.chain.entity.ChainException;
import com.rpamis.pattern.chain.entity.UniqueList;
import com.rpamis.pattern.chain.generic.ChainTypeReference;

import java.util.HashMap;
import java.util.Map;

/**
 * 责任链实例缓存
 *
 * @author benym
 * @date 2023/12/21 15:21
 */
public class ChainPipelineCache {

    private ChainPipelineCache() {
        throw new IllegalStateException("ChainPipelineFactory class prohibited instantiation");
    }

    private static final Map<String, SerialChainPipelineImpl<?>> CHAIN_MAP = new HashMap<>();

    private static final Map<String, ParallelChainPipelineImpl<?>> PARALLEL_CHAIN_MAP = new HashMap<>();

    private static final Map<String, VariableChainPipelineImpl<?>> VARIABLE_CHAIN_MAP = new HashMap<>();

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
    public static <T> void registerParallelChain(ParallelChainPipelineImpl<T> chain, String chainId) {
        if (PARALLEL_CHAIN_MAP.containsKey(chainId)) {
            throw new ChainException("There is already a parallel chain with chainId [" + chainId + "], please change your chainId, " +
                    "or use com.rpamis.pattern.chain.builder.ChainPipelineFactory.getParallelChain method to obtain the existing chain");
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
                    "or use com.rpamis.pattern.chain.builder.ChainPipelineFactory.getVariableChain method to obtain the existing chain");
        }
        VARIABLE_CHAIN_MAP.put(chainId, chain);
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
        SerialChainPipelineImpl<?> chain = CHAIN_MAP.get(chainId);
        if (chain == null) {
            throw new ChainException("There is no chain instance for " + chainId + ", please create chain with chainId");
        }
        return (SerialChainPipelineBuilder<T>) copyChain(chain);
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
        ParallelChainPipelineImpl<?> chain = PARALLEL_CHAIN_MAP.get(chainId);
        if (chain == null) {
            throw new ChainException("There is no chain instance for " + chainId + ", please create chain with chainId");
        }
        return (ParallelChainPipelineBuilder<T>) copyChain(chain);
    }

    /**
     * 根据chainId获取可变责任链
     *
     * @param chainId 唯一标识
     * @param <T>     <T>
     * @return <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> VariableChainPipelineBuilder<T> getVariableChain(String chainId) {
        VariableChainPipelineImpl<?> chain = VARIABLE_CHAIN_MAP.get(chainId);
        if (chain == null) {
            throw new ChainException("There is no chain instance for " + chainId + ", please create chain with chainId");
        }
        return (VariableChainPipelineBuilder<T>) copyChain(chain);
    }

    /**
     * 复制串行责任链属性到新串行责任链
     *
     * @param chain chain
     * @param <T>   <T>
     * @return SerialChainPipelineBuilder<T>
     */
    public static <T> SerialChainPipelineBuilder<T> copyChain(SerialChainPipelineImpl<T> chain) {
        ChainStrategy<T> chainStrategy = chain.getChainStrategy();
        ChainFallBack<T> chainFallBack = chain.getChainFallBack();
        UniqueList<ChainHandler<T>> handlerList = chain.getHandlerList();
        ChainTypeReference<T> chainTypeReference = chain.getChainTypeReference();
        SerialChainPipelineBuilder<T> newChain = ChainPipelineFactory.createChain(chainTypeReference).chain();
        newChain.addHandler(handlerList);
        newChain.globalFallback(chainFallBack);
        newChain.strategy(chainStrategy);
        return newChain;
    }

    /**
     * 复制并行责任链属性到新并行责任链
     *
     * @param chain chain
     * @param <T>   <T>
     * @return ParallelChainPipelineBuilder<T>
     */
    public static <T> ParallelChainPipelineBuilder<T> copyChain(ParallelChainPipelineImpl<T> chain) {
        ChainStrategy<T> chainStrategy = chain.getChainStrategy();
        ChainFallBack<T> chainFallBack = chain.getChainFallBack();
        UniqueList<ChainHandler<T>> handlerList = chain.getHandlerList();
        ChainTypeReference<T> chainTypeReference = chain.getChainTypeReference();
        ParallelChainPipelineBuilder<T> newChain = ChainPipelineFactory.createChain(chainTypeReference).parallelChain();
        newChain.addHandler(handlerList);
        newChain.globalFallback(chainFallBack);
        newChain.strategy(chainStrategy);
        return newChain;
    }

    /**
     * 复制可变责任链属性到新可变责任链
     *
     * @param chain chain
     * @param <T>   <T>
     * @return VariableChainPipelineBuilder<T>
     */
    public static <T> VariableChainPipelineBuilder<T> copyChain(VariableChainPipelineImpl<T> chain) {
        ChainStrategy<T> chainStrategy = chain.getChainStrategy();
        ChainFallBack<T> chainFallBack = chain.getChainFallBack();
        UniqueList<ChainHandler<T>> handlerList = chain.getHandlerList();
        ChainTypeReference<T> chainTypeReference = chain.getChainTypeReference();
        VariableChainPipelineBuilder<T> newChain = ChainPipelineFactory.createChain(chainTypeReference).variableChain();
        newChain.addHandler(handlerList);
        newChain.globalFallback(chainFallBack);
        newChain.strategy(chainStrategy);
        return newChain;
    }
}
