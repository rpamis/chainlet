package com.rpamis.chain.core.strategy;

import com.rpamis.chain.core.interfaces.ChainStrategy;
import com.rpamis.chain.core.AbstractChainPipeline;
import com.rpamis.chain.core.entity.ChainResult;
import com.rpamis.chain.core.interfaces.ChainPipeline;

/**
 * 责任链快速失败模式
 * 如果开启快速失败模式，则有一个失败就立即返回，否则执行完毕所有链上的handler
 * 有一个失败，则责任链最终结果为失败
 *
 * @author benym
 * @date 2023/3/8 16:45
 */
public class FastFailedStrategy<T> implements ChainStrategy<T> {

    @Override
    public void doStrategy(T handlerData, ChainPipeline<T> chain, ChainResult chainResult) {
        if (!chainResult.isProcessResult()) {
            AbstractChainPipeline.CHECK_RESULT.get().add(chainResult);
        } else {
            FullExecutionStrategy<T> fullExecutionStrategy = new FullExecutionStrategy<>();
            chainResult.setProcessResult(true);
            fullExecutionStrategy.doStrategy(handlerData, chain, chainResult);
        }
    }
}
