package com.rpamis.chainlet.core.fluent;

import com.rpamis.chainlet.core.definition.ChainHandler;

import java.util.List;

/**
 * Add接口
 *
 * @author benym
 * @since 2023/8/21 15:07
 */
public interface Add<T> extends With<T> {

    /**
     * 添加Handler处理
     *
     * @param handler 具体的Handler处理类
     * @return Add<T>
     */
    Add<T> addHandler(ChainHandler<T> handler);

    /**
     * 添加Handler处理列表
     *
     * @param handlerList 具体的Handler处理类列表
     * @return Add<T>
     */
    Add<T> addHandler(List<ChainHandler<T>> handlerList);
}
