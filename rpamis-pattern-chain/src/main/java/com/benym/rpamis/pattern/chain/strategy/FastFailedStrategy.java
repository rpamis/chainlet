package com.benym.rpamis.pattern.chain.strategy;

import com.benym.rpamis.pattern.chain.AbstractChainPipeline;
import com.benym.rpamis.pattern.chain.entity.ChainException;
import com.benym.rpamis.pattern.chain.entity.ChainResult;
import com.benym.rpamis.pattern.chain.interfaces.ChainPipeline;
import com.benym.rpamis.pattern.chain.interfaces.ChainStrategy;

import java.io.IOException;

/**
 * 责任链快速失败模式
 * 如果开启快速失败模式，则有一个失败就立即返回，否则执行完毕所有链上的handler
 * 有一个失败，则责任链最终结果为失败
 *
 * @date 2023/3/8 16:45
 * @author benym
 */
public class FastFailedStrategy<T> implements ChainStrategy<T> {

    @Override
    public void doStrategy(T handlerData, ChainPipeline<T> chain, ChainResult chainResult) throws IOException, ChainException {
        if (!chainResult.isProcessResult()) {
            AbstractChainPipeline.CHECK_RESULT.get().add(chainResult);
        } else {
            FullExecutionStrategy<T> fullExecutionStrategy = new FullExecutionStrategy<>();
            chainResult.setProcessResult(true);
            fullExecutionStrategy.doStrategy(handlerData, chain, chainResult);
        }
    }
}
