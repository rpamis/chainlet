package com.rpamis.chainlet.core.strategy;

import com.rpamis.chainlet.core.entities.ChainResult;
import com.rpamis.chainlet.core.context.ChainStrategyContext;
import com.rpamis.chainlet.core.definition.ChainStrategy;

import java.util.List;

/**
 * 责任链快速失败模式
 * 如果开启快速失败模式，则有一个失败就立即返回，否则执行完毕所有链上的handler
 * 有一个失败，则责任链最终结果为失败
 *
 * @author benym
 * @date 2023/3/8 16:45
 */
public class FastFailedStrategy<T> implements ChainStrategy<T> {

    private FullExecutionStrategy<T> fullExecutionStrategy;

    public void setFullExecutionStrategy(FullExecutionStrategy<T> fullExecutionStrategy) {
        this.fullExecutionStrategy = fullExecutionStrategy;
    }

    @Override
    public void doStrategy(ChainStrategyContext<T> chainStrategyContext) {
        ChainResult chainResult = chainStrategyContext.getChainResult();
        List<ChainResult> checkResults = chainStrategyContext.getCheckResults();
        if (!chainResult.isProcessResult()) {
            checkResults.add(chainResult);
        } else {
            chainResult.setProcessResult(true);
            fullExecutionStrategy.doStrategy(chainStrategyContext);
        }
    }
}
