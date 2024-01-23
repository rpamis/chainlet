package com.rpamis.chain.core.definition;

import com.rpamis.chain.core.fluent.Apply;

/**
 * 泛型责任链流水线接口
 *
 * @param <T> <T>
 * @author benym
 * @date 2023/3/8 18:24
 */
public interface ChainPipeline<T> extends Apply<T> {

}
