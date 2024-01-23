package com.rpamis.chain.core.definition;

import com.rpamis.chain.core.entity.ChainResult;

import java.util.List;

/**
 * 泛型责任链内部流水线接口
 *
 * @author benym
 * @date 2023/12/29 14:25
 */
public interface ChainInnerPipeline<T> {

    /**
     * 流水线执行Handler处理
     *
     * @param handlerData  需要处理的数据
     * @param checkResults 责任链结果存储list
     */
    void doHandler(T handlerData, List<ChainResult> checkResults);

    /**
     * 流水线执行Handler处理
     *
     * @param handlerData  需要处理的数据
     * @param processedData 返回的处理数据
     * @param checkResults 责任链结果存储list
     */
    default void doHandler(T handlerData, Object processedData, List<ChainResult> checkResults) {

    }

    /**
     * 流水线执行Handler后的处理
     */
    void afterHandler();

    /**
     * 整链结果构建
     *
     * @param checkResult 责任链存储List
     * @return boolean
     */
    boolean buildSuccess(List<ChainResult> checkResult);
}
