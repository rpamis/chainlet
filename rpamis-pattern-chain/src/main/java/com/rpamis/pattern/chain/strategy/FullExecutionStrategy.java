package com.rpamis.pattern.chain.strategy;


import com.rpamis.pattern.chain.definition.ChainPipeline;
import com.rpamis.pattern.chain.definition.ChainStrategy;
import com.rpamis.pattern.chain.entity.ChainResult;
import com.rpamis.pattern.chain.entity.ChainStrategyContext;

import java.util.List;

/**
 * 责任链全执行模式
 * 无论成功失败，始终会交给链上下一个handler处理
 *
 * @author benym
 * @date 2023/3/8 16:55
 */
public class FullExecutionStrategy<T> implements ChainStrategy<T> {

    @Override
    public void doStrategy(ChainStrategyContext<T> chainStrategyContext) {
        ChainPipeline<T> chain = chainStrategyContext.getChain();
        T handlerData = chainStrategyContext.getHandlerData();
        List<ChainResult> checkResults = chainStrategyContext.getCheckResults();
        chain.doHandler(handlerData, checkResults);
    }
}
