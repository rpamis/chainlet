package com.rpamis.pattern.chain.strategy;


import com.rpamis.pattern.chain.AbstractChainPipeline;
import com.rpamis.pattern.chain.entity.ChainException;
import com.rpamis.pattern.chain.entity.ChainResult;
import com.rpamis.pattern.chain.interfaces.ChainPipeline;
import com.rpamis.pattern.chain.interfaces.ChainStrategy;

import java.io.IOException;

/**
 * 责任链全执行模式
 * 无论成功失败，始终会交给链上下一个handler处理
 *
 * @date 2023/3/8 16:55
 * @author benym
 */
public class FullExecutionStrategy<T> implements ChainStrategy<T> {

    @Override
    public void doStrategy(T handlerData, ChainPipeline<T> chain, ChainResult chainResult) throws IOException, ChainException {
        AbstractChainPipeline.CHECK_RESULT.get().add(chainResult);
        chain.doHandler(handlerData);
    }
}
