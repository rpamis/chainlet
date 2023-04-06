package cn.rpamis.pattern.chain.interfaces;


import cn.rpamis.pattern.chain.entity.ChainException;
import cn.rpamis.pattern.chain.entity.ChainResult;

import java.io.IOException;

/**
 * 责任链策略接口
 *
 * @date 2023/3/7 18:10
 * @author benym
 */
public interface ChainStrategy<T> {

    /**
     * 策略接口初始化
     */
    default ChainResult init(Class<?> handlerClass, boolean processResult) {
        return new ChainResult(handlerClass, processResult);
    }

    /**
     * 执行对应返回策略
     *
     * @param handlerData handlerData
     * @param chain       chain
     * @param chainResult chainResult
     * @throws IOException    IOException
     * @throws ChainException ChainException
     */
    void doStrategy(T handlerData, ChainPipeline<T> chain, ChainResult chainResult) throws IOException, ChainException;
}
