package com.rpamis.pattern.chain.interfaces;


import com.rpamis.pattern.chain.FallBackResolver;
import com.rpamis.pattern.chain.entity.ChainContext;
import com.rpamis.pattern.chain.entity.ChainException;
import com.rpamis.pattern.chain.entity.ChainResult;
import com.rpamis.pattern.chain.entity.CompleteChainResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 泛型责任链流水线接口
 *
 * @param <T> <T>
 * @author benym
 * @date 2023/3/8 18:24
 */
public interface ChainPipeline<T> extends Apply<T> {

    /**
     * 流水线执行Handler处理
     *
     * @param handlerData 需要处理的数据
     */
    void doHandler(T handlerData);

    /**
     * 流水线执行Handler后的处理
     */
    void afterHandler();
}
