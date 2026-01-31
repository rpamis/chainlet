package com.rpamis.chainlet.core.strategy;


import com.rpamis.chainlet.core.entities.ChainResult;
import com.rpamis.chainlet.core.context.ChainStrategyContext;
import com.rpamis.chainlet.core.definition.ChainInnerPipeline;
import com.rpamis.chainlet.core.definition.ChainStrategy;

import java.util.List;

/**
 * 责任链全执行模式
 * 无论成功失败，始终会交给链上下一个handler处理
 *
 * @author benym
 * @since 2023/3/8 16:55
 */
public class FullExecutionStrategy<T> implements ChainStrategy<T> {

    @Override
    public void doStrategy(ChainStrategyContext<T> chainStrategyContext) {
        ChainInnerPipeline<T> chain = chainStrategyContext.getChain();
        T handlerData = chainStrategyContext.getHandlerData();
        ChainResult chainResult = chainStrategyContext.getChainResult();
        List<ChainResult> checkResults = chainStrategyContext.getCheckResults();
        checkResults.add(chainResult);
        chain.doHandler(handlerData, checkResults);
    }
}
