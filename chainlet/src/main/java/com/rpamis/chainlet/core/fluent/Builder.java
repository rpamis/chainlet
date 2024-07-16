package com.rpamis.chainlet.core.fluent;

import com.rpamis.chainlet.core.definition.ChainPipeline;

/**
 * Builder接口
 *
 * @author benym
 * @date 2023/8/21 18:20
 */
public interface Builder<T> {

    /**
     * 构建ChainPipeline
     *
     * @return ChainPipeline
     */
    ChainPipeline<T> build();
}
