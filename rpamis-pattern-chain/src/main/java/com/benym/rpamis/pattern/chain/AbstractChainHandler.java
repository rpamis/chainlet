package com.benym.rpamis.pattern.chain;

import java.io.IOException;
import java.util.List;

import static com.benym.rpamis.pattern.chain.AbstractChainPipeline.CHECK_RESULT;

/**
 * 抽象化责任链处理类Handler
 *
 * @param <T> <T>
 * @author benym
 */
public abstract class AbstractChainHandler<T> implements ChainHandler<T> {

    /**
     * handler链式处理
     *
     * @param handlerData   handlerData
     * @param chain         chain
     * @param chainStrategy chainStrategy
     * @param checkResult checkResult
     * @return boolean
     */
    @Override
    public boolean handle(T handlerData, ChainPipline<T> chain, ChainStrategy chainStrategy, ThreadLocal<List<Boolean>> checkResult) throws IOException, ChainException {
        // 具体某个handler处理
        boolean processResult = process(handlerData);
        // 如果开启快速返回模式，则有一个成功就立即返回，否则执行完毕所有链上的handler
        // 有一个成功，则责任链最终结果为成功
        if (chainStrategy == ChainStrategy.FAST_RETURN && processResult) {
            CHECK_RESULT.get().add(true);
        } else if (chainStrategy == ChainStrategy.FAST_FAILED && !processResult) {
            // 如果开启快速失败模式，则有一个失败就立即返回，否则执行完毕所有链上的handler
            // 有一个失败，则责任链最终结果为失败
            CHECK_RESULT.get().add(false);
        } else {
            // 链上下一个handler处理
            CHECK_RESULT.get().add(processResult);
            chain.doHandler(handlerData);
        }
        return true;
    }

    /**
     * 执行具体handler,true表示执行成功,false表示执行失败
     *
     * @param handlerData handlerData
     * @return boolean
     */
    protected abstract boolean process(T handlerData);
}
