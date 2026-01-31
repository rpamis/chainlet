package com.rpamis.chainlet.core.definition;

import com.rpamis.chainlet.core.fallback.GlobalChainFallBack;
import com.rpamis.chainlet.core.fluent.Apply;
import com.rpamis.chainlet.core.strategy.StrategyKey;
import com.rpamis.chainlet.core.support.ChainTypeReference;

import java.util.List;

/**
 * 泛型责任链流水线接口
 *
 * @param <T> <T>
 * @author benym
 * @since 2023/3/8 18:24
 */
public interface ChainPipeline<T> extends Apply<T> {

    /**
     * 获取HandlerClasses
     *
     * @return List<Class<?>>
     */
    List<Class<?>> getHandlerClasses();

    /**
     * 根据strategyKey获取执行策略接口
     *
     * @return ChainStrategy
     */
    ChainStrategy<T> getStrategyByKey(StrategyKey strategyKey);

    /**
     * 获取执行策略接口
     *
     * @return ChainStrategy
     */
    ChainStrategy<T> getChainStrategy();

    /**
     * 获取全局降级接口
     *
     * @return GlobalChainFallBack
     */
    GlobalChainFallBack<T> getGlobalChainFallBack();

    /**
     * 获取责任链泛型
     *
     * @return ChainTypeReference
     */
    ChainTypeReference<T> getChainTypeReference();
}
