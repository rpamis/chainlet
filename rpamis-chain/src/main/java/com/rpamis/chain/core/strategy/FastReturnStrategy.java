package com.rpamis.chain.core.strategy;


import com.rpamis.chain.core.interfaces.ChainStrategy;
import com.rpamis.chain.core.AbstractChainPipeline;
import com.rpamis.chain.core.entity.ChainResult;
import com.rpamis.chain.core.interfaces.ChainPipeline;

/**
 * 责任链快速返回模式
 * 如果开启快速返回模式，则有一个成功就立即返回，否则执行完毕所有链上的handler
 * 有一个成功，则责任链最终结果为成功
 *
 * @author benym
 * @date 2023/3/8 16:50
 */
public class FastReturnStrategy<T> implements ChainStrategy<T> {

    @Override
    public void doStrategy(T handlerData, ChainPipeline<T> chain, ChainResult chainResult) {
        if (chainResult.isProcessResult()) {
            AbstractChainPipeline.CHECK_RESULT.get().add(chainResult);
        } else {
            FullExecutionStrategy<T> fullExecutionStrategy = new FullExecutionStrategy<>();
            chainResult.setProcessResult(false);
            fullExecutionStrategy.doStrategy(handlerData, chain, chainResult);
        }
    }
}
