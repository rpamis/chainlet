package com.rpamis.pattern.chain.fluent;

import com.rpamis.pattern.chain.core.ChainHandler;

/**
 * Add接口
 *
 * @author benym
 * @date 2023/8/21 15:07
 */
public interface Add<T> extends With<T> {

    /**
     * 添加Handler处理
     *
     * @param handler 具体的Handler处理类
     * @return ChainPipeline<T>责任链流水线
     */
    Add<T> addHandler(ChainHandler<T> handler);
}
