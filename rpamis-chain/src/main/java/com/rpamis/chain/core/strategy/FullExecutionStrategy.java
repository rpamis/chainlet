package com.rpamis.chain.core.strategy;


import com.rpamis.chain.core.VariableChainPipelineImpl;
import com.rpamis.chain.core.entities.ChainResult;
import com.rpamis.chain.core.context.ChainStrategyContext;
import com.rpamis.chain.core.definition.ChainInnerPipeline;
import com.rpamis.chain.core.definition.ChainStrategy;
import com.rpamis.chain.core.support.InstanceOfCache;

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
        ChainInnerPipeline<T> chain = chainStrategyContext.getChain();
        T handlerData = chainStrategyContext.getHandlerData();
        ChainResult chainResult = chainStrategyContext.getChainResult();
        List<ChainResult> checkResults = chainStrategyContext.getCheckResults();
        checkResults.add(chainResult);
        Object processedData = chainResult.getProcessedData();
        if (processedData != null && InstanceOfCache.instanceofCheck(chain.getClass(), VariableChainPipelineImpl.class)) {
            chain.doHandler(handlerData, processedData, checkResults);
        } else {
            chain.doHandler(handlerData, checkResults);
        }
    }
}
