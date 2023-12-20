package com.rpamis.pattern.chain.definition;


import com.rpamis.extension.spi.RpamisSpi;
import com.rpamis.pattern.chain.entity.ChainStrategyContext;

/**
 * 责任链策略接口
 *
 * @author benym
 * @date 2023/3/7 18:10
 */
@RpamisSpi
public interface ChainStrategy<T> {

    /**
     * 执行对应返回策略
     *
     * @param chainStrategyContext 责任链执行策略上下文
     */
    void doStrategy(ChainStrategyContext<T> chainStrategyContext);
}